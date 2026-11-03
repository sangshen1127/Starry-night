import { get } from './http'

export function fetchFriendLinks() {
  return get('/friend-links')
}
