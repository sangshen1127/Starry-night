import { get } from './http'
import type { FriendLink } from './types'

/** GET /api/friend-links —— 只返回 visible = 1，按 sort_order 升序。 */
export function fetchFriendLinks(): Promise<FriendLink[]> {
  return get<FriendLink[]>('/friend-links')
}
