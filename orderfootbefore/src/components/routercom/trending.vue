<template>
  <!-- 搜索框（主题风格优化） -->
  <div class="search-box">
    <div class="search-container">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索商家" 
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
          @click="resttakeall"
        >
          查询所有
        </el-button>
      </div>
    </div>
  </div>

  <!-- 内容区域（保持原逻辑，同步主题色） -->
  <div class="content">
    <div class="list-header">所有商家</div>
    <div class="shop-list">
      <div 
        v-for="(s, idx) in shoplist" 
        :key="s.id" 
        class="shop-card" 
        @click="goShop(s.id)"
      >
        <!-- 商家头部信息 -->
        <div class="shop-header">
          <div class="shop-logo">
            <img :src="'http://localhost:8080/imeageserver/'+s.logo" class="shop-img" />
          </div>
          <div class="shop-info">
            <div class="shop-name">{{ s.name }}</div>
            <div class="shop-meta">
              <span class="shop-sales">商家已售{{ s.order_cnt || '10000' }}+</span>
              <span class="shop-sales"><el-tag>{{ s.businessScope }}</el-tag></span>
              <span> <el-tag type="info">营业时间:{{ s.businessHours }}</el-tag></span>
            </div>
              热销商品:
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list">
          <div
            v-for="(product, pIdx) in s.topProducts || []"
            :key="pIdx"
            class="product-item"
             @click.stop="goproduct(product.id)"
          >
            <img :src="'http://localhost:8080/imeageserver/'+product.image" class="product-img" />
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-price">
                <span class="price-current">¥{{ product.price }}</span>
              </div>
              <span>销量:{{ product.sales }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>

  <!-- 分页（同步主题色） -->
  <div class="pagination-container">
    <el-pagination 
      layout="prev, pager, next" 
      :total="total" 
      :page-size="10"
      @current-change="handchange"
      class="theme-pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {useRouter} from 'vue-router'
import selectbyloginbefore from '../../../api/selectbyloginbefore'
const searchQuery = ref('')
const shoplist = ref([])
const total = ref(1)
const router = useRouter()
const isInputFocus = ref(false) // 控制输入框聚焦状态样式

// 搜索条件（保持原逻辑）
let selectwhere = ref([{
  "column": "name",
  "type": "like",
  "value": ''
}, {
  "column": "status",
  "type": "notEqual",
  "value": 3
}])
let like = ref({
  page: 1,
  pageSize: 10, // 每页显示8条数据（两行四列）
  where: selectwhere.value
})

// 搜索功能（保持原逻辑，优化提示文案）
function onSearch() {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入商家名称关键词~')
    return
  }
  selectwhere.value[0].value = searchQuery.value
  selectAll()
}

// 跳转商家详情（补充路由逻辑，原代码缺失router）
function goShop(shopId) {
   // 确保productId是有效的数字
  const Id = Number(shopId);
  if (isNaN(Id)) {
    ElMessage.error('无效的商家ID');
    return;
  }
  router.push({
    name: 'ShopGoods',
    params: { Id }
  })
}

// 图片路径处理（保持原逻辑）
function getImg(src) {
  if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}

// 查询所有商家（保持原逻辑）
async function selectAll() {
    let res = await selectbyloginbefore.selectshopbysech(like.value)
    console.log("所有商家数据:", res)
    if (res.code === 200) { // 增加接口状态判断，提升容错
      shoplist.value = res.data.pageinfo.list || []
      total.value = res.data.pageinfo.total || 0
    } else {
      ElMessage.error(res.message || '获取商家列表失败')
    }
}
onMounted(()=>{
  selectAll();
})
// 重置查询（保持原逻辑）
function resttakeall() {
  searchQuery.value = ""
  selectwhere.value[0].value = ""
  selectAll()
  isInputFocus.value = false // 重置输入框聚焦状态
}
// 分页控制（保持原逻辑）
function handchange(pageno) {
  like.value.page = pageno
  selectAll()
}

function goproduct(productId) {
  console.log('点击商品，productId:', productId);
  // 确保productId是有效的数字
  const id = Number(productId);
  if (isNaN(id) || id <= 0) {
    ElMessage.error('无效的产品ID');
    console.error('无效的产品ID:', productId);
    return;
  }
  console.log('准备跳转到商品详情页，商品ID:', id);
  router.push({
    name: 'DishDetail',
    params: { id }
  }).catch(err => {
    console.error('路由跳转失败:', err);
    ElMessage.error('页面跳转失败');
  });
}
</script>

<style scoped>
/* 搜索区域：红色+黄色主题统一 */
.search-box {
  display: flex;
  justify-content: center;
  margin: 25px 0;
  padding: 0 20px;
}

.search-container {
  display: flex;
  align-items: center;
  width: 100%;
  max-width: 800px;
  gap: 12px; /* 按钮与输入框间距，优化布局 */
}

/* 搜索输入框：红色边框+黄色聚焦 */
.search-input {
  flex: 1;
  height: 50px;
  border: 2px solid #e54d42; /* 主题红色边框 */
  border-radius: 8px; /* 圆角与登录页输入框一致 */
  font-size: 15px;
  transition: all 0.3s ease;
  &::placeholder {
    color: #999;
    font-size: 14px;
  }
}

/* 输入框聚焦状态：黄色边框+红色阴影 */
.search-input-focus {
  border-color: #ffd700 !important; /* 主题黄色聚焦 */
  box-shadow: 0 0 0 3px rgba(229, 77, 66, 0.2) !important; /* 红色光晕，呼应登录页 */
}

/* 按钮组：红色搜索+黄色重置，与主题匹配 */
.search-btn-group {
  display: flex;
  gap: 10px;
}

.search-btn {
  background-color: #e54d42 !important; /* 主题红色按钮，与登录页按钮一致 */
  border-color: #e54d42 !important;
  border-radius: 8px;
  padding: 0 24px;
  &:hover {
    background-color: #d43829 !important; /* 深色红hover，增强交互 */
    border-color: #d43829 !important;
  }
}

.reset-btn {
  background-color: #ffd700 !important; /* 主题黄色按钮，突出重置功能 */
  border-color: #ffd700 !important;
  color: #333 !important; /* 深色文字，提升可读性 */
  border-radius: 8px;
  padding: 0 24px;
  &:hover {
    background-color: #f0c800 !important; /* 深色黄hover，保持质感 */
    border-color: #f0c800 !important;
  }
}

/* 内容区域 */
.content {
  padding: 20px;
  background-color: #FFF8F0;
}

.shop-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

/* 商家卡片 */
.shop-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.shop-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

/* 商家头部信息 */
.shop-header {
  display: flex;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.shop-logo {
  margin-right: 15px;
}

.shop-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
}

.shop-info {
  flex: 1;
}

.shop-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.shop-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: #999;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.shop-rating {
  color: #ff9500;
  font-weight: 600;
}

.shop-tags {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  white-space: nowrap;
}

.brand-tag {
  background-color: #fff0e6;
  color: #e54d42;
  border: 1px solid #ffd7cc;
}

.delivery-tag {
  background-color: #e6f7ff;
  color: #1890ff;
  border: 1px solid #b3d8ff;
}

.min-order-tag {
  background-color: #f6ffed;
  color: #52c41a;
  border: 1px solid #b7eb8f;
}

/* 商品列表 */
.product-list {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.product-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  cursor: pointer;
  transition: all 0.2s ease;
}

.product-item:hover {
  transform: translateY(-2px);
}

.product-img {
  width: 100%;
  aspect-ratio: 1;
  object-fit: cover;
  border-radius: 8px;
  margin-bottom: 8px;
}

.product-name {
  font-size: 12px;
  color: #333;
  margin-bottom: 4px;
  line-height: 1.3;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-price {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
}

.price-original {
  font-size: 10px;
  color: #999;
  text-decoration: line-through;
}

.price-current {
  font-size: 14px;
  font-weight: 600;
  color: #e54d42;
}

.price-tag {
  font-size: 10px;
  color: #e54d42;
  background-color: #fff0e6;
  padding: 1px 4px;
  border-radius: 4px;
}



/* 分页 */
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

/* 响应式设计 */
@media (max-width: 768px) {
  .shop-list {
    grid-template-columns: 1fr;
  }

  .product-list {
    grid-template-columns: repeat(2, 1fr);
  }

  .shop-meta {
    gap: 8px;
  }

  .category-tabs {
    gap: 15px;
  }
}

.list-header {
  font-size: 22px;
  font-weight: 600;
  color: #e54d42; /* 主题红色标题 */
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ffd700; /* 主题黄色左边框，与首页一致 */
}
</style>