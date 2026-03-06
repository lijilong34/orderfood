<template>
  <div class="detail-page">
    <!-- 带返回的标题栏 -->
    <el-page-header @back="goBack" content="商品详情" class="page-header" />

    <!-- 详情内容区 -->
    <div class="detail-content">
      <!-- 菜品大图 -->
      <el-image
        :src="'http://localhost:8080/imeageserver/'+dish.image"
        :preview-src-list="dish.image ? ['http://localhost:8080/imeageserver/'+dish.image] : []"
        class="detail-img"
        fit="cover"
        hide-on-click-modal
      />
      <!-- 菜品信息 -->
      <div class="detail-info">
        <div class="detail-name">{{ dish.name || '未命名' }}</div>
        <div class="detail-price">¥{{ dish.price ? dish.price.toFixed(2) : '0.00' }}</div>
        <div >所属店铺:{{dish.shopname || '未知店铺'}}</div>
        <div class="detail-spec">分类：{{ dish.categoryname || '未分类' }}</div>
        <div class="detail-desc">描述：{{ dish.description || '暂无描述' }}</div>
        <div class="detail-stock">库存：{{ dish.stock || 0 }}份</div>
        <div class="detail-stock">销量:{{ dish.sales }}份</div>
        <div class="button-group">
          <el-button 
            type="primary" 
            style="background-color: #E63946; border: none; width: 120px;"
            @click="addToCart" v-show="isLogin"
          >
            加入购物车
          </el-button>
          <el-button 
            :type="isFavorited ? 'danger' : 'default'"
            :style="isFavorited ? 
              'background-color: #E63946; border: none; width: 120px;' : 
              'border: 1px solid #E63946; color: #E63946; background-color: white; width: 120px;'"
            @click="toggleFavorite" v-show="isLogin"
          >
            {{ isFavorited ? '已收藏 ❤️' : '收藏 ⭐' }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 商品详情大图 -->
    <div class="detail-images-section" v-if="dish.imagePath1 || dish.imagePath2 || dish.imagePath3">
      <div class="detail-images-header">
        <h3>商品详情</h3>
      </div>
      <div class="detail-images-list">
        <el-image
          v-if="dish.imagePath1"
          :src="'http://localhost:8080/imeageserver/'+dish.imagePath1"
          class="detail-image-item"
          fit="cover"
          :preview-src-list="[dish.imagePath1, dish.imagePath2, dish.imagePath3].filter(Boolean).map(img => 'http://localhost:8080/imeageserver/'+img)"
          hide-on-click-modal
        />
        <el-image
          v-if="dish.imagePath2"
          :src="'http://localhost:8080/imeageserver/'+dish.imagePath2"
          class="detail-image-item"
          fit="cover"
          :preview-src-list="[dish.imagePath1, dish.imagePath2, dish.imagePath3].filter(Boolean).map(img => 'http://localhost:8080/imeageserver/'+img)"
          hide-on-click-modal
        />
        <el-image
          v-if="dish.imagePath3"
          :src="'http://localhost:8080/imeageserver/'+dish.imagePath3"
          class="detail-image-item"
          fit="cover"
          :preview-src-list="[dish.imagePath1, dish.imagePath2, dish.imagePath3].filter(Boolean).map(img => 'http://localhost:8080/imeageserver/'+img)"
          hide-on-click-modal
        />
      </div>
    </div>

    <!-- 推荐商品 -->
    <div class="recommend-section">
      <div class="recommend-header">
        <h3>推荐商品</h3>
      </div>

      <!-- 推荐商品列表 -->
      <div class="recommend-list" v-if="recommendProducts.length > 0">
        <div class="recommend-item" v-for="product in recommendProducts" :key="product.id" @click="goToProductDetail(product.id)">
          <el-image
            :src="'http://localhost:8080/imeageserver/'+product.image"
            class="recommend-img"
            fit="cover"
          />
          <div class="recommend-item-info">
            <div class="recommend-item-name">{{ product.name }}</div>
            <div class="recommend-item-price">¥{{ product.price ? product.price.toFixed(2) : '0.00' }}</div>
            <div class="recommend-item-sales">销量: {{ product.sales }}份</div>
          </div>
        </div>
      </div>
      <div class="empty-recommend" v-else>
        <p>暂无推荐商品</p>
      </div>
    </div>

    <!-- 评论区 -->
    <div class="comment-section">
      <div class="comment-header">
        <h3>用户评价</h3>
        <span class="comment-count">共 {{ comments.length }} 条评论</span>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list">
        <div class="comment-item" v-for="comment in comments" :key="comment.id">
          <div class="comment-avatar">
            <el-avatar :size="40"><img :src="'http://localhost:8080/imeageserver/'+comment.avatar"/></el-avatar>
          </div>
          <div class="comment-content">
            <div class="comment-user">
              <span class="username">{{ comment.nickname || '匿名用户' }}</span>
              <span class="avg-score-badge">综合 {{ comment.avgScore }}分</span>
            </div>
            <div class="comment-scores">
              <span class="score-item">菜品: <el-rate v-model="comment.dishScore" disabled size="small" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" /></span>
              <span class="score-item">服务: <el-rate v-model="comment.serviceScore" disabled size="small" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" /></span>
              <span class="score-item">环境: <el-rate v-model="comment.environmentScore" disabled size="small" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" /></span>
            </div>
            <div class="comment-text">{{ comment.content }}</div>
            <div class="comment-images" v-if="comment.parsedImageUrls && comment.parsedImageUrls.length > 0">
              <div
                v-for="(img, idx) in comment.parsedImageUrls"
                :key="idx"
                class="comment-img-container"
              >
                <el-image
                  :src="img"
                  :preview-src-list="comment.parsedImageUrls || []"
                  class="comment-img"
                  fit="cover"
                  hide-on-click-modal
                  preview-teleported
                  @error="(e) => handleImageError(e, idx)"
                  v-if="!imageErrors[idx]"
                />
              </div>
            </div>
            <div class="comment-meta">
              <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
            </div>
          </div>
        </div>
        <div class="empty-comments" v-if="comments.length === 0">
          <p>暂无评论，快来抢沙发吧~</p>
        </div>
      </div>
    </div>

    <!-- 购物车确认对话框 -->
    <teleport to="body">
      <el-dialog 
        v-model="showCartDialog" 
        title="确认添加到购物车" 
        width="400px"
      >
        <div class="cart-confirm-section">
          <div class="confirm-item">
            <span class="confirm-label">菜品名称:</span>
            <span class="confirm-value">{{ dish.name }}</span>
          </div>
          <div class="confirm-item">
            <span class="confirm-label">数量:</span>
            <el-input-number v-model="cartQuantity" :min="1" :max="10" size="small" @keydown="e => e.preventDefault()"/>
          </div>
          <div class="confirm-item">
            <span class="confirm-label">总金额:</span>
            <span class="confirm-amount">¥{{ totalAmount }}</span>
          </div>
          <div style="text-align: right; margin-top: 20px;">
            <el-button @click="showCartDialog = false">取消</el-button>
            <el-button 
              type="primary" 
              style="background-color: #E63946; border: none;"
              @click="confirmAddToCart"
            >
              确认添加
            </el-button>
          </div>
        </div>
      </el-dialog>
    </teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import BrowseHistoryApi from '../../../api/BrowseHistoryApi'
import selectbyloginbefore from '../../../api/selectbyloginbefore'
import ShopcartApi from '../../../api/ShopcartApi'
import FavoriteApi from '../../../api/FavoriteApi'

// 初始化路由和路由参数
const route = useRoute()
const router = useRouter()
let isLogin=ref(false);
  const token = localStorage.getItem('token')

const dish = ref({})
const showCartDialog = ref(false)
const cartQuantity = ref(1)
const isFavorited = ref(false)
const favoriteId = ref(null)
const comments = ref([])
const imageErrors = ref({}) // 跟踪图片加载失败状态
const recommendProducts = ref([]) // 推荐商品列表


const totalAmount = computed(() => {
  return ((dish.value.price || 0) * cartQuantity.value).toFixed(2)
})

onMounted(async () => {
 await selectProductinfo();
 if(token!=null){
   checkFavoriteStatus();
   addhistory();
 }
 loadComments();
})

// 监听路由参数变化，当商品ID变化时重新加载数据
watch(() => route.params.id, (newId, oldId) => {
  console.log('路由参数变化，新ID:', newId, '旧ID:', oldId);
  if (newId && newId !== oldId) {
    // 重置数据
    dish.value = {};
    recommendProducts.value = [];
    comments.value = [];
    // 重新加载数据
    selectProductinfo();
    if(token!=null){
      checkFavoriteStatus();
      addhistory();
    }
    loadComments();
  }
})

const goBack = () => {
  router.back()
}

const goToProductDetail = (productId) => {
  console.log('推荐商品点击事件触发，产品ID:', productId);
  const id = Number(productId);
  console.log('转换后的产品ID:', id);
  if (isNaN(id)) {
    ElMessage.error('无效的产品ID');
    return;
  }
  const targetPath = `/home/DishDetail/${id}`;
  console.log('准备跳转到路径:', targetPath);
  // 使用路径跳转而不是命名路由，确保路由跳转正确
  router.push(targetPath);
  console.log('路由跳转已执行');
}

const addToCart = () => {
  // 检查用户是否登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('请先登录后再加入购物车')
    return
  }
  
  if (!dish.value.id) {
    ElMessage.error('商品信息加载失败')
    return
  }
  
  if (!dish.value.stock || dish.value.stock <= 0) {
    ElMessage.error('商品库存不足')
    return
  }

  cartQuantity.value = 1
  showCartDialog.value = true
}

const confirmAddToCart = async () => {
  try {
    const cartData = {
      productId: dish.value.id,
      shopId: dish.value.shopid,
      quantity: cartQuantity.value
    }
    
    if (dish.value.stock < cartQuantity.value) {
      ElMessage.error('商品库存不足')
      return
    }

    const res = await ShopcartApi.ShopCartAdd(cartData)
    
    if (res.code === 200) {
      ElMessage.success(`${dish.value.name} × ${cartQuantity.value} 已加入购物车 ✨`)
      showCartDialog.value = false
    } else {
      ElMessage.error(res.message || '加入购物车失败')
    }
  } catch (error) {
    console.error('加入购物车异常:', error)
    ElMessage.error('加入购物车失败,请稍后重试')
  }
}

// 检查收藏状态
const checkFavoriteStatus = async () => {
  if (!dish.value.id) {
    console.log('商品ID未加载，跳过收藏状态检查')
    return
  }
  
  try {
    const res = await FavoriteApi.FavoriteCheck(dish.value.id)
    if (res.code === 200 && res.data.userFavorites!=null) {
      isFavorited.value = true
      favoriteId.value = res.data.userFavorites.id
    }
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

// 切换收藏状态
const toggleFavorite = async () => {


  try {
    if (isFavorited.value) {
      // 取消收藏
      const res = await FavoriteApi.FavoriteDelete(favoriteId.value)
      if (res.code === 200) {
        isFavorited.value = false
        favoriteId.value = null
        ElMessage.success('已取消收藏')
      } else {
        ElMessage.error(res.message || '取消收藏失败')
      }
    } else {
      // 添加收藏
      const res = await FavoriteApi.FavoriteAdd(dish.value.id)
      if (res.code === 200) {
        isFavorited.value = true;
        ElMessage.success('收藏成功 ❤️')
         checkFavoriteStatus();
      } else {
        ElMessage.error(res.message || '收藏失败')
      }
    }
  } catch (error) {
    console.error('收藏操作失败:', error)
    ElMessage.error('操作失败,请稍后重试')
  }
}

// 加载评论列表 - 根据菜品ID查询评价
// 查询逻辑：evaluation表 -> order_id -> order_item表 -> product_id匹配当前菜品
const loadComments = async () => {
  const dishId = Number(route.params.id)
  if (isNaN(dishId)) return

  try {
    const res = await selectbyloginbefore.getEvaluationsByProductId(dishId)
    console.log("评论列表",res);
    if (res.code === 200) {
      comments.value = (res.data.evaluationList || []).map(comment => ({
        ...comment,
        parsedImageUrls: parseImageUrls(comment.imageUrls),
        // 计算综合评分：(菜品+服务+环境)/3
        avgScore: comment.dishScore && comment.serviceScore && comment.environmentScore
          ? ((comment.dishScore + comment.serviceScore + comment.environmentScore) / 3).toFixed(1)
          : '0.0'
      }))
    } else {
      comments.value = []
    }
  } catch (error) {
    console.error('加载评论失败:', error)
    comments.value = []
  }
}

// 解析图片URL字符串（数据库中存储的是逗号分隔的URL字符串）
const parseImageUrls = (imageUrls) => {
  if (!imageUrls) return []
  if (typeof imageUrls === 'string') {
    return imageUrls.split(',').filter(url => url.trim()).map(url => 'http://localhost:8080/imeageserver/' + url)
  }
  return imageUrls
}

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)
  
  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  
  return date.toLocaleDateString('zh-CN')
}

async function selectProductinfo() {
  if(token!=null){
    isLogin.value=true;
  }
  const dishId = Number(route.params.id) // 获取路由中的菜品ID
  // 确保dishId是有效的数字
  if (isNaN(dishId)) {
    ElMessage.error('无效的菜品ID');
    return;
  }
  try {
    // 实际接口请求
    let res = await selectbyloginbefore.selectproductinfobyproductid(dishId);
    console.log('商品对象', res);
    if (res.code === 200) {
      // 根据API返回的数据结构调整
      dish.value = res.data.product || res.data.entity || {};
      // 只有登录用户才检查收藏状态
      if (isLogin.value) {
        checkFavoriteStatus();
      }
      // 加载推荐商品
      if (dish.value.shopId) {
        await loadRecommendProducts(dish.value.shopId, dishId);
      }
    } else {
      ElMessage.error(res.message || '获取商品详情失败');
    }
  } catch (error) {
    ElMessage.error('获取商品详情失败，请稍后重试');
    console.error('获取商品详情异常:', error);
  }
}

// 加载推荐商品 - 相同店铺内销量前5的商品，去除当前商品
const loadRecommendProducts = async (shopId, currentProductId) => {
  try {
    // 实际接口请求
    let res = await selectbyloginbefore.selectProductByShopId(shopId);
    console.log('店铺商品列表', res);
    if (res.code === 200 && res.data.productList) {
      // 过滤掉当前商品，按销量排序，取前5个
      recommendProducts.value = res.data.productList
        .filter(product => product.id !== currentProductId)
        .sort((a, b) => (b.sales || 0) - (a.sales || 0))
        .slice(0, 5);
    } else {
      recommendProducts.value = [];
    }
  } catch (error) {
    console.error('加载推荐商品失败:', error);
    recommendProducts.value = [];
  }
}
// 处理图片加载失败
function handleImageError(event, index) {
  // 标记该图片索引为加载失败状态
  imageErrors.value[index] = true
}

// 图片路径处理（保持原逻辑，添加空值检查）
function getImg(src) {
  if (!src) {
    // 如果src为空，返回一个默认图片或空字符串
    return '';
  }
  return new URL(src.replace('@', '/src'), import.meta.url).href
}
async function addhistory(){
   const dishId = Number(route.params.id) // 获取路由中的菜品ID
  // 确保dishId是有效的数字
  if (isNaN(dishId)) {
    ElMessage.error('无效的菜品ID');
    return;
  }
  let res=await  BrowseHistoryApi.addhistory(dishId);
}
</script>

<style scoped>
.detail-page {
  padding: 20px;
  background-color: #FFF5E6;
  min-height: calc(100vh - 64px);
}

/* 标题栏 */
.page-header {
  margin-bottom: 20px;
}

/* 详情内容 */
.detail-content {
  display: flex;
  gap: 30px;
  align-items: flex-start;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  border: 1px solid #E63946;
}

/* 详情大图 */
.detail-img {
  width: 300px;
  height: 240px;
  border-radius: 4px;
  cursor: zoom-in;
}

/* 菜品信息 */
.detail-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.detail-name {
  font-size: 22px;
  font-weight: bold;
  color: #E63946;
}

.detail-price {
  font-size: 20px;
  color: #FF5722;
  font-weight: 600;
}

.detail-spec, .detail-desc, .detail-stock {
  font-size: 16px;
  color: #666;
  line-height: 1.5;
}

/* 按钮组 */
.button-group {
  margin-top: 40px;
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .detail-content {
    flex-direction: column;
    gap: 20px;
  }
  .detail-img {
    width: 100%;
    height: auto;
  }
  .button-group {
    flex-direction: column;
  }
}

.cart-dialog-content {
  padding: 20px 0;
}

.dialog-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.dialog-label {
  font-size: 15px;
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.dialog-value {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.total-amount {
  font-size: 20px;
  color: #E63946;
  font-weight: bold;
}

.cart-confirm-section {
  margin-top: 20px;
  padding: 15px;
  background-color: #FFF5E6;
  border-radius: 4px;
  border: 1px solid #E63946;
}

.confirm-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.confirm-label {
  font-size: 15px;
  color: #666;
  width: 80px;
  flex-shrink: 0;
}

.confirm-value {
  font-size: 15px;
  color: #333;
  font-weight: 500;
}

.confirm-amount {
  font-size: 18px;
  color: #E63946;
  font-weight: bold;
}

/* 推荐商品 */
.recommend-section {
  margin-top: 30px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  border: 1px solid #E63946;
}

/* 商品详情大图 */
.detail-images-section {
  margin-top: 30px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  border: 1px solid #E63946;
}

.detail-images-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #E63946;
}

.detail-images-header h3 {
  font-size: 20px;
  color: #E63946;
  margin: 0;
}

.detail-images-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-image-item {
  width: 100%;
  height: 400px;
  border-radius: 4px;
  cursor: pointer;
}

/* 移动端适配 */
@media (max-width: 768px) {
  .detail-image-item {
    height: 250px;
  }
}

.recommend-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #E63946;
}

.recommend-header h3 {
  font-size: 20px;
  color: #E63946;
  margin: 0;
}

.recommend-subtitle {
  font-size: 14px;
  color: #666;
}

.recommend-list {
  display: flex;
  gap: 20px;
  overflow-x: auto;
  padding-bottom: 10px;
  flex-wrap: wrap;
}

.recommend-item {
  flex: 1;
  min-width: 200px;
  max-width: 250px;
  background-color: #FAFAFA;
  border-radius: 4px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #E63946;
}

.recommend-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(230, 57, 70, 0.2);
}

.recommend-img {
  width: 100%;
  height: 150px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.recommend-item-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.recommend-item-name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.recommend-item-price {
  font-size: 18px;
  color: #E63946;
  font-weight: bold;
}

.recommend-item-sales {
  font-size: 14px;
  color: #666;
}

.empty-recommend {
  text-align: center;
  padding: 50px;
  color: #999;
}

.empty-recommend p {
  margin: 0;
  font-size: 14px;
}

/* 评论区 */
.comment-section {
  margin-top: 30px;
  background-color: #fff;
  padding: 20px;
  border-radius: 4px;
  border: 1px solid #E63946;
  height: 483px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid #E63946;
}

.comment-header h3 {
  font-size: 20px;
  color: #E63946;
  margin: 0;
}

.comment-count {
  font-size: 14px;
  color: #666;
}



/* 评论列表 */
.comment-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 5px;
}

/* 自定义滚动条样式 */
.comment-list::-webkit-scrollbar {
  width: 6px;
}

.comment-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.comment-list::-webkit-scrollbar-thumb {
  background: #E63946;
  border-radius: 3px;
}

.comment-list::-webkit-scrollbar-thumb:hover {
  background: #c52e3b;
}

.comment-item {
  display: flex;
  gap: 15px;
  padding: 15px;
  background-color: #FAFAFA;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.comment-item:hover {
  background-color: #FFF5E6;
}

.comment-avatar {
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.username {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.avg-score-badge {
  background-color: #E63946;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-left: 10px;
}

.comment-scores {
  display: flex;
  gap: 15px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 13px;
  color: #666;
}

.comment-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin-bottom: 8px;
}

.comment-images {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.comment-img-container {
  /* 容器样式，确保在没有图片时不会占用空间 */
}

.comment-img {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
  transition: transform 0.3s;
}

.comment-img:hover {
  transform: scale(1.05);
}

.comment-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.comment-time {
  font-size: 12px;
  color: #999;
}

.empty-comments {
  text-align: center;
  padding: 200px;
  color: #999;
}

.empty-comments p {
  margin: 0;
  font-size: 14px;
}
</style>