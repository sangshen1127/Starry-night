import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
      meta: { title: '在夜色里记录一点光' },
    },
    {
      path: '/posts',
      name: 'posts',
      component: () => import('@/views/PostsView.vue'),
      meta: { title: '全部文章' },
    },
    {
      path: '/posts/:id(\\d+)',
      name: 'post',
      component: () => import('@/views/PostDetailView.vue'),
      meta: { title: '文章详情' },
    },
    {
      path: '/friends',
      name: 'friends',
      component: () => import('@/views/FriendsView.vue'),
      meta: { title: '友链' },
    },
    {
      path: '/admin/login',
      name: 'admin-login',
      component: () => import('@/views/LoginView.vue'),
      meta: {
        admin: true,
        siteChrome: true,
        showAuth: true,
        title: '后台登录',
      },
    },
    {
      path: '/admin',
      component: () => import('@/views/admin/AdminLayout.vue'),
      meta: { requiresAdmin: true, admin: true, title: '管理后台' },
      children: [
        {
          path: '',
          name: 'admin-dashboard',
          component: () => import('@/views/admin/AdminDashboardView.vue'),
          meta: { title: '夜色概览' },
        },
        {
          path: 'posts',
          name: 'admin-posts',
          component: () => import('@/views/admin/AdminPostsView.vue'),
          meta: { title: '文章管理' },
        },
        {
          path: 'posts/new',
          name: 'admin-post-new',
          component: () => import('@/views/admin/AdminPostEditorView.vue'),
          meta: { title: '写新文章' },
        },
        {
          path: 'posts/:id(\\d+)/edit',
          name: 'admin-post-edit',
          component: () => import('@/views/admin/AdminPostEditorView.vue'),
          meta: { title: '编辑文章' },
        },
        {
          path: 'comments',
          name: 'admin-comments',
          component: () => import('@/views/admin/AdminCommentsView.vue'),
          meta: { title: '评论审核' },
        },
        {
          path: 'friend-links',
          name: 'admin-friend-links',
          component: () => import('@/views/admin/AdminFriendLinksView.vue'),
          meta: { title: '友链管理' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/FallbackView.vue'),
      meta: { title: '页面未找到' },
    },
  ],
    scrollBehavior(to, _from, savedPosition) {
    if (savedPosition) return savedPosition

    const behavior = window.matchMedia('(prefers-reduced-motion: reduce)').matches
      ? 'auto'
      : 'smooth'

    if (to.hash) {
      return { el: to.hash, top: 96, behavior }
    }

    return { top: 0, behavior }
  },
})

router.beforeEach(async (to) => {
  const isAdminLogin = to.name === 'admin-login'
  if (!to.meta.requiresAdmin && !isAdminLogin) return true

  const auth = useAuthStore()
  if (!auth.restored) {
    await auth.restore()
  }

  if (isAdminLogin) {
    return auth.isAuthenticated ? { name: 'admin-dashboard' } : true
  }

  if (!auth.isAuthenticated) {
    return {
      name: 'admin-login',
      query: { redirect: to.fullPath },
    }
  }

  return true
})

router.afterEach((to) => {
  const title = typeof to.meta.title === 'string' ? to.meta.title : ''
  document.title = title ? `${title} · Start Night` : 'Start Night'
})

export default router
