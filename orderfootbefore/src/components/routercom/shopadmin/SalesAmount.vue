<template>
  <div class="chart-page-container">
    <div class="page-header">
      <h1>数据统计分析</h1>
    </div>
    <div class="chart-wrapper">
      <div ref="chartRef" class="chart-container"></div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, onBeforeUnmount, ref, watch, nextTick } from 'vue';
import { orderlist } from '../../../../api/order.js';
import * as echarts from 'echarts';

const homeInfo = ref(null);
const chartRef = ref(null);
let chart = null;
let resizeHandler = null;

// 获取接口数据
const getHomeInfo = async () => {
  try {
    await new Promise(resolve => setTimeout(resolve, 500));
    const list = await orderlist();
    console.log('接口返回数据:', list);
    
    const xAxisData = ['5:00','6:00', '9:00', '12:00', '15:00', '18:00', '20:00','22:30'];
    const orderCount = list?.data?.orderlist?.map(item => item.totalAmount) || [];
    const salesAmount = list?.data?.orderlist?.map(item => item.payAmount) || [];
    const registerUser = list?.data?.orderlist?.map(item => item.discountAmount) || [];
    
    const fillData = (data, defaultArr) => {
      if (data.length >= xAxisData.length) return data.slice(0, xAxisData.length);
      return [...data, ...defaultArr].slice(0, xAxisData.length);
    };

    homeInfo.value = {
      xAxisData,
      orderCount: fillData(orderCount, [10, 220, 370, 103, 103, 298, 150, 80]),
      salesAmount: fillData(salesAmount, [20, 200, 310, 103, 103, 298, 120, 60]),
      registerUser: fillData(registerUser, [120, 220, 330, 103, 103, 298, 180, 100])
    };
  } catch (error) {
    console.error('获取数据失败:', error);
    homeInfo.value = {
      xAxisData: ['5:00','6:00', '9:00', '12:00', '15:00', '18:00', '20:00','22:30'],
      orderCount: [10, 220, 370, 103, 103, 298, 150, 80],
      salesAmount: [20, 200, 310, 103, 103, 298, 120, 60],
      registerUser: [120, 220, 330, 103, 103, 298, 180, 100]
    };
  }
};

// 初始化折线图（修复文字重叠）
const initChart = async () => {
  try {
    await nextTick();
    
    if (!chartRef.value || !homeInfo.value) {
      console.error('容器或数据未准备好');
      return;
    }

    if (chart) chart.dispose();
    chart = echarts.init(chartRef.value);

    const option = {
      // 1. 调整标题位置，增加与图例的间距
      title: {
        text: '销售额与订单量统计',
        left: 'center',
        top: 10, // 标题上移，留出图例空间
        textStyle: {
          fontSize: 18,
          fontWeight: 600,
          color: '#333'
        },
        subtext: '按小时维度统计',
        subtextStyle: {
          fontSize: 12,
          color: '#666'
        },
        subtextAlign: 'center', // 副标题居中
        itemGap: 15 // 标题与副标题间距
      },
      
      // 2. 调整图例位置，避免与标题重叠
      legend: {
        data: ['销售额', '订单量', '优惠金额'],
        top: 60, // 图例下移，避开标题/副标题
        left: 'center',
        textStyle: {
          color: '#666',
          fontSize: 12 // 缩小图例文字
        },
        itemGap: 30, // 增大图例项间距
        itemWidth: 15, // 图例标记宽度
        itemHeight: 15 // 图例标记高度
      },
      
      // 3. 调整提示框样式
      tooltip: {
        trigger: 'axis',
        axisPointer: { type: 'cross' },
        backgroundColor: 'rgba(255,255,255,0.9)',
        borderColor: '#e6e6e6',
        borderWidth: 1,
        padding: 10,
        textStyle: { color: '#333' },
        formatter: function(params) {
          let res = `<div style="font-weight:600;margin-bottom:5px;">${params[0].axisValue}</div>`;
          params.forEach(item => {
            res += `<div style="margin:3px 0;">
              <span style="display:inline-block;width:10px;height:10px;background:${item.color};border-radius:50%;margin-right:5px;"></span>
              ${item.seriesName}：<strong>${item.value}</strong> 元
            </div>`;
          });
          return res;
        }
      },
      
      // 4. 调整网格布局，增加上下间距
      grid: {
        left: '8%',   // 左间距增大，避免Y轴标签挤压
        right: '8%',
        top: '25%',   // 上间距增大，避开图例
        bottom: '12%',
        containLabel: true
      },
      
      // 5. X轴样式优化
      xAxis: [
        {
          type: 'category',
          boundaryGap: false,
          data: homeInfo.value.xAxisData,
          axisLine: { lineStyle: { color: '#e6e6e6' } },
          axisLabel: {
            color: '#666',
            fontSize: 11, // 缩小X轴文字
            interval: 0,
            margin: 8 // 增加与轴线的间距
          },
          axisTick: { alignWithLabel: true }
        }
      ],
      
      // 6. Y轴样式优化
      yAxis: [
        {
          type: 'value',
          name: '金额 (元)',
          nameTextStyle: {
            color: '#666',
            fontSize: 11,
            padding: [0, 0, 0, -10] // 调整Y轴名称位置，避免挤压
          },
          nameGap: 20, // Y轴名称与轴线间距
          axisLine: { lineStyle: { color: '#e6e6e6' } },
          axisLabel: {
            color: '#666',
            fontSize: 11,
            margin: 8 // 增加与轴线的间距
          },
          splitLine: { lineStyle: { color: '#f5f5f5' } }
        }
      ],
      
      // 7. 折线图系列配置
      series: [
        {
          name: '销售额',
          type: 'line',
          data: homeInfo.value.salesAmount,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 2, color: '#409EFF' },
          itemStyle: { color: '#409EFF', borderColor: '#fff', borderWidth: 1 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
            ])
          },
          emphasis: { symbolSize: 8 }
        },
        {
          name: '订单量',
          type: 'line',
          data: homeInfo.value.orderCount,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 2, color: '#67C23A' },
          itemStyle: { color: '#67C23A', borderColor: '#fff', borderWidth: 1 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
              { offset: 1, color: 'rgba(103, 194, 58, 0.05)' }
            ])
          },
          emphasis: { symbolSize: 8 }
        },
        {
          name: '优惠金额',
          type: 'line',
          data: homeInfo.value.registerUser,
          smooth: true,
          symbol: 'circle',
          symbolSize: 6,
          lineStyle: { width: 2, color: '#E6A23C' },
          itemStyle: { color: '#E6A23C', borderColor: '#fff', borderWidth: 1 },
          areaStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
              { offset: 1, color: 'rgba(230, 162, 60, 0.05)' }
            ])
          },
          emphasis: { symbolSize: 8 }
        }
      ],
      
      animation: true,
      animationDuration: 1000,
      animationEasing: 'cubicOut'
    };

    chart.setOption(option, true);
    console.log('折线图初始化完成');
  } catch (error) {
    console.error('折线图初始化失败:', error);
  }
};

// 窗口缩放处理
const handleResize = () => {
  if (chart) chart.resize();
};

// 监听数据变化
watch(homeInfo, (newVal) => {
  if (newVal) nextTick(() => initChart());
}, { immediate: false });

// 生命周期
onMounted(async () => {
  await getHomeInfo();
  await initChart();
  resizeHandler = handleResize;
  window.addEventListener('resize', resizeHandler);
});

onBeforeUnmount(() => {
  if (chart) chart.dispose();
  if (resizeHandler) window.removeEventListener('resize', resizeHandler);
  chart = null;
  resizeHandler = null;
});
</script>

<style scoped>
.chart-page-container {
  width: 100%;
  height: 100vh;
  padding: 20px;
  box-sizing: border-box;
  background-color: #f5f7fa;
  overflow: hidden;
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e6e6e6;
}

.page-header h1 {
  margin: 0;
  font-size: 20px;
  color: #333;
  font-weight: 600;
}

.chart-wrapper {
  width: 100%;
  height: calc(100% - 60px);
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-sizing: border-box;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.chart-container {
  width: 100% !important;
  height: 100% !important;
  min-width: 700px;
  min-height: 400px;
}

/* 响应式适配，避免小屏幕重叠 */
@media (max-width: 992px) {
  .chart-container {
    min-width: 500px;
    min-height: 350px;
  }
  :deep(.echarts-title) {
    font-size: 16px !important;
  }
  :deep(.echarts-legend-text) {
    font-size: 11px !important;
  }
}

@media (max-width: 768px) {
  .chart-page-container {
    padding: 10px;
  }
  .chart-wrapper {
    padding: 10px;
  }
  .chart-container {
    min-width: 300px;
    min-height: 300px;
  }
  .page-header h1 {
    font-size: 16px;
  }
  /* 移动端进一步缩小文字 */
  :deep(.echarts-title) {
    font-size: 14px !important;
  }
  :deep(.echarts-legend) {
    top: 50px !important;
  }
  :deep(.echarts-legend-text) {
    font-size: 10px !important;
  }
  :deep(.echarts-axis-label) {
    font-size: 10px !important;
  }
}
</style>