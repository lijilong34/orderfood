# Vue3餐饮点餐系统

## 项目概述
- 项目类型：Vue 3 + Vite + Element Plus 餐饮点餐系统
- 目标：以开发模式启动前端项目，确保与后端服务正常交互

## 运行步骤

### 第一步：环境验证
1. 确认Node.js版本（建议16+）
2. 检查项目依赖是否已安装
3. 验证后端服务在localhost:8080可访问

### 第二步：安装依赖（如需要）
```bash
npm install
```

### 第三步：启动开发服务器
```bash
npm run dev
```
- 服务器将在 http://localhost:5173 启动
- 支持热重载
- 自动打开浏览器

### 第四步：功能验证
1. 访问首页，验证界面加载
2. 测试用户登录功能
3. 测试管理员登录和动态路由加载
4. 验证主要功能模块（商品浏览、购物车、订单等）

## 关键配置文件
- package.json - 项目依赖和脚本
- vite.config.js - Vite开发服务器配置
- src/main.js - 应用入口
- src/components/router/router.js - 路由配置
- utils/request.js - API请求配置

## 注意事项
1. 确保后端服务在8080端口正常运行
2. 项目使用localStorage存储token
3. 管理员中心路由依赖API动态加载
4. 支持cpolar内网穿透访问

## 可能遇到的问题及解决方案
1. 端口冲突：修改vite.config.js中的port配置
2. API连接失败：检查后端服务状态和CORS配置
3. 依赖安装失败：尝试清除npm缓存后重新安装