import request from "../utils/request";

//地址相关api
export default {
    //获取地址列表
    getAddressList() {
        return request.get('/address/list')
    },
    //查询地址
    AddressSelect(params = {}) {
        return request.post('/address/select',params)
    //添加地址
    },
    AddAddress(params={}){
        return request.post("/address/add",params)
		//根据id查询
    },
	selectbyid(params){
		return request.get("/address/selectbyid/"+params)
		//修改地址
	},UpdateAddress(params={}){
		return request.post('/address/update',params)
		//删除地址
	},DelAddress(params){
		return request.get('/address/remove/'+params)
	},loadbiao(){
        return request.get('/address/loadbiao')
    },selectall(){
        return request.get('/address/allselect')
    }
}