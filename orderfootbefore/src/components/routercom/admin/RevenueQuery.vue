<template>
  <div class="revenue-query">

    <el-card shadow="never" class="card-body">
      <!-- 搜索条件 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="店铺ID">
          <el-input v-model="searchForm.shopId" placeholder="请输入店铺ID" />
        </el-form-item>
        <el-form-item label="店铺名称">
          <el-input v-model="searchForm.shopName" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="查询类型">
          <el-radio-group v-model="searchForm.queryType">
            <el-radio value="total">总营业额</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="queryRevenue">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 营业额数据展示 -->
      <div v-if="loading" class="loading-container">
        <el-loading-spinner />
        <span class="loading-text">加载中...</span>
      </div>

      <div v-else-if="revenueData && revenueData.length > 0" class="revenue-data">
        <!-- 图表切换按钮 -->
        <div class="chart-controls">
          <el-radio-group v-model="chartType" @change="handleChartTypeChange">
            <el-radio-button value="line">折线图</el-radio-button>
            <el-radio-button value="bar">柱状图</el-radio-button>
            <el-radio-button value="pie">饼图</el-radio-button>
          </el-radio-group>
          <el-button type="primary" @click="exportChart" :disabled="!chartData">导出图表</el-button>
        </div>

        <!-- 图表容器 -->
        <div class="chart-container">
          <div ref="chartRef" class="chart"></div>
        </div>

        <!-- 数据统计卡片 -->
        <div class="stats-cards">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">总营业额</div>
              <div class="stat-value">¥{{ formatMoney(totalRevenue) }}</div>
            </div>
            <div class="stat-icon revenue-icon">💰</div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">平均营业额</div>
              <div class="stat-value">¥{{ formatMoney(avgRevenue) }}</div>
            </div>
            <div class="stat-icon avg-icon">📊</div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">最高营业额</div>
              <div class="stat-value">¥{{ formatMoney(maxRevenue) }}</div>
            </div>
            <div class="stat-icon max-icon">📈</div>
          </el-card>
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-title">店铺数量</div>
              <div class="stat-value">{{ totalShops }}</div>
            </div>
            <div class="stat-icon shop-icon">🏪</div>
          </el-card>
        </div>

        <!-- 数据表格（可折叠） -->
        <el-collapse v-model="tableVisible" class="data-table-collapse">
          <el-collapse-item title="查看详细数据表格" name="table">
            <el-table :data="revenueData" style="width: 100%" stripe>
              <el-table-column prop="shopId" label="店铺ID" width="100" />
              <el-table-column prop="shopName" label="店铺名称" width="180" />
              <el-table-column prop="revenue" label="营业额" width="150">
              <template #default="scope">
                <span v-if="scope.row.revenue !== undefined && scope.row.revenue !== null && scope.row.revenue > 0" class="revenue-amount">
                  ¥{{ formatMoney(scope.row.revenue) }} 元
                </span>
                <span v-else-if="scope.row.revenue === 0" class="revenue-amount">
                  ¥0.00 元
                </span>
                <span v-else style="color: #909399; font-style: italic;">
                  暂无数据
                </span>
              </template>
              </el-table-column>
              <el-table-column prop="orderCount" label="订单数量" width="100" />
              <el-table-column prop="avgOrderValue" label="客单价" width="100">
                <template #default="scope">
                  ¥{{ formatMoney(scope.row.avgOrderValue) }}
                </template>
              </el-table-column>
              <el-table-column label="操作" width="150">
                <template #default="scope">
                  <el-button type="primary" size="small" @click="viewDetails(scope.row)">查看详情</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-collapse-item>
        </el-collapse>

        <!-- 分页 -->
        <div class="pagination">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handleCurrentChange"
            :current-page="currentPage"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
          />
        </div>
      </div>

      <div v-else-if="!loading && revenueData !== null" class="no-data">
        <el-empty description="暂无数据" />
      </div>

      <!-- 详情对话框 -->
      <teleport to="body">
        <el-dialog v-model="detailDialogVisible" :title="detailDialogTitle" width="700px">
          <div v-if="currentDetailData">
            <el-descriptions :column="1" border class="detail-descriptions">
              <el-descriptions-item label="店铺ID">{{ currentDetailData.shopId }}</el-descriptions-item>
              <el-descriptions-item label="店铺名称">{{ currentDetailData.shopName }}</el-descriptions-item>
              <el-descriptions-item label="营业额">{{ currentDetailData.revenue }} 元</el-descriptions-item>
              <el-descriptions-item label="统计时间">
                <span v-if="currentDetailData.timeRange">{{ currentDetailData.timeRange }}</span>
                <span v-else-if="searchForm.queryType === 'total'">总营业额</span>
                <span v-else-if="searchForm.queryType === 'daily' && searchForm.date">{{ formatDate(searchForm.date) }}</span>
                <span v-else-if="searchForm.queryType === 'monthly' && searchForm.month">{{ searchForm.month }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="订单数量">{{ currentDetailData.orderCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="客单价">{{ currentDetailData.avgOrderValue || 0 }} 元</el-descriptions-item>
            </el-descriptions>
            
            <!-- 订单列表预览 -->
            <div v-if="currentDetailData.orders && currentDetailData.orders.length > 0" class="order-preview">
              <h3 style="margin-top: 20px; margin-bottom: 10px;">订单明细（前5条）</h3>
              <el-table :data="currentDetailData.orders.slice(0, 5)" style="width: 100%" size="small">
                <el-table-column prop="orderId" label="订单ID" width="120" />
                <el-table-column prop="amount" label="订单金额" width="100" />
                <el-table-column prop="createTime" label="下单时间" width="180" />
              </el-table>
            </div>
          </div>
        </el-dialog>
      </teleport>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed, watch } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import ShopApi from '../../../../api/ShopApi'
import request from '../../../../utils/request'
import * as echarts from 'echarts'

// 搜索条件
const searchForm = reactive({
  shopId: '',
  shopName: '',
  queryType: 'total'
})

// 移除模拟数据，直接使用数据库真实数据

// 营业额数据
const revenueData = ref(null)
const allRevenueData = ref(null) // 存储所有数据
const loading = ref(false)
const total = ref(0)
const totalShops = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 详情对话框
const detailDialogVisible = ref(false)
const detailDialogTitle = ref('')
const currentDetailData = ref(null)

// 图表相关
const chartType = ref('line')
const chartRef = ref(null)
const chartInstance = ref(null)
const tableVisible = ref([''])

// 计算属性
const chartData = computed(() => {
  if (!revenueData.value || revenueData.value.length === 0) return null
  
  return revenueData.value.map(item => ({
    name: item.shopName,
    value: item.revenue || 0,
    orderCount: item.orderCount || 0,
    avgOrderValue: item.avgOrderValue || 0
  }))
})

const totalRevenue = computed(() => {
  if (!chartData.value) return 0
  return chartData.value.reduce((sum, item) => sum + item.value, 0)
})

const avgRevenue = computed(() => {
  if (!chartData.value || chartData.value.length === 0) return 0
  return Math.round(totalRevenue.value / chartData.value.length)
})

const maxRevenue = computed(() => {
  if (!chartData.value || chartData.value.length === 0) return 0
  return Math.max(...chartData.value.map(item => item.value))
})

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

// 格式化金额，添加千位分隔符并保留两位小数
const formatMoney = (value) => {
  if (value === undefined || value === null || isNaN(value)) return '0.00'
  
  // 转换为数字并保留两位小数
  const num = parseFloat(value)
  const formattedNum = Math.round(num * 100) / 100
  
  // 添加千位分隔符
  const withCommas = formattedNum.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
  
  // 确保有两位小数
  if (!withCommas.includes('.')) {
    return withCommas + '.00'
  }
  
  const parts = withCommas.split('.')
  if (parts[1] && parts[1].length < 2) {
    return withCommas + '0'
  }
  
  return withCommas
}

// 查询营业额数据
const queryRevenue = async () => {
  try {
    loading.value = true
    revenueData.value = null
    console.log('开始查询营业额数据', searchForm)
    
    // 根据查询类型调用不同的API
    if (searchForm.shopId) {
      // 单个店铺查询
      console.log('单个店铺查询，店铺ID:', searchForm.shopId)
      
      // 查询总营业额
      console.log('查询总营业额')
      const res = await ShopApi.getShopRevenue(searchForm.shopId)
      console.log('总营业额数据返回:', res)
      
      if (res && res.code === 200) {
        // 直接使用后端返回的entity数据
        const entity = res.data.entity || {}
        
        const singleShopData = [{
          shopId: entity.shopId || searchForm.shopId,
          shopName: entity.shopName || `店铺${searchForm.shopId}`,
          revenue: entity.revenue || 0,
          orderCount: entity.orderCount || 0,
          avgOrderValue: entity.avgOrderValue || 0,
          timeRange: entity.time || '总营业额'
        }]
        
        allRevenueData.value = singleShopData
        revenueData.value = singleShopData
        total.value = 1
      } else {
        // 数据查询失败
        console.log('获取店铺营业额数据失败')
        revenueData.value = []
        allRevenueData.value = []
        total.value = 0
        ElMessage.error('获取营业额数据失败，请检查店铺ID是否正确')
      }
    } else if (searchForm.shopName) {
      // 根据店铺名称查询（需要先获取店铺ID）
      console.log('按名称查询店铺，名称:', searchForm.shopName)
      
      let allShops = []
      let totalPages = 1
      let currentPage_num = 1
      let totalShops_count = 0
      
      // 循环查询所有页的店铺数据
      do {
        const shopRes = await ShopApi.ShopSelect({
          where: [
            { column: 'name', type: 'like', value: searchForm.shopName },
            { column: 'status', type: 'ne', value: 3 }
          ],
          page: currentPage_num,
          pageSize: 6
        })
        
        if (shopRes && shopRes.code === 200 && shopRes.data && shopRes.data.pageInfo) {
          const pageInfo = shopRes.data.pageInfo
          allShops.push(...pageInfo.list)
          totalPages = pageInfo.pages || 1
          totalShops_count = pageInfo.total || 0
          currentPage_num++
        } else {
          break
        }
      } while (currentPage_num <= totalPages)
      
      // 设置总店铺数量
      totalShops.value = totalShops_count
      
      if (allShops.length > 0) {
        console.log('找到匹配的店铺，数量:', allShops.length)
        const results = []
        
        // 并发请求优化
        console.log('开始并发查询店铺营业额')
        const promises = allShops.map(async (shop) => {
          try {
            console.log('查询店铺', shop.id, shop.name, '的营业额')
            const res = await ShopApi.getShopRevenue(shop.id)
            console.log('店铺', shop.id, '总营业额数据返回:', res)
            
            if (res && res.code === 200) {
                const entity = res.data.entity || {}
                return {
                  shopId: shop.id,
                  shopName: shop.name,
                  revenue: entity.revenue || 0,
                  orderCount: entity.orderCount || 0,
                  avgOrderValue: entity.avgOrderValue || 0,
                  timeRange: entity.time || '总营业额'
                }
            }
          } catch (err) {
            console.error(`获取店铺${shop.id}营业额失败:`, err)
          }
          return null
        })
        
        const promiseResults = await Promise.all(promises)
        results.push(...promiseResults.filter(Boolean))
        
        allRevenueData.value = results
        total.value = results.length
        
        // 前端分页处理
        updatePagedData()
        
        if (results.length === 0) {
          ElMessage.info('未查询到营业额数据')
        }
      } else {
        revenueData.value = []
        allRevenueData.value = []
        total.value = 0
        ElMessage.info('未找到相关店铺或店铺无营业额数据')
      }
    } else {
      // 查询所有店铺 - 使用专门的图表查询接口
      try {
        console.log('查询所有店铺营业额数据（图表专用）')
        const res = await ShopApi.getAllShopsRevenueForChart()
        console.log('图表数据返回:', res)
        
        if (res && res.code === 200 && res.data && res.data.shops) {
          allRevenueData.value = res.data.shops
          total.value = res.data.shops.length
          totalShops.value = res.data.totalShops || res.data.shops.length
          
          // 前端分页处理
          updatePagedData()
          
          if (res.data.shops.length === 0) {
            ElMessage.info('当前查询条件下未找到营业额数据')
          }
        } else {
          allRevenueData.value = []
          revenueData.value = []
          total.value = 0
          totalShops.value = 0
          ElMessage.info('暂无店铺数据')
        }
      } catch (error) {
        console.error('批量查询失败:', error)
        ElMessage.error('查询失败，请稍后重试')
        allRevenueData.value = []
        revenueData.value = []
        total.value = 0
        totalShops.value = 0
      }
    }
  } catch (error) {
    console.error('查询营业额失败:', error)
    // 移除模拟数据逻辑，直接显示错误信息
    ElMessage.error('查询失败，请稍后重试')
    revenueData.value = []
    allRevenueData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

// 重置搜索条件
const resetForm = () => {
  Object.assign(searchForm, {
    shopId: '',
    shopName: '',
    queryType: 'total'
  })
  revenueData.value = null
  allRevenueData.value = null
  totalShops.value = 0
  currentPage.value = 1
}

// 查看详情
const viewDetails = async (row) => {
  try {
    detailDialogTitle.value = `${row.shopName} - 营业额详情`
    
    // 获取详细数据
    const res = await ShopApi.getShopRevenue(row.shopId)
    
    if (res && res.code === 200) {
        const entity = res.data.entity || {}
        currentDetailData.value = {
          ...row,
          ...entity,
          // 确保核心数据字段存在
          orderCount: entity.orderCount || 0,
          avgOrderValue: entity.avgOrderValue || 0,
          timeRange: entity.time || '总营业额',
          // 订单列表
          orders: entity.orders || []
        }
        detailDialogVisible.value = true
    } else {
      ElMessage.warning('获取详情数据失败')
    }
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败，请稍后重试')
  }
}



// 初始化图表
const initChart = () => {
  if (!chartRef.value) return
  
  // 销毁旧实例
  if (chartInstance.value) {
    chartInstance.value.dispose()
  }
  
  // 创建新实例
  chartInstance.value = echarts.init(chartRef.value)
  updateChart()
}

// 更新图表
const updateChart = () => {
  if (!chartInstance.value || !chartData.value) return
  
  let option = {}
  
  if (chartType.value === 'line') {
    option = {
      title: {
        text: '店铺营业额趋势',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      tooltip: {
        trigger: 'axis',
        formatter: (params) => {
          const data = params[0]
          return `
            <div style="padding: 10px;">
              <div style="font-weight: bold;">${data.name}</div>
              <div>营业额: ¥${formatMoney(data.value)}</div>
              <div>订单数: ${data.data.orderCount}</div>
              <div>客单价: ¥${formatMoney(data.data.avgOrderValue)}</div>
            </div>
          `
        }
      },
      xAxis: {
        type: 'category',
        data: chartData.value.map(item => item.name),
        axisLabel: {
          rotate: 45,
          interval: 0
        }
      },
      yAxis: {
        type: 'value',
        name: '营业额 (元)',
        axisLabel: {
          formatter: (value) => `¥${formatMoney(value)}`
        }
      },
      series: [{
        data: chartData.value.map(item => ({
          value: item.value,
          orderCount: item.orderCount,
          avgOrderValue: item.avgOrderValue
        })),
        type: 'line',
        smooth: true,
        lineStyle: {
          color: '#409EFF',
          width: 3
        },
        itemStyle: {
          color: '#409EFF'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
              { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
            ]
          }
        }
      }]
    }
  } else if (chartType.value === 'bar') {
    option = {
      title: {
        text: '店铺营业额对比',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      tooltip: {
        trigger: 'axis',
        axisPointer: {
          type: 'shadow'
        },
        formatter: (params) => {
          const data = params[0]
          return `
            <div style="padding: 10px;">
              <div style="font-weight: bold;">${data.name}</div>
              <div>营业额: ¥${formatMoney(data.value)}</div>
              <div>订单数: ${data.data.orderCount}</div>
              <div>客单价: ¥${formatMoney(data.data.avgOrderValue)}</div>
            </div>
          `
        }
      },
      xAxis: {
        type: 'category',
        data: chartData.value.map(item => item.name),
        axisLabel: {
          rotate: 45,
          interval: 0
        }
      },
      yAxis: {
        type: 'value',
        name: '营业额 (元)',
        axisLabel: {
          formatter: (value) => `¥${formatMoney(value)}`
        }
      },
      series: [{
        data: chartData.value.map(item => ({
          value: item.value,
          orderCount: item.orderCount,
          avgOrderValue: item.avgOrderValue
        })),
        type: 'bar',
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ]),
          borderRadius: [5, 5, 0, 0]
        },
        emphasis: {
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#2378f7' },
              { offset: 0.7, color: '#2378f7' },
              { offset: 1, color: '#83bff6' }
            ])
          }
        }
      }]
    }
  } else if (chartType.value === 'pie') {
    option = {
      title: {
        text: '店铺营业额占比',
        left: 'center',
        textStyle: {
          fontSize: 16,
          fontWeight: 'bold'
        }
      },
      tooltip: {
        trigger: 'item',
        formatter: '{a} <br/>{b}: ¥{c} ({d}%)'
      },
      legend: {
        orient: 'vertical',
        left: 'left',
        top: 'middle'
      },
      series: [{
        name: '营业额',
        type: 'pie',
        radius: ['40%', '70%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 10,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '30',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: chartData.value.map(item => ({
          value: item.value,
          name: item.name,
          orderCount: item.orderCount,
          avgOrderValue: item.avgOrderValue
        }))
      }]
    }
  }
  
  chartInstance.value.setOption(option)
}

// 图表类型切换
const handleChartTypeChange = () => {
  nextTick(() => {
    updateChart()
  })
}

// 导出图表
const exportChart = () => {
  if (!chartInstance.value) return
  
  const url = chartInstance.value.getDataURL({
    type: 'png',
    backgroundColor: '#fff'
  })
  
  const link = document.createElement('a')
  link.href = url
  link.download = `营业额图表_${new Date().toISOString().split('T')[0]}.png`
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  ElMessage.success('图表导出成功')
}

// 更新分页数据
const updatePagedData = () => {
  if (!allRevenueData.value) {
    revenueData.value = []
    return
  }
  
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  revenueData.value = allRevenueData.value.slice(start, end)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  updatePagedData()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  updatePagedData()
}

// 跳转到指定页码
const goToPage = (page) => {
  currentPage.value = page
  queryRevenue()
}

// 获取分页信息
const getPaginationInfo = () => {
  return {
    currentPage: currentPage.value,
    pageSize: pageSize.value,
    total: total.value,
    totalPages: Math.ceil(total.value / pageSize.value)
  }
}

// 每页显示数量选择器
const pageSizeOptions = [10, 20, 50, 100]

// 重置到第一页
const resetToFirstPage = () => {
  currentPage.value = 1
  queryRevenue()
}

// 跳转到最后一页
const goToLastPage = () => {
  const totalPages = Math.ceil(total.value / pageSize.value)
  currentPage.value = totalPages
  queryRevenue()
}

// 初始加载时设置默认日期为今天，默认月份为当月，并自动执行查询
onMounted(() => {
  const now = new Date()
  searchForm.date = now
  searchForm.month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  // 自动执行查询，展示初始数据
  queryRevenue()
})

// 监听数据变化，更新图表
watch(revenueData, () => {
  nextTick(() => {
    if (revenueData.value && revenueData.value.length > 0) {
      if (!chartInstance.value) {
        initChart()
      } else {
        updateChart()
      }
    }
  })
}, { deep: true })
</script>

<style scoped>
.revenue-query {
  padding: 20px;
}

.card-header {
  margin-bottom: 20px;
}

.card-header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.revenue-amount {
  font-weight: bold;
  color: #67C23A;
}

.el-input-group__append,
.el-input-group__prepend {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
}

.el-button--primary {
  background-color: #409eff;
  border-color: #409eff;
}

.el-button--primary:hover {
  background-color: #66b1ff;
  border-color: #66b1ff;
}

.el-table {
  margin-top: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.el-table__header th {
  background-color: #f5f7fa;
  font-weight: 500;
}

.el-table__row:hover {
  background-color: #f5f7fa;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
}

.loading-text {
  margin-top: 10px;
  color: #606266;
}

.revenue-amount {
  color: #f56c6c;
  font-weight: bold;
}

.no-data {
  padding: 60px 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-descriptions {
  margin-bottom: 20px;
}

.order-preview {
  margin-top: 20px;
}

/* 图表相关样式 */
.chart-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.chart-container {
  width: 100%;
  height: 500px;
  margin-bottom: 30px;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.chart {
  width: 100%;
  height: 100%;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  position: relative;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-content {
  padding: 20px;
  position: relative;
  z-index: 1;
}

.stat-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 32px;
  opacity: 0.3;
}

.data-table-collapse {
  margin-bottom: 20px;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .search-form {
    display: flex;
    flex-direction: column;
  }
  
  .search-form .el-form-item {
    margin-right: 0;
    margin-bottom: 10px;
    width: 100%;
  }
  
  .chart-container {
    height: 400px;
    padding: 10px;
  }
  
  .stats-cards {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 15px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .chart-controls {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
}
</style>