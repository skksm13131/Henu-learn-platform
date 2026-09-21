import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const request = axios.create({
  // Keep /api in the base URL so the Vite proxy can forward requests.
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

const getResponseMessage = (data, fallback = '请求失败，请稍后重试') => {
  return data?.message || data?.error?.message || fallback
}

const rejectWithMessage = (message) => Promise.reject(new Error(message))

request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

request.interceptors.response.use(
  response => {
    const res = response.data

    if (res && res.success !== undefined) {
      if (res.success) {
        return res.data !== undefined ? res.data : res
      }
      const message = getResponseMessage(res)
      ElMessage.error(message)
      return rejectWithMessage(message)
    }

    return res
  },
  async error => {
    const { response } = error

    if (!response) {
      const message = '网络异常，请检查连接后重试'
      ElMessage.error(message)
      return rejectWithMessage(message)
    }

    const { status, data } = response

    if (status === 401) {
      const requestUrl = error.config?.url || ''
      const isAuthRequest = requestUrl.includes('/auth/login')
        || requestUrl.includes('/auth/register')
        || requestUrl.includes('/auth/refresh')
        || requestUrl.includes('/auth/captcha')

      if (isAuthRequest) {
        const message = getResponseMessage(data, '用户名、密码或验证码不正确')
        ElMessage.error(message)
        return rejectWithMessage(message)
      }

      const authStore = useAuthStore()

      try {
        await authStore.refreshToken()
        const config = error.config
        const token = localStorage.getItem('token')
        if (token) {
          config.headers.Authorization = `Bearer ${token}`
        }
        return request(config)
      } catch (refreshError) {
        authStore.logout()
        const message = '登录状态已过期，请重新登录'
        ElMessage.error(message)
        window.location.href = '/login'
        return rejectWithMessage(message)
      }
    }

    if (status === 403) {
      const message = getResponseMessage(data, '无权访问该资源')
      ElMessage.error(message)
      return rejectWithMessage(message)
    }

    if (status === 404) {
      const message = getResponseMessage(data, '请求的资源不存在')
      ElMessage.error(message)
      return rejectWithMessage(message)
    }

    if (status === 413) {
      const message = getResponseMessage(data, '文件过大，请重新选择文件')
      ElMessage.error(message)
      return rejectWithMessage(message)
    }

    if (status >= 500) {
      const message = getResponseMessage(data, '服务器异常，请稍后重试')
      ElMessage.error(message)
      return rejectWithMessage(message)
    }

    const message = getResponseMessage(data)
    ElMessage.error(message)
    return rejectWithMessage(message)
  }
)

export default request
