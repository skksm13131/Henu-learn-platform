<template>
  <div class="donut-chart">
    <div ref="chartRef" class="donut-canvas"></div>
    <div v-if="empty" class="donut-empty">暂无数据</div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  value: { type: Number, default: 0 },
  label: { type: String, default: '' },
  empty: { type: Boolean, default: false },
  completed: { type: Number, default: 0 },
  inProgress: { type: Number, default: 0 },
  total: { type: Number, default: 0 }
})

const chartRef = ref(null)
let chartInstance = null

const getSeriesData = () => {
  if (props.total > 0) {
    const completed = Math.max(0, props.completed)
    const inProgress = Math.max(0, props.inProgress)
    const remaining = Math.max(0, props.total - completed - inProgress)
    return [
      { value: completed, name: '完成' },
      { value: inProgress, name: '学习中' },
      { value: remaining, name: '未完成' }
    ]
  }
  return [
    { value: props.value, name: '完成' },
    { value: Math.max(0, 100 - props.value), name: '未完成' }
  ]
}

const buildOption = () => ({
  tooltip: { trigger: 'item' },
  series: [
    {
      type: 'pie',
      radius: ['70%', '88%'],
      avoidLabelOverlap: false,
      label: { show: false },
      emphasis: { scale: true },
      data: getSeriesData(),
      color: ['#1E88E5', '#60a5fa', '#E5E7EB']
    }
  ]
})

const renderChart = () => {
  if (!chartRef.value) return
  if (!chartInstance) {
    chartInstance = echarts.init(chartRef.value)
  }
  chartInstance.setOption(buildOption(), true)
}

const resizeChart = () => {
  if (chartInstance) chartInstance.resize()
}

onMounted(() => {
  renderChart()
  window.addEventListener('resize', resizeChart)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeChart)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})

watch(() => [props.value, props.completed, props.inProgress, props.total], renderChart)
</script>

<style scoped>
.donut-chart {
  position: relative;
  width: 200px;
  height: 200px;
}

.donut-canvas {
  width: 100%;
  height: 100%;
}

.donut-empty {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 13px;
}
</style>
