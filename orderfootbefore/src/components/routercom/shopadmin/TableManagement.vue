<template>
  <div class="table-manage">
    <!-- 搜索区域：可容纳人数改为精准查询（2-8人） -->
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <!-- 餐桌编号查询 -->
        <el-form-item label="餐桌编号" class="form-item">
          <el-input 
            v-model="searchForm.tableNo" 
            placeholder="请输入餐桌编号（如11-1）" 
            clearable
            style="width: 180px;"
          />
        </el-form-item>
        <!-- 可容纳人数：精准查询（2-8人） -->
        <el-form-item label="可容纳人数" class="form-item">
          <el-input-number
            v-model="searchForm.capacity"
            placeholder="请输入人数"
            min="2"
            max="8"
            style="width: 140px;"
            clearable
          />
        </el-form-item>
        <!-- 餐桌状态查询 -->
        <el-form-item label="餐桌状态" class="form-item">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 140px;"
          >
            <el-option label="空闲中" value="0" />
            <el-option label="已预订" value="1" />
            <el-option label="使用中" value="2" />
          </el-select>
        </el-form-item>
        <!-- 按钮组 -->
        <el-form-item class="btn-group">
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleAdd">增加</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 餐桌列表：保持不变 -->
    <el-table 
      :data="tableList" 
      border 
      stripe 
      v-loading="loading"
      style="width: 100%; margin-top: 15px;"
    >
      <el-table-column prop="id" label="餐桌ID" width="80" />
      <el-table-column prop="tableNo" label="餐桌编号" min-width="120" />
      <el-table-column prop="capacity" label="容纳人数" width="100" />
      <el-table-column label="餐桌状态" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.status === 0">空闲中</el-tag>
          <el-tag type="warning" v-else-if="scope.row.status === 1">已预订</el-tag>
          <el-tag type="danger" v-else>使用中</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180" />
      <el-table-column prop="updateTime" label="更新时间" min-width="180" />
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页：保持居中 -->
    <div class="pagination-container">
      <el-pagination
        :page-size="6" 
        :total="total"
        layout="total, prev, pager, next, jumper"  
        @current-change="handleCurrentChange"  
      />
    </div>

    <!-- 增加/编辑弹窗：容纳人数限制2-8人 -->
    <teleport to="body">
      <el-dialog 
        v-model="dialogVisible" 
        :title="dialogType === 'add' ? '增加餐桌' : '编辑餐桌'"
        width="600px"
      >
        <el-form 
          :model="form" 
          :rules="formRules" 
          ref="formRef"
          label-width="100px"
        >
          <el-form-item label="餐桌编号" prop="tableNo">
            <el-input 
              v-model="form.tableNo" 
              placeholder="请输入餐桌编号（如11-1）" 
              maxlength="20"
            />
          </el-form-item>
          <el-form-item label="容纳人数" prop="capacity">
            <el-input-number
              v-model="form.capacity"
              placeholder="请输入容纳人数"
              min="2"
              max="8"
              style="width: 100%;"
            />
          </el-form-item>
          <el-form-item label="餐桌状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择餐桌状态">
              <el-option label="空闲中" value="0" />
              <el-option label="已预订" value="1" />
              <el-option label="使用中" value="2" />
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleSubmit">提交</el-button>
          </div>
        </template>
      </el-dialog>
    </teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import tableApi from '../../../../api/tableApi.js';
let total=ref(1);
// ==================== 响应式数据 ====================
const shopId = ref(localStorage.getItem("shopid") || '');
const loading = ref(false);
const tableList = ref([]);
// 可容纳人数改为精准查询（capacity），限制2-8人
const searchForm = reactive({
  tableNo: '',
  capacity: '', // 精准查询：容纳人数（2-8）
  status: '' // 状态值：0-空闲中 1-已预订 2-使用中
});
const selectwhere = ref({
  page:1,
  where:[]
});
const dialogVisible = ref(false);
const dialogType = ref('add');
const formRef = ref(null);

// 表单数据：容纳人数默认2人（符合2-8限制）
const form = reactive({
  id: '',
  shopId: shopId.value,
  tableNo: '',
  capacity: 2, // 默认2人（原1人改为2人，符合限制）
  status: 0 // 默认为空闲中
});

// 表单校验规则：容纳人数限制2-8人
const formRules = reactive({
  tableNo: [
    { required: true, message: '请输入餐桌编号', trigger: 'blur' },
    { max: 20, message: '餐桌编号不能超过20个字符', trigger: 'blur' }
  ],
  capacity: [
    { required: true, message: '请输入容纳人数', trigger: 'blur' },
    { type: 'number', min: 2, max: 8, message: '容纳人数需在2-8人之间', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择餐桌状态', trigger: 'change' }
  ]
});

// ==================== 方法 ====================
// 加载餐桌列表：精准查询容纳人数
const fetchTableList = async () => {
  if (!shopId.value) {
    ElMessage.error('未获取到店铺信息，请重新登录');
    return;
  }
  loading.value = true;
  try {
	  console.log("shopid编号",shopId.value)
     selectwhere.value.where=([{
		"column":"shop_id",
		"type":"eq",
		"value":shopId.value
	}])
    // 处理搜索条件
    if (searchForm.tableNo) {
      selectwhere.value.where.push({
        "column":"table_no",
        "type":"like",
        "value":searchForm.tableNo
      });
    }
    if (searchForm.capacity) {
      selectwhere.value.where.push({
        "column":"capacity",
        "type":"eq",
        "value":searchForm.capacity
      });
    }
    if (searchForm.status !== '') {
      selectwhere.value.where.push({
        "column":"status",
        "type":"eq",
        "value":searchForm.status
      });
    }

    const res = await tableApi.selectTableList(selectwhere.value);
	console.log(res)
    if (res.code === 200 && res.data.pageInfo) {
      tableList.value = res.data.pageInfo.list;
      total.value = res.data.pageInfo.total;
    } else {
      ElMessage.error('获取餐桌列表失败');
    }
  } catch (error) {
    console.error('获取餐桌列表异常:', error);
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 搜索：保持不变
const handleSearch = () => {
  fetchTableList();
};

// 重置：清空容纳人数精准查询条件
const handleReset = () => {
  searchForm.tableNo = '';
  searchForm.capacity = ''; // 清空容纳人数
  searchForm.status = '';
  fetchTableList();
};

// 新增：容纳人数默认2人
const handleAdd = () => {
  dialogType.value = 'add';
  form.id = '';
  form.tableNo = '';
  form.capacity = 2; // 默认2人（符合2-8限制）
  form.status = 0;
  if (formRef.value) {
    formRef.value.clearValidate();
  }
  dialogVisible.value = true;
};

// 编辑：保持不变（回显实际数据，自动受2-8限制）
const handleEdit = async (row) => {
  dialogType.value = 'edit';
  try {
    const res = await tableApi.selectById(row.id);
    if (res.code === 200 && res.data.entity) {
      const table = res.data.entity;
      Object.assign(form, {
        ...table,
        status: String(table.status)
      });
      if (formRef.value) {
        formRef.value.clearValidate();
      }
      dialogVisible.value = true;
    } else {
      ElMessage.error('获取餐桌详情失败');
    }
  } catch (error) {
    console.error('获取餐桌详情异常:', error);
    ElMessage.error('网络异常，请稍后重试');
  }
};

// 提交：保持不变
const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    form.shopId = shopId.value;

    let res;
    if (dialogType.value === 'add') {
      res = await tableApi.addTable(form);
    } else {
		searchForm.tableNo=null;
		searchForm.capacity=null;
		searchForm.status=null;
      res = await tableApi.updateTable(form);
    }

    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '增加成功' : '编辑成功');
      dialogVisible.value = false;
      fetchTableList();
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '增加失败' : '编辑失败'));
    }
  } catch (error) {
    if (error.name !== 'ValidationError') {
      console.error('提交异常:', error);
      ElMessage.error('网络异常，请稍后重试');
    }
  }
};

// 删除：保持不变
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该餐桌吗？删除后不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    const res = await tableApi.deleteTable(id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      fetchTableList();
    } else {
      ElMessage.error(res.message || '删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除异常:', error);
      ElMessage.error('网络异常，请稍后重试');
    }
  }
};

// 分页切换：保持不变
const handleCurrentChange = (val) => {
  selectwhere.value.page = val;
  fetchTableList();
};

// 生命周期：保持不变
onMounted(() => {
  fetchTableList();
});
</script>

<style scoped>
/* 样式适配精准查询的数字输入框 */
.table-manage {
  padding: 20px;
  box-sizing: border-box;
}

.search-container {
  width: 100%;
}

.search-form {
  display: flex;
  align-items: flex-start;
  flex-wrap: nowrap;
  gap: 12px;
  background-color: #f5f7fa;
  padding: 5px 10px;
  border-radius: 8px;
  width: 100%;
  min-height: 60px;
}

.form-item {
  margin: 0 !important;
  display: flex;
  align-items: center;
  height: 36px;
}

/* 数字输入框高度适配，与其他组件对齐 */
:deep(.el-input-number) {
  height: 36px;
}
:deep(.el-input-number__wrapper) {
  height: 100% !important;
}

.btn-group {
  margin: 0 !important;
  display: flex;
  align-items: center;
  margin-left: auto !important;
  flex-shrink: 0;
  gap: 8px;
  height: 36px;
}

:deep(.el-form-item__label) {
  white-space: nowrap;
  padding-right: 8px;
  font-size: 14px;
}

:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  height: 36px !important;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 10px 0;
}

/* 小屏适配：搜索区域自动换行 */
@media (max-width: 992px) {
  .search-form {
    flex-wrap: wrap;
  }
  .form-item {
    margin-bottom: 8px !important;
  }
}

@media (max-width: 768px) {
  .pagination-container {
    flex-wrap: wrap;
    gap: 10px;
  }
}
</style>