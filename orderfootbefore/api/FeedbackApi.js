import request from "../utils/request";

//反馈相关api
export default {
    //用户提交反馈
    submitFeedback(data) {
        return request.post('/feedback/submit', data)
    },
    //获取反馈列表
    getFeedbackList(params = {}) {
        return request.post('/feedback/selectByUser', params)
    },
    //根据ID获取反馈详情
    getFeedbackById(id) {
        return request.get('/feedback/' + id)
    },
    //更新反馈状态
    updateFeedbackStatus(data) {
        return request.put('/feedback/status', data)
    },
    //删除反馈
    deleteFeedback(id) {
        return request.delete('/feedback/' + id)
    },
    //管理员查询所有反馈
    selectfeedbackforadmin(params = {}) {
        return request.post('/feedback/selectfeedbackforadmin', params)
    }
}