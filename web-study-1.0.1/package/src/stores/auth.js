import { defineStore } from 'pinia'
import {
  login as loginApi,
  register as registerApi,
  refreshToken as refreshTokenApi,
  getCurrentUser as getCurrentUserApi
} from '@/api/auth'
import { ElMessage } from 'element-plus'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    refreshToken: localStorage.getItem('refreshToken') || '',
    user: JSON.parse(localStorage.getItem('user') || 'null'),
    isAuthenticated: false
  }),

  getters: {
    isAdmin: state => state.user?.role === 'ADMIN',
    displayName: state => state.user?.displayName || state.user?.username || '用户'
  },

  actions: {
    setCookie(name, value, days = 7) {
      const expires = new Date()
      expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000)
      document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/;SameSite=Lax`
    },

    deleteCookie(name) {
      document.cookie = `${name}=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/;`
    },

    async login(username, password, captchaId, captchaCode) {
      try {
        const data = await loginApi(username, password, captchaId, captchaCode)
        this.token = data.token
        this.refreshToken = data.refreshToken
        this.user = {
          userId: data.userId,
          username: data.username,
          displayName: data.displayName,
          role: data.role
        }
        this.isAuthenticated = true

        localStorage.setItem('token', data.token)
        localStorage.setItem('refreshToken', data.refreshToken)
        localStorage.setItem('user', JSON.stringify(this.user))
        this.setCookie('token', data.token, 7)

        ElMessage.success('登录成功')
        return true
      } catch (error) {
        return false
      }
    },

    async register(userInfo) {
      try {
        const data = await registerApi(userInfo)
        this.token = data.token
        this.refreshToken = data.refreshToken
        this.user = {
          userId: data.userId,
          username: data.username,
          displayName: data.displayName,
          role: data.role
        }
        this.isAuthenticated = true

        localStorage.setItem('token', data.token)
        localStorage.setItem('refreshToken', data.refreshToken)
        localStorage.setItem('user', JSON.stringify(this.user))
        this.setCookie('token', data.token, 7)

        ElMessage.success('注册成功')
        return true
      } catch (error) {
        return false
      }
    },

    async refreshToken() {
      if (!this.refreshToken) {
        throw new Error('缺少刷新令牌')
      }

      try {
        const data = await refreshTokenApi(this.refreshToken)
        this.token = data.token
        this.refreshToken = data.refreshToken
        localStorage.setItem('token', data.token)
        localStorage.setItem('refreshToken', data.refreshToken)
        this.setCookie('token', data.token, 7)
        return true
      } catch (error) {
        this.logout()
        throw error
      }
    },

    async fetchUserInfo() {
      try {
        const user = await getCurrentUserApi()
        this.user = user
        localStorage.setItem('user', JSON.stringify(user))
        return user
      } catch (error) {
        console.error('获取用户信息失败:', error)
        return null
      }
    },

    initUser() {
      const token = localStorage.getItem('token')
      const userStr = localStorage.getItem('user')

      if (token && userStr) {
        this.token = token
        this.refreshToken = localStorage.getItem('refreshToken') || ''
        this.user = JSON.parse(userStr)
        this.isAuthenticated = true
        this.setCookie('token', token, 7)
      }
    },

    logout() {
      this.token = ''
      this.refreshToken = ''
      this.user = null
      this.isAuthenticated = false

      localStorage.removeItem('token')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('user')
      this.deleteCookie('token')
    }
  }
})
