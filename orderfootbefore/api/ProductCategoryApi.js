import request from "../utils/request";

//分类相关api
export default {
   //根据店铺id查询分类
   selectcategorybyshopid(shopid){
	   return request.get('/ProductCategory/selectcategorybyshopid/'+shopid);
   },
   //添加分类
   addCategory(categoryForm){
	   return request.post('/ProductCategory/add', categoryForm);
   },
   //编辑分类
   updateCategory(categoryForm){
	   return request.post('/ProductCategory/update', categoryForm);
   },
   //删除分类
   deleteCategory(id){
	   return request.get('/ProductCategory/remove/'+id);
   }
}