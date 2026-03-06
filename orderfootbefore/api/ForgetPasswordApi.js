import request from "../utils/request";

//忘记密码相关api
export default {
   selectphoneexists(phone){
   return request.post('/ForgetPassword/selectphoneisexits', phone)
   },updatepassword(params = {}){
    return request.post('/ForgetPassword/updatepassword',params)
   }
}