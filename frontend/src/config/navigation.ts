import type { RouteLocationRaw } from 'vue-router'

export interface NavLink {
  label: string
  to: RouteLocationRaw
}

export const navLinks: NavLink[] = [
  { label: '首页', to: { name: 'home' } },
  { label: '文章', to: { name: 'posts' } },
  { label: '友链', to: { name: 'friends' } },
  { label: '关于', to: { name: 'home', hash: '#about' } },
]
