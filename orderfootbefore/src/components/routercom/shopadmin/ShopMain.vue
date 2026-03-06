<template>
  <div class="shop-main">
    <div class="dashboard-header">
      <h2>店铺管理仪表板</h2>
      <div class="date-range">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
      </div>
    </div>

    <div class="dashboard-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="card">
            <div class="card-icon bg-blue">
              <i class="el-icon-shopping-bag-1"></i>
            </div>
            <div class="card-content">
              <h3>{{ shopData.totalOrders }}</h3>
              <p>总订单数</p>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="card">
            <div class="card-icon bg-green">
              <i class="el-icon-money"></i>
            </div>
            <div class="card-content">
              <h3>¥{{ formatCurrency(shopData.totalRevenue) }}</h3>
              <p>总收入</p>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="card">
            <div class="card-icon bg-orange">
              <i class="el-icon-user"></i>
            </div>
            <div class="card-content">
              <h3>{{ shopData.totalCustomers }}</h3>
              <p>客户数</p>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="card">
            <div class="card-icon bg-purple">
              <i class="el-icon-trend-charts"></i>
            </div>
            <div class="card-content">
              <h3>{{ shopData.orderGrowth }}%</h3>
              <p>订单增长率</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="dashboard-charts">
      <el-row :gutter="20">
        <el-col :span="24">
          <div class="chart-card">
            <h3>订单量趋势</h3>
            <div ref="orderChartRef" class="chart-container"></div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="dashboard-tables">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="table-card">
            <h3>热门商品</h3>
            <el-table :data="shopData.hotProducts" style="width: 100%">
              <el-table-column prop="name" label="商品名称" />
              <el-table-column prop="sales" label="销量" />
              <el-table-column prop="revenue" label="销售额" :formatter="currencyFormatter" />
            </el-table>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="table-card">
            <h3>最新订单</h3>
            <el-table :data="shopData.latestOrders" style="width: 100%">
              <el-table-column prop="orderId" label="订单号" />
              <el-table-column prop="customer" label="客户" />
              <el-table-column prop="amount" label="金额" />
              <el-table-column prop="status" label="状态" />
            </el-table>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';
import { getShopDashboardData, getShopSalesStats, getHotProducts, getLatestOrders, getShopTrendData } from '../../../../api/ShopMainApi.js';

const dateRange = ref([]);
const shopData = ref({
  totalOrders: 0,
  totalRevenue: 0,
  totalCustomers: 0,
  orderGrowth: 0,
  hotProducts: [],
  latestOrders: []
});

const orderChartRef = ref(null);
let orderChart = null;

// 获取店铺数据
const getShopData = async () => {
  try {
    const shopId = localStorage.getItem("shopid");
    if (!shopId) {
      console.error('未获取到店铺ID');
      return;
    }

    // 获取仪表板数据
    const dashboardRes = await getShopDashboardData({ shopId });
    if (dashboardRes?.data?.data) {
      shopData.value = {
        ...shopData.value,
        ...dashboardRes.data.data
      };
    }

    // 获取热门商品数据
    const hotProductsRes = await getHotProducts({ shopId });
    if (hotProductsRes?.data?.data?.products) {
      shopData.value.hotProducts = hotProductsRes.data.data.products;
    }

    // 获取最新订单数据
    const latestOrdersRes = await getLatestOrders({ shopId });
    if (latestOrdersRes?.data?.data?.orders) {
      shopData.value.latestOrders = latestOrdersRes.data.data.orders;
    }

    // 获取趋势数据
    const trendRes = await getShopTrendData({ shopId });
    if (trendRes?.data?.data) {
      // 将趋势数据存储到shopData中，以便图表使用
      shopData.value.orderTrend = trendRes.data.data.orderTrend || [];
    }

    // 初始化图表
    await nextTick();
    initOrderChart();
  } catch (error) {
    console.error('获取店铺数据失败:', error);
    
    // 如果获取数据失败，显示错误信息而不是假数据
    console.error('获取店铺数据失败，无法显示真实数据');
    // 仍然初始化图表，但使用空数据
    shopData.value.orderTrend = [];
    
    await nextTick();
    initOrderChart();
  }
};

// 初始化订单量图表
const initOrderChart = () => {
  if (orderChartRef.value) {
    orderChart = echarts.init(orderChartRef.value);
    
    // 准备图表数据
    let dates = [];
    let orderData = [];
    
    // 使用实际的趋势数据或回退到示例数据
    if (shopData.value.orderTrend && shopData.value.orderTrend.length > 0) {
      dates = shopData.value.orderTrend.map(item => item.date);
      orderData = shopData.value.orderTrend.map(item => item.orders);
    } 
    
    const option = {
      tooltip: {
        trigger: 'axis',
        formatter: '{b}<br/>{a}: {c}'
      },
      xAxis: {
        type: 'category',
        data: dates,
        axisLabel: {
          rotate: 45,
          fontSize: 12
        }
      },
      yAxis: {
        type: 'value',
        name: '订单数',
        minInterval: 1
      },
      series: [{
        name: '订单量',
        data: orderData,
        type: 'line',
        smooth: false, // 改为false以显示更明显的波折
        symbol: 'circle',
        symbolSize: 6,
        itemStyle: {
          color: '#409eff'
        },
        lineStyle: {
          color: '#409eff',
          width: 3
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [{
              offset: 0,
              color: 'rgba(64, 158, 255, 0.3)'
            }, {
              offset: 1,
              color: 'rgba(64, 158, 255, 0.1)'
            }]
          }
        }
      }]
    };
    orderChart.setOption(option);
  }
};

// 处理日期范围变化
const handleDateChange = (value) => {
  console.log('日期范围变化:', value);
  getShopData();
};

// 窗口大小变化处理
const handleResize = () => {
  if (orderChart) orderChart.resize();
};

onMounted(async () => {
  await getShopData();
  
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  if (orderChart) orderChart.dispose();
  window.removeEventListener('resize', handleResize);
});

// 格式化货币，保留两位小数
const formatCurrency = (value) => {
  if (value == null || value === '') {
    return '0.00';
  }
  return parseFloat(value).toFixed(2);
};

// 表格货币格式化器
const currencyFormatter = (row, column, cellValue) => {
  if (cellValue == null || cellValue === '') {
    return '0.00';
  }
  return parseFloat(cellValue).toFixed(2);
};
</script>

<style scoped>
.shop-main {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.dashboard-header h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  color: white;
  font-size: 24px;
}

.bg-blue { background-color: #409eff; }
.bg-green { background-color: #67c23a; }
.bg-orange { background-color: #e6a23c; }
.bg-purple { background-color: #9013fe; }

.card-content h3 {
  margin: 0 0 5px 0;
  font-size: 24px;
  color: #333;
}

.card-content p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.dashboard-charts {
  margin-bottom: 20px;
}

.chart-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.chart-card h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.chart-container {
  height: 300px;
}

.table-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.table-card h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}
</style>
