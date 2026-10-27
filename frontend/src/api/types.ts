export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

/** 首页文章卡片所需字段，对应 GET /api/home 的 latestPosts。 */
export interface PostSummary {
  id: number
  title: string
  summary?: string | null
  coverUrl?: string | null
  publishedAt?: string | null
  /** 接口文档首页示例未列出，但 PostListItem 实际会返回，前端按可选处理。 */
  viewCount?: number | null
}

/** GET /api/home 的 latestComments / 实际后端的 lastestComments。 */
export interface HomeComment {
  id: number
  postId: number
  postTitle?: string | null
  nickname: string
  content: string
  createdAt?: string | null
}

/** GET /api/home 的 friendLinks，字段同 GET /api/friend-links。 */
export interface FriendLink {
  id: number
  name: string
  url: string
  avatarUrl?: string | null
  description?: string | null
}

/** 前端统一后的首页数据结构。 */
export interface HomeData {
  latestPosts: PostSummary[]
  latestComments: HomeComment[]
  friendLinks: FriendLink[]
}

/**
 * 后端 Home record 当前组件名是 lastestComments（拼写少了一个 t），
 * 接口文档写的是 latestComments。这里两种都接受，避免首页评论为空。
 */
export interface HomeRaw {
  latestPosts?: PostSummary[]
  latestComments?: HomeComment[]
  lastestComments?: HomeComment[]
  friendLinks?: FriendLink[]
}

/** 后端 PageResult<T> 的公共结构。 */
export interface PageResult<T> {
  list: T[]
  total: number
  page: number
  size: number
}

/** GET /api/posts/{id}：比列表多正文、更新时间、评论数。 */
export interface PostDetail extends PostSummary {
  content: string
  updatedAt?: string | null
  /** 后端当前固定返回 0，页面实际用评论接口的 total 更准确。 */
  commentCount?: number | null
}

/** GET /api/posts/{id}/comments 的公开评论字段。 */
export interface PublicComment {
  id: number
  nickname: string
  website?: string | null
  content: string
  createdAt?: string | null
}

/** POST /api/comments 请求体。 */
export interface CommentCreateRequest {
  postId: number
  nickname: string
  email?: string
  website?: string
  content: string
}

export interface PostListQuery {
  page?: number
  size?: number
  keyword?: string
}

export interface CommentListQuery {
  page?: number
  size?: number
}

/** 登录接口返回的用户信息。 */
export interface UserInfo {
  id: number
  username: string
  nickname: string
}

export interface LoginRequest {
  username: string
  password: string
}
