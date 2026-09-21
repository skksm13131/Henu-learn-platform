export const learnAnalyticsMock = {
  summary: {
    completionRate: 68,
    completedCount: 34,
    totalCount: 50,
    inProgressCount: 9,
    learnDurationSec: 15240,
    lastActiveTime: '2025-01-02T16:20:00'
  },
  byCategory: [
    { category: '古典密码学', completedCount: 12, totalCount: 16, learnDurationSec: 4200 },
    { category: '密码协议设计专题', completedCount: 6, totalCount: 10, learnDurationSec: 3600 },
    { category: '对称加密基础', completedCount: 8, totalCount: 12, learnDurationSec: 2800 },
    { category: '现代密码学', completedCount: 4, totalCount: 8, learnDurationSec: 1900 },
    { category: '区块链安全', completedCount: 4, totalCount: 4, learnDurationSec: 2740 }
  ],
  byDifficulty: [
    { difficulty: '简单', completedCount: 12, totalCount: 18, learnDurationSec: 3600 },
    { difficulty: '中等', completedCount: 16, totalCount: 22, learnDurationSec: 6200 },
    { difficulty: '困难', completedCount: 6, totalCount: 10, learnDurationSec: 5440 }
  ],
  completionTrend: [
    { date: '2024-12-27', completedCount: 1 },
    { date: '2024-12-28', completedCount: 0 },
    { date: '2024-12-29', completedCount: 2 },
    { date: '2024-12-30', completedCount: 3 },
    { date: '2024-12-31', completedCount: 1 },
    { date: '2025-01-01', completedCount: 4 },
    { date: '2025-01-02', completedCount: 2 }
  ],
  recent: [
    {
      itemPk: 834729,
      title: '凯撒密码 (Caesar Cipher)',
      category: '古典密码学',
      difficulty: '中等',
      learnDurationSec: 3200,
      updatedAt: '2025-01-02T16:20:00',
      completeTime: '2025-01-02T16:05:00',
      completeRemark: '理解到位，准备做练习题'
    },
    {
      itemPk: 873492,
      title: '仿射密码 (Affine Cipher)',
      category: '古典密码学',
      difficulty: '中等',
      learnDurationSec: 1800,
      updatedAt: '2025-01-01T21:10:00',
      completeTime: null,
      completeRemark: null
    }
  ],
  recommendation: {
    itemPk: 857291,
    title: '跨链协议（Cross-Chain Protocols）',
    category: '密码协议设计专题',
    difficulty: '中等',
    learnDurationSec: 1200,
    lastLearnTime: '2025-01-01T18:30:00'
  }
}
