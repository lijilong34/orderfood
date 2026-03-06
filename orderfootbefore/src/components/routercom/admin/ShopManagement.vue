<template>
  <div class="shop-management">
    <div class="div1">
      <fieldset class="my-fieldset">
        <legend class="my-legend">条件查询:</legend>
        店铺名称:<el-input v-model="searchForm.name" placeholder="请输入店铺名称" style="width: 200px;"></el-input>
        店铺状态:<el-select v-model="searchForm.status" placeholder="请选择店铺状态" style="width: 200px">
          <el-option label="正常营业" value="1"></el-option>
          <el-option label="暂停营业" value="0"></el-option>
          <el-option label="已封禁" value="2"></el-option>
        </el-select>
        <el-button type="primary" @click="getShopList">搜索</el-button>
        <el-button @click="resetForm">重置</el-button>
      </fieldset>
    </div>

      <el-table :data="shopList" style="width: 95%;margin-left: 10px;">
      <el-table-column prop="id" label="序号" width="60px" />
      <el-table-column prop="name" label="店铺名称" />
      <el-table-column prop="businessScope" label="经营品类"></el-table-column>
      <el-table-column prop="address" label="地址" />
	  <el-table-column prop="businessHours" label="营业时间"></el-table-column>
      <el-table-column prop="phone" label="联系电话" />
      <el-table-column label="店铺状态">
        <template #default="scope">
          <span v-if="scope.row.status===0">暂停营业</span>
          <span v-else-if="scope.row.status===1">正常营业</span>
          <span v-else-if="scope.row.status===2">已封禁</span>
          <span v-else>未知状态</span>
        </template>
      </el-table-column>
      <el-table-column label="操作">
        <template #header>
          <el-button type="primary" @click="takeadd()">添加</el-button>
        </template>
        <template #default="scope">
          <div style="display: flex; gap: 8px;">
            <el-button size="small" @click="handleEdit(scope.row)">
              修改
            </el-button>
            <el-button size="small" type="danger" @click="handleDelete(scope.row.id)">
              删除
            </el-button>
            <el-button v-if="scope.row.status==3" size="small" type="success" @click="handleBan(scope.row.id)">
              审核通过
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          background
          layout="prev, pager, next"
          :total="total"
          :page-size="6"
          @current-change="handleCurrentChange"
        />
      </div>

    <!-- 添加/编辑店铺对话框 -->
    <teleport to="body">
      <el-dialog v-model="dialogVisible" title="店铺信息" width="600px">
        <el-form :model="shopForm" :rules="rules" ref="formRef" label-width="100px">
          <el-form-item label="店铺名称" prop="name">
            <el-input v-model="shopForm.name" placeholder="请输入店铺名称" />
          </el-form-item>
          <el-form-item label="地址" prop="address">
            <el-input v-model="shopForm.address" placeholder="请输入店铺地址" />
          </el-form-item>
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="shopForm.phone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="店铺状态" prop="status">
            <el-select v-model="shopForm.status" placeholder="请选择店铺状态">
              <el-option label="暂停营业" value="0" />
              <el-option label="正常营业" value="1" />
              <el-option label="已封禁" value="2" />
            </el-select>
          </el-form-item>
          <el-form-item label="上传图片">
               <el-upload ref="uploadRef" action="http://localhost:8080/imgupdate/updateimage" :on-success="handleAvatarSuccess"  :show-file-list="false" list-type="picture-card" :limit="1" class="avatar" :on-remove="handleAvatarRemove">
	                  <div>
	                     <img v-if="imageUrl" :src="imageUrl" alt="" class="avatar-uploader-icon" style="max-width: 178px; max-height: 178px;"/>
	             	<el-icon v-else ><Plus /></el-icon>
	             	</div>
	               </el-upload>
				</el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitForm">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 营业额详情对话框 -->
      <el-dialog v-model="revenueDialogVisible" :title="revenueDialogTitle" width="500px">
        <div v-if="revenueData">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="店铺ID">{{ revenueData.shopId }}</el-descriptions-item>
            <el-descriptions-item label="营业额">{{ revenueData.revenue }} 元</el-descriptions-item>
            <el-descriptions-item label="统计时间" v-if="revenueData.time">{{ revenueData.time }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </el-dialog>
    </teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ShopApi from '../../../../api/ShopApi'
  const imageUrl = ref('')
  const uploadRef = ref(null)

// 搜索条件
const searchForm = reactive({
  name: '',
  status: ''
})

// 店铺列表数据
const shopList = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

// 对话框
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

// 店铺表单数据
const shopForm = reactive({
  id: null,
  name: '',
  address: '',
  phone: '',
  status: '1',
  logo: ''
})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入店铺名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  address: [
    { required: true, message: '请输入店铺地址', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择店铺状态', trigger: 'change' }
  ]
}

// 营业额对话框
const revenueDialogVisible = ref(false)
const revenueDialogTitle = ref('')
const revenueData = ref(null)

// 获取店铺列表
const getShopList = async () => {
  try {
    // 构建后端期望的where条件格式
    const where = []
    if (searchForm.name) {
      where.push({ column: 'name', type: 'like', value: searchForm.name })
    }
    if (searchForm.status !== '') {
      where.push({ column: 'status', type: 'eq', value: searchForm.status })
    }
    
    const params = {
      where: where,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await ShopApi.ShopSelect(params)
    console.log(res)
    if (res.code === 200) {
      shopList.value = res.data.pageInfo.list
      total.value = res.data.pageInfo.total
    }
  } catch (error) {
    console.error('获取店铺列表失败:', error)
    ElMessage.error('获取店铺列表失败')
  }
}

// 重置搜索条件
const resetForm = () => {
  Object.assign(searchForm, {
    name: '',
    status: ''
  })
  getShopList()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getShopList()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getShopList()
}

// 打开编辑店铺对话框
const handleEdit = async (row) => {
  try {
    const res = await ShopApi.getShopById(row.id)
    if (res.code === 200) {
      isEdit.value = true
      const entity = res.data.entity
      // 确保状态值是字符串类型，与el-select的value匹配
      Object.assign(shopForm, {
        ...entity,
        status: String(entity.status)
      })
      // 同步图片URL
      if (entity.logo) {
        imageUrl.value = 'http://localhost:8080/imeageserver/' + entity.logo
      } else {
        imageUrl.value = ''
      }
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('获取店铺详情失败:', error)
    ElMessage.error('获取店铺详情失败')
  }
}

// 重置店铺表单
const resetShopForm = () => {
  Object.assign(shopForm, {
    id: null,
    name: '',
    address: '',
    phone: '',
    status: '1',
    logo: ''
  })
  imageUrl.value = ''
  if (formRef.value) {
    formRef.value.resetFields()
  }
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        let res
        if (isEdit.value) {
          res = await ShopApi.updateShop(shopForm)
        } else {
          res = await ShopApi.addShop(shopForm)
        }
        if (res.code === 200) {
          ElMessage.success(isEdit.value ? '更新成功' : '添加成功')
          dialogVisible.value = false
          getShopList()
        } else {
          ElMessage.error(res.message || '操作失败')
        }
      } catch (error) {
        console.error('操作失败:', error)
        ElMessage.error('操作失败: ' + (error.message || '未知错误'))
      }
    }
  })
}

// 删除店铺
const handleDelete = async (id) => {
  ElMessageBox.confirm('确定要删除这个店铺吗?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const res = await ShopApi.deleteShop(id)
      if (res.code === 200) {
        ElMessage.success(res.data.status || '删除成功')
        getShopList()
      } else {
        ElMessage.error(res.message || '删除失败')
      }
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败: ' + (error.message || '未知错误'))
    }
  }).catch(() => {
    // 用户取消删除，不执行任何操作
  })
}

// 暂停营业
const handlePause = async (id) => {
  try {
    const res = await ShopApi.pauseShop(id)
    if (res.code === 200) {
      ElMessage.success(res.data.status || '暂停营业成功')
      getShopList()
    } else {
      ElMessage.error(res.message || '暂停营业失败')
    }
  } catch (error) {
    console.error('暂停营业失败:', error)
    ElMessage.error('暂停营业失败: ' + (error.message || '未知错误'))
  }
}

// 开启营业
const handleOpen = async (id) => {
  try {
    const res = await ShopApi.openShop(id)
    if (res.code === 200) {
      ElMessage.success(res.data.status || '开启营业成功')
      getShopList()
    } else {
      ElMessage.error(res.message || '开启营业失败')
    }
  } catch (error) {
    console.error('开启营业失败:', error)
    ElMessage.error('开启营业失败: ' + (error.message || '未知错误'))
  }
}

// 通过审核店铺
const handleBan = async (id) => {
    try {
      const res = await ShopApi.openShop(id)
      if (res.code === 200) {
        ElMessage.success(res.data.status || '封禁成功')
        getShopList()
      } else {
        ElMessage.error(res.message || '封禁失败')
      }
    } catch (error) {
      console.error('封禁失败:', error)
      ElMessage.error('封禁失败: ' + (error.message || '未知错误'))
    }
  }



// 查看日销售额
const viewDailyRevenue = async (id) => {
  try {
    const res = await ShopApi.getDailyRevenue(id)
    if (res.code === 200) {
      revenueData.value = res.data
      revenueDialogTitle.value = '今日销售额'
      revenueDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取日销售额失败')
    }
  } catch (error) {
    console.error('获取日销售额失败:', error)
    ElMessage.error('获取日销售额失败: ' + (error.message || '未知错误'))
  }
}

// 查看月销售额
const viewMonthlyRevenue = async (id) => {
  try {
    const res = await ShopApi.getMonthlyRevenue(id)
    if (res.code === 200) {
      revenueData.value = res.data
      revenueDialogTitle.value = '本月销售额'
      revenueDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '获取月销售额失败')
    }
  } catch (error) {
    console.error('获取月销售额失败:', error)
    ElMessage.error('获取月销售额失败: ' + (error.message || '未知错误'))
  }
}

// 添加店铺
const takeadd = () => {
  isEdit.value = false
  resetShopForm()
  dialogVisible.value = true
  // 清空表单验证状态
  Promise.resolve().then(() => {
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  })
}

// 页面加载时获取店铺列表
onMounted(() => {
  getShopList()
})

function handleAvatarSuccess(imgSuccess){
		imageUrl.value='http://localhost:8080/imeageserver/'+imgSuccess.data.imgpath;
     shopForm.logo = imgSuccess.data.imgpath;
     // 上传成功后清空文件列表，允许更换图片
     if (uploadRef.value) {
       uploadRef.value.clearFiles();
     }
	}

	const handleAvatarRemove = () => {
  imageUrl.value = '';
  shopForm.logo = '';
   }
</script>

<style scoped>
.shop-management {
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

.search-form {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}
</style>