<template>
  <div class="feedback-management">
    <div class="page-header">
      <h2>用户反馈管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="exportFeedback">导出反馈</el-button>
      </div>
    </div>

    <div class="search-filter">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索用户/内容" 
            prefix-icon="el-icon-search"
            @keyup.enter="searchFeedback"
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="typeFilter" placeholder="反馈类型" @change="filterByType">
            <el-option label="全部类型" value="" />
            <el-option label="建议" value="suggestion" />
            <el-option label="投诉" value="complaint" />
            <el-option label="咨询" value="inquiry" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="处理状态" @change="filterByStatus">
            <el-option label="全部状态" value="" />
            <el-option label="未处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已处理" value="completed" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="searchFeedback">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="feedback-table">
      <el-table 
        :data="feedbackList" 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="反馈ID" width="120" />
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeType(row.type)">
              {{ getTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="反馈内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="contact" label="联系方式" width="150" />
        <el-table-column prop="createTime" label="提交时间" width="160" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewFeedback(row)">查看</el-button>
            <el-button size="small" type="primary" @click="processFeedback(row)">处理</el-button>
            <el-button size="small" type="danger" @click="deleteFeedback(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 查看反馈详情对话框 -->
    <el-dialog title="反馈详情" v-model="viewDialogVisible" width="600px">
      <div class="feedback-detail">
        <div class="detail-item">
          <span class="label">反馈ID：</span>
          <span class="value">{{ currentFeedback.id }}</span>
        </div>
        <div class="detail-item">
          <span class="label">用户：</span>
          <span class="value">{{ currentFeedback.userName }}</span>
        </div>
        <div class="detail-item">
          <span class="label">类型：</span>
          <span class="value">
            <el-tag :type="getTypeType(currentFeedback.type)">
              {{ getTypeText(currentFeedback.type) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item">
          <span class="label">联系方式：</span>
          <span class="value">{{ currentFeedback.contact }}</span>
        </div>
        <div class="detail-item">
          <span class="label">提交时间：</span>
          <span class="value">{{ currentFeedback.createTime }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="getStatusType(currentFeedback.status)">
              {{ getStatusText(currentFeedback.status) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item full-width">
          <span class="label">反馈内容：</span>
          <div class="content-value">{{ currentFeedback.content }}</div>
        </div>
        <div class="detail-item full-width" v-if="currentFeedback.reply">
          <span class="label">回复内容：</span>
          <div class="content-value">{{ currentFeedback.reply }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="processFeedback(currentFeedback)">处理</el-button>
      </template>
    </el-dialog>

    <!-- 处理反馈对话框 -->
    <el-dialog title="处理反馈" v-model="processDialogVisible" width="600px">
      <el-form :model="processForm" label-width="100px">
        <el-form-item label="反馈内容">
          <div class="feedback-content">{{ processForm.content }}</div>
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="processForm.status" placeholder="请选择处理状态">
            <el-option label="处理中" value="processing" />
            <el-option label="已处理" value="completed" />
          </el-select>
        </el-form-item>
        <el-form-item label="回复内容">
          <el-input 
            v-model="processForm.reply" 
            type="textarea" 
            :rows="4"
            placeholder="请输入回复内容"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmProcess">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';

const searchKeyword = ref('');
const typeFilter = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const feedbackList = ref([
  { id: 1, userName: '张三', type: 'suggestion', content: '建议增加更多素食选项', contact: '13800138001', createTime: '2024-01-15 10:30:00', status: 'pending' },
  { id: 2, userName: '李四', type: 'complaint', content: '配送速度太慢，等了40分钟', contact: '13800138002', createTime: '2024-01-15 11:15:00', status: 'processing', reply: '已联系配送员，会改进配送效率' },
  { id: 3, userName: '王五', type: 'inquiry', content: '请问营业时间到几点？', contact: '13800138003', createTime: '2024-01-15 12:00:00', status: 'completed', reply: '营业到晚上10点' },
  { id: 4, userName: '赵六', type: 'other', content: '菜品质量很好，继续加油', contact: '13800138004', createTime: '2024-01-15 13:20:00', status: 'completed' },
  { id: 5, userName: '钱七', type: 'suggestion', content: '希望增加外卖包装选项', contact: '13800138005', createTime: '2024-01-15 14:45:00', status: 'pending' }
]);

const viewDialogVisible = ref(false);
const currentFeedback = ref({});

const processDialogVisible = ref(false);
const processForm = ref({
  id: null,
  content: '',
  status: '',
  reply: ''
});

// 获取类型类型
const getTypeType = (type) => {
  if (type === 'suggestion') return 'primary';
  if (type === 'complaint') return 'danger';
  if (type === 'inquiry') return 'warning';
  if (type === 'other') return 'info';
  return 'info';
};

// 获取类型文本
const getTypeText = (type) => {
  const typeMap = {
    suggestion: '建议',
    complaint: '投诉',
    inquiry: '咨询',
    other: '其他'
  };
  return typeMap[type] || type;
};

// 获取状态类型
const getStatusType = (status) => {
  if (status === 'pending') return 'warning';
  if (status === 'processing') return 'primary';
  if (status === 'completed') return 'success';
  return 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    pending: '未处理',
    processing: '处理中',
    completed: '已处理'
  };
  return statusMap[status] || status;
};

// 搜索反馈
const searchFeedback = () => {
  console.log('搜索反馈:', searchKeyword.value);
  // 这里应该调用API进行搜索
};

// 按类型筛选
const filterByType = () => {
  console.log('按类型筛选:', typeFilter.value);
};

// 按状态筛选
const filterByStatus = () => {
  console.log('按状态筛选:', statusFilter.value);
};

// 查看反馈
const viewFeedback = (row) => {
  currentFeedback.value = { ...row };
  viewDialogVisible.value = true;
};

// 处理反馈
const processFeedback = (row) => {
  processForm.value = {
    id: row.id,
    content: row.content,
    status: row.status,
    reply: row.reply || ''
  };
  processDialogVisible.value = true;
};

// 确认处理
const confirmProcess = () => {
  console.log('确认处理:', processForm.value);
  // 这里应该调用API处理反馈
  ElMessage.success('反馈处理成功');
  processDialogVisible.value = false;
  
  // 更新列表中的状态
  const index = feedbackList.value.findIndex(item => item.id === processForm.value.id);
  if (index !== -1) {
    feedbackList.value[index].status = processForm.value.status;
    feedbackList.value[index].reply = processForm.value.reply;
  }
};

// 删除反馈
const deleteFeedback = (row) => {
  ElMessageBox.confirm(`确定要删除反馈 "${row.id}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用API删除反馈
    feedbackList.value = feedbackList.value.filter(item => item.id !== row.id);
    ElMessage.success('反馈删除成功');
  });
};

// 导出反馈
const exportFeedback = () => {
  console.log('导出反馈');
  // 这里应该调用API导出反馈数据
  ElMessage.success('反馈导出功能开发中');
};

// 处理多选
const handleSelectionChange = (selection) => {
  console.log('选中项:', selection);
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  // 重新加载数据
};

const handleCurrentChange = (page) => {
  currentPage.value = page;
  // 重新加载数据
};

onMounted(() => {
  // 初始化数据
});
</script>

<style scoped>
.feedback-management {
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
}

.search-filter {
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-table {
  margin-bottom: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.pagination {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.feedback-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item.full-width {
  flex-direction: column;
}

.detail-item .label {
  width: 100px;
  font-weight: bold;
  color: #666;
}

.detail-item .value {
  flex: 1;
  color: #333;
}

.detail-item .content-value {
  flex: 1;
  color: #333;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-all;
}

.feedback-content {
  background: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
  line-height: 1.6;
}
</style>
