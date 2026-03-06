import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import router from './components/router/router.js'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import SlideVerify from 'vue3-slide-verify'
import 'vue3-slide-verify/dist/style.css'
import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css'

const app = createApp(App)
app.use(ElementPlus, {
	locale: zhCn,
})

app.use(router)
app.use(SlideVerify)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component)
}
app.use(Viewer)
Viewer.setDefaults({
  zIndex: 9999
})
app.mount('#app')
