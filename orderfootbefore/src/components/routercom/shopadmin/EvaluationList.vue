<template>
  <div class="order-list">
    <!-- 搜索表单 -->
    <el-form :inline="true" class="search-form" style="margin-bottom: 15px;">
      <el-form-item label="订单号">
        <el-input v-model="textinput.orderNumber" placeholder="订单号" clearable />
      </el-form-item>
      <el-form-item label="用户名">
        <el-input v-model="textinput.nickname" placeholder="用户名" clearable />
      </el-form-item>
      <el-form-item label="商品名称">
        <el-input v-model="textinput.productName" placeholder="商品名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 评价表格 -->
    <el-table 
      :data="orderForm" 
      border 
      style="width: 100%" 
      :row-class-name="getRowClassName"
      v-loading="loading"
    >
      <el-table-column label="订单号" prop="id" />
      <el-table-column label="订单用户" prop="nickname" />
      <el-table-column label="订单菜品" prop="productname" />
	  <el-table-column label="菜品评分" prop="dishScore">
		<template v-slot="scope">
		  <!-- 手动渲染星星：值为5就显示5颗，值为4显示4颗 -->
		  <div style="color: #ff9900; font-size: 16px;">
		    {{ '★'.repeat(Number(scope.row.dishScore) || 0) }}{{ '☆'.repeat(5 - (Number(scope.row.dishScore) || 0)) }}
		  </div>
		</template></el-table-column>
	  <el-table-column label="服务评分" prop="serviceScore">
		<template v-slot="scope">
		  <!-- 手动渲染星星：值为5就显示5颗，值为4显示4颗 -->
		  <div style="color: #ff9900; font-size: 16px;">
		    {{ '★'.repeat(Number(scope.row.serviceScore) || 0) }}{{ '☆'.repeat(5 - (Number(scope.row.serviceScore) || 0)) }}
		  </div>
		</template>
	  </el-table-column>
	  <el-table-column label="环境评分" prop="environmentScore">
		  <template v-slot="scope">
		    <!-- 手动渲染星星：值为5就显示5颗，值为4显示4颗 -->
		    <div style="color: #ff9900; font-size: 16px;">
		      {{ '★'.repeat(Number(scope.row.environmentScore) || 0) }}{{ '☆'.repeat(5 - (Number(scope.row.environmentScore) || 0)) }}
		    </div>
		  </template>
	  </el-table-column>
	  <el-table-column label="评价内容" prop="content" />
	  <el-table-column label="评价图片" prop="imageUrls"/>
	  <el-table-column label="评论日期" prop="createTime" />
      <el-table-column label="操作" width="150">
        <template #default="scope">
          <el-button
            type="danger" 
            size="small" 
            @click="handleEdit(scope.$index, scope.row)"
          >
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 分页组件 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        :page-size="pagination.pageSize" 
        :total="pagination.total"
        layout="total, prev, pager, next, jumper"  
        @current-change="handleCurrentChange"
        @size-change="handleSizeChange"
        @click="BeforOrderCurrentOrder"
        class="custom-pagination"
      />
    </div>
    <!-- 查看弹出框 -->
    <teleport to="body">
      <el-dialog v-model="isDialogVisible" title="订单详情" width="500">
        <div v-if="currentOrder" class="dialog-content">
          <h3>商品id：{{ currentOrder.id || '无' }}</h3>
          <h3>商品名称：{{ currentOrder.productname || '无' }}</h3>
          <h3>用户名称：{{ currentOrder.nickname|| '无' }}</h3>
          <h3>数量：{{ currentOrder.orderItem.quantity || '无' }}</h3>
  		<h3>单价：{{ currentOrder.orderItem.price || '无' }}</h3>
  		<h3>小计：{{ currentOrder.orderItem.amount || '无' }}</h3>
  		<h3>订单备注：{{ currentOrder.orderItem.remark || '无' }}</h3>
  		<h3>评价时间：{{ currentOrder.createTime || '无' }}</h3>
  		<h3>修改时间：{{ currentOrder.updateTime || '无' }}</h3>
        </div>
        <div v-else>暂无订单详情</div>
      </el-dialog>
    </teleport>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import Evaluation from '../../../../api/Evaluation.js';
import { useRouter } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';

const router = useRouter();
const loading = ref(false);
const orderForm = ref([]);
const isDialogVisible = ref(false);
const currentOrder = ref(null);

// 查询条件
const textinput = reactive({
  orderNumber: '',
  nickname: '',
  productName: ''
})

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 8,
  total: 0
})

// 查询参数
const selectwhere = ref({
  page: 1,
  where: []
})

// 页面挂载加载数据
onMounted(() => {
  handleData()
})

// 查询函数 - 模仿UserManagement的sharebylike
function onSubmit() {
  selectwhere.value.where = []
  selectwhere.value.page = 1
  
  if (textinput.orderNumber) {
    selectwhere.value.where.push({
      "column": "a.id",
      "type": "like", 
      "value": textinput.orderNumber
    })
  }
  
  if (textinput.nickname) {
    selectwhere.value.where.push({
      "column": "b.nickname",
      "type": "like",
      "value": textinput.nickname
    })
  }
  
  if (textinput.productName) {
    selectwhere.value.where.push({
      "column": "f.name", 
      "type": "like",
      "value": textinput.productName
    })
  }
  
  handleData()
}

// 重置查询
function resetSearch() {
  textinput.orderNumber = ''
  textinput.nickname = ''
  textinput.productName = ''
  selectwhere.value.where = []
  selectwhere.value.page = 1
  handleData()
}

// 获取数据
async function handleData() {
  loading.value = true
  try {
    // 添加shopId参数
    const params = {
      ...selectwhere.value,
      shopId: localStorage.getItem("shopid") || 1
    }
    
    const res = await Evaluation.selectEvaluation(params)
    orderForm.value = res.data.pageInfo.list || []
    pagination.total = res.data.pageInfo.total || 0
  } catch (error) {
    console.error("获取评价数据失败:", error)
    ElMessage.error("获取评价数据失败")
    orderForm.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 分页处理
function handleCurrentChange(pagenum) {
  selectwhere.value.page = pagenum
  handleData()
}

// 每页大小变化
function handleSizeChange(val) {
  // 这里可以调整每页显示数量，但需要后端支持
  handleData()
}

// 表格行背景
const getRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 查看详情
const handleinquire = (index, row) => {
  if (!row) {
    ElMessage.warning("评价数据异常，无法查看")
    return
  }
  currentOrder.value = { ...row }
  isDialogVisible.value = true
  console.log('详细信息', currentOrder.value)
}

// 删除评价
const handleEdit = async (index, row) => {
  if (!row?.id) {
    ElMessage.warning("评价数据异常，无法删除")
    return
  }
  
  try {
    await ElMessageBox.confirm(
      "确定要删除该评价吗？删除后不可恢复！",
      "警告",
      {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }
    )
    
    const res = await Evaluation.deleteEvaluation(row.id)
    console.log(res.data.status)
    
    if (res?.data?.status === "删除成功") {
      ElMessage.success("删除成功")
      handleData() // 重新加载数据
    } else {
      ElMessage.error(res?.data?.message || "删除失败")
    }
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除评价失败:", error)
      ElMessage.error("删除失败，请稍后重试")
    }
  }
}
</script>

<style lang="scss" scoped>
/* 基础布局 */
.order-list {
  width: 100%;
  box-sizing: border-box;
  padding: 15px;
  display: flex;
  flex-direction: column;
}

/* 搜索表单 */
.search-form {
  display: flex;
  align-items: center;
  gap: 12px;
  background-color: #f5f7fa;
  padding: 15px 20px;
  border-radius: 8px;
}
:deep(.el-form-item) {
  margin: 0 !important;
  display: flex;
  align-items: center;
  height: 36px;
}
:deep(.el-form-item__label) {
  white-space: nowrap;
  padding-right: 8px;
  font-size: 14px;
}
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  height: 36px !important;
}

/* 表格核心样式：隔行变色 + 保留悬停 */
:deep(.el-table) {
  // 覆盖表格默认行样式，确保隔行变色生效
  .el-table__row {
    // 偶数行（白）
    &.even-row > td {
      background-color: #ffffff !important;
    }
    // 奇数行（灰）
    &.odd-row > td {
      background-color: #eaeaea !important;
    }
    // 保留原生悬停变色（继承 Element UI 原有 hover 样式，仅微调权重）
    &:hover > td {
      background-color: var(--el-table-row-hover-bg-color) !important;
    }
  }
}

/* 分页容器 */
.pagination-container {
  margin-top: 15px;
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}
:deep(.custom-pagination) {
  :deep(.el-pagination__total) {
    margin-right: 12px !important;
    color: #666;
    font-size: 14px;
  }
  :deep(.el-pagination__prev),
  :deep(.el-pagination__next),
  :deep(.el-pager li) {
    margin: 0 4px;
  }
  :deep(.el-pager li) {
    min-width: 32px;
    height: 32px;
    line-height: 32px;
  }
}

/* 弹窗内容 */
.dialog-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px 0;
}
.dialog-content h3 {
  margin: 0;
  font-size: 16px;
  color: #333;
  line-height: 1.5;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .search-form {
    flex-wrap: wrap;
    padding: 15px;
  }
  :deep(.el-form-item) {
    width: calc(50% - 6px);
    margin-bottom: 8px !important;
  }
  :deep(.custom-pagination) {
    :deep(.el-pagination__total) {
      margin-right: 8px !important;
      font-size: 13px;
    }
    :deep(.el-pager li) {
      min-width: 28px;
      height: 28px;
      line-height: 28px;
      font-size: 13px;
    }
  }
}
</style>