import request from "../utils/request";

//投诉管理相关api
export default {
	//添加投诉
	addComplaint(data) {
		return request.post('/complaint/addComplaint', data)
	},
	//获取投诉列表
	getComplaintList(params = {}) {
		return request.post('/complaint/selectcomplaint', params)
	},
	//删除投诉
	deleteComplaint(id) {
		return request.delete('/complaint/' + id)
	},
	//处理投诉
	handleComplaint(data) {
		return request.put('/complaint/handle', data)
	},
	//修改投诉状态
	updateStatus(data) {
		return request.post('/complaint/updateStatus', data)
	}
}