import request from "../utils/request";

//历史相关api
export default {
    //获取历史列表
    getHistoryList(params) {
        return request.post('/history/selecthistorybyuserId', params);
    }, addhistory(params){
        return request.get('/history/addhistory/'+params);
    },delhistory(params = null){
        return request.post('/history/delhistory', params)
    }
}