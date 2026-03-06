<template>
  <div class="shop-rankings">
    <div class="page-header">
      <h2>店铺排行榜</h2>
      <div class="header-actions">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          @change="handleDateChange"
        />
        <el-button type="primary" @click="refreshRankings">刷新数据</el-button>
      </div>
    </div>

    <div class="ranking-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="card">
            <div class="card-icon bg-blue">
              <i class="el-icon-shopping-bag-1"></i>
            </div>
            <div class="card-content">
              <h3>{{ rankingData.totalOrders }}</h3>
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
              <h3>¥{{ rankingData.totalRevenue }}</h3>
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
              <h3>{{ rankingData.totalCustomers }}</h3>
              <p>客户数</p>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="card">
            <div class="card-icon bg-purple">
              <i class="el-icon-star"></i>
            </div>
            <div class="card-content">
              <h3>{{ rankingData.avgRating }}</h3>
              <p>平均评分</p>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="ranking-sections">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="ranking-card">
            <h3>销售排行榜</h3>
            <el-table :data="salesRankings" style="width: 100%">
              <el-table-column type="index" label="排名" width="80" />
              <el-table-column prop="productName" label="商品名称" />
              <el-table-column prop="salesCount" label="销量" width="120" />
              <el-table-column prop="revenue" label="销售额" width="120">
                <template #default="{ row }">
                  ¥{{ row.revenue }}
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="ranking-card">
            <h3>好评排行榜</h3>
            <el-table :data="ratingRankings" style="width: 100%">
              <el-table-column type="index" label="排名" width="80" />
              <el-table-column prop="productName" label="商品名称" />
              <el-table-column prop="rating" label="评分" width="120">
                <template #default="{ row }">
                  <el-rate 
                    v-model="row.rating" 
                    disabled 
                    :max="5"
                    :allow-half="true"
                  />
                </template>
              </el-table-column>
              <el-table-column prop="reviewCount" label="评价数" width="120" />
            </el-table>
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <div class="ranking-card">
            <h3>复购率排行榜</h3>
            <el-table :data="repeatRankings" style="width: 100%">
              <el-table-column type="index" label="排名" width="80" />
              <el-table-column prop="productName" label="商品名称" />
              <el-table-column prop="repeatRate" label="复购率" width="120">
                <template #default="{ row }">
                  {{ row.repeatRate }}%
                </template>
              </el-table-column>
              <el-table-column prop="customerCount" label="复购客户" width="120" />
            </el-table>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="ranking-card">
            <h3>利润排行榜</h3>
            <el-table :data="profitRankings" style="width: 100%">
              <el-table-column type="index" label="排名" width="80" />
              <el-table-column prop="productName" label="商品名称" />
              <el-table-column prop="profit" label="利润" width="120">
                <template #default="{ row }">
                  ¥{{ row.profit }}
                </template>
              </el-table-column>
              <el-table-column prop="profitMargin" label="利润率" width="120">
                <template #default="{ row }">
                  {{ row.profitMargin }}%
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="charts-section">
      <div class="chart-card">
        <h3>销售趋势对比</h3>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import * as echarts from 'echarts';

const dateRange = ref([]);
const rankingData = ref({
  totalOrders: 0,
  totalRevenue: 0,
  totalCustomers: 0,
  avgRating: 0
});

const salesRankings = ref([
  { productName: '宫保鸡丁', salesCount: 156, revenue: 4368 },
  { productName: '麻婆豆腐', salesCount: 134, revenue: 2412 },
  { productName: '红烧肉', salesCount: 98, revenue: 3724 },
  { productName: '鱼香肉丝', salesCount: 87, revenue: 2349 },
  { productName: '酸辣汤', salesCount: 76, revenue: 1140 }
]);

const ratingRankings = ref([
  { productName: '宫保鸡丁', rating: 4.8, reviewCount: 156 },
  { productName: '红烧肉', rating: 4.7, reviewCount: 98 },
  { productName: '麻婆豆腐', rating: 4.6, reviewCount: 134 },
  { productName: '鱼香肉丝', rating: 4.5, reviewCount: 87 },
  { productName: '酸辣汤', rating: 4.4, reviewCount: 76 }
]);

const repeatRankings = ref([
  { productName: '宫保鸡丁', repeatRate: 65, customerCount: 101 },
  { productName: '红烧肉', repeatRate: 58, customerCount: 57 },
  { productName: '麻婆豆腐', repeatRate: 52, customerCount: 70 },
  { productName: '鱼香肉丝', repeatRate: 48, customerCount: 42 },
  { productName: '酸辣汤', repeatRate: 45, customerCount: 34 }
]);

const profitRankings = ref([
  { productName: '红烧肉', profit: 1862, profitMargin: 50 },
  { productName: '宫保鸡丁', profit: 1310, profitMargin: 30 },
  { productName: '麻婆豆腐', profit: 965, profitMargin: 40 },
  { productName: '鱼香肉丝', profit: 705, profitMargin: 30 },
  { productName: '酸辣汤', profit: 342, profitMargin: 30 }
]);

const trendChartRef = ref(null);
let trendChart = null;

// 初始化趋势图表
const initTrendChart = () => {
  if (trendChartRef.value) {
    trendChart = echarts.init(trendChartRef.value);
    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['销售额', '订单数', '客户数']
      },
      xAxis: {
        type: 'category',
        data: ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
      },
      yAxis: [
        {
          type: 'value',
          name: '金额(元)',
          position: 'left'
        },
        {
          type: 'value',
          name: '数量',
          position: 'right'
        }
      ],
      series: [
        {
          name: '销售额',
          type: 'line',
          yAxisIndex: 0,
          data: [1200, 1320, 1010, 1340, 900, 2300, 2100],
          smooth: true
        },
        {
          name: '订单数',
          type: 'bar',
          yAxisIndex: 1,
          data: [12, 18, 15, 22, 14, 25, 20]
        },
        {
          name: '客户数',
          type: 'line',
          yAxisIndex: 1,
          data: [8, 15, 12, 18, 11, 22, 17],
          smooth: true
        }
      ]
    };
    trendChart.setOption(option);
  }
};

// 处理日期范围变化
const handleDateChange = (value) => {
  console.log('日期范围变化:', value);
  refreshRankings();
};

// 刷新排行榜数据
const refreshRankings = () => {
  // 模拟数据刷新
  rankingData.value = {
    totalOrders: 1560,
    totalRevenue: 43680,
    totalCustomers: 890,
    avgRating: 4.6
  };
  
  // 重新初始化图表
  nextTick(() => {
    if (trendChart) {
      trendChart.dispose();
    }
    initTrendChart();
  });
};

// 窗口大小变化处理
const handleResize = () => {
  if (trendChart) trendChart.resize();
};

onMounted(() => {
  refreshRankings();
  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  if (trendChart) trendChart.dispose();
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.shop-rankings {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.page-header h2 {
  margin: 0;
  color: #333;
  font-size: 20px;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.ranking-cards {
  margin-bottom: 20px;
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

.ranking-sections {
  margin-bottom: 20px;
}

.ranking-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.ranking-card h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.charts-section {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.charts-section h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
}

.chart-container {
  height: 400px;
}

:deep(.el-table) {
  border-radius: 4px;
  overflow: hidden;
}

:deep(.el-rate) {
  display: flex;
  align-items: center;
}
</style>
