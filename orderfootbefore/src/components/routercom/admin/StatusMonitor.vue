<template>
  <div class="status-monitor-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>系统状态监控</span>
          <el-button type="primary" size="small" @click="refreshStatus">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>
      
      <!-- 系统状态概览 -->
      <div class="overview-section">
        <h2>系统概览</h2>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Timer /></el-icon>
                  <span>系统运行时间</span>
                </div>
              </template>
              <div class="statistic-content">
                <el-statistic
                  :value="formattedUptime"
                  :precision="0"
                  :value-style="{ color: '#3f8600', fontSize: '24px' }"
                >
                </el-statistic>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><User /></el-icon>
                  <span>当前用户数量</span>
                </div>
              </template>
              <div class="statistic-content">
                <el-statistic
                  :value="status.userCount"
                  :value-style="{ color: '#1890ff', fontSize: '24px' }"
                >
                </el-statistic>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Document /></el-icon>
                  <span>内存使用率</span>
                </div>
              </template>
              <div class="statistic-content">
                <el-statistic
                  :value="status.memory.usage"
                  :precision="1"
                  suffix="%"
                  :value-style="{ color: getColorByUsage(status.memory.usage), fontSize: '24px' }"
                >
                </el-statistic>
              </div>
            </el-card>
          </el-col>
          <el-col :span="6">
            <el-card shadow="hover">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Document /></el-icon>
                  <span>磁盘使用率</span>
                </div>
              </template>
              <div class="statistic-content">
                <el-statistic
                  :value="status.disk.usage"
                  :precision="1"
                  suffix="%"
                  :value-style="{ color: getColorByUsage(status.disk.usage), fontSize: '24px' }"
                >
                </el-statistic>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 详细状态信息 -->
      <div class="detail-section">
        <el-row :gutter="20">
          <!-- 第一行：内存、磁盘 -->
          <el-col :span="12">
            <!-- 内存使用情况 -->
            <el-card shadow="hover" class="detail-card">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Document /></el-icon>
                  <span>内存使用情况</span>
                </div>
              </template>
              <div class="progress-content">
                <el-progress
                  :percentage="status.memory.usage"
                  :status="getStatusByUsage(status.memory.usage)"
                  :format="(percentage) => `${percentage.toFixed(1)}%`"
                  :stroke-width="16"
                ></el-progress>
                <div class="memory-detail">
                  <span>已使用：{{ formatSize(status.memory.used) }}</span>
                  <span>可用：{{ formatSize(status.memory.available) }}</span>
                  <span>总内存：{{ formatSize(status.memory.total) }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <!-- 磁盘使用情况 -->
            <el-card shadow="hover" class="detail-card">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Document /></el-icon>
                  <span>磁盘使用情况</span>
                </div>
              </template>
              <div class="progress-content">
                <el-progress
                  :percentage="status.disk.usage"
                  :status="getStatusByUsage(status.disk.usage)"
                  :format="(percentage) => `${percentage.toFixed(1)}%`"
                  :stroke-width="16"
                ></el-progress>
                <div class="disk-detail">
                  <span>已使用：{{ formatSize(status.disk.used) }}</span>
                  <span>可用：{{ formatSize(status.disk.available) }}</span>
                  <span>总容量：{{ formatSize(status.disk.total) }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 第二行：JVM内存、数据库连接池 -->
          <el-col :span="12">
            <!-- JVM内存使用情况 -->
            <el-card shadow="hover" class="detail-card">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Document /></el-icon>
                  <span>JVM内存使用情况</span>
                </div>
              </template>
              <div class="progress-content">
                <div class="jvm-memory">
                  <h3>堆内存</h3>
                  <el-progress
                    :percentage="status.jvmMemory.heapUsage"
                    :status="getStatusByUsage(status.jvmMemory.heapUsage)"
                    :format="(percentage) => `${percentage.toFixed(1)}%`"
                    :stroke-width="12"
                  ></el-progress>
                  <div class="memory-detail">
                    <span>已使用：{{ formatSize(status.jvmMemory.heapUsed) }}</span>
                    <span>总内存：{{ formatSize(status.jvmMemory.heapTotal) }}</span>
                  </div>
                  
                  <h3>非堆内存</h3>
                  <el-progress
                    :percentage="status.jvmMemory.nonHeapUsage"
                    :status="getStatusByUsage(status.jvmMemory.nonHeapUsage)"
                    :format="(percentage) => `${percentage.toFixed(1)}%`"
                    :stroke-width="12"
                    color="#722ed1"
                  ></el-progress>
                  <div class="memory-detail">
                    <span>已使用：{{ formatSize(status.jvmMemory.nonHeapUsed) }}</span>
                    <span>总内存：{{ formatSize(status.jvmMemory.nonHeapTotal) }}</span>
                  </div>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <!-- 数据库连接池状态 -->
            <el-card shadow="hover" class="detail-card">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><Connection /></el-icon>
                  <span>数据库连接池</span>
                </div>
              </template>
              <div class="progress-content">
                <el-progress
                  :percentage="status.dbPool.usage"
                  :status="getStatusByUsage(status.dbPool.usage)"
                  :format="(percentage) => `${percentage.toFixed(1)}%`"
                  :stroke-width="16"
                  color="#faad14"
                ></el-progress>
                <div class="db-detail">
                  <span>活跃连接：{{ status.dbPool.activeConnections }} / {{ status.dbPool.maxConnections }}</span>
                  <span>空闲连接：{{ status.dbPool.idleConnections }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
        
        <el-row :gutter="20" style="margin-top: 20px;">
          <!-- 第三行：API请求统计 -->
          <el-col :span="24">
            <!-- API请求统计 -->
            <el-card shadow="hover" class="detail-card">
              <template #header>
                <div class="card-header-small">
                  <el-icon class="card-icon"><DataAnalysis /></el-icon>
                  <span>API请求统计</span>
                </div>
              </template>
              <div class="api-stats">
                <el-row :gutter="10">
                  <el-col :span="8">
                    <div class="stat-item">
                      <div class="stat-label">总请求数</div>
                      <div class="stat-value">{{ status.apiStats.totalRequests }}</div>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="stat-item">
                      <div class="stat-label">成功请求</div>
                      <div class="stat-value success">{{ status.apiStats.successRequests }}</div>
                    </div>
                  </el-col>
                  <el-col :span="8">
                    <div class="stat-item">
                      <div class="stat-label">失败请求</div>
                      <div class="stat-value error">{{ status.apiStats.failedRequests }}</div>
                    </div>
                  </el-col>
                  <el-col :span="24">
                    <div class="stat-item full-width">
                      <div class="stat-label">平均响应时间</div>
                      <div class="stat-value">{{ status.apiStats.avgResponseTime.toFixed(2) }} ms</div>
                    </div>
                  </el-col>
                </el-row>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      
      <!-- 监控时间 -->
      <div class="monitor-time">
        <el-tag size="small" type="info">
          最后监控时间：{{ formattedMonitorTime }}
        </el-tag>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElMessage } from 'element-plus';
import { 
  Refresh, 
  Timer, 
  DataAnalysis, 
  Connection,
  Document,
  User 
} from '@element-plus/icons-vue';
import * as StatusApi from '../../../../api/StatusApi';

// 状态数据
const status = ref({
  uptime: 0,
  userCount: 0,
  memory: {
    total: 0,
    used: 0,
    available: 0,
    usage: 0
  },
  disk: {
    total: 0,
    used: 0,
    available: 0,
    usage: 0
  },
  jvmMemory: {
    heapTotal: 0,
    heapUsed: 0,
    nonHeapTotal: 0,
    nonHeapUsed: 0,
    heapUsage: 0,
    nonHeapUsage: 0
  },
  dbPool: {
    activeConnections: 0,
    maxConnections: 0,
    idleConnections: 0,
    usage: 0
  },
  apiStats: {
    totalRequests: 0,
    successRequests: 0,
    failedRequests: 0,
    avgResponseTime: 0
  },
  monitorTime: new Date()
});

// 计算属性：格式化运行时间
const formattedUptime = computed(() => {
  const uptime = status.value.uptime;
  const days = Math.floor(uptime / (24 * 3600));
  const hours = Math.floor((uptime % (24 * 3600)) / 3600);
  const minutes = Math.floor((uptime % 3600) / 60);
  const seconds = Math.floor(uptime % 60);
  
  if (days > 0) {
    return `${days}天 ${hours}小时`;
  } else if (hours > 0) {
    return `${hours}小时 ${minutes}分钟`;
  } else if (minutes > 0) {
    return `${minutes}分钟 ${seconds}秒`;
  } else {
    return `${seconds}秒`;
  }
});

// 计算属性：格式化监控时间
const formattedMonitorTime = computed(() => {
  return new Date(status.value.monitorTime).toLocaleString('zh-CN');
});

// 根据使用率获取颜色
const getColorByUsage = (usage) => {
  if (usage < 30) return '#3f8600';
  if (usage < 70) return '#faad14';
  return '#cf1322';
};

// 根据使用率获取进度条状态
const getStatusByUsage = (usage) => {
  if (usage < 30) return 'success';
  if (usage < 70) return 'warning';
  return 'exception';
};

// 格式化大小
const formatSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B';
  
  const k = 1024;
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 刷新状态
const refreshStatus = async () => {
  try {
    console.log('开始获取系统状态...');
    const response = await StatusApi.getSystemStatus();
    console.log('获取系统状态成功，响应数据:', response);
    
    // 检查响应是否存在
    if (response && response.data && response.data.status) {
      const newStatus = response.data.status;
      
      // 确保数据正确，避免出现过小的数值
      if (newStatus.memory && newStatus.memory.total < 1024) {
        console.error('内存数据异常，可能单位转换错误:', newStatus.memory.total);
        // 假设返回的是MB，转换为字节
        newStatus.memory.total *= 1024 * 1024;
        newStatus.memory.used *= 1024 * 1024;
        newStatus.memory.available *= 1024 * 1024;
      }
      
      if (newStatus.disk && newStatus.disk.total < 1024) {
        console.error('磁盘数据异常，可能单位转换错误:', newStatus.disk.total);
        // 假设返回的是GB，转换为字节
        newStatus.disk.total *= 1024 * 1024 * 1024;
        newStatus.disk.used *= 1024 * 1024 * 1024;
        newStatus.disk.available *= 1024 * 1024 * 1024;
      }
      
      status.value = newStatus;
      status.value.monitorTime = new Date();
    } else {
      console.error('API返回数据格式不正确:', response);
      ElMessage.warning('状态数据格式不正确');
    }
    
    // 获取用户数量
    try {
      const userCountResponse = await StatusApi.getUserCount();
      console.log('获取用户数量成功，响应数据:', userCountResponse);
      
      if (userCountResponse && userCountResponse.code === 200 && userCountResponse.data) {
        status.value.userCount = userCountResponse.data.userCount.totalUsers;
        console.log('用户数量:', status.value.userCount);
      }
    } catch (error) {
      console.error('获取用户数量失败:', error);
    }
    
  } catch (error) {
    console.error('获取系统状态失败:', error);
    ElMessage.error('获取系统状态失败');
  }
};

// 页面加载时获取状态
onMounted(() => {
  refreshStatus();
  // 定时刷新状态（每5秒）
  setInterval(refreshStatus, 5000);
});
</script>

<style scoped>
.status-monitor-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.overview-section {
  margin-bottom: 20px;
}

.overview-section h2 {
  margin-bottom: 15px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.detail-section {
  margin-top: 20px;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header-small {
  display: flex;
  align-items: center;
  gap: 8px;
}

.card-icon {
  font-size: 18px;
  color: #409eff;
}

.statistic-content {
  text-align: center;
  padding: 10px 0;
}

.progress-content {
  padding: 10px 0;
}

.memory-detail, .disk-detail, .db-detail {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 12px;
  color: #606266;
}

.jvm-memory {
  margin: 10px 0;
}

.jvm-memory h3 {
  font-size: 14px;
  margin: 10px 0 5px 0;
  color: #333;
}

.api-stats {
  padding: 10px 0;
}

.stat-item {
  text-align: center;
  padding: 10px;
  background-color: #f0f2f5;
  border-radius: 8px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.stat-value.success {
  color: #67c23a;
}

.stat-value.error {
  color: #f56c6c;
}

.stat-item.full-width {
  margin-top: 10px;
}

.monitor-time {
  margin-top: 20px;
  text-align: right;
}
</style>
