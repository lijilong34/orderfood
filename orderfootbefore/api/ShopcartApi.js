import request from "../utils/request";

//购物车相关api
export default {
    //查询购物车
    ShopCartSelect() {
        return request.get('/ShopCart/selectshopcartbyuserid')
    },
    //删除购物车商品
    ShopCartDelete(id) {
        return request.get(`/ShopCart/delShopCart/${id}`)
    },
    ShopCartAdd(data) {
        return request.post('/ShopCart/addToCart', data)
    },
    ShopCartUpdate(data) {
        return request.post('/ShopCart/updateShoppingCart', data)
    },addShopCartfor(){
        return request.get('/ShopCart/addShopCartfor')
    }
}