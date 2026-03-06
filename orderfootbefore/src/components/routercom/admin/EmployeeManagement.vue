<template>
  <div class="employee-management">
      <!-- 搜索条件 -->
       <fieldset class="my-fieldset">
        <legend class="my-legend">条件查询:</legend>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="岗位">
          <el-select v-model="searchForm.post" placeholder="请选择岗位" style="width: 200px;">
            <el-option label="服务员" value="服务员" />
            <el-option label="厨师" value="厨师" />
            <el-option label="收银员" value="收银员" />
            <el-option label="店长" value="店长"></el-option>
            <el-option label="管理员" value="管理员"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工作状态">
          <el-select v-model="searchForm.workStatus" placeholder="请选择工作状态" style="width: 200px;">
            <el-option label="在岗" value="1" />
            <el-option label="休假" value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getEmployeeList">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
</fieldset>
      <!-- 员工列表 -->
      <el-table :data="employeeList" style="width: 95%;margin-left: 10px;">
        <el-table-column prop="id" label="员工ID" width="80"/>
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="shopName" label="店铺名字"/>
        <el-table-column prop="post" label="岗位" />
         <el-table-column prop="schedule" label="工作时间"/>
        <el-table-column prop="workStatus" label="工作状态">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.workStatus === 1">在岗</el-tag>
            <el-tag type="info" v-else>休假</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #header>
				<el-button type="primary" @click="openAddDialog">添加</el-button>
			</template>
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.row)">修改</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
            <el-button v-if="scope.row.workStatus === 1" type="warning" size="small" @click="handlePause(scope.row.id)">暂停</el-button>
            <el-button v-else type="success" size="small" @click="handleResume(scope.row.id)">恢复</el-button>
          </template>
        </el-table-column>
      </el-table>

     <div class="pagination-container">
		<el-pagination background layout="prev, pager, next" :total="total" :page-size="6"
			@current-change="handchange" />
	</div>

    <!-- 添加员工对话框 -->
    <teleport to="body">
      <el-dialog v-model="dialogVisible" title="员工信息" width="500px">
        <el-form :model="employeeForm" :rules="rules" ref="addFormRef" label-width="100px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="employeeForm.username" placeholder="请输入用户名" />
          </el-form-item>
  		<el-form-item label="所属店铺">
  			<el-select v-model="employeeForm.shopId" filterable>
  				<el-option v-for="shop in shoplist" :value="shop.id" :label="shop.name"></el-option>
  			</el-select>
  		</el-form-item>
      	<el-form-item label="工作时间">
  				<el-input v-model="employeeForm.schedule" placeholder="请输入工作时间" clearable>
  				</el-input>
  			</el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="employeeForm.password" type="password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="岗位" prop="post">
            <el-select v-model="employeeForm.post" placeholder="请选择岗位">
              <el-option label="服务员" value="服务员" />
              <el-option label="厨师" value="厨师" />
              <el-option label="收银员" value="收银员" />
               <el-option label="店长" value="店长"></el-option>
              <el-option label="管理员" value="管理员"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="工作状态" prop="workStatus">
            <el-radio-group v-model="employeeForm.workStatus">
              <el-radio :label="1">在岗</el-radio>
              <el-radio :label="0">休假</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAddForm">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 修改员工对话框 -->
      <el-dialog v-model="dialogVisibleup" title="员工信息" width="500px">
        <el-form :model="employeeFormup" :rules="rules" ref="editFormRef" label-width="100px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="employeeFormup.username" placeholder="请输入用户名" />
          </el-form-item>
  		<el-form-item label="所属店铺">
  			<el-select v-model="employeeFormup.shopId" filterable>
  				<el-option v-for="shop in shoplist" :value="shop.id" :label="shop.name"></el-option>
  			</el-select>
  		</el-form-item>
      	<el-form-item label="工作时间">
  				<el-input v-model="employeeFormup.schedule" placeholder="请输入工作时间" clearable>
  				</el-input>
  			</el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="employeeFormup.password" type="password" placeholder="请输入密码" />
          </el-form-item>
          <el-form-item label="岗位" prop="post">
            <el-select v-model="employeeFormup.post" placeholder="请选择岗位">
              <el-option label="服务员" value="服务员" />
              <el-option label="厨师" value="厨师" />
              <el-option label="收银员" value="收银员" />
               <el-option label="店长" value="店长"></el-option>
              <el-option label="管理员" value="管理员"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="工作状态" prop="workStatus">
            <el-radio-group v-model="employeeFormup.workStatus">
              <el-radio :label="1">在岗</el-radio>
              <el-radio :label="0">休假</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisibleup = false">取消</el-button>
            <el-button type="primary" @click="submitEditForm">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </teleport>
  </div>

  
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import EmployeeApi from '../../../../api/EmployeeApi'
import ShopApi from '../../../../api/ShopApi';
const shoplist=ref();
const dialogVisibleup=ref(false);//修改模态框
// 搜索条件
const searchForm = ref({
  username: '',
  post: '',
  workStatus: ''
})
const searchFormlike=ref({
    page:1,
    where:[]
})
// 员工列表数据
const employeeList = ref([])
const total = ref(0)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const addFormRef = ref(null)
const editFormRef = ref(null)
const isEdit = ref(false)

// 员工表单数据
const employeeForm = reactive({
  id: null,
  username: '',
  schedule:'',
  password: '',
  post: '',
  shopId:'',
  workStatus: 1
})

// 修改员工表单数据
const employeeFormup = reactive({
  id: '',
  username: '',
  password: '',
  post: '',
  shopId:'',
  schedule:'',
  workStatus: 1
})

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '长度在 3 到 20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  post: [
    { required: true, message: '请选择岗位', trigger: 'change' }
  ],
  workStatus: [
    { required: true, message: '请选择工作状态', trigger: 'change' }
  ]
}
// 获取员工列表
const getEmployeeList = async () => {
  try {
        searchFormlike.value.where=[{
     "column":"username",
     "type":"like",
     "value":searchForm.value.username
    },{
      "column":"post",
     "type":"eq",
     "value":searchForm.value.post
    },{
       "column":"work_status",
     "type":"eq",
     "value":searchForm.value.workStatus
    }]
    const res = await EmployeeApi.selectEmployeeandshop(searchFormlike.value)
    console.log(res)
    if (res.code === 200) {
      employeeList.value = res.data.pageInfo.list;
      total.value = res.data.pageInfo.total
    }
  } catch (error) {
    console.error('获取员工列表失败:', error)
    ElMessage.error('获取员工列表失败')
  }
}

// 重置搜索条件
const resetForm = () => {
  searchForm.value.username = '';
  searchForm.value.post = '';
  searchForm.value.workStatus = '';
  getEmployeeList();
}

const handchange = (current) => {
  searchFormlike.value.page = current
  getEmployeeList()
}

// 打开添加员工对话框
const openAddDialog = async() => {
  isEdit.value = false
  resetEmployeeForm()
  // 同时重置修改表单的数据
  Object.assign(employeeFormup, {
    id: '',
    username: '',
    schedule: '',
    password: '',
    post: '',
    shopId: '',
    workStatus: 1
  })
  dialogVisible.value = true
  let res=await ShopApi.ShopSelect();
  shoplist.value=res.data.pageInfo.list;
}

// 打开编辑员工对话框
const handleEdit = async(row) => {
  isEdit.value = true
  // 确保workStatus是数字类型
  Object.assign(employeeFormup, {
    ...row,
    workStatus: Number(row.workStatus) || 1,
    shopId:Number(row.shopId)
  })
  
  dialogVisibleup.value = true
    let res=await ShopApi.ShopSelect();
  shoplist.value=res.data.pageInfo.list;
}

// 重置员工表单
const resetEmployeeForm = () => {
  Object.assign(employeeForm, {
    id: null,
    username: '',
    schedule: '',
    password: '',
    post: '',
    shopId: '',
    workStatus: 1
  })
  if (addFormRef.value) {
    addFormRef.value.resetFields()
  }
}

// 提交添加表单
const submitAddForm = async () => {
  if (!addFormRef.value) return
  await addFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await EmployeeApi.addEmployee(employeeForm)
        if (res.code === 200) {
          ElMessage.success('添加成功')
          dialogVisible.value = false
          getEmployeeList()
        }
      } catch (error) {
        console.error('添加失败:', error)
        ElMessage.error('添加失败')
      }
    }
  })
}

// 提交修改表单
const submitEditForm = async () => {
  if (!editFormRef.value) return
  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const res = await EmployeeApi.updateEmployee(employeeFormup)
        if (res.code === 200) {
          ElMessage.success('更新成功')
          dialogVisibleup.value = false
          getEmployeeList()
        }
      } catch (error) {
        console.error('更新失败:', error)
        ElMessage.error('更新失败')
      }
    }
  })
}

// 删除员工
const handleDelete = async (id) => {
  try {
    const res = await EmployeeApi.deleteEmployee(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getEmployeeList()
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 暂停员工
const handlePause = async (id) => {
  try {
    const res = await EmployeeApi.pauseEmployee(id)
    if (res.code === 200) {
      ElMessage.success('暂停成功')
      getEmployeeList()
    }
  } catch (error) {
    console.error('暂停失败:', error)
    ElMessage.error('暂停失败')
  }
}

// 恢复员工工作
const handleResume = async (id) => {
  try {
    const res = await EmployeeApi.resumeEmployee(id)
    if (res.code === 200) {
      ElMessage.success('恢复成功')
      getEmployeeList()
    }
  } catch (error) {
    console.error('恢复失败:', error)
    ElMessage.error('恢复失败')
  }
}

// 页面加载时获取员工列表
onMounted(() => {
  getEmployeeList()
})
</script>

<style scoped>
.pagination-container {
		display: flex;
		justify-content: center;
		margin-top: 20px;
	}
</style>