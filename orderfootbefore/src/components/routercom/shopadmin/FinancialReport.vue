<template>
  <div class="financial-report-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>财务报表</h1>
      <div class="date-filter">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          @change="handleDateChange"
        />
        <el-button type="primary" @click="refreshData">刷新</el-button>
      </div>
    </div>

    <!-- 总览卡片 -->
    <div class="overview-cards">
      <div class="card">
        <div class="card-icon revenue">
          <el-icon><Money /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">今日营收</div>
          <div class="card-value">¥{{ formatMoney(overview.today?.payAmount || 0) }}</div>
          <div class="card-subtitle">{{ overview.today?.totalOrders || 0 }} 单</div>
        </div>
      </div>

      <div class="card">
        <div class="card-icon orders">
          <el-icon><ShoppingCart /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">本月营收</div>
          <div class="card-value">¥{{ formatMoney(overview.month?.payAmount || 0) }}</div>
          <div class="card-subtitle">{{ overview.month?.totalOrders || 0 }} 单</div>
        </div>
      </div>

      <div class="card">
        <div class="card-icon customers">
          <el-icon><User /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">总订单数</div>
          <div class="card-value">{{ overview.total?.totalOrders || 0 }}</div>
          <div class="card-subtitle">累计订单</div>
        </div>
      </div>

      <div class="card">
        <div class="card-icon amount">
          <el-icon><Wallet /></el-icon>
        </div>
        <div class="card-content">
          <div class="card-title">总金额</div>
          <div class="card-value">¥{{ formatMoney(overview.total?.payAmount || 0) }}</div>
          <div class="card-subtitle">累计营收</div>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section">
      <!-- 收入趋势图 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>收入趋势</h3>
          <el-radio-group v-model="trendDays" size="small" @change="loadTrendData">
            <el-radio-button :label="7">近7天</el-radio-button>
            <el-radio-button :label="30">近30天</el-radio-button>
          </el-radio-group>
        </div>
        <div ref="trendChartRef" class="chart-container"></div>
      </div>

      <!-- 支付方式分布 -->
      <div class="chart-card">
        <div class="chart-header">
          <h3>支付方式分布</h3>
        </div>
        <div ref="paymentChartRef" class="chart-container"></div>
      </div>
    </div>

    <!-- 详细数据表格 -->
    <div class="table-section">
      <div class="table-header">
        <h3>财务明细</h3>
        <el-button type="primary" size="small" @click="exportData">导出报表</el-button>
      </div>
      <el-table :data="reportList" stripe border style="width: 100%">
        <el-table-column prop="reportDate" label="日期" width="120" />
        <el-table-column prop="totalOrders" label="总订单数" width="100" align="right" />
        <el-table-column prop="completedOrders" label="已完成" width="100" align="right" />
        <el-table-column prop="cancelledOrders" label="已取消" width="100" align="right" />
        <el-table-column prop="totalAmount" label="总金额" width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.totalAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="payAmount" label="实付金额" width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.payAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="discountAmount" label="优惠金额" width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.discountAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="balancePayAmount" label="余额支付" width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.balancePayAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="alipayAmount" label="支付宝" width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.alipayAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalCustomers" label="客户数" width="100" align="right" />
        <el-table-column prop="avgOrderAmount" label="平均订单额" width="120" align="right">
          <template #default="scope">
            ¥{{ formatMoney(scope.row.avgOrderAmount) }}
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue';
import { ElMessage } from 'element-plus';
import * as echarts from 'echarts';
import * as XLSX from 'xlsx';
import FinancialReportApi from '../../../../api/FinancialReportApi';
import { Money, ShoppingCart, User, Wallet } from '@element-plus/icons-vue';

const dateRange = ref([]);
const overview = ref({});
const reportList = ref([]);
const trendDays = ref(7);

const trendChartRef = ref(null);
const paymentChartRef = ref(null);
let trendChart = null;
let paymentChart = null;

// 格式化金额
const formatMoney = (value) => {
  if (!value) return '0.00';
  return Number(value).toFixed(2);
};

// 日期变化
const handleDateChange = () => {
  loadStatsData();
  loadReportList();
};

// 刷新数据
const refreshData = () => {
  loadOverviewData();
  loadTrendData();
  loadStatsData();
  loadReportList();
};

// 加载总览数据
const loadOverviewData = async () => {
  try {
    const res = await FinancialReportApi.getOverview();
    if (res.code === 200) {
      overview.value = res.data || {};
    }
  } catch (error) {
    console.error('加载总览数据失败:', error);
  }
};

// 加载趋势数据
const loadTrendData = async () => {
  try {
    const res = await FinancialReportApi.getTrend({ days: trendDays.value });
    if (res.code === 200) {
      initTrendChart(res.data || []);
    }
  } catch (error) {
    console.error('加载趋势数据失败:', error);
  }
};

// 加载统计数据
const loadStatsData = async () => {
  try {
    if (!dateRange.value || dateRange.value.length !== 2) return;

    const res = await FinancialReportApi.getStats({
      startDate: dateRange.value[0],
      endDate: dateRange.value[1]
    });

    if (res.code === 200) {
      initPaymentChart(res.data || {});
    }
  } catch (error) {
    console.error('加载统计数据失败:', error);
  }
};

// 加载报表列表
const loadReportList = async () => {
  try {
    const params = {};
    if (dateRange.value && dateRange.value.length === 2) {
      params.startDate = dateRange.value[0];
      params.endDate = dateRange.value[1];
    }

    const res = await FinancialReportApi.getReportList(params);
    if (res.code === 200) {
      reportList.value = res.data || [];
    }
  } catch (error) {
    console.error('加载报表列表失败:', error);
  }
};

// 初始化趋势图
const initTrendChart = (data) => {
  if (!trendChartRef.value) return;

  if (trendChart) {
    trendChart.dispose();
  }

  trendChart = echarts.init(trendChartRef.value);

  const dates = data.map(item => item.date);
  const amounts = data.map(item => item.payAmount);
  const orders = data.map(item => item.totalOrders);

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['收入', '订单数'],
      top: 10
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false
    },
    yAxis: [
      {
        type: 'value',
        name: '收入(元)',
        position: 'left'
      },
      {
        type: 'value',
        name: '订单数',
        position: 'right'
      }
    ],
    series: [
      {
        name: '收入',
        type: 'line',
        data: amounts,
        smooth: true,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
          ])
        }
      },
      {
        name: '订单数',
        type: 'bar',
        yAxisIndex: 1,
        data: orders,
        itemStyle: { color: '#67C23A' }
      }
    ]
  };

  trendChart.setOption(option);
};

// 初始化支付方式图表
const initPaymentChart = (data) => {
  if (!paymentChartRef.value) return;

  if (paymentChart) {
    paymentChart.dispose();
  }

  paymentChart = echarts.init(paymentChartRef.value);

  const balanceAmount = Number(data.balancePayAmount || 0);
  const alipayAmount = Number(data.alipayAmount || 0);

  const paymentData = [
    { name: '余额支付', value: balanceAmount, itemStyle: { color: '#FF6B00' } },
    { name: '支付宝', value: alipayAmount, itemStyle: { color: '#1677FF' } }
  ];

  // 过滤掉值为0的数据
  const filteredPaymentData = paymentData.filter(item => item.value > 0);

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: ¥{c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left'
    },
    series: [
      {
        name: '支付方式',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        label: {
          show: true,
          formatter: '{b}: {d}%'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        data: filteredPaymentData
      }
    ]
  };

  paymentChart.setOption(option);
};

// 导出数据
const exportData = () => {
  try {
    if (!reportList.value || reportList.value.length === 0) {
      ElMessage.warning('暂无数据可导出');
      return;
    }

    // 创建工作簿
    const wb = XLSX.utils.book_new();

    // 定义表头
    const headers = ['报表日期', '总订单数', '已完成', '已取消', '总金额', '实付金额', '优惠金额', '余额支付', '支付宝', '客户数', '平均订单额'];

    // 创建数据数组
    const dataArray = [headers];

    // 添加数据行
    reportList.value.forEach(row => {
      const rowArray = [
        row.reportDate,
        row.totalOrders,
        row.completedOrders,
        row.cancelledOrders,
        row.totalAmount,
        row.payAmount,
        row.discountAmount,
        row.balancePayAmount,
        row.alipayAmount,
        row.totalCustomers,
        row.avgOrderAmount
      ];
      dataArray.push(rowArray);
    });

    // 创建工作表
    const ws = XLSX.utils.aoa_to_sheet(dataArray);
    
    // 设置列宽
    ws['!cols'] = [
      { wch: 15 }, // 报表日期
      { wch: 10 }, // 总订单数
      { wch: 10 }, // 已完成
      { wch: 10 }, // 已取消
      { wch: 12 }, // 总金额
      { wch: 12 }, // 实付金额
      { wch: 12 }, // 优惠金额
      { wch: 12 }, // 余额支付
      { wch: 12 }, // 支付宝
      { wch: 10 }, // 客户数
      { wch: 15 }  // 平均订单额
    ];

    XLSX.utils.book_append_sheet(wb, ws, '财务报表');

    // 生成文件名
    const startDate = dateRange.value[0] ? new Date(dateRange.value[0]).toLocaleDateString() : '';
    const endDate = dateRange.value[1] ? new Date(dateRange.value[1]).toLocaleDateString() : '';
    const fileName = `财务报表_${startDate}_${endDate}.xlsx`;

    XLSX.writeFile(wb, fileName);
    ElMessage.success('导出成功');
  } catch (error) {
    console.error('导出Excel失败:', error);
    ElMessage.error('导出失败');
  }
};

// 窗口缩放处理
const handleResize = () => {
  if (trendChart) trendChart.resize();
  if (paymentChart) paymentChart.resize();
};

onMounted(async () => {
  // 设置默认日期范围为本月
  const now = new Date();
  const firstDay = new Date(now.getFullYear(), now.getMonth(), 1);
  const lastDay = new Date(now.getFullYear(), now.getMonth() + 1, 0);
  dateRange.value = [firstDay, lastDay];
  
  await loadOverviewData();
  await loadTrendData();
  await loadStatsData();
  await loadReportList();
  await nextTick();

  window.addEventListener('resize', handleResize);
});

onBeforeUnmount(() => {
  if (trendChart) trendChart.dispose();
  if (paymentChart) paymentChart.dispose();
  window.removeEventListener('resize', handleResize);
});
</script>

<style scoped>
.financial-report-container {
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
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.page-header h1 {
  margin: 0;
  font-size: 24px;
  color: #333;
  font-weight: 600;
}

.date-filter {
  display: flex;
  gap: 10px;
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.card {
  display: flex;
  align-items: center;
  padding: 20px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease;
}

.card:hover {
  transform: translateY(-5px);
}

.card-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 28px;
  color: #fff;
}

.card-icon.revenue {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.card-icon.orders {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon.customers {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.card-icon.amount {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  flex: 1;
}

.card-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 8px;
}

.card-value {
  font-size: 24px;
  font-weight: 600;
  color: #333;
  margin-bottom: 4px;
}

.card-subtitle {
  font-size: 12px;
  color: #999;
}

.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}

.chart-container {
  width: 100%;
  height: 350px;
}

.table-section {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.table-header h3 {
  margin: 0;
  font-size: 18px;
  color: #333;
  font-weight: 600;
}
</style>