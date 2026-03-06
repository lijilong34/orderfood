<template>
  <!-- 左侧侧边栏 + 右侧内容区 布局 -->
  <div class="layout-container">
    <!-- 左侧侧边栏 -->
    <div class="sidebar">
      <!-- 用户信息 -->
      <div class="sidebar-user">
        <div class="user-top">
          <span class="user-name">用户名</span>
          <span class="user-nickname">{{ userInfo.nickname || userInfo.phone || '未命名' }}</span>
        </div>
        <div class="growth-value" @click="switchContent('404')">成长值 {{ userGrowthValue }}/5000</div>
      </div>

      <!-- 快捷功能（侧边栏） -->
      <div class="sidebar-func">
        <div class="func-item" @click="switchContent('personalInfo')">
          <img src="https://img.icons8.com/?size=100&id=HEBTcR9O3uzR&format=png&color=000000" alt="消费赢免单">
          <p>个人信息</p>
        </div>
        <div class="func-item" @click="switchContent('coupon')">
          <img src="https://img.icons8.com/fluency/48/000000/ticket.png" alt="每日领券">
          <p>每日领券</p>
        </div>
      </div>

      <!-- 功能入口（侧边栏） -->
      <div class="sidebar-entry">
        <div class="entry-item" @click="switchContent('favorites')">
          <img src="https://img.icons8.com/fluency/32/000000/bookmark.png" alt="收藏">
          <p>收藏</p>
        </div>
        <div class="entry-item" @click="switchContent('browseHistory')">
          <img src="https://img.icons8.com/fluency/32/000000/clock.png" alt="浏览记录">
          <p>浏览记录</p>
        </div>
      </div>

      <!-- 订单入口（侧边栏） -->
      <div class="sidebar-order">
        <h4>订单</h4>
        <div class="order-item" @click="switchContent('allOrders', 'all')">
          <img src="https://img.icons8.com/fluency/32/000000/order.png" alt="全部订单">
          <p>全部订单</p>
        </div>
        <div class="order-item" @click="switchContent('allOrders', 'pendingReceive')">
          <img src="https://img.icons8.com/fluency/32/000000/delivery.png" alt="待收货">
          <p>待收货/使用</p>
        </div>
        <div class="order-item" @click="switchContent('allOrders', 'pendingReview')">
          <img src="https://img.icons8.com/fluency/32/000000/comments.png" alt="待评价">
          <p>已完成</p>
        </div>
      </div>
    </div>

    <!-- 右侧内容区（动态切换） -->
    <div class="content-area">
      <!-- 收藏子路由 -->
      <router-view v-if="$route.path === '/cz/favorites' || $route.path === '/cz/coupon' || currentContent === 'favorites' || currentContent === 'coupon'"></router-view>
      
      <!-- 其他内容 -->
      <template v-else>
      <!-- 新增：个人信息主页面（默认显示） -->
      <div v-if="currentContent === 'personalInfo'" class="personal-info-section">
        <!-- 个人信息子选项卡 -->
        <div class="info-tabs">
          <div class="tab-item" :class="{ active: infoTab === 'myInfo' }" @click="infoTab = 'myInfo'">
            我的信息
          </div>
          <div class="tab-item" :class="{ active: infoTab === 'myWallet' }" @click="infoTab = 'myWallet'">
            我的钱包
          </div>
          <div class="tab-item" :class="{ active: infoTab === 'setting' }" @click="infoTab = 'setting'">
            设置
          </div>
        </div>

        <!-- 子选项1：我的信息 -->
        <div v-if="infoTab === 'myInfo'" class="my-info-content">
          <h4>我的基本信息</h4>
          <div class="avatar-section">
            <div class="avatar-wrapper">
              <el-image
                :src="'http://localhost:8080/imeageserver/'+userInfo.avatar"
                :preview-src-list="userInfo.avatar ? ['http://localhost:8080/imeageserver/'+userInfo.avatar] : []"
                alt="头像"
                class="avatar-img"
                fit="cover"
                hide-on-click-modal
              />
              <button class="change-avatar-btn" @click="openAvatarModal">更换头像</button>
            </div>
          </div>
          <div class="info-item">
            <label>昵称：</label>
            <span>{{ userInfo.nickname || '未设置' }}</span>
            <!-- 新增：修改昵称按钮 -->
            <button class="edit-nickname-btn" @click="openNicknameModal">修改</button>
          </div>
          <div class="info-item">
            <label>手机号：</label>
            <span>{{ userInfo.phone || '未绑定' }}</span>
          </div>
          <div class="info-item">
            <label>成长值：</label>
            <span>{{ userGrowthValue }}/5000</span>
          </div>
        </div>

        <!-- 子选项2：我的钱包（复用原有钱包逻辑） -->
        <div v-if="infoTab === 'myWallet'" class="wallet-section">
          <h4>我的钱包</h4>
          <div class="wallet-info">
            <div class="wallet-balance">
              <span class="balance-label">余额</span>
              <span class="balance-amount">¥{{ walletBalance }}</span>
            </div>
            <div class="wallet-quick">
              <el-button type="danger" size="large" class="quick-item" @click="openRechargeModal">
                <p>立即充值</p>
              </el-button>
            </div>
          </div>

          <!-- 充值金额选择（原充值区域，仅用于选择金额） -->
          <div class="recharge-amount-select" v-if="showRechargeAmountSelect">
            <h5>选择充值金额</h5>
            <div class="recharge-amounts">
              <div class="amount-item" :class="{ active: selectedAmount === 100 }" @click="selectedAmount = 100">
                ¥100
              </div>
              <div class="amount-item" :class="{ active: selectedAmount === 200 }" @click="selectedAmount = 200">
                ¥200
              </div>
              <div class="amount-item" :class="{ active: selectedAmount === 500 }" @click="selectedAmount = 500">
                ¥500
              </div>
              <div class="amount-item" :class="{ active: selectedAmount === 1000 }" @click="selectedAmount = 1000">
                ¥1000
              </div>
            </div>
            <div class="custom-amount">
              <input type="number" v-model.number="customAmount" placeholder="自定义金额" min="10" max="10000">
            </div>
            <div class="recharge-btn" @click="confirmRechargeAmount">
              确认金额
            </div>
          </div>
        </div>

        <!-- 子选项3：设置 -->
        <div v-if="infoTab === 'setting'" class="setting-content">
          <h4>设置</h4>
          <div class="setting-item" @click="openMessageModal">
            <img src="https://img.icons8.com/fluency/24/000000/notification.png" alt="消息通知">
            <span>消息通知</span>
            <img src="https://img.icons8.com/fluency/16/000000/chevron-right.png" alt="箭头">
          </div>
          <div class="setting-item" @click="openPrivacyModal">
            <img src="https://img.icons8.com/fluency/24/000000/privacy.png" alt="隐私设置">
            <span>隐私设置</span>
            <img src="https://img.icons8.com/fluency/16/000000/chevron-right.png" alt="箭头">
          </div>
          <div class="setting-item" @click="openAddressModal">
            <img src="https://img.icons8.com/fluency/32/000000/delivery.png" alt="隐私设置">
            <span>收货地址设置</span>
            <img src="https://img.icons8.com/fluency/16/000000/chevron-right.png" alt="箭头">
          </div>
          <div class="setting-item" @click="openFeedbackModal">
            <img src="https://img.icons8.com/fluency/32/000000/feedback.png" alt="我要反馈">
            <span>我要反馈</span>
            <img src="https://img.icons8.com/fluency/16/000000/chevron-right.png" alt="箭头">
          </div>
        </div>
      </div>

      <!-- 原有钱包模块（保留，兼容旧逻辑） -->
      <div v-if="currentContent === 'wallet'" class="wallet-section">
        <h4>我的钱包</h4>
        <div class="wallet-info">
          <div class="wallet-balance">
            <span class="balance-label">余额</span>
            <span class="balance-amount">¥{{ walletBalance }}</span>
          </div>
          <div class="wallet-quick">
            <div class="quick-item" @click="openRechargeModal">
              <img src="https://img.icons8.com/fluency/32/000000/credit-card.png" alt="充值">
              <p>充值</p>
            </div>
          </div>
        </div>

        <!-- 充值金额选择（原充值区域） -->
        <div class="recharge-amount-select" v-if="showRechargeAmountSelect">
          <h5>选择充值金额</h5>
          <div class="recharge-amounts">
            <div class="amount-item" :class="{ active: selectedAmount === 100 }" @click="selectedAmount = 100">
              ¥100
            </div>
            <div class="amount-item" :class="{ active: selectedAmount === 200 }" @click="selectedAmount = 200">
              ¥200
            </div>
            <div class="amount-item" :class="{ active: selectedAmount === 500 }" @click="selectedAmount = 500">
              ¥500
            </div>
            <div class="amount-item" :class="{ active: selectedAmount === 1000 }" @click="selectedAmount = 1000">
              ¥1000
            </div>
          </div>
          <div class="custom-amount">
            <input type="number" v-model.number="customAmount" placeholder="自定义金额" min="10" max="10000">
          </div>
          <div class="recharge-btn" @click="confirmRechargeAmount">
            确认金额
          </div>
        </div>
      </div>

      <!-- 全部订单 -->
      <AllOrders v-if="currentContent === 'allOrders'" ref="allOrdersRef" />
      

      
      <!-- 浏览记录 -->
      <BrowseHistory v-if="currentContent === 'browseHistory'" />
      
      <!-- 404占位 -->
      <div v-if="currentContent === '404'" class="not-found">
        <img src="https://img.icons8.com/fluency/96/000000/error.png" alt="404" class="not-found-icon" />
        <p class="not-found-text">功能正在开发中...</p>
      </div>
      </template>
    </div>

    <!-- 昵称修改弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isNicknameModalOpen" @click="closeNicknameModal">
        <div class="modal-content" @click.stop>
          <div class="modal-header">
            <h3>修改昵称</h3>
            <button class="close-btn" @click="closeNicknameModal">&times;</button>
          </div>
          <div class="modal-body">
            <input 
              type="text" 
              v-model="newNickname" 
              placeholder="请输入新昵称" 
              maxlength="12"
              class="nickname-input"
            >
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="closeNicknameModal">取消</button>
            <button class="confirm-btn" @click="saveNickname" :disabled="!newNickname.trim()">确定</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 隐私设置弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isPrivacyModalOpen" @click="closePrivacyModal">
        <div class="modal-content privacy-modal" @click.stop>
          <div class="modal-header">
            <h3>隐私设置</h3>
            <button class="close-btn" @click="closePrivacyModal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="privacy-tabs">
              <div class="privacy-tab" :class="{ active: privacyTab === 'phone' }" @click="privacyTab = 'phone'">
                修改手机号
              </div>
              <div class="privacy-tab" :class="{ active: privacyTab === 'password' }" @click="privacyTab = 'password'">
                修改密码
              </div>
            </div>
            
            <!-- 修改手机号表单 -->
            <div v-if="privacyTab === 'phone'" class="privacy-form">
              <div class="form-item">
                <label>当前手机号：</label>
                <span>{{ userInfo.phone || '未绑定' }}</span>
              </div>
              <div class="form-item">
                <label>验证码：</label>
                <div class="code-input-group">
                  <input type="text" v-model="phoneCode" placeholder="请输入验证码" maxlength="6">
                  <button class="code-btn" @click="sendPhoneCode" :disabled="phoneCodeTimer > 0">
                    {{ phoneCodeTimer > 0 ? `${phoneCodeTimer}秒后重发` : '获取验证码' }}
                  </button>
                </div>
              </div>
              <div class="form-item">
                <label>新手机号：</label>
                <input type="tel" v-model="newPhone" placeholder="请输入新手机号" maxlength="11">
              </div>
            </div>
            
            <!-- 修改密码表单 -->
            <div v-if="privacyTab === 'password'" class="privacy-form">
              <div class="form-item">
                <label>当前密码：</label>
                <input type="password" v-model="currentPassword" placeholder="请输入当前密码">
              </div>
              <div class="form-item">
                <label>新密码：</label>
                <input type="password" v-model="newPassword" placeholder="请输入新密码" maxlength="20">
              </div>
              <div class="form-item">
                <label>确认密码：</label>
                <input type="password" v-model="confirmPassword" placeholder="请再次输入新密码" maxlength="20">
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="closePrivacyModal">取消</button>
            <button class="confirm-btn" @click="savePrivacySettings" :disabled="!canSavePrivacy">确定</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 消息通知弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isMessageModalOpen" @click="closeMessageModal">
        <div class="modal-content message-modal" @click.stop>
          <div class="modal-header">
            <h3>消息通知</h3>
            <button class="close-btn" @click="closeMessageModal">&times;</button>
          </div>
          <div class="modal-body">
            <!-- 消息列表 -->
            <div class="message-list" v-if="messageList.length > 0">
              <div 
                v-for="message in messageList" 
                :key="message.id" 
                class="message-item"
              >
                <div class="message-header">
                  <div class="message-type">{{ getMessageTypeText(message.type) }}</div>
                  <div class="message-time">{{ formatTime(message.createTime) }}</div>
                </div>
                <div class="message-title">
                  {{ message.title }}
                </div>
                <div class="message-content">{{ message.content }}</div>
                <div class="message-footer">
                  <button class="message-btn delete-btn" @click="deleteMessage(message.id)">
                    删除
                  </button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div class="empty-state" v-else>
              <img src="https://img.icons8.com/fluency/96/000000/inbox.png" alt="空消息" class="empty-icon" />
              <p class="empty-text">暂无消息</p>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 收货地址弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isAddressModalOpen" @click="closeAddressModal">
        <div class="modal-content address-modal" @click.stop>
          <div class="modal-header">
            <h3>收货地址管理</h3>
            <button class="close-btn" @click="closeAddressModal">&times;</button>
          </div>
          <div class="modal-body">
            <!-- 地址列表 -->
            <div class="address-list" v-if="addressList.length > 0">
              <div 
                v-for="address in addressList" 
                :key="address.id" 
                class="address-item"
                :class="{ default: address.isDefault }"
              >
                <div class="address-info">
                  <div class="address-header">
                    <span class="receiver-name">{{ address.receiver }}</span>
                    <span class="receiver-phone">{{ address.phone }}</span>
                    <span v-if="address.isDefault" class="default-tag">默认</span>
                  </div>
                  <div class="address-detail">
                    {{ address.city }} {{ address.district }} {{ address.detailAddress }}
                  </div>
                </div>
                <div class="address-actions">
                  <button class="address-btn edit-btn" @click="editAddress(address)">
                    编辑
                  </button>
                  <button 
                    v-if="!address.isDefault" 
                    class="address-btn set-default-btn" 
                    @click="setDefaultAddress(address.id)"
                  >
                    设为默认
                  </button>
                  <button class="address-btn delete-btn" @click="deleteAddress(address.id)">
                    删除
                  </button>
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div class="empty-state" v-else>
              <img src="https://img.icons8.com/fluency/96/000000/marker.png" alt="空地址" class="empty-icon" />
              <p class="empty-text">暂无收货地址</p>
            </div>

            <!-- 添加地址按钮 -->
            <button class="add-address-btn" @click="openAddressForm">
              + 添加新地址
            </button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 地址表单弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isEditingAddress" @click="closeAddressForm">
        <div class="modal-content address-form-modal" @click.stop>
          <div class="modal-header">
            <h3>{{ editingAddressId ? '编辑地址' : '添加地址' }}</h3>
            <button class="close-btn" @click="closeAddressForm">&times;</button>
          </div>
          <div class="modal-body">
            <div class="form-group">
              <label>收货人姓名 <span class="required">*</span></label>
              <input type="text" v-model="addressForm.receiverName" placeholder="请输入收货人姓名" maxlength="20">
            </div>
            <div class="form-group">
              <label>手机号码 <span class="required">*</span></label>
              <input type="tel" v-model="addressForm.phone" placeholder="请输入手机号码" maxlength="11">
            </div>
            <div class="form-group">
              <label>所在地区 <span class="required">*</span></label>
              <div class="region-select">
                <input type="text" v-model="addressForm.city" placeholder="市" maxlength="20">
                <input type="text" v-model="addressForm.district" placeholder="区/县" maxlength="20">
              </div>
            </div>
            <div class="form-group">
              <label>详细地址 <span class="required">*</span></label>
              <textarea v-model="addressForm.detailAddress" placeholder="请输入详细地址" rows="3" maxlength="100"></textarea>
            </div>
            <div class="form-group">
              <label class="checkbox-label">
                <input type="checkbox" v-model="addressForm.isDefault">
                <span>设为默认地址</span>
              </label>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="closeAddressForm">取消</button>
            <button class="confirm-btn" @click="saveAddress">保存</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 头像上传弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isAvatarModalOpen" @click="closeAvatarModal">
        <div class="modal-content avatar-modal" @click.stop>
          <div class="modal-header">
            <h3>更换头像</h3>
            <button class="close-btn" @click="closeAvatarModal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="avatar-upload-area">
              <el-upload
                class="avatar-uploader"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
                :action="uploadAction"
                :headers="uploadHeaders"
                name="file"
                accept="image/*"
              >
                <img v-if="avatarPreview" :src="avatarPreview" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
              <button class="upload-trigger-btn" @click="triggerUpload">更换头像</button>
              <p class="upload-tip">支持 JPG、PNG 格式，建议尺寸 200x200，大小不超过 2MB</p>
               <p class="upload-tip">上传成功后，将在圆形框显示，刷新页面生效。</p>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="closeAvatarModal">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 我要反馈弹窗 - 历史反馈列表 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isFeedbackModalOpen" @click="closeFeedbackModal">
        <div class="modal-content feedback-list-modal" @click.stop>
          <div class="modal-header">
            <h3>历史反馈</h3>
            <button class="close-btn" @click="closeFeedbackModal">&times;</button>
          </div>
          <div class="modal-body">
            <!-- 添加反馈按钮 -->
            <button class="add-feedback-btn" @click="openAddFeedbackModal">
              <el-icon><Plus /></el-icon>
              添加反馈
            </button>

            <!-- 反馈列表 -->
            <div class="feedback-list" v-if="feedbackHistoryList.length > 0">
              <div 
                v-for="feedback in feedbackHistoryList" 
                :key="feedback.id" 
                class="feedback-history-item"
              >
                <div class="feedback-header">
                  <div class="feedback-type-badge" :class="'type-' + feedback.type">
                    {{ getFeedbackTypeText(feedback.type) }}
                  </div>
                  <div class="feedback-priority" :class="'priority-' + feedback.priority">
                    {{ getPriorityText(feedback.priority) }}
                  </div>
                  <div class="feedback-time">{{ formatTime(feedback.createTime) }}</div>
                </div>
                <div class="feedback-body">
                  <p class="feedback-content-text">{{ feedback.content }}</p>
                </div>
                <div class="feedback-status" :class="'status-' + feedback.status">
                  {{ getStatusText(feedback.status) }}
                </div>
              </div>
            </div>

            <!-- 空状态 -->
            <div class="empty-state" v-else>
              <img src="https://img.icons8.com/fluency/96/000000/speech-bubble.png" alt="空反馈" class="empty-icon" />
              <p class="empty-text">暂无反馈记录</p>
            </div>
          </div>
        </div>
      </div>
    </Teleport>

    <!-- 添加反馈弹窗 -->
    <Teleport to="body">
      <div class="modal-overlay" v-if="isAddFeedbackModalOpen" @click="closeAddFeedbackModal">
        <div class="modal-content feedback-modal" @click.stop>
          <div class="modal-header">
            <h3>我要反馈</h3>
            <button class="close-btn" @click="closeAddFeedbackModal">&times;</button>
          </div>
          <div class="modal-body">
            <div class="feedback-form">
              <div class="form-group">
                <label>反馈类型 <span class="required">*</span></label>
                <select v-model="feedbackForm.type" class="form-select">
                  <option value="">请选择反馈类型</option>
                  <option value="1">功能建议</option>
                  <option value="2">问题反馈</option>
                  <option value="3">投诉</option>
                  <option value="4">其他</option>
                </select>
              </div>
              <div class="form-group">
                <label>反馈内容 <span class="required">*</span></label>
                <textarea 
                  v-model="feedbackForm.content" 
                  placeholder="请详细描述您的问题或建议..." 
                  rows="5" 
                  maxlength="500"
                  class="form-textarea"
                ></textarea>
                <div class="char-count">{{ feedbackForm.content.length }}/500</div>
              </div>
              <div class="form-group">
                <label>优先级</label>
                <div class="priority-options">
                  <label class="priority-option">
                    <input type="radio" v-model="feedbackForm.priority" :value="1">
                    <span class="priority-label low">低</span>
                  </label>
                  <label class="priority-option">
                    <input type="radio" v-model="feedbackForm.priority" :value="2">
                    <span class="priority-label medium">中</span>
                  </label>
                  <label class="priority-option">
                    <input type="radio" v-model="feedbackForm.priority" :value="3">
                    <span class="priority-label high">高</span>
                  </label>
                </div>
              </div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="closeAddFeedbackModal">取消</button>
            <button class="confirm-btn" @click="submitFeedback" :disabled="!canSubmitFeedback">提交反馈</button>
          </div>
        </div>
      </div>
    </Teleport>


  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import {ElMessage, ElUpload, ElIcon} from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
// 导入子组件
import AllOrders from './AllOrders.vue';

import BrowseHistory from './BrowseHistory.vue';
// 导入API
import UserApi from '../../../../api/UserApi.js';
import registerApi from '../../../../api/registerApi.js';
import MessageApi from '../../../../api/MessageApi.js';
import AddressApi from '../../../../api/AddressApi.js';
import FeedbackApi from '../../../../api/FeedbackApi.js';

// 响应式数据
const userInfo = ref({});
const userGrowthValue = ref(0); // 用户成长值
const walletBalance = ref(0);
const selectedAmount = ref(100);
const customAmount = ref(null);
const currentContent = ref('personalInfo'); 
const allOrdersRef = ref(null); // 订单组件引用
const infoTab = ref('myInfo'); 

// 创建router和route实例
const router = useRouter();
const route = useRoute(); 

// 昵称修改弹窗相关
const isNicknameModalOpen = ref(false);
const newNickname = ref('');

// 头像上传相关
const isAvatarModalOpen = ref(false);
const avatarFile = ref(null);
const avatarPreview = ref('');
const serverUrl = ref('http://localhost:8080/'); // 服务器地址

// 隐私设置弹窗相关
const isPrivacyModalOpen = ref(false);
const privacyTab = ref('phone');
const newPhone = ref('');
const phoneCode = ref('');
const phoneCodeTimer = ref(0);
const currentPassword = ref('');
const newPassword = ref('');
const confirmPassword = ref('');
let code=ref(0);

// 充值相关
const showRechargeAmountSelect = ref(false); // 是否显示金额选择区域

// 消息通知相关
const isMessageModalOpen = ref(false); // 消息通知弹窗开关
const messageList = ref([]); // 消息列表

// 收货地址相关
const isAddressModalOpen = ref(false); // 收货地址弹窗开关
const addressList = ref([]); // 地址列表
const isEditingAddress = ref(false); // 是否正在编辑地址
const editingAddressId = ref(null); // 正在编辑的地址ID
const addressForm = ref({
  receiverName: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detailAddress: '',
  isDefault: false
});

// 反馈相关
const isFeedbackModalOpen = ref(false); // 反馈列表弹窗开关
const isAddFeedbackModalOpen = ref(false); // 添加反馈弹窗开关
const feedbackHistoryList = ref([]); // 历史反馈列表
const feedbackForm = ref({
  type: '',
  content: '',
  priority: 2 // 默认为中
});

// 初始化
onMounted(async () => {
  getUserInfo();
  getMessageList();
});

// 图片路径处理
const getImg = (src) => {
  if (!src) return 'https://img.icons8.com/fluency/96/000000/user.png';
  return new URL(src.replace('@', '/src'), import.meta.url).href;
};

// 获取用户详细信息
const getUserInfo = async () => {
  try {
    console.log('通过token获取用户信息');
    
    const res = await UserApi.getCurrentUser();
    console.log('API响应:', res);
    
    if (res && res.data) {
      // 更新用户信息，包括成长值
      userInfo.value = res.data.entity;
      userGrowthValue.value = res.data.entity.growthValue || 0;
      getWalletBalance();
    } else {
      console.warn('API响应格式不正确:', res);
    }
  } catch (error) {
    console.error('获取用户信息失败：', error);
  }
};

// 切换右侧内容
const switchContent = (content, tab = 'pendingPay') => {
  if (content === 'favorites') {
    // 点击收藏时，跳转到收藏路由
    router.push('/cz/favorites');
  } else if (content === 'coupon') {
    // 点击每日领券时，跳转到优惠券路由
    router.push('/cz/coupon');
  } else if (route.path === '/cz/favorites' || route.path === '/cz/coupon') {
    // 如果当前在收藏或优惠券路由下，点击其他菜单时先跳转到主路由
    router.push('/cz');
    // 延迟设置内容，确保路由跳转后再设置
    setTimeout(() => {
      currentContent.value = content;
    }, 100);
  } else {
    // 正常切换内容
    currentContent.value = content;
  }
  
  // 如果切换到订单，同步选中标签并重新加载数据
  if (content === 'allOrders' && allOrdersRef.value) {
    allOrdersRef.value.activeTab = tab;
    allOrdersRef.value.selectorder(); // 重新加载对应类型的订单数据
  }
  // 充值金额选择区域显示控制
  if (content === 'recharge') {
    currentContent.value = 'wallet'; 
    showRechargeAmountSelect.value = true;
  }
};

// 获取钱包余额
const getWalletBalance = () => {
  // 使用用户的真实余额，如果没有则显示默认值
  if (userInfo.value && userInfo.value.balance) {
    walletBalance.value = userInfo.value.balance;
  } else {
    walletBalance.value = 0.00;
  }
  console.log('钱包余额:', walletBalance.value);
};

// 昵称修改弹窗方法
const openNicknameModal = () => {
  newNickname.value = userInfo.value.nickname || '';
  isNicknameModalOpen.value = true;
};
const closeNicknameModal = () => {
  isNicknameModalOpen.value = false;
  newNickname.value = '';
};
const saveNickname = async () => {
  try {
    // 调用用户信息更新接口（假设UserApi有updateNickname方法）
    const res = await UserApi.updateNickname({ id:userInfo.value.id,nickname: newNickname.value.trim() });
    if (res.code=200) {
      // 更新本地用户信息
      userInfo.value.nickname = newNickname.value.trim();
      closeNicknameModal();
      alert('昵称修改成功');
    } else {
      alert('昵称修改失败，请稍后再试');
    }
  } catch (error) {
    console.error('修改昵称失败：', error);
    alert('网络异常，修改失败');
  }
};

// 头像上传相关方法
const openAvatarModal = () => {
  isAvatarModalOpen.value = true;
  avatarPreview.value = '';
};

const closeAvatarModal = () => {
  isAvatarModalOpen.value = false;
  avatarPreview.value = '';
};

const uploadAction = computed(() => {
  return 'http://localhost:8080/user/uploadAvatar';
});

const uploadHeaders = computed(() => {
  const token = localStorage.getItem('token');
  return {
    'Authorization': `Bearer ${token}`
  };
});

const beforeAvatarUpload = (file) => {
  const isImage = file.type.startsWith('image/');
  const isLt2M = file.size / 1024 / 1024 < 2;

  if (!isImage) {
    ElMessage.error('只能上传图片文件!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!');
    return false;
  }
  return true;
};

const handleAvatarSuccess = (response) => {
  if (response.code === 200) {
    console.log('头像信息',response)
    // 更新本地用户信息
    userInfo.value.avatar ='http://localhost:8080/imeageserver/'+ response.data.avatar;
    // 更新localStorage
    localStorage.setItem('avatar',response.data.avatar);
    // 更新预览
    avatarPreview.value ='http://localhost:8080/imeageserver/'+response.data.avatar;
    ElMessage.success('头像更新成功');
    // closeAvatarModal();
  } else {
    ElMessage.error(response.message || '头像上传失败');
  }
};

const triggerUpload = () => {
  document.querySelector('.avatar-uploader .el-upload').click();
};

// 反馈相关方法
// 打开反馈列表弹窗
const openFeedbackModal = async () => {
  isFeedbackModalOpen.value = true;
  // 加载历史反馈列表
  await loadFeedbackHistory();
};

// 关闭反馈列表弹窗
const closeFeedbackModal = () => {
  isFeedbackModalOpen.value = false;
};

// 打开添加反馈弹窗
const openAddFeedbackModal = () => {
  isAddFeedbackModalOpen.value = true;
  // 重置表单数据
  feedbackForm.value = {
    type: '',
    content: '',
    priority: 2
  };
};

// 关闭添加反馈弹窗
const closeAddFeedbackModal = () => {
  isAddFeedbackModalOpen.value = false;
  feedbackForm.value = {
    type: '',
    content: '',
    priority: 2
  };
};

// 加载历史反馈列表
const loadFeedbackHistory = async () => {
  try {
    const res = await FeedbackApi.getFeedbackList({});
    if (res.code === 200 && res.data) {
      feedbackHistoryList.value = res.data.list || res.data || [];
    } else {
      feedbackHistoryList.value = [];
    }
  } catch (error) {
    console.error('加载反馈列表失败:', error);
    feedbackHistoryList.value = [];
  }
};

// 获取反馈类型文本
const getFeedbackTypeText = (type) => {
  const typeMap = {
    1: '功能建议',
    2: '问题反馈',
    3: '投诉',
    4: '其他'
  };
  return typeMap[type] || '其他';
};

// 获取优先级文本
const getPriorityText = (priority) => {
  const priorityMap = {
    1: '低',
    2: '中',
    3: '高'
  };
  return priorityMap[priority] || '中';
};

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '待处理',
    1: '处理中',
    2: '已完成',
    3: '已关闭'
  };
  return statusMap[status] || '待处理';
};

// 提交反馈
const submitFeedback = async () => {
  try {
    // 验证表单数据
    if (!feedbackForm.value.type) {
      ElMessage.error('请选择反馈类型');
      return;
    }
    if (!feedbackForm.value.content || feedbackForm.value.content.trim().length === 0) {
      ElMessage.error('请输入反馈内容');
      return;
    }
    if (feedbackForm.value.content.trim().length < 5) {
      ElMessage.error('反馈内容至少需要5个字符');
      return;
    }

    // 调用提交反馈的API
    const res = await FeedbackApi.submitFeedback({
      userId: userInfo.value.id,
      type: parseInt(feedbackForm.value.type),
      content: feedbackForm.value.content.trim(),
      priority: parseInt(feedbackForm.value.priority)
    });

    if (res.code === 200) {
      ElMessage.success('反馈提交成功，我们会尽快处理');
      closeAddFeedbackModal();
      // 重新加载反馈列表
      await loadFeedbackHistory();
      
      // 可选：发送系统通知告知用户
      try {
        await UserApi.addmessage({
          userId: userInfo.value.id,
          type: 1,
          title: '反馈提交成功',
          content: '您的反馈已成功提交，我们会尽快处理，请耐心等待回复。'
        });
      } catch (error) {
        console.error('添加反馈成功通知失败:', error);
      }
    } else {
      ElMessage.error(res.message || '反馈提交失败，请稍后再试');
    }
  } catch (error) {
    console.error('提交反馈失败:', error);
    ElMessage.error('网络异常，反馈提交失败');
  }
};

// 计算属性：是否可以提交反馈
const canSubmitFeedback = computed(() => {
  return feedbackForm.value.type && 
         feedbackForm.value.content && 
         feedbackForm.value.content.trim().length >= 5;
});

// 充值相关方法
// 打开金额选择区域
const openRechargeModal = () => {
  showRechargeAmountSelect.value = true;
};

// 隐私设置相关方法
const openPrivacyModal = () => {
  isPrivacyModalOpen.value = true;
  privacyTab.value = 'phone';
  // 重置表单数据
  newPhone.value = '';
  phoneCode.value = '';
  currentPassword.value = '';
  newPassword.value = '';
  confirmPassword.value = '';
};

const closePrivacyModal = () => {
  isPrivacyModalOpen.value = false;
  // 清除定时器
  if (phoneCodeTimer.value) {
    clearInterval(phoneCodeTimer.value);
    phoneCodeTimer.value = 0;
  }
};

// 发送手机验证码
const sendPhoneCode = async () => {
  
  try {
    //调用获取验证码接口（替换为你的实际接口）
    const res = await registerApi.getCode(userInfo.value.phone);
    if (res.code === 200) {
      ElMessage.success('验证码已发送，请注意查收');
      code.value=res.data.code;
    }
  ///  模拟验证码发送成功，开始倒计时
    phoneCodeTimer.value = 60;
    const timer = setInterval(() => {
      phoneCodeTimer.value--;
      if (phoneCodeTimer.value <= 0) {
        clearInterval(timer);
      }
    }, 1000);
  } catch (error) {
    ElMessage.error('手机号格式错误，请重新输入');
  }
};

// 保存隐私设置
const savePrivacySettings = async () => {
  if (privacyTab.value === 'phone') {
    // 修改手机号
    if (!newPhone.value || !/^1[3-9]\d{9}$/.test(newPhone.value)) {
      ElMessage.error('请输入正确的手机号');
      return;
    }
    if (!phoneCode.value || phoneCode.value.length !== 6) {
      ElMessage.error('请输入6位验证码');
      return;
    }
    if(phoneCode.value!=code.value){
        ElMessage.error('请输入正确验证码')
        return;
    }

    try {
      // 调用修改手机号的API
       const res = await UserApi.updateUser({ 
         id: userInfo.value.id, 
         phone: newPhone.value 
      });
     if (res.code==200) {
        userInfo.value.phone = newPhone.value;
        closePrivacyModal();
        ElMessage.success('手机号修改成功');
       } else {
        ElMessage.error('修改失败');
        return;
      }
    } catch (error) {
      console.error('修改手机号失败:', error);
      ElMessage.error('网络异常，修改失败');
      return;
    }
  } else {
    // 修改密码
    if (!currentPassword.value) {
      ElMessage.error('请输入当前密码');
      return;
    }
    if(currentPassword.value!=userInfo.value.password){
      ElMessage.error('当前密码不正确');
      return;
    }
    if (!newPassword.value || newPassword.value.length < 6) {
      ElMessage.error('新密码至少6位');
      return;
    }
    if (newPassword.value !== confirmPassword.value) {
       ElMessage.error('两次输入的密码不一致');
      return;
    }
    
    try {
      // 调用修改密码的API
      const res = await UserApi.updateUser({ 
         id: userInfo.value.id, 
         password: newPassword.value 
       });
     if (res.code==200) {
        closePrivacyModal();
        ElMessage.success('密码修改成功');
     } else {
         ElMessage.error('修改失败：' + res.message);
         return;
     }
    } catch (error) {
      console.error('修改密码失败:', error);
      ElMessage.error('网络异常，修改失败');
      return;
    }
  }
  ElMessage.warning('隐私设置已修改，请重新登录');
  let addmessage=await UserApi.addmessage({
  userId:userInfo.value.id,
  type:1,
  title:'系统通知',
  content:'您的账户隐私信息已更新'
})
  localStorage.removeItem('avatar')
  localStorage.removeItem('phone')
  localStorage.removeItem('token')
  router.push('/login');
};

// 计算属性：是否可以保存隐私设置
const canSavePrivacy = computed(() => {
  if (privacyTab.value === 'phone') {
    return newPhone.value && phoneCode.value.length === 6;
  } else {
    return currentPassword.value && newPassword.value.length >= 6 && newPassword.value === confirmPassword.value;
  }
});
// 确认充值金额并开始支付宝支付
const confirmRechargeAmount = async () => {
  const amount = customAmount.value || selectedAmount.value;
  if (!amount || amount < 10 || amount > 10000) {
    alert('请输入有效的充值金额（10-10000元）');
    return;
  }
  showRechargeAmountSelect.value = false;
  // 直接开始支付宝支付
  await startAlipayPayment(amount);
};
// 开始支付宝支付
const startAlipayPayment = async (amount) => {
  try {
    // 构建支付数据
    const payData = {
      amount: amount,
      type: 'recharge' // 充值类型
    };

    console.log('开始支付宝支付，充值金额：', amount);

    // 调用后端接口生成支付宝支付
    const res = await UserApi.userRecharge(payData);
    
    if (res.code === 200 && res.data.alipayForm) {
      // 打开支付宝支付页面
      const newWindow = window.open('', '_blank');
      newWindow.document.write(res.data.alipayForm);
      newWindow.document.close();
      
      ElMessage.success('正在跳转到支付宝...');
      
      // 跳转到支付成功页面（或者等待用户支付完成后回调）
      setTimeout(() => {
        // 可以跳转到支付成功页面，或者直接更新余额
        // 这里我们先跳转到支付成功页面
        router.push({
          path: '/home/PaymentSuccess',
          query: {
            amount: amount,
            type: 'recharge'
          }
        });
      }, 1000);
      
    } else {
      ElMessage.error('支付接口调用失败');
    }
  } catch (error) {
    console.error('支付宝支付失败:', error);
    ElMessage.error('支付失败：' + (error.response?.data?.message || error.message));
  }
};

// 消息通知相关方法
// 打开消息通知弹窗
const openMessageModal = () => {
  isMessageModalOpen.value = true;
  getMessageList();
};

// 关闭消息通知弹窗
const closeMessageModal = () => {
  isMessageModalOpen.value = false;
};

// 获取消息列表
const getMessageList = async () => {
  try {
    const res = await MessageApi.getMessageList();
    if (res.code === 200) {
      messageList.value = res.data.data || [];
    }
  } catch (error) {
    console.error('获取消息列表失败:', error);
  }
};

// 获取未读消息数量
const getUnreadCount = async () => {
  try {
    const res = await MessageApi.getUnreadCount();
    if (res.code === 200) {
      unreadCount.value = res.data || 0;
    }
  } catch (error) {
    console.error('获取未读消息数量失败:', error);
  }
};

// 删除消息
const deleteMessage = async (messageId) => {
  try {
    const res = await MessageApi.deleteMessage(messageId);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      messageList.value = messageList.value.filter(m => m.id !== messageId);
    }
  } catch (error) {
    console.error('删除消息失败:', error);
    ElMessage.error('删除失败，请稍后重试');
  }
};

// 收货地址相关方法
// 打开收货地址弹窗
const openAddressModal = () => {
  isAddressModalOpen.value = true;
  getAddressList();
};

// 关闭收货地址弹窗
const closeAddressModal = () => {
  isAddressModalOpen.value = false;
};

// 获取地址列表
const getAddressList = async () => {
  try {
    const res = await AddressApi.getAddressList();
    if (res.code === 200) {
      addressList.value = res.data.addressList || [];
    }
  } catch (error) {
    console.error('获取地址列表失败:', error);
  }
};

// 打开地址表单
const openAddressForm = () => {
  isEditingAddress.value = true;
  editingAddressId.value = null;
  addressForm.value = {
    receiverName: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  };
};

// 关闭地址表单
const closeAddressForm = () => {
  isEditingAddress.value = false;
  editingAddressId.value = null;
  addressForm.value = {
    receiverName: '',
    phone: '',
    province: '',
    city: '',
    district: '',
    detailAddress: '',
    isDefault: false
  };
};

// 编辑地址
const editAddress = (address) => {
  isEditingAddress.value = true;
  editingAddressId.value = address.id;
  addressForm.value = {
    receiverName: address.receiver,
    phone: address.phone,
    province: '',
    city: address.city,
    district: address.district,
    detailAddress: address.detailAddress,
    isDefault: address.isDefault === 1 || address.isDefault === true
  };
};

// 保存地址
const saveAddress = async () => {
  // 验证表单
  if (!addressForm.value.receiverName.trim()) {
    ElMessage.error('请输入收货人姓名');
    return;
  }
  if (!addressForm.value.phone.trim()) {
    ElMessage.error('请输入手机号码');
    return;
  }
  if (!/^1[3-9]\d{9}$/.test(addressForm.value.phone)) {
    ElMessage.error('请输入正确的手机号码');
    return;
  }
  if (!addressForm.value.city.trim() || !addressForm.value.district.trim()) {
    ElMessage.error('请选择所在地区');
    return;
  }
  if (!addressForm.value.detailAddress.trim()) {
    ElMessage.error('请输入详细地址');
    return;
  }

  try {
    const submitData = {
      userId: userInfo.value.id,
      receiver: addressForm.value.receiverName,
      phone: addressForm.value.phone,
      city: addressForm.value.city,
      district: addressForm.value.district,
      detailAddress: addressForm.value.detailAddress,
      isDefault: addressForm.value.isDefault ? 1 : 0
    };

    if (editingAddressId.value) {
      // 更新地址
      const res = await AddressApi.UpdateAddress({ ...submitData, id: editingAddressId.value });
      if (res.code === 200) {
        ElMessage.success('地址修改成功');
        closeAddressForm();
        getAddressList();
      } else {
        ElMessage.error('地址修改失败');
      }
    } else {
      // 添加地址
      const res = await AddressApi.AddAddress(submitData);
      if (res.code === 200) {
        ElMessage.success('地址添加成功');
        closeAddressForm();
        getAddressList();
      } else {
        ElMessage.error('地址添加失败');
      }
    }
  } catch (error) {
    console.error('保存地址失败:', error);
    ElMessage.error('操作失败，请稍后重试');
  }
};

// 设为默认地址
const setDefaultAddress = async (addressId) => {
  try {
    const res = await AddressApi.UpdateAddress({ id: addressId, isDefault: 1 });
    if (res.code === 200) {
      ElMessage.success('已设为默认地址');
      getAddressList();
    } else {
      ElMessage.error('设置失败');
    }
  } catch (error) {
    console.error('设置默认地址失败:', error);
    ElMessage.error('操作失败，请稍后重试');
  }
};

// 删除地址
const deleteAddress = async (addressId) => {
  try {
    const res = await AddressApi.DelAddress(addressId);
    if (res.code === 200) {
      ElMessage.success('删除成功');
      getAddressList();
    } else {
      ElMessage.error('删除失败');
    }
  } catch (error) {
    console.error('删除地址失败:', error);
    ElMessage.error('操作失败，请稍后重试');
  }
};

// 获取消息类型文本
const getMessageTypeText = (type) => {
  switch (type) {
    case 1:
      return '系统通知';
    case 2:
      return '订单提醒';
    case 3:
      return '优惠活动';
    default:
      return '通知';
  }
};

// 格式化时间
const formatTime = (time) => {
  if (!time) return '';
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  
  // 如果是今天
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
  }
  // 如果是昨天
  const yesterday = new Date(now);
  yesterday.setDate(yesterday.getDate() - 1);
  if (date.toDateString() === yesterday.toDateString()) {
    return '昨天 ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
  }
  // 其他日期
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' }) + 
         ' ' + date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' });
};


</script>

<style scoped>
/* 整体布局 */
.layout-container {
  display: flex;
  width: 100%;
  min-height: 100vh;
  background-color: #f8f9fa;
  overflow: hidden;
}

/* 左侧侧边栏 */
.sidebar {
  width: 220px;
  min-width: 220px;
  max-width: 220px;
  flex-shrink: 0;
  background-color: #fff;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.05);
  padding: 20px 10px;
  display: flex;
  flex-direction: column;
  gap: 30px;
  box-sizing: border-box;
}
.sidebar-user {
  text-align: center;
  padding: 10px 0;
  border-bottom: 1px solid #e9ecef;
}
.user-top {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 10px;
}
.user-name {
  font-size: 16px;
  font-weight: 600;
}
.user-nickname {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}
.growth-value {
  font-size: 12px;
  color: #6c757d;
  cursor: pointer;
}

/* 侧边栏功能/订单入口 */
.sidebar-func, .sidebar-entry, .sidebar-order {
  display: flex;
  flex-direction: column;
  gap: 15px;
}
.sidebar-order {
  gap: 10px;
}
.sidebar-order h4 {
  font-size: 14px;
  font-weight: 600;
  padding: 0 10px;
  color: #666;
}
.func-item, .entry-item, .order-item {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
  padding: 8px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}
.func-item:hover, .entry-item:hover, .order-item:hover {
  background-color: #f8f9fa;
}
.func-item img, .entry-item img, .order-item img {
  width: 24px;
  height: 24px;
}
.func-item p, .entry-item p, .order-item p {
  font-size: 14px;
}

/* 右侧内容区 */
.content-area {
  flex: 1;
  min-width: 0;
  padding: 30px;
  background-color: #fff;
  box-shadow: 0 0 8px rgba(0, 0, 0, 0.05);
  margin: 20px;
  border-radius: 8px;
  min-height: 80vh;
  overflow: hidden;
}

/* 个人信息页面样式 */
.personal-info-section {
  width: 100%;
}
.info-tabs {
  display: flex;
  gap: 0;
  border-bottom: 1px solid #e9ecef;
  margin-bottom: 20px;
}
.tab-item {
  padding: 10px 20px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}
.tab-item.active {
  color: #d9251c;
  border-bottom-color: #d9251c;
}
.tab-item:hover {
  color: #d9251c;
}

/* 我的信息子选项样式 */
.my-info-content {
  padding: 10px 0;
}
.my-info-content h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
}

/* 头像区域 */
.avatar-section {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}
.avatar-wrapper {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}
.avatar-img {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e9ecef;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
.change-avatar-btn {
  padding: 6px 16px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
}
.change-avatar-btn:hover {
  background-color: #e9ecef;
  color: #333;
}

.info-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 16px;
}
.info-item label {
  width: 80px;
  color: #666;
}
.info-item span {
  color: #333;
}
.edit-nickname-btn {
  margin-left: auto;
  padding: 4px 12px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: background-color 0.3s;
}
.edit-nickname-btn:hover {
  background-color: #e9ecef;
}

/* 设置子选项样式 */
.setting-content {
  padding: 10px 0;
}
.setting-content h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
}
.setting-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px 10px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  font-size: 16px;
}
.setting-item:hover {
  background-color: #f8f9fa;
}
.setting-item img:first-child {
  width: 24px;
  height: 24px;
  margin-right: 10px;
}
.setting-item span {
  flex: 1;
  color: #333;
}
.setting-item img:last-child {
  width: 16px;
  height: 16px;
  color: #999;
}

/* 钱包样式 */
.wallet-section h4 {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e9ecef;
}
.wallet-info {
  margin-bottom: 30px;
}
.wallet-balance {
  margin-bottom: 20px;
}
.balance-label {
  font-size: 16px;
  color: #666;
  margin-right: 10px;
}
.balance-amount {
  font-size: 32px;
  font-weight: bold;
  color: #d9251c;
}
.wallet-quick {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  padding: 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}
.quick-item:hover {
  background-color: #f8f9fa;
}
.quick-item img {
  width: 32px;
  height: 32px;
}
.quick-item p {
  font-size: 14px;
}

/* 充值金额选择样式 */
.recharge-amount-select {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}
.recharge-amount-select h5 {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 20px;
  color: #333;
}
.recharge-amounts {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
}
.amount-item {
  flex: 0 0 calc(25% - 11.25px);
  text-align: center;
  padding: 15px;
  background-color: #fff;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 16px;
  font-weight: 500;
}
.amount-item:hover {
  border-color: #d9251c;
}
.amount-item.active {
  border-color: #d9251c;
  background-color: #fff0f0;
  color: #d9251c;
}
.custom-amount input {
  width: 100%;
  padding: 15px;
  border: 2px solid #e9ecef;
  border-radius: 6px;
  font-size: 16px;
  text-align: center;
  margin-bottom: 20px;
}
.custom-amount input:focus {
  outline: none;
  border-color: #d9251c;
}
.recharge-btn {
  width: 100%;
  padding: 15px;
  background-color: #d9251c;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  cursor: pointer;
  font-size: 18px;
  font-weight: 600;
}

/* 404样式 */
.not-found {
  text-align: center;
  padding: 50px 20px;
}
.not-found-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}
.not-found-text {
  font-size: 16px;
  color: #333;
}

/* 通用弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
}
.modal-content {
  background-color: #fff;
  width: 400px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}
.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e9ecef;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}
.close-btn {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
}
.modal-body {
  padding: 20px;
}
.nickname-input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  font-size: 16px;
  outline: none;
}
.nickname-input:focus {
  border-color: #d9251c;
}
.modal-footer {
  padding: 16px 20px;
  border-top: 1px solid #e9ecef;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
.cancel-btn {
  padding: 8px 20px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.confirm-btn {
  padding: 8px 20px;
  background-color: #d9251c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}
.confirm-btn:disabled {
  background-color: #e9ecef;
  cursor: not-allowed;
}

/* 头像上传弹窗样式 */
.avatar-modal {
  width: 450px;
}

.avatar-upload-area {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.avatar-uploader {
  width: 150px;
  height: 150px;
}

.avatar-uploader .avatar {
  width: 150px;
  height: 150px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-uploader .el-upload {
  border: 2px dashed #d9d9d9;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s;
}

.avatar-uploader .el-upload:hover {
  border-color: #d9251c;
}

.avatar-uploader-icon {
  font-size: 40px;
  color: #8c939d;
  width: 150px;
  height: 150px;
  text-align: center;
  line-height: 150px;
}

.upload-trigger-btn {
  padding: 8px 20px;
  background-color: #d9251c;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background-color 0.3s;
}

.upload-trigger-btn:hover {
  background-color: #b91f16;
}

.upload-tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  margin: 0;
}

/* 反馈弹窗样式 */
.feedback-modal {
  width: 500px;
}

.feedback-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.feedback-form .form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.feedback-form .form-group label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.feedback-form .form-group .required {
  color: #d9251c;
}

.feedback-form .form-select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  background-color: #fff;
  cursor: pointer;
  transition: border-color 0.3s;
}

.feedback-form .form-select:focus {
  outline: none;
  border-color: #d9251c;
}

.feedback-form .form-textarea {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  transition: border-color 0.3s;
}

.feedback-form .form-textarea:focus {
  outline: none;
  border-color: #d9251c;
}

.feedback-form .char-count {
  font-size: 12px;
  color: #999;
  text-align: right;
}

.priority-options {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.priority-option {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
}

.priority-option input[type="radio"] {
  cursor: pointer;
}

.priority-label {
  padding: 5px 15px;
  border-radius: 4px;
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
}

.priority-label.low {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.priority-label.medium {
  background-color: #fff3e0;
  color: #ef6c00;
}

.priority-label.high {
  background-color: #ffebee;
  color: #c62828;
}

.priority-label.urgent {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.priority-option:hover .priority-label {
  opacity: 0.8;
}

.priority-option input[type="radio"]:checked + .priority-label {
  box-shadow: 0 0 0 2px #d9251c;
}

/* 隐私设置弹窗样式 */
.privacy-modal {
  width: 450px;
}

.privacy-tabs {
  display: flex;
  margin-bottom: 20px;
  border-bottom: 1px solid #e9ecef;
}

.privacy-tab {
  flex: 1;
  padding: 10px 0;
  text-align: center;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.privacy-tab.active {
  color: #d9251c;
  border-bottom-color: #d9251c;
}

.privacy-tab:hover {
  color: #d9251c;
}

.privacy-form {
  padding: 10px 0;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.form-item input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.form-item input:focus {
  border-color: #d9251c;
}

.form-item span {
  display: inline-block;
  padding: 10px 0;
  font-size: 14px;
  color: #333;
}

.code-input-group {
  display: flex;
  gap: 10px;
}

.code-input-group input {
  flex: 1;
}

.code-btn {
  padding: 10px 16px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  white-space: nowrap;
  transition: all 0.3s;
}

.code-btn:hover:not(:disabled) {
  background-color: #e9ecef;
}

.code-btn:disabled {
  cursor: not-allowed;
  color: #999;
}

/* 消息通知弹窗样式 */
.message-modal {
  width: 600px;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

.message-modal .modal-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 消息列表 */
.message-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-thumb {
  background-color: #e9ecef;
  border-radius: 3px;
}

.message-item {
  padding: 15px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  margin-bottom: 12px;
  background-color: #fff;
  transition: all 0.3s;
}

.message-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.message-type {
  font-size: 12px;
  padding: 3px 8px;
  background-color: #f0f0f0;
  border-radius: 3px;
  color: #666;
}

.message-time {
  font-size: 12px;
  color: #999;
}

.message-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.message-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 12px;
}

.message-footer {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.message-btn {
  padding: 5px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.delete-btn {
  background-color: #ffebee;
  color: #f44336;
}

.delete-btn:hover {
  background-color: #ffcdd2;
}

/* 空状态 */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 15px;
  opacity: 0.5;
}

.empty-text {
  font-size: 14px;
  color: #999;
}

/* 收货地址弹窗样式 */
.address-modal {
  width: 700px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.address-modal .modal-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 地址列表 */
.address-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
  margin-bottom: 15px;
}

.address-list::-webkit-scrollbar {
  width: 6px;
}

.address-list::-webkit-scrollbar-thumb {
  background-color: #e9ecef;
  border-radius: 3px;
}

.address-item {
  padding: 15px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  margin-bottom: 12px;
  background-color: #fff;
  transition: all 0.3s;
}

.address-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.address-item.default {
  border-color: #d9251c;
  background-color: #fff0f0;
}

.address-info {
  margin-bottom: 10px;
}

.address-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.receiver-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.receiver-phone {
  font-size: 14px;
  color: #666;
}

.default-tag {
  background-color: #d9251c;
  color: #fff;
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 3px;
}

.address-detail {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  gap: 8px;
  justify-content: flex-end;
}

.address-btn {
  padding: 5px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.edit-btn {
  background-color: #e8f5e9;
  color: #4caf50;
}

.edit-btn:hover {
  background-color: #c8e6c9;
}

.set-default-btn {
  background-color: #e3f2fd;
  color: #2196f3;
}

.set-default-btn:hover {
  background-color: #bbdefb;
}

.delete-btn {
  background-color: #ffebee;
  color: #f44336;
}

.delete-btn:hover {
  background-color: #ffcdd2;
}

.add-address-btn {
  width: 100%;
  padding: 12px;
  background-color: #f8f9fa;
  border: 2px dashed #e9ecef;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  transition: all 0.3s;
  margin-top: 10px;
}

.add-address-btn:hover {
  background-color: #e9ecef;
  color: #d9251c;
  border-color: #d9251c;
}

/* 地址表单弹窗样式 */
.address-form-modal {
  width: 500px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

.required {
  color: #f44336;
}

.form-group input[type="text"],
.form-group input[type="tel"],
.form-group textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #d9251c;
}

.form-group textarea {
  resize: vertical;
}

.region-select {
  display: flex;
  gap: 10px;
}

.region-select input {
  flex: 1;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.checkbox-label input[type="checkbox"] {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.checkbox-label span {
  font-size: 14px;
  color: #333;
}

/* 反馈列表弹窗样式 */
.feedback-list-modal {
  width: 700px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.feedback-list-modal .modal-body {
  flex: 1;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 添加反馈按钮 */
.add-feedback-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  width: 100%;
  padding: 12px;
  background-color: #d9251c;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.add-feedback-btn:hover {
  background-color: #b01e17;
  transform: translateY(-1px);
}

.add-feedback-btn .el-icon {
  font-size: 18px;
}

/* 反馈列表 */
.feedback-list {
  flex: 1;
  overflow-y: auto;
  padding-right: 5px;
}

.feedback-list::-webkit-scrollbar {
  width: 6px;
}

.feedback-list::-webkit-scrollbar-thumb {
  background-color: #e9ecef;
  border-radius: 3px;
}

/* 反馈历史项 */
.feedback-history-item {
  padding: 15px;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  margin-bottom: 12px;
  background-color: #fff;
  transition: all 0.3s;
}

.feedback-history-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 反馈头部 */
.feedback-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  flex-wrap: wrap;
}

/* 反馈类型徽章 */
.feedback-type-badge {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.feedback-type-badge.type-1 {
  background-color: #e3f2fd;
  color: #2196f3;
}

.feedback-type-badge.type-2 {
  background-color: #fff3e0;
  color: #ff9800;
}

.feedback-type-badge.type-3 {
  background-color: #ffebee;
  color: #f44336;
}

.feedback-type-badge.type-4 {
  background-color: #f5f5f5;
  color: #666;
}

/* 反馈优先级 */
.feedback-priority {
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 500;
}

.feedback-priority.priority-1 {
  background-color: #f5f5f5;
  color: #666;
}

.feedback-priority.priority-2 {
  background-color: #e8f5e9;
  color: #4caf50;
}

.feedback-priority.priority-3 {
  background-color: #ffebee;
  color: #f44336;
}

/* 反馈时间 */
.feedback-time {
  margin-left: auto;
  font-size: 12px;
  color: #999;
}

/* 反馈内容 */
.feedback-body {
  margin-bottom: 10px;
}

.feedback-content-text {
  font-size: 14px;
  color: #333;
  line-height: 1.6;
  margin: 0;
  word-break: break-word;
}

/* 反馈状态 */
.feedback-status {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.feedback-status.status-0 {
  background-color: #f5f5f5;
  color: #666;
}

.feedback-status.status-1 {
  background-color: #fff3e0;
  color: #ff9800;
}

.feedback-status.status-2 {
  background-color: #e8f5e9;
  color: #4caf50;
}

.feedback-status.status-3 {
  background-color: #f5f5f5;
  color: #999;
}

/* 添加反馈弹窗样式 */
.feedback-modal {
  width: 500px;
}

.feedback-form .form-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  background-color: #fff;
  cursor: pointer;
}

.feedback-form .form-select:focus {
  border-color: #d9251c;
}

.feedback-form .form-textarea {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #e9ecef;
  border-radius: 4px;
  font-size: 14px;
  outline: none;
  resize: vertical;
  font-family: inherit;
}

.feedback-form .form-textarea:focus {
  border-color: #d9251c;
}

.feedback-form .char-count {
  text-align: right;
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

/* 优先级选项 */
.priority-options {
  display: flex;
  gap: 20px;
}

.priority-option {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
}

.priority-option input[type="radio"] {
  cursor: pointer;
}

.priority-label {
  font-size: 13px;
  padding: 4px 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.priority-label.low {
  background-color: #f5f5f5;
  color: #666;
}

.priority-label.medium {
  background-color: #e8f5e9;
  color: #4caf50;
}

.priority-label.high {
  background-color: #ffebee;
  color: #f44336;
}

.priority-option input:checked + .priority-label {
  transform: scale(1.05);
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
}


</style>