import request from './request'

export function getAdminLearningItems(params = {}) {
  return request({
    url: '/admin/learning-items',
    method: 'get',
    params
  })
}

export function getAdminLearningItemStats(params = {}) {
  return request({
    url: '/admin/learning-items/stats',
    method: 'get',
    params
  })
}

export function getAdminLearningItem(id) {
  return request({
    url: `/admin/learning-items/${id}`,
    method: 'get'
  })
}

export function createAdminLearningItem(data) {
  return request({
    url: '/admin/learning-items',
    method: 'post',
    data
  })
}

export function updateAdminLearningItem(id, data) {
  return request({
    url: `/admin/learning-items/${id}`,
    method: 'put',
    data
  })
}

export function updateAdminLearningItemStatus(id, status) {
  return request({
    url: `/admin/learning-items/${id}/status`,
    method: 'put',
    data: { status }
  })
}

export function uploadAdminLearningItemTemplate(id, file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: `/admin/learning-items/${id}/template`,
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
