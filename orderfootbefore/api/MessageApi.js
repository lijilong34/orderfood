import request from "../utils/request";

//消息通知相关api
export default {
    //查询当前用户的消息列表
    getMessageList(params = {}) {
        return request.post('/message/list', params)
    },
    //删除消息
    deleteMessage(params) {
        return request.get('/message/delete/' + params)
    }
}