import request from "../utils/request";

export default {
     orderitembyid(){
        // 检查是否是店长登录（使用shopid）
        let shopid = localStorage.getItem("shopid");
        let userid = localStorage.getItem("userid");
        let page = localStorage.getItem("BeforOrderCurrentOrder");
        
        if(shopid){
            // 店长使用shopid查询
            let url = '/orderitem/selectbyshopid?shopid='+shopid+"&page="+page;
            console.log("店长查询店铺订单，shopid:", shopid, "page:", page);
            console.log("完整请求URL:", "http://localhost:8080" + url);
            return request.get(url);
        } else if(userid){
            // 普通用户使用userid查询
            let url = '/orderitem/selectbyuserid?userid='+userid+"&page="+page;
            console.log("查询用户订单，userid:", userid, "page:", page);
            console.log("完整请求URL:", "http://localhost:8080" + url);
            return request.get(url);
        } else {
            console.error("用户未登录，无法查询订单");
            return Promise.reject("用户未登录");
        }
    },
	orderitembylist(params){
		// 检查是否是店长登录（使用shopid）
		let shopid = localStorage.getItem("shopid");
		let userid = localStorage.getItem("userid");
		let page = localStorage.getItem("BeforOrderCurrentOrder");
		let oids=params.oid
		if(params.oid=='' ||params.oid==null){
			oids=0
		}
		
		if(shopid){
			// 店长使用shopid查询
			let url = '/orderitem/selectorderbylist?id='+shopid+"&oid="+oids+"&name="+params.name+"&phone="+params.phone+"&page="+page;
			console.log("店长条件查询店铺订单，shopid:", shopid, "完整URL:", "http://localhost:8080" + url);
			return request.post(url);
		} else if(userid){
			// 普通用户使用userid查询
			let url = '/orderitem/selectorderbylist?id='+userid+"&oid="+oids+"&name="+params.name+"&phone="+params.phone+"&page="+page;
			console.log("条件查询用户订单，userid:", userid, "完整URL:", "http://localhost:8080" + url);
			return request.post(url);
		} else {
			console.error("用户未登录，无法查询订单");
			return Promise.reject("用户未登录");
		}
	},
	delect(id){
		return request.get('/orderitem/remove/'+id)
	}
}
