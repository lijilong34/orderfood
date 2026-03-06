<template>
  <div class="pc-wrap">
    <!-- 顶部导航栏（红色主色调） -->
    <div class="nav-bar">
      <div class="nav-title">御&nbsp;膳&nbsp;房</div>
      <div class="nav">
        <el-menu 
          :default-active="activeIndex" 
          class="el-menu-demo" 
          mode="horizontal" 
          :ellipsis="false" 
          @select="handleSelect"
          background-color="#e54d42" 
          text-color="#fff" 
          active-text-color="#ffd700"
        >
          <el-menu-item index="1">首页</el-menu-item>
          <el-menu-item index="2">所有商家</el-menu-item>
          <el-menu-item index="3">所有分类</el-menu-item>
                    <el-menu-item index="4">所有商品</el-menu-item>
                    <el-menu-item index="5" v-if="isLogin">购物车</el-menu-item>
                    <el-menu-item index="6" v-if="isLogin">我的</el-menu-item>       
                   </el-menu>
      </div>
      <div class="nav-right">
          <div class="auth-btns" v-if="!isLogin">
             <el-button @click="login" class="gold-btn">登录</el-button>
             <el-button @click="register" class="gold-btn">注册</el-button>
           </div>
             <div class="user-info" v-else>
          <img 
            class="user-avatar" 
            :src="'http://localhost:8080/imeageserver/'+avatar" 
            alt="用户头像"
          />
          <span class="user-name-text" v-if="isLogin">{{phone }}</span>
          <el-button 
            type="text" 
            class="logout-btn" 
            @click="logout"
          >
            退出登录
          </el-button>
        </div>
      </div>
	  </div>

     <!-- 搜索导入trending.vue容器-->
    <router-view></router-view>
  
    <!-- 页脚（红色边框顶部） -->
    <div class="footer">
      
      <!-- 链接区 -->
      <div class="footer-links">
        <router-link to="/home/selectall">首页</router-link> |
        <router-link to="/home/trending">所有商家</router-link> |
        <router-link to="/home/AllCategory">所有分类</router-link> |
        <router-link to="/home/Product">所有商品</router-link> |
        <router-link to="/home/cart">购物车</router-link> |
        <router-link to="/cz">个人中心</router-link> |
        <router-link to="/addshopbyshop">商家入驻</router-link> |
        <router-link to="/login">登录</router-link> |
        <router-link to="/register">注册</router-link> |
        <router-link to="/adminlogin">管理员登录</router-link>
      </div>

      <!-- 备案信息区 -->
      <div class="footer-records">
        <span>粤网文 (2025) 1226-111号</span> |
        <span>网络经营许可证 1910564号</span> |
        <span>增值电信业务 粤B2-20060339</span> |
        <span>粤ICP备09017694号</span>
      </div>

      <div class="footer-records">
        <span>粤公网安备 44030002000001号</span> |
        <span>互联网宗教信息服务许可证 粤 (2025) 0000021</span> |
        <span>营业执照</span>
      </div>

      <!-- 举报与客服区 -->
      <div class="footer-report">
        <span>互联网不良信息举报中心</span>
        <span>御膳房不良信息举报邮箱：kg_jb_music@tencentmusic.com</span>
        <span>客服电话：020-22043742</span>
      </div>

      <!-- 版权区 -->
      <div class="footer-copyright">
        <span>© 2025 智能点餐平台</span>
        <span>Copyright © 2004-2026 yushanfangorderfood-Inc.All Rights Reserved</span>
      </div>
    </div>

    <!-- 聊天按钮固定定位 -->
    <div class="chat-button-wrapper" v-show="!dialogVisible">
      <el-affix position="bottom"  :offset="50">
        <el-button type="primary" class="chat-button" @click="openChatDialog"><el-icon><ChatDotSquare /></el-icon></el-button>
      </el-affix>
    </div>

    <!-- 聊天对话框 -->
    <teleport to="body">
      <el-dialog
        v-model="dialogVisible"
        title="营养大师"
        width="400px"
        :close-on-click-modal="true"
        :modal="false"
        :destroy-on-close="false"
        class="chat-dialog custom-dialog"
        :style="{
          position: 'fixed',
          right: '20px',
          bottom: '50px',
          top: '100px',
          left: 'auto',
          margin: '0',
          height: 'auto',
          minHeight: '600px',
          zIndex: '2006'
        }"
      >
        <!-- 消息列表 -->
        <div class="message-container">
          <div 
            v-for="(message, index) in messages" 
            :key="index"
            :class="['message-item', message.sender === 'bot' ? 'bot-message' : 'user-message']"
          >
            <div class="message-content" v-html="message.content">
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="input-container">
          <el-upload ref="uploadRefup" action="http://localhost:8080/imgupdate/updateimage" :show-file-list="false" :on-success="handleAvatarSuccessup" :limit="1" accept=".jpg,.png">
          <el-button type="text" class="add-btn">
          <el-icon><Plus /></el-icon>
          </el-button>
          </el-upload>
          <el-input
            v-model="inputMessage"
            placeholder="输入消息..."
            class="message-input"
            @change="changetext"
            @keyup.enter.prevent="sendMessage"
            :disabled="isRecording"
          ></el-input>
          <el-button type="primary" @click="handleSendButtonClick()" class="send-btn" :loading="isshare" :disabled="isshare">
            <span v-if="inputMessage">发送</span>
            <el-icon v-else-if="isRecording"><Mute /></el-icon>
            <el-icon v-else><Microphone /></el-icon>
          </el-button>
        </div>
      </el-dialog>
    </teleport>
  </div>
  <teleport to="body">
    <el-dialog title="御膳房公告" v-model="dialogTableVisible" :before-close="closebefore" width="60%">
      <div v-if="announcements.length > 0">
        <div v-for="(announcement, index) in announcements" :key="announcement.id" style="line-height: 1.6; font-size: 14px; margin-bottom: 30px;">
          <h3 style="color: #e54d42; font-size: 18px; margin-bottom: 10px;">{{ announcement.title }}</h3>
          <div style="white-space: pre-wrap;">{{ announcement.content }}</div>
          <div style="margin-top: 15px; color: #999; font-size: 12px;">
            <span>发布人: {{ announcement.publisher }}</span>
            <span style="margin-left: 20px;">发布时间: {{ announcement.publishTime || announcement.publish_time }}</span>
          </div>
          <el-divider v-if="index < announcements.length - 1"></el-divider>
        </div>
      </div>
      <div v-else style="text-align: center; color: #999; padding: 30px;">
        暂无公告信息
      </div>
    </el-dialog>
  </teleport>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { ChatDotSquare, Plus, Position, Microphone, Mute } from '@element-plus/icons-vue'
import selectbyloginbefore from '../../../api/selectbyloginbefore'
import AnnouncementApi from '../../../api/AnnouncementApi'
const router = useRouter()
const activeIndex = ref('1')
const phone = ref() // 存储登录用户信息
const isLogin = ref(false) // 标记是否登录
const avatar=ref();//头像
const dialogTableVisible=ref(false);//系统公告弹窗
const announcements = ref([]);//公告列表
const isshare=ref(false)
const sending = ref(false) // 防止重复发送的标志位
const originalMessage = ref('') // 保存原始消息供递归调用使用

function closebefore(){
  localStorage.setItem('isLoad','1');
  dialogTableVisible.value = false;
}

// 聊天相关状态
const dialogVisible = ref(false)
const messages = ref([
  {
    sender: 'bot',
    content: '你好，我是你的智能营养大师glm-4.6v-flash需要什么服务吗'
  }
])
const inputMessage = ref('')
const sharetext=ref('<el-icon><Microphone /></el-icon>')
const isRecording = ref(false)
const conversationId = ref('default')
const toolResults = ref('')

// 登录/注册跳转（保持原逻辑）
function login() {
  router.push('/login')
}
function register() {
  router.push('/register')
}

// 导航栏选择逻辑（补充完整分类跳转）
function handleSelect(key){
  switch(key){
    case "1":
      router.push('/home/selectall')
      return;
    case "2":
      router.push('/home/trending') // 热门商家（需自行配置）
      return;
    case "3":
      router.push('/home/AllCategory') // 所有分类路由（需自行配置）
      return;
    case "4":
      router.push('/home/Product') // 商品列表路由（需自行配置）
      return;
    case "5":
      router.push('/home/cart')
      return;
    case "6":
      router.push('/cz') // 充值页面路由（需自行配置）
      return;
  }
}

function logout() {
  // 清除本地存储的用户信息
  localStorage.clear();
  // 重置状态
  isLogin.value = false;
  // 提示退出成功
  ElMessage.success('退出登录成功');
  // 跳转到首页
  router.push('/home/selectall');
  // 重置导航激活状态为首页
  activeIndex.value = '1';
}

onMounted(()=>{
  router.push('/home/selectall')
  checklogin();
  loadAnnouncements();
  let isload=localStorage.getItem('isLoad')
  if(isload==null){
  dialogTableVisible.value=true;
  }
})
//检查用户是否登录的方法
function checklogin(){
let res=localStorage.getItem("phone");
let avatar1=localStorage.getItem("avatar");
if(res!=null){
  avatar.value=avatar1;
  phone.value=res;
  isLogin.value=true;
}
}

// 从数据库加载公告列表
function loadAnnouncements() {
  AnnouncementApi.AnnouncementSelect({
    page: 1,
    where: [
      { column: "status", type: "eq", value: 1 }
    ]
  }).then(res => {
    if (res.data && res.data.pageInfo && res.data.pageInfo.list) {
      announcements.value = res.data.pageInfo.list;
    } else if (res.data && Array.isArray(res.data)) {
      announcements.value = res.data;
    }
  }).catch(err => {
    console.error('获取公告列表失败:', err);
  });
}

// 图片路径处理（保持原逻辑）
function getImg(src) {
  return new URL(src.replace('@', '/src'), import.meta.url).href
}

// 聊天功能
function openChatDialog() {
  dialogVisible.value = true
}

// 语音识别功能
let recognition = null
let speechRetryCount = 0
const MAX_SPEECH_RETRY = 3

// 按钮点击处理
function handleSendButtonClick() {
  console.log('=== 按钮点击 ===', 'inputMessage.value:', inputMessage.value, 'isshare:', isshare.value, 'sending:', sending.value)
  
  if (inputMessage.value && inputMessage.value.trim()) {
    sendMessage()
  } else {
    toggleSpeech()
  }
}

function toggleSpeech() {
  // 检查浏览器是否支持语音识别
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  if (!SpeechRecognition) {
    ElMessage.error('您的浏览器不支持语音识别功能')
    return
  }
  
  if (isRecording.value) {
    // 停止录音
    if (recognition) {
      recognition.stop()
    }
    isRecording.value = false
    speechRetryCount = 0
    return
  }
  
  // 开始录音
  speechRetryCount = 0
  startRecognition()
}

function startRecognition() {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition
  recognition = new SpeechRecognition()
  recognition.lang = 'zh-CN'
  recognition.interimResults = false
  recognition.maxAlternatives = 1
  recognition.continuous = false
  
  recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript
    inputMessage.value = transcript
    changetext()
    isRecording.value = false
    speechRetryCount = 0
    // 自动发送
    sendMessage()
  }
  
  recognition.onerror = (event) => {
    console.error('语音识别错误:', event.error)
    isRecording.value = false

    // 根据错误类型提供更友好的提示
    let errorMessage = '语音识别失败'
    let shouldRetry = false

    switch (event.error) {
      case 'network':
        errorMessage = '网络连接失败'
        shouldRetry = speechRetryCount < MAX_SPEECH_RETRY
        if (shouldRetry) {
          errorMessage += `，正在重试 (${speechRetryCount + 1}/${MAX_SPEECH_RETRY})...`
        } else {
          errorMessage += '，请检查网络连接后重试'
        }
        break
      case 'no-speech':
        errorMessage = '未检测到语音，请重新尝试'
        shouldRetry = speechRetryCount < MAX_SPEECH_RETRY
        break
      case 'audio-capture':
        errorMessage = '无法访问麦克风，请检查麦克风权限'
        break
      case 'not-allowed':
        errorMessage = '麦克风权限被拒绝，请在浏览器设置中允许麦克风访问'
        break
      case 'aborted':
        errorMessage = '语音识别已取消'
        break
      default:
        errorMessage = `语音识别失败: ${event.error}`
        shouldRetry = speechRetryCount < MAX_SPEECH_RETRY
    }

    ElMessage.error(errorMessage)

    // 如果是网络错误且未达到最大重试次数，自动重试
    if (shouldRetry && (event.error === 'network' || event.error === 'no-speech')) {
      speechRetryCount++
      console.log(`语音识别重试 ${speechRetryCount}/${MAX_SPEECH_RETRY}`)
      setTimeout(() => {
        if (!isRecording.value) {
          isRecording.value = true
          startRecognition()
        }
      }, 1000)
    }
  }
  
  recognition.onend = () => {
    isRecording.value = false
  }
  
  isRecording.value = true
  try {
    recognition.start()
  } catch (error) {
    console.error('启动语音识别失败:', error)
    isRecording.value = false
    ElMessage.error('启动语音识别失败，请刷新页面后重试')
  }
}
let imgurlname=ref();
function sendMessage(isContinuation = false) {
  // 检查isContinuation是否被错误地传递为事件对象
  if (isContinuation && typeof isContinuation === 'object' && isContinuation instanceof KeyboardEvent) {
    console.log('=== 检测到事件对象，修正为false ===')
    isContinuation = false
  }
  
  console.log('=== sendMessage调用 ===', 'isContinuation:', isContinuation, 'inputMessage.value:', inputMessage.value, 'toolResults.value:', toolResults.value, 'originalMessage.value:', originalMessage.value)
  
  // 如果不是继续对话（递归调用），检查输入和发送状态
  if (!isContinuation) {
    if (!inputMessage.value.trim()) {
      console.log('输入为空，直接返回')
      return
    }
    if (sending.value) {
      console.log('正在发送中，忽略重复调用')
      return
    }
    if (isshare.value) {
      ElMessage.error('ai正在加载回复中，请勿重复发送。')
      return
    }
  }

  // 设置发送状态
  if (!isContinuation) {
    sending.value = true
  }

  // 【关键】在清空输入框之前，立即保存消息内容
  const message = inputMessage.value
  console.log('=== 保存message ===', message)
  
  // 保存原始消息（只在第一次调用时保存）
  if (!isContinuation) {
    originalMessage.value = message
      }
    
      // 立即添加用户消息到聊天界面
      if (!isContinuation && !toolResults.value) {
        messages.value.push({
          sender: 'user',
          content: message
        })
      }
    
      if (!isContinuation) {
        isshare.value = true
        sharetext.value = "思考中"
      }
    
      // 清空输入框（只在第一次调用时清空）
      let imgurl = imgurlname.value
      if (!isContinuation) {
        console.log('=== 清空输入框 ===', 'inputMessage.value:', inputMessage.value)
        inputMessage.value = ''
      }
    
      // 创建AI回复消息（用于流式输出）
      const botMessageIndex = messages.value.length
      messages.value.push({
        sender: 'bot',
        content: '',
        isStreaming: true,
        thinking: true
      })
    
      // 使用流式输出接口
        const eventSource = selectbyloginbefore.takeaiStream({
          message: isContinuation ? '' : message,
          imgone: imgurl,
          conversationId: conversationId.value || 'default'
        }, (data) => {
          // 处理流式消息
          if (data.type === 'start') {          // 开始处理
          messages.value[botMessageIndex].thinking = true
          messages.value[botMessageIndex].content = data.data
        } else if (data.type === 'thinking') {
          // 正在思考/执行
          messages.value[botMessageIndex].thinking = true
          messages.value[botMessageIndex].content = data.data
        } else if (data.type === 'content') {
          // 收到文本内容，追加显示
          messages.value[botMessageIndex].thinking = false
          messages.value[botMessageIndex].content += data.data
        }
      }, (error) => {
        // 错误处理
        console.error('流式连接错误:', error)
        messages.value[botMessageIndex].thinking = false
        messages.value[botMessageIndex].content = '抱歉，处理过程中出现错误：' + (error.data || error.message || '未知错误')
        isshare.value = false
        sharetext.value = '发送'
        sending.value = false
      }, (complete) => {
        // 完成
        console.log('流式处理完成:', complete)
        messages.value[botMessageIndex].thinking = false
        messages.value[botMessageIndex].isStreaming = false
        isshare.value = false
        sharetext.value = '发送'
        sending.value = false
      })
}

function handleAvatarSuccessup(imgSuccess){
   let imgname=imgSuccess.data.imgpath;
   messages.value.push({
    sender: 'user',
    content: "<img src='http://localhost:8080/imeageserver/"+imgname+"' style='max-width: 200px; max-height: 200px; width: auto; height: auto; border-radius: 8px; margin-top: 5px; display: block;' />"
   })
   messages.value.push({
    sender: 'bot',
    content: "系统:图片上传成功了，请问你有什么要问营养大师的可以在下方输入框问。"
   })
   imgurlname.value=imgname;
}
function changetext(){
  if(inputMessage.value!=null){
      sharetext.value='发送'
  }else{
     sharetext.value='<el-icon><Microphone /></el-icon>'
  }
}

// 执行AI工具调用
function executeToolCall(toolCall) {
  return new Promise((resolve) => {
    const functionName = toolCall.function.name
    let args = {}

    try {
      args = JSON.parse(toolCall.function.arguments)
    } catch (e) {
      console.error('解析工具参数失败:', e)
      resolve({ status: 'error', message: '解析工具参数失败' })
      return
    }

    console.log('执行工具:', functionName, '参数:', args)

    switch (functionName) {
      case 'navigate_to_page':
        // 跳转页面
        if (args.path) {
          console.log('跳转到页面:', args.path)
          router.push(args.path)
          ElMessage.success('正在为您跳转页面...')
          resolve({ status: 'success', message: `已跳转到页面: ${args.path}` })
        } else {
          resolve({ status: 'error', message: '缺少path参数' })
        }
        break

      case 'search_product':
        // 搜索商品
        console.log('搜索商品:', args)
        // 调用商品搜索API
        import('../../../api/productApi').then(productApi => {
          productApi.ProductSelect({
            productName: args.keyword || '',
            minPrice: args.minPrice || 0,
            maxPrice: args.maxPrice || 999999,
            sortBy: args.sortBy || 'price_asc'
          })
            .then(res => {
              console.log('搜索商品成功:', res)
              const productList = res.data?.productList || res.data || []
              if (productList.length > 0) {
                // 返回第一个商品的信息
                const firstProduct = productList[0]
                ElMessage.success(`找到${productList.length}个商品，最便宜的是${firstProduct.productName}，价格¥${firstProduct.price}`)
                resolve({
                  status: 'success',
                  message: `找到${productList.length}个商品`,
                  productList: productList,
                  firstProduct: {
                    id: firstProduct.id,
                    name: firstProduct.productName,
                    price: firstProduct.price
                  }
                })
              } else {
                ElMessage.warning('未找到相关商品')
                resolve({
                  status: 'error',
                  message: '未找到相关商品'
                })
              }
            })
            .catch(error => {
              console.error('搜索商品失败:', error)
              ElMessage.error('搜索商品失败')
              resolve({ status: 'error', message: '搜索商品失败' })
            })
        })
        break

      case 'add_to_cart':
        // 添加到购物车
        if (args.productId) {
          const quantity = args.quantity || 1
          console.log('添加商品到购物车:', args.productId, '数量:', quantity)

          import('../../../api/ShopcartApi').then(ShopcartApi => {
            ShopcartApi.default.ShopCartAdd({
              productId: args.productId,
              quantity: quantity
            })
              .then(res => {
                console.log('添加到购物车成功:', res)
                ElMessage.success('已为您添加到购物车')
                resolve({ status: 'success', message: '已添加到购物车' })
              })
              .catch(error => {
                console.error('添加到购物车失败:', error)
                ElMessage.error('添加到购物车失败')
                resolve({ status: 'error', message: '添加到购物车失败' })
              })
          })
        } else {
          resolve({ status: 'error', message: '缺少productId参数' })
        }
        break

      case 'create_order':
        // 创建订单 - 先跳转到结算页，再创建订单
        console.log('创建订单，跳转到结算页面')
        router.push('/home/Settlement')
        // 延迟一下，等待页面加载后自动创建订单
        setTimeout(() => {
          resolve({ status: 'success', message: '已跳转到结算页面，正在创建订单' })
        }, 500)
        break

      case 'pay_with_balance':
        // 使用余额支付 - 如果在结算页面，点击余额支付按钮
        if (args.orderId) {
          console.log('使用余额支付订单:', args.orderId)
          // 尝试点击余额支付按钮
          const payButtons = document.querySelectorAll('button')
          let paid = false
          for (let btn of payButtons) {
            if (btn.textContent.includes('余额') || btn.textContent.includes('确认支付')) {
              btn.click()
              ElMessage.success('正在为您使用余额支付...')
              resolve({ status: 'success', message: '正在使用余额支付' })
              paid = true
              break
            }
          }
          if (!paid) {
            // 如果没找到按钮，尝试调用支付API
            ElMessage.success('正在为您使用余额支付...')
            resolve({ status: 'success', message: '正在使用余额支付' })
          }
        } else {
          resolve({ status: 'error', message: '缺少orderId参数' })
        }
        break

      case 'click_button':
        // 点击按钮
        if (args.buttonText) {
          console.log('点击按钮:', args.buttonText)
          const buttons = document.querySelectorAll('button')
          let clicked = false
          for (let btn of buttons) {
            if (btn.textContent.includes(args.buttonText)) {
              btn.click()
              ElMessage.success('已为您点击按钮')
              resolve({ status: 'success', message: `已点击按钮: ${args.buttonText}` })
              clicked = true
              break
            }
          }
          if (!clicked) {
            resolve({ status: 'error', message: `未找到按钮: ${args.buttonText}` })
          }
        } else if (args.selector) {
          console.log('点击元素:', args.selector)
          const element = document.querySelector(args.selector)
          if (element) {
            element.click()
            ElMessage.success('已为您点击元素')
            resolve({ status: 'success', message: `已点击元素: ${args.selector}` })
          } else {
            resolve({ status: 'error', message: `未找到元素: ${args.selector}` })
          }
        } else {
          resolve({ status: 'error', message: '缺少buttonText或selector参数' })
        }
        break

      case 'get_user_info':
        // 获取用户信息
        console.log('获取用户信息')
        import('../../../api/UserApi').then(UserApi => {
          UserApi.default.getUserBalance()
            .then(res => {
              console.log('用户余额:', res)
              const balance = res.data || 0
              ElMessage.success(`您的账户余额为：¥${balance}`)
              resolve({ status: 'success', message: `账户余额: ¥${balance}`, balance: balance })
            })
            .catch(error => {
              console.error('获取用户信息失败:', error)
              resolve({ status: 'error', message: '获取用户信息失败' })
            })
        })
        break

      case 'mcp_navigate':
        // MCP工具：导航到指定URL
        if (args.url) {
          console.log('MCP导航到:', args.url)
          ElMessage.success(`正在导航到: ${args.url}`)
          // 在浏览器中打开URL
          window.location.href = args.url
          resolve({ status: 'success', message: `已导航到: ${args.url}` })
        } else {
          resolve({ status: 'error', message: '缺少url参数' })
        }
        break

      case 'mcp_click':
        // MCP工具：点击页面元素
        if (args.selector) {
          console.log('MCP点击元素:', args.selector)
          const element = document.querySelector(args.selector)
          if (element) {
            element.click()
            ElMessage.success(`已点击元素: ${args.selector}`)
            resolve({ status: 'success', message: `已点击: ${args.selector}` })
          } else {
            resolve({ status: 'error', message: `未找到元素: ${args.selector}` })
          }
        } else {
          resolve({ status: 'error', message: '缺少selector参数' })
        }
        break

      case 'mcp_input':
        // MCP工具：在输入框中输入文本
        if (args.selector && args.text) {
          console.log('MCP输入:', args.selector, args.text)
          const element = document.querySelector(args.selector)
          if (element) {
            element.value = args.text
            element.dispatchEvent(new Event('input', { bubbles: true }))
            ElMessage.success(`已输入文本到: ${args.selector}`)
            resolve({ status: 'success', message: `已输入文本` })
          } else {
            resolve({ status: 'error', message: `未找到元素: ${args.selector}` })
          }
        } else {
          resolve({ status: 'error', message: '缺少selector或text参数' })
        }
        break

      case 'mcp_screenshot':
        // MCP工具：截取当前页面截图
        console.log('MCP截图开始')
        import('html2canvas').then(html2canvas => {
          console.log('html2canvas加载成功')
          html2canvas.default(document.body, {
            useCORS: true,
            allowTaint: true,
            logging: true
          }).then(canvas => {
            console.log('canvas生成成功，尺寸:', canvas.width, 'x', canvas.height)
            const imgData = canvas.toDataURL('image/png')
            console.log('截图转base64完成，长度:', imgData.length)
            resolve({ 
              status: 'success', 
              message: '截图成功', 
              screenshot: imgData,
              width: canvas.width,
              height: canvas.height
            })
          }).catch(error => {
            console.error('html2canvas生成失败:', error)
            resolve({ status: 'error', message: '截图失败: ' + error.message })
          })
        }).catch(error => {
          console.error('html2canvas加载失败:', error)
          resolve({ status: 'error', message: 'html2canvas加载失败' })
        })
        break

      default:
        console.warn('未知的工具:', functionName)
        resolve({ status: 'error', message: `未知的工具: ${functionName}` })
    }
  })
}
</script>

<style scoped>
/* 全局容器：红色+黄色风格统一 */
.pc-wrap {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: #fff8f0; /* 浅黄背景，契合餐饮暖色调 */
}
</style>

<style>
/* 全局样式：隐藏营养大师弹窗的遮罩层 */
body > .el-overlay.el-overlay-message-box {
  display: none !important;
}

.el-dialog__wrapper {
  pointer-events: none !important;
}

.el-dialog__wrapper .el-dialog {
  pointer-events: auto !important;
}

/* 针对custom-dialog的特殊处理 */
.el-dialog__wrapper.custom-dialog .el-overlay {
  display: none !important;
}

.el-dialog__wrapper.custom-dialog {
  background: transparent !important;
}

.el-dialog__wrapper.custom-dialog > .el-overlay {
  display: none !important;
}
</style>

<style scoped>

/* 导航栏：红色主色，黄色激活态 */
.nav-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #ff0000; /* 登录页红色主色 */
  color: #fff;
  padding: 0 30px;
  height: 60px;
  box-shadow: 0 2px 8px rgba(229, 77, 66, 0.3); /* 红色阴影增强层次感 */
}

.nav-title {
  font-size: 36px;
  font-weight: bold;
  color: #ffd700; /* 黄色标题，突出品牌名 */
  text-shadow: 0 0 3px rgba(255, 215, 0, 0.5); /* 轻微荧光效果，呼应登录页标题 */
}

/* 导航菜单：放大字体+优化间距 */
.nav {
  flex: 1;
  margin: 0 20px; /* 调整与标题的间距 */
}

.el-menu-demo {
  border-bottom: none; /* 取消默认下划线 */
}

.el-menu-item {
  font-size: 18px; /* 字体放大（原15px→18px） */
  margin: 0 10px; /* 增大菜单项间距，避免拥挤 */
  line-height: 60px; /* 与导航栏高度一致，确保文字垂直居中 */
}

.el-menu-item.is-active {
  font-weight: bold;
  color: #ffd700 !important; /* 黄色激活色，与标题呼应 */
}

/* 右侧登录注册按钮：放大字体 */
.nav-right {
  display: flex;
  gap: 15px;
  align-items: center; /* 确保按钮与文字垂直对齐 */
}

/* 用户头像样式 */
.user-avatar {
  width: 40px; /* 头像宽度 */
  height: 40px; /* 头像高度 */
  border-radius: 50%; /* 圆形头像 */
  object-fit: cover; /* 图片填充方式 */
  border: 2px solid #fff; /* 白色边框 */
  box-shadow: 0 0 3px rgba(0, 0, 0, 0.3); /* 轻微阴影效果 */
  cursor: pointer; /* 鼠标悬停时显示指针 */
}

/* 用户名称样式 */
.user-name-text {
  color: #fff;
  font-size: 16px;
  margin: 0 10px;
}

.nav-right .el-button--text {
  color: #fff !important;
  font-size: 16px; /* 字体放大（原14px→16px） */
  padding: 0 8px;
}

.nav-right .el-button--warning {
  font-size: 16px; /* 注册按钮字体放大 */
  padding: 8px 16px; /* 调整按钮内边距，适配字体大小 */
}

.nav-right .el-button--text:hover {
  color: #ffd700 !important; /* hover变黄，增强交互 */
}

/* 金色按钮样式（统一登录/注册） */
.gold-btn {
  background-color: #ffd700; /* 金色背景 */
  border-color: #ffd700;
  color: #333; /* 黑色字体 */
  font-size: 16px;
  padding: 8px 20px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

/* 鼠标悬浮效果 */
.gold-btn:hover {
  background-color: #f0c800; /* 深色金色hover */
  border-color: #f0c800;
  color: #000; /* 加深字体色 */
  transform: translateY(-2px); /* 轻微上浮，增强交互 */
}

/* 内容区域：统一卡片风格 */
.content {
  padding: 0 20px 30px;
  flex: 1;
}

.list-header {
  font-size: 22px;
  font-weight: 600;
  color: #e54d42; /* 红色标题 */
  margin-bottom: 20px;
  padding-left: 10px;
  border-left: 4px solid #ffd700; /* 黄色左边框强调 */
}

/* 商家卡片：红色+黄色点缀 */
.shop-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr); /* 固定5列 */
  grid-template-rows: repeat(2, auto); /* 固定2行 */
  gap: 24px;
  padding: 0 20px; /* 左右各留20px间距 */
  max-width: 1600px; /* 最大宽度，防止在大屏幕上过宽 */
  margin: 0 auto; /* 居中显示 */
}

.shop-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  border-top: 3px solid #e54d42; /* 红色顶边标识 */
}

.shop-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(229, 77, 66, 0.15); /* 红色阴影增强hover效果 */
}

/* 排名标识：黄色突出 */
.rank {
  font-size: 26px;
  font-weight: 700;
  color: #ffd700; /* 黄色排名，醒目 */
  width: 36px;
  text-align: center;
  background-color: #fff8f0;
  border-radius: 50%;
  height: 36px;
  line-height: 36px;
}

.shop-img {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  border: 2px solid #fff8f0; /* 浅黄边框 */
}

.name {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 6px;
  text-align: center;
}

/* 商家标签：红色/黄色区分 */
.tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  margin-right: 5px;
  margin-bottom: 5px;
}

.tag-silver {
  background-color: #f5f5f5;
  color: #666;
}

.tag-hot {
  background-color: #fff8f0;
  color: #e54d42; /* 红色热榜标签 */
}

.sale {
  font-size: 14px;
  color: #666;
  margin-bottom: 6px;
  text-align: center;
}

/* 分页：红色主题 */
.pagination-container {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

:deep(.el-pagination .el-pager li.active) {
  color: #fff !important;
  background-color: #e54d42 !important;
  font-weight: bold;
}

:deep(.el-pagination .el-pager li.is-active) {
  color: #fff !important;
  background-color: #e54d42 !important;
}

.el-pagination button:hover {
  color: #e54d42;
}

/* 页脚：红色顶部边框 */
.footer {
  height: 60px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 32px;
  font-size: 14px;
  color: #666;
  background: #fff;
  border-top: 1px solid #f8d7da; /* 浅红边框，呼应主题 */
  margin-top: auto;
}

/* 聊天按钮固定定位容器 */
.chat-button-wrapper {
  position: fixed;
  right: 20px;
  bottom: 50px;
  z-index: 100;
}

/* 聊天按钮样式 */
.chat-button {
  border-radius: 50% !important;
  background-color: #fff !important;
  border: 2px solid #e54d42 !important;
  color: #e54d42 !important;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(229, 77, 66, 0.3);
  transition: all 0.3s ease;
}

.chat-button:hover {
  background-color: #e54d42 !important;
  color: #fff !important;
  transform: scale(1.1);
}

/* 聊天对话框样式 - 强制覆盖所有默认样式 */

/* 隐藏遮罩层，让后面的内容可以操作 */
:deep(.el-dialog__wrapper.custom-dialog) > .el-overlay {
  display: none !important;
}

:deep(.el-dialog__wrapper.custom-dialog .el-overlay) {
  display: none !important;
}

:deep(.el-overlay) {
  display: none !important;
  background-color: transparent !important;
}

/* 1. 强制覆盖Element Plus对话框包装器的默认定位 - 适当增加整体高度 */
:deep(.el-dialog__wrapper.custom-dialog) {
  position: fixed !important;
  top: 100px !important;
  left: auto !important;
  right: 20px !important;
  bottom: 50px !important;
  width: 400px !important;
  height: auto !important;
  min-height: 600px !important;
  margin: 0 !important;
  background-color: transparent !important;
  z-index: 2005 !important;
  transform: none !important;
  display: block !important;
  overflow: visible !important;
  pointer-events: none !important;
}

/* 对话框本身可以点击 */
:deep(.el-dialog__wrapper.custom-dialog .el-dialog) {
  pointer-events: auto !important;
  position: fixed !important;
  top: 100px !important;
  left: auto !important;
  right: 20px !important;
  bottom: 50px !important;
  width: 400px !important;
  height: 700px !important;
  min-height: 700px !important;
    max-height: 700px !important;
  margin: 0 !important;
  padding: 0 !important;
  border-radius: 10px !important;
  box-shadow: 0 -2px 20px rgba(0, 0, 0, 0.15) !important;
  display: flex !important;
  flex-direction: column !important;
  overflow: hidden !important;
}

/* 3. 最小化头部高度，让更多空间给消息区域 */
:deep(.el-dialog__wrapper.custom-dialog .el-dialog .el-dialog__header) {
  padding: 8px 15px !important;
  border-bottom: 1px solid #e4e7ed !important;
  background-color: #fff !important;
  flex-shrink: 0 !important;
  height: 36px !important;
  min-height: 36px !important;
  max-height: 36px !important;
}

/* 4. 隐藏默认的关闭按钮，进一步节省空间 */
:deep(.el-dialog__wrapper.custom-dialog .el-dialog .el-dialog__headerbtn) {
  font-size: 14px !important;
  top: 8px !important;
  right: 10px !important;
}

/* 5. 让内容区域完全占据中间空间 */
:deep(.el-dialog__wrapper.custom-dialog .el-dialog .el-dialog__body) {
  padding: 0 !important;
  margin: 0 !important;
  display: flex !important;
  flex-direction: column !important;
  overflow: hidden !important;
  flex: 1 !important;
  height: auto !important;
  min-height: auto !important;
  max-height: none !important;
}

/* 6. 确保所有相关元素都继承正确的定位 */
.chat-dialog,
.custom-dialog,
.el-dialog.chat-dialog {
  position: fixed !important;
  right: 20px !important;
  bottom: 50px !important;
  width: 400px !important;
  height: 750px !important;
  margin: 0 !important;
  top: auto !important;
  left: auto !important;
  transform: none !important;
}

/* 7. 消息容器绝对占据全部可用空间 - 这是关键！ */
.message-container {
  flex: 1 !important;
  padding: 15px !important;
  overflow-y: auto !important;
  background-color: #f5f7fa !important;
  height: 650px !important;
  min-height: 650px !important;
  max-height: none !important;
  display: flex !important;
  flex-direction: column !important;
  border: none !important;
  outline: none !important;
  box-sizing: border-box !important;
  flex-grow: 0 !important;
  flex-shrink: 0 !important;
}

/* 8. 最小化输入容器高度，只保留必要空间 */
.input-container {
  flex-shrink: 0 !important;
  display: flex !important;
  align-items: center !important;
  padding: 8px 12px !important;
  background-color: #fff !important;
  border-top: 1px solid #e4e7ed !important;
  gap: 8px !important;
  height: 44px !important;
  min-height: 44px !important;
  max-height: 44px !important;
  flex-grow: 0 !important;
}

/* 消息项 */
.message-item {
  display: flex;
  margin-bottom: 15px;
  max-width: 80%;
}

/* 机器人消息 */
.bot-message {
  justify-content: flex-start;
}

/* 用户消息 */
.user-message {
  justify-content: flex-end;
}

/* 消息内容 */
.message-content {
  padding: 10px 15px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.4;
  word-break: break-word;
}

/* 消息内容中的图片样式 */
.message-content img {
  max-width: 200px !important;
  max-height: 200px !important;
  width: auto !important;
  height: auto !important;
  border-radius: 8px;
  margin-top: 5px;
  display: block;
}

/* 更具体的选择器确保样式生效 */
:deep(.message-content) img {
  max-width: 200px !important;
  max-height: 200px !important;
}

.bot-message .message-content {
  background-color: #fff;
  border: 1px solid #e4e7ed;
  border-top-left-radius: 5px;
}

.user-message .message-content {
  background-color: #409eff;
  color: #fff;
  border-top-right-radius: 5px;
}

/* 添加按钮 */
.add-btn {
  font-size: 20px;
  color: #606266;
}

.add-btn.speech-active {
  color: #e54d42;
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

/* 消息输入框 */
.message-input {
  flex: 1;
  border-radius: 20px;
  padding: 8px 15px;
}

/* 发送按钮 */
.send-btn {
  border-radius: 20px;
  padding: 8px 20px;
}
.footer {
  margin-top: 30px;
  background-color: #1a1a1a; /* 更深的背景色，更显高级 */
  color: #d1d1d1; /* 更亮的文字颜色，提高可读性 */
  text-align: center;
  flex-direction: column;
  align-items: center;
  gap: 20px;
  height: 300px;
}

.footer-links {
  display: inline-block;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  margin-top: 10px;
}

.footer-links a {
  color: #ede6e6;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  padding: 4px 12px;
  border-radius: 16px;
}

.footer-links a:hover {
  color: #fff;
  background-color: rgba(229, 77, 66, 0.2); /* 红色背景渐变 */
  transform: translateY(-2px);
}

.footer-records,
.footer-report {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  line-height: 1.4;
  color: #f1eded;
  flex-wrap: wrap;
  justify-content: center;
}

.footer-records span,
.footer-report span {
  display: flex;
  align-items: center;
}

.footer-records span:not(:last-child)::after,
.footer-report span:not(:last-child)::after {
  content: '|';
  margin-left: 12px;
  color: #ede7e7;
}

.footer-copyright {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  line-height: 1.4;
  color: #fdf6f6;
}

/* .footer-copyright {
  margin-top: 10px;
  padding-top: 15px;
  border-top: 1px solid #333;
  width: 100%;
  max-width: 800px;
} */

.footer-icons {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 25px;
  flex-wrap: wrap;
}

.icon-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.icon {
  font-size: 36px;
  color: #409eff; /* 蓝色图标，模仿酷狗风格 */
}

.footer-links {
  font-size: 14px;
  margin-bottom: 20px;
}

.footer-links a {
  color: #a8a8a8;
  text-decoration: none;
  margin: 0 5px;
}

.footer-links a:hover {
  color: #fff;
}

.footer-records {
  font-size: 13px;
  margin-bottom: 10px;
}

.footer-report {
  font-size: 13px;
  margin-bottom: 20px;
}

.footer-copyright {
  font-size: 13px;
}

/* 聊天按钮固定定位容器 */
.chat-button-wrapper {
  position: fixed;
  right: 20px;
  bottom: 50px;
  z-index: 100;
}

/* 聊天按钮样式 */
.chat-button {
  border-radius: 50% !important;
  background-color: #fff !important;
  border: 2px solid #e54d42 !important;
  color: #e54d42 !important;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(229, 77, 66, 0.3);
  transition: all 0.3s ease;
}

.chat-button:hover {
  background-color: #e54d42 !important;
  color: #fff !important;
  transform: scale(1.1);
}

</style>