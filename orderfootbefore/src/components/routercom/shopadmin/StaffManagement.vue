<template>
  <div class="staff-management">
    <div class="page-header">
      <h2>员工管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="addStaff">添加员工</el-button>
        <el-button @click="exportStaff">导出员工</el-button>
      </div>
    </div>

    <div class="search-filter">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="searchKeyword" 
            placeholder="搜索员工姓名/工号" 
            prefix-icon="el-icon-search"
            @keyup.enter="searchStaff"
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="positionFilter" placeholder="选择职位" @change="filterByPosition">
            <el-option label="全部职位" value="" />
            <el-option label="店长" value="manager" />
            <el-option label="厨师" value="chef" />
            <el-option label="服务员" value="waiter" />
            <el-option label="收银员" value="cashier" />
            <el-option label="配送员" value="delivery" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="statusFilter" placeholder="员工状态" @change="filterByStatus">
            <el-option label="全部状态" value="" />
            <el-option label="在职" value="active" />
            <el-option label="离职" value="inactive" />
            <el-option label="休假" value="leave" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-button type="primary" @click="searchStaff">搜索</el-button>
        </el-col>
      </el-row>
    </div>

    <div class="staff-table">
      <el-table 
        :data="staffList" 
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="staffId" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :src="row.avatar" size="small" />
          </template>
        </el-table-column>
        <el-table-column prop="position" label="职位" width="120">
          <template #default="{ row }">
            <el-tag :type="getPositionType(row.position)">
              {{ getPositionText(row.position) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" width="200" />
        <el-table-column prop="hireDate" label="入职日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="viewStaff(row)">查看</el-button>
            <el-button size="small" @click="editStaff(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteStaff(row)">删除</el-button>
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

    <!-- 添加/编辑员工对话框 -->
    <el-dialog 
      :title="dialogTitle" 
      v-model="dialogVisible" 
      width="600px"
      @close="resetForm"
    >
      <el-form :model="staffForm" :rules="formRules" ref="staffFormRef" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="员工姓名" prop="name">
              <el-input v-model="staffForm.name" placeholder="请输入员工姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="工号" prop="staffId">
              <el-input v-model="staffForm.staffId" placeholder="请输入工号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="职位" prop="position">
              <el-select v-model="staffForm.position" placeholder="请选择职位">
                <el-option label="店长" value="manager" />
                <el-option label="厨师" value="chef" />
                <el-option label="服务员" value="waiter" />
                <el-option label="收银员" value="cashier" />
                <el-option label="配送员" value="delivery" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="staffForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="staffForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入职日期" prop="hireDate">
              <el-date-picker
                v-model="staffForm.hireDate"
                type="date"
                placeholder="选择入职日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="密码" prop="password">
              <el-input 
                v-model="staffForm.password" 
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                show-password
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input 
                v-model="staffForm.confirmPassword" 
                :type="showPassword ? 'text' : 'password'"
                placeholder="请确认密码"
                show-password
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="staffForm.avatar" :src="staffForm.avatar" class="avatar" />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveStaff">确定</el-button>
      </template>
    </el-dialog>

    <!-- 查看员工详情对话框 -->
    <el-dialog title="员工详情" v-model="viewDialogVisible" width="600px">
      <div class="staff-detail">
        <div class="detail-item">
          <span class="label">工号：</span>
          <span class="value">{{ currentStaff.staffId }}</span>
        </div>
        <div class="detail-item">
          <span class="label">姓名：</span>
          <span class="value">{{ currentStaff.name }}</span>
        </div>
        <div class="detail-item">
          <span class="label">职位：</span>
          <span class="value">
            <el-tag :type="getPositionType(currentStaff.position)">
              {{ getPositionText(currentStaff.position) }}
            </el-tag>
          </span>
        </div>
        <div class="detail-item">
          <span class="label">联系电话：</span>
          <span class="value">{{ currentStaff.phone }}</span>
        </div>
        <div class="detail-item">
          <span class="label">邮箱：</span>
          <span class="value">{{ currentStaff.email }}</span>
        </div>
        <div class="detail-item">
          <span class="label">入职日期：</span>
          <span class="value">{{ currentStaff.hireDate }}</span>
        </div>
        <div class="detail-item">
          <span class="label">状态：</span>
          <span class="value">
            <el-tag :type="getStatusType(currentStaff.status)">
              {{ getStatusText(currentStaff.status) }}
            </el-tag>
          </span>
        </div>
      </div>
      <template #footer>
        <el-button @click="viewDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';

const searchKeyword = ref('');
const positionFilter = ref('');
const statusFilter = ref('');
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

const staffList = ref([
  { id: 1, staffId: 'ST001', name: '张三', position: 'manager', phone: '13800138001', email: 'zhangsan@example.com', hireDate: '2023-01-15', status: 'active', avatar: '' },
  { id: 2, staffId: 'ST002', name: '李四', position: 'chef', phone: '13800138002', email: 'lisi@example.com', hireDate: '2023-02-20', status: 'active', avatar: '' },
  { id: 3, staffId: 'ST003', name: '王五', position: 'waiter', phone: '13800138003', email: 'wangwu@example.com', hireDate: '2023-03-10', status: 'active', avatar: '' },
  { id: 4, staffId: 'ST004', name: '赵六', position: 'cashier', phone: '13800138004', email: 'zhaoliu@example.com', hireDate: '2023-04-05', status: 'leave', avatar: '' },
  { id: 5, staffId: 'ST005', name: '钱七', position: 'delivery', phone: '13800138005', email: 'qianqi@example.com', hireDate: '2023-05-12', status: 'active', avatar: '' }
]);

const dialogVisible = ref(false);
const dialogTitle = ref('添加员工');
const staffForm = ref({
  id: null,
  staffId: '',
  name: '',
  position: '',
  phone: '',
  email: '',
  hireDate: '',
  password: '',
  confirmPassword: '',
  avatar: ''
});

const viewDialogVisible = ref(false);
const currentStaff = ref({});

const showPassword = ref(false);

const formRules = {
  name: [{ required: true, message: '请输入员工姓名', trigger: 'blur' }],
  staffId: [{ required: true, message: '请输入工号', trigger: 'blur' }],
  position: [{ required: true, message: '请选择职位', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
};

// 验证确认密码
function validateConfirmPassword(rule, value, callback) {
  if (value !== staffForm.value.password) {
    callback(new Error('两次输入的密码不一致'));
  } else {
    callback();
  }
}

// 获取职位类型
const getPositionType = (position) => {
  if (position === 'manager') return 'primary';
  if (position === 'chef') return 'warning';
  if (position === 'waiter') return 'success';
  if (position === 'cashier') return 'info';
  if (position === 'delivery') return 'danger';
  return 'info';
};

// 获取职位文本
const getPositionText = (position) => {
  const positionMap = {
    manager: '店长',
    chef: '厨师',
    waiter: '服务员',
    cashier: '收银员',
    delivery: '配送员'
  };
  return positionMap[position] || position;
};

// 获取状态类型
const getStatusType = (status) => {
  if (status === 'active') return 'success';
  if (status === 'inactive') return 'danger';
  if (status === 'leave') return 'warning';
  return 'info';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    active: '在职',
    inactive: '离职',
    leave: '休假'
  };
  return statusMap[status] || status;
};

// 搜索员工
const searchStaff = () => {
  console.log('搜索员工:', searchKeyword.value);
  // 这里应该调用API进行搜索
};

// 按职位筛选
const filterByPosition = () => {
  console.log('按职位筛选:', positionFilter.value);
};

// 按状态筛选
const filterByStatus = () => {
  console.log('按状态筛选:', statusFilter.value);
};

// 添加员工
const addStaff = () => {
  dialogTitle.value = '添加员工';
  staffForm.value = {
    id: null,
    staffId: '',
    name: '',
    position: '',
    phone: '',
    email: '',
    hireDate: '',
    password: '',
    confirmPassword: '',
    avatar: ''
  };
  dialogVisible.value = true;
};

// 编辑员工
const editStaff = (row) => {
  dialogTitle.value = '编辑员工';
  staffForm.value = { ...row };
  dialogVisible.value = true;
};

// 查看员工
const viewStaff = (row) => {
  currentStaff.value = { ...row };
  viewDialogVisible.value = true;
};

// 保存员工
const saveStaff = () => {
  console.log('保存员工:', staffForm.value);
  // 这里应该调用API保存员工
  ElMessage.success('员工保存成功');
  dialogVisible.value = false;
};

// 删除员工
const deleteStaff = (row) => {
  ElMessageBox.confirm(`确定要删除员工 "${row.name}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    // 这里应该调用API删除员工
    staffList.value = staffList.value.filter(item => item.id !== row.id);
    ElMessage.success('员工删除成功');
  });
};

// 导出员工
const exportStaff = () => {
  console.log('导出员工');
  // 这里应该调用API导出员工数据
  ElMessage.success('员工导出功能开发中');
};

// 重置表单
const resetForm = () => {
  staffForm.value = {
    id: null,
    staffId: '',
    name: '',
    position: '',
    phone: '',
    email: '',
    hireDate: '',
    password: '',
    confirmPassword: '',
    avatar: ''
  };
};

// 处理头像上传成功
const handleAvatarSuccess = (response, file) => {
  staffForm.value.avatar = URL.createObjectURL(file.raw);
};

// 上传头像前验证
const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isJPG) {
    ElMessage.error('头像图片只能是 JPG/PNG 格式!');
  }
  if (!isLt2M) {
    ElMessage.error('头像图片大小不能超过 2MB!');
  }
  return isJPG && isLt2M;
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
.staff-management {
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

.staff-table {
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

.avatar-uploader {
  width: 178px;
  height: 178px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.staff-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.detail-item .label {
  width: 80px;
  font-weight: bold;
  color: #666;
}

.detail-item .value {
  flex: 1;
  color: #333;
}
</style>
