import { get, post } from './http'

export async function fetchPostComments(postId, query = {}) {
  const options = query || {}
  const page = Math.max(1, Math.trunc(options.page ?? 1))
  const size = Math.min(50, Math.max(1, Math.trunc(options.size ?? 10)))

  return get(`/posts/${postId}/comments`, {
    params: { page, size },
  })
}

export function submitComment(payload) {
  return post('/comments', payload)
}
