<template>
  <div class="category-page">
    <!-- 搜索区域 -->
    <div class="search-container">
      <el-input 
        v-model="searchQuery" 
        placeholder="搜索商品" 
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
          重置
        </el-button>
      </div>
    </div>

    <!-- 分类选项卡 -->
    <div class="category-tabs-container">
      <el-tabs 
        v-model="activeCategory" 
        class="category-tabs"
        @tab-click="handleTabClick"
      >
        <el-tab-pane label="全部" name="all"></el-tab-pane>
         <el-tab-pane v-for="category in categorylist" :key="category.id" :label="category.name" :name="category.id.toString()"></el-tab-pane>
      </el-tabs>
    </div>

    <!-- 菜品内容区域 -->
    <div class="content"> 
      <div class="list-header">所有菜品</div>
      
      <!-- 菜品列表（网格布局：自适应列数） -->
      <div class="dish-grid" v-if="dishList.length>0" >
        <div 
        v-for="(dish, idx) in dishList" 
        :key="dish.id" 
        class="dish-card" 
        @click="goDishDetail(dish.id)"
        >
        <div class="dish-rank">{{idx+1}}</div>
        <div class="dish-img-container">
          <img 
            :src="'http://localhost:8080/imeageserver/'+dish.image" 
            alt="菜品图片" 
            class="dish-img"
            @error="handleImgError"
          >
        </div>
        <div class="dish-info">
          <div class="dish-name">{{ dish.name }}</div>
          <div class="dish-category">{{ dish.categoryname }}</div>
          <div class="dish-price">¥{{ dish.price.toFixed(2) }}</div>
        </div>
      </div>
      </div>
      
      <!-- 空数据提示 -->
      <div class="empty-state" v-else>
        <el-empty description="暂无菜品数据"></el-empty>
      </div>
    </div>

    <!-- 分页（同步主题色） -->
    <div class="pagination-container">
      <el-pagination 
        layout="prev, pager, next" 
        :total="total" 
        :page-size="8"
        @current-change="handchange"
        class="theme-pagination"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import selectbyloginbefore from '../../../api/selectbyloginbefore.js'

// 分页配置
const pageSize = ref(8) // 每页8条数据

let searchQuery=ref();
// 分类选项卡激活状态
const activeCategory = ref('all')

// 路由实例
const router = useRouter()

// 菜品列表数据
let dishList = ref([])
const total = ref(0)
const isInputFocus = ref(false)

let categorylist=ref();

// 获取当前板块标题
const getSectionTitle = computed(() => {
  return `${[activeCategory.value]}菜品列表`
})

// 根据分类类型获取中文名称
const getCategoryText = (type) => {
  return [type] || '未知分类'
}

// 页面挂载
onMounted(() => {
  fetchDishList(0)
  selectcategorylist();
})

 // 构建请求参数
    let selectwhere =ref({
      page:1,
      where: [
        {
          column: 'status',
          type: 'eq',
          value: 1
        },
        {
          column: 'stock',
          type: 'gt',
          value: 0
        }
      ]
    })

// 获取菜品列表数据
async function fetchDishList(CategoryId) {
  try {
    // 重置查询条件
    selectwhere.value.where = [
      {
        column: 'status',
        type: 'eq',
        value: 1
      },
      {
        column: 'stock',
        type: 'gt',
        value: 0
      }
    ];
    console.log("当前分类ID:", CategoryId);
    // 添加分类筛选条件
    if(CategoryId && CategoryId != 0){
      selectwhere.value.where.push({
        column:'category_id',
        type:'eq',
        value:CategoryId
      })
    }
    
    // 添加搜索条件
    if(searchQuery.value && searchQuery.value.trim() != ''){
       selectwhere.value.where.push({
        column:'name',
        type:'like',
        value:searchQuery.value.trim()
      })
    }
    
    console.log("发送请求参数:", selectwhere.value);
    let res = await selectbyloginbefore.selectDishByCategory(selectwhere.value);
    console.log("菜品列表响应:", res);
    
    // 处理响应数据
    if (!res || !res.data) {
      console.error('API响应为空')
      ElMessage.error('获取菜品数据失败: API响应为空')
      dishList.value = []
      total.value = 0
      return
    }
    
    // 更新分页总数和菜品列表
    if (res.data.pageInfo) {
      total.value = res.data.pageInfo.total || 0
      dishList.value = Array.isArray(res.data.pageInfo.list) ? res.data.pageInfo.list : []
    } else {
      dishList.value = []
      total.value = 0
    }
    
  } catch (error) {
    console.error('获取菜品数据失败:', error)
    ElMessage.error('获取菜品数据失败: ' + error.message)
    dishList.value = []
    total.value = 0
  }
}

// 搜索事件
function onSearch() {
  selectwhere.value.page = 1 // 搜索重置到第一页
  fetchDishList(0)
}

// 重置事件
function resttakeall() {
  searchQuery.value = ''
  selectwhere.value.page = 1
  activeCategory.value = 'all'
  fetchDishList(0)
}

// 分页切换
function handchange(pageNo) {
  selectwhere.value.page = pageNo
  // 获取当前选中的分类ID
  let categoryId = 0;
  if (activeCategory.value !== 'all') {
    categoryId = parseInt(activeCategory.value);
  }
  fetchDishList(categoryId)
}

// Tab点击事件处理
function handleTabClick(tab) {
  console.log("点击的tab:", tab.props.name);
  let categoryId = 0;
  if (tab.props.name !== 'all') {
    categoryId = parseInt(tab.props.name);
  }
  selectwhere.value.page = 1; // 切换分类重置到第一页
  fetchDishList(categoryId);
}

// 分类标签切换（保留兼容性）
function handleCategoryChange(tab) {
  handleTabClick(tab);
}

// 跳转菜品详情页
function goDishDetail(dishId) {
  // 确保productId是有效的数字
  const id = Number(dishId);
  if (isNaN(id)) {
    ElMessage.error('无效的产品ID');
    return;
  }
  router.push({
    name: 'DishDetail',
    params: { id }
  })
}

// 图片加载失败处理
function handleImgError(e) {
  e.target.src = '/default-dish.png' // 默认菜品图片
}
async function selectcategorylist(){
try {
  let res= await selectbyloginbefore.selectCategory({
    page: 1,
    where: []
  });
  console.log("分类列表",res)
  if (res && res.data && res.data.pageInfo) {
    categorylist.value=res.data.pageInfo.list || [];
  } else {
    categorylist.value = [];
    console.warn("分类数据格式异常", res);
  }
} catch (error) {
  console.error("获取分类列表失败:", error);
  categorylist.value = [];
  ElMessage.error("获取分类列表失败");
}
}
// 图片路径处理（添加空值检查）
function getImg(src) {
  if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}
</script>

<style scoped>
/* 页面基础样式 */
.category-page {
  background-color: #fff8f0;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  padding-bottom: 20px;
}

/* 搜索区域样式 */
.search-container {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  max-width: 800px;
  gap: 12px;
  margin: 25px auto;
  padding: 0 20px;
}

.search-input {
  flex: 1;
  height: 50px;
  border: 2px solid #e54d42;
  border-radius: 8px;
  font-size: 15px;
  transition: all 0.3s ease;
}

.search-input::placeholder {
  color: #999;
  font-size: 14px;
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
}

.search-btn:hover {
  background-color: #d43829 !important;
  border-color: #d43829 !important;
}

.reset-btn {
  background-color: #ffd700 !important;
  border-color: #ffd700 !important;
  color: #333 !important;
  border-radius: 8px;
  padding: 0 24px;
}

.reset-btn:hover {
  background-color: #f0c800 !important;
  border-color: #f0c800 !important;
}

/* 分类选项卡样式 */
.category-tabs-container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

:deep(.category-tabs) {
  --el-tabs-header-text-color: #666;
  --el-tabs-active-text-color: #e54d42;
  --el-tabs-underline-color: #ffd700;
  --el-tabs-hover-text-color: #e54d42;
  --el-tabs-active-bar-color: #ffd700;
}

:deep(.category-tabs .el-tabs__nav-wrap) {
  border-bottom: 1px solid #f8d7da;
}

:deep(.category-tabs .el-tabs__item) {
  font-size: 16px;
  padding: 0 20px;
  margin: 0 5px;
}

:deep(.category-tabs .el-tabs__item.is-active) {
  color: #e54d42 !important;
  font-weight: 600;
}

:deep(.category-tabs .el-tabs__active-bar) {
  background-color: #ffd700 !important;
  height: 3px !important;
}

:deep(.category-tabs .el-tabs__item:hover) {
  color: #e54d42 !important;
}

/* 内容区域样式 */
.content {
  padding: 20px;
  flex: 1;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  font-size: 20px;
  font-weight: 600;
  color: #e54d42;
  margin-bottom: 20px;
  padding-left: 8px;
  border-left: 4px solid #ffd700;
}

/* 菜品网格布局：自适应列数，与Product.vue一致 */
.dish-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 24px;
}

/* 菜品卡片样式：与Product.vue保持一致 */
.dish-card {
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
  position: relative;
}

.dish-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(229, 77, 66, 0.15); /* 红色阴影，增强hover质感 */
}

/* 菜品排名：黄色文字，与主题呼应 */
.dish-rank {
  position: absolute;
  top: -10px;
  left: -10px;
  font-size: 26px;
  font-weight: 700;
  color: #ffd700; /* 主题黄色排名 */
  width: 36px;
  height: 36px;
  line-height: 36px;
  text-align: center;
  background-color: #fff8f0; /* 浅黄背景 */
  border-radius: 50%;
  border: 2px solid #fff8f0;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

/* 菜品图片容器：与Product.vue保持一致的样式 */
.dish-img-container {
  width: 100px;
  height: 100px;
  overflow: hidden;
  border-radius: 8px;
  border: 2px solid #fff8f0; /* 浅黄边框，呼应主题 */
}

.dish-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.dish-card:hover .dish-img {
  transform: scale(1.05);
}

/* 菜品信息 */
.dish-info {
  width: 100%;
  text-align: center;
}

.dish-name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.dish-price {
  font-size: 16px;
  font-weight: 700;
  color: #e54d42;
  margin-bottom: 5px;
}

.dish-category {
  font-size: 14px;
  color: #666;
  background-color: #fff8f0;
  padding: 3px 8px;
  border-radius: 12px;
  display: inline-block;
}

/* 空数据状态 */
.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding: 0 20px;
}

:deep(.theme-pagination) {
  --el-pagination-text-color: #666;
  --el-pagination-active-color: #e54d42;
  --el-pagination-hover-color: #e54d42;
}

:deep(.theme-pagination .el-pager li.active) {
  color: #fff !important;
  background-color: #e54d42 !important;
  font-weight: bold;
}

:deep(.theme-pagination .el-pagination__total) {
  color: #666;
  margin-right: 10px;
}

/* 响应式适配 */
@media (max-width: 768px) {
  .dish-grid {
    grid-template-columns: 1fr; /* 移动端一行一条 */
  }
  
  .dish-img-container {
    width: 80px;
    height: 80px;
  }
  
  :deep(.category-tabs .el-tabs__item) {
    font-size: 14px;
    padding: 0 10px;
  }
}
/* 商家卡片：补充主题色点缀 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); /* 自适应列数，适配不同屏幕 */
  gap: 24px;
}

.product-card {
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

.product-card:hover {
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

.product-img {
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

/* 商家标签：红色背景，强化主题 */
.product-tag {
  font-size: 12px;
  color: #fff;
  background-color: #e54d42;
  padding: 2px 8px;
  border-radius: 12px;
  margin-top: 4px;
}

/* 分页：同步主题色 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}

</style>