import request from "../utils/request";
import axios from 'axios';
//登录相关的api操作
export default {
	//管理员人员登录
	managerlogin(params = {}){
		return  request.post('/managerlogin', params)
	//客户登录
	}, Userlogin(params = {}){
		return request.post("/UserLogin",params)
	}
}
