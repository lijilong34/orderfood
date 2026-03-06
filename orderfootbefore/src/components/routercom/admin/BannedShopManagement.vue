<template>
  <div class="shop-management">
      <!-- 搜索条件 -->
       <fieldset class="my-fieldset">
			<legend class="my-legend">条件查询:</legend>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="店铺名称">
          <el-input v-model="searchForm.name" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="getShopList">查询</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
      </fieldset>

      <!-- 店铺列表 -->
      <el-table :data="shopList" style="width: 100%" stripe>
        <el-table-column prop="id" label="店铺ID" width="70" />
        <el-table-column prop="name" label="店铺名称" width="150" />
        <el-table-column prop="businessScope" label="经营品类" width="150" />
        <el-table-column prop="address" label="地址"  width="200"/>
	  <el-table-column prop="businessHours" label="营业时间"/>
      <el-table-column prop="phone" label="联系电话" />
       <el-table-column label="状态">
          <template #default="scope">
              <!-- 确保status是数字类型进行比较 -->
              <el-tag type="success" v-if="Number(scope.row.status) === 0">正常营业</el-tag>
              <el-tag type="warning" v-else-if="Number(scope.row.status) === 1">暂停营业</el-tag>
              <el-tag type="danger" v-else-if="Number(scope.row.status) === 2">已封禁</el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template #default="scope">
              <el-button type="primary" size="small" @click="handleUnban(scope.row.id)">解禁</el-button>
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

    <!-- 编辑店铺对话框 -->
    <teleport to="body">
      <el-dialog v-model="dialogVisible" title="编辑店铺信息" width="600px">
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
import { ElMessage } from 'element-plus'
import ShopApi from '../../../../api/ShopApi'

// 搜索条件
const searchForm = reactive({
  name: ''
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
  phone: ''
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
  ]
}

// 营业额对话框
const revenueDialogVisible = ref(false)
const revenueDialogTitle = ref('')
const revenueData = ref(null)

// 获取店铺列表 - 默认筛选已封禁店铺（状态为3）
const getShopList = async () => {
  try {
    // 构建后端期望的where条件格式
    const where = []
    if (searchForm.name) {
      where.push({ column: 'name', type: 'like', value: searchForm.name })
    }
    // 默认添加已封禁状态筛选
    where.push({ column: 'status', type: 'eq', value: 2 })
    
    const params = {
      where: where,
      page: currentPage.value,
      pageSize: pageSize.value
    }
    const res = await ShopApi.ShopSelect(params)
    console.log(res)
    if (res.code === 200) {
      shopList.value = res.data.pageInfo.list;
      total.value = res.data.pageInfo.total;
    }
  } catch (error) {
    console.error('获取店铺列表失败:', error)
    ElMessage.error('获取店铺列表失败')
  }
}

// 重置搜索条件
const resetForm = () => {
  Object.assign(searchForm, {
    name: ''
  })
  getShopList()
}

// 处理编辑
const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(shopForm, row)
  dialogVisible.value = true
}

// 处理删除
const handleDelete = async (id) => {
  try {
    const res = await ShopApi.deleteShop(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      getShopList()
    }
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

// 处理解除封禁
const handleUnban = async (id) => {
  try {
    const res = await ShopApi.openShop(id)
    if (res.code === 200) {
      ElMessage.success('解除封禁成功')
      getShopList()
    }
  } catch (error) {
    console.error('解除封禁失败:', error)
    ElMessage.error('解除封禁失败')
  }
}

// 提交表单
const submitForm = async () => {
  try {
    await formRef.value.validate()
    const res = await ShopApi.updateShop(shopForm)
    if (res.code === 200) {
      ElMessage.success('更新成功')
      dialogVisible.value = false
      getShopList()
    }
  } catch (error) {
    console.error('更新失败:', error)
    ElMessage.error('更新失败')
  }
}

// 查看营业额
const viewRevenue = async (id) => {
  try {
    const res = await ShopApi.getShopRevenue(id)
    if (res.code === 200) {
      revenueData.value = res.data
      revenueDialogTitle.value = '店铺总营业额'
      revenueDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取营业额失败:', error)
    ElMessage.error('获取营业额失败')
  }
}

// 查看日销售额
const viewDailyRevenue = async (id) => {
  const date = new Date().toISOString().split('T')[0]
  try {
    const res = await ShopApi.getDailyRevenue(id, date)
    if (res.code === 200) {
      revenueData.value = res.data
      revenueDialogTitle.value = `${date} 日销售额`
      revenueDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取日销售额失败:', error)
    ElMessage.error('获取日销售额失败')
  }
}

// 查看月销售额
const viewMonthlyRevenue = async (id) => {
  const now = new Date()
  const month = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}`
  try {
    const res = await ShopApi.getMonthlyRevenue(id, month)
    if (res.code === 200) {
      revenueData.value = res.data
      revenueDialogTitle.value = `${month} 月销售额`
      revenueDialogVisible.value = true
    }
  } catch (error) {
    console.error('获取月销售额失败:', error)
    ElMessage.error('获取月销售额失败')
  }
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

// 初始加载
onMounted(() => {
  getShopList()
})
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