import request from './request'

export function getLearningItems(params = {}) {
  return request({
    url: '/learning-items',
    method: 'get',
    params
  })
}

export function getLearningItemPreview(id) {
  return request({
    url: `/learning-items/${id}/preview`,
    method: 'get'
  })
}

export function getLearningItemDetail(id) {
  return request({
    url: `/learning-items/${id}`,
    method: 'get'
  })
}

export function completeLearningProgress(payload) {
  return request({
    url: '/learning-progress/complete',
    method: 'post',
    data: payload
  })
}

export function getLearningProgress(params) {
  return request({
    url: '/learning-progress',
    method: 'get',
    params
  })
}

export function enterLearningProgress(payload) {
  return request({
    url: '/learning-progress/enter',
    method: 'post',
    data: payload
  })
}

export function getUserLearningProgress() {
  return request({
    url: '/learning-progress/user',
    method: 'get'
  })
}

export function addLearningTime(payload) {
  return request({
    url: '/learning-progress/time',
    method: 'post',
    data: payload
  })
}
