import request from "../utils/request";

//收藏相关api
export default {
    //查询用户收藏列表
    FavoriteSelect() {
        return request.get('/Favorite/selectfavoritebyuserid')
    },
    //添加收藏
    FavoriteAdd(productId) {
        return request.get('/Favorite/addToFavorite/'+productId)
    },
    //取消收藏
    FavoriteDelete(id) {
        return request.get(`/Favorite/removeFavorite/${id}`)
    },
    //检查是否已收藏
    FavoriteCheck(productId) {
        return request.get(`/Favorite/check/${productId}`)
    }
}