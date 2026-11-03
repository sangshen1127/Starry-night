import { get } from './http'

export async function fetchHomeData() {
  const raw = await get('/home')

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
