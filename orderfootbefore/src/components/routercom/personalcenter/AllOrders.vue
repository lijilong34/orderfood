<template>
  <div class="all-orders">
    <!-- 顶部搜索栏 -->
    <!-- 时间范围限制：最多查询1年内的订单，默认显示最近30天 -->
    <div class="search-bar">
      <input type="text" placeholder="请输入商家" class="search-input" v-model="Searchform.shopname"/>
      请选择日期范围: <el-date-picker
        v-model="Searchform.dateRange"
        type="daterange"
        range-separator="到"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        :disabled-date="disabledDate"
        value-format="YYYY-MM-DD"
       />
       <el-button type="primary" @click="selectToday" plain>今天</el-button>
      <el-button type="danger" @click="searchoder" :loading="isLoading">筛选</el-button>
      <el-button @click="resetFilters">重置</el-button>
    </div>

    <!-- 订单分类标签 -->
    <div class="order-tabs">
      <div class="tab-item" :class="{ active: activeTab === 'all' }" @click="activeTab = 'all'">
        全部
      </div>
      <div class="tab-item" :class="{ active: activeTab === 'pendingPay' }" @click="activeTab = 'pendingPay'">
        待付款
      </div>
      <div class="tab-item" :class="{ active: activeTab === 'pendingReceive' }" @click="activeTab = 'pendingReceive'">
        待收货/待使用
      </div>
      <div class="tab-item" :class="{ active: activeTab === 'pendingReview' }" @click="activeTab = 'pendingReview'">
        已完成
      </div>
    </div>

    <div class="have-order" v-if="isOrderHave">
     <el-table :data="orderList">
      <el-table-column prop="id" label="序号" width="65"/>
      <el-table-column prop="orderNo" label="订单号" width="200"/>
      <el-table-column prop="shopname" label="商家名称" width="120"/>
      <el-table-column prop="totalAmount" label="订单总金额" width="100"/>
      <el-table-column prop="discountAmount" label="优惠金额" width="100"/>
      <el-table-column prop="payAmount" label="实付金额" width="80"/>
      <el-table-column label="订单类型" width="80">
        <template #default="scope">
				<span v-if="scope.row.orderType==1">店内</span>
				<span v-else-if="scope.row.orderType==2">外卖</span>
			</template>
      </el-table-column>
      <el-table-column label="订单状态" width="80">
           <template #default="scope">
				<span v-if="scope.row.status==0">待支付</span>
				<span v-else-if="scope.row.status==1">待收货</span>
        <span v-else-if="scope.row.status==2">已完成</span>
        <span v-else-if="scope.row.status==3">已取消</span>
        <span v-else-if="scope.row.status==4">已评价</span>
        <span v-else>未知状态</span>
			</template>
      </el-table-column>
      <el-table-column label="支付方式" width="80">
        <template #default="scope">
				<span v-if="scope.row.payType==1">余额</span>
				<span v-else-if="scope.row.payType==2">支付宝</span>
			</template>
      </el-table-column>
      <el-table-column label="创建时间" width="180">
        <template #default="scope">
          {{ formatDate(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280">
        <template #default="scope">
        <div style="display: flex; gap: 5px; flex-wrap: wrap;">
          <el-button type="danger" size="small" @click="takeopen(scope.row.id)">查看详情</el-button>
          <el-button v-if="scope.row.status==0" size="small" @click="openPayDialog(scope.row)">去支付</el-button>
          <el-button type="danger" v-if="scope.row.status==1" size="small" @click="Cancelorder(scope.row.id)">取消订单</el-button>
          <el-button type="success" v-if="scope.row.status==2" size="small" @click="openReviewDialog(scope.row)">去评价</el-button>
          <el-button type="warning" v-if="scope.row.status==4||scope.row.status==2" size="small" @click="openComplaintDialog(scope.row)">去投诉</el-button>
        </div>
        </template>
      </el-table-column>
     </el-table>
     <div class="pagination-container">
    <el-pagination 
      layout="prev, pager, next" 
      :total="total" 
      :page-size="6"
      @current-change="handlePageChange"
      class="theme-pagination"
    />
  </div>
    </div>
    <teleport to="body">
      <el-dialog v-model="dialogshow" title="订单详情" width="600px">
    <div v-if="orderitemlist && orderitemlist.length > 0">
      <div v-for="(orderitem, index) in orderitemlist" :key="index" style="border-bottom: 1px solid #eee; padding: 10px 0;">
        <p><strong>菜品名称：</strong>{{ orderitem.productName || orderitem.product_name || '暂无' }}</p>
        <p><strong>菜品单价：</strong>￥{{ orderitem.price || '0.00' }}</p>
        <p><strong>菜品数量：</strong>{{ orderitem.quantity || '0' }}</p>
        <p><strong>商品总价：</strong>￥{{ orderitem.amount || orderitem.total_amount || '0.00' }}</p>
        <p><strong>备注：</strong>{{ orderitem.remark || '无' }}</p>
      </div>
    </div>
    <div v-else>
      <p>暂无订单详情数据</p>
    </div>
      </el-dialog>


      <!-- 支付弹窗 -->
      <el-dialog v-model="payDialogVisible" title="选择支付方式" width="400px" :before-close="closePayDialog">
      <div class="payment-dialog">
        <div class="order-info">
          <p><strong>订单号：</strong>{{ currentOrder.orderNo }}</p>
          <p><strong>支付金额：</strong><span class="amount">￥{{ currentOrder.payAmount }}</span></p>
        </div>
        
        <div class="payment-methods">
          <el-radio-group v-model="selectedPayMethod">
            <el-radio label="balance" class="payment-option">
              <div class="payment-option-content">
                <span class="payment-icon">💰</span>
                <div class="payment-info">
                  <span class="payment-name">余额支付</span>
                  <span class="payment-desc">使用账户余额支付</span>
                </div>
              </div>
            </el-radio>
            <el-radio label="alipay" class="payment-option">
              <div class="payment-option-content">
                <img src="/alipay-logo.svg" class="payment-icon alipay-icon" alt="支付宝">
                <div class="payment-info">
                  <span class="payment-name">支付宝</span>
                  <span class="payment-desc">支付宝安全支付</span>
                </div>
              </div>
            </el-radio>
          </el-radio-group>
        </div>
      </div>
      
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closePayDialog">取消</el-button>
          <el-button type="primary" @click="confirmPay" :loading="payLoading">确认支付</el-button>
        </div>
      </template>
      </el-dialog>

      <!-- 评价弹窗 -->
      <el-dialog v-model="reviewDialogVisible" title="订单评价" width="600px" :before-close="closeReviewDialog">
      <div class="review-dialog">
        <div class="order-info">
          <p><strong>订单号：</strong>{{ reviewOrder.orderNo }}</p>
          <p><strong>商家名称：</strong>{{ reviewOrder.shopname }}</p>
        </div>

        <div class="review-form">
          <!-- 菜品评分 -->
          <div class="rating-section">
            <label class="rating-label">菜品评分</label>
            <el-rate v-model="reviewForm.dishScore" :colors="['#ff9900', '#ff9900', '#ff9900']" show-text />
          </div>

          <!-- 服务评分 -->
          <div class="rating-section">
            <label class="rating-label">服务评分</label>
            <el-rate v-model="reviewForm.serviceScore" :colors="['#ff9900', '#ff9900', '#ff9900']" show-text />
          </div>

          <!-- 环境评分 -->
          <div class="rating-section">
            <label class="rating-label">环境评分</label>
            <el-rate v-model="reviewForm.environmentScore" :colors="['#ff9900', '#ff9900', '#ff9900']" show-text />
          </div>

          <!-- 评价内容 -->
          <div class="content-section">
            <label class="content-label">评价内容</label>
            <el-input
              v-model="reviewForm.content"
              type="textarea"
              :rows="4"
              placeholder="请分享您的用餐体验，您的评价对其他用户很有帮助..."
              maxlength="500"
              show-word-limit
            />
          </div>
          
          <!-- 图片上传 -->
          <div class="image-section">
            <label class="image-label">上传图片（可选）</label>
            <el-upload
              action="http://localhost:8080/imgupdate/updateimage"
              list-type="picture-card"
              :limit="9"
              :on-success="handleExceed"
            >
              <el-icon><Plus /></el-icon>
            </el-upload>
          </div>
        </div>
      </div>
      
      <template #footer>
              <div class="dialog-footer">
                <el-button @click="closeReviewDialog">取消</el-button>
                <el-button type="primary" @click="submitReview" :loading="reviewLoading">提交评价</el-button>
              </div>
            </template>
          </el-dialog>
      
          <!-- 投诉弹窗 -->
          <el-dialog v-model="complaintDialogVisible" title="订单投诉" width="600px">
            <div class="complaint-dialog">
              <div class="order-info">
                <p><strong>订单号：</strong>{{ complaintOrder.orderNo }}</p>
                <p><strong>商家名称：</strong>{{ complaintOrder.shopname }}</p>
              </div>
      
              <el-form :model="complaintForm" label-width="100px">
                <el-form-item label="投诉类型">
                  <el-select v-model="complaintForm.complaintType" placeholder="请选择投诉类型">
                    <el-option label="菜品问题" :value="1" />
                    <el-option label="服务问题" :value="2" />
                    <el-option label="配送问题" :value="3" />
                    <el-option label="其他" :value="4" />
                  </el-select>
                </el-form-item>
                <el-form-item label="投诉内容">
                  <el-input
                    v-model="complaintForm.content"
                    type="textarea"
                    :rows="4"
                    placeholder="请详细描述您遇到的问题..."
                    maxlength="500"
                    show-word-limit
                  />
                </el-form-item>
              </el-form>
            </div>
      
            <template #footer>
              <div class="dialog-footer">
                <el-button @click="closeComplaintDialog">取消</el-button>
                <el-button type="primary" @click="submitComplaint" :loading="complaintLoading">提交投诉</el-button>
              </div>
            </template>
          </el-dialog>    </teleport>
    
    <!-- 空订单提示 -->
    <div class="empty-order" v-if="isOrderEmpty">
      <img src="https://img.icons8.com/fluency/96/000000/empty-box.png" alt="空订单" class="empty-icon" />
      <p class="empty-text">您还没有相关订单</p>
    </div>
    </div>
</template>

<script setup>
import { ref,onMounted, watch } from 'vue';
import orderbyuser from '../../../../api/orderbyuser';
import { Shop, Plus } from '@element-plus/icons-vue';
import { ElMessage,ElMessageBox  } from 'element-plus';
import { useRouter } from 'vue-router';
import { createOrder } from '../../../../api/order';
import OrderStatusApi from '../../../../api/OrderStatusApi';
import ReviewApi from '../../../../api/ReviewApi';
import ComplaintApi from '../../../../api/ComplaintApi';
import request from '../../../../utils/request';
let total=ref();
// 响应式数据
const activeTab = ref('all');
const isOrderEmpty = ref(true);
const isOrderHave=ref(false)
const showFilter = ref(false);
const isLoading = ref(false);
//订单详情数组
let orderitemlist=ref();
//订单详情弹窗
let dialogshow=ref(false);
let orderList=ref();

// 支付相关
const router = useRouter();
let payDialogVisible=ref(false);
let currentOrder=ref({});
let selectedPayMethod=ref('balance');
let payLoading=ref(false);

// 评价相关
let reviewDialogVisible=ref(false);
let reviewOrder=ref({});
let reviewForm=ref({
  orderId: '',
  shopId: '',
  dishScore: 5,
  serviceScore: 5,
  environmentScore: 5,
  content: '',
  imageUrls: ''
});
let reviewLoading=ref(false);

// 投诉相关
let complaintDialogVisible=ref(false);
let complaintOrder=ref({});
let complaintForm=ref({
  orderId: '',
  shopId: '',
  complaintType: null,
  content: ''
});
let complaintLoading=ref(false);
let selectwhere=ref({
  page:1,
  where:[]
})
// 方法
let Searchform=ref({
  shopname:'',
  dateRange: null,
  orderType:'',
  status:'',
  minAmount:'',
  maxAmount:''
})
// 日期格式化函数
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

// 禁用未来日期和过老日期（超过1年）
const disabledDate = (time) => {
  const now = new Date();
  const oneYearAgo = new Date(now.getTime() - 365 * 24 * 60 * 60 * 1000);
  return time.getTime() > now.getTime() || time.getTime() < oneYearAgo.getTime();
};

// 设置默认时间范围（最近30天）
const setDefaultDateRange = () => {
  const now = new Date();
  const thirtyDaysAgo = new Date(now.getTime() - 30 * 24 * 60 * 60 * 1000);
  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };
  Searchform.value.dateRange = [formatDate(thirtyDaysAgo), formatDate(now)];
};

// 快速选择今天的日期范围
const selectToday = () => {
  const now = new Date();
  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  };
  Searchform.value.dateRange = [formatDate(now), formatDate(now)];
  // 自动触发查询
  searchoder();
};
onMounted(()=>{
  // 初始加载时应用默认筛选条件
  searchoder();
})
async function selectorder(){
  try {
    console.log("selectorder被调用，查询条件：", JSON.stringify(selectwhere.value, null, 2));
    
    // 确保查询参数格式正确
    const queryParams = {
      page: selectwhere.value.page || 1,
      where: selectwhere.value.where || []
    };
    
    console.log("发送的查询参数：", JSON.stringify(queryParams, null, 2));
    
    // 直接使用已构建的筛选条件，不再重置
    let res = await orderbyuser.orderlistbyuser(queryParams);
    console.log("=== API完整响应 ===");
    console.log("API响应：", JSON.stringify(res, null, 2));
    
    // 检查响应数据结构
    if (!res) {
      console.warn("API响应为空");
      isOrderEmpty.value = true;
      isOrderHave.value = false;
      return;
    }
    
    // 检查数据结构并获取订单列表
    let orderData = [];
    let totalCount = 0;
    
    if (res.data && res.data.pageInfo) {
      orderData = res.data.pageInfo.list || [];
      totalCount = res.data.pageInfo.total || 0;
      console.log("总页数：", res.data.pageInfo.pages);
      console.log("当前页：", res.data.pageInfo.pageNum);
    } else if (res.data && Array.isArray(res.data)) {
      orderData = res.data;
      totalCount = res.data.length;
    } else if (Array.isArray(res)) {
      orderData = res;
      totalCount = res.length;
    }
    
    console.log("解析后的订单数据：", orderData);
    console.log("总记录数：", totalCount);
    
    // 统计状态分布
    const statusCount = {};
    if (orderData && orderData.length > 0) {
      orderData.forEach(order => {
        const status = order.status;
        statusCount[status] = (statusCount[status] || 0) + 1;
      });
      console.log("订单状态分布：", statusCount);
    }
    
    if (orderData && orderData.length > 0) {
      isOrderEmpty.value = false;
      isOrderHave.value = true;
      
      // 转换字段名从 snake_case 到 camelCase
      let filteredOrders = orderData.map(order => {
        return {
          id: order.id,
          orderNo: order.order_no || order.orderNo,
          shopId: order.shop_id || order.shopId,
          shopname: order.shopname || order.name,
          totalAmount: order.total_amount || order.totalAmount,
          discountAmount: order.discount_amount || order.discountAmount,
          payAmount: order.pay_amount || order.payAmount,
          payType: order.pay_type || order.payType,
          orderType: order.order_type || order.orderType,
          status: order.status,
          createTime: order.create_time || order.createTime
        };
      });
      
      // 不再需要前端筛选，因为后端已经处理了in条件
      total.value = totalCount;
      
      orderList.value = filteredOrders;
      console.log("转换后的订单列表数据：", orderList.value);
    } else {
      isOrderEmpty.value = true;
      isOrderHave.value = false;
      console.log("没有找到订单数据");
    }
  } catch (error) {
    console.error("获取订单列表失败：", error);
    isOrderEmpty.value = true;
    isOrderHave.value = false;
  }
}
// 监听activeTab变化，重新查询订单
watch(activeTab,(newTab)=>{
console.log("activeTab变化:", newTab);
// 重新构建筛选条件并查询，保持现有搜索条件
searchoder();
});
async function takeopen(id){
  try {
    dialogshow.value = true;
    let res = await orderbyuser.orderitembyorderid(id);
    console.log("根据订单id查详情", res);
    
    // 检查响应数据结构
    if (res && res.data) {
      if (res.data.orderItemList && Array.isArray(res.data.orderItemList)) {
        orderitemlist.value = res.data.orderItemList;
      } else if (Array.isArray(res.data)) {
        orderitemlist.value = res.data;
      } else {
        orderitemlist.value = [];
      }
    } else {
      orderitemlist.value = [];
    }
    
    console.log("订单详情列表：", orderitemlist.value);
  } catch (error) {
    console.error("获取订单详情失败：", error);
    orderitemlist.value = [];
    dialogshow.value = false;
  }
}

// 暴露变量给父组件
defineExpose({
activeTab,
selectorder
});
// 重置搜索表单
function resetSearchForm() {
  Searchform.value = {
    shopname: '',
    dateRange: null,
    orderType: '',
    status: '',
    minAmount: '',
    maxAmount: ''
  };
  // 重置时间范围为默认的30天
  setDefaultDateRange();
}

// 重置筛选条件并重新查询
function resetFilters() {
  console.log("重置筛选条件");
  resetSearchForm();
  activeTab.value = 'all';
  selectwhere.value = {
    page: 1,
    where: []
  };
  // 重置时设置默认时间范围（最近30天）
  setDefaultDateRange();
  // 重新加载数据
  selectorder();
}

async function searchoder() {
  try {
    isLoading.value = true;
    console.log("=== 筛选函数开始 ===");
    console.log("searchoder被调用");
    console.log("当前activeTab:", activeTab.value);
    console.log("Searchform数据：", JSON.stringify(Searchform.value));
    
    // 构建筛选条件
    const whereConditions = [];
    
    // 添加标签筛选条件
    switch(activeTab.value){
      case 'all':
        break;
      case 'pendingPay':
        whereConditions.push({column:'o.status',type:'eq',value:0});
        break;
      case 'pendingReceive':
        whereConditions.push({column:'o.status',type:'eq',value:1});
        break;
      case 'pendingReview':
        // 使用in条件筛选状态2或4
        console.log("添加in条件筛选状态2和4");
        whereConditions.push({column:'o.status',type:'in',value:'2,4'});
        console.log("whereConditions:", JSON.stringify(whereConditions));
        break;
    }
    
    // 添加商家名称筛选
    if (Searchform.value.shopname && Searchform.value.shopname.trim()) {
      whereConditions.push({
        column: 'shop.name',
        type: 'like',
        value: Searchform.value.shopname.trim()
      });
    }
    
    // 添加日期范围筛选
    
        if (Searchform.value.dateRange && Array.isArray(Searchform.value.dateRange) && Searchform.value.dateRange.length === 2) {
    
          const startDateStr = Searchform.value.dateRange[0];
    
          const endDateStr = Searchform.value.dateRange[1];
    
    
    
    
    
          console.log("日期筛选范围:", startDateStr, "到", endDateStr);
    
    
    
          // 检查日期范围是否超过1年
    
          const startDate = new Date(startDateStr);
    
          const endDate = new Date(endDateStr);
    
          const oneYearMs = 365 * 24 * 60 * 60 * 1000;
    
          if (endDate.getTime() - startDate.getTime() > oneYearMs) {
    
            ElMessage.warning('查询时间范围不能超过1年，请重新选择');
    
            return;
    
          }
    
    
    
          // 使用日期字符串进行比较
    
          whereConditions.push({
    
            column: 'o.create_time',
    
            type: 'ge',
    
            value: startDateStr + ' 00:00:00'
    
          });
    
    
    
          whereConditions.push({
    
            column: 'o.create_time',
    
            type: 'le',
    
            value: endDateStr + ' 23:59:59'
    
          });
    
        }
    
    // 更新筛选条件
    selectwhere.value.where = whereConditions;
    selectwhere.value.page = 1;
    
    console.log("最终筛选条件：", JSON.stringify(selectwhere.value));
    
    console.log("开始调用selectorder...");
    await selectorder();
    console.log("=== 筛选函数结束 ===");
  } catch (error) {
    console.error("筛选过程中发生错误:", error);
  } finally {
    isLoading.value = false;
  }
}
// 分页切换
function handlePageChange(pageno) {
  selectwhere.value.page = pageno
  selectorder()
}

// 打开支付弹窗
function openPayDialog(order) {
  currentOrder.value = order;
  selectedPayMethod.value = 'balance';
  payDialogVisible.value = true;
  console.log('打开支付弹窗，订单信息：', order);
}

// 关闭支付弹窗
function closePayDialog() {
  payDialogVisible.value = false;
  currentOrder.value = {};
  selectedPayMethod.value = 'balance';
}

// 确认支付
async function confirmPay() {
  if (!currentOrder.value.id) {
    ElMessage.error('订单信息错误');
    return;
  }

  payLoading.value = true;
  
  try {
    console.log('开始支付，订单ID：', currentOrder.value.id);
    console.log('支付方式：', selectedPayMethod.value);

    if (selectedPayMethod.value === 'alipay') {
      // 支付宝支付 - 调用后端接口生成支付表单
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          ElMessage.error('请先登录');
          return;
        }

        const payData = {
          orderId: currentOrder.value.id,
          orderNo: currentOrder.value.orderNo,
          payAmount: currentOrder.value.payAmount,
          orderType: currentOrder.value.orderType,
          token: token
        };

        // 调用后端接口生成支付宝支付
        const res = await request.post('/pay/existing-order', payData);
        
        if (res.code === 200 && res.data.alipayForm) {
          // 打开支付宝支付页面
          const newWindow = window.open('', '_blank');
          newWindow.document.write(res.data.alipayForm);
          newWindow.document.close();
          
          // 关闭支付弹窗
          closePayDialog();
          
          ElMessage.success('正在跳转到支付宝...');
          ElMessage.success('支付成功，订单状态已更新');
          // 立即刷新订单列表，显示已支付状态
          searchoder();
          
        } else {
          ElMessage.error('支付接口调用失败');
        }
      } catch (error) {
        console.error('支付宝支付失败:', error);
        ElMessage.error('支付失败：' + (error.response?.data?.message || error.message));
      }
    } else {
      // 余额支付 - 直接更新订单状态
      const updateRes = await OrderStatusApi.updateOrderStatus(currentOrder.value.id);
      
      if (updateRes.code === 200) {
        ElMessage.success('支付成功');
        closePayDialog();
        // 刷新订单列表
        searchoder();
      } else {
        ElMessage.error('支付失败：' + updateRes.message);
      }
    }
  } catch (error) {
    console.error('支付失败：', error);
    ElMessage.error('支付失败：' + (error.response?.data?.message || error.message));
  } finally {
    payLoading.value = false;
  }
}
function Cancelorder(orderId){
  ElMessageBox.confirm('确定要取消这个订单吗?', '警告', {
			confirmButtonText: '确定',
			cancelButtonText: '取消',
			type: 'warning'
		}).then(async () => {
			try {
				let rs = await orderbyuser.Cancelorder(orderId);
				if (rs.code === 200) {
					ElMessage.success(rs.data.status || '取消成功');
					selectorder(); // 重新加载数据
				} else {
					ElMessage.error(rs.message || '取消失败');
				}
			} catch (error) {
				console.error('取消地址错误:', error);
				ElMessage.error('取消失败: ' + (error.message || '未知错误'));
			}
		}).catch(() => {
			// 用户取消删除，不执行任何操作
		});
}

// 打开评价弹窗
function openReviewDialog(order) {
  reviewOrder.value = order;
  reviewForm.value = {
    orderId: order.id,
    shopId: order.shopId,
    dishScore: 5,
    serviceScore: 5,
    environmentScore: 5,
    content: '',
    images: []
  };
  reviewDialogVisible.value = true;
  console.log('打开评价弹窗，订单信息：', order);
}

// 关闭评价弹窗
function closeReviewDialog() {
  reviewDialogVisible.value = false;
  reviewOrder.value = {};
  reviewForm.value = {
    orderId: '',
    shopId: '',
    dishScore: 5,
    serviceScore: 5,
    environmentScore: 5,
    content: '',
    images: []
  };
}

// 处理图片上传成功
function handleExceed(response, file, fileList) {
    console.log('图片上传成功回调：', response);
    console.log('文件对象：', file);
    console.log('文件列表：', fileList);

    if (response && response.data && response.data.imgpath) {
        const imagePath = response.data.imgpath;
        console.log('添加图片路径：', imagePath);
        reviewForm.value.images.push(imagePath);
        console.log('当前图片数组：', reviewForm.value.images);
    } else {
        console.error('上传响应格式错误：', response);
        ElMessage.error('图片上传失败，返回数据格式错误');
    }
}

// 提交评价
async function submitReview() {
  if (!reviewForm.value.content.trim()) {
    ElMessage.warning('请填写评价内容');
    return;
  }

  reviewLoading.value = true;

  try {
    console.log('=== 开始提交评价 ===');
    console.log('原始表单数据：', JSON.stringify(reviewForm.value, null, 2));
    console.log('图片数组：', reviewForm.value.images);
    console.log('图片数组类型：', Array.isArray(reviewForm.value.images));
    console.log('图片数组长度：', reviewForm.value.images.length);

    // 处理图片数据，将字符串数组转换为逗号分隔的字符串
    // handleExceed 中存储的是 files.data.imgpath (字符串)
    const imageUrls = reviewForm.value.images
      .filter(img => img && typeof img === 'string' && img.trim())
      .join(',');

    console.log('处理后的图片URL字符串：', imageUrls);
    console.log('imageUrls 类型：', typeof imageUrls);
    console.log('imageUrls 长度：', imageUrls.length);

    // 映射到后端Evaluation实体类的字段
    const reviewData = {
      orderId: reviewForm.value.orderId,
      shopId: reviewForm.value.shopId,
      dishScore: reviewForm.value.dishScore,
      serviceScore: reviewForm.value.serviceScore,
      environmentScore: reviewForm.value.environmentScore,
      content: reviewForm.value.content,
      imageUrls: imageUrls
    };

    console.log('发送到后端的完整数据：', JSON.stringify(reviewData, null, 2));

    const res = await ReviewApi.submitReview(reviewData);

    console.log('后端返回结果：', res);

    if (res.code === 200 || res.code === 1) {
      ElMessage.success('评价提交成功');
      closeReviewDialog();
      // 刷新订单列表
      searchoder();
    } else {
      ElMessage.error(res.message || '提交失败');
    }

  } catch (error) {
    console.error('提交评价失败：', error);
    console.error('错误详情：', error.response?.data || error.message);
    ElMessage.error('提交评价失败，请稍后重试');
  } finally {
    reviewLoading.value = false;
  }
}

// 打开投诉弹窗
function openComplaintDialog(order) {
  complaintOrder.value = order;
  complaintForm.value = {
    orderId: order.id,
    shopId: order.shopId,
    complaintType: null,
    content: ''
  };
  complaintDialogVisible.value = true;
  console.log('打开投诉弹窗，订单信息：', order);
}

// 关闭投诉弹窗
function closeComplaintDialog() {
  complaintDialogVisible.value = false;
  complaintOrder.value = {};
  complaintForm.value = {
    orderId: '',
    shopId: '',
    complaintType: null,
    content: ''
  };
}

// 提交投诉
async function submitComplaint() {
  if (!complaintForm.value.complaintType) {
    ElMessage.warning('请选择投诉类型');
    return;
  }
  if (!complaintForm.value.content.trim()) {
    ElMessage.warning('请填写投诉内容');
    return;
  }
  if (complaintForm.value.content.trim().length < 5) {
    ElMessage.warning('投诉内容至少需要5个字符');
    return;
  }

  complaintLoading.value = true;
  
  try {
    console.log('提交投诉：', complaintForm.value);
    
    // 从localStorage获取用户信息
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    
    const complaintData = {
      userId: userInfo.id,
      orderId: complaintForm.value.orderId,
      shopId: complaintForm.value.shopId,
      complaintType: complaintForm.value.complaintType,
      content: complaintForm.value.content.trim(),
      status: 0 // 待处理
    };
    
    console.log('发送到后端的数据：', complaintData);
    
    const res = await ComplaintApi.addComplaint(complaintData);
    
    if (res.code === 200 || res.code === 1) {
      ElMessage.success('投诉提交成功');
      closeComplaintDialog();
    } else {
      ElMessage.error(res.message || '提交失败');
    }
    
  } catch (error) {
    console.error('提交投诉失败：', error);
    ElMessage.error('提交投诉失败，请稍后重试');
  } finally {
    complaintLoading.value = false;
  }
}
</script>

<style scoped>
/* 订单组件样式 */
.all-orders {
  width: 100%;
  height: 100%;
}
.search-bar {
  display: flex;
  align-items: center;
  padding: 15px 10px;
  border-bottom: 1px solid #f5f5f5;
}
.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #eee;
  border-radius: 20px;
  font-size: 14px;
}
.filter-btn, .invoice-btn {
  background: none;
  border: none;
  margin-left: 10px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}
.order-tabs {
  display: flex;
  overflow-x: auto;
  padding: 10px 0;
  border-bottom: 1px solid #f5f5f5;
}
.tab-item {
  flex-shrink: 0;
  padding: 8px 15px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}
.tab-item.active {
  color: #d9251c;
  border-bottom: 2px solid #d9251c;
  font-weight: 600;
}
.empty-order {
  text-align: center;
  padding: 50px 20px;
}
.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: 20px;
}
.empty-text {
  font-size: 16px;
  color: #333;
  margin-bottom: 10px;
}
.view-all-link {
  font-size: 14px;
  color: #409eff;
  text-decoration: none;
  display: block;
  margin-bottom: 30px;
}
.goto-group-btn {
  background-color: #ffd100;
  color: #333;
  border: none;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
}
.filter-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-end;
  z-index: 999;
}
.filter-content {
  width: 100%;
  background-color: #fff;
  border-top-left-radius: 15px;
  border-top-right-radius: 15px;
  padding: 20px;
}
.filter-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 15px;
  color: #333;
}
.time-btns, .service-btns {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
.time-btn, .service-btn {
  padding: 8px 15px;
  border: 1px solid #eee;
  border-radius: 20px;
  background-color: #fff;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}
.custom-time {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.time-sep {
  font-size: 14px;
  color: #999;
}
.modal-btns {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
}
.clear-btn {
  padding: 8px 20px;
  border: 1px solid #eee;
  border-radius: 20px;
  background-color: #fff;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}
.confirm-btn {
  padding: 8px 20px;
  border: none;
  border-radius: 20px;
  background-color: #ffd100;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}
.feedback-btn {
  position: relative;
  top: -40px;
  right: -10px;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  background-color: #333;
  color: #fff;
  font-size: 12px;
  cursor: pointer;
}
.have-order {
  text-align: center;
  padding: 50px 20px;
  overflow: auto;
}
:deep(.theme-pagination .el-pager li.active) {
  color: #fff !important;
  background-color: #e54d42 !important;
  font-weight: bold;
}

:deep(.theme-pagination .el-pager li.is-active) {
  color: #fff !important;
  background-color: #e54d42 !important;
}

.theme-pagination button:hover {
  color: #e54d42 !important;
}
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-bottom: 20px;
}

/* 支付弹窗样式 */
.payment-dialog {
  padding: 10px 0;
}

.order-info {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.order-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #666;
}

.order-info .amount {
  color: #e54d42;
  font-size: 18px;
  font-weight: bold;
}

.payment-methods {
  margin: 20px 0;
}

.payment-option {
  width: 100%;
  margin-bottom: 15px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s;
}

.payment-option:hover {
  border-color: #e54d42;
  box-shadow: 0 2px 8px rgba(229, 77, 66, 0.1);
}

.payment-option.is-checked {
  border-color: #e54d42;
  background: #fef5f5;
}

.payment-option-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.payment-icon {
  font-size: 24px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.alipay-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.payment-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.payment-name {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.payment-desc {
  font-size: 12px;
  color: #909399;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 评价弹窗样式 */
.review-dialog {
  padding: 10px 0;
}

.review-form {
  margin-top: 20px;
}

.rating-section {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.rating-label {
  width: 100px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.content-section {
  margin-bottom: 20px;
}

.content-label {
  display: block;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-bottom: 10px;
}

.image-section {
  margin-bottom: 20px;
}

.image-label {
  display: block;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-bottom: 10px;
}

:deep(.el-rate__text) {
  font-size: 12px;
  color: #909399;
}

:deep(.el-upload--picture-card) {
  width: 100px;
  height: 100px;
}

:deep(.el-upload-list--picture-card .el-upload-list__item) {
  width: 100px;
  height: 100px;
}

/* 投诉弹窗样式 */
.complaint-dialog {
  padding: 10px 0;
}

.complaint-dialog .order-info {
  background: #fff0f0;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  border-left: 4px solid #e54d42;
}

</style>