export interface LearnSummary {
  completionRate: number
  completedCount: number
  totalCount: number
  inProgressCount: number
  learnDurationSec: number
  lastActiveTime?: string | null
}

export interface LearnCategoryProgress {
  category: string
  completedCount: number
  totalCount: number
  learnDurationSec: number
}

export interface LearnDifficultyProgress {
  difficulty: string
  completedCount: number
  totalCount: number
  learnDurationSec: number
}

export interface LearnCompletionTrend {
  date: string
  completedCount: number
}

export interface LearnRecentItem {
  itemPk: number
  title: string
  category?: string | null
  difficulty?: string | null
  learnDurationSec: number
  updatedAt?: string | null
  completeTime?: string | null
  completeRemark?: string | null
}

export interface LearnRecommendation {
  itemPk: number
  title: string
  category?: string | null
  difficulty?: string | null
  learnDurationSec: number
  lastLearnTime?: string | null
}

export interface DashboardDTO {
  summary: LearnSummary
  byCategory: LearnCategoryProgress[]
  byDifficulty: LearnDifficultyProgress[]
  completionTrend: LearnCompletionTrend[]
  recent: LearnRecentItem[]
  recommendation?: LearnRecommendation | null
}
