<template>
  <!-- 外层容器：统一页面布局 -->
  <div class="shop-info-wrapper">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton 
        :rows="8" 
        width="100%" 
        animated 
        class="skeleton-card"
      />
    </div>

    <!-- 店铺信息卡片：数据加载完成后显示 -->
    <div v-else class="shop-info-card">
      <!-- 卡片头部：店铺名称 + 状态标签 + 操作按钮 -->
      <div class="card-header">
        <h2 class="shop-name">{{ shoplist.name || '未设置店铺名称' }}</h2>
        <div class="header-actions">
          <!-- 非编辑模式显示状态标签 -->
          <el-tag 
            v-if="!isEditing" 
            :type="getStatusTagType()" 
            size="medium"
          >
            {{ sta || '未知状态' }}
          </el-tag>
          <!-- 编辑模式显示状态下拉选择框 -->
          <el-select 
            v-else 
            v-model="editForm.status" 
            placeholder="请选择营业状态"
            size="medium"
            style="width: 120px;"
          >
            <el-option label="休息" value="0" />
            <el-option label="营业" value="1" />
            <el-option label="暂停营业" value="2" />
          </el-select>
          <el-button 
            v-if="!isEditing" 
            type="primary" 
            size="small" 
            @click="enterEditMode"
          >
            编辑
          </el-button>
        </div>
      </div>

      <!-- 卡片主体：图片 + 信息列表 -->
      <div class="card-body">
        <!-- 店铺logo区域 -->
        <div class="logo-container">
          <img 
            v-if="shoplist.logo && 'http://localhost:8080/imeageserver/'+shoplist.logo && !isEditing" 
            :src="'http://localhost:8080/imeageserver/'+shoplist.logo" 
            alt="店铺logo" 
            class="shop-logo"
            @error="handleLogoError"
          />
          <!-- 编辑模式下的logo上传 -->
          <el-upload
            v-else-if="isEditing"
            class="logo-uploader"
            drag
            action=""
            :auto-upload="false"
            :show-file-list="false"
            :on-change="handleLogoChange"
          >
            <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
            <div class="el-upload__text">将图片拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">
                仅支持jpg/png文件，且不超过2MB
              </div>
            </template>
          </el-upload>
          <!-- logo为空/加载失败时的兜底 -->
          <div v-else class="default-logo">
            <el-icon size="48"><Shop /></el-icon>
            <p>暂无店铺LOGO</p>
          </div>
        </div>

        <!-- 店铺信息列表 -->
        <div class="info-list">
          <!-- 展示模式 -->
          <div v-if="!isEditing" class="info-item">
            <span class="info-label">店铺地址：</span>
            <span class="info-value">{{ shoplist.address || '未填写地址' }}</span>
          </div>
          <!-- 编辑模式 -->
          <div v-else class="info-item">
            <span class="info-label">店铺地址：</span>
            <el-input 
              v-model="editForm.address" 
              placeholder="请输入店铺地址"
              style="width: 300px;"
            />
          </div>

          <!-- 联系电话 -->
          <div v-if="!isEditing" class="info-item">
            <span class="info-label">联系电话：</span>
            <span class="info-value">{{ shoplist.phone || '未预留电话' }}</span>
          </div>
          <div v-else class="info-item">
            <span class="info-label">联系电话：</span>
            <el-input 
              v-model="editForm.phone" 
              placeholder="请输入联系电话"
              style="width: 300px;"
              maxlength="11"
            />
          </div>

          <!-- 营业时间 -->
          <div v-if="!isEditing" class="info-item">
            <span class="info-label">营业时间：</span>
            <span class="info-value">{{ shoplist.businessHours || '未设置营业时间' }}</span>
          </div>
          <div v-else class="info-item">
            <span class="info-label">营业时间：</span>
            <el-input 
              v-model="editForm.businessHours" 
              placeholder="请输入营业时间，如：09:00-22:00"
              style="width: 300px;"
            />
          </div>

          <!-- 经营品类 -->
          <div v-if="!isEditing" class="info-item">
            <span class="info-label">经营品类：</span>
            <span class="info-value">{{ shoplist.businessScope || '未填写经营品类' }}</span>
          </div>
          <div v-else class="info-item">
            <span class="info-label">经营品类：</span>
            <el-input 
              v-model="editForm.businessScope" 
              placeholder="请输入经营品类，如：中餐、火锅"
              style="width: 300px;"
            />
          </div>

          <!-- 停车信息 -->
          <div v-if="!isEditing" class="info-item">
            <span class="info-label">停车信息：</span>
            <span class="info-value">{{ shoplist.parkingInfo || '无停车信息' }}</span>
          </div>
          <div v-else class="info-item">
            <span class="info-label">停车信息：</span>
            <el-input 
              v-model="editForm.parkingInfo" 
              placeholder="请输入停车信息，如：免费停车2小时"
              style="width: 300px;"
            />
          </div>

          <!-- 店铺介绍 -->
          <div v-if="!isEditing" class="info-item intro-item">
            <span class="info-label">店铺介绍：</span>
            <span class="info-value intro-value">{{ shoplist.introduction || '暂无店铺介绍' }}</span>
          </div>
          <div v-else class="info-item intro-item">
            <span class="info-label">店铺介绍：</span>
            <el-input 
              v-model="editForm.introduction" 
              type="textarea" 
              placeholder="请输入店铺介绍"
              rows="4"
              style="width: 300px;"
            />
          </div>
        </div>
      </div>

      <!-- 编辑模式下的操作按钮 -->
      <div v-if="isEditing" class="card-footer">
        <el-button @click="cancelEdit">取消</el-button>
        <el-button type="primary" @click="saveShopInfo">保存</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { ElSkeleton, ElTag, ElIcon, ElMessage, ElButton, ElInput, ElUpload } from 'element-plus';
import { Shop, UploadFilled } from '@element-plus/icons-vue';
import ShopApi from '../../../../api/ShopApi';

// 核心修复：shoplist 初始化为对象（店铺信息是单条数据，非数组），避免数组取属性报错
const shoplist = ref({}); 
const sta = ref('');
const loading = ref(false);
const logoLoadError = ref(false); // 记录logo加载失败状态
const isEditing = ref(false); // 编辑模式标志
const editForm = ref({}); // 编辑表单数据

// 获取店铺信息（强化空值校验）
const getShopInfo = async () => {
  try {
    loading.value = true;
    const shopId = localStorage.getItem("shopid");
    
    // 1. 校验shopId有效性
    if (!shopId || shopId === 'null' || shopId === 'undefined') {
      ElMessage.warning('未获取到有效店铺ID，请重新登录');
      return;
    }

    // 2. 调用接口并严格校验返回值
    const res = await ShopApi.getShopById(shopId);
    if (!res || !res.data || !res.data.entity) {
      ElMessage.info('未查询到店铺信息');
      shoplist.value = {}; // 重置为空对象，避免后续取属性报错
      return;
    }

    // 3. 赋值并确保为对象类型
    shoplist.value = typeof res.data.entity === 'object' ? res.data.entity : {};
    
    // 4. 状态判断（强化空值防护）
    const status = Number(shoplist.value.status); // 转为数字，避免字符串判断错误
    if (status === 0) {
      sta.value = '休息';
    } else if (status === 1) {
      sta.value = '营业';
    } else if (status === 2) {
      sta.value = '暂停营业';
    } else {
      sta.value = '未知状态'; // 非预期值兜底
    }

  } catch (err) {
    console.error("获取店铺信息失败：", err);
    ElMessage.error('获取店铺信息失败，请稍后重试');
    shoplist.value = {}; // 异常时重置为空对象
  } finally {
    loading.value = false;
  }
};

// 进入编辑模式
const enterEditMode = () => {
  // 复制当前店铺信息到编辑表单
  editForm.value = JSON.parse(JSON.stringify(shoplist.value));
  // 确保status是字符串类型，与下拉选择框的value类型匹配
  if (editForm.value.status !== undefined && editForm.value.status !== null) {
    editForm.value.status = editForm.value.status.toString();
  }
  isEditing.value = true;
};

// 取消编辑
const cancelEdit = () => {
  isEditing.value = false;
  editForm.value = {};
};

// 保存店铺信息
const saveShopInfo = async () => {
  try {
    loading.value = true;
    const shopId = localStorage.getItem("shopid");
    
    // 1. 校验shopId有效性
    if (!shopId || shopId === 'null' || shopId === 'undefined') {
      ElMessage.warning('未获取到有效店铺ID，请重新登录');
      return;
    }

    // 2. 准备提交数据，将status转换为数字类型
    const submitData = { ...editForm.value };
    if (submitData.status !== undefined && submitData.status !== null) {
      submitData.status = Number(submitData.status);
    }

    // 3. 调用接口更新店铺信息
    const res = await ShopApi.updateShop(submitData);
    if (res.code === 200) {
      ElMessage.success('店铺信息更新成功');
      isEditing.value = false;
      // 重新获取店铺信息
      await getShopInfo();
    } else {
      ElMessage.error(res.message || '更新店铺信息失败');
    }
  } catch (err) {
    console.error("更新店铺信息失败：", err);
    ElMessage.error('更新店铺信息失败，请稍后重试');
  } finally {
    loading.value = false;
  }
};

// 图片路径处理（极致空值防护）
function getImg(src) {
  // 校验src的有效性：非空、字符串类型、非null/undefined
  if (!src || typeof src !== "string" || src === 'null' || src === 'undefined') {
    return "";
  }
  // 替换路径并处理Vite静态资源
  try {
    const newSrc = src.replace("@", "/src");
    return new URL(newSrc, import.meta.url).href;
  } catch (e) {
    console.error('图片路径处理失败：', e);
    return "";
  }
}

// logo加载失败处理
const handleLogoError = () => {
  logoLoadError.value = true;
};

// logo上传处理
const handleLogoChange = (file) => {
  // 这里可以处理logo文件，例如压缩、预览等
  // 目前只是简单地将文件名赋值给表单
  editForm.value.logo = file.name;
};

// 计算状态标签类型（适配Element Plus标签样式）
const getStatusTagType = () => {
  if (sta.value === '营业') return 'success';
  if (sta.value === '暂停营业') return 'warning';
  if (sta.value === '休息') return 'info';
  return 'default';
};

// 组件挂载后请求数据
onMounted(() => {
  getShopInfo();
});
</script>

<style scoped lang="scss">
// 全局样式重置
.shop-info-wrapper {
  max-width: 1200px;
  margin: 20px auto;
  padding: 0 20px;
  font-family: "Microsoft YaHei", sans-serif;
}

// 加载状态样式
.loading-container {
  padding: 20px;
  .skeleton-card {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
  }
}

// 卡片主体样式
.shop-info-card {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 16px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

// 卡片头部
.card-header {
  padding: 24px;
  border-bottom: 1px solid #f5f7fa;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;

  .shop-name {
    font-size: 22px;
    font-weight: 600;
    color: #1f2937;
    margin: 0;
  }

  // 头部操作区域
  .header-actions {
    display: flex;
    align-items: center;
    gap: 12px;
  }
}

// 卡片主体内容
.card-body {
  padding: 24px;
  display: flex;
  gap: 30px;
  flex-wrap: wrap; // 小屏自动换行

  // 响应式适配
  @media (max-width: 768px) {
    flex-direction: column;
    align-items: center;
  }
}

// LOGO容器
.logo-container {
  width: 280px;
  height: 200px;
  border-radius: 8px;
  overflow: hidden;
  background: #f8fafc;
  display: flex;
  justify-content: center;
  align-items: center;

  .shop-logo {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.05);
    }
  }

  // logo上传区域样式
  .logo-uploader {
    width: 100%;
    height: 100%;
    border: 1px dashed #d0d5dd;
    border-radius: 8px;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #f8fafc;
    transition: all 0.3s ease;

    &:hover {
      border-color: #6366f1;
      background: #f0f4ff;
    }
  }

  .default-logo {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    color: #94a3b8;

    p {
      margin-top: 12px;
      font-size: 14px;
    }
  }
}

// 信息列表
.info-list {
  flex: 1;
  min-width: 300px;

  .info-item {
    display: flex;
    margin-bottom: 16px;
    line-height: 24px;
    font-size: 15px;
    align-items: flex-start;

    &:last-child {
      margin-bottom: 0;
    }

    // 标签样式
    .info-label {
      width: 100px;
      color: #64748b;
      font-weight: 500;
      flex-shrink: 0; // 固定宽度，不被挤压
      padding-top: 6px;
    }

    // 值样式
    .info-value {
      color: #1e293b;
      flex: 1;
    }

    // 编辑模式下的输入框样式
    :deep(.el-input),
    :deep(.el-textarea) {
      margin-top: 0;
    }
  }

  // 介绍项特殊样式（支持换行）
  .intro-item {
    .intro-value {
      white-space: pre-wrap;
      word-break: break-all;
      line-height: 1.6;
    }
  }
}

// 卡片底部操作按钮
.card-footer {
  padding: 16px 24px;
  border-top: 1px solid #f5f7fa;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  background: #fafbfc;
}

// 适配小屏幕
@media (max-width: 768px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    justify-content: space-between;
  }

  .logo-container {
    width: 100%;
    max-width: 300px;
  }

  .info-list {
    width: 100%;
    min-width: auto;
  }
}
</style>