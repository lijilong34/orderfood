<template>
  <!-- 顶部返回+商家名称区域 -->
  <div class="header-bar">
    <el-button 
      icon="el-icon-arrow-left" 
      type="text" 
      class="back-btn"
      @click="goBack"
    >
      返回
    </el-button>
    <div class="current-shop-name">{{ shopInfo.name }}</div>
  </div>

  <!-- 商家信息展示区域 -->
  <div class="shop-info-card">
    <div class="shop-info-grid">
      <div class="info-item">
        <i class="el-icon-phone-outline"></i>
        <span class="info-label">电话：</span>
        <span class="info-value">{{ shopInfo.phone || '暂无' }}</span>
      </div>
      <div class="info-item">
        <i class="el-icon-location-outline"></i>
        <span class="info-label">地址：</span>
        <span class="info-value">{{ shopInfo.address || '暂无' }}</span>
      </div>
      <div class="info-item">
        <i class="el-icon-time"></i>
        <span class="info-label">营业时间：</span>
        <span class="info-value">{{ shopInfo.businessHours || '暂无' }}</span>
      </div>
      <div class="info-item">
        <i class="el-icon-setting"></i>
        <span class="info-label">状态：</span>
        <span class="info-value status">
          <el-tag  type="success" v-if="shopInfo.status">开放</el-tag>
          <el-tag type="danger" v-else-if="shopInfo.status">休息</el-tag>
        </span>
      </div>
      <div class="info-item">
        <i class="el-icon-menu"></i>
        <span class="info-label">经营品类：</span>
        <span class="info-value">{{ shopInfo.businessScope || '暂无' }}</span>
      </div>
      <div class="info-item">
        <i class="el-icon-truck"></i>
        <span class="info-label">停车信息：</span>
        <span class="info-value">{{ shopInfo.parkingInfo || '暂无' }}</span>
      </div>
    </div>
    <div class="shop-introduction" v-if="shopInfo.introduction">
      <div class="intro-title">
        <i class="el-icon-document"></i>
        <span>店铺介绍</span>
      </div>
      <div class="intro-content">{{ shopInfo.introduction }}</div>
    </div>
  </div>

  <!-- 搜索区域（模糊查询+查询全部） -->
  <div class="search-box">
    <div class="search-container">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索菜品" 
        size="large" 
        class="search-input"
        prefix-icon="el-icon-search"
        @keyup.enter="onSearch"
        :class="{ 'search-input-focus': isInputFocus }"
        @focus="isInputFocus = true"
        @blur="isInputFocus = false"
      />
      <div class="search-btn-group">
        <el-button 
          type="primary" 
          size="large" 
          class="search-btn" 
          @click="onSearch"
        >
          搜索
        </el-button>
        <el-button 
          type="warning" 
          size="large" 
          class="reset-btn" 
          @click="resetSearch"
        >
          查询所有
        </el-button>
      </div>
    </div>
  </div>

  <!-- 菜品列表区域 -->
  <div class="content">
    <div class="list-header">所有菜品</div>
    <div class="goods-grid">
      <div 
        v-for="(good, idx) in goodsList" 
        :key="good.id" 
        class="goods-card"
        @click="goproduct(good.id)"
      >
        <div class="rank">{{ idx + 1 }}</div>
        <img :src="'http://localhost:8080/imeageserver/'+good.image" class="goods-img" />
        <div class="info">
          <div class="name">{{ good.name }}</div>
          <div class="spec">单价:{{ good.price }}元</div>
        </div>
      </div>
    </div>
  </div>

  <!-- 分页组件 -->
  <div class="pagination-container" v-if="total > pageSize">
    <el-pagination 
      layout="prev, pager, next"  
      :total="total" 
      :page-size="pageSize"
      :current-page="currentPage"
      @current-change="handlePageChange"
      class="theme-pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router' // 新增useRouter
import { ElMessage } from 'element-plus'
// 假设接口文件：需替换为实际接口路径
import ShopApi from '../../../api/ShopApi'
import productApi from '../../../api/productApi'
let shopInfo=ref({});
const route = useRoute()
const router = useRouter() // 初始化router
const shopId = route.params.Id // 接收商家ID

// 检查shopId是否有效
if (!shopId || shopId === 'undefined') {
  ElMessage.error('商家ID无效，请重新访问')
  router.back()
  throw new Error('Invalid shopId')
}

// 新增：当前商家名称
const currentShopName = ref('')

// 搜索&分页参数
const searchQuery = ref('')
const goodsList = ref([])
const total = ref(0)
const isInputFocus = ref(false)
const currentPage = ref(1)
const pageSize = 8 // 每页显示8条，与后端保持一致

// 新增：返回上一级
function goBack() {
  router.back() // 回退到商家列表页
}

// 新增：获取商家名称
async function getShopName() {
  try {
    // 调用获取商家信息的接口（需根据实际接口调整）
    const res = await ShopApi.getShopInfo(shopId)
    console.log("获取商家信息:",res)
    if (res.code === 200) {
      shopInfo.value = res.data.entity;
    }
  } catch (error) {
    console.error('获取商家名称失败:', error)
    currentShopName.value = '未知商家'
  }
}

// 搜索菜品
function onSearch() {
  currentPage.value = 1 // 搜索时重置到第一页
  getGoodsList()
}

// 重置搜索（查询全部）
function resetSearch() {
  searchQuery.value = ""
  currentPage.value = 1
  getGoodsList()
  isInputFocus.value = false
}

// 分页切换
function handlePageChange(pageno) {
  currentPage.value = pageno
  getGoodsList()
}

// 图片路径处理
function getImg(src) {
   if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}

// 请求菜品列表
async function getGoodsList() {
  try {
    // 构建查询条件
    const whereConditions = []
    
    // 添加搜索条件
    if (searchQuery.value.trim()) {
      whereConditions.push({
        "column": "name",
        "type": "like",
        "value": searchQuery.value.trim()
      })
    }
  
    // 添加商家ID条件
    whereConditions.push({
      "column": "shop_id",
      "type": "eq",
      "value": shopId
    })
    
    // 添加状态条件（只显示上架的商品）
    whereConditions.push({
      "column": "status",
      "type": "eq",
      "value": 1
    })
    
    // 添加库存条件（只显示有库存的商品）
    whereConditions.push({
      "column": "stock",
      "type": "gt",
      "value": 0
    })

    const queryParams = {
      page: currentPage.value,
      where: whereConditions
    }
    
    console.log("查询参数:", queryParams)
    
    const res = await productApi.ProductSelect(queryParams)
    console.log("查询结果:", res)
    
    if (res.code === 200) {
      goodsList.value = res.data.pageInfo.list || []
      total.value = res.data.pageInfo.total || 0
      console.log("获取到菜品:", goodsList.value.length, "条，总数:", total.value)
    } else {
      ElMessage.error(res.message || '获取菜品失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
    console.error("查询菜品异常:", error)
  }
}

function goproduct(productId) {
  // 确保productId是有效的数字
  const id = Number(productId);
  if (isNaN(id)) {
    ElMessage.error('无效的产品ID');
    return;
  }
  router.push({
    name: 'DishDetail',
    params: { id }
  })
}

onMounted(() => {
  getShopName() // 先获取商家名称
  getGoodsList() // 再获取菜品列表
})
</script>

<style scoped>
/* 顶部返回+商家名称样式：与home.vue保持一致的红色主题 */
.header-bar {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  background-color: #FFF8F0; /* 登录页红色主色 */
  color: #fff; /* 白色文字 */
  box-shadow: 0 2px 8px rgba(229, 77, 66, 0.3); /* 红色阴影增强层次感 */
  margin-bottom: 10px;
}

.back-btn {
  font-size: 16px;
  color: #ffd700; /* 黄色，与主题色呼应 */
  padding: 8px 12px;
  margin-right: 15px;
  &:hover {
    color: #e80d0d;
    background-color: rgba(255, 215, 0, 0.2);
    border-radius: 6px;
  }
}

.current-shop-name {
  font-size: 20px;
  font-weight: 600;
  color: #fd0c0c; /* 白色文字 */
}

/* 原有样式保留，仅调整间距适配 */
.search-box {
  display: flex;
  justify-content: center;
  margin: 15px 0 25px; /* 减少顶部间距 */
  padding: 0 20px;
}

.search-container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 800px;
  gap: 12px;
}

.search-input {
  flex: 1;
  height: 50px;
  border: 2px solid #e54d42;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s ease;
  &::placeholder {
    color: #999;
    font-size: 14px;
  }
}

.search-input-focus {
  border-color: #ffd700 !important;
  box-shadow: 0 0 0 3px rgba(229, 77, 66, 0.2) !important;
}

.search-btn-group {
  display: flex;
  gap: 10px;
}

.search-btn {
  background-color: #e54d42 !important;
  border-color: #e54d42 !important;
  border-radius: 8px;
  padding: 0 24px;
  &:hover {
    background-color: #d43829 !important;
    border-color: #d43829 !important;
  }
}

.reset-btn {
  background-color: #ffd700 !important;
  border-color: #ffd700 !important;
  color: #333 !important;
  border-radius: 8px;
  padding: 0 24px;
  &:hover {
    background-color: #f0c800 !important;
    border-color: #f0c800 !important;
  }
}

.content {
  padding: 0 20px 30px;
  flex: 1;
}

.list-header {
  font-size: 22px;
  font-weight: 600;
  color: #e54d42;
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ffd700;
}

.goods-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

.goods-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-top: 3px solid #e54d42;
}

.goods-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(229, 77, 66, 0.15);
}

.rank {
  font-size: 26px;
  font-weight: 700;
  color: #ffd700;
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background-color: #fff8f0;
  border-radius: 50%;
}

.goods-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #fff8f0;
}

.name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  text-align: center;
}

.spec {
  font-size: 14px;
  color: #666;
  text-align: center;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}

:deep(.theme-pagination .el-pager li.active) {
  color: #fff !important;
  background-color: #e54d42 !important;
  font-weight: bold;
}

:deep(.theme-pagination .el-pager li.is-active) {
  color: #fff !important;
  background-color: #e54d42 !important;
}

.theme-pagination button:hover {
  color: #e54d42 !important;
}

/* 商家信息展示区域样式 */
.shop-info-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin: 0 20px 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  border-left: 4px solid #e54d42;
}

.shop-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 16px;
  margin-bottom: 20px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: #fff8f0;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.info-item:hover {
  background-color: #fff0e0;
}

.info-item i {
  font-size: 18px;
  color: #e54d42;
}

.info-label {
  font-weight: 600;
  color: #333;
  min-width: 80px;
}

.info-value {
  color: #666;
  flex: 1;
}

.status {
  font-weight: 600;
  padding: 2px 8px;
  border-radius: 4px;
}

.status-open {
  color: #67c23a;
  background-color: #f0f9ff;
}

.status-rest {
  color: #f56c6c;
  background-color: #fef0f0;
}

.status-paused {
  color: #e6a23c;
  background-color: #fdf6ec;
}

.shop-introduction {
  border-top: 1px solid #f5f5f5;
  padding-top: 16px;
}

.intro-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  font-weight: 600;
  color: #333;
}

.intro-title i {
  font-size: 18px;
  color: #e54d42;
}

.intro-content {
  color: #666;
  line-height: 1.6;
  padding: 12px;
  background-color: #fafafa;
  border-radius: 6px;
}
</style>