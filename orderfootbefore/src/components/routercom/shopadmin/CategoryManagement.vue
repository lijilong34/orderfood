<template>
  <div class="category-management">
    <h2>菜品分类管理</h2>

    <!-- 操作按钮区 -->
    <div class="btn-group" style="margin: 15px 0;">
      <el-button type="success" @click="handleAdd">添加分类</el-button>
    </div>

    <!-- 分类列表：直接展示联表查询的店铺名称 -->
    <div class="category-list">
      <el-table 
        :data="categoryList" 
        border 
        stripe
        v-loading="loading"
        style="width: 100%;"
        fit
        :select-on-indeterminate="false"
        :show-select="false"
      >
        <el-table-column 
          prop="id" 
          label="分类ID" 
          width="100"
        />

        <el-table-column 
          prop="shopname" 
          label="店铺名称" 
          min-width="180"
        />
        <el-table-column 
          prop="name" 
          label="分类名称" 
          min-width="200"
        />
        <el-table-column 
          label="操作" 
          width="180"
        >
          <template #default="scope">
            <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 添加/编辑分类弹窗 -->
    <teleport to="body">
      <el-dialog 
        :title="dialogType === 'add' ? '添加分类' : '编辑分类'" 
        v-model="dialogVisible" 
        width="400px"
      >
        <el-form 
          :model="categoryForm" 
          :rules="formRules" 
          ref="categoryFormRef"
          label-width="100px"
        >
          <el-form-item label="分类名称" prop="name">
            <el-input 
              v-model="categoryForm.name" 
              placeholder="请输入分类名称"
              maxlength="50"
              show-word-limit
            />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </template>
      </el-dialog>
    </teleport>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import ProductCategoryApi from '../../../../api/ProductCategoryApi'

// ==================== 响应式数据 ====================
// 当前登录店铺ID（从本地存储获取）
const currentShopId = ref(localStorage.getItem("shopid") || '')
// 分类列表（直接存储联表查询结果：包含id、name、shopname）
const categoryList = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const dialogType = ref('add')
const categoryFormRef = ref(null)

// 分类表单数据
const categoryForm = reactive({
  id: '',
  name: '',
  shopId: currentShopId.value // 关联当前店铺ID，新增/编辑时传递
})

// 表单校验规则
const formRules = reactive({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 50, message: '分类名称不能超过50个字符', trigger: 'blur' }
  ]
})

// ==================== 方法 ====================
// 加载分类数据（联表查询结果，直接包含店铺名称）
const loadCategoryData = async () => {
  if (!currentShopId.value) {
    ElMessage.error('未获取到店铺信息，请重新登录')
    return
  }

  loading.value = true
  try {
    // 调用联表查询接口（后端已通过INNER JOIN shop返回shopname）
    const categoryRes = await ProductCategoryApi.selectcategorybyshopid(currentShopId.value)
    console.log("联表查询分类数据：", categoryRes)
    
    // 直接赋值联表结果，数据结构包含：id（分类ID）、name（分类名称）、shopname（店铺名称）
    categoryList.value = categoryRes.data.productCategoryList || []

    // 校验联表字段是否存在，避免前端显示异常
    if (categoryList.value.length > 0 && !categoryList.value[0].hasOwnProperty('shopname')) {
      ElMessage.warning('联表查询未返回店铺名称，将显示默认值')
      // 字段缺失时兜底
      categoryList.value = categoryList.value.map(item => ({
        ...item,
        shopname: '未知店铺'
      }))
    }
  } catch (error) {
    ElMessage.error('加载分类数据失败：' + (error.message || '网络异常'))
    console.error('分类数据加载异常：', error)
    categoryList.value = []
  } finally {
    loading.value = false
  }
}

// 打开新增分类弹窗
const handleAdd = () => {
  dialogType.value = 'add'
  resetForm()
  dialogVisible.value = true
}

// 打开编辑分类弹窗（回显联表数据中的分类信息）
const handleEdit = (row) => {
  dialogType.value = 'edit'
  // 仅回显分类相关字段（shopname是联表查询结果，无需编辑）
  categoryForm.id = row.id || ''
  categoryForm.name = row.name || ''
  categoryForm.shopId = currentShopId.value // 编辑时仍关联当前店铺ID
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  categoryForm.id = ''
  categoryForm.name = ''
  categoryForm.shopId = currentShopId.value
  if (categoryFormRef.value) {
    categoryFormRef.value.clearValidate()
  }
}

// 提交表单（新增/编辑分类）
const handleSubmit = async () => {
  if (!categoryFormRef.value) return

  try {
    // 表单校验
    await categoryFormRef.value.validate()

    let res
    if (dialogType.value === 'add') {
      // 新增分类：传递shopId（当前店铺ID）和分类名称
      res = await ProductCategoryApi.addCategory(categoryForm)
    } else {
      // 编辑分类：传递分类ID、名称和shopId（确保归属正确）
      res = await ProductCategoryApi.updateCategory(categoryForm)
    }

    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '新增分类成功' : '编辑分类成功')
      dialogVisible.value = false
      loadCategoryData() // 重新加载联表数据，更新店铺名称显示
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '新增分类失败' : '编辑分类失败'))
    }
  } catch (error) {
    // 排除表单校验失败的异常
    if (error.name !== 'ValidationError') {
      ElMessage.error('提交失败：' + (error.message || '网络异常'))
      console.error('表单提交异常：', error)
    }
  }
}

// 删除分类
const handleDelete = async (id) => {
  if (!id) {
    ElMessage.warning('分类ID无效，无法删除')
    return
  }

  try {
    await ElMessageBox.confirm(
      '确定要删除该分类吗？删除后关联的菜品分类信息也会受影响！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    const res = await ProductCategoryApi.deleteCategory(id)
    if (res.code === 200) {
      ElMessage.success('删除分类成功')
      loadCategoryData() // 重新加载联表数据
    } else {
      ElMessage.error(res.message || '删除分类失败')
    }
  } catch (error) {
    // 排除用户取消操作的异常
    if (error !== 'cancel') {
      ElMessage.error('删除失败：' + (error.message || '网络异常'))
      console.error('分类删除异常：', error)
    }
  }
}

// ==================== 生命周期 ====================
onMounted(() => {
  // 页面加载时加载联表分类数据
  loadCategoryData()
})
</script>

<style scoped>
.category-management {
  padding: 20px;
  box-sizing: border-box;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.category-management h2 {
  margin: 0 0 15px 0;
  font-size: 18px;
  font-weight: 600;
  color: #333;
}

.btn-group {
  display: flex;
  align-items: center;
}

.category-list {
  background: #fff;
  border-radius: 4px;
  padding: 10px;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* 适配小窗口，避免表格挤压 */
:deep(.el-table) {
  min-width: 700px;
}

/* 弹窗表单样式优化 */
:deep(.el-form-item) {
  margin-bottom: 15px;
}

/* 按钮样式优化 */
:deep(.el-button) {
  padding: 8px 16px;
}
</style>