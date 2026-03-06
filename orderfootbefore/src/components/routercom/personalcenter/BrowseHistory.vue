<template>
  <div class="browse-history">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" placeholder="请输入菜品名称" class="search-input" v-model="dishName"/>
      <span class="date-label">浏览时间：</span>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="到"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        size="default"
        style="width: 280px;"
      />
      <el-button type="danger" @click="searchHistory" :loading="loading">筛选</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 浏览记录表格 -->
    <div class="table-container" v-if="historyList.length > 0">
      <el-table :data="historyList">
        <el-table-column prop="id" label="序号" width="80"/>
        <el-table-column prop="dishName" label="菜品名称" width="200">
          <template #default="scope">
            <div class="dish-info">
              <img :src="scope.row.dishImage" class="dish-thumb" @error="handleImageError" />
              <div class="dish-details">
                <div class="dish-name">{{ scope.row.dishName }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="shopName" label="商家名称" width="150"/>
        <el-table-column prop="dishPrice" label="价格" width="100">
          <template #default="scope">
            <div class="dish-price">¥{{ scope.row.dishPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="100">
          <template #default="scope">
            <div class="stock-info">
              <span  class="stock-number">{{ scope.row.stock }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="scope.row.status === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ scope.row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="browseTime" label="浏览时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.browseTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #header>
				<el-button type="danger" @click="delhistory(0)">全部清除</el-button>
			</template>
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="danger" size="small" @click="viewDetail(scope.row.dishId)">查看详情</el-button>
              <el-button type="danger" size="small" @click="delhistory(scope.row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
              <el-pagination 
                layout="prev, pager, next" 
                :total="total" 
                :page-size="6"
                :current-page="currentPage"
                @current-change="handlePageChange"
                class="theme-pagination"
              />      </div>
    </div>

    <!-- 空记录提示 -->
    <div class="empty-record" v-else>
      <img src="https://img.icons8.com/fluency/96/000000/clock--v1.png" alt="空记录" class="empty-icon" />
      <p class="empty-text">您还没有浏览记录</p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage,ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import BrowseHistoryApi from '../../../../api/BrowseHistoryApi'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const historyList = ref([])
const total = ref(0)
const currentPage = ref(1)

// 搜索条件
const dishName = ref('')
const dateRange = ref([])

// 分页参数
const params = ref({
  page: 1,
  where: []
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化时长
const formatDuration = (seconds) => {
  if (!seconds) return '0秒'
  if (seconds < 60) return `${seconds}秒`
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分`
  return `${Math.floor(seconds / 3600)}时${Math.floor((seconds % 3600) / 60)}分`
}

// 处理图片URL
const getImageUrl = (imagePath) => {
 if (!imagePath) {
    // 如果src为空，返回一个默认图片或空字符串
    return '';
  }
  return new URL(imagePath.replace('@', '/src'), import.meta.url).href
}

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/src/assets/default-dish.jpg'
}
// 搜索浏览记录
async function searchHistory() {
  try {
    loading.value = true
    
    // 清空之前的查询条件
    params.value.where = []
    
    // 添加菜品名称条件
    if (dishName.value.trim()) {
      params.value.where.push({
        column: 'product.name',
        type: 'like',
        value: dishName.value.trim()
      })
    }
    
    // 添加日期范围条件
    if (dateRange.value && dateRange.value.length === 2) {
      const [start, end] = dateRange.value
      params.value.where.push({
        column: 'browse_history.create_time',
        type: 'ge',
        value: new Date(start).toISOString()
      })
      params.value.where.push({
        column: 'browse_history.create_time',
        type: 'le',
        value: new Date(end).toISOString()
      })
    }
    
    // 调用后端接口
    const res = await BrowseHistoryApi.getHistoryList(params.value);
    console.log("历史记录显示",res);
    if (res.code === 200 && res.data && res.data.pageInfo) {
      const pageInfo = res.data.pageInfo
      const list = pageInfo.list || []
      
      // 转换数据结构，适配前端显示
      historyList.value = list.map((item, index) => ({
        id: item.id || index + 1,
        dishId: item.productId || item.product?.id,
        dishName: item.product?.name || '未知菜品',
        dishPrice: item.product?.price || 0,
        dishImage: item.product?.image ? 'http://localhost:8080/imeageserver/'+item.product.image : '/src/assets/default-dish.jpg',
        shopName: item.shopname || '未知商家',
        browseTime: item.createTime,
        stock: item.product?.stock ?? item.stock ?? 0,
        status: item.product?.status ?? item.status ?? 0,
        duration: Math.floor(Math.random() * 300) + 10 // 随机时长作为备用
      }))
      
      total.value = pageInfo.total || list.length
      currentPage.value = pageInfo.pageNum || 1
      console.log('后端原始数据:', list)
      console.log('转换后的数据:', historyList.value)
    } else {
      ElMessage.error(res.message || '获取浏览记录失败')
      historyList.value = []
      total.value = 0
    }
    
  } catch (error) {
    console.error('获取浏览记录失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    historyList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置搜索
function resetSearch() {
  dishName.value = ''
  dateRange.value = []
  currentPage.value = 1
  params.value.page = 1
  params.value.where = []
  searchHistory()
}

// 分页切换
function handlePageChange(page) {
  params.value.page = page
  currentPage.value = page
  searchHistory()
}

// 查看详情
function viewDetail(dishId) {
  router.push({
    name: 'DishDetail',
    params: { id: dishId }
  })
}
async function delhistory(historyid){
if(historyid==0){
ElMessageBox.confirm('确定清空历史吗?', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(async () => {
			try {
				let rs = await BrowseHistoryApi.delhistory(historyid);
				if (rs.code === 200) {
					ElMessage.success(rs.data.status || '清空成功');
					searchHistory(); // 重新加载数据
				} else {
					ElMessage.error(rs.message || '清空失败');
				}
			} catch (error) {
				console.error('清空地址错误:', error);
				ElMessage.error('清空失败: ' + (error.message || '未知错误'));
			}
		}).catch(() => {
			// 用户取消删除，不执行任何操作
		});
}else{
let res=await BrowseHistoryApi.delhistory(historyid);
if(res.code==200){
 ElMessage.success('删除成功');
 searchHistory();
}
}
}
// 初始化加载
searchHistory()
</script>

<style scoped>
.browse-history {
  width: 100%;
  height: 100%;
}

.search-bar {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  border-bottom: 1px solid #f5f5f5;
  gap: 10px;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #eee;
  border-radius: 20px;
  font-size: 14px;
}

.date-label {
  font-size: 14px;
  color: #666;
  white-space: nowrap;
}

.table-container {
  padding: 20px;
}

.empty-record {
  text-align: center;
  padding: 50px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
}

.dish-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.dish-thumb {
  width: 40px;
  height: 40px;
  border-radius: 4px;
  object-fit: cover;
}

.dish-details {
  flex: 1;
}

.dish-name {
  font-weight: 500;
  color: #333;
  margin-bottom: 2px;
}

.dish-price {
  font-size: 12px;
  color: #d9251c;
}

.stock-info {
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.stock-unlimited {
  color: #409EFF;
  font-weight: 500;
  font-size: 12px;
}

.stock-number {
  color: #333;
  font-weight: 500;
  font-size: 12px;
}

:deep(.el-tag) {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 12px;
}

:deep(.el-tag--success) {
  background-color: #f0f9ff;
  border-color: #b3e5fc;
  color: #0277bd;
}

:deep(.el-tag--danger) {
  background-color: #ffebee;
  border-color: #ffcdd2;
  color: #c62828;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

:deep(.theme-pagination .el-pager li.active) {
  color: #fff !important;
  background-color: #d9251c !important;
  font-weight: bold;
}

:deep(.theme-pagination .el-pager li.is-active) {
  color: #fff !important;
  background-color: #d9251c !important;
}

.theme-pagination button:hover {
  color: #d9251c !important;
}

@media (max-width: 768px) {
  .search-bar {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .search-input {
    width: 100%;
    margin-bottom: 10px;
  }
  
  .date-label {
    display: none;
  }
  
  .dish-info {
    flex-direction: column;
    text-align: center;
  }
  
  .dish-thumb {
    width: 50px;
    height: 50px;
  }
  
  /* 移动端隐藏库存和状态列 */
  .el-table .el-table__cell:nth-child(4),
  .el-table .el-table__cell:nth-child(5) {
    display: none;
  }
}
</style>