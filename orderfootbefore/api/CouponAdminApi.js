import request from "../utils/request";

// 优惠券管理API
export default {
	// 获取优惠券模板列表
	getCouponList(params = {}) {
		return request.post('/coupon/admin/list', params)
	},

	// 创建优惠券模板
	createCoupon(data) {
		return request.post('/coupon/admin/create', data)
	},

	// 更新优惠券模板
	updateCoupon(data) {
		return request.put('/coupon/admin/update', data)
	},

	// 删除优惠券模板
	deleteCoupon(id) {
		return request.delete('/coupon/admin/delete/' + id)
	},

	// 获取优惠券详情
	getCouponDetail(id) {
		return request.get('/coupon/admin/detail/' + id)
	}
}