import { get } from './http'
import type {
  PageResult,
  PostDetail,
  PostListQuery,
  PostSummary,
} from './types'

const MAX_PAGE_SIZE = 50

function normalizePage(value?: number): number {
  if (!Number.isFinite(value) || (value ?? 0) < 1) return 1
  return Math.trunc(value as number)
}

/** GET /api/posts?page=&size=&keyword= */
export async function fetchPosts(query: PostListQuery = {}): Promise<PageResult<PostSummary>> {
  const page = normalizePage(query.page)
  const size = Math.min(MAX_PAGE_SIZE, Math.max(1, normalizePage(query.size ?? 8)))
  const keyword = query.keyword?.trim().slice(0, 50) ?? ''

  const params: Record<string, string | number> = { page, size }
  if (keyword) params.keyword = keyword

  return get<PageResult<PostSummary>>('/posts', { params })
}

/** GET /api/posts/{id} —— 每次成功调用会让后端浏览量 +1。 */
export async function fetchPostDetail(id: number): Promise<PostDetail> {
  return get<PostDetail>(`/posts/${id}`)
}
