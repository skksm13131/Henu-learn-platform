<template>
  <div class="login-shell">
    <div class="login-card">
      <h1 class="login-title">用户登录</h1>
      <p class="login-subtitle">大数据智能处理团队研究生学习平台</p>

      <form @submit.prevent="handleLogin" id="login-form">
        <div class="input-wrapper">
          <label class="form-label">用户名</label>
          <input
            type="text"
            v-model="form.username"
            class="login-input"
            placeholder="请输入用户名"
            autocomplete="off"
          />
        </div>

        <div class="input-wrapper">
          <label class="form-label">密码</label>
          <input
            type="password"
            v-model="form.password"
            class="login-input"
            placeholder="请输入密码"
            autocomplete="off"
            @keyup.enter="handleLogin"
          />
        </div>

        <div class="input-wrapper">
          <label class="form-label">验证码</label>
          <div class="captcha-row">
            <input
              type="text"
              v-model="form.captchaCode"
              class="login-input"
              placeholder="请输入验证码"
              autocomplete="off"
            />
            <img
              v-if="loginCaptchaImage"
              :src="loginCaptchaImage"
              class="captcha-image"
              alt="验证码"
              title="点击刷新验证码"
              @click="loadLoginCaptcha"
            />
            <button type="button" class="captcha-refresh" @click="loadLoginCaptcha">刷新</button>
          </div>
        </div>

        <div class="input-wrapper">
          <label class="form-label">年级</label>
          <input
            type="text"
            v-model="form.grade"
            class="login-input"
            placeholder="例如：2022级 / 大一 / 研一（可选）"
            autocomplete="off"
          />
        </div>

        <div class="input-wrapper" style="margin-top: 6px;">
          <label style="display: flex; align-items: center; color: #e2e8f0; font-size: 14px; cursor: pointer;">
            <input
              type="checkbox"
              v-model="form.remember"
              style="margin-right: 8px; width: 16px; height: 16px; cursor: pointer;"
            />
            记住登录状态
          </label>
        </div>

        <button type="submit" class="primary-button" :disabled="loading">
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="login-footer">
        若已登录，将自动跳转至系统。<br>
        还没有账号？<a href="#" @click.prevent="openRegister">立即注册</a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'
import { getCaptcha } from '@/api/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const loading = ref(false)
const loginCaptchaImage = ref('')

const form = reactive({
  username: '',
  password: '',
  grade: '',
  remember: true,
  captchaId: '',
  captchaCode: ''
})

onMounted(() => {
  // 如果已登录，自动跳转
  if (authStore.isAuthenticated) {
    router.push('/')
  }
  loadLoginCaptcha()
})

const handleLogin = async () => {
  if (!form.username || !form.password || !form.captchaId || !form.captchaCode) {
    ElMessage.warning('请输入用户名、密码和验证码')
    return
  }

  loading.value = true
  try {
    const success = await authStore.login(form.username.trim(), form.password, form.captchaId, form.captchaCode.trim())
    if (success) {
      const redirect = route.query.redirect || '/'
      router.push(redirect)
    } else {
      await loadLoginCaptcha()
    }
  } finally {
    loading.value = false
  }
}

const loadLoginCaptcha = async () => {
  try {
    const data = await getCaptcha()
    form.captchaId = data.captchaId
    form.captchaCode = ''
    loginCaptchaImage.value = data.image
  } catch (error) {
    console.error('加载验证码失败', error)
    ElMessage.error('加载验证码失败')
  }
}

const openRegister = () => {
  ElMessage.info('注册功能已关闭，请联系管理员创建账号')
}

</script>

<style scoped>
.login-shell {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 32px 20px;
  background: url('@/assets/images/fig2.jpeg') no-repeat center center / cover fixed;
  position: relative;
  font-family: 'Inter', system-ui, sans-serif;
}

.login-card {
  width: 400px;
  background: rgba(4, 8, 24, 0.72);
  border-radius: 32px;
  padding: 42px 48px;
  box-shadow: 0 40px 80px rgba(2, 6, 23, 0.65);
  border: 1px solid rgba(148, 163, 184, 0.2);
  backdrop-filter: blur(20px);
  color: #f8fafc;
}

.login-title {
  text-align: center;
  font-size: 26px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #f8fafc;
}

.login-subtitle {
  text-align: center;
  color: #94a3b8;
  font-size: 13px;
  margin-bottom: 28px;
}

.form-label {
  display: block;
  color: #e2e8f0;
  font-size: 14px;
  margin-bottom: 6px;
}

.input-wrapper {
  margin-bottom: 18px;
}

.login-input {
  width: 100%;
  border-radius: 14px;
  border: 1px solid rgba(148, 163, 184, 0.4);
  padding: 12px 16px;
  background: rgba(15, 23, 42, 0.7);
  color: #f8fafc;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
  box-sizing: border-box;
}

.login-input::placeholder {
  color: #94a3b8;
}

.login-input:focus {
  border-color: #60a5fa;
  box-shadow: 0 0 0 2px rgba(96, 165, 250, 0.25);
}

.captcha-row {
  display: grid;
  grid-template-columns: 1fr 120px auto;
  gap: 8px;
  align-items: center;
}

.captcha-image {
  width: 120px;
  height: 42px;
  border-radius: 10px;
  cursor: pointer;
  background: #f8fafc;
}

.captcha-refresh {
  border: 1px solid rgba(148, 163, 184, 0.45);
  border-radius: 10px;
  padding: 0 10px;
  height: 42px;
  background: rgba(15, 23, 42, 0.72);
  color: #e2e8f0;
  cursor: pointer;
}

.primary-button {
  width: 100%;
  border: none;
  border-radius: 16px;
  padding: 14px 0;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 2px;
  background: linear-gradient(135deg, #4f83ff, #2f6bff);
  color: #fff;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  box-shadow: 0 18px 35px rgba(47, 107, 255, 0.35);
}

.primary-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 25px 40px rgba(47, 107, 255, 0.45);
}

.primary-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.login-footer {
  margin-top: 20px;
  font-size: 13px;
  color: #94a3b8;
  text-align: center;
  line-height: 1.8;
}

.login-footer a {
  color: #63b3ff;
  text-decoration: none;
  margin-left: 6px;
  cursor: pointer;
}

.login-footer a:hover {
  text-decoration: underline;
}

</style>
