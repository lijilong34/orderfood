import request from '../utils/request';

export default {
submitReview(reviewData) {
  return request({
    url: '/evaluation/addevaluation',
    method: 'post',
    data: reviewData
  });
},
getReviewStatus(orderId) {
  return request({
    url: `/review/status/${orderId}`,
    method: 'get'
  });
},
getUserReviews(params) {
  return request({
    url: '/review/user/list',
    method: 'get',
    params
  });
},getShopReviews(shopId, params) {
  return request({
    url: `/review/shop/${shopId}`,
    method: 'get',
    params
  });
},deleteReview(reviewId) {
  return request({
    url: `/review/delete/${reviewId}`,
    method: 'delete'
  });
},uploadReviewImage(file) {
  const formData = new FormData();
  formData.append('file', file);
  
  return request({
    url: '/review/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
}
}