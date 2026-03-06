import request from "../utils/request";

//用户相关api
export default {
    //查询商家
    ShopSelect(params = {}) {
        return request.post('/shop/select', params)
    },
    //添加店铺
    addShop(params = {}) {
        return request.post('/shop/add', params)
    },
    //删除店铺
    deleteShop(id) {
        return request.get(`/shop/remove/${id}`)
    },
    //暂停营业
    pauseShop(id) {
        return request.post(`/shop/pause/${id}`)
    },
    //开启营业
    openShop(id) {
        return request.post(`/shop/open/${id}`)
    },
    //封禁店铺
    banShop(id) {
        return request.post(`/shop/ban/${id}`)
    },
    //根据id查询店铺
    getShopById(id) {
        return request.get(`/shop/selectbyid/${id}`)
    },
    //更新店铺信息
    updateShop(params = {}) {
        return request.post('/shop/update', params)
    },
    //查看店铺营业额
    getShopRevenue(id) {
        return request.get(`/shop/revenue/${id}`)
    },
    //查看店铺日销售额
    getDailyRevenue(id, date) {
        return request.get(`/shop/daily-revenue/${id}?date=${date}`)
    },
    //查看店铺月销售额
    getMonthlyRevenue(id, month) {
        return request.get(`/shop/monthly-revenue/${id}?month=${month}`)
    },
    //获取所有店铺营业额数据（用于图表）
    getAllShopsRevenueForChart() {
        return request.get('/shop/revenue-all-for-chart')
    },selectshopbyshop(params={}){
        return request.post('/shop/selectshopbyshop',params);
    },getShopInfo(id){
        return request.get('/shop/selectbyid/'+id)
    }

}