<template>
  <div class="supplier-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">供应商管理</span>
          <el-button type="primary" @click="addSupplier">添加供应商</el-button>
        </div>
      </template>

      <div class="search-filter">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索供应商名称/联系人/电话"
              prefix-icon="Search"
              clearable
              @clear="loadSuppliers"
              @keyup.enter="searchSuppliers"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="statusFilter" placeholder="合作状态" clearable>
              <el-option label="正常" :value="1" />
              <el-option label="停用" :value="0" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="searchSuppliers">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table :data="supplierList" style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="供应商名称" width="180" />
        <el-table-column prop="contactPerson" label="联系人" width="120" />
        <el-table-column prop="contactPhone" label="联系电话" width="150" />
        <el-table-column prop="address" label="地址" width="200" />
        <el-table-column label="合作状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editSupplier(row)">编辑</el-button>
            <el-button 
              size="small" 
              :type="row.status === 1 ? 'warning' : 'success'" 
              @click="toggleStatus(row)"
            >
              {{ row.status === 1 ? '停用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteSupplier(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 添加/编辑供应商对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form :model="supplierForm" :rules="formRules" ref="supplierFormRef" label-width="100px">
        <el-form-item label="供应商名称" prop="name">
          <el-input v-model="supplierForm.name" placeholder="请输入供应商名称" />
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="supplierForm.contactPerson" placeholder="请输入联系人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="supplierForm.contactPhone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input 
            v-model="supplierForm.address" 
            type="textarea"
            :rows="3"
            placeholder="请输入供应商地址" 
          />
        </el-form-item>
        <el-form-item label="合作状态" prop="status" v-if="supplierForm.id">
          <el-radio-group v-model="supplierForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveSupplier" :loading="saveLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import SupplierApi from '../../../../api/SupplierApi';

// 从本地存储获取当前登录的店铺ID
const getShopId = () => {
  return localStorage.getItem("shopid") || 0;
};

const loading = ref(false);
const saveLoading = ref(false);
const supplierList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const searchKeyword = ref('');
const statusFilter = ref('');

const dialogVisible = ref(false);
const dialogTitle = ref('添加供应商');
const supplierFormRef = ref(null);

const supplierForm = ref({
  id: null,
  name: '',
  contactPerson: '',
  contactPhone: '',
  address: '',
  shopId: getShopId(),
  status: 1
});

const formRules = {
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }],
  contactPerson: [{ required: true, message: '请输入联系人', trigger: 'blur' }],
  contactPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ]
};

const loadSuppliers = async () => {
  loading.value = true;
  try {
    const shopId = getShopId();
    console.log('获取供应商列表 - 店铺ID:', shopId);
    
    if (!shopId || shopId === '0' || shopId === 0) {
      ElMessage.error('未获取到店铺ID，请先登录');
      loading.value = false;
      return;
    }

    const res = await SupplierApi.getShopAdminSupplierList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      status: statusFilter.value,
      shopId: shopId
    });

    console.log('API响应:', res);

    if (res.code === 200 && res.data && res.data.pageInfo) {
      supplierList.value = res.data.pageInfo.list;
      total.value = res.data.pageInfo.total;
      console.log('供应商列表加载成功，数量:', res.data.pageInfo.list.length);
    } else {
      ElMessage.error(res.message || '获取供应商列表失败');
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error);
    ElMessage.error('获取供应商列表失败: ' + error.message);
  } finally {
    loading.value = false;
  }
};

const searchSuppliers = () => {
  currentPage.value = 1;
  loadSuppliers();
};

const resetFilters = () => {
  searchKeyword.value = '';
  statusFilter.value = '';
  currentPage.value = 1;
  loadSuppliers();
};

const addSupplier = () => {
  dialogTitle.value = '添加供应商';
  supplierForm.value = {
    id: null,
    name: '',
    contactPerson: '',
    contactPhone: '',
    address: '',
    shopId: getShopId(),
    status: 1
  };
  dialogVisible.value = true;
};

const editSupplier = (row) => {
  dialogTitle.value = '编辑供应商';
  supplierForm.value = {
    id: row.id,
    name: row.name,
    contactPerson: row.contactPerson,
    contactPhone: row.contactPhone,
    address: row.address,
    shopId: row.shopId,
    status: row.status
  };
  dialogVisible.value = true;
};

const saveSupplier = async () => {
  if (!supplierFormRef.value) return;

  try {
    await supplierFormRef.value.validate();

    saveLoading.value = true;

    let res;
    if (supplierForm.value.id) {
      // 更新
      res = await SupplierApi.updateSupplier(supplierForm.value);
    } else {
      // 创建
      res = await SupplierApi.createSupplier(supplierForm.value);
    }

    if (res.code === 200) {
      ElMessage.success(supplierForm.value.id ? '供应商更新成功' : '供应商创建成功');
      dialogVisible.value = false;
      loadSuppliers();
    } else {
      ElMessage.error(res.message || '保存失败');
    }
  } catch (error) {
    if (error !== false) {
      console.error('保存供应商失败:', error);
      ElMessage.error('保存失败');
    }
  } finally {
    saveLoading.value = false;
  }
};

const deleteSupplier = (row) => {
  ElMessageBox.confirm(`确定要删除供应商 "${row.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await SupplierApi.deleteSupplier(row.id);
      if (res.code === 200) {
        ElMessage.success('供应商删除成功');
        loadSuppliers();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除供应商失败:', error);
      ElMessage.error('删除失败');
    }
  }).catch(() => {});
};

const toggleStatus = (row) => {
  const action = row.status === 1 ? '停用' : '启用';
  ElMessageBox.confirm(`确定要${action}供应商 "${row.name}" 吗？`, `确认${action}`, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      let res;
      if (row.status === 1) {
        res = await SupplierApi.disableSupplier(row.id);
      } else {
        res = await SupplierApi.enableSupplier(row.id);
      }
      
      if (res.code === 200) {
        ElMessage.success(`供应商${action}成功`);
        loadSuppliers();
      } else {
        ElMessage.error(res.message || `${action}失败`);
      }
    } catch (error) {
      console.error(`${action}供应商失败:`, error);
      ElMessage.error(`${action}失败`);
    }
  }).catch(() => {});
};

const resetForm = () => {
  if (supplierFormRef.value) {
    supplierFormRef.value.resetFields();
  }
  supplierForm.value = {
    id: null,
    name: '',
    contactPerson: '',
    contactPhone: '',
    address: '',
    shopId: getShopId(),
    status: 1
  };
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadSuppliers();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  loadSuppliers();
};

const formatDate = (date) => {
  if (!date) return '-';
  const d = new Date(date);
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hours = String(d.getHours()).padStart(2, '0');
  const minutes = String(d.getMinutes()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

onMounted(() => {
  loadSuppliers();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.search-filter {
  margin-bottom: 20px;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
