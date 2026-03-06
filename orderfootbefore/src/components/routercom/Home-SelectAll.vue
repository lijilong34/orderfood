<template>
       <div class="carousel-wrapper">
         <div class="list-header">热门商品</div>
    <el-carousel 
      indicator-position="none"
      autoplay
      interval="3000"
      style="width: 100%;"
      >
      <el-carousel-item v-for="product in producttop5list" :key="product.id" style="width: 100%;">
        <div class="carousel-content">
          <img 
            class="carousel-img" 
            :src="'http://localhost:8080/imeageserver/'+product.image" 
            alt="轮播图"
            @click="goproduct(product.id)"
          >
          <span class="product-name" @click="goproduct(product.id)">{{ product.name }}</span>
        </div>
      </el-carousel-item>
    </el-carousel>
  </div>
  <div class="list-header">🔥 销量前 30 商家</div>
  <div class="shop-grid">
    <div 
      v-for="(s, idx) in shoptop10" 
      :key="s.id" 
      class="shop-card1" 
      @click="goShop(s.id)"
    >
      <div class="rank">{{ idx + 1 }}</div>
      <img :src="'http://localhost:8080/imeageserver/'+s.logo" class="shop-img" />
      <div class="info">
        <div class="name">{{ s.name }}</div>
      </div>
    </div>
  </div>
  <div class="list-header">🔥 销量前 30 商品</div>
  <div class="shop-grid">
      <div 
        v-for="(s, idx) in producttop10" 
        :key="s.id" 
        class="shop-card" 
        @click="goproduct(s.id)"
      >
        <div class="rank">{{ idx + 1 }}</div>
        <img :src="'http://localhost:8080/imeageserver/'+s.image" class="shop-img" />
        <div class="info">
          <div class="name">{{ s.name }}</div>
          <div class="shop-tag">销量:{{ s.sales}}</div>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router' // 补充路由导入（原代码缺失）
import { ElMessage } from 'element-plus'
import selectshopbybefore from '../../../api/selectbyloginbefore.js'
//商品前五列表
let producttop5list=ref();
let producttop10=ref();
const router = useRouter() // 初始化路由（原代码缺失，导致goShop报错）
let shoptop10=ref();
async function selectshoptop10(){
  let res=await selectshopbybefore.selectshopbytop10();
  console.log("加载热门店铺:",res)
  shoptop10.value=res.data.shopList;
}
// 图片路径处理（保持原逻辑）
function getImg(src) {
    if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}
// 页面挂载时查询所有（保持原逻辑）
onMounted(() => {
  selectshoptop10();
  selecttop5list();
  selectproducttop10();
})

async function selecttop5list(){
    let res = await selectshopbybefore.selectproductbytop5();
    producttop5list.value=res.data.productList;
    console.log("前五商品", res);
    console.log("前五商品列表:", producttop5list.value);
    if (producttop5list.value && producttop5list.value.length > 0) {
      console.log("第一个商品ID:", producttop5list.value[0].id);
    }
}
function goShop(shopId){
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
function goproduct(productId) {
  console.log('点击轮播图，产品ID:', productId)
  // 确保productId是有效的数字
  const id = Number(productId);
  if (isNaN(id) || !id) {
    ElMessage.error('无效的产品ID');
    return;
  }
  console.log('准备跳转到菜品详情，ID:', id)
  router.push(`/home/DishDetail/${id}`).then(() => {
    console.log('跳转成功')
  }).catch(err => {
    console.error('跳转失败:', err)
    ElMessage.error('跳转失败，请重试')
  })
}
async function selectproducttop10(){
    let res=await selectshopbybefore.selectproducttop10();
    console.log('查询前10商品',res);
    producttop10.value=res.data.productList;
}
</script>

<style scoped>
/* 穿透scoped样式，覆盖Element Plus轮播组件默认样式 */
:deep(.el-carousel) {
  width: 100% !important;
  max-width: 100% !important;
}

:deep(.el-carousel__container) {
  width: 100% !important;
  max-width: 100% !important;
}

:deep(.el-carousel__item) {
  width: 100% !important;
  max-width: 100% !important;
}

:deep(.el-carousel__item > div) {
  width: 100% !important;
  max-width: 100% !important;
  display: flex !important;
  justify-content: center !important;
  align-items: center !important;
}

/* 轮播图：宽度最大，确保图片完整显示 */
.carousel-wrapper {
  width: 100%; /* 宽度占满整个页面 */
  margin-bottom: 20px; /* 与下方内容的间距 */
  padding: 0; /* 移除内边距 */
}

.carousel-content {
  width: 100%;
  display: flex;
  justify-content: center;
  position: relative;
  cursor: pointer;
}

.product-name {
  position: absolute;
  bottom: 20px;
  left: 20px;
  color: white;
  font-size: 24px;
  font-weight: bold;
  text-shadow: 1px 1px 3px rgba(0,0,0,0.5);
  z-index: 10;
  cursor: pointer;
}

.carousel-wrapper .el-carousel {
  width: 100% !important; /* 确保el-carousel占满宽度 */
}

.carousel-wrapper .el-carousel__container {
  width: 100% !important; /* 确保轮播容器占满宽度 */
}

.carousel-wrapper .el-carousel__item {
  width: 100% !important; /* 确保轮播项占满宽度 */
  display: flex !important; /* 使用flex布局 */
  justify-content: center !important; /* 水平居中 */
}

.carousel-img {
  width: 100%;
  height: auto;
  object-fit: contain;
  object-position: center;
}
/* 列表标题：红色文字+黄色边框，强化主题 */
.list-header {
  font-size: 22px;
  font-weight: 600;
  color: #e54d42; /* 主题红色标题 */
  margin-bottom: 20px;
  padding-left: 12px;
  border-left: 4px solid #ffd700; /* 主题黄色左边框，与首页一致 */
}

/* 商家卡片：补充主题色点缀 */
.shop-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 固定5列 */
  grid-template-rows: repeat(2, auto); /* 固定2行 */
  gap: 24px;
  padding: 0 20px; /* 左右各留20px间距 */
  max-width: 1600px; /* 最大宽度，防止在大屏幕上过宽 */
  margin: 0 auto; /* 居中显示 */
}

.shop-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-top: 3px solid #e54d42; /* 红色顶边，统一卡片标识 */
}

.shop-card .info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
}

/* 上面的销量前 10 商家仿照下面的销量前 10 商品样式 */
.shop-grid:first-of-type .shop-card {
  height: 240px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.shop-grid:first-of-type .shop-card .info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
  width: 100%;
}

.shop-grid:first-of-type .shop-card .info::after {
  content: '';
  height: 24px;
  width: 100px;
  display: block;
  visibility: hidden;
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(229, 77, 66, 0.15); /* 红色阴影，增强hover质感 */
}

/* 排名：黄色文字，与主题呼应 */
.rank {
  font-size: 26px;
  font-weight: 700;
  color: #ffd700; /* 主题黄色排名，醒目且契合风格 */
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background-color: #fff8f0; /* 浅黄背景，提升视觉层次 */
  border-radius: 50%;
}

.shop-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #fff8f0; /* 浅黄边框，呼应主题 */
}

.name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  text-align: center;
}

/* 销量标签样式 */
.shop-tag {
  display: inline-block;
  padding: 4px 12px;
  background-color: #fff8f0;
  color: #e54d42;
  border-radius: 12px;
  font-size: 13px;
  text-align: center;
}

.shop-card1 {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-top: 3px solid #e54d42; /* 红色顶边，统一卡片标识 */
  width: 200px;
}

.shop-card1 .info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
}

/* 上面的销量前 10 商家仿照下面的销量前 10 商品样式 */
.shop-grid:first-of-type .shop-card1 {
  height: 240px;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.shop-grid:first-of-type .shop-card1 .info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  flex: 1;
  justify-content: center;
  width: 100%;
}

.shop-grid:first-of-type .shop-card1 .info::after {
  content: '';
  height: 24px;
  width: 100px;
  display: block;
  visibility: hidden;
}

.shop-card1:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(229, 77, 66, 0.15); /* 红色阴影，增强hover质感 */
}

</style>