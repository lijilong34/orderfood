import request from "../utils/request";

//动态加载菜单api操作
export default {
    //管理员加载菜单
    LoadMenusForAdmin() {
        return request.get('/selectmenusforadmin')
        //权限管理根据userid查询
    }, selectmenusbyuserid(employeeid){
        return request.get('/selectmenusbyuserid/' + employeeid);
        //查询所有
    },selectallmenus(){
        return request.get('/selectmenus',{})
    }
}
