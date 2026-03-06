<template>
  <div class="favorite-page">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <input type="text" placeholder="请输入商品名称" class="search-input" v-model="searchKeyword"/>
      <span class="date-label">收藏时间：</span>
      <el-date-picker
        v-model="dateRange"
        type="daterange"
        range-separator="到"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        size="default"
        style="width: 280px;"
      />
      <el-button type="danger" @click="searchFavorites" :loading="loading">筛选</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <!-- 收藏列表表格 -->
    <div class="table-container" v-if="favoriteList.length > 0">
      <el-table :data="paginatedFavorites">
        <el-table-column prop="id" label="序号" width="80"/>
        <el-table-column prop="product.name" label="商品名称" width="200">
          <template #default="scope">
            <div class="dish-info">
              <img :src="'http://localhost:8080/imeageserver/'+scope.row.product.image" class="dish-thumb" @error="handleImageError" />
              <div class="dish-details">
                <div class="dish-name">{{ scope.row.product.name }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="shopName" label="商家名称" width="150"></el-table-column>
        <el-table-column prop="product.price" label="价格" width="100">
          <template #default="scope">
            <div class="dish-price">¥{{ scope.row.product.price ? scope.row.product.price.toFixed(2) : '0.00' }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="product.stock" label="库存" width="100">
          <template #default="scope">
            <div class="stock-info">
              <span v-if="scope.row.product.stock === 0" class="stock-unlimited">无限</span>
              <span v-else class="stock-number">{{ scope.row.product.stock }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="product.status" label="状态" width="100">
          <template #default="scope">
            <el-tag 
              :type="scope.row.product.status === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ scope.row.product.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="收藏时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="viewDetail(scope.row.dishId)">查看详情</el-button>
              <el-button type="danger" size="small" @click="removeFavorite(scope.row.id, scope.$index)">取消收藏</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <div class="pagination-container">
        <el-pagination 
          layout="prev, pager, next" 
          :total="total" 
          :page-size="pageSize"
          :current-page="currentPage"
          @current-change="handlePageChange"
          class="theme-pagination"
        />
      </div>
    </div>

    <!-- 空记录提示 -->
    <div class="empty-record" v-else>
      <img src="https://img.icons8.com/fluency/96/000000/bookmark-ribbon.png" alt="空记录" class="empty-icon" />
      <p class="empty-text">您还没有收藏任何商品</p>
      <el-button type="primary" style="background-color: #E63946; border: none;" @click="goHome">
        去首页看看
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import FavoriteApi from '../../../../api/FavoriteApi'
import ShopcartApi from '../../../../api/ShopcartApi'

const router = useRouter()

// 响应式数据
const loading = ref(false)
const favoriteList = ref([])
const total = ref(0)
const currentPage = ref(1)

// 搜索条件
const searchKeyword = ref('')
const dateRange = ref([])

// 分页参数
const params = ref({
  page: 1,
  where: []
})
const pageSize = ref(6)

// 计算分页后的收藏列表
const paginatedFavorites = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return favoriteList.value.slice(start, end)
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

// 处理图片URL
const getImageUrl = (imagePath) => {
  if (!imagePath) {
    return '';
  }
  return new URL(imagePath.replace('@', '/src'), import.meta.url).href
}

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = '/src/assets/default-dish.jpg'
}

// 获取收藏列表
const loadFavorites = async () => {
  try {
    loading.value = true
    
    // 调用后端接口
    const res = await FavoriteApi.FavoriteSelect();
    
    if (res.code === 200 && res.data && res.data.favoriteList) {
      const list = res.data.favoriteList || []
      
      // 转换数据结构，适配前端显示
      favoriteList.value = list.map((item, index) => ({
        id: item.id || index + 1,
        dishId: item.productId || item.product?.id,
        product: item.product || {},
        shopName: item.shopname || '未知商家',
        createTime: item.createTime || new Date().toISOString()
      }))
      
      total.value = list.length
      currentPage.value = params.value.page
    } else {
      ElMessage.error(res.message || '获取收藏列表失败')
      favoriteList.value = []
      total.value = 0
    }
    
  } catch (error) {
    console.error('获取收藏列表失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    favoriteList.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 搜索收藏
const searchFavorites = async () => {
  try {
    loading.value = true
    
    // 重置分页
    params.value.page = 1
    currentPage.value = 1
    params.value.where = []
    
    // 添加商品名称条件
    if (searchKeyword.value.trim()) {
      params.value.where.push({
        column: 'product.name',
        type: 'like',
        value: searchKeyword.value.trim()
      })
    }
    
    // 添加日期范围条件
    if (dateRange.value && dateRange.value.length === 2) {
      const [start, end] = dateRange.value
      params.value.where.push({
        column: 'user_favorites.create_time',
        type: 'ge',
        value: new Date(start).toISOString()
      })
      params.value.where.push({
        column: 'user_favorites.create_time',
        type: 'le',
        value: new Date(end).toISOString()
      })
    }
    
    // 调用后端接口
    const res = await FavoriteApi.FavoriteSelect();
    
    if (res.code === 200 && res.data && res.data.favoriteList) {
      let list = res.data.favoriteList || []
      
      // 客户端筛选（因为当前API不支持复杂的where条件）
      if (searchKeyword.value.trim()) {
        list = list.filter(item => 
          item.product?.name?.toLowerCase().includes(searchKeyword.value.toLowerCase()) ||
          (item.product?.shopname && item.product.shopname.toLowerCase().includes(searchKeyword.value.toLowerCase()))
        )
      }
      
      if (dateRange.value && dateRange.value.length === 2) {
        const [start, end] = dateRange.value
        const startDate = new Date(start).getTime()
        const endDate = new Date(end).getTime()
        
        list = list.filter(item => {
          const itemDate = new Date(item.createTime).getTime()
          return itemDate >= startDate && itemDate <= endDate
        })
      }
      
      favoriteList.value = list.map((item, index) => ({
        id: item.id || index + 1,
        dishId: item.productId || item.product?.id,
        product: item.product || {},
        shopName: item.shopname || '未知商家',
        createTime: item.createTime || new Date().toISOString()
      }))
      
      total.value = list.length
    } else {
      ElMessage.error(res.message || '搜索失败')
    }
    
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

// 重置搜索
const resetSearch = () => {
  searchKeyword.value = ''
  dateRange.value = []
  currentPage.value = 1
  params.value.page = 1
  params.value.where = []
  loadFavorites()
}

// 分页切换
function handlePageChange(page) {
  params.value.page = page
  currentPage.value = page
  loadFavorites()
}

// 查看详情
function viewDetail(productId) {
  router.push({
    name: 'DishDetail',
    params: { id: productId }
  })
}



// 取消收藏
const removeFavorite = async (favoriteId, index) => {
  try {
    await ElMessageBox.confirm(
      '确定要取消收藏这个商品吗？',
      '确认取消收藏',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    )

    const res = await FavoriteApi.FavoriteDelete(favoriteId)
    
    if (res.code === 200) {
      // 从列表中删除
      favoriteList.value.splice(index, 1)
      total.value -= 1
      ElMessage.success('已取消收藏')
      
      // 如果当前页没有数据了，切换到上一页
      const start = (currentPage.value - 1) * pageSize.value
      if (start >= favoriteList.value.length && currentPage.value > 1) {
        currentPage.value -= 1
        params.value.page = currentPage.value
      }
    } else {
      ElMessage.error(res.message || '取消收藏失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消收藏异常:', error)
      ElMessage.error('取消收藏失败，请稍后重试')
    }
  }
}

// 返回首页
const goHome = () => {
  router.push('/home/selectall')
}

// 初始化加载
onMounted(() => {
  loadFavorites()
})
</script>

<style scoped>
.favorite-page {
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
  color: #E63946;
}

.shop-name {
  font-size: 12px;
  color: #333;
  font-weight: 500;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.stock-info {
  display: flex;
  align-items: center;
  justify-content: center;
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
  background-color: #E63946 !important;
  font-weight: bold;
}

:deep(.theme-pagination .el-pager li.is-active) {
  color: #fff !important;
  background-color: #E63946 !important;
}

.theme-pagination button:hover {
  color: #E63946 !important;
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