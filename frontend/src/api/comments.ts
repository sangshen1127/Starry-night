import { get, post, type MutationResult } from './http'
import type {
  CommentCreateRequest,
  CommentListQuery,
  PageResult,
  PublicComment,
} from './types'

/** GET /api/posts/{id}/comments?page=&size= —— 只返回已通过评论。 */
export async function fetchPostComments(
  postId: number,
  query: CommentListQuery = {},
): Promise<PageResult<PublicComment>> {
  const page = Math.max(1, Math.trunc(query.page ?? 1))
  const size = Math.min(50, Math.max(1, Math.trunc(query.size ?? 10)))

  return get<PageResult<PublicComment>>(`/posts/${postId}/comments`, {
    params: { page, size },
  })
}

/** POST /api/comments —— 入库为 pending，后端把提示放在 message。 */
export function submitComment(
  payload: CommentCreateRequest,
): Promise<MutationResult<null>> {
  return post<null>('/comments', payload)
}
