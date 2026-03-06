<template>
  <div class="product-manage">
    <!-- 搜索区域：优化Flex布局，强制同行显示 -->
    <div class="search-container">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <!-- 菜品序号（ID）查询 -->
        <el-form-item label="菜品序号" class="form-item">
          <el-input 
            v-model="searchForm.id" 
            placeholder="请输入菜品ID" 
            type="number" 
            clearable
            style="width: 140px;"
          />
        </el-form-item>
        <!-- 菜品名称查询 -->
        <el-form-item label="菜品名称" class="form-item">
          <el-input 
            v-model="searchForm.name" 
            placeholder="请输入菜品名称" 
            clearable
            style="width: 180px;"
          />
        </el-form-item>
        <!-- 菜品状态查询 -->
        <el-form-item label="菜品状态" class="form-item">
          <el-select 
            v-model="searchForm.status" 
            placeholder="请选择状态" 
            clearable
            style="width: 140px;"
          >
            <el-option label="全部" value="" />
            <el-option label="上架" value="1" />
            <el-option label="下架" value="0" />
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

    <!-- 菜品列表（保持不变） -->
    <el-table 
      :data="productList" 
      border 
      stripe 
      v-loading="loading"
      style="width: 100%; margin-top: 15px;"
    >
      <el-table-column prop="id" label="菜品序号" width="80" />
      <el-table-column prop="name" label="菜品名称" min-width="150" />
      <el-table-column prop="price" label="菜品价格" width="100">
        <template #default="scope">¥{{ scope.row.price.toFixed(2) }}</template>
      </el-table-column>
      <el-table-column prop="description" label="菜品描述" min-width="200" />
      <el-table-column prop="stock" label="库存数量" width="100">
        <template #default="scope">{{ scope.row.stock === 0 ? '无限' : scope.row.stock }}</template>
      </el-table-column>
      <el-table-column prop="sales" label="销售数量" width="100" />
      <el-table-column label="菜品状态" width="100">
        <template #default="scope">
          <el-tag type="success" v-if="scope.row.status === 1">上架</el-tag>
          <el-tag type="danger" v-else>下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160">
        <template #default="scope">
          <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button type="danger" size="small" @click="handleDelete(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页（保持不变） -->
    <div class="pagination-container" style="margin-top: 15px; text-align: right;">
      <el-pagination
        v-model:current-page="pagination.currentPage"
        :page-size="6" 
        :total="pagination.total"
        layout="total, prev, pager, next, jumper"  
        @current-change="handleCurrentChange"  
      />
    </div>

    <!-- 增加/编辑弹窗（保持不变） -->
    <teleport to="body">
      <el-dialog 
        v-model="dialogVisible" 
        :title="dialogType === 'add' ? '增加菜品' : '编辑菜品'"
        width="600px"
      >
        <el-form 
          :model="form" 
          :rules="formRules" 
          ref="formRef"
          label-width="100px"
        >
          <el-form-item label="菜品名称" prop="name">
            <el-input v-model="form.name" placeholder="请输入菜品名称" />
          </el-form-item>
          <!-- 新增菜品分类选择 -->
          <el-form-item label="菜品分类" prop="categoryId">
            <el-select v-model="form.categoryId" placeholder="请选择分类" >
              <el-option v-for="category in categoryList" :key="category.id" :label="category.name" :value="String(category.id)" />
            </el-select>
          </el-form-item>
          <el-form-item label="菜品价格" prop="price">
            <el-input 
              v-model="form.price" 
              placeholder="请输入菜品价格" 
              type="number" 
              step="0.01"
            />
          </el-form-item>
          <el-form-item label="菜品图片" prop="image">
            <el-input v-model="form.image" placeholder="请输入图片路径（如：/src/assets/food1.jpg）" />
          </el-form-item>
          <el-form-item label="菜品描述" prop="description">
            <el-input 
              v-model="form.description" 
              placeholder="请输入菜品描述" 
              type="textarea" 
              rows="3"
            />
          </el-form-item>
          <el-form-item label="库存数量" prop="stock">
            <el-input 
              v-model="form.stock" 
              placeholder="0表示无限库存" 
              type="number"
            />
          </el-form-item>
          <el-form-item label="菜品状态" prop="status">
            <el-select v-model="form.status" placeholder="请选择菜品状态">
              <el-option label="上架" value="1" />
              <el-option label="下架" value="0" />
            </el-select>
          </el-form-item>
          <!-- 商品详情图 -->
          <el-form-item label="详情图1" prop="imagePath1">
            <el-upload ref="uploadRef1" action="http://localhost:8080/imgupdate/updateimage" :on-success="handleImageSuccess1" :show-file-list="false" list-type="picture-card" :limit="1" class="avatar" :on-remove="handleImageRemove1">
              <div>
                <img v-if="form.imagePath1" :src="'http://localhost:8080/imeageserver/' + form.imagePath1" alt="" class="avatar-uploader-icon" style="max-width: 178px; max-height: 178px;"/>
                <el-icon v-else ><Plus /></el-icon>
              </div>
            </el-upload>
          </el-form-item>
          <el-form-item label="详情图2" prop="imagePath2">
            <el-upload ref="uploadRef2" action="http://localhost:8080/imgupdate/updateimage" :on-success="handleImageSuccess2" :show-file-list="false" list-type="picture-card" :limit="1" class="avatar" :on-remove="handleImageRemove2">
              <div>
                <img v-if="form.imagePath2" :src="'http://localhost:8080/imeageserver/' + form.imagePath2" alt="" class="avatar-uploader-icon" style="max-width: 178px; max-height: 178px;"/>
                <el-icon v-else ><Plus /></el-icon>
              </div>
            </el-upload>
          </el-form-item>
          <el-form-item label="详情图3" prop="imagePath3">
            <el-upload ref="uploadRef3" action="http://localhost:8080/imgupdate/updateimage" :on-success="handleImageSuccess3" :show-file-list="false" list-type="picture-card" :limit="1" class="avatar" :on-remove="handleImageRemove3">
              <div>
                <img v-if="form.imagePath3" :src="'http://localhost:8080/imeageserver/' + form.imagePath3" alt="" class="avatar-uploader-icon" style="max-width: 178px; max-height: 178px;"/>
                <el-icon v-else ><Plus /></el-icon>
              </div>
            </el-upload>
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
// 脚本部分完全保持不变
import { ref, reactive, onMounted, getCurrentInstance } from 'vue';
import { ElMessage, ElMessageBox, ElLoading, ElIcon } from 'element-plus';
import productApi from '../../../../api/productApi.js';
import ProductCategoryApi from '../../../../api/ProductCategoryApi.js'
let shopid=localStorage.getItem("shopid");
let categoryList=ref();
const loading = ref(false);
const productList = ref([]);
// 图片上传相关变量
const uploadRef1 = ref(null);
const uploadRef2 = ref(null);
const uploadRef3 = ref(null);
const searchForm = reactive({
  id: '',
  name: '',
  status: ''
});
const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
});
const dialogVisible = ref(false);
const dialogType = ref('add');
const form = reactive({
  id: '',
  shopId: shopid,
  name: '',
  categoryId:'',
  price: 0,
  image: '',
  description: '',
  stock: 0,
  status: '',
  category: '',
  imagePath1: '',
  imagePath2: '',
  imagePath3: ''
});
const formRules = reactive({
  name: [
    { required: true, message: '请输入菜品名称', trigger: 'blur' },
    { max: 100, message: '菜品名称不能超过100个字符', trigger: 'blur' }
  ],
  price: [
    { required: true, message: '请输入菜品价格', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const num = Number(value);
        if (isNaN(num)) {
          callback(new Error('请输入有效的数字'));
        } else if (num < 0) {
          callback(new Error('价格不能为负数'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  image: [
    { required: true, message: '请输入图片路径', trigger: 'blur' }
  ],
  stock: [
    { required: true, message: '请输入库存数量', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        const num = Number(value);
        if (isNaN(num) || num < 0) {
          callback(new Error('库存不能为负数'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  status: [
    { required: true, message: '请选择菜品状态', trigger: 'change' }
  ],
  categoryId: [
    { required: true, message: '请选择菜品分类', trigger: 'change' }
  ]
});
const formRef = ref(null);

onMounted(() => {
  fetchProductList();
});

const handleEdit = async (row) => {
  dialogType.value = 'edit';
  try {
    // 确保shopid是数字类型
    const shopIdNumber = parseInt(shopid);
    let res1=await ProductCategoryApi.selectcategorybyshopid(shopIdNumber);
    categoryList.value=res1.data.productCategoryList;
    console.log("获取分类列表:",res1.data.productCategoryList);
    const res = await productApi.selectById(row.id);
    console.log("获取商品对象:",res)
    if (res.code === 200 && res.data.entity) {
      const product = res.data.entity;
      form.id = product.id;
      form.name = product.name;
      form.price = product.price;
      form.image = product.image;
      form.description = product.description;
      form.stock = product.stock;
      form.status = String(product.status);
      form.categoryId = String(product.categoryId) || '';
      // 处理详情图字段
      form.imagePath1 = product.imagePath1 || '';
      form.imagePath2 = product.imagePath2 || '';
      form.imagePath3 = product.imagePath3 || '';
      
      if (formRef.value) {
        formRef.value.clearValidate();
      }
      dialogVisible.value = true;
    } else {
      ElMessage.error('获取菜品详情失败');
    }
  } catch (error) {
    console.error('获取菜品详情异常:', error);
    ElMessage.error('网络异常，请稍后重试');
  }
};

const fetchProductList = async () => {
  loading.value = true;
  try {
    const params = {
      page: pagination.currentPage,
      where: []
    };
    params.where.push({
      column: 'shop_id',
      type: 'eq',
      value: shopid
    });
    if (searchForm.id) {
      params.where.push({
        column: 'id',
        type: 'eq',
        value: searchForm.id
      });
    }
    if (searchForm.name) {
      params.where.push({
        column: 'name',
        type: 'like',
        value: searchForm.name
      });
    }
    if (searchForm.status != '') {
      params.where.push({
        column: 'status',
        type: 'eq',
        value: searchForm.status
      });
    }
    const res = await productApi.ProductSelect(params);
    console.log("商品列表",res)
    if (res.code === 200 && res.data.pageInfo) {
      productList.value = res.data.pageInfo.list;
      pagination.total = res.data.pageInfo.total;
    } else {
      ElMessage.error('获取菜品列表失败');
    }
  } catch (error) {
    console.error('获取菜品列表异常:', error);
    ElMessage.error('网络异常，请稍后重试');
  } finally {
    loading.value = false;
  }
};

const handleSearch = () => {
  pagination.currentPage = 1;
  fetchProductList();
};

const handleReset = () => {
  searchForm.id = '';
  searchForm.name = '';
  searchForm.status = '';
  pagination.currentPage = 1;
  fetchProductList();
};

const handleAdd = async() => {
  dialogType.value = 'add';
  form.id = '';
  form.name = '';
  form.price = 0;
  form.image = '';
  form.description = '';
  form.stock = 0;
  // 核心修改：将status初始值改为空字符串，显示占位符
  form.status = '';
  form.categoryId = '';
  // 重置详情图字段
  form.imagePath1 = '';
  form.imagePath2 = '';
  form.imagePath3 = '';
  
  if (formRef.value) {
    formRef.value.clearValidate();
  }
  // 确保shopid是数字类型
  const shopIdNumber = parseInt(shopid);
  let res=await ProductCategoryApi.selectcategorybyshopid(shopIdNumber);
  categoryList.value=res.data.productCategoryList;
  dialogVisible.value = true;
};

const handleSubmit = async () => {
  try {
    await formRef.value.validate();
    
    let res;
    if (dialogType.value === 'add') {
      res = await productApi.AddProduct(form);
    } else {
      res = await productApi.UpdateProduct(form);
    }
    if (res.code === 200) {
      ElMessage.success(dialogType.value === 'add' ? '增加成功' : '编辑成功');
      dialogVisible.value = false;
      fetchProductList();
    } else {
      ElMessage.error(res.message || (dialogType.value === 'add' ? '增加失败' : '编辑失败'));
    }
  } catch (error) {
    if (error.name === 'Error') {
      console.error('提交异常:', error);
      ElMessage.error('网络异常，请稍后重试');
    }
  }
};

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该菜品吗？删除后不可恢复！',
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    const res = await productApi.DelProduct(id);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      fetchProductList();
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

const handleCurrentChange = (val) => {
  pagination.currentPage = val;
  fetchProductList();
};

// 图片上传成功处理方法
function handleImageSuccess1(imgSuccess){
  form.imagePath1 = imgSuccess.data.imgpath;
  // 上传成功后清空文件列表，允许更换图片
  if (uploadRef1.value) {
    uploadRef1.value.clearFiles();
  }
}

function handleImageSuccess2(imgSuccess){
  form.imagePath2 = imgSuccess.data.imgpath;
  // 上传成功后清空文件列表，允许更换图片
  if (uploadRef2.value) {
    uploadRef2.value.clearFiles();
  }
}

function handleImageSuccess3(imgSuccess){
  form.imagePath3 = imgSuccess.data.imgpath;
  // 上传成功后清空文件列表，允许更换图片
  if (uploadRef3.value) {
    uploadRef3.value.clearFiles();
  }
}

// 图片删除处理方法
const handleImageRemove1 = () => {
  form.imagePath1 = '';
};

const handleImageRemove2 = () => {
  form.imagePath2 = '';
};

const handleImageRemove3 = () => {
  form.imagePath3 = '';
};
</script>

<style scoped>
.product-manage {
  padding: 20px;
  box-sizing: border-box;
}

/* 核心：搜索区域顶部对齐 + 高度自适应 */
.search-container {
  width: 100%;
  
}

.search-form {
  display: flex;
  align-items: flex-start; /* 顶部对齐，消除垂直方向的空白 */
  flex-wrap: nowrap; /* 强制不换行 */
  gap: 12px; /* 组件间距 */
  background-color: #f5f7fa;
  padding: 5px 10px;
  border-radius: 8px;
  width: 100%; /* 占满容器宽度，消除右侧空白 */
  min-height: 60px; /* 保证最小高度，避免内容挤压 */
}

/* 表单项目样式重置（顶部对齐） */
.form-item {
  margin: 0 !important;
  display: flex;
  align-items: center; /* 表单内元素垂直居中 */
  height: 36px; /* 与输入框高度一致，保证对齐 */
}

/* 按钮组样式（顶部对齐 + 无额外间距） */
.btn-group {
  margin: 0 !important;
  display: flex;
  align-items: center;
  margin-left: auto !important; /* 靠右对齐 */
  flex-shrink: 0; /* 不压缩 */
  gap: 8px; /* 按钮间距 */
  height: 36px; /* 与输入框高度一致，保证对齐 */
}


/* 标签样式优化（避免换行） */
:deep(.el-form-item__label) {
  white-space: nowrap;
  padding-right: 8px;
  font-size: 14px;
}

/* 输入框/下拉框统一高度 */
:deep(.el-input__wrapper),
:deep(.el-select__wrapper) {
  height: 36px !important;
}

/* 核心修改：分页组件居中（基于Flexbox布局） */
.pagination-container {
  margin-top: 20px;
  display: flex; /* 启用Flex布局 */
  justify-content: center; /* 水平居中 */
  align-items: center; /* 垂直居中（可选，优化视觉） */
  padding: 10px 0; /* 增加上下内边距，提升间距 */
}

/* 适配小窗口：分页组件自动换行 */
@media (max-width: 768px) {
  .pagination-container {
    flex-wrap: wrap; /* 小屏时允许分页元素换行 */
    gap: 10px; /* 换行后元素间距 */
  }
}
.pagination-container {
  margin-top: 20px;
}
</style>
