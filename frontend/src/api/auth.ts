import { get, post, type MutationResult } from './http'
import type { LoginRequest, UserInfo } from './types'

/** GET /api/auth/me —— 未登录返回 401，由 auth store 视为游客。 */
export function fetchCurrentUser(): Promise<UserInfo> {
  return get<UserInfo>('/auth/me')
}

/** POST /api/auth/login —— 成功后后端下发 JSESSIONID。 */
export function loginRequest(payload: LoginRequest): Promise<MutationResult<UserInfo>> {
  return post<UserInfo>('/auth/login', payload)
}

/** POST /api/auth/logout —— 需要登录；成功后后端销毁 Session。 */
export function logoutRequest(): Promise<MutationResult<null>> {
  return post<null>('/auth/logout')
}
