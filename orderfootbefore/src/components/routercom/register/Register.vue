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
        <p>用户注册</p>
      </div>
      <!-- 确保 form 初始化后再渲染表单 -->
      <el-form 
        v-if="form" 
        ref="registerFormRef" 
        :model="form" 
        :rules="formRules" 
        label-width="100px" 
        class="login-form"
      >
        <!-- 手机号 -->
        <el-form-item label="手机号:" prop="phone" class="form-item">
          <el-input v-model="form.phone" @change="checkphone()" placeholder="请输入手机号" class="input-style" />
        </el-form-item>
        
        <!-- 验证码 -->
        <el-form-item label="验证码:" prop="code" class="form-item">
          <el-row :gutter="10">
            <el-col :span="16">
              <el-input v-model="form.code" placeholder="请输入验证码" class="input-style" />
            </el-col>
            <el-col :span="8">
              <el-button 
                type="text" 
                class="code-btn" 
                :disabled="countDown > 0"
                @click="getCode"
              >
                {{ countDown > 0 ? `${countDown}s后重新获取` : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>
        
        <!-- 密码 -->
        <el-form-item label="密 码:" prop="password" class="form-item">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入6-16位密码" 
            class="input-style" 
            show-password
          />
        </el-form-item>
        
        <!-- 确认密码 -->
        <el-form-item label="确认密码:" prop="confirmPassword" class="form-item">
          <el-input 
            v-model="form.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            class="input-style" 
            show-password
          />
        </el-form-item>

        <!-- 注册按钮 -->
        <el-form-item class="btn-item">
          <el-button type="primary" @click="handleRegister" class="login-btn" :loading="registerstatus">{{registertext }}</el-button>
          <div class="link-group">
            <span>已有账号？</span>
            <a @click="toLogin" class="link">立即登录</a>
          </div>
           <span class="separator">|</span>
           <div class="link-group">
              <span>你是商家？</span>
              <a @click="toaddshop()" class="link">商家入驻</a>
           </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { ElForm, ElFormItem, ElInput, ElButton, ElMessage, ElRow, ElCol } from 'element-plus';
import { ArrowLeft } from '@element-plus/icons-vue';
import registerApi from '../../../../api/registerApi'; // 假设注册接口api
let code=ref();//接收获取到的验证码
let registerstatus=ref(false);//注册按钮状态
let registertext=ref('注册')
let checktext=ref('');
// 响应式表单数据
const form = ref({
  phone: '',
  code: '',
  password: '',
  confirmPassword: ''
});
// 表单验证规则
const formRules = ref({
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { min: 11, max: 11, message: '手机号长度必须11位', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入合法手机号', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (checktext.value === '手机号码已存在') {
          callback(new Error('手机号码已存在,请换个手机号'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ],
  code: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 6, max: 6, message: '验证码长度必须6位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 16, message: '密码长度必须6-16位', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d).+$/, message: '密码需包含字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { 
      validator: (rule, value, callback) => {
        if (value !== form.value.password) {
          callback(new Error('两次输入密码不一致'));
        } else {
          callback();
        }
      },
      trigger: 'blur'
    }
  ]
});

// 表单引用+路由
const registerFormRef = ref(null);
const router = useRouter();

// 验证码倒计时
const countDown = ref(0);

async function checkphone(){
  // 重置检查状态
  checktext.value = '';
  let res=await registerApi.checkPhone(form.value.phone);
  checktext.value=res.data.status;
}

// 跳转到登录页
const toLogin = () => {
  router.push('/login');
};

// 返回首页
const toHome = () => {
  router.push('/');
};

// 获取验证码（模拟）
const getCode = async () => {
  try {
    // 先验证手机号格式
    await registerFormRef.value.validateField('phone');
    //调用获取验证码接口（替换为你的实际接口）
    const res = await registerApi.getCode(form.value.phone);
    if (res.code === 200) {
      ElMessage.success('验证码已发送，请注意查收');
      code.value=res.data.code;
    }
  ///  模拟验证码发送成功，开始倒计时
    countDown.value = 60;
    const timer = setInterval(() => {
      countDown.value--;
      if (countDown.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (error) {
    ElMessage.error('手机号格式错误，请重新输入');
  }
};

// 注册处理函数
const handleRegister = async () => {
  try {
    // 表单验证
    await registerFormRef.value.validate();
    // 调用注册接口（替换为你的实际接口）
    const res = await registerApi.userRegister(form.value);
    registerstatus.value=true;
    registertext.value="正在注册";
    if (res.code === 200) {
      setTimeout(() => {
        ElMessage.success('注册成功！即将跳转到登录页');
        router.push('/login');
      }, 1500);
    } else {
      ElMessage.error(res.message || '注册失败，请重试');
    }
     registerstatus.value=false;
    registertext.value="注册";
    } catch (error) {
     ElMessage.error(error.response?.data?.message || '请检查你填写的信息');
  }
};
function toaddshop(){
  router.push('/addshopbyshop');
}
</script>

<style scoped>
/* 滚动条样式（与登录页一致） */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}
::-webkit-scrollbar-thumb {
  background-color: #ccc;
  border-radius: 3px;
}
::-webkit-scrollbar-track {
  background-color: transparent;
}

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
  /* 核心修改：白色半透明背景（0.8为透明度，可按需调整） */
  background-color: rgba(255, 255, 255, 0.8); 
  border-radius: 8px;
  /* 微调阴影：增加透明度，适配半透明背景 */
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

/* 验证码按钮样式 */
.code-btn {
  width: 100%;
  color: #e54d42;
  border: 1px solid #e54d42;
  border-radius: 4px;
  height: 38px;
  line-height: 36px;
  &:disabled {
    color: #ccc;
    border-color: #ccc;
    cursor: not-allowed;
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

.link-item {
  margin-bottom: 15px;
  text-align: center;
}

.link-group {
  margin-top: 12px;
  font-size: 0.8rem;
  white-space: nowrap;
}

.separator {
  margin: 0 8px;
  color: #999;
}

.link {
  color: #e54d42;
  cursor: pointer;
  text-decoration: none;
  &:hover {
    text-decoration: underline;
  }
}
</style>