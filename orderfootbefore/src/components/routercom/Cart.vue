<template>
  <div class="cart-wrap">
    <!-- 购物车标题 -->
    <div class="list-header">
      我的购物车
    </div>

    <!-- 购物车空状态 -->
    <div class="empty-cart" v-if="cartList.length === 0">
      <img src="/carousel/dickfood4.png" alt="购物车为空" class="empty-img" />
      <p class="empty-text">购物车空空如也~快去挑选美食吧！</p>
      <el-button class="gold-btn" @click="toProduct">去逛逛</el-button>
    </div>

    <!-- 购物车列表（有商品时） -->
    <div class="cart-list" v-else>
      <!-- 全选 -->
      <div class="select-all">
        <el-checkbox 
          v-model="isAllSelected" 
          label="全选" 
          size="large"
          @change="handleSelectAll"
        ></el-checkbox>
      </div>
      
      <!-- 购物车商品列表 -->
      <div class="cart-item" v-for="(item, index) in cartList" :key="item.id || index">
        <!-- 选择框 -->
        <el-checkbox 
          v-model="item.selected" 
          size="large"
          @change="handleItemSelect(index)"
        ></el-checkbox>
        
        <!-- 商品图片 -->
        <el-image
          :src="'http://localhost:8080/imeageserver/'+item.product.image"
          :preview-src-list="item.product.image ? ['http://localhost:8080/imeageserver/'+item.product.image] : []"
          alt="商品图片"
          class="cart-item-img"
          fit="cover"
          hide-on-click-modal
        />
        
        <!-- 商品信息 -->
        <div class="cart-item-info">
          <h3 class="cart-item-name">{{ item.product.name }}</h3>
          <p class="cart-item-price">单价：¥{{ item.product.price ? item.product.price.toFixed(2) : '0.00' }}</p>
        </div>
        
        <!-- 数量操作 -->
        <div class="cart-item-count">
          <el-input-number 
            v-model="item.quantity" 
            :min="1" 
            :max="10" 
            @change="handleQuantityChange(index)"
            size="small"
            @keydown="e => e.preventDefault()"
          />
        </div>
        
        <!-- 商品小计 -->
        <div class="cart-item-subtotal">
          <span class="subtotal-label">小计：</span>
          <span class="subtotal-price">¥{{ ((item.product.price || 0) * (item.quantity || 0)).toFixed(2) }}</span>
        </div>
        
        <!-- 操作 -->
        <el-button type="text" class="delete-btn" @click="deleteItem(index)">删除</el-button>
      </div>

      <!-- 结算栏 -->
      <div class="cart-footer">
        <div class="selected-info">
          <span>已选 {{ selectedCount }} 件商品</span>
          <div class="total-price">
            合计：¥{{ selectedTotalPrice.toFixed(2) }}
          </div>
        </div>
        <el-button 
          class="gold-btn" 
          @click="checkout"
          :disabled="selectedCount === 0"
        >
          去结算 ({{ selectedCount }})
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import ShopcartApi from '../../../api/ShopcartApi'

const router = useRouter()

// 购物车数据
const cartList = ref([])

// 全选状态
const isAllSelected = ref(false)

// 计算选中商品数量
const selectedCount = computed(() => {
  return cartList.value.filter(item => item.selected).length
})

// 计算选中商品总价
const selectedTotalPrice = computed(() => {
  return cartList.value.reduce((sum, item) => {
    if (item.selected) {
      const price = item.product?.price || 0
      const quantity = item.quantity || 0
      return sum + price * quantity
    }
    return sum
  }, 0)
})

// 图片路径处理（与首页一致）
function getImg(src) {
 if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}

// 防抖函数
function debounce(fn, delay = 800) {
  let timer = null
  return function(...args) {
    if (timer) clearTimeout(timer)
    timer = setTimeout(() => {
      fn.apply(this, args)
    }, delay)
  }
}

// 实际更新数据库的函数
async function updateQuantityToDatabase(item) {
  try {
    const res = await ShopcartApi.ShopCartUpdate({
      id: item.productId,
      quantity: item.quantity
    })
    
    if (res.code === 200) {
      console.log('数量已同步到数据库')
    } else {
      ElMessage.error(res.message || '更新失败')
      await loadshopcart()
    }
  } catch (error) {
    console.error('更新购物车数量失败:', error)
    ElMessage.error('网络错误，请稍后重试')
    await loadshopcart()
  }
}

const debouncedUpdate = debounce(updateQuantityToDatabase, 800)

// 数量修改
function handleQuantityChange(index) {
  const item = cartList.value[index]
  if (item.quantity < 1) {
    item.quantity = 1
  } else if (item.quantity > 10) {
    item.quantity = 10
    ElMessage.warning('商品数量最多为10件')
  }
  
  debouncedUpdate(item)
}

// 删除商品
async function deleteItem(index) {
  try {
    const item = cartList.value[index]
    const res = await ShopcartApi.ShopCartDelete(item.productId)
    
    if (res.code === 200) {
      cartList.value.splice(index, 1)
      ElMessage.success('商品已删除')
    } else {
      ElMessage.error(res.message || '删除失败')
    }
  } catch (error) {
    console.error('删除商品失败:', error)
    ElMessage.error('删除失败，请稍后重试')
  }
}

// 去商品页
function toProduct() {
  router.push('/home/Product')
}

// 全选处理
function handleSelectAll(checked) {
  cartList.value.forEach(item => {
    item.selected = checked
  })
}

// 单个商品选择处理
function handleItemSelect(index) {
  // 更新全选状态
  isAllSelected.value = cartList.value.every(item => item.selected)
}

// 获取选中的商品 - 类似getSelectionRows的功能
function getSelectedRows() {
  return cartList.value.filter(item => item.selected)
}

// 结算
function checkout() {
  // 使用getSelectedRows获取选中的商品
  const selectedRows = getSelectedRows()
  
  if (selectedRows.length === 0) {
    ElMessage.warning('请选择要结算的商品')
    return
  }
  
  // 输出选中商品的详细信息
  console.log('=== 选中的商品信息 ===')
  console.log('选中商品数量:', selectedRows.length)
  console.log('选中商品总价:', selectedTotalPrice.value)
  
  // 准备要传递到结算页面的数据
  const selectedProducts = selectedRows.map(item => ({
    id: item.id, // 购物车项ID
    productId: item.productId || item.product?.id, // 商品ID
    product: {
      id: item.productId || item.product?.id,
      name: item.product.name,
      price: item.product.price,
      image: item.product.image,
      shopId: item.product.shopId
    },
    quantity: item.quantity,
    shopId: item.shopId || item.product?.shopId,
    remark: '' // 结算页面可以添加备注
  }))
  
  selectedProducts.forEach((item, index) => {
    console.log(`商品 ${index + 1}:`)
    console.log('  - 商品ID:', item.productId)
    console.log('  - 商品名称:', item.product.name)
    console.log('  - 商品单价:', item.product.price)
    console.log('  - 商品数量:', item.quantity)
    console.log('  - 商品小计:', item.product.price * item.quantity)
  })

 let res=ShopcartApi.addShopCartfor();

  // 将选中的商品存储到localStorage，传递到结算页面
  localStorage.setItem('selectedCartItems', JSON.stringify(selectedProducts))
  console.log('已存储到localStorage:', selectedProducts)
  
  // 跳转到结算页面
  router.push('/home/Settlement')
}

onMounted(() => {
  loadshopcart()
  
  // 监听路由变化，当从结算页面返回时刷新购物车
  const unwatch = router.afterEach((to, from) => {
    if (from.path === '/home/Settlement' && to.path === '/home/Cart') {
      console.log('从结算页面返回，刷新购物车')
      loadshopcart()
    }
  })
  
  // 组件卸载时取消监听
  onUnmounted(() => {
    unwatch()
  })
})

async function loadshopcart() {
  try {
    let res = await ShopcartApi.ShopCartSelect()
    console.log('购物车列表', res)
    
    // 初始化购物车数据，添加selected字段
    cartList.value = (res.data.shoppingCartList || []).map(item => ({
      ...item,
      selected: false // 默认未选中
    }))
    
    // 重置全选状态
    isAllSelected.value = false
  } catch (error) {
    console.error('加载购物车失败:', error)
    ElMessage.error('加载购物车失败')
  }
}
</script>

<style scoped>
/* 购物车容器 */
.cart-wrap {
  padding: 0 20px 30px;
  flex: 1;
}

/* 标题（与首页一致） */
.list-header {
  font-size: 22px;
  font-weight: 600;
  color: #e54d42;
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #ffd700;
}

/* 空购物车 */
.empty-cart {
  text-align: center;
  padding: 50px 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

.empty-img {
  width: 150px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-text {
  font-size: 16px;
  color: #666;
  margin-bottom: 20px;
}

/* 购物车列表 */
.cart-list {
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
}

/* 全选区域 */
.select-all {
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
  background-color: #f9f9f9;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

/* 购物车项 */
.cart-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #f5f5f5;
  transition: background-color 0.3s;
}

.cart-item:hover {
  background-color: #f9f9f9;
}

.cart-item-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
}

.cart-item-info {
  flex: 1;
}

.cart-item-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.cart-item-price {
  font-size: 14px;
  color: #e54d42;
  font-weight: 600;
}

/* 数量操作 */
.cart-item-count {
  display: flex;
  align-items: center;
  margin: 0 20px;
}

.count-num {
  min-width: 30px;
  text-align: center;
}

/* 商品小计 */
.cart-item-subtotal {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  margin-left: auto;
  margin-right: 20px;
}

.subtotal-label {
  font-size: 12px;
  color: #999;
  margin-bottom: 4px;
}

.subtotal-price {
  font-size: 18px;
  font-weight: bold;
  color: #e54d42;
}

/* 删除按钮 */
.delete-btn {
  color: #e54d42 !important;
}

/* 结算栏 */
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  background: #fff8f0;
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

.total-price {
  font-size: 18px;
  font-weight: 600;
  color: #e54d42;
}

/* 金色按钮（与首页一致） */
.gold-btn {
  background-color: #ffd700;
  border-color: #ffd700;
  color: #333;
  font-size: 16px;
  padding: 8px 20px;
  border-radius: 4px;
  transition: all 0.3s ease;
}
.gold-btn:hover {
  background-color: #f0c800;
  border-color: #f0c800;
  color: #000;
  transform: translateY(-2px);
}
</style>