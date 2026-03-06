<template>
  <div class="forget-password-container">
    <div class="forget-password-bg"></div>
    <div class="forget-password-card">
      <div class="forget-password-title">
        <h1>御膳房</h1>
        <p>忘记密码</p>
      </div>
      
      <el-form 
        ref="forgetFormRef" 
        :model="form" 
        :rules="formRules" 
        label-width="70px" 
        class="forget-password-form"
      >
        <!-- 手机号输入框 -->
        <el-form-item label="手机号:" prop="phone" class="form-item">
          <el-input v-model="form.phone" placeholder="请输入手机号" class="input-style"></el-input>
        </el-form-item>
        
        <!-- 验证码输入框 -->
        <el-form-item label="验证码:" prop="code" class="form-item">
          <div class="code-input-wrapper">
            <el-input v-model="form.code" placeholder="请输入验证码" class="input-style code-input"></el-input>
            <el-button 
              type="primary" 
              @click="sendCode" 
              class="code-btn"
              :disabled="countdown > 0"
            >
              {{ countdown > 0 ? `${countdown}秒` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
        
        <!-- 新密码输入框 -->
        <el-form-item label="新密码:" prop="newPassword" class="form-item">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" class="input-style"></el-input>
        </el-form-item>
        
        <!-- 确认密码输入框 -->
        <el-form-item label="确认密码:" prop="confirmPassword" class="form-item">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" class="input-style"></el-input>
        </el-form-item>
        
        <!-- 链接容器 -->
        <el-form-item class="link-group">
          <div>
            <a @click="toLogin" class="link-item">返回登录</a>
            <span class="split-line">|</span>
            <a @click="toRegister" class="link-item">还没有账号？立即注册</a>
          </div>
        </el-form-item>
        
        <el-form-item class="btn-item">
          <el-button type="primary" @click="handleResetPassword" class="submit-btn">重置密码</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus';
import registerApi from '../../../../api/registerApi';
import ForgetPasswordApi from '../../../../api/ForgetPasswordApi';
let code=ref();

const form = ref({
  phone: '',
  code: '',
  newPassword: '',
  confirmPassword: ''
});

const countdown = ref(0);
const forgetFormRef = ref(null);
const router = useRouter();

// 表单验证规则
const formRules = ref({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { pattern: /^\d{6}$/, message: '验证码为6位数字', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度在6-20位之间', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== form.value.newPassword) {
          callback(new Error('两次输入的密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

// 跳转到登录页
const toLogin = () => {
  router.push('/login');
};

// 跳转到注册页
const toRegister = () => {
  router.push('/register');
};

// 发送验证码
const sendCode = async() => {
  if (!form.value.phone) {
    ElMessage.warning('请先输入手机号');
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(form.value.phone)) {
    ElMessage.warning('请输入正确的手机号');
    return;
  }
   
  const res1=await ForgetPasswordApi.selectphoneexists(form.value.phone);
  if(res1.code!=200){
    return;
  }

   const res = await registerApi.getCode(form.value.phone);
    if (res.code === 200) {
      ElMessage.success('验证码已发送，请注意查收');
      code.value=res.data.code;
    }
  
  // 开始倒计时
  countdown.value = 60;
  const timer = setInterval(() => {
    countdown.value--;
    if (countdown.value <= 0) {
      clearInterval(timer);
    }
  }, 1000);
};

// 重置密码
const handleResetPassword = async () => {
  try {
    await forgetFormRef.value.validate();
    if(form.value.code!=code.value){
      ElMessage.error('密码错误')
      return;
    }
    let res = await ForgetPasswordApi.updatepassword({
      phone:form.value.phone,
      password:form.value.confirmPassword
    });
    if(res.code==200){
     ElMessage.success('密码重置成功，请重新登录');
     router.push('/login');
    }
  } catch (error) {
    ElMessage.error('请检查输入信息');
  }
};
</script>

<style scoped>
.forget-password-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: auto;
}

.forget-password-bg {
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
    background-color: rgba(0, 0, 0, 0.4);
  }
}

.forget-password-card {
  position: relative;
  z-index: 10;
  width: 90%;
  max-width: 450px;
  margin: 60px auto;
  padding: 25px 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
}

.forget-password-title {
  text-align: center;
  margin-bottom: 20px;
  h1 {
    font-size: 2.5rem;
    font-weight: bold;
    color: #e54d42;
    margin: 0 0 5px 0;
    text-shadow: 
      0 0 5px rgba(229, 77, 66, 0.5),
      0 0 10px rgba(229, 77, 66, 0.5);
  }
  p {
    font-size: 0.9rem;
    color: #666;
    margin: 0;
  }
}

.forget-password-form {
  margin-top: 5px;
}

.form-item {
  margin-bottom: 15px;
}

.code-input-wrapper {
  display: flex;
  gap: 10px;
  width: 100%;
}

.code-input {
  flex: 1;
}

.code-btn {
  width: 110px;
  height: 38px;
  border-radius: 4px;
  background-color: #e54d42;
  border: none;
  font-size: 0.85rem;
  white-space: nowrap;
  &:hover:not(:disabled) {
    background-color: #d43829;
  }
  &:disabled {
    background-color: #ccc;
    cursor: not-allowed;
  }
}

.link-group {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: nowrap;
  font-size: 0.8rem;
}

.link-item {
  color: #e54d42;
  cursor: pointer;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
}

.split-line {
  margin: 0 6px;
  color: #999;
}

.input-style {
  width: 100%;
  height: 38px;
  border-radius: 4px;
  border: 1px solid #ccc;
  &:focus {
    border-color: #e54d42;
    box-shadow: 0 0 0 2px rgba(229, 77, 66, 0.2);
  }
}

.btn-item {
  margin-top: 8px;
  text-align: center;
}

.submit-btn {
  width: 100%;
  height: 40px;
  border-radius: 4px;
  background-color: #e54d42;
  border: none;
  font-size: 1rem;
  &:hover {
    background-color: #d43829;
  }
}
</style>