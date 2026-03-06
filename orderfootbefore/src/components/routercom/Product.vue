<template>
  <!-- 搜索框（主题风格优化） -->
  <div class="search-box">
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
          查询所有
        </el-button>
      </div>
    </div>
  </div>

  <!-- 内容区域（保持原逻辑，同步主题色） -->
  <div class="content">
    <div class="list-header">所有菜品</div>
    <div class="product-grid">
      <div 
        v-for="(p, idx) in productlist" 
        :key="p.id" 
        class="product-card" 
        @click="goproduct(p.id)"
        >
        <div class="rank">{{ idx + 1 }}</div>
        <img :src="'http://localhost:8080/imeageserver/'+p.image" class="product-img" />
        <div class="info">
          <div class="name">{{ p.name }}</div>
          <div >单价:{{p.price}}元</div>
        </div>
      </div>
    </div>
  </div>

  <!-- 分页（显示，固定显示8条数据） -->
  <div class="pagination-container">
    <el-pagination 
      layout="prev, pager, next" 
      :total="total" 
      :page-size="8"
      @current-change="handchange"
      class="theme-pagination"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import selectbyloginbefore from '../../../api/selectbyloginbefore'
const router = useRouter()
const searchQuery = ref('')
const productlist = ref([])
const total = ref(1)
const isInputFocus = ref(false) // 控制输入框聚焦状态样式

// 搜索条件（保持原逻辑）
let selectwhere = ref([{
  "column": "name",
  "type": "like",
  "value": ''
}])
// 固定分页参数，每页6条
let like = ref({
  page: 1,
  where: selectwhere.value
})

// 搜索功能（保持原逻辑，优化提示文案）
function onSearch() {
  if (!searchQuery.value.trim()) {
    ElMessage.warning('请输入商品名称关键词~')
    return
  }
  selectwhere.value[0].value = searchQuery.value
  selectAll()
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

// 图片路径处理（添加空值检查）
function getImg(src) {
  if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}

// 查询所有菜品（固定每页6条，仅显示上架且库存不为0的数据）
async function selectAll() {
  try {
    // 确保status条件只存在一次（状态为上架）
    const statusConditionIndex = selectwhere.value.findIndex(item => item.column === 'status');
    if (statusConditionIndex !== -1) {
      selectwhere.value.splice(statusConditionIndex, 1);
    }
    selectwhere.value.push({
      column:'status',
      type:'eq',
      value:1      
    })
    
    // 确保stock条件只存在一次（库存不为0）
    const stockConditionIndex = selectwhere.value.findIndex(item => item.column === 'stock');
    if (stockConditionIndex !== -1) {
      selectwhere.value.splice(stockConditionIndex, 1);
    }
    selectwhere.value.push({
      column:'stock',
      type:'gt',
      value:0      
    })
    
    // 固定每页8条数据（两行四列）
    like.value.pageSize = 8;
    like.value.where = selectwhere.value;
    
    let res = await selectbyloginbefore.productSelect(like.value)
    console.log("所有商品数据:", res)
    
    if (res.code === 200) { // 增加接口状态判断，提升容错
      // 直接使用后端返回的数据，不再进行前端过滤
      // 后端应该已经处理好状态过滤和分页逻辑
      productlist.value = res.data.pageInfo.list || [];
      total.value = res.data.pageInfo.total || 0;
      console.log(`当前页显示${productlist.value.length}条数据，总共${total.value}条数据`);
    } else {
      ElMessage.error(res.message || '获取菜品列表失败')
    }
  } catch (error) {
    ElMessage.error('网络异常，请稍后重试')
    console.error("查询菜品异常:", error)
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
// 分页控制（正常切换页码，每页固定6条）
function handchange(pageno) {
  like.value.page = pageno
  selectAll()
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

/* 内容区域：同步主题色细节 */
.content {
  padding: 0 20px 30px;
  flex: 1;
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

/* 商品卡片：两行五列固定布局 */
.product-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 固定5列 */
  grid-template-rows: repeat(2, auto); /* 固定2行 */
  gap: 24px;
  padding: 0 20px; /* 左右各留20px间距 */
  max-width: 1600px; /* 最大宽度，防止在大屏幕上过宽 */
  margin: 0 auto; /* 居中显示 */
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
  color: #e54d42 !important; /* hover红色，增强交互 */
}


</style>