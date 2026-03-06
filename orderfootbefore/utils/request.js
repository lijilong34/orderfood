import axios from 'axios'
import {
	ElMessage
} from 'element-plus';
import { ro } from 'element-plus/es/locales.mjs';
import {useRoute} from  'vue-router'
let router = useRoute();
//1. 创建新的axios实例，
const request = axios.create({
	// 超时时间 单位是ms，这里设置了180s的超时时间（AI处理截图需要更长时间）
	baseURL: 'http://localhost:8080/',
	timeout: 180 * 1000,
	headers: {
		'Content-Type': 'application/json;charset=UTF-8'
	}
})

// 请求拦截器（可添加 token 等）
request.interceptors.request.use(
	(config) => {
		const token = localStorage.getItem('token');
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	},
	(error) => Promise.reject(error)
);

// 3.响应拦截器
request.interceptors.response.use(response => {
	//接收到响应数据并成功后的一些共有的处理,保存token，关闭loading等
	//alert("==" + response)
	if (response.data.code != 200) {
		ElMessage.error(response.data.message)
		if(response.data.message=='出错了，呜呜呜!登录过期，请重新登录'){
			localStorage.clear();
			router.push({path:'/login'});
		}
	}
	// 总是返回response.data，无论code是否为200
	return response.data;
}, error => {
	/***** 接收到异常响应的处理开始 *****/
	let errorMessage = ''
	if (error && error.response) {
		// 1.公共错误处理
		// 2.根据响应码具体处理
		switch (error.response.status) {
			case 400:
				errorMessage = '错误请求'
				break;
			case 500:
				errorMessage = '服务器端出错'
				break;
			default:
				errorMessage = `连接错误${error.response.status}`
		}
	} else {
		// 超时处理
		if (JSON.stringify(error).includes('timeout')) {
			errorMessage = '服务器响应超时，请刷新当前页'
		} else {
			errorMessage = '连接服务器失败'
		}
	}
	
	ElMessage.error(errorMessage)

	/***** 处理结束 *****/
	// 抛出错误，让catch块能够捕获
	throw new Error(errorMessage)
})


export default request;
