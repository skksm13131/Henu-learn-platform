import request from './request'

export function login(username, password, captchaId, captchaCode) {
  return request({
    url: '/auth/login',
    method: 'post',
    data: {
      username,
      password,
      captchaId,
      captchaCode
    }
  })
}

export function register(userInfo) {
  return request({
    url: '/auth/register',
    method: 'post',
    data: userInfo
  })
}

export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get'
  })
}

export function refreshToken(refreshToken) {
  return request({
    url: '/auth/refresh',
    method: 'post',
    data: {
      refreshToken
    }
  })
}

export function getCurrentUser() {
  return request({
    url: '/auth/me',
    method: 'get'
  })
}

