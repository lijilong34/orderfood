<template>
  <div class="daily-coupon-page">
    <h4>每日领券</h4>
    
    <!-- 每日领券区域 -->
    <div class="daily-coupon-section">
      <div class="coupon-header">
        <div class="coupon-title">
          <img src="https://img.icons8.com/fluency/48/000000/ticket.png" alt="优惠券" class="coupon-icon">
          <span>优惠券</span>
        </div>
        <div class="coupon-count">今日剩余 {{ availableCoupons.length }} 张</div>
      </div>
      
      <!-- 优惠券列表 -->
      <div class="coupon-list">
        <div 
          v-for="coupon in availableCoupons" 
          :key="coupon.id" 
          class="coupon-item"
          :class="{ 
            'coupon-available': coupon.status === 1,
            'coupon-claimed': coupon.status === 0,
            'coupon-expired': coupon.status === 2
          }"
        >
          <div class="coupon-info">
            <div class="coupon-amount">
              <span class="amount-value">{{ coupon.title }}</span>
              <span class="amount-condition"><el-tag :type="coupon.discountType === 1 ? 'primary' : 'success'">{{ coupon.discountType === 1 ? '满减券' : '折扣券' }}</el-tag></span>
            </div>
            <div class="coupon-details">
              <p class="coupon-title-text">优惠值：{{ coupon.discountValue }}</p>
              <p class="coupon-time">
                有效期：{{ formatDate(coupon.startTime) }} 至 {{ formatDate(coupon.endTime) }}
              </p>
            </div>
          </div>
          <div class="coupon-action">
            <button 
              v-if="coupon.status === 1" 
              @click="claimCoupon(coupon)"
              class="claim-btn"
              :disabled="!canClaimMore || loading"
            >
              {{ canClaimMore ? '立即领取' : '已领完' }}
            </button>
            <!-- <span v-else-if="coupon.status === 0" class="claimed-text">已使用</span>
            <span v-else class="expired-text">{{ getStatusText(coupon.status) }}</span> -->
          </div>
        </div>
      </div>
      
      <!-- 领券说明 -->
      <div class="coupon-rules">
        <h5>领券规则</h5>
        <ul>
          <li>每日限量发放，先到先得</li>
          <li>每用户每日最多领取 5 张优惠券</li>
          <li>优惠券仅限当日使用，逾期无效</li>
          <li>部分商品可能不参与优惠活动</li>
        </ul>
      </div>
    </div>
    
    <!-- 已领取优惠券 -->
    <div class="claimed-coupons-section" v-if="allUserCoupons.length > 0">
      <h4>我的优惠券</h4>
      <div class="claimed-coupon-list">
        <div 
          v-for="userCoupon in allUserCoupons" 
          :key="userCoupon.id" 
          class="user-coupon-item"
          :class="{ 
            'coupon-valid': userCoupon.status === 1,
            'coupon-used': userCoupon.status === 0,
            'coupon-expired': userCoupon.status === 2
          }"
        >
          <div class="user-coupon-info">
            <div class="user-coupon-amount">
              <span class="amount-value">{{ getDiscountDisplay(userCoupon) }}</span>
              <span class="amount-condition">{{ getConditionDisplay(userCoupon) }}</span>
            </div>
            <div class="user-coupon-details">
              <p class="coupon-title-text">{{ userCoupon.title }}</p>
              <p class="coupon-time">到期时间：{{ formatDate(userCoupon.endTime) }}</p>
            </div>
          </div>
          <div class="user-coupon-action">
            <button 
              v-if="userCoupon.status == 1" 
              @click="useCoupon(userCoupon)"
              class="use-btn"
              :disabled="loading"
            >
              去使用
            </button>
            <span v-else-if="userCoupon.status == 0" class="used-text">已使用</span>
            <span v-else class="expired-text">已过期</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, computed, watch } from 'vue';
import { ElMessage } from 'element-plus';
import { useRouter } from 'vue-router';
import CouponApi from '../../../../api/CouponApi';

const router = useRouter();

// 响应式数据
const loading = ref(false);

// 每日固定优惠券数据
const dailyCoupons = ref([]);

// 可领取的优惠券列表（根据今日领取状态动态生成）
const availableCoupons = ref([]);

const userCoupons = ref([]); // 今日已领取的优惠券列表（用于判断领取状态）
const allUserCoupons = ref([]); // 所有用户优惠券列表（用于显示"我的优惠券"）

// 状态值
const claimedToday = ref(0);
const maxDailyClaims = ref(5); // 每用户每日最多领取5张

const canClaimMore = computed(() => {
  return claimedToday.value < maxDailyClaims.value;
});

// 格式化日期时间
const formatDateTime = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleString('zh-CN');
};

// 格式化日期（仅日期部分）
const formatDate = (date) => {
  if (!date) return '';
  return new Date(date).toLocaleDateString('zh-CN');
};

// 获取优惠券显示金额
const getDiscountDisplay = (coupon) => {
  if (coupon.discountType === 1) {
    // 满减优惠券
    return `¥${coupon.discountValue}`;
  } else if (coupon.discountType === 2) {
    // 折扣优惠券
    return `${coupon.discountValue * 10}折`;
  }
  return `¥${coupon.discountValue}`;
};

// 检查今日是否已领取该优惠券（通过标题匹配）
// const isClaimedToday = (couponId) => {
//   const coupon = dailyCoupons.value.find(c => c.id === couponId);
//   if (!coupon) return false;
//   return userCoupons.value.some(uc => uc.title === coupon.title);
// };

// // 生成可领取的优惠券列表
// const generateAvailableCoupons = () => {
//   availableCoupons.value = dailyCoupons.value.map((coupon) => {
//     // 直接通过标题匹配判断是否已领取
//     const isClaimed = userCoupons.value.some(uc => uc.title === coupon.title);
//     return {
//       ...coupon,
//       status: isClaimed ? 0 : 1, // 0-已领取，1-可领取
//       startTime: new Date(), // 从当前时间开始
//       endTime: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000) // 30天后过期
//     };
//   });
// };

// 获取使用条件显示
const getConditionDisplay = (coupon) => {
  const minAmount = coupon.minAmount;
  if (!minAmount || minAmount <= 0) {
    return '无门槛使用';
  }
  return `满${minAmount}元可用`;
};

// 判断优惠券状态
const getCouponStatus = (coupon) => {
  const now = new Date();
  const startTime = new Date(coupon.startTime);
  const endTime = new Date(coupon.endTime);
  
  if (coupon.status === 0) {
    return 'used'; // 已使用
  } else if (coupon.status === 2 || endTime < now) {
    return 'expired'; // 已过期
  } else if (startTime > now) {
    return 'pending'; // 未生效
  } else {
    return 'available'; // 可用
  }
};

// 获取优惠券状态文本
const getStatusText = (status) => {
  const statusMap = {
    1: '可领取',
    0: '已领取',
    2: '已过期'
  };
  return statusMap[status] || '未知';
};

// 领取优惠券
const claimCoupon = async (coupon) => {
  console.log('尝试领取优惠券:', coupon);
  
  
  loading.value = true;
  try {
    // 调用API领取优惠券
    const response = await CouponApi.claimCoupon({
      couponId: coupon.id
    });
    
    console.log('领取响应:', response);
    
    if (response.code === 200) {
      ElMessage.success('优惠券领取成功！');
      
      // 重新获取用户优惠券列表以同步后端数据（只获取今日领取的）
      // await fetchUserCoupons(true);
      
      // // 更新今日领取数量
      // claimedToday.value = userCoupons.value.length;
      
      // const today = new Date().toISOString().split('T')[0];
      // localStorage.setItem('dailyClaimDate', today);
      // localStorage.setItem('dailyClaimCount', claimedToday.value.toString());
      
      // 重新生成可领取列表 - 确保立即更新UI
        loadnotakeCoupons(); // 重新加载可领取优惠券列表
    }
  } catch (error) {
    console.error('领取优惠券失败:', error);
    console.error('错误详情:', error.response || error.message || error);
    const errorMessage = error.message || error.response?.data?.message || '领取失败，请稍后重试';
    ElMessage.error(errorMessage);
    // 重新获取用户优惠券列表以同步后端数据（只获取今日领取的）
    await fetchUserCoupons(true);
   
  } finally {
    loading.value = false;
  }
};
// 使用优惠券（直接跳转到首页，不调用后端接口）
const useCoupon = (coupon) => {
  if (coupon.status !== 1) {
    ElMessage.warning('优惠券不可用');
    return;
  }
  
  // 直接跳转到首页
  router.push('/home/selectall');
};


const fetchUserCoupons = async (todayOnly = false) => {
    try {
        const response = await CouponApi.getUserCoupons();
        if (response.code === 1 || response.code === 200) {
            const allCoupons = response.data.couponList || [];
            
            // 添加调试信息
            console.log('获取到的优惠券数据:', allCoupons);

            
            // 始终保存所有优惠券到allUserCoupons
            allUserCoupons.value = allCoupons;
            
            if (todayOnly) {
                // 只获取今日领取的优惠券用于判断领取状态
                const today = new Date().toISOString().split('T')[0];
                userCoupons.value = allCoupons.filter(coupon => {
                    const createTime = new Date(coupon.createTime).toISOString().split('T')[0];
                    return createTime === today;
                });
            } else {
                // 获取所有优惠券
                userCoupons.value = allCoupons;
            }
        } else {
            console.error('获取用户优惠券失败:', response.message);
        }
    } catch (error) {
        console.error('获取用户优惠券出错:', error);
    }
};

// 获取今日已领取的优惠券ID列表（已废弃，直接使用userCoupons判断）
const fetchTodayClaimedCouponIds = async () => {
    // 此函数已废弃，现在直接通过userCoupons.value来判断领取状态
    // 保留此函数是为了兼容性，实际功能已移除
    console.log('fetchTodayClaimedCouponIds已废弃，使用userCoupons.value代替');
};







// 初始化数据
onMounted(() => { 
     loadnotakeCoupons();
     refreshCouponData();
});



// 刷新优惠券数据
const refreshCouponData = async () => {
  // 从数据库获取最新数据（只获取今日领取的用于判断状态）
  await fetchUserCoupons(true);
  
  // 更新今日领取数量
  claimedToday.value = userCoupons.value.length;
  
  // 生成可领取的优惠券列表
 
  
};
//加载未领取的优惠券（模拟数据）
async function loadnotakeCoupons(){
 let res = await CouponApi.getAvailableCoupons();
 console.log('获取到的可领取优惠券数据:', res);
 availableCoupons.value = res.data.couponList;
}
</script>

<style scoped>
.daily-coupon-page {
  padding: 30px;
  max-width: 1200px;
  margin: 0 auto;
}

.daily-coupon-page h4 {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e9ecef;
  color: #333;
}

.daily-coupon-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 30px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.coupon-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #f0f0f0;
}

.coupon-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.coupon-icon {
  width: 24px;
  height: 24px;
}

.coupon-count {
  font-size: 14px;
  color: #666;
  background: #f8f9fa;
  padding: 5px 12px;
  border-radius: 15px;
}

.coupon-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.coupon-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  border-radius: 12px;
  border: 2px solid #f0f0f0;
  transition: all 0.3s ease;
}

.coupon-item.coupon-available {
  background: linear-gradient(135deg, #fff5f5 0%, #ffe0e0 100%);
  border-color: #ffcccc;
}

.coupon-item.coupon-claimed {
  background: #f8f9fa;
  border-color: #e9ecef;
  opacity: 0.7;
}

.coupon-item.coupon-expired {
  background: #f5f5f5;
  border-color: #ddd;
  opacity: 0.5;
}

.coupon-info {
  flex: 1;
}

.coupon-amount {
  margin-bottom: 8px;
}

.amount-value {
  font-size: 24px;
  font-weight: bold;
  color: #d9251c;
  margin-right: 8px;
}

.amount-condition {
  font-size: 12px;
  color: #666;
}

.coupon-details {
  font-size: 12px;
  color: #666;
}

.coupon-title-text {
  margin: 0 0 4px 0;
  font-weight: 600;
  color: #333;
}

.coupon-time {
  margin: 0;
}

.coupon-action {
  margin-left: 15px;
}

.claim-btn {
  background: linear-gradient(45deg, #d9251c, #ff6b6b);
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.claim-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(217, 37, 28, 0.3);
}

.claim-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.claimed-text,
.used-text,
.pending-text {
  color: #999;
  font-size: 14px;
}

.expired-text {
  color: #ccc;
  font-size: 14px;
}

.coupon-rules {
  margin-top: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.coupon-rules h5 {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.coupon-rules ul {
  margin: 0;
  padding-left: 20px;
  font-size: 12px;
  color: #666;
}

.coupon-rules li {
  margin-bottom: 5px;
}

.claimed-coupons-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.claimed-coupon-list {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
}

.user-coupon-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: linear-gradient(135deg, #e8f5e8 0%, #d4edda 100%);
  border: 1px solid #c3e6cb;
  border-radius: 12px;
}

.user-coupon-info {
  flex: 1;
}

.user-coupon-amount {
  margin-bottom: 8px;
}

.user-coupon-details {
  font-size: 12px;
  color: #666;
}

.user-coupon-action {
  margin-left: 15px;
}

.use-btn {
  background: linear-gradient(45deg, #28a745, #34ce57);
  color: #fff;
  border: none;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.use-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(40, 167, 69, 0.3);
}

.use-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 用户优惠券状态样式 */
.user-coupon-item.coupon-valid {
  background: linear-gradient(135deg, #e8f5e8 0%, #d4edda 100%);
  border: 1px solid #c3e6cb;
}

.user-coupon-item.coupon-used {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 1px solid #dee2e6;
  opacity: 0.7;
}

.user-coupon-item.coupon-expired {
  background: linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%);
  border: 1px solid #ddd;
  opacity: 0.5;
}

/* 电脑端优化样式 */
@media (min-width: 1200px) {
  .daily-coupon-page {
    max-width: 1400px;
    padding: 40px;
  }
  
  .coupon-list,
  .claimed-coupon-list {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 768px) {
  .daily-coupon-page {
    padding: 15px;
  }
  
  .coupon-list,
  .claimed-coupon-list {
    grid-template-columns: 1fr;
  }
  
  .coupon-item,
  .user-coupon-item {
    flex-direction: column;
    align-items: stretch;
    text-align: center;
  }
  
  .coupon-action,
  .user-coupon-action {
    margin: 15px 0 0 0;
  }
}
</style>