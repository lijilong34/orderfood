import request from '../utils/request.js';

// 公告管理API
const AnnouncementApi = {
    // 分页查询公告
    AnnouncementSelect(data) {
        return request({
            url: '/announcement/selectbylike',
            method: 'post',
            data
        });
    },
    
    // 根据ID获取公告详情
    getAnnouncementById(id) {
        return request({
            url: `/announcement/${id}`,
            method: 'get'
        });
    },
    
    // 添加公告
    addAnnouncement(data) {
        return request({
            url: '/announcement/add',
            method: 'post',
            data
        });
    },
    
    // 更新公告
    updateAnnouncement(data) {
        return request({
            url: '/announcement/update',
            method: 'put',
            data
        });
    },
    
    // 删除公告
    deleteAnnouncement(id) {
        return request({
            url: `/announcement/delete/${id}`,
            method: 'delete'
        });
    }
};

export default AnnouncementApi;