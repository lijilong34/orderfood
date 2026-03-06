<template>
  <div class="order-list">
      <el-form-item label="订单号">
        <el-input v-model="orderNumber" placeholder="订单号"/>
      </el-form-item>
      <el-form-item label="收货人">
        <el-input v-model="consignees" placeholder="收货人"/>
      </el-form-item>
      <el-form-item label="用户手机号">
        <el-input v-model="phoneNumber" placeholder="用户手机号"/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="onSubmit">查询</el-button>
      </el-form-item>
     <!-- 订单表格 -->
    <el-table :data="orderForm" border style="width: 100%" :row-class-name="getRowClassName">
    <el-table-column label="订单号" prop="orderNo" width="120"/>
    <el-table-column label="用户昵称" prop="nickname" width="150"/>
     <el-table-column label="实付金额" prop="payAmount" width="90"/>
    <el-table-column label="下单时间" width="150">
      <template #default="scope">
        {{ dayjs(scope.row.createTime).format('YYYY-MM-DD HH:mm:ss') }}
      </template>
    </el-table-column>
    <el-table-column label="订单类型"  width="90">
      <template #default="scope">
        <el-tag  size="large" v-if="scope.row.orderType==1">
          堂食
        </el-tag>
        <el-tag size="large" v-if="scope.row.orderType==2">
          外卖
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column label="收货人信息"  width="150">
     <template #default="scope">
      <span  v-if="scope.row.orderType==1">
       桌号:{{scope.row.tableNo}}
      </span>
       <span  v-if="scope.row.orderType==2">
       姓名:{{scope.row.receiver}},手机号:{{scope.row.phone}},收货地址:{{scope.row.address}}
      </span>
     </template>
    </el-table-column>
    <el-table-column label="状态"  width="90">
     <template #default="scope">
        <el-tag type="danger" size="large" v-if="scope.row.status==0">
          待支付
        </el-tag>
        <el-tag size="large" v-if="scope.row.status==1">
          待制作
        </el-tag>
        <el-tag type="success" size="large"  v-if="scope.row.status==2">
          已完成
        </el-tag>
        <el-tag type="danger" size="large"  v-if="scope.row.status==3">
          已取消
        </el-tag>
      </template>
    </el-table-column>
    <el-table-column prop="remark" label="备注"  width="90"/>
    <el-table-column label="操作"  width="150">
      <template #default="scope">
        <el-button size="small" @click="handleinquire(scope.row.id)">
          查看
        </el-button>
        <el-button type="primary" size="small" @click="handleEdit(scope.row.id)" >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>
  </div>
  <div class="demo-pagination-block">
  <el-pagination
      :page-size="8"
      :background="background"
      layout="total, prev, pager, next, jumper"
      :total="total"
      @current-change="handleCurrentChange"
    />
  </div>
  <!-- 查看 的弹出框 -->
  <teleport to="body">
    <el-dialog v-model="isDialogVisible" title="订单详情" width="500">
      <div v-for="order in currentOrder">
        <h3>商品名称：{{order.productName }}</h3>
        <h3>单价：{{order.price}}</h3>
        <h3>数量：{{order.quantity}}</h3>
        <h3>总价：{{ order.amount}}</h3>
        <h3>备注：{{order.remark}}</h3>
      </div>
    </el-dialog>
  </teleport>
</template>

<script setup>
import { reactive, ref , onMounted} from 'vue'
import OrderApi from '../../../../api/OrderApi'
import orderInfo from '../../../../api/orderbyuser'
import { ElDialog } from 'element-plus'; // 引入 el-dialog 组件
import dayjs from 'dayjs' // 导入dayjs用于日期格式化


const background = ref(true); // 分页背景
const size = ref('default'); // 分页尺寸
const disabled = ref(false); // 分页禁用状态
import { ElMessage } from 'element-plus'; // 引入消息提示（用于数据校验）

let editInfo = []; // 存储编辑的订单信息
const total = ref(); // 总条数

const allOrders = ref([]); // 存储所有订单数据
const orderForm = ref([]); // 存储当前显示的订单数据
let shopid=localStorage.getItem("shopid");

let selectwhere=ref({
  page:1,
  where:[{
   column:"`order`.shop_id",
   type:"eq",
   value:shopid
  }]
})

// 控制弹出框的显示状态
const isDialogVisible = ref(false);
// 当前查看的订单数据
const currentOrder = ref(null);


onMounted(() => {
  getOrderList(); // 组件挂载后加载数据
});

// 获取订单列表
const getOrderList = async () => {
  try {
    let res = await orderInfo.orderInfoHistory(selectwhere.value);
    console.log('历史订单API响应:', res);
    
    if(res.data && res.data.pageInfo) {
      orderForm.value = res.data.pageInfo.list;
      // 如果没有返回total字段，使用数组长度作为总记录数
      total.value = res.data.pageInfo.total || res.data.pageInfo.list.length;
      
      console.log('总记录数:', total.value);
      console.log('当前页数据:', orderForm.value.length);
    } else {
      console.error('API响应格式错误:', res);
      orderForm.value = [];
      total.value = 0;
    }
  } catch (error) {
    console.error('获取历史订单列表失败:', error);
    ElMessage.error('获取历史订单列表失败');
    allOrders.value = [];
    orderForm.value = [];
    total.value = 0;
  }
}

// 定义查询条件
const orderNumber = ref('');
const consignees = ref('');
const phoneNumber = ref('');
const orderStatus = ref('');

// 查询 
const onSubmit = async () => {
  // 重置查询条件
  selectwhere.value.where = [{
    column: "`order`.shop_id",
    type: "eq",
    value: shopid
  }];
  
  // 添加订单号筛选
  if (orderNumber.value && orderNumber.value.trim()) {
    selectwhere.value.where.push({
      column: "order.order_no",
      type: "like",
      value: orderNumber.value.trim()
    });
  }
  
  // 添加收货人筛选
  if (consignees.value && consignees.value.trim()) {
    selectwhere.value.where.push({
      column: "order.receiver",
      type: "like",
      value: consignees.value.trim()
    });
  }
  
  // 添加手机号筛选
  if (phoneNumber.value && phoneNumber.value.trim()) {
    selectwhere.value.where.push({
      column: "order.phone",
      type: "like",
      value: phoneNumber.value.trim()
    });
  }
  
  // 重置页码
  selectwhere.value.page = 1;
  
  getOrderList();
};

// 分页处理
const handleSizeChange = () => {
  getOrderList();
}
const handleCurrentChange = (pageno) => {
  selectwhere.value.page=pageno;
  getOrderList();
}
// 背景色处理
const getRowClassName = ({ row, rowIndex }) => {
  if (rowIndex%2==0) {
    return 'even-row';
  }
  return 'odd-row';
};
// 删除
const handleEdit = async (id) => {
  try {
    const res = await OrderApi.deleteOrder(id);
    if (res.code === 1) {
      ElMessage.success(res.msg || '订单删除成功');
      getOrderList(); // 重新加载数据
    } else {
      ElMessage.error(res.msg || '订单删除失败');
    }
  } catch (error) {
    console.error('删除订单失败:', error);
    ElMessage.error('删除订单失败: ' + error.message);
  }
};
// 查看
const handleinquire = async(id) => {
  let res = await orderInfo.orderitembyorderid(id);
  currentOrder.value = res.data.orderItemList;
  console.log("订单详细信息",res)
  isDialogVisible.value = true;
}
</script>

<style lang="scss" scoped>
.order-list{
  width: 100%;
  // height: 100%;
  box-sizing: border-box;
  padding: 15px;
  display: flex;
  flex-wrap: wrap;
}
:deep(.el-form-item){
  margin-right: 10px;
}
:deep(.el-select__wrapper){
  width: 200px;
}
.demo-pagination-block{
  margin: 15px 0 0 0;
  padding-left: 15px;
  display: flex;
  justify-content: center;
}
:deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'Microsoft YaHei', sans-serif;
  font-size: 14px;
}
:deep(.el-pagination__total) {
  margin-right: 12px;
  color: #333;
}
:deep(.el-pagination__sizes) {
  margin-right: 12px;
}
:deep(.el-pagination__sizes .el-select .el-input) {
  width: 100px;
}
:deep(.el-pagination__sizes .el-select .el-input__wrapper) {
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
}
:deep(.el-pagination__sizes .el-select .el-input__wrapper:hover) {
  border-color: #1890ff;
}
:deep(.el-pagination__sizes .el-select .el-input__wrapper.is-focus) {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}
:deep(.el-pagination__jump) {
  margin-left: 12px;
  color: #333;
}
:deep(.el-pagination__jump .el-input) {
  width: 40px;
  margin: 0 4px;
}
:deep(.el-pagination__jump .el-input__wrapper) {
  padding: 4px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  text-align: center;
}
:deep(.el-pagination__jump .el-input__wrapper:hover) {
  border-color: #1890ff;
}
:deep(.el-pagination__jump .el-input__wrapper.is-focus) {
  border-color: #1890ff;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}
:deep(.el-pagination__jump .el-pagination__editor) {
  text-align: center;
}
:deep(.el-pager li) {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  margin: 0 4px;
  padding: 0 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  color: #333;
  font-size: 14px;
}
:deep(.el-pager li:hover) {
  color: #1890ff;
  border-color: #1890ff;
}
:deep(.el-pager li.is-active) {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
  font-weight: bold;
}
:deep(.el-pager li.is-active:hover) {
  background-color: #40a9ff;
  border-color: #40a9ff;
}
:deep(.btn-prev),
:deep(.btn-next) {
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  margin: 0 4px;
  padding: 0 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background: #fff;
  color: #333;
  font-size: 14px;
}
:deep(.btn-prev):hover,
:deep(.btn-next):hover {
  color: #1890ff;
  border-color: #1890ff;
}
:deep(.btn-prev.is-disabled),
:deep(.btn-next.is-disabled) {
  color: #c0c4cc;
  border-color: #e4e7ed;
  background-color: #f5f7fa;
  cursor: not-allowed;
}
:deep(.btn-prev.is-disabled:hover),
:deep(.btn-next.is-disabled:hover) {
  color: #c0c4cc;
  border-color: #e4e7ed;
}
// 表格背景颜色
:deep(.even-row){
  background-color: #f4f4f4;
}
:deep(.odd-row){
  background-color: #cacaca;
}
</style>
