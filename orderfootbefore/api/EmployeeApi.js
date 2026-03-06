import request from "../utils/request";

//员工管理相关的api操作
export default {
    //查询员工列表
    getEmployeeList(params = {}){
        return request.post('/employee/select', params)
    },
    //添加员工
    addEmployee(params = {}){
        return request.post('/employee/add', params)
    },
    //删除员工
    deleteEmployee(id){
        return request.get(`/employee/remove/${id}`)
    },
    //暂停员工
    pauseEmployee(id){
        return request.post(`/employee/pause/${id}`)
    },
    //恢复员工工作
    resumeEmployee(id){
        return request.post(`/employee/resume/${id}`)
    },
    //根据id查询员工
    getEmployeeById(id){
        return request.get(`/employee/selectbyid/${id}`)
    },
    //更新员工信息
    updateEmployee(params = {}){
        return request.post('/employee/update', params)
        //连接商店表查询员工
    }, selectEmployeeandshop(params={}){
        return request.post('/employee/selectemployee',params);
    },selectemployeebyemployee(params={}){
        return request.post('/employee/selectemployeebyemployee',params)
    }
}
