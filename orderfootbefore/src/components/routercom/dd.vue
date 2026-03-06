<template>
  <div class="orders-container">
    <div class="back-bar">
      <button class="back-btn" @click="goBack">← 返回我的页面</button>
    </div>
    <div class="orders-title">
      <h2>我的订单</h2>
    </div>
    <!-- 订单列表 -->
    <div class="orders-list" v-if="orders.length">
      <div class="order-item" v-for="order in orders" :key="order.id">
        <div class="order-header">
          <span class="order-no">订单编号：{{ order.order_no }}</span>
          <span class="order-status" :class="getStatusClass(order.status)">{{ getStatusText(order.status) }}</span>
        </div>
        <div class="order-body">
          <div class="order-info">
            <p>店铺ID：{{ order.shop_id }}</p>
            <p>实付金额：¥{{ order.pay_amount }}</p>
            <p>支付方式：{{ getPayTypeText(order.pay_type) }}</p>
            <p>订单类型：{{ getOrderTypeText(order.order_type) }}</p>
          </div>
          <div class="order-time">
            <p>创建时间：{{ formatTime(order.create_time) }}</p>
            <p v-if="order.pay_time">支付时间：{{ formatTime(order.pay_time) }}</p>
          </div>
        </div>
      </div>
    </div>
    <div class="no-orders" v-else>
      暂无订单
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import orderApi from '../../api/orderApi'; // 需自行实现订单接口

const router = useRouter();
const orders = ref([]);

// 获取登录用户ID
const getUserId = () => {
  const userInfo = localStorage.getItem('userInfo');
  return userInfo ? JSON.parse(userInfo).id : null;
};

// 获取订单列表
const fetchOrders = async () => {
  const userId = getUserId();
  if (!userId) {
    router.push('/login');
    return;
  }
  try {
    const response = await orderApi.getUserOrders(userId);
    if (response.code === 200) {
      orders.value = response.data;
    }
  } catch (error) {
    console.error('获取订单失败：', error);
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '暂无';
  return new Date(time).toLocaleString();
};

// 订单状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待支付',
    1: '已支付',
    2: '已接单',
    3: '配送中',
    4: '已完成'
  };
  return statusMap[status] || '未知状态';
};

// 订单状态样式
const getStatusClass = (status) => {
  const classMap = {
    0: 'status-wait',
    1: 'status-paid',
    2: 'status-accepted',
    3: 'status-delivering',
    4: 'status-finished'
  };
  return classMap[status] || '';
};

// 支付方式文本
const getPayTypeText = (type) => {
  return type === 1 ? '微信支付' : type === 2 ? '支付宝支付' : '未知方式';
};

// 订单类型文本
const getOrderTypeText = (type) => {
  return type === 1 ? '堂食' : type === 2 ? '外卖' : '未知类型';
};

// 返回我的页面
const goBack = () => {
  router.push('/cz');
};

// 初始化加载订单
onMounted(() => {
  fetchOrders();
});
</script>

<style scoped>
.orders-container {
  background-color: #f5f5f5;
  min-height: 100vh;
}

.back-bar {
  background-color: #fff;
  padding: 12px 15px;
  border-bottom: 1px solid #f0f0f0;
}
.back-btn {
  border: none;
  background: transparent;
  font-size: 16px;
  color: #333;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
}

.orders-title {
  padding: 20px 15px;
  background-color: #fff;
  margin-bottom: 10px;
  h2 {
    font-size: 20px;
    font-weight: 600;
    color: #333;
  }
}

.orders-list {
  padding: 0 15px;
}

.order-item {
  background-color: #fff;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
  .order-no {
    font-size: 14px;
    color: #666;
  }
  .order-status {
    font-size: 14px;
    font-weight: 500;
  }
  .status-wait {
    color: #ff9900;
  }
  .status-paid {
    color: #409EFF;
  }
  .status-accepted {
    color: #52c41a;
  }
  .status-delivering {
    color: #fa8c16;
  }
  .status-finished {
    color: #999;
  }
}

.order-body {
  display: flex;
  justify-content: space-between;
  .order-info {
    flex: 1;
    p {
      margin-bottom: 5px;
      font-size: 14px;
      color: #333;
    }
  }
  .order-time {
    text-align: right;
    p {
      margin-bottom: 5px;
      font-size: 14px;
      color: #666;
    }
  }
}

.no-orders {
  text-align: center;
  padding: 50px 0;
  color: #999;
  font-size: 16px;
  background-color: #fff;
  margin: 0 15px;
  border-radius: 8px;
}
</style>