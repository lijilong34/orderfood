<template>
  <div class="log-management-container">
    <h2>日志管理</h2>
    
    <!-- 搜索条件 -->
    <el-card shadow="hover" class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable></el-input>
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择操作类型" clearable>
            <el-option label="登录" value="登录"></el-option>
            <el-option label="新增" value="新增"></el-option>
            <el-option label="修改" value="修改"></el-option>
            <el-option label="删除" value="删除"></el-option>
            <el-option label="查询" value="查询"></el-option>
            <el-option label="导出" value="导出"></el-option>
            <el-option label="导入" value="导入"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="操作结果">
          <el-select v-model="searchForm.result" placeholder="请选择操作结果" clearable>
            <el-option label="成功" value="成功"></el-option>
            <el-option label="失败" value="失败"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="操作时间">
          <el-date-picker
            v-model="searchForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
          ></el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchLogs">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 日志列表 -->
    <el-card shadow="hover" class="log-list-card">
      <template #header>
        <div class="card-header">
          <span>操作日志列表</span>
          <el-button type="danger" @click="handleBatchDelete" :disabled="selectedLogIds.length === 0">
            <el-icon><Delete /></el-icon>
            批量删除
          </el-button>
        </div>
      </template>
      
      <el-table
        v-loading="loading"
        :data="logList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
        stripe
        border
      >
        <el-table-column type="selection" width="55"></el-table-column>
        <el-table-column prop="logId" label="日志ID" width="100"></el-table-column>
        <el-table-column prop="username" label="操作人" width="120"></el-table-column>
        <el-table-column prop="operationType" label="操作类型" width="120"></el-table-column>
        <el-table-column prop="operationContent" label="操作内容" min-width="200"></el-table-column>
        <el-table-column prop="ipAddress" label="IP地址" width="150"></el-table-column>
        <el-table-column prop="result" label="操作结果" width="100">
          <template #default="scope">
            <el-tag :type="scope.row.result === '成功' ? 'success' : 'danger'">
              {{ scope.row.result }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="operationTime" label="操作时间" width="200">
          <template #default="scope">
            {{ formatDateTime(scope.row.operationTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="responseTime" label="响应时间(ms)" width="120"></el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewLogDetail(scope.row.logId)">
              <el-icon><View /></el-icon>
              详情
            </el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.logId)">
              <el-icon><Delete /></el-icon>
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页信息 -->
      <div class="pagination-info" v-if="pagination.total > 0">
        <span class="info-text">
          显示第 {{ (pagination.currentPage - 1) * pagination.pageSize + 1 }} - 
          {{ Math.min(pagination.currentPage * pagination.pageSize, pagination.total) }} 条，
          共 {{ pagination.total }} 条记录
        </span>
      </div>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :hide-on-single-page="false"
        ></el-pagination>
      </div>
    </el-card>
    
    <!-- 日志详情对话框 -->
    <teleport to="body">
      <el-dialog
        v-model="dialogVisible"
        title="日志详情"
        width="60%"
        :close-on-click-modal="false"
      >
        <el-descriptions :column="1" border>
          <el-descriptions-item label="日志ID">{{ currentLog.logId }}</el-descriptions-item>
          <el-descriptions-item label="操作人">{{ currentLog.username }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">{{ currentLog.operationType }}</el-descriptions-item>
          <el-descriptions-item label="操作内容">{{ currentLog.operationContent }}</el-descriptions-item>
          <el-descriptions-item label="IP地址">{{ currentLog.ipAddress }}</el-descriptions-item>
          <el-descriptions-item label="操作结果">
            <el-tag :type="currentLog.result === '成功' ? 'success' : 'danger'">
              {{ currentLog.result }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="错误信息" v-if="currentLog.result === '失败' && currentLog.errorMessage">
            <pre style="margin: 0; background: #f5f7fa; padding: 10px; border-radius: 4px;">{{ currentLog.errorMessage }}</pre>
          </el-descriptions-item>
          <el-descriptions-item label="请求URL">{{ currentLog.requestUrl }}</el-descriptions-item>
          <el-descriptions-item label="请求方法">{{ currentLog.requestMethod }}</el-descriptions-item>
          <el-descriptions-item label="响应时间">{{ currentLog.responseTime }} ms</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ formatDateTime(currentLog.operationTime) }}</el-descriptions-item>
        </el-descriptions>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">关闭</el-button>
          </span>
        </template>
      </el-dialog>
    </teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import {
  Search,
  Refresh,
  Delete,
  View
} from '@element-plus/icons-vue';
import * as LogApi from '../../../../api/LogApi';

// 搜索表单
const searchForm = reactive({
  username: '',
  operationType: '',
  result: '',
  dateRange: []
});

// 分页信息
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});

// 日志列表
const logList = ref([]);
// 加载状态
const loading = ref(false);
// 选中的日志ID
const selectedLogIds = ref([]);
// 对话框显示状态
const dialogVisible = ref(false);
// 当前查看的日志
const currentLog = ref({});

// 计算属性
const totalPages = computed(() => {
  return Math.ceil(pagination.total / pagination.pageSize);
});

// 格式化日期时间
const formatDateTime = (dateTime) => {
  if (!dateTime) return '';
  return new Date(dateTime).toLocaleString('zh-CN');
};

// 搜索日志
const searchLogs = () => {
  pagination.currentPage = 1;
  getLogList();
};

// 重置搜索条件
const resetSearch = () => {
  Object.assign(searchForm, {
    username: '',
    operationType: '',
    result: '',
    dateRange: []
  });
  pagination.currentPage = 1;
  getLogList();
};

// 获取日志列表
const getLogList = async () => {
  loading.value = true;
  try {
    // 构建查询参数
    const params = {
      username: searchForm.username,
      operationType: searchForm.operationType,
      result: searchForm.result,
      pageNum: pagination.currentPage,
      pageSize: pagination.pageSize
    };
    
    console.log('发起分页请求:', {
      pageNum: params.pageNum,
      pageSize: params.pageSize,
      currentPagination: {
        currentPage: pagination.currentPage,
        pageSize: pagination.pageSize,
        total: pagination.total
      }
    });
    
    // 处理日期范围
    if (searchForm.dateRange && searchForm.dateRange.length === 2) {
      params.startTime = searchForm.dateRange[0];
      params.endTime = searchForm.dateRange[1];
    }
    
    const response = await LogApi.getLogList(params);
    console.log('API响应:', response);
    
    if (response.code === 200) {
      // 解析后端返回的分页数据
      const logData = response.data.logList;
      
      console.log('后端返回的分页数据:', logData);
      
      if (logData) {
        // 设置日志列表
        logList.value = Array.isArray(logData.rows) ? logData.rows : [];
        
        // 设置分页信息
        pagination.total = Number(logData.total) || 0;
        pagination.currentPage = Number(logData.pageNum) || 1;
        pagination.pageSize = Number(logData.pageSize) || 10;
        
        console.log('设置后的分页状态:', {
          total: pagination.total,
          currentPage: pagination.currentPage,
          pageSize: pagination.pageSize,
          listLength: logList.value.length
        });
        
        console.log('分页数据:', {
          listCount: logList.value.length,
          total: pagination.total,
          currentPage: pagination.currentPage,
          pageSize: pagination.pageSize,
          totalPages: logData.pages
        });
      } else {
        console.warn('后端返回的数据中没有 logList 字段');
        logList.value = [];
        pagination.total = 0;
      }
    } else {
      console.error('获取日志列表失败:', response);
      ElMessage.error(response.message || '获取日志列表失败');
      logList.value = [];
      pagination.total = 0;
    }
  } catch (error) {
    console.error('获取日志列表失败:', error);
    ElMessage.error('获取日志列表失败');
  } finally {
    loading.value = false;
  }
};

// 查看日志详情
const viewLogDetail = async (logId) => {
  try {
    const response = await LogApi.getLogById(logId);
    if (response.code === 200) {
      currentLog.value = response.data.log || {};
      dialogVisible.value = true;
    } else {
      ElMessage.error(response.message || '获取日志详情失败');
    }
  } catch (error) {
    console.error('获取日志详情失败:', error);
    ElMessage.error('获取日志详情失败');
  }
};

// 处理选择变化
const handleSelectionChange = (selection) => {
  selectedLogIds.value = selection.map(item => item.logId);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pagination.pageSize = size;
  pagination.currentPage = 1; // 重置到第一页
  getLogList();
};

// 处理当前页码变化
const handleCurrentChange = (currentPage) => {
  pagination.currentPage = currentPage;
  getLogList();
};

// 删除日志
const handleDelete = async (logId) => {
  try {
    await ElMessageBox.confirm('确定要删除这条日志吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await LogApi.deleteLog(logId);
    if (response.code === 200) {
      ElMessage.success('删除日志成功');
      getLogList();
    } else {
      ElMessage.error(response.message || '删除日志失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除日志失败:', error);
      ElMessage.error('删除日志失败');
    }
  }
};

// 批量删除日志
const handleBatchDelete = async () => {
  if (selectedLogIds.value.length === 0) {
    ElMessage.warning('请选择要删除的日志');
    return;
  }
  
  try {
    await ElMessageBox.confirm(`确定要删除选中的${selectedLogIds.value.length}条日志吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    });
    
    const response = await LogApi.batchDeleteLog(selectedLogIds.value);
    if (response.code === 200) {
      ElMessage.success('批量删除日志成功');
      getLogList();
      selectedLogIds.value = [];
    } else {
      ElMessage.error(response.message || '批量删除日志失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('批量删除日志失败:', error);
      ElMessage.error('批量删除日志失败');
    }
  }
};

// 页面加载时获取日志列表
onMounted(() => {
  // 确保分页参数初始值正确
  console.log('组件初始化完成，开始获取日志列表');
  console.log('初始分页状态:', {
    currentPage: pagination.currentPage,
    pageSize: pagination.pageSize,
    total: pagination.total
  });
  getLogList();
});

// 监听分页状态变化
const updatePaginationInfo = () => {
  console.log('分页状态变化:', {
    currentPage: pagination.currentPage,
    pageSize: pagination.pageSize,
    total: pagination.total,
    logListLength: logList.value.length
  });
};
</script>

<style scoped>
.log-management-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination-info {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 10px;
  padding: 10px 0;
}

.info-text {
  font-size: 14px;
  color: #606266;
}

.pagination-container {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style>