import { get } from './http'
import type { HomeData, HomeRaw } from './types'

/**
 * GET /api/home
 *
 * 后端一次返回首页三块数据：
 * - latestPosts：最新 5 篇已发布文章
 * - lastestComments / latestComments：最新 5 条已通过评论
 * - friendLinks：可见友链，按 sort_order 升序
 *
 * 注意：后端 Home record 当前字段名是 lastestComments（拼写问题），
 * 而接口文档写的是 latestComments。这里做兼容，推荐后端后续统一命名。
 */
export async function fetchHomeData(): Promise<HomeData> {
  const raw = await get<HomeRaw>('/home')

  return {
    latestPosts: Array.isArray(raw?.latestPosts) ? raw.latestPosts : [],
    latestComments: Array.isArray(raw?.latestComments)
      ? raw.latestComments
      : Array.isArray(raw?.lastestComments)
        ? raw.lastestComments
        : [],
    friendLinks: Array.isArray(raw?.friendLinks) ? raw.friendLinks : [],
  }
}
