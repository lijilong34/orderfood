<template>
  <div class="system-settings">
    <el-card class="settings-header">
      <h2>系统设置</h2>
      <p>管理系统全局配置和参数</p>
    </el-card>

    <el-row :gutter="20">
      <!-- 基本设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Setting /></el-icon>
              <span>基本设置</span>
            </div>
          </template>
          
          <el-form :model="basicSettings" label-width="120px">
            <el-form-item label="系统名称">
              <el-input v-model="basicSettings.systemName" placeholder="请输入系统名称" />
            </el-form-item>
            
            <el-form-item label="系统版本">
              <el-input v-model="basicSettings.systemVersion" disabled />
            </el-form-item>
            
            <el-form-item label="系统语言">
              <el-select v-model="basicSettings.language" placeholder="选择语言">
                <el-option label="简体中文" value="zh-CN" />
                <el-option label="English" value="en-US" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="时区">
              <el-select v-model="basicSettings.timezone" placeholder="选择时区">
                <el-option label="北京时间 (GMT+8)" value="Asia/Shanghai" />
                <el-option label="东京时间 (GMT+9)" value="Asia/Tokyo" />
                <el-option label="纽约时间 (GMT-5)" value="America/New_York" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="系统Logo">
              <el-upload
                class="logo-uploader"
                action="#"
                :show-file-list="false"
                :auto-upload="false"
                accept=".jpg,.jpeg,.png,.gif"
                :on-change="handleLogoUpload"
              >
                <img v-if="basicSettings.logoUrl" :src="basicSettings.logoUrl" class="logo" />
                <el-icon v-else class="logo-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 安全设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Lock /></el-icon>
              <span>安全设置</span>
            </div>
          </template>
          
          <el-form :model="securitySettings" label-width="120px">
            <el-form-item label="密码策略">
              <el-select v-model="securitySettings.passwordPolicy" placeholder="选择密码策略">
                <el-option label="简单 (6位以上)" value="simple" />
                <el-option label="中等 (8位以上，包含字母数字)" value="medium" />
                <el-option label="复杂 (8位以上，包含大小写字母、数字、特殊字符)" value="complex" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="登录超时">
              <el-input-number v-model="securitySettings.loginTimeout" :min="10" :max="480" />
              <span style="margin-left: 10px;">分钟</span>
            </el-form-item>
            
            <el-form-item label="SSL证书上传">
              <el-upload
                class="ssl-uploader"
                action="#"
                :show-file-list="false"
                :auto-upload="false"
                accept=".pem,.crt,.key"
                :on-change="handleSSLUpload"
              >
                <el-button type="primary">选择证书文件</el-button>
                <template #tip>
                  <div class="el-upload__tip">
                    支持.pem、.crt、.key格式文件
                  </div>
                </template>
              </el-upload>
              <div v-if="securitySettings.sslCertificate" class="ssl-info">
                <span>已上传证书：{{ securitySettings.sslCertificate }}</span>
                <el-button type="danger" size="small" @click="removeSSL">删除</el-button>
              </div>
            </el-form-item>
            
            <el-form-item label="验证码设置">
              <el-switch v-model="securitySettings.captchaEnabled" />
              <span style="margin-left: 10px;">启用验证码</span>
            </el-form-item>
            
            <el-form-item label="域名绑定">
              <el-input v-model="securitySettings.domainBinding" placeholder="请输入绑定的域名" />
              <div class="domain-tip">
                <span>示例：example.com 或 *.example.com</span>
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px;">
      <!-- 管理员密码修改 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><Key /></el-icon>
              <span>修改密码</span>
            </div>
          </template>
          
          <el-form :model="passwordChange" label-width="120px" :rules="passwordRules" ref="passwordFormRef">
            <el-form-item label="当前密码" prop="currentPassword">
              <el-input v-model="passwordChange.currentPassword" type="password" placeholder="请输入当前密码" show-password />
            </el-form-item>
            
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="passwordChange.newPassword" type="password" placeholder="请输入新密码" show-password />
            </el-form-item>
            
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="passwordChange.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
            </el-form-item>
            
            <el-form-item>
              <div class="password-strength">
                <span>密码强度：</span>
                <el-progress :percentage="passwordStrength" :color="strengthColor" :stroke-width="8" />
                <span class="strength-text">{{ strengthText }}</span>
              </div>
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="changePassword">修改密码</el-button>
              <el-button @click="resetPasswordForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>

      <!-- 备份设置 -->
      <el-col :span="12">
        <el-card class="settings-card">
          <template #header>
            <div class="card-header">
              <el-icon><FolderOpened /></el-icon>
              <span>备份设置</span>
            </div>
          </template>
          
          <el-form :model="backupSettings" label-width="120px">
            <el-form-item label="自动备份">
              <el-switch v-model="backupSettings.autoBackup" />
            </el-form-item>
            
            <el-form-item label="备份频率" v-if="backupSettings.autoBackup">
              <el-select v-model="backupSettings.backupFrequency" placeholder="选择备份频率">
                <el-option label="每天" value="daily" />
                <el-option label="每周" value="weekly" />
                <el-option label="每月" value="monthly" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="备份时间" v-if="backupSettings.autoBackup">
              <el-time-picker v-model="backupSettings.backupTime" placeholder="选择备份时间" />
            </el-form-item>
            
            <el-form-item label="保留天数">
              <el-input-number v-model="backupSettings.retentionDays" :min="1" :max="365" />
              <span style="margin-left: 10px;">天</span>
            </el-form-item>
            
            <el-form-item label="备份路径">
              <el-input v-model="backupSettings.backupPath" placeholder="/backup" />
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>

    

    <!-- 操作按钮 -->
    <div class="action-buttons">
      <el-button type="primary" @click="saveSettings" :loading="saving">
        <el-icon><Check /></el-icon>
        保存设置
      </el-button>
      <el-button @click="resetSettings">
        <el-icon><RefreshLeft /></el-icon>
        重置
      </el-button>
      <el-button type="danger" @click="restartSystem">
        <el-icon><VideoPlay /></el-icon>
        重启系统
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Setting, Lock, Message, FolderOpened, Check, RefreshLeft, VideoPlay, Plus } from '@element-plus/icons-vue'
import {
  getSystemSettings,
  saveBasicSettings as saveBasicSettingsApi,
  saveSecuritySettings as saveSecuritySettingsApi,
  saveBackupSettings as saveBackupSettingsApi,
  changeAdminPassword,
  uploadSSLCertificate,
  removeSSLCertificate,
  restartSystem as restartSystemService,
  uploadSystemLogo,
  calculatePasswordStrength as checkPasswordStrength
} from '../../../api/systemSettings'

// 基本设置
const basicSettings = reactive({
  systemName: '',
  systemVersion: '',
  language: '',
  timezone: '',
  logoUrl: ''
})

// 安全设置
const securitySettings = reactive({
  passwordPolicy: '',
  loginTimeout: 30,
  sslCertificate: '',
  captchaEnabled: true,
  domainBinding: ''
})

// 密码修改
const passwordFormRef = ref()
const passwordChange = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 密码强度计算
const passwordStrength = ref(0)
const strengthText = ref('弱')
const strengthColor = ref('#f56c6c')

// 保存状态
const saving = ref(false)

// 密码验证规则
const passwordRules = reactive({
  currentPassword: [
    { required: true, message: '请输入当前密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordChange.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 备份设置

const backupSettings = reactive({

  autoBackup: true,

  backupFrequency: 'daily',

  backupTime: new Date(2000, 1, 1, 2, 0, 0),

  retentionDays: 30,

  backupPath: '/backup'

})





// 页面加载时获取系统设置
onMounted(() => {
  loadSystemSettings()
})

// 加载系统设置
const loadSystemSettings = async () => {
  try {
    const response = await getSystemSettings()
    if (response.code === 200) {
      const settings = response.data.settings
      Object.assign(basicSettings, {
        systemName: settings.systemName,
        version: settings.systemVersion,
        language: settings.language,
        timezone: settings.timezone,
        logoUrl: settings.logoUrl
      })
      
      Object.assign(securitySettings, {
        passwordPolicy: settings.passwordPolicy,
        loginTimeout: settings.loginTimeout,
        sslCertificate: settings.sslCertificate,
        captchaEnabled: settings.captchaEnabled,
        domainBinding: settings.domainBinding
      })
      
      // 处理备份时间，将字符串转换为Date对象
      if (settings.backupTime) {
        try {
          // 如果是字符串格式 "HH:mm:ss"
          if (typeof settings.backupTime === 'string') {
            const timeStr = settings.backupTime
            const [hours, minutes] = timeStr.split(':')
            const date = new Date()
            date.setHours(parseInt(hours), parseInt(minutes), 0, 0)
            backupSettings.backupTime = date
          } else {
            backupSettings.backupTime = new Date(settings.backupTime)
          }
        } catch (error) {
          console.error('备份时间格式转换失败:', error)
          // 设置默认时间
          const defaultDate = new Date()
          defaultDate.setHours(2, 0, 0, 0)
          backupSettings.backupTime = defaultDate
        }
      }
      
      Object.assign(backupSettings, {
        autoBackup: settings.autoBackup,
        backupFrequency: settings.backupFrequency,
        backupTime: settings.backupTime,
        retentionDays: settings.retentionDays,
        backupPath: settings.backupPath
      })
    }
  } catch (error) {
    console.error('获取系统设置失败:', error)
    ElMessage.error('获取系统设置失败')
  }
}



// Logo上传处理
const handleLogoUpload = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    
    const response = await uploadSystemLogo(formData)
    if (response.code === 200) {
      ElMessage.success('Logo上传成功')
      loadSystemSettings() // 重新加载设置以获取新的Logo URL
    } else {
      ElMessage.error(response.message || 'Logo上传失败')
    }
  } catch (error) {
    console.error('Logo上传失败:', error)
    ElMessage.error('Logo上传失败')
  }
}

// SSL证书上传处理
const handleSSLUpload = async (file) => {
  try {
    const formData = new FormData()
    formData.append('file', file.raw)
    
    const response = await uploadSSLCertificate(formData)
    if (response.code === 200) {
      ElMessage.success('SSL证书上传成功')
      securitySettings.sslCertificate = file.name
    } else {
      ElMessage.error(response.message || 'SSL证书上传失败')
    }
  } catch (error) {
    console.error('SSL证书上传失败:', error)
    ElMessage.error('SSL证书上传失败')
  }
}

// 删除SSL证书
const removeSSL = async () => {
  try {
    const response = await removeSSLCertificate()
    if (response.code === 200) {
      ElMessage.success('SSL证书已删除')
      securitySettings.sslCertificate = ''
    } else {
      ElMessage.error(response.message || 'SSL证书删除失败')
    }
  } catch (error) {
    console.error('SSL证书删除失败:', error)
    ElMessage.error('SSL证书删除失败')
  }
}

// 计算密码强度
const calculatePasswordStrength = async (password) => {
  if (!password) {
    passwordStrength.value = 0
    strengthText.value = '弱'
    strengthColor.value = '#f56c6c'
    return
  }

  try {
    const response = await checkPasswordStrength(password)
    if (response.code === 200) {
      const strengthInfo = response.data.passwordStrength
      passwordStrength.value = strengthInfo.strength
      strengthText.value = strengthInfo.strengthText
      strengthColor.value = strengthInfo.strengthColor
    }
  } catch (error) {
    console.error('计算密码强度失败:', error)
    // 如果API调用失败，使用本地计算
    localCalculatePasswordStrength(password)
  }
}

// 本地密码强度计算（备用方案）
const localCalculatePasswordStrength = (password) => {
  let strength = 0
  
  // 长度检测
  if (password.length >= 8) strength += 25
  if (password.length >= 12) strength += 15
  
  // 字符类型检测
  if (/[a-z]/.test(password)) strength += 15
  if (/[A-Z]/.test(password)) strength += 15
  if (/[0-9]/.test(password)) strength += 15
  if (/[^a-zA-Z0-9]/.test(password)) strength += 15
  
  passwordStrength.value = Math.min(strength, 100)
  
  if (passwordStrength.value < 40) {
    strengthText.value = '弱'
    strengthColor.value = '#f56c6c'
  } else if (passwordStrength.value < 70) {
    strengthText.value = '中等'
    strengthColor.value = '#e6a23c'
  } else {
    strengthText.value = '强'
    strengthColor.value = '#67c23a'
  }
}

// 监听新密码变化
watch(() => passwordChange.newPassword, (newPassword) => {
  calculatePasswordStrength(newPassword)
})

// 修改密码
const changePassword = async () => {
  try {
    await passwordFormRef.value.validate()
    
    // 检查token是否存在
    const token = localStorage.getItem('token')
    if (!token) {
      ElMessage.error('请先登录')
      return
    }
    
    const response = await changeAdminPassword({
      currentPassword: passwordChange.currentPassword,
      newPassword: passwordChange.newPassword
    })
    
    if (response.code === 200) {
      ElMessage.success('密码修改成功')
      resetPasswordForm()
    } else {
      ElMessage.error(response.message || '密码修改失败')
    }
  } catch (error) {
    if (error.message) {
      ElMessage.error('密码修改失败: ' + error.message)
    }
  }
}

// 重置密码表单
const resetPasswordForm = () => {
  passwordFormRef.value.resetFields()
  passwordStrength.value = 0
  strengthText.value = '弱'
  strengthColor.value = '#f56c6c'
}

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage < 50) return '#67c23a'
  if (percentage < 80) return '#e6a23c'
  return '#f56c6c'
}

// 保存设置
const saveSettings = async () => {
  saving.value = true
  
  try {
    // 保存基本设置
    const basicResponse = await saveBasicSettingsApi({
      systemName: basicSettings.systemName,
      language: basicSettings.language,
      timezone: basicSettings.timezone,
      logoUrl: basicSettings.logoUrl
    })
    
    if (basicResponse.code !== 200) {
      throw new Error(basicResponse.message || '基本设置保存失败')
    }
    
    // 保存安全设置
    const securityResponse = await saveSecuritySettingsApi({
      passwordPolicy: securitySettings.passwordPolicy,
      loginTimeout: securitySettings.loginTimeout,
      captchaEnabled: securitySettings.captchaEnabled,
      domainBinding: securitySettings.domainBinding
    })
    
    if (securityResponse.code !== 200) {
      throw new Error(securityResponse.message || '安全设置保存失败')
    }
    
    // 保存备份设置
    // 格式化备份时间为字符串
    let formattedBackupTime = null
    if (backupSettings.backupTime) {
      const date = new Date(backupSettings.backupTime)
      formattedBackupTime = `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}:00`
    }
    
    const backupResponse = await saveBackupSettingsApi({
      autoBackup: backupSettings.autoBackup,
      backupFrequency: backupSettings.backupFrequency,
      backupTime: formattedBackupTime,
      retentionDays: backupSettings.retentionDays,
      backupPath: backupSettings.backupPath
    })
    
    if (backupResponse.code !== 200) {
      throw new Error(backupResponse.message || '备份设置保存失败')
    }
    
    ElMessage.success('所有设置保存成功')
  } catch (error) {
    console.error('保存设置失败:', error)
    ElMessage.error('保存设置失败: ' + error.message)
  } finally {
    saving.value = false
  }
}

// 重置设置
const resetSettings = () => {
  ElMessageBox.confirm('确定要重置所有设置吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    loadSystemSettings()
    ElMessage.success('设置已重置')
  })
}

// 重启系统
const restartSystem = () => {
  ElMessageBox.confirm('确定要重启系统吗？这将中断所有用户连接。', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      const response = await restartSystemService()
      if (response.code === 200) {
        ElMessage.success(response.message || '系统重启指令已发送')
      } else {
        ElMessage.error(response.message || '系统重启失败')
      }
    } catch (error) {
      console.error('系统重启失败:', error)
      ElMessage.error('系统重启失败')
    }
  })
}


</script>

<style scoped>
.system-settings {
  padding: 20px;
}

.settings-header {
  margin-bottom: 20px;
}

.settings-header h2 {
  margin: 0 0 8px 0;
  color: #303133;
}

.settings-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.settings-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
}

.card-header .el-icon {
  margin-right: 8px;
  color: #409eff;
}

.logo-uploader {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-uploader:hover {
  border-color: #409eff;
}

.logo-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 100px;
  text-align: center;
  line-height: 100px;
}

.logo {
  width: 100px;
  height: 100px;
  object-fit: cover;
}

.ssl-info {
  margin-top: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.domain-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
}



.action-buttons {
  margin-top: 30px;
  text-align: center;
}

.action-buttons .el-button {
  margin: 0 10px;
  min-width: 120px;
}
</style>