import request from '../utils/request';

/**
 * 采购管理API
 */
const PurchaseApi = {
  /**
   * 获取店铺采购记录列表
   */
  getShopAdminPurchaseList: (params) => {
    return request({
      url: '/purchase/shopadmin/list',
      method: 'post',
      data: params
    });
  },

  /**
   * 创建采购记录
   */
  createPurchase: (params) => {
    return request({
      url: '/purchase/shopadmin/create',
      method: 'post',
      data: params
    });
  },

  /**
   * 更新采购记录
   */
  updatePurchase: (params) => {
    return request({
      url: '/purchase/shopadmin/update',
      method: 'put',
      data: params
    });
  },

  /**
   * 删除采购记录
   */
  deletePurchase: (id) => {
    return request({
      url: `/purchase/shopadmin/delete/${id}`,
      method: 'delete'
    });
  },

  /**
   * 获取采购记录详情
   */
  getPurchaseDetail: (id) => {
    return request({
      url: `/purchase/shopadmin/detail/${id}`,
      method: 'get'
    });
  },

  /**
   * 获取采购统计信息
   */
  getPurchaseStats: (shopId) => {
    return request({
      url: '/purchase/shopadmin/stats',
      method: 'get',
      params: { shopId }
    });
  }
};

export default PurchaseApi;
