<template>
  <div class="addshop-container">
    <div class="addshop-bg"></div>
    <div class="addshop-card">
      <div class="addshop-title">
        <h1>御膳房</h1>
        <p>商家入驻</p>
      </div>
      
      <el-form 
        :rules="formRules" 
        label-width="80px" 
        class="addshop-form"
      >
        <el-form-item label="商家名称:" prop="name" class="form-item">
          <el-input v-model="shopform.name" placeholder="请输入商家名称" class="input-style"></el-input>
        </el-form-item>
        
        <el-form-item label="联系电话:" prop="phone" class="form-item">
          <el-input v-model="shopform.phone" placeholder="请输入联系电话" class="input-style"></el-input>
        </el-form-item>
        <p>店长设置:</p>
        <el-form-item label="账号:" class="form-item">
          <el-input v-model="shopadminform.username" placeholder="请输入店长用户名" class="input-style"></el-input>
        </el-form-item>
        
        <el-form-item label="密码:" class="form-item">
          <el-input v-model="shopadminform.password" placeholder="请输入店长密码" show-password/>
        </el-form-item>
        
        <el-form-item label="身份验证"  class="form-item">
         <el-button @click="openDialog">验证你是真人</el-button><span v-if="dialogVisible">{{Visibletext}}</span>
        </el-form-item>
        
        <el-form-item class="btn-item">
          <el-button type="primary" @click="handleSubmit" class="submit-btn">提交申请</el-button>
          <el-button @click="goBack" class="back-btn">返回</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
  <teleport to="body">
    <el-dialog 
      v-model="dialogVisible" 
      width="380px"
      title="安全验证"
      append-to-body
      @close="closeDialog"
    >
      <slide-verify
        v-if="dialogVisible"
        :l="42"
        :r="10"
        :w="310"
        :h="155"
        :show="true"
        slider-text="向右滑动"
        @success="onSuccess"
        @fail="onFail"
        @refresh="onRefresh"
      ></slide-verify>
    </el-dialog>
  </teleport>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import SlideVerify from 'vue3-slide-verify';
import 'vue3-slide-verify/dist/style.css';
import ShopApi from '../../../../api/ShopApi';
import EmployeeApi from '../../../../api/EmployeeApi';
import EmployeeMenusApi from '../../../../api/EmployeeMenusApi';
let Visibletext=ref();
let Visibleok=ref(false)
const router = useRouter();
const dialogVisible = ref(false);
const shopform = ref({
  name: '',
  phone: '',
  status:3
});
const shopadminform = ref({
  shopId: '',
  username: '',
  password: '',
  post:'店长'
});
const formRules = ref({});

const handleSubmit = async() => {
  if (!shopform.value.name || !shopform.value.phone || !shopadminform.value.username || !shopadminform.value.password||!Visibleok.value) {
    ElMessage.error('请填写完整信息,并完成验证');
    return;
  }
     let res=await ShopApi.addShop(shopform.value);
     let res1=await ShopApi.selectshopbyshop(shopform.value);
     let shopid=res1.data.shop.id;
     shopadminform.value.shopId=shopid;
     let res2=await EmployeeApi.addEmployee(shopadminform.value);
     let res3= await EmployeeApi.selectemployeebyemployee(shopadminform.value);
     let employeeid=res3.data.employee.id;
     let menus={
      employeeId:employeeid,
      menuIds:[27,28,29,30,31,32,33,34,35,36,37]
     }
     let res4=await EmployeeMenusApi.addEmployeeMenus(menus);
     if(res.code==200&&res1.code==200&&res2.code==200&&res3.code==200&&res4.code==200){
   ElMessage.success('申请提交成功，我们将在1-3个工作日内联系您！');
  setTimeout(() => {
    router.push('/login');
  }, 2000);
}
};

const goBack = () => {
  router.go(-1);
};

const openDialog = () => {
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
};

const onSuccess = () => {
  ElMessage.success('验证成功');
  setTimeout(() => {
    Visibleok.value=true;
    dialogVisible.value = false;
    Visibletext.value='验证成功';
  }, 800);
};

const onFail = () => {
   Visibletext.value='验证失败';
  ElMessage.error('验证失败，请重试');
};

const onRefresh = () => {
  console.log('刷新验证码');
};
</script>

<style scoped>
.addshop-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: auto;
  background-color: #f5f5f5;
}

.addshop-bg {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: url('/src/assets/御膳房2.0.png') center/cover no-repeat;
  &::after {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.3);
  }
}

.addshop-card {
  position: relative;
  z-index: 10;
  width: 90%;
  max-width: 600px;
  margin: 50px auto;
  padding: 30px 25px;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.addshop-title {
  text-align: center;
  margin-bottom: 30px;
  h1 {
    font-size: 2.5rem;
    font-weight: bold;
    color: #e54d42;
    margin: 0 0 8px 0;
    text-shadow: 
      0 0 8px rgba(229, 77, 66, 0.6),
      0 0 15px rgba(229, 77, 66, 0.6);
  }
  p {
    font-size: 1.1rem;
    color: #666;
    margin: 0;
  }
}

.addshop-form {
  margin-top: 10px;
}

.form-item {
  margin-bottom: 20px;
}

.input-style {
  width: 100%;
}

.btn-item {
  margin-top: 25px;
  text-align: center;
}

.submit-btn {
  width: 150px;
  height: 45px;
  border-radius: 6px;
  background-color: #e54d42;
  border: none;
  font-size: 1rem;
  font-weight: 600;
  &:hover {
    background-color: #d43829;
  }
}

.back-btn {
  width: 100px;
  height: 45px;
  margin-left: 15px;
  border-radius: 6px;
  font-size: 1rem;
}

:deep(.el-dialog) {
  z-index: 3000 !important;
}

:deep(.el-dialog__body) {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

:deep(.el-overlay) {
  z-index: 2999 !important;
}
</style>