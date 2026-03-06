<template>
  <div class="order-list">
    <!-- 搜索表单 -->
    <el-form :inline="true" class="search-form" style="margin-bottom: 15px;">
      <el-form-item label="用户昵称">
        <el-input v-model="textinput.nickname" placeholder="用户昵称" clearable />
      </el-form-item>
      <el-form-item label="评价内容">
        <el-input v-model="textinput.content" placeholder="评价内容" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>
    
    <!-- 评价表格 -->
    <el-table 
      :data="tableData" 
      border 
      style="width: 100%" 
      :row-class-name="getRowClassName"
      v-loading="loading"
    >
      <el-table-column label="序号" prop="id" width="60" />
      <el-table-column label="用户头像">
        <template #default="scope">
          <img :src="'http://localhost:8080/imeageserver/'+scope.row.avatar" width="50" height="50" v-if="scope.row.avatar"/>
          <span v-else>无头像</span>
        </template>
      </el-table-column>
      <el-table-column prop="nickname" label="用户昵称" />
      <el-table-column label="评分">
        <template #default="scope">
          <el-rate :model-value="calculateAverageRating(scope.row)" disabled show-score text-color="#ff9900" score-template="{value}分" />
          <div style="font-size: 12px; color: #666;">
            菜品:{{ scope.row.dishScore || 0 }} | 服务:{{ scope.row.serviceScore || 0 }} | 环境:{{ scope.row.environmentScore || 0 }}
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="content" label="评价内容" width="300">
        <template #default="scope">
          <div class="review-content">{{ scope.row.content }}</div>
        </template>
      </el-table-column>
      <el-table-column label="评价图片">
        <template #default="scope">
          <div v-if="scope.row.imageUrls">
            <el-image
              :src="'http://localhost:8080/imeageserver/' + scope.row.imageUrls.split(',')[0]"
              :preview-src-list="scope.row.imageUrls.split(',').map(url => 'http://localhost:8080/imeageserver/' + url)"
              style="width: 50px; height: 50px; margin-right: 5px;"
              fit="cover"
            />
            <span v-if="scope.row.imageUrls.split(',').length > 1">+{{ scope.row.imageUrls.split(',').length - 1 }}</span>
          </div>
          <span v-else>无图片</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="评价时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="scope">
          <el-button 
            type="danger" 
            size="small" 
            @click="del(scope.$index, scope.row.id)"
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
        class="custom-pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import EvaluationApi from '../../../../api/EvaluationApi.js'
import { ElMessageBox, ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])

// 查询条件
const textinput = reactive({
  nickname: '',
  content: ''
})

// 分页配置
const pagination = reactive({
  currentPage: 1,
  pageSize: 8,
  total: 0
})

// 查询参数
const selectwhere = reactive({
  page: 1,
  where: []
})

// 页面挂载加载数据
onMounted(() => {
  handleData()
})

// 查询函数
function onSubmit() {
  selectwhere.where = []
  selectwhere.page = 1
  
  if (textinput.nickname) {
    selectwhere.where.push({
      "column": "nickname",
      "type": "like",
      "value": textinput.nickname
    })
  }
  
  if (textinput.content) {
    selectwhere.where.push({
      "column": "content",
      "type": "like",
      "value": textinput.content
    })
  }
  
  handleData()
}

// 重置查询
function resetSearch() {
  textinput.nickname = ''
  textinput.content = ''
  selectwhere.where = []
  selectwhere.page = 1
  handleData()
}

// 获取数据
async function handleData() {
  loading.value = true
  try {
    // 添加shopId参数（从localStorage获取）
    const params = {
      ...selectwhere,
      shopId: localStorage.getItem("shopid") || 1
    }
    
    const res = await EvaluationApi.EvaluationSelect(params)
    tableData.value = res.data.pageInfo.list || []
    pagination.total = res.data.pageInfo.total || 0
  } catch (error) {
    console.error("获取评价数据失败:", error)
    ElMessage.error("获取评价数据失败")
    tableData.value = []
    pagination.total = 0
  } finally {
    loading.value = false
  }
}

// 分页处理
function handleCurrentChange(pagenum) {
  selectwhere.page = pagenum
  handleData()
}

function handleSizeChange(val) {
  handleData()
}

// 表格行背景
const getRowClassName = ({ rowIndex }) => {
  return rowIndex % 2 === 0 ? 'even-row' : 'odd-row'
}

// 删除评价
const del = async (index, id) => {
  ElMessageBox.confirm('确定要删除这条评价吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      let rs = await EvaluationApi.deleteEvaluation(id)
      if (rs.code === 200) {
        ElMessage.success(rs.data.status || '删除成功')
        handleData()
      } else {
        ElMessage.error(rs.message || '删除失败')
      }
    } catch (error) {
      console.error('删除评价错误:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }).catch(() => {
  })
}

// 计算平均评分
function calculateAverageRating(review) {
  if (!review) return 0
  const dishScore = review.dishScore || 0
  const serviceScore = review.serviceScore || 0
  const environmentScore = review.environmentScore || 0
  const average = (dishScore + serviceScore + environmentScore) / 3
  return parseFloat(average.toFixed(2))
}

// 格式化日期
function formatDate(dateString) {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
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
  .el-table__row {
    &.even-row > td {
      background-color: #ffffff !important;
    }
    &.odd-row > td {
      background-color: #eaeaea !important;
    }
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

.review-content {
  word-break: break-word;
  line-height: 1.5;
  max-height: 60px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
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