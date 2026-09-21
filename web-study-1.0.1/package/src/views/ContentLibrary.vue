<template>
  <div class="content-library-page">
    <div class="page-shell">
      <section class="hero-card">
        <div>
          <h1>学习内容管理</h1>
          <p>在这里创建学习卡片、维护步骤、上传 notebook 模板，并控制学生可见的发布状态。</p>
        </div>
        <el-button type="primary" size="large" @click="openCreateDialog">
          新建学习卡片
        </el-button>
      </section>

      <section class="stats-grid">
        <article class="stat-card">
          <span>卡片总数</span>
          <strong>{{ stats.total }}</strong>
        </article>
        <article class="stat-card">
          <span>草稿</span>
          <strong>{{ draftCount }}</strong>
        </article>
        <article class="stat-card">
          <span>已发布</span>
          <strong>{{ publishedCount }}</strong>
        </article>
        <article class="stat-card">
          <span>已归档</span>
          <strong>{{ archivedCount }}</strong>
        </article>
      </section>

      <section class="panel">
        <div class="panel-toolbar">
          <el-input
            v-model="filters.keyword"
            placeholder="搜索标题或摘要"
            clearable
            style="width: 240px"
            @keyup.enter="handleFilter"
          />
          <el-select v-model="filters.status" placeholder="状态" clearable style="width: 160px">
            <el-option label="全部状态" value="" />
            <el-option label="草稿" value="DRAFT" />
            <el-option label="已发布" value="PUBLISHED" />
            <el-option label="已归档" value="ARCHIVED" />
          </el-select>
          <el-select v-model="filters.category" placeholder="分类" clearable style="width: 180px">
            <el-option label="全部分类" value="" />
            <el-option
              v-for="category in categories"
              :key="category"
              :label="category"
              :value="category"
            />
          </el-select>
          <el-button @click="handleFilter">筛选</el-button>
        </div>

        <el-table v-loading="loading" :data="items" stripe>
          <el-table-column prop="title" label="标题" min-width="220" />
          <el-table-column prop="category" label="分类" min-width="130" />
          <el-table-column prop="difficulty" label="难度" min-width="100" />
          <el-table-column prop="duration" label="时长" min-width="100" />
          <el-table-column label="状态" min-width="110">
            <template #default="{ row }">
              <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="步骤数" min-width="90">
            <template #default="{ row }">
              {{ row.stepCount || 0 }}
            </template>
          </el-table-column>
          <el-table-column label="模板" min-width="100">
            <template #default="{ row }">
              <el-tag :type="row.templateAvailable ? 'success' : 'info'">
                {{ row.templateAvailable ? '已上传' : '未上传' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="updatedAt" label="更新时间" min-width="180" />
          <el-table-column label="操作" min-width="360" fixed="right">
            <template #default="{ row }">
              <div class="row-actions">
                <el-button size="small" @click="openEditDialog(row.id)">编辑</el-button>
                <el-button size="small" @click="openUploadDialog(row)">模板管理</el-button>
                <el-button
                  v-if="row.status !== 'PUBLISHED'"
                  size="small"
                  type="success"
                  :disabled="!row.templateAvailable"
                  @click="changeStatus(row, 'PUBLISHED')"
                >
                  发布
                </el-button>
                <el-button
                  v-if="row.status === 'PUBLISHED'"
                  size="small"
                  type="warning"
                  @click="changeStatus(row, 'DRAFT')"
                >
                  撤回
                </el-button>
                <el-button
                  v-if="row.status !== 'ARCHIVED'"
                  size="small"
                  type="info"
                  @click="changeStatus(row, 'ARCHIVED')"
                >
                  归档
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          class="pagination"
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="fetchItems"
          @size-change="handlePageSizeChange"
        />
      </section>
    </div>

    <el-dialog
      v-model="editorVisible"
      :title="editingId ? '编辑学习卡片' : '新建学习卡片'"
      width="960px"
      destroy-on-close
    >
      <el-form label-position="top">
        <div class="form-grid">
          <el-form-item label="标题">
            <el-input v-model="form.title" placeholder="请输入标题" />
          </el-form-item>
          <el-form-item label="分类">
            <el-input v-model="form.category" placeholder="如：现代密码学" />
          </el-form-item>
          <el-form-item label="难度">
            <el-select v-model="form.difficulty" placeholder="请选择难度">
              <el-option label="简单" value="简单" />
              <el-option label="中等" value="中等" />
              <el-option label="困难" value="困难" />
            </el-select>
          </el-form-item>
          <el-form-item label="预计时长">
            <el-input v-model="form.duration" placeholder="如：2小时" />
          </el-form-item>
        </div>

        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="前置要求">
          <el-input v-model="form.prerequisites" type="textarea" :rows="2" />
        </el-form-item>

        <el-form-item label="学习目标">
          <el-input
            v-model="form.objectivesText"
            type="textarea"
            :rows="4"
            placeholder="每行一个学习目标"
          />
        </el-form-item>

        <section class="editor-section">
          <div class="section-header">
            <h3>特色亮点</h3>
            <el-button link type="primary" @click="addFeature">新增亮点</el-button>
          </div>
          <div v-if="!form.features.length" class="empty-hint">还没有配置亮点</div>
          <div v-for="(feature, index) in form.features" :key="`feature-${index}`" class="feature-card">
            <el-input v-model="feature.title" placeholder="亮点标题" />
            <el-input v-model="feature.description" placeholder="亮点描述" type="textarea" :rows="2" />
            <el-button link type="danger" @click="removeFeature(index)">删除</el-button>
          </div>
        </section>

        <section class="editor-section">
          <div class="section-header">
            <h3>学习步骤</h3>
            <el-button link type="primary" @click="addStep">新增步骤</el-button>
          </div>
          <div v-if="!form.steps.length" class="empty-hint">还没有配置步骤</div>
          <div v-for="(step, index) in form.steps" :key="`step-${index}`" class="step-card">
            <div class="step-card-header">
              <span>步骤 {{ index + 1 }}</span>
              <el-button link type="danger" @click="removeStep(index)">删除</el-button>
            </div>
            <div class="form-grid">
              <el-form-item label="序号">
                <el-input-number v-model="step.stepNo" :min="1" :step="1" />
              </el-form-item>
              <el-form-item label="标题">
                <el-input v-model="step.title" placeholder="步骤标题" />
              </el-form-item>
            </div>
            <el-form-item label="步骤说明">
              <el-input v-model="step.description" type="textarea" :rows="3" />
            </el-form-item>
            <el-form-item label="提示">
              <el-input v-model="step.tip" type="textarea" :rows="2" />
            </el-form-item>
            <el-form-item label="代码 / 示例">
              <el-input v-model="step.code" type="textarea" :rows="6" />
            </el-form-item>
          </div>
        </section>
      </el-form>

      <template #footer>
        <el-button @click="editorVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveItem">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="uploadVisible"
      title="实验模板管理"
      width="560px"
      destroy-on-close
    >
      <div v-if="uploadTarget" class="template-manager">
        <div class="template-status-card">
          <div>
            <span class="template-status-label">当前卡片</span>
            <strong>{{ uploadTarget.title }}</strong>
          </div>
          <el-tag :type="uploadTarget.templateAvailable ? 'success' : 'info'">
            {{ uploadTarget.templateAvailable ? '已上传模板' : '未上传模板' }}
          </el-tag>
        </div>
        <div class="template-meta">
          <div>
            <span>模板路径</span>
            <strong>{{ uploadTarget.templatePath || '暂无' }}</strong>
          </div>
          <div>
            <span>更新时间</span>
            <strong>{{ uploadTarget.updatedAt || '暂无' }}</strong>
          </div>
        </div>
        <p class="upload-intro">
          上传新的 `.ipynb` 文件会替换该卡片的默认实验模板。学生首次进入实验会生成个人副本；学生在实验页点击“恢复默认模板”时，也会重新拉取这里维护的模板。
        </p>
      </div>
      <el-upload
        drag
        action="#"
        :auto-upload="false"
        :limit="1"
        accept=".ipynb"
        :on-change="handleTemplateSelect"
        :on-remove="handleTemplateRemove"
      >
        <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
        <div class="el-upload__text">拖拽文件到这里，或点击选择 `.ipynb` 模板</div>
      </el-upload>

      <template #footer>
        <el-button @click="closeUploadDialog">取消</el-button>
        <el-button type="primary" :loading="uploading" @click="submitTemplateUpload">
          {{ uploadTarget?.templateAvailable ? '替换模板' : '上传模板' }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import {
  createAdminLearningItem,
  getAdminLearningItem,
  getAdminLearningItemStats,
  getAdminLearningItems,
  updateAdminLearningItem,
  updateAdminLearningItemStatus,
  uploadAdminLearningItemTemplate
} from '@/api/learningContentAdmin'

const loading = ref(false)
const saving = ref(false)
const uploading = ref(false)
const items = ref([])
const editorVisible = ref(false)
const uploadVisible = ref(false)
const editingId = ref(null)
const uploadTarget = ref(null)
const selectedTemplateFile = ref(null)

const filters = reactive({
  keyword: '',
  status: '',
  category: ''
})

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const stats = reactive({
  total: 0,
  draft: 0,
  published: 0,
  archived: 0
})

const createEmptyForm = () => ({
  title: '',
  summary: '',
  category: '',
  difficulty: '',
  duration: '',
  prerequisites: '',
  objectivesText: '',
  features: [],
  steps: []
})

const form = reactive(createEmptyForm())

const categories = computed(() => {
  const values = new Set()
  items.value.forEach(item => {
    if (item.category) {
      values.add(item.category)
    }
  })
  return Array.from(values)
})

const draftCount = computed(() => stats.draft)
const publishedCount = computed(() => stats.published)
const archivedCount = computed(() => stats.archived)

const resetForm = () => {
  Object.assign(form, createEmptyForm())
}

const mapDetailToForm = detail => {
  resetForm()
  form.title = detail.title || ''
  form.summary = detail.summary || ''
  form.category = detail.category || ''
  form.difficulty = detail.difficulty || ''
  form.duration = detail.duration || ''
  form.prerequisites = detail.prerequisites || ''
  form.objectivesText = Array.isArray(detail.objectives) ? detail.objectives.join('\n') : ''
  form.features = Array.isArray(detail.features)
    ? detail.features.map(feature => ({
        title: feature.title || '',
        description: feature.description || ''
      }))
    : []
  form.steps = Array.isArray(detail.steps)
    ? detail.steps.map(step => ({
        stepNo: step.stepNo || 1,
        title: step.title || '',
        description: step.description || '',
        tip: step.tip || '',
        code: step.code || ''
      }))
    : []
}

const buildPayload = () => ({
  title: form.title,
  summary: form.summary,
  category: form.category,
  difficulty: form.difficulty,
  duration: form.duration,
  prerequisites: form.prerequisites,
  objectives: form.objectivesText
    .split('\n')
    .map(value => value.trim())
    .filter(Boolean),
  features: form.features
    .map(feature => ({
      title: feature.title?.trim() || '',
      description: feature.description?.trim() || ''
    }))
    .filter(feature => feature.title || feature.description),
  steps: form.steps
    .map((step, index) => ({
      stepNo: step.stepNo || index + 1,
      title: step.title?.trim() || '',
      description: step.description?.trim() || '',
      tip: step.tip?.trim() || '',
      code: step.code || ''
    }))
    .filter(step => step.title)
})

const fetchItems = async () => {
  loading.value = true
  try {
    const params = {
      keyword: filters.keyword || undefined,
      status: filters.status || undefined,
      category: filters.category || undefined,
      page: pagination.page,
      pageSize: pagination.pageSize
    }
    const [pageData, statData] = await Promise.all([
      getAdminLearningItems(params),
      getAdminLearningItemStats({
        keyword: filters.keyword || undefined,
        status: filters.status || undefined,
        category: filters.category || undefined
      })
    ])
    items.value = pageData.records || []
    pagination.total = pageData.total || 0
    stats.total = Number(statData.total || 0)
    stats.draft = Number(statData.draft || 0)
    stats.published = Number(statData.published || 0)
    stats.archived = Number(statData.archived || 0)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.page = 1
  fetchItems()
}

const handlePageSizeChange = () => {
  pagination.page = 1
  fetchItems()
}

const openCreateDialog = () => {
  editingId.value = null
  resetForm()
  editorVisible.value = true
}

const openEditDialog = async id => {
  const detail = await getAdminLearningItem(id)
  editingId.value = id
  mapDetailToForm(detail)
  editorVisible.value = true
}

const saveItem = async () => {
  if (!form.title.trim()) {
    ElMessage.warning('请先填写标题')
    return
  }

  saving.value = true
  try {
    const payload = buildPayload()
    if (editingId.value) {
      await updateAdminLearningItem(editingId.value, payload)
      ElMessage.success('学习卡片已更新')
    } else {
      const created = await createAdminLearningItem(payload)
      editingId.value = created.id
      ElMessage.success('学习卡片已创建，请继续上传模板后发布')
    }
    editorVisible.value = false
    await fetchItems()
  } finally {
    saving.value = false
  }
}

const changeStatus = async (row, status) => {
  try {
    const actionText = statusText(status)
    await ElMessageBox.confirm(`确定将“${row.title}”调整为${actionText}吗？`, '状态确认', {
      type: 'warning'
    })
    await updateAdminLearningItemStatus(row.id, status)
    ElMessage.success('状态已更新')
    await fetchItems()
  } catch (error) {
    // user cancelled
  }
}

const openUploadDialog = async row => {
  uploadTarget.value = row
  selectedTemplateFile.value = null
  uploadVisible.value = true
  try {
    const detail = await getAdminLearningItem(row.id)
    uploadTarget.value = {
      ...row,
      ...detail
    }
  } catch (error) {
    // keep list row information if detail loading fails
  }
}

const closeUploadDialog = () => {
  uploadVisible.value = false
  uploadTarget.value = null
  selectedTemplateFile.value = null
}

const handleTemplateSelect = uploadFile => {
  selectedTemplateFile.value = uploadFile.raw
}

const handleTemplateRemove = () => {
  selectedTemplateFile.value = null
}

const submitTemplateUpload = async () => {
  if (!uploadTarget.value?.id) {
    ElMessage.warning('请先选择要上传的卡片')
    return
  }
  if (!selectedTemplateFile.value) {
    ElMessage.warning('请先选择 notebook 模板')
    return
  }
  uploading.value = true
  try {
    await uploadAdminLearningItemTemplate(uploadTarget.value.id, selectedTemplateFile.value)
    ElMessage.success('模板上传成功')
    closeUploadDialog()
    await fetchItems()
  } finally {
    uploading.value = false
  }
}

const addFeature = () => {
  form.features.push({ title: '', description: '' })
}

const removeFeature = index => {
  form.features.splice(index, 1)
}

const addStep = () => {
  form.steps.push({
    stepNo: form.steps.length + 1,
    title: '',
    description: '',
    tip: '',
    code: ''
  })
}

const removeStep = index => {
  form.steps.splice(index, 1)
}

const statusText = status => {
  if (status === 'PUBLISHED') return '已发布'
  if (status === 'ARCHIVED') return '已归档'
  return '草稿'
}

const statusTagType = status => {
  if (status === 'PUBLISHED') return 'success'
  if (status === 'ARCHIVED') return 'info'
  return 'warning'
}

onMounted(() => {
  fetchItems()
})
</script>

<style scoped>
.content-library-page {
  min-height: 100%;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.16), transparent 30%),
    linear-gradient(180deg, #f8fbff 0%, #eef4ff 100%);
  padding: 24px;
}

.page-shell {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hero-card,
.panel,
.stat-card {
  background: rgba(255, 255, 255, 0.92);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
  backdrop-filter: blur(16px);
}

.hero-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  border-radius: 24px;
  padding: 28px 32px;
}

.hero-card h1 {
  margin: 0 0 8px;
  font-size: 30px;
  color: #102a43;
}

.hero-card p {
  margin: 0;
  color: #486581;
  line-height: 1.7;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  border-radius: 20px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stat-card span {
  color: #486581;
  font-size: 14px;
}

.stat-card strong {
  font-size: 32px;
  color: #0f172a;
}

.panel {
  border-radius: 24px;
  padding: 24px;
}

.panel-toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.pagination {
  justify-content: flex-end;
  margin-top: 16px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.editor-section {
  margin-top: 20px;
  border: 1px solid #dbe7f3;
  border-radius: 18px;
  padding: 18px;
  background: #f8fbff;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.section-header h3 {
  margin: 0;
  font-size: 18px;
  color: #102a43;
}

.feature-card,
.step-card {
  border: 1px solid #dbe7f3;
  background: #ffffff;
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 14px;
}

.feature-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.step-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  color: #243b53;
  font-weight: 600;
}

.empty-hint {
  padding: 14px 0;
  color: #829ab1;
}

.upload-intro {
  color: #486581;
  line-height: 1.7;
  margin-bottom: 18px;
}

.template-manager {
  margin-bottom: 18px;
}

.template-status-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #dbe7f3;
  background: #f8fbff;
}

.template-status-card strong {
  display: block;
  margin-top: 6px;
  color: #102a43;
  font-size: 16px;
  line-height: 1.5;
}

.template-status-label,
.template-meta span {
  color: #829ab1;
  font-size: 12px;
}

.template-meta {
  display: grid;
  grid-template-columns: 1fr;
  gap: 10px;
  margin: 14px 0;
}

.template-meta div {
  padding: 12px 14px;
  border-radius: 12px;
  background: #ffffff;
  border: 1px solid #e4edf7;
}

.template-meta strong {
  display: block;
  margin-top: 4px;
  color: #243b53;
  font-size: 13px;
  font-weight: 600;
  word-break: break-all;
}

@media (max-width: 980px) {
  .stats-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }

  .hero-card {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
