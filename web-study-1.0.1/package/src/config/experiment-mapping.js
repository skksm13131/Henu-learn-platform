/**
 * 系统到实验编号的映射配置
 * 每个系统对应一个实验编号
 */
export const EXPERIMENT_MAPPING = {
  // 数据库管理系统 - 对应一个整体实验
  database: 'exp-java-005',
  
  // 综合安全服务系统 - 对应一个整体实验
  security: 'exp-java-003',
  
  // 高效并行服务系统 - 对应一个整体实验
  parallel: 'exp-java-004',
  
  // 全链路密码仿真系统 - 对应一个整体实验
  crypto: 'exp-java-001'
}

/**
 * 根据系统类型获取对应的实验编号
 * @param {string} systemType - 系统类型: 'database' | 'security' | 'parallel' | 'crypto'
 * @returns {string} 实验编号
 */
export function getExperimentIdBySystem(systemType) {
  return EXPERIMENT_MAPPING[systemType] || 'exp-java-001'
}

