import request from "../utils/request";

//评价相关api
export default {
    //获取评价信息（旧接口，不支持分页和查询）
    getEvaluation() {
        return request.get('/evaluation/selectevaluationShopId/'+localStorage.getItem("shopid"))
    },
    
    //管理员查询评价（支持分页和模糊查询）
    selectEvaluation(params = {}) {
        return request.post('/evaluation/selectevaluation', params)
    },
    
    //删除评价
    deleteEvaluation(evaluationId){
        return request.get('/evaluation/remove/'+evaluationId);
    }
}