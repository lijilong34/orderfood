import request from "../utils/request";

//订单相关api
export default {
    //获取订单信息
    getOrderInfo(orderId) {
        return request.get('/order/selectbyid/'+orderId)
    },updateorderbyorderid(orderId){
        return request.get('/order/updateorderbyorderid/'+orderId);
    },completedorder(params={}){
        return request.post('/order/update',params);
    },selectorderbyid(orderId){
       return request.get('/order/selectbyid/'+orderId)
    },addmessage(params={}){
       return request.post('/message/add',params)
    }
}