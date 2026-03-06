<template>
  <div class="login-container">
	  <div class="login-bg"></div>
    <el-card class="login-card">
      <h1 class="title">管理人员登录</h1>
      <!-- 可选：给表单绑定回车兜底（表单内任意聚焦元素回车都触发） -->
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="loginForm.username"
            prefix-icon="User"
            placeholder="请输入用户名"
            @keyup.enter="handleSubmit"
          ></el-input> <!-- 用户名输入框回车触发登录 -->
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            prefix-icon="Lock"
            placeholder="请输入密码"
            show-password
            @keyup.enter="handleSubmit"
          ></el-input><!-- 密码输入框回车触发登录 -->
        </el-form-item>
        <el-form-item label="身份" prop="Identity">
          <el-select
            v-model="loginForm.Identity"
            placeholder="请选择身份"
            @keyup.enter="handleSubmit"
          ><!-- 身份下拉框回车触发登录 -->
            <el-option label="管理员" value="0" key="0"></el-option>
            <el-option label="店长" value="1" key="1"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <!-- 调整为弹性布局，让两个按钮并排显示 -->
          <div class="button-group">
            <el-button type="primary" @click="handleSubmit" class="submit-button" size="large" :loading="isload">登录</el-button>
            <el-button type="danger" @click="returnSubmit" class="return-button" size="large">返回</el-button>
          </div>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ElMessage,ElMessageBox } from 'element-plus';
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import loginApi from '../../../../api/loginApi';

const router = useRouter();
const isload = ref(false);
const loginFormRef = ref(null); // 表单引用

// 登录表单数据（Identity 初始值设为 ''，配合表单验证）
const loginForm = ref({
  username: '',
  password: '',
  Identity: ''
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  Identity: [
    { required: true, message: '请选择身份', trigger: 'change' } // 强制选择身份
  ]
};

// 登录处理函数
const handleSubmit = async () => {
  try {
    // 表单验证
    await loginFormRef.value.validate();

    isload.value = true;

    // 构造请求参数
    const subform = {
      ...loginForm.value,
      Identity: Number(loginForm.value.Identity)
    };

    // 调用登录接口
    const res = await loginApi.managerlogin(subform);
    console.log(res.data.shopid + "123");
    console.log("返回信息:", res);

    // 验证接口返回是否正常
    if (!res || res.code !== 200) {
      ElMessage.error(res?.message || '登录失败，请重试！');
      isload.value = false;
      return;
    }

    // 存储用户信息
    localStorage.setItem("token", res.data.token || '');
    localStorage.setItem("username", res.data.username || '');
    localStorage.setItem("identity", subform.Identity); // 存储身份值
    localStorage.setItem("shopid", res.data.shopid || '');
    
    // 核心修改：根据身份显示不同的成功提示
    const identityText = subform.Identity === 0 ? '管理员' : '店长';
    ElMessage.success(`${identityText}登录成功！`);
    
    router.push("/adminmain");
  } catch (error) {
    // 统一处理所有异常（表单验证失败、接口报错、网络错误等）
    console.error("登录异常:", error);
    
    // 判断错误类型
    if (error.name === 'ValidationError' || error.message?.includes('validation')) {
      // 表单验证失败（Element Plus已自动提示，这里可补充全局提示）
      ElMessage.error('表单填写不完整，请检查后重试！');
    } else {
      // 接口请求异常
      ElMessage.error('网络异常或服务器错误，请稍后重试！');
    }
  } finally {
    // 无论成功失败，都停止加载状态
    isload.value = false;
  }
};

// 新增：返回按钮处理函数 - 跳转到 /login 路由
const returnSubmit = () => {
  // 可选：如果表单有输入内容，提示是否放弃编辑（优化用户体验）
  if (loginForm.value.username || loginForm.value.password || loginForm.value.Identity) {
    ElMessageBox.confirm(
      '当前表单已输入内容，返回将放弃编辑，是否继续？',
      '提示',
      {
        confirmButtonText: '继续返回',
        cancelButtonText: '取消',
        type: 'warning'
      }
    ).then(() => {
      // 确认后跳转到 login 页面
      router.push("/login");
    }).catch(() => {
      // 取消返回，不做操作
      ElMessage.info('已取消返回');
    });
  } else {
    // 表单无内容，直接跳转
    router.push("/login");
  }
};
</script>

<style scoped>

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

.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f4f4f4;
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
.title {
  text-align: center;
  margin-bottom: 20px;
  color: #1989fa;
}

.button-group {
  display: flex;
  gap: 200px; /* 两个按钮之间的间距 */
}
/* 调整按钮宽度，平分父容器 */
.submit-button, .return-button {
  flex: 1; /* 两个按钮各占一半宽度 */
  margin-left: 25px;
}
</style>