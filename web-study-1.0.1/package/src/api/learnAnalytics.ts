import request from './request'

export function getLearnDashboard() {
  return request({
    url: '/learn-analytics/dashboard',
    method: 'get'
  })
}

export function getLearnSummary() {
  return request({
    url: '/learn-analytics/summary',
    method: 'get'
  })
}

export function getLearnByCategory() {
  return request({
    url: '/learn-analytics/by-category',
    method: 'get'
  })
}

export function getLearnByDifficulty() {
  return request({
    url: '/learn-analytics/by-difficulty',
    method: 'get'
  })
}

export function getLearnCompletionTrend(days = 30) {
  return request({
    url: '/learn-analytics/completion-trend',
    method: 'get',
    params: { days }
  })
}

export function getLearnRecent(limit = 20) {
  return request({
    url: '/learn-analytics/recent',
    method: 'get',
    params: { limit }
  })
}

export function getLearnRecommendation() {
  return request({
    url: '/learn-analytics/recommendation',
    method: 'get'
  })
}
