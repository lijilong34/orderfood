import request from "../utils/request";

//评价相关api
export default {
    //查询评价（管理员用）
    EvaluationSelect(params = {}) {
        return request.post('/evaluation/selectevaluation', params)
    },
    //根据id查询评价详情
    getEvaluationById(params) {
        return request.get("/evaluation/selectbyid/" + params)
    },
    //删除评价
    deleteEvaluation(params) {
        return request.get('/evaluation/remove/' + params)
    },loadbiao(){
        return request.get('/evaluation/loadbiao')
    },selectall(){
        return request.get('/evaluation/allselect')
    }
}