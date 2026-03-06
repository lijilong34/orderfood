import request from "../utils/request";

// 评论管理相关API（管理员用）
export default {
    // 查询所有评论（分页）
    getAllReviews(params = {}) {
        return request.post('/evaluation/selectevaluation', params)
    },
    // 根据ID查询评论详情
    getReviewById(id) {
        return request.get('/evaluation/selectbyid/' + id)
    },
    // 删除评论
    deleteReview(id) {
        return request.get('/evaluation/remove/' + id)
    },
    // 批量删除评论
    batchDeleteReviews(ids) {
        return request.post('/evaluation/batchRemove', { ids })
    },
    // 更新评论状态（如：隐藏/显示）
    updateReviewStatus(params = {}) {
        return request.post('/evaluation/updateStatus', params)
    },
    // 回复评论
    replyReview(params = {}) {
        return request.post('/evaluation/reply', params)
    },
    // 获取评论统计信息
    getReviewStats() {
        return request.get('/evaluation/stats')
    }
}