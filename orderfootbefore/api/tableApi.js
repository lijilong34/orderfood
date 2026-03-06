import request from '../utils/request'

const TableApi = {
  // 餐桌列表查询
  selectTableList: (params) => {
    return request.post('/table/select', params)
  },

  // 餐桌详情查询
  selectById: (id) => {
    return request.get(`/table/selectbyid/${id}`)
  },

  // 新增餐桌
  addTable: (data) => {
    return request.post('/table/add', data)
  },

  // 编辑餐桌
  updateTable: (data) => {
    return request.post('/table/update', data)
  },

  // 删除餐桌
  deleteTable: (id) => {
    return request.get(`/table/delete/${id}`)
  },

  // 获取可用餐桌
  getAvailableTables: (params) => {
    return request.get('/table/available', { params })
  }
}

export default TableApi