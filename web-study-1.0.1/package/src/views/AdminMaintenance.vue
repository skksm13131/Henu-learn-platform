<template>
  <div class="maintenance-page">
    <div class="page-header">
      <div>
        <h1 class="page-title">项目维护</h1>
        <p class="page-subtitle">平台数据概览与贡献者名单维护。本页仅管理员可访问。</p>
      </div>
      <el-button type="primary" @click="openCreate">新增贡献者</el-button>
    </div>

    <!-- 数据概览 -->
    <section class="section">
      <h2 class="section-title">平台数据概览</h2>
      <div v-loading="summaryLoading" class="stat-grid">
        <div v-for="item in statItems" :key="item.key" class="stat-card">
          <div class="stat-value">{{ summary[item.key] ?? '-' }}</div>
          <div class="stat-label">{{ item.label }}</div>
          <div v-if="item.hint" class="stat-hint">{{ item.hint }}</div>
        </div>
      </div>
    </section>

    <!-- 快捷入口 -->
    <section class="section">
      <h2 class="section-title">维护入口</h2>
      <div class="shortcut-grid">
        <button
          v-for="entry in shortcuts"
          :key="entry.path"
          type="button"
          class="shortcut-card"
          @click="go(entry.path)"
        >
          <div class="shortcut-title">{{ entry.title }}</div>
          <div class="shortcut-desc">{{ entry.desc }}</div>
        </button>
      </div>
    </section>

    <!-- 贡献者管理 -->
    <section class="section">
      <div class="section-head">
        <h2 class="section-title">贡献者名单</h2>
        <span class="section-note">共 {{ contributors.length }} 条，其中 {{ visibleCount }} 条对外展示</span>
      </div>

      <el-table v-loading="tableLoading" :data="contributors" border stripe size="small">
        <el-table-column prop="sortOrder" label="排序" width="70" align="center" />
        <el-table-column label="展示名" min-width="130">
          <template #default="{ row }">
            <span>{{ row.displayName || '-' }}</span>
            <el-tag v-if="row.username" size="small" type="info" class="inline-tag">
              {{ row.username }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="roleTitle" label="职责" min-width="110" />
        <el-table-column prop="moduleScope" label="负责模块" min-width="160" show-overflow-tooltip />
        <el-table-column prop="grade" label="年级" width="90" />
        <el-table-column prop="joinDate" label="加入时间" width="110" />
        <el-table-column label="展示" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.visible === 1 ? 'success' : 'info'" size="small">
              {{ row.visible === 1 ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="140" align="center">
          <template #default="{ row }">
            <el-button link type="primary" @click="openEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="confirmDelete(row)">删除</el-button>
          </template>
        </el-table-column>
        <template #empty>
          <el-empty description="暂无贡献者，点击右上角新增" />
        </template>
      </el-table>
    </section>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="editingId ? '编辑贡献者' : '新增贡献者'"
      width="620px"
      @closed="resetForm"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="关联账号">
          <el-select
            v-model="form.userId"
            filterable
            clearable
            placeholder="留空表示无平台账号的外部贡献者"
            style="width: 100%"
          >
            <el-option
              v-for="user in userOptions"
              :key="user.userId"
              :label="`${user.displayName || user.username} (${user.username})`"
              :value="user.userId"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="展示名" prop="displayName">
          <el-input
            v-model="form.displayName"
            :placeholder="form.userId ? '留空则取账号的显示名' : '未关联账号时必填'"
            maxlength="100"
            show-word-limit
          />
        </el-form-item>

        <el-form-item label="职责标题">
          <el-input v-model="form.roleTitle" placeholder="如：内容维护、前端开发" maxlength="100" />
        </el-form-item>

        <el-form-item label="负责模块">
          <el-input
            v-model="form.moduleScope"
            placeholder="多个模块用顿号分隔，如：学习卡片、模板管理"
            maxlength="255"
          />
        </el-form-item>

        <el-form-item label="贡献描述">
          <el-input v-model="form.description" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>

        <el-form-item label="GitHub">
          <el-input v-model="form.githubUrl" placeholder="https://github.com/username" maxlength="255" />
        </el-form-item>

        <el-form-item label="加入时间">
          <el-date-picker
            v-model="form.joinDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
          <span class="form-hint">数字越小越靠前</span>
        </el-form-item>

        <el-form-item label="对外展示">
          <el-switch v-model="form.visible" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'
import {
  createAdminContributor,
  deleteAdminContributor,
  getAdminContributors,
  getMaintenanceSummary,
  updateAdminContributor
} from '@/api/contributors'

const router = useRouter()

const summary = ref({})
const summaryLoading = ref(false)
const contributors = ref([])
const tableLoading = ref(false)
const userOptions = ref([])

const dialogVisible = ref(false)
const saving = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const form = reactive({
  userId: null,
  displayName: '',
  roleTitle: '',
  moduleScope: '',
  description: '',
  githubUrl: '',
  joinDate: '',
  sortOrder: 0,
  visible: 1
})

const rules = {
  displayName: [
    {
      validator: (rule, value, callback) => {
        if (!form.userId && !value) {
          callback(new Error('未关联平台账号时必须填写展示名'))
          return
        }
        callback()
      },
      trigger: 'blur'
    }
  ]
}

const statItems = [
  { key: 'learningItemTotal', label: '学习卡片', hint: '含已发布与已归档' },
  { key: 'learningItemPublished', label: '已发布卡片' },
  { key: 'learningItemArchived', label: '已归档卡片' },
  { key: 'userTotal', label: '平台用户' },
  { key: 'userActive', label: '活跃用户' },
  { key: 'adminTotal', label: '管理员' },
  { key: 'assignmentTotal', label: '能力考核' },
  { key: 'assignmentPublished', label: '已发布考核' },
  { key: 'learningRecordTotal', label: '学习记录' },
  { key: 'contributorTotal', label: '贡献者' },
  { key: 'contributorVisible', label: '对外展示贡献者' }
]

const shortcuts = [
  { title: '学习内容管理', desc: '编辑学习卡片、上传实验模板', path: '/content-library' },
  { title: '考核管理', desc: '创建考核、批改与退回提交', path: '/admin-assignments' },
  { title: '用户管理', desc: '创建账号、重置密码、停用账号', path: '/users' },
  { title: '平台学习总览', desc: '查看全平台学习数据与导出', path: '/admin-dashboard' },
  { title: '贡献者页面', desc: '查看前台展示效果', path: '/contributors' }
]

const visibleCount = computed(() => contributors.value.filter(item => item.visible === 1).length)

const go = path => {
  router.push(path).catch(() => {})
}

const loadSummary = async () => {
  summaryLoading.value = true
  try {
    summary.value = await getMaintenanceSummary()
  } catch (error) {
    ElMessage.error(error?.message || '加载平台概览失败')
  } finally {
    summaryLoading.value = false
  }
}

const loadContributors = async () => {
  tableLoading.value = true
  try {
    contributors.value = await getAdminContributors()
  } catch (error) {
    ElMessage.error(error?.message || '加载贡献者名单失败')
  } finally {
    tableLoading.value = false
  }
}

const loadUserOptions = async () => {
  try {
    const page = await request({ url: '/users', method: 'get', params: { page: 1, pageSize: 100 } })
    userOptions.value = page?.records || []
  } catch (error) {
    // 用户列表加载失败不阻塞维护页主体
    userOptions.value = []
  }
}

const resetForm = () => {
  editingId.value = null
  Object.assign(form, {
    userId: null,
    displayName: '',
    roleTitle: '',
    moduleScope: '',
    description: '',
    githubUrl: '',
    joinDate: '',
    sortOrder: 0,
    visible: 1
  })
  formRef.value?.clearValidate()
}

const openCreate = () => {
  resetForm()
  dialogVisible.value = true
}

const openEdit = row => {
  resetForm()
  editingId.value = row.contributorId
  Object.assign(form, {
    userId: row.userId ?? null,
    displayName: row.displayName || '',
    roleTitle: row.roleTitle || '',
    moduleScope: row.moduleScope || '',
    description: row.description || '',
    githubUrl: row.githubUrl || '',
    joinDate: row.joinDate || '',
    sortOrder: row.sortOrder ?? 0,
    visible: row.visible ?? 1
  })
  dialogVisible.value = true
}

const submit = async () => {
  try {
    await formRef.value.validate()
  } catch (error) {
    return
  }

  saving.value = true
  try {
    const payload = {
      userId: form.userId || null,
      displayName: form.displayName || null,
      roleTitle: form.roleTitle || null,
      moduleScope: form.moduleScope || null,
      description: form.description || null,
      githubUrl: form.githubUrl || null,
      joinDate: form.joinDate || null,
      sortOrder: Number(form.sortOrder) || 0,
      visible: form.visible
    }

    if (editingId.value) {
      await updateAdminContributor(editingId.value, payload)
      ElMessage.success('已更新')
    } else {
      await createAdminContributor(payload)
      ElMessage.success('已新增')
    }

    dialogVisible.value = false
    await Promise.all([loadContributors(), loadSummary()])
  } catch (error) {
    ElMessage.error(error?.message || '保存失败')
  } finally {
    saving.value = false
  }
}

const confirmDelete = async row => {
  const name = row.displayName || row.username || `#${row.contributorId}`
  try {
    await ElMessageBox.confirm(`确定删除贡献者「${name}」吗？此操作不可撤销。`, '确认删除', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消'
    })
  } catch (error) {
    return
  }

  try {
    await deleteAdminContributor(row.contributorId)
    ElMessage.success('已删除')
    await Promise.all([loadContributors(), loadSummary()])
  } catch (error) {
    ElMessage.error(error?.message || '删除失败')
  }
}

onMounted(() => {
  loadSummary()
  loadContributors()
  loadUserOptions()
})
</script>

<style scoped>
.maintenance-page {
  padding: 24px 32px 40px;
}

.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 24px;
}

.page-title {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
  color: #1f2d3d;
}

.page-subtitle {
  margin: 0;
  font-size: 14px;
  color: #7a8699;
}

.section {
  margin-bottom: 32px;
}

.section-head {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 12px;
}

.section-title {
  margin: 0 0 12px;
  font-size: 16px;
  font-weight: 600;
  color: #1f2d3d;
}

.section-head .section-title {
  margin-bottom: 0;
}

.section-note {
  font-size: 12px;
  color: #909399;
}

.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 14px;
}

.stat-card {
  padding: 16px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #1f2d3d;
  line-height: 1.2;
}

.stat-label {
  margin-top: 6px;
  font-size: 13px;
  color: #606266;
}

.stat-hint {
  margin-top: 2px;
  font-size: 11px;
  color: #a8abb2;
}

.shortcut-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 14px;
}

.shortcut-card {
  padding: 16px;
  text-align: left;
  background: #ffffff;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.shortcut-card:hover {
  border-color: #409eff;
  box-shadow: 0 4px 14px rgba(64, 158, 255, 0.12);
}

.shortcut-title {
  font-size: 14px;
  font-weight: 600;
  color: #1f2d3d;
}

.shortcut-desc {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
}

.inline-tag {
  margin-left: 6px;
}

.form-hint {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
