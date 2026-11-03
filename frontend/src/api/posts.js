import { get } from './http'

const MAX_PAGE_SIZE = 50

function normalizePage(value) {
  if (!Number.isFinite(value) || value < 1) return 1
  return Math.trunc(value)
}

export async function fetchPosts(query = {}) {
  const options = query || {}  //如果query为空对象，则使用空对象作为默认值
  const page = normalizePage(options.page)
  const size = Math.min(MAX_PAGE_SIZE, Math.max(1, normalizePage(options.size ?? 9)))
  const keyword = options.keyword?.trim().slice(0, 50) ?? ''

  const params = { page, size }
  if (keyword) params.keyword = keyword

  return get('/posts', { params })  //向后端发送请求，获取文章列表数据
}

export async function fetchPostDetail(id) {
  return get(`/posts/${id}`)
}
