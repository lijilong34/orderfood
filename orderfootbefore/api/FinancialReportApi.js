import request from '../utils/request.js';

// 财务报表API
const FinancialReportApi = {
    // 获取财务报表总览数据
    getOverview(shopId) {
        return request({
            url: '/financial-report/overview',
            method: 'get',
            params: { shopId }
        });
    },

    // 获取财务统计数据
    getStats(params) {
        return request({
            url: '/financial-report/stats',
            method: 'get',
            params
        });
    },

    // 获取财务报表列表
    getReportList(params) {
        return request({
            url: '/financial-report/list',
            method: 'get',
            params
        });
    },

    // 生成财务报表
    generateReport(data) {
        return request({
            url: '/financial-report/generate',
            method: 'post',
            data
        });
    },

    // 保存财务报表
    saveReport(data) {
        return request({
            url: '/financial-report/save',
            method: 'post',
            data
        });
    },

    // 获取财务趋势数据
    getTrend(params) {
        return request({
            url: '/financial-report/trend',
            method: 'get',
            params
        });
    }
};

export default FinancialReportApi;