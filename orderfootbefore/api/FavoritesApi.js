import request from "../utils/request";

//收藏相关api
export default {
    //获取收藏列表
    getFavoritesList(params) {
        return request.post('/favorites/selectFavoritesByUserId', params);
    }, 
    addFavorite(params){
        return request.post('/favorites/addFavorite', params);
    },
    removeFavorite(params = null){
        return request.post('/favorites/removeFavorite', params)
    }
}