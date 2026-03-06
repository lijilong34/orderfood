<template>
  <div class="login-container">
    <div class="login-bg"></div>
    <div class="login-card">
      <!-- 左上角返回首页按钮 -->
      <div class="back-home-btn">
        <el-button @click="toHome" type="text" class="back-btn">
          <el-icon><ArrowLeft /></el-icon> 返回首页
        </el-button>
      </div>
      
      <div class="login-title">
        <h1>御膳房</h1>
        <p>用户登录</p>
      </div>
      
      <el-form 
        v-if="form" 
        ref="loginFormRef" 
        :model="form" 
        :rules="formRules" 
        label-width="60px" 
        class="login-form"
      >
        <!-- 手机号输入框 -->
        <el-form-item label="手机号:" prop="phone" class="form-item">
          <el-input v-model="form.phone" placeholder="请输入手机号" class="input-style" @keyup.enter="handleLogin"></el-input>
        </el-form-item>
        <!-- 密码输入框 -->
        <el-form-item label="密&nbsp;&nbsp;&nbsp;码:" prop="password" class="form-item">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" class="input-style" @keyup.enter="handleLogin"></el-input>
        </el-form-item>
        <!-- 链接容器：flex强制同行 -->
        <el-form-item class="link-group">
			<div>
				<a @click="toForgetPassword" class="link-item">忘记密码？</a>
				<span class="split-line">|</span>
				<a @click="toRegister" class="link-item">还没有账号？立即注册</a>
				<span class="split-line">|</span>
				<a @click="toAdminLogin" class="link-item">需要管理员权限？点击登录</a>
			</div>
         
        </el-form-item>
        <el-form-item class="btn-item">
          <el-button type="primary" @click="handleLogin" class="login-btn">登录</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import loginApi from '../../../../api/loginApi';

const form = ref({
  phone: '',
  password: ''
});

const formRules = ref({
  phone: [
    { required: false, message: '请输入手机号', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号长度必须11位', trigger: 'blur' }
  ],
  password: [
    { required: false, message: '请输入密码', trigger: 'blur' }
  ]
});

const loginFormRef = ref(null);
const router = useRouter();

// 跳转到注册页
const toRegister = () => {
  router.push('/register');
};

// 跳转到管理员登录页
const toAdminLogin = () => {
  router.push('/adminlogin');
};

// 跳转到忘记密码页
const toForgetPassword = () => {
  router.push('/forget-password');
};

// 返回首页
const toHome = () => {
  router.push('/');
};

// 登录逻辑
const handleLogin = async () => {
  try {
    await loginFormRef.value.validate();
    const response = await loginApi.Userlogin(form.value);
    if (response.code === 200) {
      ElMessage.success('登录成功！');
      localStorage.setItem("phone", response.data.user.phone);
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("avatar", response.data.user.avatar);
      router.push('/');
    } else {
      ElMessage.error(response.message || '登录失败');
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '登录异常，请稍后重试');
  }
};
</script>

<style scoped>
.login-container {
  position: relative;
  width: 100vw;
  height: 100vh;
  overflow: auto;
}

.login-bg {
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

.login-card {
  position: relative;
  z-index: 10;
  width: 90%;
  max-width: 450px;
  margin: 80px auto;
  padding: 25px 20px;
  background-color: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.15);
}

/* 返回首页按钮样式 */
.back-home-btn {
  position: absolute;
  top: 15px;
  left: 15px;
  z-index: 20;
}

.back-btn {
  color: #e54d42;
  font-size: 14px;
  padding: 5px 10px;
  &:hover {
    color: #d43829;
    background-color: rgba(229, 77, 66, 0.1);
  }
}

.login-title {
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

.login-form {
  margin-top: 5px;
}

.form-item {
  margin-bottom: 15px;
}

/* 链接容器：flex强制同行 */
.link-group {
  margin-bottom: 15px;
  display: flex; /* 核心：flex布局 */
  align-items: center;
  justify-content: center; /* 居中对齐（匹配设计图） */
  flex-wrap: nowrap; /* 禁止换行 */
  font-size: 0.8rem;
}

/* 链接样式 */
.link-item {
  color: #e54d42;
  cursor: pointer;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
}

/* 分隔线样式 */
.split-line {
  margin: 0 6px; /* 调整间距，匹配设计图 */
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

.login-btn {
  width: 100%;
  height: 40px;
  border-radius: 4px;
  background-color: #e54d42;
  border: none;
  font-size: 1rem;
  margin-bottom: 10px;
  &:hover {
    background-color: #d43829;
  }
}

.home-btn {
  width: 100%;
  height: 40px;
  border-radius: 4px;
  background-color: #fff;
  border: 1px solid #e54d42;
  color: #e54d42;
  font-size: 1rem;
  &:hover {
    background-color: #f5f5f5;
  }
}
</style>