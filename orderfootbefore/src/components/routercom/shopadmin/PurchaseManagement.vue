<template>
  <div class="purchase-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">采购管理</span>
          <el-button type="primary" @click="addPurchase">添加采购记录</el-button>
        </div>
      </template>

      <div class="search-filter">
        <el-row :gutter="20">
          <el-col :span="5">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索产品名称"
              prefix-icon="Search"
              clearable
              @clear="loadPurchases"
              @keyup.enter="searchPurchases"
            />
          </el-col>
          <el-col :span="5">
            <el-input
              v-model="supplierKeyword"
              placeholder="搜索供应商名称"
              prefix-icon="Search"
              clearable
              @clear="loadPurchases"
              @keyup.enter="searchPurchases"
            />
          </el-col>
          <el-col :span="4">
            <el-select v-model="statusFilter" placeholder="采购状态" clearable>
              <el-option label="待确认" :value="0" />
              <el-option label="已完成" :value="1" />
              <el-option label="已取消" :value="2" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-button type="primary" @click="searchPurchases">搜索</el-button>
            <el-button @click="resetFilters">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table :data="purchaseList" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="产品名称" width="120">
          <template #default="{ row }">
            {{ row.productName || `产品ID: ${row.productId}` }}
          </template>
        </el-table-column>
        <el-table-column label="供应商名称" width="120">
          <template #default="{ row }">
            {{ row.supplierName || `供应商ID: ${row.supplierId}` }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="采购数量" width="100" />
        <el-table-column prop="unitPrice" label="采购单价" width="120">
          <template #default="{ row }">
            ¥{{ row.unitPrice ? row.unitPrice.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount ? row.totalAmount.toFixed(2) : '0.00' }}
          </template>
        </el-table-column>
        <el-table-column label="采购状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="采购日期" width="160">
          <template #default="{ row }">
            {{ formatDate(row.purchaseDate) }}
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" width="150" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editPurchase(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deletePurchase(row)">删除</el-button>
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

    <!-- 添加/编辑采购记录对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form :model="purchaseForm" :rules="formRules" ref="purchaseFormRef" label-width="100px">
        <el-form-item label="产品" prop="productId">
          <el-select v-model="purchaseForm.productId" placeholder="请选择产品" clearable>
            <el-option
              v-for="item in productOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="供应商" prop="supplierId">
          <el-select v-model="purchaseForm.supplierId" placeholder="请选择供应商" clearable>
            <el-option
              v-for="item in supplierOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="采购数量" prop="quantity">
          <el-input-number v-model="purchaseForm.quantity" :min="1" :precision="0" />
        </el-form-item>
        <el-form-item label="采购单价" prop="unitPrice">
          <el-input-number v-model="purchaseForm.unitPrice" :min="0" :precision="2" :step="0.01" />
        </el-form-item>
        <el-form-item label="采购日期" prop="purchaseDate">
          <el-date-picker
            v-model="purchaseForm.purchaseDate"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="选择采购日期"
          />
        </el-form-item>
        <el-form-item label="采购状态" prop="status" v-if="purchaseForm.id">
          <el-radio-group v-model="purchaseForm.status">
            <el-radio :value="0">待确认</el-radio>
            <el-radio :value="1">已完成</el-radio>
            <el-radio :value="2">已取消</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input 
            v-model="purchaseForm.remark" 
            type="textarea"
            :rows="3"
            placeholder="请输入备注信息" 
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePurchase" :loading="saveLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import PurchaseApi from '../../../../api/PurchaseApi';
import ProductApi from '../../../../api/productApi';
import SupplierApi from '../../../../api/SupplierApi';

// 从本地存储获取当前登录的店铺ID
const getShopId = () => {
  const shopId = localStorage.getItem("shopid") || localStorage.getItem("shopId") || localStorage.getItem("currentShopId");
  console.log('获取到的店铺ID:', shopId);
  return shopId ? parseInt(shopId) : 0;
};

const loading = ref(false);
const saveLoading = ref(false);
const purchaseList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const searchKeyword = ref('');
const supplierKeyword = ref('');
const statusFilter = ref('');
const dateRange = ref([]);

// 添加产品和供应商选项
const productOptions = ref([]);
const supplierOptions = ref([]);

const dialogVisible = ref(false);
const dialogTitle = ref('添加采购记录');
const purchaseFormRef = ref(null);

const purchaseForm = ref({
  id: null,
  productId: '',
  supplierId: '',
  quantity: 1,
  unitPrice: 0,
  purchaseDate: null,
  status: 1,
  remark: '',
  shopId: getShopId()
});

const formRules = {
  productId: [{ required: true, message: '请选择产品', trigger: 'change' }],
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入采购数量', trigger: 'blur' }],
  unitPrice: [{ required: true, message: '请输入采购单价', trigger: 'blur' }]
};

// 获取产品列表
const loadProductOptions = async () => {
  try {
    const shopId = getShopId();
    if (!shopId || shopId === '0' || shopId === 0) {
      return;
    }
    
    // 使用现有的ProductSelect方法，添加shopId参数
    const res = await ProductApi.ProductSelect({
      page: 1,
      pageSize: 1000,
      shopId: shopId
    });
    if (res.code === 200 && res.data && res.data.pageInfo) {
      productOptions.value = res.data.pageInfo.list.map(item => ({
        value: item.id,
        label: item.name
      }));
    }
  } catch (error) {
    console.error('获取产品列表失败:', error);
  }
};

// 获取供应商列表
const loadSupplierOptions = async () => {
  try {
    const shopId = getShopId();
    if (!shopId || shopId === '0' || shopId === 0) {
      return;
    }
    
    const res = await SupplierApi.getShopAdminSupplierList({
      page: 1,
      pageSize: 1000,
      shopId: shopId
    });
    if (res.code === 200 && res.data && res.data.pageInfo) {
      supplierOptions.value = res.data.pageInfo.list.map(item => ({
        value: item.id,
        label: item.name
      }));
    }
  } catch (error) {
    console.error('获取供应商列表失败:', error);
  }
};

const loadPurchases = async () => {
  loading.value = true;
  try {
    const shopId = getShopId();
    console.log('获取采购记录列表 - 店铺ID:', shopId);
    
    if (!shopId || shopId === '0' || shopId === 0) {
      ElMessage.error('未获取到店铺ID，请先登录');
      loading.value = false;
      return;
    }

    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      productName: searchKeyword.value,
      supplierName: supplierKeyword.value,
      status: statusFilter.value,
      shopId: parseInt(shopId) || 0
    };

    console.log('请求参数:', params);

    const res = await PurchaseApi.getShopAdminPurchaseList(params);

    console.log('API响应:', res);

    if (res.code === 200 && res.data && res.data.pageInfo) {
      purchaseList.value = res.data.pageInfo.list;
      total.value = res.data.pageInfo.total;
      console.log('采购记录列表加载成功，数量:', res.data.pageInfo.list.length);
    } else {
      ElMessage.error(res.message || '获取采购记录列表失败');
    }
  } catch (error) {
    console.error('获取采购记录列表失败:', error);
    ElMessage.error('获取采购记录列表失败: ' + error.message);
  } finally {
    loading.value = false;
  }
};

const searchPurchases = () => {
  currentPage.value = 1;
  loadPurchases();
};

const resetFilters = () => {
  searchKeyword.value = '';
  supplierKeyword.value = '';
  statusFilter.value = '';
  dateRange.value = [];
  currentPage.value = 1;
  loadPurchases();
};

const addPurchase = async () => {
  const shopId = getShopId();
  console.log('添加采购记录 - 店铺ID:', shopId);
  
  if (!shopId || shopId === '0' || shopId === 0) {
    ElMessage.error('未获取到店铺ID，请先登录');
    return;
  }
  
  // 加载产品和供应商选项
  await loadProductOptions();
  await loadSupplierOptions();
  
  dialogTitle.value = '添加采购记录';
  purchaseForm.value = {
    id: null,
    productId: '',
    supplierId: '',
    quantity: 1,
    unitPrice: 0,
    purchaseDate: null,
    status: 1,
    remark: '',
    shopId: shopId
  };
  dialogVisible.value = true;
};

const editPurchase = async (row) => {
  // 加载产品和供应商选项
  await loadProductOptions();
  await loadSupplierOptions();
  
  dialogTitle.value = '编辑采购记录';
  purchaseForm.value = {
    id: row.id,
    productId: row.productId,
    supplierId: row.supplierId,
    quantity: row.quantity,
    unitPrice: row.unitPrice,
    purchaseDate: row.purchaseDate,
    status: row.status,
    remark: row.remark,
    shopId: row.shopId
  };
  dialogVisible.value = true;
};

const savePurchase = async () => {
  if (!purchaseFormRef.value) return;

  try {
    await purchaseFormRef.value.validate();

    saveLoading.value = true;

    let res;
    if (purchaseForm.value.id) {
      // 更新
      res = await PurchaseApi.updatePurchase(purchaseForm.value);
    } else {
      // 创建
      res = await PurchaseApi.createPurchase(purchaseForm.value);
    }

    if (res.code === 200) {
      ElMessage.success(purchaseForm.value.id ? '采购记录更新成功' : '采购记录创建成功');
      dialogVisible.value = false;
      loadPurchases();
    } else {
      ElMessage.error(res.message || '保存失败');
    }
  } catch (error) {
    if (error !== false) {
      console.error('保存采购记录失败:', error);
      ElMessage.error('保存失败');
    }
  } finally {
    saveLoading.value = false;
  }
};

const deletePurchase = (row) => {
  ElMessageBox.confirm(`确定要删除采购记录 "${row.id}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await PurchaseApi.deletePurchase(row.id);
      if (res.code === 200) {
        ElMessage.success('采购记录删除成功');
        loadPurchases();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除采购记录失败:', error);
      ElMessage.error('删除失败');
    }
  }).catch(() => {});
};

const resetForm = () => {
  if (purchaseFormRef.value) {
    purchaseFormRef.value.resetFields();
  }
  purchaseForm.value = {
    id: null,
    productId: '',
    supplierId: '',
    quantity: 1,
    unitPrice: 0,
    purchaseDate: null,
    status: 1,
    remark: '',
    shopId: getShopId()
  };
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadPurchases();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  loadPurchases();
};

const getStatusText = (status) => {
  switch (status) {
    case 0: return '待确认';
    case 1: return '已完成';
    case 2: return '已取消';
    default: return '未知';
  }
};

const getStatusType = (status) => {
  switch (status) {
    case 0: return 'warning';
    case 1: return 'success';
    case 2: return 'danger';
    default: return 'info';
  }
};

const formatDate = (date) => {
  if (!date) return '-';
  try {
    // 处理不同的日期格式
    let d;
    if (typeof date === 'string') {
      // 如果是字符串格式，尝试解析
      if (date.includes(' ')) {
        // "2026-02-15 14:37:38" 格式
        d = new Date(date.replace(' ', 'T'));
      } else {
        // ISO格式或其他格式
        d = new Date(date);
      }
    } else {
      // 如果是时间戳
      d = new Date(date);
    }
    
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    const hours = String(d.getHours()).padStart(2, '0');
    const minutes = String(d.getMinutes()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}`;
  } catch (error) {
    console.error('日期格式化错误:', error);
    return date; // 如果格式化失败，返回原始值
  }
};

onMounted(async () => {
  console.log('PurchaseManagement组件挂载，开始加载数据');
  const shopId = getShopId();
  console.log('获取到的店铺ID:', shopId);
  
  if (!shopId || shopId === '0' || shopId === 0) {
    console.log('未获取到店铺ID，显示错误信息');
    ElMessage.error('未获取到店铺ID，请先登录');
    return;
  }
  
  console.log('开始加载采购记录');
  await loadPurchases();
  console.log('采购记录加载完成');
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
