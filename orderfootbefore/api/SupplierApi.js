import request from "../utils/request";

// 供应商管理API
export default {
    // 获取店铺管理员供应商列表
    getShopAdminSupplierList(params = {}) {
        return request.post('/supplier/shopadmin/list', params)
    },

    // 创建供应商
    createSupplier(data) {
        return request.post('/supplier/shopadmin/create', data)
    },

    // 更新供应商
    updateSupplier(data) {
        return request.put('/supplier/shopadmin/update', data)
    },

    // 删除供应商
    deleteSupplier(id) {
        return request.delete('/supplier/shopadmin/delete/' + id)
    },

    // 获取供应商详情
    getSupplierDetail(id) {
        return request.get('/supplier/shopadmin/detail/' + id)
    },

    // 启用供应商
    enableSupplier(id) {
        return request.post('/supplier/shopadmin/enable/' + id)
    },

    // 禁用供应商
    disableSupplier(id) {
        return request.post('/supplier/shopadmin/disable/' + id)
    },

    // 获取供应商下拉列表
    getSupplierOptions(shopId) {
        return request.get('/supplier/shopadmin/options', {
            params: { shopId }
        })
    }
}
