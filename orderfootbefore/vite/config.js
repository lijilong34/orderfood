import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    // 配置代理，解决跨域和接口路径问题
    proxy: {
      '/api': {  // 用 '/api' 作为后端接口的前缀
        target: 'http://localhost:8080',  // 后端服务地址（修改为你的后端端口）
        changeOrigin: true,  // 允许跨域
        rewrite: (path) => path.replace(/^\/api/, '')  // 去掉前缀 '/api'，转发到实际接口
      }
    }
  }
})