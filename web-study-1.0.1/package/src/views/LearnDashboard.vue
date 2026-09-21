<template>
  <div class="dashboard-page">
    <div class="dashboard-container">
      <div class="dashboard-header">
        <div>
          <h1>学习数据面板</h1>
          <p>查看自己的学习进度、近期活动和推荐内容。</p>
        </div>
        <div class="header-actions">
          <el-button type="primary" plain @click="goHome">
            <el-icon><HomeFilled /></el-icon>
            返回首页
          </el-button>
          <el-button type="primary" @click="goLearningCenter">
            返回学习中心
          </el-button>
        </div>
      </div>

      <DashboardSection />
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'
import DashboardSection from '@/components/knowledge/DashboardSection.vue'

const router = useRouter()

const goHome = () => {
  router.push('/').catch(() => {})
}

const goLearningCenter = () => {
  router.push('/knowledge').catch(() => {})
}
</script>

<style scoped>
.dashboard-page {
  padding: 24px 28px 40px;
  background: #f8fafc;
  min-height: 100%;
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

.dashboard-container {
  max-width: 1180px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 16px;
}

.dashboard-header h1 {
  margin: 0;
  font-size: 24px;
  color: #111827;
}

.dashboard-header p {
  margin: 6px 0 0;
  color: #6b7280;
}

.header-actions {
  display: flex;
  gap: 12px;
}

@media (max-width: 900px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-wrap: wrap;
  }
}
</style>
