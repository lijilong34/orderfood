import request from '../../utils/request'

// 获取系统设置
export const getSystemSettings = () => {
  return request({
    url: '/system-settings/get',
    method: 'get'
  })
}

// 保存基本设置
export const saveBasicSettings = (data) => {
  return request({
    url: '/system-settings/save-basic',
    method: 'post',
    data
  })
}

// 保存安全设置
export const saveSecuritySettings = (data) => {
  return request({
    url: '/system-settings/save-security',
    method: 'post',
    data
  })
}

// 保存备份设置
export const saveBackupSettings = (data) => {
  return request({
    url: '/system-settings/save-backup',
    method: 'post',
    data
  })
}

// 修改当前登录用户的密码
export const changeAdminPassword = (data) => {
  return request({
    url: '/system-settings/change-password',
    method: 'post',
    data
  })
}

// 上传SSL证书
export const uploadSSLCertificate = (formData, uploadPath) => {
  return request({
    url: `/system-settings/upload-ssl${uploadPath ? '?uploadPath=' + uploadPath : ''}`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 删除SSL证书
export const removeSSLCertificate = () => {
  return request({
    url: '/system-settings/remove-ssl',
    method: 'delete'
  })
}



// 重启系统
export const restartSystem = () => {
  return request({
    url: '/system-settings/restart',
    method: 'post'
  })
}

// 上传系统Logo
export const uploadSystemLogo = (formData, uploadPath) => {
  return request({
    url: `/system-settings/upload-logo${uploadPath ? '?uploadPath=' + uploadPath : ''}`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 计算密码强度
export const calculatePasswordStrength = (password) => {
  return request({
    url: '/system-settings/password-strength',
    method: 'post',
    data: { password }
  })
}