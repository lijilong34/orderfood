<template>
  <div class="payment-success-container">
      <el-card class="success-card" shadow="hover">
        <div class="success-content">
          <!-- 成功图标 -->
          <div class="success-icon">
            <el-icon size="80" color="#67C23A">
              <CircleCheck />
            </el-icon>
          </div>
          
          <!-- 成功消息 -->
          <h1 class="success-title">支付成功</h1>
          <p class="success-message">您的支付已成功完成！</p>
          
          <!-- 订单状态更新提示 -->
          <div class="status-update-info" v-if="!orderInfo">
            <el-alert
              title="正在更新订单状态..."
              type="info"
              :closable="false"
              show-icon
            >
              <template #description>
                <div class="status-description">
                  <p>系统正在处理您的支付信息，请稍候...</p>
                  <el-progress :percentage="statusProgress" :stroke-width="8" :show-text="false" />
                  <p class="status-tip">如果长时间未显示订单信息，请刷新页面或联系客服</p>
                </div>
              </template>
            </el-alert>
          </div>
          
          <!-- 订单信息 -->
          <div class="order-info" v-if="orderInfo">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="订单号">{{ orderInfo.orderNo }}</el-descriptions-item>
              <el-descriptions-item label="支付金额">¥{{ orderInfo.payAmount }}</el-descriptions-item>
              <el-descriptions-item label="支付时间">{{ orderInfo.payTime}}</el-descriptions-item>
              <el-descriptions-item label="订单类型">{{ orderInfo.orderType==1? '堂食' : '外卖' }}</el-descriptions-item>
              <el-descriptions-item label="订单状态">
                <el-tag :type="getStatusTagType(orderInfo.status)">
                  {{ getStatusText(orderInfo.status) }}
                </el-tag>
              </el-descriptions-item>
            </el-descriptions>
          </div>
          
          <!-- 操作按钮 -->
          <div class="action-buttons">
            <el-button type="primary" size="large" @click="goToHome" class="primary-btn">
              <el-icon><House /></el-icon>
              返回首页
            </el-button>
          </div>
          
          </div>
      </el-card>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { CircleCheck, House, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import OrderStatusApi from '../../../api/OrderStatusApi'
import OrderApi from '../../../api/OrderApi'
import UserApi from '../../../api/UserApi.js'

const router = useRouter()
const route = useRoute()

// 响应式数据
const orderInfo = ref(null)
const isRecharge = ref(false) // 是否为充值支付
const statusProgress = ref(0) // 状态更新进度
const isUpdatingStatus = ref(false) // 是否正在更新状态

// 获取订单状态文本
const getStatusText = (status) => {
  switch (status) {
    case 0: return '待支付'
    case 1: return '已支付'
    case 2: return '已接单'
    case 3: return '配送中'
    case 4: return '已完成'
    case 5: return '已取消'
    default: return '未知状态'
  }
}

// 获取订单状态标签类型
const getStatusTagType = (status) => {
  switch (status) {
    case 0: return 'warning'
    case 1: return 'success'
    case 2: return 'primary'
    case 3: return 'info'
    case 4: return 'success'
    case 5: return 'danger'
    default: return 'info'
  }
}

// 页面跳转
const goToHome = () => {
  // 检查登录状态
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.warning('登录状态已失效，请重新登录')
    router.push('/login')
    return
  }
  router.push('/home/selectall')
}


// 获取订单ID（从localStorage或路由参数）
const getOrderId = () => {
  // 优先从localStorage获取
  const orderIdFromStorage = localStorage.getItem('orderId')
  // 也可以从路由参数获取
  const orderIdFromRoute = route.query.orderId
  
  return orderIdFromStorage || orderIdFromRoute || null
}

// 加载订单信息
const loadOrderInfo = async() => {
  try {
    const orderId = getOrderId()
    if (!orderId) {
      console.error('未找到订单ID')
      ElMessage.warning('未找到订单信息，请返回重新操作')
      return
    }
    
    console.log('加载订单信息，订单ID:', orderId)
    
    // 直接获取订单详情（状态更新已经在updateOrderStatusOnLoad中完成）
    const orderRes = await OrderApi.getOrderInfo(orderId)
    console.log('订单详情响应:', orderRes)
    
    if (orderRes.code === 200 && orderRes.data && orderRes.data.entity) {
      orderInfo.value = orderRes.data.entity
      console.log('订单信息加载成功:', orderInfo.value)
    } else {
      console.warn('获取订单详情失败:', orderRes.message)
      ElMessage.warning('获取订单详情失败: ' + (orderRes.message || '未知错误'))
    }
    
  } catch (error) {
    console.error('加载订单信息失败:', error)
    ElMessage.error('加载订单信息失败: ' + (error.message || '未知错误'))
  }
}

// 生命周期
onMounted(async () => {
  console.log('PaymentSuccess页面加载，路由参数:', route.query)
  
  // 检查是否是充值支付
  const type = route.query.type
  
  if (type === 'recharge') {
    // 处理充值支付成功
    console.log('处理充值支付成功')
    isRecharge.value = true
    await handleRechargeSuccess()
  } else {
    // 处理订单支付成功
    console.log('处理订单支付成功')
    
    // 先更新订单状态
    await updateOrderStatusOnLoad()
    
    // 然后加载订单信息
    await loadOrderInfo()
    
    // 支付成功后清理localStorage中的订单ID（避免重复使用）
    setTimeout(() => {
      localStorage.removeItem('orderId')
      console.log('已清理localStorage中的orderId')
    }, 5000)
  }
})

// 页面加载时更新订单状态（带重试机制和进度显示）
const updateOrderStatusOnLoad = async () => {
  isUpdatingStatus.value = true
  statusProgress.value = 10
  
  const orderId = getOrderId()
  console.log('尝试更新订单状态，订单ID:', orderId)
  
  if (!orderId) {
    console.warn('未找到订单ID，无法更新状态')
    ElMessage.warning('未找到订单ID，订单状态可能无法自动更新')
    isUpdatingStatus.value = false
    statusProgress.value = 100
    return false
  }
  
  // 先检查订单是否已经处于已支付状态
  try {
    const checkRes = await OrderApi.getOrderInfo(orderId)
    if (checkRes.code === 200 && checkRes.data && checkRes.data.entity) {
      const currentStatus = checkRes.data.entity.status
      if (currentStatus === 1) { // 1表示已支付
        console.log('订单状态已经是已支付，无需重复更新')
        statusProgress.value = 100
        isUpdatingStatus.value = false
        return true
      }
    }
  } catch (error) {
    console.warn('检查订单状态失败，继续尝试更新:', error)
  }
  
  let retryCount = 0
  const maxRetries = 3
  
  const tryUpdateStatus = async () => {
    try {
      console.log(`第${retryCount + 1}次尝试更新订单状态...`)
      statusProgress.value = 30 + (retryCount * 20)
      
      const res = await OrderStatusApi.updateOrderStatus(orderId)
      
      if (res.code === 200) {
        console.log('订单状态更新成功')
        statusProgress.value = 100
        ElMessage.success('订单状态已更新为已支付')
        return true
      } else {
        console.warn('订单状态更新失败:', res.message)
        return false
      }
    } catch (error) {
      console.error('更新订单状态出错:', error)
      return false
    }
  }
  
  // 立即尝试第一次
  let success = await tryUpdateStatus()
  
  // 如果失败，重试最多3次
  while (!success && retryCount < maxRetries) {
    retryCount++
    console.log(`等待2秒后重试... (${retryCount}/${maxRetries})`)
    await new Promise(resolve => setTimeout(resolve, 2000))
    
    // 重试前再次检查订单状态
    try {
      const checkRes = await OrderApi.getOrderInfo(orderId)
      if (checkRes.code === 200 && checkRes.data && checkRes.data.entity) {
        const currentStatus = checkRes.data.entity.status
        if (currentStatus === 1) { // 1表示已支付
          console.log('订单状态已经是已支付，停止重试')
          success = true
          break
        }
      }
    } catch (error) {
      console.warn('重试前检查订单状态失败，继续重试:', error)
    }
    
    if (!success) {
      success = await tryUpdateStatus()
    }
  }
  
  if (!success) {
    console.warn('订单状态更新失败，已达到最大重试次数')
    statusProgress.value = 100
    ElMessage.warning({
      message: '订单状态更新失败，请稍后到"我的订单"页面查看',
      duration: 5000
    })
  }
  
  isUpdatingStatus.value = false
  return success
}

// 处理充值支付成功
const handleRechargeSuccess = async () => {
  try {
    const amount = route.query.amount
    
    // 获取当前用户信息
    const userRes = await UserApi.getCurrentUser()
    if (userRes.code === 200) {
      const userId = userRes.data.entity.id
      
      // 更新用户余额
      const updateRes = await fetch('http://localhost:8080/user/updateBalance', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        },
        body: JSON.stringify({
          userId: userId,
          amount: amount
        })
      })
      
      const updateData = await updateRes.json()
      if (updateData.code === 200) {
      } else {
        ElMessage.error(updateData.message || '余额更新失败')
      }
    }
  } catch (error) {
    ElMessage.error('余额更新失败')
  }
}
</script>

<style scoped>
.payment-success-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  background: linear-gradient(135deg, #fff5f5 0%, #ffe0e0 100%);
}

.success-card {
  max-width: 600px;
  width: 100%;
  border-radius: 16px;
  overflow: hidden;
}

.success-content {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  margin-bottom: 20px;
}

.success-title {
  font-size: 32px;
  color: #67C23A;
  margin-bottom: 10px;
  font-weight: bold;
}

.success-message {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.status-update-info {
  margin: 30px 0;
}

.status-description {
  padding: 10px 0;
}

.status-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

.order-info {
  margin: 30px 0;
  text-align: left;
}

.action-buttons {
  display: flex;
  gap: 20px;
  justify-content: center;
  margin: 30px 0;
}

.primary-btn, .success-btn {
  padding: 12px 30px;
  font-size: 16px;
  border-radius: 25px;
  transition: all 0.3s;
}

.primary-btn {
  background: linear-gradient(45deg, #e54d42, #ff6b6b);
  border: none;
}

.primary-btn:hover {
  background: linear-gradient(45deg, #ff6b6b, #e54d42);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(229, 77, 66, 0.3);
}

.success-btn {
  background: linear-gradient(45deg, #67C23A, #85ce61);
  border: none;
}

.success-btn:hover {
  background: linear-gradient(45deg, #85ce61, #67C23A);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.3);
}

@media (max-width: 768px) {
  .action-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .primary-btn, .success-btn {
    width: 200px;
  }
}
</style>