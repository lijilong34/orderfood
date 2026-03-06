<template>
  <div class="inventory-management">
    <div class="page-header">
      <h2>库存管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="addProduct">添加商品</el-button>
        <el-button @click="exportInventory">导出库存</el-button>
      </div>
    </div>

    <div class="search-filter">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索商品名称" 
            prefix-icon="el-icon-search"
            @keyup.enter="searchInventory"
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="categoryFilter" placeholder="选择分类" @change="filterByCategory">
            <el-option label="全部分类" value="" />
            <el-option label="主食" value="main" />
            <el-option label="饮品" value="drink" />
            <el-option label="小吃" value="snack" />
            <el-option label="甜品" value="dessert" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="库存状态" @change="filterByStatus">
            <el-option label="全部状态" value="" />
            <el-option label="库存充足" value="sufficient" />
            <el-option label="库存不足" value="low" />
            <el-option label="缺货" value="out" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="searchInventory">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="inventory-table">
      <el-table 
        :data="inventoryList" 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="name" label="商品名称" width="200" />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="currentStock" label="当前库存" width="120">
          <template #default="{ row }">
            <el-tag :type="getStockStatusType(row.currentStock)">
              {{ row.currentStock }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="minStock" label="最低库存" width="120" />
        <el-table-column prop="maxStock" label="最高库存" width="120" />
        <el-table-column prop="unit" label="单位" width="100" />
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            ¥{{ row.price }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="editProduct(row)">编辑</el-button>
            <el-button size="small" type="primary" @click="adjustStock(row)">调库存</el-button>
            <el-button size="small" type="danger" @click="deleteProduct(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="500px"
      @close="resetForm"
    >
      <el-form :model="productForm" :rules="formRules" ref="productFormRef" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择分类">
            <el-option label="主食" value="main" />
            <el-option label="饮品" value="drink" />
            <el-option label="小吃" value="snack" />
            <el-option label="甜品" value="dessert" />
          </el-select>
        </el-form-item>
        <el-form-item label="当前库存" prop="currentStock">
          <el-input-number v-model="productForm.currentStock" :min="0" />
        </el-form-item>
        <el-form-item label="最低库存" prop="minStock">
          <el-input-number v-model="productForm.minStock" :min="0" />
        </el-form-item>
        <el-form-item label="最高库存" prop="maxStock">
          <el-input-number v-model="productForm.maxStock" :min="0" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="productForm.unit" placeholder="如：份、杯、个" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">确定</el-button>
      </template>
    </el-dialog>

    <!-- 调库存对话框 -->
    <el-dialog title="调整库存" v-model="stockDialogVisible" width="400px">
      <el-form :model="stockForm" label-width="100px">
        <el-form-item label="商品名称">
          <span>{{ stockForm.productName }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span>{{ stockForm.currentStock }}</span>
        </el-form-item>
        <el-form-item label="调整数量" prop="adjustment">
          <el-input-number v-model="stockForm.adjustment" :min="-stockForm.currentStock" :max="9999" />
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="stockForm.reason" type="textarea" placeholder="请输入调整原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="stockDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmStockAdjustment">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchKeyword = ref('');
const categoryFilter = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const inventoryList = ref([
  { id: 1, name: '宫保鸡丁', category: 'main', currentStock: 15, minStock: 5, maxStock: 50, unit: '份', price: 28.00, status: 'sufficient' },
  { id: 2, name: '麻婆豆腐', category: 'main', currentStock: 3, minStock: 5, maxStock: 30, unit: '份', price: 18.00, status: 'low' },
  { id: 3, name: '可乐', category: 'drink', currentStock: 0, minStock: 10, maxStock: 100, unit: '杯', price: 8.00, status: 'out' },
  { id: 4, name: '红烧肉', category: 'main', currentStock: 25, minStock: 5, maxStock: 40, unit: '份', price: 38.00, status: 'sufficient' },
  { id: 5, name: '冰淇淋', category: 'dessert', currentStock: 12, minStock: 3, maxStock: 20, unit: '个', price: 12.00, status: 'sufficient' }
]);

const dialogVisible = ref(false);
const dialogTitle = ref('添加商品');
const productForm = ref({
  id: null,
  name: '',
  category: '',
  currentStock: 0,
  minStock: 0,
  maxStock: 0,
  unit: '',
  price: 0
});

const stockDialogVisible = ref(false);
const stockForm = ref({
  productId: null,
  productName: '',
  currentStock: 0,
  adjustment: 0,
  reason: ''
});

const formRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  currentStock: [{ required: true, message: '请输入当前库存', trigger: 'blur' }],
  minStock: [{ required: true, message: '请输入最低库存', trigger: 'blur' }],
  maxStock: [{ required: true, message: '请输入最高库存', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }]
};

// 获取库存状态类型
const getStockStatusType = (stock) => {
  if (stock === 0) return 'danger';
  if (stock <= 5) return 'warning';
  return 'success';
};

// 获取状态类型
const getStatusType = (status) => {
  if (status === 'sufficient') return 'success';
  if (status === 'low') return 'warning';
  if (status === 'out') return 'danger';
  return 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  if (status === 'sufficient') return '库存充足';
  if (status === 'low') return '库存不足';
  if (status === 'out') return '缺货';
  return '未知';
};

// 搜索库存
const searchInventory = () => {
  console.log('搜索库存:', searchKeyword.value);
  // 这里应该调用API进行搜索
};

// 按分类筛选
const filterByCategory = () => {
  console.log('按分类筛选:', categoryFilter.value);
};

// 按状态筛选
const filterByStatus = () => {
  console.log('按状态筛选:', statusFilter.value);
};

// 添加商品
const addProduct = () => {
  dialogTitle.value = '添加商品';
  productForm.value = {
    id: null,
    name: '',
    category: '',
    currentStock: 0,
    minStock: 0,
    maxStock: 0,
    unit: '',
    price: 0
  };
  dialogVisible.value = true;
};

// 编辑商品
const editProduct = (row) => {
  dialogTitle.value = '编辑商品';
  productForm.value = { ...row };
  dialogVisible.value = true;
};

// 保存商品
const saveProduct = () => {
  console.log('保存商品:', productForm.value);
  // 这里应该调用API保存商品
  ElMessage.success('商品保存成功');
  dialogVisible.value = false;
};

// 调库存
const adjustStock = (row) => {
  stockForm.value = {
    productId: row.id,
    productName: row.name,
    currentStock: row.currentStock,
    adjustment: 0,
    reason: ''
  };
  stockDialogVisible.value = true;
};

// 确认库存调整
const confirmStockAdjustment = () => {
  console.log('确认库存调整:', stockForm.value);
  // 这里应该调用API调整库存
  ElMessage.success('库存调整成功');
  stockDialogVisible.value = false;
};

// 删除商品
const deleteProduct = (row) => {
  ElMessageBox.confirm(`确定要删除商品 "${row.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用API删除商品
    inventoryList.value = inventoryList.value.filter(item => item.id !== row.id);
    ElMessage.success('商品删除成功');
  });
};

// 导出库存
const exportInventory = () => {
  console.log('导出库存');
  // 这里应该调用API导出库存数据
  ElMessage.success('库存导出功能开发中');
};

// 重置表单
const resetForm = () => {
  productForm.value = {
    id: null,
    name: '',
    category: '',
    currentStock: 0,
    minStock: 0,
    maxStock: 0,
    unit: '',
    price: 0
  };
};

// 处理多选
const handleSelectionChange = (selection) => {
  console.log('选中项:', selection);
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  // 重新加载数据
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  // 重新加载数据
};

onMounted(() => {
  // 初始化数据
});
</script>

<style scoped>
.inventory-management {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0;
  color: #333;
}

.search-filter {
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.inventory-table {
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.pagination {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
