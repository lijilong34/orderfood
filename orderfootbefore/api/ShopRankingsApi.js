import request from "../utils/request";

//店铺排行榜相关api
export default {
	//获取营业额排行榜
	getRevenueRankings() {
		return request.get('/shop/rankings/revenue')
	},
	//获取订单数排行榜
	getOrderRankings() {
		return request.get('/shop/rankings/orders')
	},
	//获取评分排行榜
	getRatingRankings() {
		return request.get('/shop/rankings/rating')
	}
}