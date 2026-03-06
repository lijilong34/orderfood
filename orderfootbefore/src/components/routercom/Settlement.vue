<template>
  <view class="settlement-page">
    <!-- 商品清单卡片 -->
    <el-card shadow="never" class="goods-card">
      <div slot="header" class="card-header">
        <span>【商品清单】</span>
      </div>
      <div class="goods-list">
        <!-- 多商品列表 - 循环渲染 -->
        <div v-for="(item, index) in goodsList" :key="index" class="goods-item">
          <img :src="'http://localhost:8080/imeageserver/'+item.product.image" alt="商品图片" class="cart-item-img" />
          <div class="goods-info">
            <div class="goods-name">{{ item.product.name }}</div>
            <el-input 
              v-model="item.remark" 
              placeholder="请输入菜品备注" 
              class="goods-remark-input"
              maxlength="50"
              show-word-limit
            />
            <div class="goods-price">
              <span>¥{{ item.product.price }}</span>
              <span>x{{ item.quantity }}</span>
              <span class="item-total">小计：¥{{ (item.product.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 订单类型选择卡片 -->
    <el-card shadow="never" class="order-type-card" style="margin-top: 10px;">
      <div slot="header" class="card-header">
        <span>【订单类型】</span>
      </div>
      <el-radio-group v-model="orderType" @change="handleOrderTypeChange">
        <el-radio label="dine-in">堂食</el-radio>
        <el-radio label="takeout">外卖</el-radio>
      </el-radio-group>
      
      <!-- 堂食：选择餐桌 -->
      <div v-if="orderType === 'dine-in'" class="table-select">
        <el-select v-model="selectedTable" placeholder="请选择餐桌" style="width: 100%; margin-top: 10px;">
          <el-option
            v-for="table in tableList"
            :key="table.id"
            :label="`${table.tableNo} (可容纳${table.capacity}人)`"
            :value="table.id"
          />
        </el-select>
      </div>
      
      <!-- 外卖：选择收货地址 -->
      <div v-if="orderType === 'takeout'" class="address-select">
        <div class="address-item" @click="chooseAddress">
          <div v-if="selectedAddress">
            <div class="address-info">
              <span class="address-name">{{ selectedAddress.receiver }}</span>
              <span class="address-phone">{{ selectedAddress.phone }}</span>
            </div>
            <div class="address-detail">{{ selectedAddress.city }} {{ selectedAddress.district }} {{ selectedAddress.detailAddress }}</div>
          </div>
          <div v-else class="address-placeholder">
            请选择收货地址
          </div>
          <el-icon><ArrowRight /></el-icon>
        </div>
      </div>
    </el-card>

    <!-- 优惠券卡片 -->
    <el-card shadow="never" class="coupon-card" style="margin-top: 10px;" v-if="isLogin">
      <div slot="header" class="card-header">
        <span>【优惠券】</span>
      </div>
      <div class="coupon-item" @click="showCouponDialog">
        <div v-if="selectedCoupon">
          <div class="selected-coupon-title">{{ selectedCoupon.title }}</div>
          <div class="selected-coupon-desc">
            <span v-if="selectedCoupon.discountType === 1">满{{ selectedCoupon.minAmount }}减{{ selectedCoupon.discountValue }}</span>
            <span v-else>{{ (selectedCoupon.discountValue * 10).toFixed(1) }}折</span>
          </div>
        </div>
        <span v-else>未选择优惠券</span>
        <el-icon><ArrowRight /></el-icon>
      </div>
    </el-card>

    <!-- 优惠券选择对话框 -->
    <teleport to="body">
      <el-dialog v-model="couponDialogVisible" title="选择优惠券" width="90%" :close-on-click-modal="false">
      <div v-if="couponList.length === 0" class="empty-coupon">
        <el-empty description="暂无可用优惠券" />
      </div>
      <div v-else class="coupon-list">
        <div 
          v-for="coupon in couponList" 
          :key="coupon.id" 
          class="coupon-card-item"
          :class="{ 'selected': tempSelectedCoupon && tempSelectedCoupon.id === coupon.id }"
          @click="selectCoupon(coupon)"
        >
          <div class="coupon-left">
            <div class="coupon-value">
              <span v-if="coupon.discountType === 1">¥{{ coupon.discountValue }}</span>
              <span v-else>{{ (coupon.discountValue * 10).toFixed(1) }}折</span>
            </div>
            <div class="coupon-condition">
              <span v-if="coupon.discountType === 1">满{{ coupon.minAmount }}可用</span>
              <span v-else>无门槛</span>
            </div>
          </div>
          <div class="coupon-right">
            <div class="coupon-title">{{ coupon.title }}</div>
            <div class="coupon-time">{{ formatDate(coupon.startTime) }} - {{ formatDate(coupon.endTime) }}</div>
            <div class="coupon-shop" v-if="coupon.shopId === 0">全系统通用</div>
          </div>
          <div class="coupon-check" v-if="tempSelectedCoupon && tempSelectedCoupon.id === coupon.id">
            <el-icon color="#ff4d4f"><Check /></el-icon>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="cancelSelectCoupon">不使用优惠券</el-button>
                <el-button type="primary" @click="confirmSelectCoupon">确定</el-button>
              </template>
              </el-dialog>
        
        
              <!-- 地址选择对话框 -->
              <el-dialog v-model="addressDialogVisible" title="选择收货地址" width="90%" :close-on-click-modal="false">
      <div v-if="addressList.length === 0" class="empty-address">
        <el-empty description="暂无收货地址" />
      </div>
      <div v-else class="address-list">
        <div 
          v-for="address in addressList" 
          :key="address.id" 
          class="address-card-item"
          :class="{ 'selected': tempSelectedAddress && tempSelectedAddress.id === address.id }"
          @click="selectAddress(address)"
        >
          <div class="address-header">
            <span class="address-receiver">{{ address.receiver }}</span>
            <span class="address-phone">{{ address.phone }}</span>
            <el-tag v-if="address.isDefault === 1" type="danger" size="small">默认</el-tag>
          </div>
          <div class="address-detail">
            {{ address.city }} {{ address.district }} {{ address.detailAddress }}
          </div>
          <div class="address-check" v-if="tempSelectedAddress && tempSelectedAddress.id === address.id">
            <el-icon color="#ff4d4f"><Check /></el-icon>
          </div>
        </div>
      </div>
      <template #footer>
        <el-button @click="cancelSelectAddress">取消</el-button>
        <el-button type="primary" @click="confirmSelectAddress">确定</el-button>
      </template>
      </el-dialog>
    </teleport>

    <!-- 结算信息卡片 -->
    <el-card shadow="never" class="amount-card" style="margin-top: 10px;">
      <div class="order-remark">
        <div class="remark-label">订单备注</div>
        <el-input
          v-model="orderRemark"
          type="textarea"
          :rows="3"
          placeholder="请输入订单备注(选填)"
          maxlength="200"
          show-word-limit
        />
      </div>
      <div class="amount-item">
        <span>商品总价</span>
        <span>¥{{ totalAmount }}</span>
      </div>
      <div class="amount-item" v-if="orderType === 'takeout'">
        <span>运费</span>
        <span>¥{{ freightAmount }}</span>
      </div>
      <div class="amount-item">
        <span>优惠券抵扣</span>
        <span>-¥{{ couponAmount }}</span>
      </div>
      <div class="amount-item total">
        <span>实付金额</span>
        <span>¥{{ payAmount }}</span>
      </div>
    </el-card>

    <!-- 支付方式卡片 -->
    <el-card shadow="never" class="payment-card" style="margin-top: 10px;">
      <div slot="header" class="card-header">
        <span>【支付方式】</span>
      </div>
      <el-radio-group v-model="paymentMethod" class="payment-methods">
        <el-radio label="balance" class="payment-option">
          <div class="payment-option-content">
            <span class="payment-icon">💰</span>
            <span class="payment-name">余额支付</span>
          </div>
        </el-radio>
        <el-radio label="alipay" class="payment-option">
          <div class="payment-option-content">
            <img src="/alipay-logo.svg" class="payment-icon alipay-icon" alt="支付宝">
            <span class="payment-name">支付宝</span>
          </div>
        </el-radio>
      </el-radio-group>
    </el-card>

    <!-- 底部固定提交栏 -->
    <div class="submit-bar">
      <div class="submit-amount">
        实付：<span>¥{{ payAmount }}</span>
      </div>
      <el-button 
        type="primary" 
        class="submit-btn" 
        @click="submitOrder"
        :loading="submitLoading"
      >
        【提交订单】
      </el-button>
    </div>
  </view>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { ArrowRight, Check } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import ShopcartApi from '../../../api/ShopcartApi'
import CouponApi from '../../../api/CouponApi'
import request from '../../../utils/request'
import AddressApi from '../../../api/AddressApi'
import { createOrder } from '../../../api/order'
import OrderStatusApi from '../../../api/OrderStatusApi'

// 路由
const router = useRouter()

// 登录状态
const isLogin = ref(false)

// 加载状态
const submitLoading = ref(false)

// 订单类型相关
const orderType = ref('dine-in')
const selectedTable = ref('')
const selectedAddress = ref(null)
const tempSelectedAddress = ref(null)
const addressDialogVisible = ref(false)

// 餐桌列表
const tableList = ref([])

// 地址列表
const addressList = ref([])

// 商品列表数据（模拟多商品）
const goodsList = ref([
])

// 订单备注
const orderRemark = ref('')

// 支付方式
const paymentMethod = ref('balance')

// 优惠券相关
const couponList = ref([])
const selectedCoupon = ref(null)
const tempSelectedCoupon = ref(null)
const couponDialogVisible = ref(false)

// 金额相关
const totalAmount = ref(0)
const freightAmount = ref(5)
const couponAmount = computed(() => {
  if (!selectedCoupon.value) return 0
  if (selectedCoupon.value.discountType === 1) {
    return parseFloat(selectedCoupon.value.discountValue)
  } else {
    const amount = parseFloat(totalAmount.value) * (1 - selectedCoupon.value.discountValue)
    return parseFloat(amount.toFixed(2))
  }
})
const payAmount = computed(() => {
  const freight = orderType.value === 'takeout' ? freightAmount.value : 0
  return (parseFloat(totalAmount.value) + freight - couponAmount.value).toFixed(2)
})

// 计算商品总价
const calculateTotalAmount = () => {
  totalAmount.value = goodsList.value.reduce((sum, item) => {
    return sum + (item.product.price * item.quantity)
  }, 0).toFixed(2)
}

// 订单类型切换
const handleOrderTypeChange = async (type) => {
  if (type === 'dine-in') {
    selectedAddress.value = null
    await loadAvailableTables()
  } else {
    selectedTable.value = ''
  }
}

// 加载可用餐桌
const loadAvailableTables = async () => {
  try {
    if (goodsList.value.length === 0) {
      console.warn('商品列表为空,无法获取商品ID')
      return
    }
    const firstItem = goodsList.value[0]
    const productId = firstItem?.productId || firstItem?.product?.id || firstItem?.id
    console.log('购物车第一项:', firstItem)
    console.log('加载餐桌,商品ID:', productId)
    
    if (!productId) {
      ElMessage.error('无法获取商品信息')
      return
    }
    
    const params = {
      productId: productId
    }
    const res = await request.get('/table/available', { params })
    tableList.value = res.data.tableList || []
    console.log('加载到的餐桌列表:', tableList.value)
  } catch (error) {
    console.error('加载餐桌失败', error)
    ElMessage.error('加载餐桌失败')
  }
}

// 加载地址列表
const loadAddressList = async () => {
  try {
    const res = await AddressApi.getAddressList()
    addressList.value = res.data.addressList || []
  } catch (error) {
    console.error('加载地址失败', error)
    ElMessage.error('加载地址失败')
  }
}

// 显示地址选择对话框
const chooseAddress = async () => {
  await loadAddressList()
  tempSelectedAddress.value = selectedAddress.value
  addressDialogVisible.value = true
}

// 选择地址
const selectAddress = (address) => {
  tempSelectedAddress.value = address
}

// 确认选择地址
const confirmSelectAddress = () => {
  selectedAddress.value = tempSelectedAddress.value
  addressDialogVisible.value = false
  if (selectedAddress.value) {
    ElMessage.success('地址选择成功')
  }
}

// 取消选择地址
const cancelSelectAddress = () => {
  tempSelectedAddress.value = selectedAddress.value
  addressDialogVisible.value = false
}

// 加载用户已领取的优惠券
const loadAvailableCoupons = async () => {
  try {
    console.log('开始获取用户优惠券...');
    // 获取用户所有已领取的优惠券
    const res = await CouponApi.getUserCoupons()
    console.log('获取用户优惠券响应:', res)
    
    // 检查响应是否成功 - 后端可能返回 code=200 而不是 code=1
    if (res.code === 1 || res.code === 200) {
      // 获取所有用户已领取的优惠券
      const allCoupons = res.data?.couponList || []
      console.log('所有用户优惠券:', allCoupons)
      
      const currentShopId = goodsList.value[0]?.product?.shopId || 0
      console.log('当前店铺ID:', currentShopId)
      
      // 添加一个字段标识优惠券是否可用于当前订单
      const enhancedCoupons = allCoupons.map(coupon => {
        const isUnused = coupon.status === 1
        const isStarted = new Date(coupon.startTime) <= new Date()
        const isNotExpired = new Date(coupon.endTime) >= new Date()
        const isShopMatch = (coupon.shopId === 0 || coupon.shopId === currentShopId)
        
        const isUsable = isUnused && isStarted && isNotExpired && isShopMatch
        
        return {
          ...coupon,
          isUsable: isUsable
        }
      })
      
      // 只显示可用的优惠券
      const usableCoupons = enhancedCoupons.filter(coupon => coupon.isUsable)
      console.log('可用优惠券:', usableCoupons)
      couponList.value = usableCoupons
      
      // 如果没有可用优惠券，也显示提示（但不会覆盖原本的空状态）
      if (usableCoupons.length === 0 && allCoupons.length > 0) {
        console.log('用户有优惠券，但没有适用于当前订单的优惠券')
      }
    } else {
      couponList.value = []
      console.log('获取优惠券失败:', res.message)
      ElMessage.error(res.message || '获取优惠券失败')
    }
  } catch (error) {
    console.error('加载优惠券失败', error)
    ElMessage.error('加载优惠券失败')
    couponList.value = []
  }
}

// 显示优惠券选择对话框
const showCouponDialog = async () => {
  // 检查用户是否已登录
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    router.push('/home/login')
    return
  }
  
  console.log('开始加载优惠券...')
  await loadAvailableCoupons()
  tempSelectedCoupon.value = selectedCoupon.value
  couponDialogVisible.value = true
}

// 选择优惠券
const selectCoupon = (coupon) => {
  tempSelectedCoupon.value = coupon
}

// 确认选择优惠券
const confirmSelectCoupon = () => {
  selectedCoupon.value = tempSelectedCoupon.value
  couponDialogVisible.value = false
  if (selectedCoupon.value) {
    ElMessage.success('优惠券选择成功')
  }
}

// 取消选择优惠券
const cancelSelectCoupon = () => {
  selectedCoupon.value = null
  tempSelectedCoupon.value = null
  couponDialogVisible.value = false
}

// 格式化日期
const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 提交订单
const submitOrder = async () => {
  // 验证订单信息
  if (goodsList.value.length === 0) {
    ElMessage.error('购物车为空')
    return
  }
  
  if (orderType.value === 'dine-in' && !selectedTable.value) {
    ElMessage.error('请选择餐桌')
    return
  }
  
  if (orderType.value === 'takeout' && !selectedAddress.value) {
    ElMessage.error('请选择收货地址')
    return
  }
  
  submitLoading.value = true
  try {
    // 验证商品数据并获取shopId
    if (goodsList.value.length === 0) {
      ElMessage.error('购物车为空')
      return
    }

    const firstItem = goodsList.value[0]
    console.log('第一个商品数据:', firstItem)
    console.log('商品数据完整结构:', JSON.stringify(firstItem, null, 2))

    // 获取shopId - 尝试多个可能的路径
    let shopId = firstItem?.product?.shopId
    if (!shopId) {
      shopId = firstItem?.shopId
    }
    if (!shopId) {
      shopId = firstItem?.productId
    }

    console.log('获取到的shopId:', shopId, '类型:', typeof shopId)
    console.log('product.shopId:', firstItem?.product?.shopId)
    console.log('shopId:', firstItem?.shopId)
    console.log('productId:', firstItem?.productId)

    // 如果还是undefined，尝试从商品列表中查找
    if (!shopId || shopId === 'undefined' || shopId === undefined) {
      // 尝试从后端重新获取商品信息
      ElMessage.error('正在重新加载商品信息...')
      await loadGoodsList()
      const newFirstItem = goodsList.value[0]
      shopId = newFirstItem?.product?.shopId || newFirstItem?.shopId
      console.log('重新加载后的shopId:', shopId)
    }

    // 如果还是获取不到，使用默认值1（你需要根据实际情况修改）
    if (!shopId || shopId === 'undefined' || shopId === undefined || isNaN(Number(shopId))) {
      console.error('无法获取店铺ID，使用默认值1')
      shopId = 1
    }

    shopId = Number(shopId)
    console.log('最终店铺ID:', shopId, '(类型:', typeof shopId, ')')

    // 构建订单明细
    const items = goodsList.value.map(item => {
      const productId = item.productId || item.product?.id || item.id
      const quantity = item.quantity
      const price = item.product?.price || item.price
      const amount = (price * quantity).toFixed(2)

      // 数据验证
      if (!productId) {
        throw new Error('商品ID缺失')
      }
      if (!quantity || quantity <= 0) {
        throw new Error('商品数量无效')
      }
      if (!price || price <= 0) {
        throw new Error('商品价格无效')
      }

      return {
        productId: String(productId),
        quantity: Number(quantity),
        price: String(price),
        amount: String(amount),
        remark: item.remark || ''
      }
    })

    // 构建订单数据
    const orderData = {
      shopId: Number(shopId),
      totalAmount: totalAmount.value,
      payAmount: payAmount.value,
      discountAmount: couponAmount.value,
      payType: paymentMethod.value === 'balance' ? 1 : 2,
      orderType: orderType.value === 'dine-in' ? 1 : 2,
      remark: orderRemark.value,
      items: items
    }

    console.log('提交订单数据:', orderData)
    console.log('shopId类型:', typeof orderData.shopId, '值:', orderData.shopId)
    
    // 堂食订单
    if (orderType.value === 'dine-in') {
      orderData.tableId = selectedTable.value
      orderData.receiver = '堂食'
      orderData.phone = ''
      orderData.address = '堂食'
    } else {
      // 外卖订单
      orderData.receiver = selectedAddress.value.receiver
      orderData.phone = selectedAddress.value.phone
      orderData.address = `${selectedAddress.value.city} ${selectedAddress.value.district} ${selectedAddress.value.detailAddress}`
    }
    
    // 如果使用了优惠券
    if (selectedCoupon.value) {
      orderData.couponId = selectedCoupon.value.id
    }
    
    const res = await createOrder(orderData)
    
    if (res.code === 200) {
      let orderId = res.data.orderId
      let orderNo = res.data.orderNo
      localStorage.setItem("orderId", orderId)
      
      // 根据支付方式处理
      if (paymentMethod.value === 'alipay') {
        // 支付宝支付 - 后端已经返回支付宝HTML表单
        if (res.data && res.data.alipayForm) {
          // 创建新窗口并写入支付宝表单
          const newWindow = window.open('', '_blank')
          newWindow.document.write(res.data.alipayForm)
          newWindow.document.close()
        } else {
          ElMessage.error('支付接口调用失败')
        }
      } else {
        // 余额支付
        // 更新订单状态
        try {
          console.log('余额支付，更新订单状态，orderId:', orderId)
          const statusRes = await OrderStatusApi.updateOrderStatus(orderId)
          if (statusRes.code === 200) {
            console.log('订单状态更新成功')
          } else {
          }
        } catch (error) {
          console.error('更新订单状态失败:', error)
          ElMessage.error('订单状态更新失败: ' + (error.response?.data?.message || error.message))
        }
        
        // 清空购物车
        for (const item of goodsList.value) {
          const cartId = item.id || item.cartId || item.productId || item.product?.id
          if (cartId) {
            console.log('删除购物车商品，ID:', cartId)
            await ShopcartApi.ShopCartDelete(cartId)
          } else {
            console.warn('跳过无效的购物车项:', item)
          }
        }
        
        // 跳转到支付成功页面
        setTimeout(() => {
          router.push({
            path: '/home/PaymentSuccess',
            query: {
              orderNo: orderNo,
              amount: payAmount.value,
              deliveryMethod: orderType.value
            }
          })
        }, 1000)
      }
    } else {
      ElMessage.error(res.message || '订单提交失败')
    }
  } catch (error) {
    console.error('订单提交失败', error)
    ElMessage.error(error.response?.data?.message || '订单提交失败，请重试')
  } finally {
    submitLoading.value = false
  }
}

// 页面初始化
onMounted(() => {
  // 检查用户是否已登录
  const token = localStorage.getItem('token')
  if (token) {
    isLogin.value = true
    console.log('用户已登录，Token存在')
  } else {
    console.log('用户未登录')
    ElMessage.warning('请先登录')
    router.push('/home/login')
    return
  }
  
  loadGoodsList()
  loadAddressList()
  loadAvailableTables()
})

// 加载购物车商品列表
async function loadGoodsList() {
  try {
    // 优先从localStorage读取购物车选中的商品
    const selectedCartItems = localStorage.getItem('selectedCartItems')
    
    if (selectedCartItems) {
      // 使用购物车选中的商品
      goodsList.value = JSON.parse(selectedCartItems)
      console.log('从购物车选中的商品:', goodsList.value)
      
      // 使用后清除localStorage中的数据，避免重复使用
      localStorage.removeItem('selectedCartItems')
    } else {
      // 如果没有选中的商品，则加载所有购物车商品（兼容旧逻辑）
      const res = await ShopcartApi.ShopCartSelect()
      console.log('购物车显示', res)
      goodsList.value = res.data.shoppingCartList || []
      console.log('加载所有购物车商品:', goodsList.value)
    }
    
    calculateTotalAmount()
    
    // 检查商品是否包含店铺信息
    if (goodsList.value.length > 0) {
      const firstItem = goodsList.value[0]
      console.log('第一个商品信息:', firstItem)
      console.log('商品对象结构:', Object.keys(firstItem))
      if (firstItem.product) {
        console.log('商品产品信息:', firstItem.product)
        console.log('产品店铺ID:', firstItem.product.shopId)
      }
    }
    
    if (orderType.value === 'dine-in') {
      await loadAvailableTables()
    }
  } catch (error) {
    console.error('加载购物车失败', error)
    ElMessage.error('加载购物车失败')
  }
}
// 图片路径处理（与首页一致）
function getImg(src) {
 if (!src) return ''
  return new URL(src.replace('@', '/src'), import.meta.url).href
}
</script>

<style scoped>
.settlement-page {
  padding: 10px;
  background-color: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 80px; /* 给底部提交栏留空间 */
}

.card-header {
  font-size: 15px;
  font-weight: 500;
  color: #333;
}

/* 订单类型卡片样式 */
.order-type-card {
  --el-card-padding: 15px;
  --el-card-border-radius: 8px;
}

.table-select {
  margin-top: 10px;
}

.address-select {
  margin-top: 10px;
}

.address-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background-color: #f9f9f9;
  border-radius: 4px;
  cursor: pointer;
}

.address-item:hover {
  background-color: #f0f0f0;
}

.address-info {
  display: flex;
  gap: 15px;
  margin-bottom: 5px;
}

.address-name {
  font-weight: 500;
  font-size: 14px;
}

.address-phone {
  color: #666;
  font-size: 14px;
}

.address-detail {
  font-size: 13px;
  color: #999;
}

.address-placeholder {
  color: #999;
  font-size: 14px;
}

/* 商品清单样式 */
.goods-card {
  --el-card-padding: 15px;
  --el-card-border-radius: 8px;
}

.goods-item {
  display: flex;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}

.goods-item:last-child {
  border-bottom: none;
}

.goods-img {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  margin-right: 10px;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 5px;
}

.goods-spec {
  font-size: 12px;
  color: #999;
  margin-bottom: 5px;
}

.goods-remark-input {
  margin: 8px 0;
  font-size: 12px;
}

.goods-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #ff4d4f;
  font-size: 14px;
}

.item-total {
  font-weight: 600;
  margin-left: auto;
}

/* 结算信息样式 */
.amount-card {
  --el-card-padding: 15px;
  --el-card-border-radius: 8px;
}

.order-remark {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.remark-label {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #333;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  font-size: 14px;
  border-bottom: 1px solid #eee;
}

.amount-item.total {
  font-weight: 600;
  color: #ff4d4f;
  border: none;
  font-size: 15px;
}

/* 优惠券样式 */
.coupon-card {
  --el-card-padding: 15px;
  --el-card-border-radius: 8px;
}

.coupon-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.selected-coupon-title {
  font-weight: 500;
  color: #ff4d4f;
  margin-bottom: 5px;
}

.selected-coupon-desc {
  font-size: 12px;
  color: #999;
}

/* 支付方式样式 */
.payment-card {
  --el-card-padding: 15px;
  --el-card-border-radius: 8px;
}

.payment-methods {
  display: flex;
  flex-direction: row;
  gap: 15px;
  width: 100%;
}

.payment-option {
  flex: 1;
  margin-right: 0;
  height: auto;
}

.payment-option-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  white-space: nowrap;
}

.payment-option.is-checked .payment-option-content {
  color: #409eff;
}

.payment-icon {
  font-size: 24px;
}

.alipay-icon {
  width: 24px;
  height: 24px;
}

.payment-name {
  font-size: 15px;
  font-weight: 500;
}

/* 优惠券对话框样式 */
.empty-coupon {
  padding: 40px 0;
  text-align: center;
}

.coupon-list {
  max-height: 500px;
  overflow-y: auto;
}

.coupon-card-item {
  display: flex;
  align-items: center;
  padding: 15px;
  margin-bottom: 10px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.coupon-card-item:hover {
  border-color: #ff4d4f;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.2);
}

.coupon-card-item.selected {
  border-color: #ff4d4f;
  background-color: #fff5f5;
}

.coupon-left {
  width: 100px;
  text-align: center;
  border-right: 2px dashed #eee;
  padding-right: 15px;
}

.coupon-value {
  font-size: 24px;
  font-weight: bold;
  color: #ff4d4f;
  margin-bottom: 5px;
}

.coupon-condition {
  font-size: 12px;
  color: #999;
}

.coupon-right {
  flex: 1;
  padding-left: 15px;
}

.coupon-title {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 5px;
}

.coupon-time {
  font-size: 12px;
  color: #999;
  margin-bottom: 3px;
}

.coupon-shop {
  font-size: 12px;
  color: #ff4d4f;
}

.coupon-check {
  position: absolute;
  right: 15px;
  top: 15px;
  font-size: 24px;
}

/* 地址选择对话框样式 */
.empty-address {
  padding: 40px 0;
  text-align: center;
}

.address-list {
  max-height: 500px;
  overflow-y: auto;
}

.address-card-item {
  padding: 15px;
  margin-bottom: 10px;
  border: 2px solid #eee;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  position: relative;
}

.address-card-item:hover {
  border-color: #ff4d4f;
  box-shadow: 0 2px 8px rgba(255, 77, 79, 0.2);
}

.address-card-item.selected {
  border-color: #ff4d4f;
  background-color: #fff5f5;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.address-receiver {
  font-size: 16px;
  font-weight: 500;
}

.address-phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 14px;
  color: #999;
  line-height: 1.5;
}

.address-check {
  position: absolute;
  right: 15px;
  top: 15px;
  font-size: 24px;
}

/* 底部提交栏 */
.submit-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 15px;
  background-color: #fff;
  border-top: 1px solid #eee;
  z-index: 99;
}

.submit-amount {
  font-size: 16px;
}

.submit-amount span {
  color: #ff4d4f;
  font-weight: 600;
}

.submit-btn {
  width: 120px;
}
.cart-item-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
}
</style>