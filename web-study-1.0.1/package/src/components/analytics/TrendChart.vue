<template>
  <div class="trend-chart">
    <div ref="chartRef" class="trend-canvas"></div>
    <div v-if="empty" class="trend-empty">暂无趋势数据</div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  labels: { type: Array, default: () => [] },
  values: { type: Array, default: () => [] },
  empty: { type: Boolean, default: false }
})

const chartRef = ref(null)
let chartInstance = null

const buildOption = () => ({
  tooltip: { trigger: 'axis' },
  grid: { left: 24, right: 24, top: 20, bottom: 24, containLabel: true },
  xAxis: {
    type: 'category',
    data: props.labels,
    axisLine: { lineStyle: { color: '#E5E7EB' } },
    axisLabel: { color: '#6B7280', fontSize: 11 }
  },
  yAxis: {
    type: 'value',
    axisLine: { show: false },
    splitLine: { lineStyle: { color: '#EEF2F7' } },
    axisLabel: { color: '#6B7280', fontSize: 11 }
  },
  series: [
    {
      type: 'line',
      smooth: true,
      data: props.values,
      showSymbol: false,
      lineStyle: { color: '#1E88E5', width: 3 },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(30,136,229,0.25)' },
          { offset: 1, color: 'rgba(30,136,229,0.03)' }
        ])
      }
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

watch(() => [props.labels, props.values], renderChart, { deep: true })
</script>

<style scoped>
.trend-chart {
  position: relative;
  width: 100%;
  height: 260px;
}

.trend-canvas {
  width: 100%;
  height: 100%;
}

.trend-empty {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #9ca3af;
  font-size: 13px;
}
</style>
