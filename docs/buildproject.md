## 本项目部署流程
一.云端编译(推荐方式)<br>
1.fork本项目<br>
2.运行actions<br>
3.下载<br>
4.解压<br>
二.本地编译<br>
1.克隆此项目<br>
```shell
git clone https://github.com/lijilong34/orderfood.git
```
2.编译后端<br>
```shell
cd orderfood/orderfoodafter
mvn clean package -DskipTests
```
3.编译前端
```shell
cd orderfood/orderfootbefore
npm install
npm run build
```
三.上传到服务器
四.启动后端
```shell
java -jar target\jar文件
```
五.配置前端
1.进入 Nginx 站点配置目录(例如:`cd /etc/nginx/sites-available/`)
2.新建配置文件（示例名称：vue-project.conf），内容如下:
```nginx
server {
    listen 80;                     # 监听80端口（默认HTTP端口）
    server_name your-domain.com;   # 替换为你的域名或服务器IP
    root /var/www/vue-project/dist;# 指向Vue打包后的dist目录
    index index.html index.htm;    # 默认访问首页

    # 静态资源访问配置（优化加载速度）
    location ~* \.(jpg|jpeg|png|gif|ico|css|js)$ {
        expires 30d;               # 静态资源缓存30天
        add_header Cache-Control "public, no-transform";
    }
}
```