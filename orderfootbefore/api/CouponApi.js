import request from "../utils/request";

export default {
    // 获取可用的优惠券列表
    getAvailableCoupons(params) {
        return request.get('/coupon/available', { params })
    },
    
    // 获取可领取的优惠券模板列表
    getCouponTemplates() {
        return request.get('/coupon/templates')
    },
    
    // 领取优惠券
    claimCoupon(couponData) {
        return request.post('/coupon/claim', couponData)
    },
    
    // 获取用户已领取的优惠券列表
    getUserCoupons() {
        return request.get('/coupon/couponlist')
    },
    
    // 获取今日已领取的优惠券
    getTodayClaimedCoupons() {
        return request.get('/coupon/today-claimed')
    },
    
    // 使用优惠券
    useCoupon(couponId) {
        return request.post('/coupon/use', null, { params: { couponId } })
    }
}
