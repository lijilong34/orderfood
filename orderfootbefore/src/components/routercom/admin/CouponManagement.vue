<template>
  <div class="coupon-management">
    <el-card>
      <template #header>
        <div class="card-header">
          <span class="title">优惠券管理</span>
          <el-button type="primary" @click="addCoupon">添加优惠券</el-button>
        </div>
      </template>

      <div class="search-filter">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索优惠券名称"
              prefix-icon="Search"
              clearable
              @clear="loadCoupons"
              @keyup.enter="searchCoupons"
            />
          </el-col>
          <el-col :span="6">
            <el-select v-model="typeFilter" placeholder="优惠券类型" clearable>
              <el-option label="满减券" :value="1" />
              <el-option label="折扣券" :value="2" />
            </el-select>
          </el-col>
          <el-col :span="6">
            <el-select v-model="statusFilter" placeholder="状态" clearable>
              <el-option label="已使用" :value="0" />
              <el-option label="未使用" :value="1" />
              <el-option label="已过期" :value="2" />
            </el-select>
          </el-col>
          <el-col :span="4">
            <el-button type="primary" @click="searchCoupons">搜索</el-button>
            <el-button type="primary" plain @click="resetFilters">重置</el-button>
          </el-col>
        </el-row>
      </div>

      <el-table :data="couponList" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="优惠券名称" width="180" />
        <el-table-column label="优惠类型" width="150">
          <template #default="{ row }">
            <el-tag :type="row.discountType === 1 ? 'primary' : 'success'">
              {{ row.discountType === 1 ? '满减' : '折扣' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="优惠内容" width="200">
          <template #default="{ row }">
            <span v-if="row.discountType === 1">满{{ row.minAmount }}减{{ row.discountValue }}</span>
            <span v-else>{{ row.discountValue }}折</span>
          </template>
        </el-table-column>
        <el-table-column label="生效时间" width="200">
          <template #default="{ row }">
            {{ formatDate(row.startTime) }}
          </template>
        </el-table-column>
        <el-table-column label="过期时间" width="200">
          <template #default="{ row }">
            {{ formatDate(row.endTime) }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="200">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="editCoupon(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCoupon(row)">删除</el-button>
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

    <!-- 添加/编辑优惠券对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @close="resetForm"
    >
      <el-form :model="couponForm" :rules="formRules" ref="couponFormRef" label-width="100px">
        <el-form-item label="优惠券名称" prop="title">
          <el-input v-model="couponForm.title" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠类型" prop="discountType">
          <el-radio-group v-model="couponForm.discountType">
            <el-radio :value="1">满减券</el-radio>
            <el-radio :value="2">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠值" prop="discountValue">
          <el-input-number
            v-model="couponForm.discountValue"
            :min="0"
            :precision="couponForm.discountType === 2 ? 1 : 2"
            :step="couponForm.discountType === 2 ? 0.1 : 0.01"
            :max="couponForm.discountType === 2 ? 10 : 999999"
            :controls="false"
            style="width: 100%"
          />
          <span style="margin-left: 10px; color: #909399;">
            {{ couponForm.discountType === 1 ? '元' : '折' }}
          </span>
        </el-form-item>
        <el-form-item label="最低消费" prop="minAmount" v-if="couponForm.discountType === 1">
          <el-input-number
            v-model="couponForm.minAmount"
            :min="0"
            :precision="2"
            :step="0.01"
            :controls="false"
            style="width: 100%"
          />
          <span style="margin-left: 10px; color: #909399;">元</span>
        </el-form-item>
        <el-form-item label="生效时间" prop="startTime">
          <el-date-picker
            v-model="couponForm.startTime"
            type="datetime"
            placeholder="选择生效时间"
            style="width: 100%"
            value-format="x"
          />
        </el-form-item>
        <el-form-item label="过期时间" prop="endTime">
          <el-date-picker
            v-model="couponForm.endTime"
            type="datetime"
            placeholder="选择过期时间"
            style="width: 100%"
            value-format="x"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="couponForm.status">
            <el-radio :value="0">已使用</el-radio>
            <el-radio :value="1">未使用</el-radio>
            <el-radio :value="2">已过期</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCoupon" :loading="saveLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search } from '@element-plus/icons-vue';
import CouponAdminApi from '../../../../api/CouponAdminApi';

const loading = ref(false);
const saveLoading = ref(false);
const couponList = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const searchKeyword = ref('');
const typeFilter = ref('');
const statusFilter = ref('');

const dialogVisible = ref(false);
const dialogTitle = ref('添加优惠券');
const couponFormRef = ref(null);

const couponForm = ref({
  id: null,
  title: '',
  discountType: 1,
  discountValue: 0,
  minAmount: 0,
  shopId: 0,
  startTime: '',
  endTime: '',
  quantity: 1
});

const formRules = {
  title: [{ required: true, message: '请输入优惠券名称', trigger: 'blur' }],
  discountType: [{ required: true, message: '请选择优惠类型', trigger: 'change' }],
  discountValue: [{ required: true, message: '请输入优惠值', trigger: 'blur' }],
  minAmount: [{ required: true, message: '请输入最低消费', trigger: 'blur' }],
  startTime: [{ required: true, message: '请选择生效时间', trigger: 'change' }],
  endTime: [{ required: true, message: '请选择过期时间', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
};

const loadCoupons = async () => {
  loading.value = true;
  try {
    const res = await CouponAdminApi.getCouponList({
      page: currentPage.value,
      pageSize: pageSize.value,
      keyword: searchKeyword.value,
      discountType: typeFilter.value,
      status: statusFilter.value
    });

    if (res.code === 200 && res.data && res.data.pageInfo) {
      couponList.value = res.data.pageInfo.list;
      total.value = res.data.pageInfo.total;
    } else {
      ElMessage.error(res.message || '获取优惠券列表失败');
    }
  } catch (error) {
    console.error('获取优惠券列表失败:', error);
    ElMessage.error('获取优惠券列表失败');
  } finally {
    loading.value = false;
  }
};

const searchCoupons = () => {
  currentPage.value = 1;
  loadCoupons();
};

const filterByType = () => {
  currentPage.value = 1;
  loadCoupons();
};

const filterByStatus = () => {
  currentPage.value = 1;
  loadCoupons();
};

const resetFilters = () => {
  searchKeyword.value = '';
  typeFilter.value = '';
  statusFilter.value = '';
  currentPage.value = 1;
  loadCoupons();
};

const addCoupon = () => {
  dialogTitle.value = '添加优惠券';
  couponForm.value = {
    id: null,
    title: '',
    discountType: 1,
    discountValue: 0,
    minAmount: 0,
    shopId: 0,
    startTime: '',
    endTime: '',
    status: 1
  };
  dialogVisible.value = true;
};

const editCoupon = (row) => {
  dialogTitle.value = '编辑优惠券';
  couponForm.value = {
    id: row.id,
    title: row.title,
    discountType: row.discountType,
    discountValue: row.discountValue,
    minAmount: row.minAmount,
    shopId: row.shopId,
    startTime: row.startTime ? new Date(row.startTime).getTime() : '',
    endTime: row.endTime ? new Date(row.endTime).getTime() : '',
    status: row.status
  };
  dialogVisible.value = true;
};

const saveCoupon = async () => {
  if (!couponFormRef.value) return;

  try {
    await couponFormRef.value.validate();

    // 验证时间
    if (couponForm.value.startTime && couponForm.value.endTime) {
      if (couponForm.value.startTime >= couponForm.value.endTime) {
        ElMessage.error('过期时间必须大于生效时间');
        return;
      }
    }

    saveLoading.value = true;

    let res;
    if (couponForm.value.id) {
      // 更新
      res = await CouponAdminApi.updateCoupon(couponForm.value);
    } else {
      // 创建
      res = await CouponAdminApi.createCoupon(couponForm.value);
    }

    if (res.code === 200) {
      ElMessage.success(couponForm.value.id ? '优惠券更新成功' : '优惠券创建成功');
      dialogVisible.value = false;
      loadCoupons();
    } else {
      ElMessage.error(res.message || '保存失败');
    }
  } catch (error) {
    if (error !== false) {
      console.error('保存优惠券失败:', error);
      ElMessage.error('保存失败');
    }
  } finally {
    saveLoading.value = false;
  }
};

const deleteCoupon = (row) => {
  ElMessageBox.confirm(`确定要删除优惠券 "${row.title}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await CouponAdminApi.deleteCoupon(row.id);
      if (res.code === 200) {
        ElMessage.success('优惠券删除成功');
        loadCoupons();
      } else {
        ElMessage.error(res.message || '删除失败');
      }
    } catch (error) {
      console.error('删除优惠券失败:', error);
      ElMessage.error('删除失败');
    }
  }).catch(() => {});
};

const resetForm = () => {
  if (couponFormRef.value) {
    couponFormRef.value.resetFields();
  }
  couponForm.value = {
    id: null,
    title: '',
    discountType: 1,
    discountValue: 0,
    minAmount: 0,
    shopId: 0,
    startTime: '',
    endTime: '',
    status: 1
  };
};

const handleSizeChange = (val) => {
  pageSize.value = val;
  loadCoupons();
};

const handleCurrentChange = (val) => {
  currentPage.value = val;
  loadCoupons();
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

const getStatusType = (status) => {
  const typeMap = {
    0: 'success',    // 已使用 - 绿色
    1: 'warning',    // 未使用 - 橙色
    2: 'danger'      // 已过期 - 红色
  };
  return typeMap[status] || 'info';
};

const getStatusText = (status) => {
  const textMap = {
    0: '已使用',
    1: '未使用',
    2: '已过期'
  };
  return textMap[status] || '未知';
};

onMounted(() => {
  loadCoupons();
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
