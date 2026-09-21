<template>
  <div class="assessment-page">
    <div class="page-shell">
      <section class="hero-card">
        <div class="hero-copy">
          <h1>大数据智能处理团队能力考核</h1>
        </div>
        <div class="hero-actions">
          <el-input
            v-model="keyword"
            clearable
            placeholder="搜索考核标题或说明"
            class="search-input"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
          <el-button :icon="Refresh" plain @click="loadAssignments">刷新</el-button>
        </div>
      </section>

      <section class="assessment-panel">
        <div class="panel-head">
          <div>
            <h2>我的考核</h2>
            <p>选择一项考核查看要求，填写文字答案或上传材料后提交。</p>
          </div>
          <el-tag type="info" effect="plain">共 {{ total }} 项</el-tag>
        </div>

        <div v-loading="loading" class="assessment-list">
          <el-empty v-if="!assignments.length && !loading" description="暂无可参与考核" />
          <article
            v-for="item in assignments"
            :key="item.assignmentId"
            class="assessment-card"
            @click="openDetail(item.assignmentId)"
          >
            <div class="card-main">
              <div class="card-topline">
                <el-tag size="small" effect="plain">{{ item.category || '综合考核' }}</el-tag>
                <el-tag :type="submissionTag(item.mySubmission?.status)" size="small">
                  {{ submissionText(item.mySubmission?.status) }}
                </el-tag>
              </div>
              <h3>{{ item.title }}</h3>
              <p>{{ item.description || '暂无考核说明' }}</p>
              <div class="meta-grid">
                <span>截止：{{ formatTime(item.deadline) }}</span>
                <span>满分：{{ item.totalScore || 100 }}</span>
                <span>得分：{{ item.mySubmission?.score ?? '-' }}</span>
              </div>
            </div>
            <div class="card-action">
              <el-button type="primary" plain @click.stop="openDetail(item.assignmentId)">
                进入考核
              </el-button>
            </div>
          </article>
        </div>

        <el-pagination
          class="pagination"
          layout="total, prev, pager, next"
          :total="total"
          :page-size="pageSize"
          v-model:current-page="page"
          @current-change="loadAssignments"
        />
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { Refresh, Search } from '@element-plus/icons-vue'
import { getAssignments } from '@/api/assignments'

const router = useRouter()
const assignments = ref([])
const loading = ref(false)
const keyword = ref('')
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const loadAssignments = async () => {
  loading.value = true
  try {
    const params = { keyword: keyword.value, page: page.value, pageSize: pageSize.value }
    const data = await getAssignments(params)
    assignments.value = data.records || []
    total.value = data.total || 0
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  page.value = 1
  loadAssignments()
}

const openDetail = id => {
  router.push(`/assignments/${id}`)
}

const formatTime = value => value ? String(value).replace('T', ' ').slice(0, 16) : '-'
const submissionText = status => ({
  DRAFT: '未提交',
  SUBMITTED: '已提交',
  LATE: '逾期提交',
  GRADED: '已评分',
  RETURNED: '已退回'
}[status] || '未提交')
const submissionTag = status => ({
  SUBMITTED: 'warning',
  LATE: 'danger',
  GRADED: 'success',
  RETURNED: 'info'
}[status] || '')

onMounted(loadAssignments)
</script>

<style scoped>
.assessment-page {
  min-height: 100%;
  background:
    radial-gradient(circle at top left, rgba(37, 99, 235, 0.14), transparent 30%),
    linear-gradient(180deg, #f8fbff 0%, #eef4ff 100%);
  padding: 24px;
}

.page-shell {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.hero-card,
.assessment-panel,
.assessment-card {
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid rgba(148, 163, 184, 0.18);
  box-shadow: 0 18px 40px rgba(15, 23, 42, 0.08);
}

.hero-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  border-radius: 16px;
  padding: 16px 20px;
}

.hero-copy {
  max-width: 720px;
}

.hero-card h1 {
  margin: 0;
  color: #102a43;
  font-size: 22px;
  line-height: 1.35;
}

.hero-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.search-input {
  width: 280px;
}

.assessment-panel {
  border-radius: 24px;
  padding: 24px;
}

.panel-head {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;
}

.panel-head h2 {
  margin: 0 0 6px;
  color: #102a43;
  font-size: 22px;
}

.panel-head p {
  margin: 0;
  color: #64748b;
}

.assessment-list {
  min-height: 260px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.assessment-card {
  min-height: 132px;
  border-radius: 18px;
  padding: 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  cursor: pointer;
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

.assessment-card:hover {
  transform: translateY(-2px);
  border-color: rgba(37, 99, 235, 0.42);
  box-shadow: 0 20px 44px rgba(37, 99, 235, 0.12);
}

.card-topline,
.card-action {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.card-main {
  min-width: 0;
  flex: 1;
}

.assessment-card h3 {
  margin: 12px 0 8px;
  color: #102a43;
  font-size: 18px;
  line-height: 1.45;
}

.assessment-card p {
  margin: 0;
  color: #486581;
  line-height: 1.65;
  display: -webkit-box;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.meta-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
  margin-top: 16px;
  color: #64748b;
  font-size: 13px;
}

.meta-grid span {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.pagination {
  justify-content: flex-end;
  margin-top: 18px;
}

@media (max-width: 1100px) {
  .hero-card {
    flex-direction: column;
    align-items: flex-start;
  }

  .hero-actions,
  .search-input {
    width: 100%;
  }

  .assessment-card {
    align-items: stretch;
    flex-direction: column;
    gap: 16px;
  }
}
</style>
