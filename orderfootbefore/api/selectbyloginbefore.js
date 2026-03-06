import request from "../utils/request";
import axios from 'axios';

// 创建不需要登录的axios实例
const noAuthRequest = axios.create({
	baseURL: 'http://localhost:8080/',
	timeout: 30 * 1000,
	headers: {
		'Content-Type': 'application/json;charset=UTF-8'
	}
});

// 不需要登录的响应拦截器
noAuthRequest.interceptors.response.use(response => {
	if (response.data.code != 200) {
		console.error('API错误:', response.data.message);
	}
	return response.data;
}, error => {
	let errorMessage = ''
	if (error && error.response) {
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
		errorMessage = '连接服务器失败'
	}
	console.error(errorMessage);
	throw new Error(errorMessage)
});

//登录之前相关的api操作
export default {
	//获取销量前十的店铺
	selectshopbytop10() {
		return request.get("/Shopforbefor/selectshopbytop10")
	//根据搜索词语
	}, selectshopbysech(params = {}){
		return request.post("/Shopforbefor/selectallandProduct",params);
		//获取前五的商品
	}, selectproductbytop5(){
		return request.get('/Shopforbefor/selectproducttop5')
	},takeai(params){
		return request.post('/Shopforbefor/takeai',params)
	},takeaiStream(params, onMessage, onError, onComplete) {
		// 使用EventSource接收SSE流式数据
		const url = `http://localhost:8080/Shopforbefor/takeaiStream?message=${encodeURIComponent(params.message || '')}&imgone=${encodeURIComponent(params.imgone || '')}&conversationId=${encodeURIComponent(params.conversationId || 'default')}`;
		
		const eventSource = new EventSource(url);
		
		eventSource.onmessage = (event) => {
			try {
				const data = JSON.parse(event.data);
				if (onMessage) onMessage(data);
			} catch (e) {
				// 忽略解析错误
			}
		};
		
		eventSource.onerror = (error) => {
			if (onError) onError(error);
			eventSource.close();
		};
		
		eventSource.addEventListener('start', (event) => {
			if (onMessage) onMessage({ type: 'start', data: event.data });
		});
		
		eventSource.addEventListener('thinking', (event) => {
			if (onMessage) onMessage({ type: 'thinking', data: event.data });
		});
		
		eventSource.addEventListener('content', (event) => {
			if (onMessage) onMessage({ type: 'content', data: event.data });
		});
		
		eventSource.addEventListener('end', (event) => {
			if (onComplete) onComplete({ type: 'end', data: event.data });
			eventSource.close();
		});
		
		eventSource.addEventListener('error', (event) => {
			if (onError) onError({ type: 'error', data: event.data });
			eventSource.close();
		});
		
		return eventSource;
	},selectshopbylist(queryParams){
	let cids=queryParams.cid
	if(queryParams.cid=='' ||queryParams.cid==null){
		cids=0
	}
	return request.post('/Shopforbefor/selectshopbylist?id='+localStorage.getItem('shopid')+"&cid="+cids+"&nickname="+queryParams.nickname+"&phone="+queryParams.phone+"&page="+localStorage.getItem("OrederListPage"))
	},selectcategoryforshop(params={}){
		return request.get('/Shopforbefor/selectcategoryforshop',{ params })
	}, productSelect(params = {}) {
		return request.post('/product/select', params)
	}, selectproductinfobyproductid(params){
		return noAuthRequest.get('/selectproductinfobyproductid/'+params)
	}, getEvaluationsByProductId(productId){
		return noAuthRequest.get('/evaluation/selectevaluationProductId/'+productId)
	},selectDishByCategory(params={}){
		return request.post('/product/selectproductbycategory',params)
	},selectCategory(params={}){
		return request.post('/ProductCategory/select',params)
	},selectproducttop10(){
		return request.get('/product/selectproducttop10')
	},selectProductByShopId(shopId){
		return noAuthRequest.get('/product/selectallproductbyshop?shopId='+shopId)
	}
}