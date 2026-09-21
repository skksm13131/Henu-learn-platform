<template>
  <div class="personal-center">
    <div class="page-header">
      <div>
        <h1 class="page-title">个人中心</h1>
        <p class="page-description">查看并编辑你的个人信息</p>
      </div>
    </div>

    <el-card class="profile-card">
      <el-form :model="form" ref="formRef" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled />
        </el-form-item>
        <el-form-item label="显示名">
          <el-input v-model="form.displayName" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="真实姓名">
          <el-input v-model="form.realName" />
        </el-form-item>
        <el-form-item label="旧密码" v-if="form.password">
          <el-input v-model="form.oldPassword" type="password" placeholder="修改密码时必填" />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="form.password" type="password" placeholder="不修改请留空" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const formRef = ref(null)
const form = ref({
  userId: null,
  username: '',
  displayName: '',
  email: '',
  realName: '',
  oldPassword: '',
  password: ''
})

const load = async () => {
  try {
    const res = await request.get('/auth/me')
    const user = res
    form.value.userId = user?.userId || user?.id
    form.value.username = user?.username || ''
    form.value.displayName = user?.displayName || ''
    form.value.email = user?.email || ''
    form.value.realName = user?.realName || ''
  } catch (err) {
    console.error('加载用户信息失败', err)
    ElMessage.error('加载用户信息失败')
  }
}

const save = async () => {
  try {
    if (form.value.password && !isStrongPassword(form.value.password)) {
      ElMessage.warning('密码至少 12 位，且需包含大小写字母、数字和特殊字符')
      return
    }
    const payload = {
      displayName: form.value.displayName,
      email: form.value.email,
      realName: form.value.realName
    }
    if (form.value.password) {
      payload.oldPassword = form.value.oldPassword
      payload.password = form.value.password
    }
    await request.put('/users/me', payload)

    // 更新 store 中的用户信息
    if (authStore.user) {
      authStore.user.displayName = form.value.displayName
      authStore.user.email = form.value.email
      authStore.user.realName = form.value.realName
      // 更新 localStorage
      localStorage.setItem('user', JSON.stringify(authStore.user))
    }

    ElMessage.success('保存成功')
    form.value.oldPassword = ''
    form.value.password = ''
  } catch (err) {
    console.error('保存失败', err)
    ElMessage.error('保存失败')
  }
}

const isStrongPassword = password => {
  return password.length >= 12
    && /[a-z]/.test(password)
    && /[A-Z]/.test(password)
    && /\d/.test(password)
    && /[^A-Za-z0-9]/.test(password)
}

onMounted(() => {
  load()
})
</script>

<style scoped>
.personal-center { padding: 24px; }
.profile-card { max-width: 720px; margin: 0 auto; }
</style>


