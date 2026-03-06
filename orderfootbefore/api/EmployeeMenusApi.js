import request from "../utils/request";

//员工菜单api
export default {
    delEmployeeMenus(params={}){
        return request.post('/EmployeeMenus/delEmployeeMenus',params)
    },addEmployeeMenus(params={}){
        return request.post('/EmployeeMenus/addEmployeeMenus',params)
    }    
}