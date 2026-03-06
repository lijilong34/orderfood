import request from "../utils/request";

//用户相关api
export default {
    //查询用户
    UserSelect(params = {}) {
        return request.post('/user/select', params)
    },
    //添加用户
    addUser(params = {}) {
        return request.post("/user/add", params)
    },
    //根据id查询
    getUserById(params) {
        return request.get("/user/selectbyid/" + params)
    },
    //获取当前用户信息（通过token）
    getCurrentUser() {
        return request.get("/user/getCurrentUser")
    },
    //修改用户
    updateUser(params = {}) {
        return request.post('/user/update', params)
    },
    //删除用户
    deleteUser(params) {
        return request.get('/user/remove/' + params)
    },
    //用户充值
    userRecharge(params) {
        return request.post('/user/recharge', params)
    },
    //查询用户余额
    getUserBalance() {
        return request.get('/user/getBalance')
    },addmessage(params={}){
       return request.post('/message/add',params)
    },updateNickname(params={}){
      return request.post('/user/update',params)
    },loadbiao(){
        return request.get('/user/loadbiao')
    },selectall(){
        return request.get('/user/allselect')
    }
}