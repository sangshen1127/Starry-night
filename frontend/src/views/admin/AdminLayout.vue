<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { siteConfig } from '@/config/site'
import { useAuthStore } from '@/stores/auth'
import { getInitial, resolveAssetUrl } from '@/utils/url'
import '@/styles/admin.css'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const menuOpen = ref(false)
const logoutError = ref('')
const currentYear = new Date().getFullYear()

const mascot = resolveAssetUrl(siteConfig.avatarImage || siteConfig.characterImage)
const initial = computed(() =>
  getInitial(auth.user?.nickname || auth.user?.username || 'S'),
)
const pageTitle = computed(() => {
  return typeof route.meta.title === 'string' ? route.meta.title : '管理后台'
})
const pageCode = computed(() => {
  if (route.name === 'admin-posts') return 'POST ARCHIVE'
  if (route.name?.startsWith('admin-post')) return 'POST EDITOR'
  if (route.name === 'admin-comments') return 'COMMENT QUEUE'
  if (route.name === 'admin-friend-links') return 'LINK REGISTRY'
  return 'OWNER CONSOLE'
})

const navItems = [
  {
    label: '夜色概览',
    sub: 'DASHBOARD',
    icon: '✦',
    index: '01',
    to: { name: 'admin-dashboard' },
    match: ['admin-dashboard'],
  },
  {
    label: '文章管理',
    sub: 'POSTS',
    icon: '✎',
    index: '02',
    to: { name: 'admin-posts' },
    match: ['admin-posts', 'admin-post-new', 'admin-post-edit'],
  },
  {
    label: '评论审核',
    sub: 'COMMENTS',
    icon: '◌',
    index: '03',
    to: { name: 'admin-comments' },
    match: ['admin-comments'],
  },
  {
    label: '友链管理',
    sub: 'FRIEND LINKS',
    icon: '↗',
    index: '04',
    to: { name: 'admin-friend-links' },
    match: ['admin-friend-links'],
  },
]

function isActive(item) {
  return item.match.includes(route.name)
}

function closeMenu() {
  menuOpen.value = false
}

async function handleLogout() {
  logoutError.value = ''
  try {
    await auth.logout()
    closeMenu()
    await router.replace({
      name: 'admin-login',
      query: { redirect: '/admin' },
    })
  } catch {
    logoutError.value = auth.error || '退出登录失败，请稍后再试'
  }
}

function handleKeydown(event) {
  if (event.key === 'Escape') closeMenu()
}

watch(() => route.fullPath, closeMenu)

onMounted(() => {
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <div class="admin-shell">
    <button
      v-if="menuOpen"
      class="admin-overlay"
      type="button"
      aria-label="关闭管理菜单"
      @click="closeMenu"
    />

    <aside class="admin-side" :class="{ 'is-open': menuOpen }" aria-label="管理导航">
      <RouterLink class="admin-brand" :to="{ name: 'admin-dashboard' }" @click="closeMenu">
        <span class="admin-brand__mark" aria-hidden="true">
          <img v-if="mascot" :src="mascot" alt="" />
          <span v-else>✦</span>
        </span>
        <span class="admin-brand__copy">
          <span class="admin-brand__name">START NIGHT</span>
          <span class="admin-brand__sub">OWNER CONSOLE</span>
        </span>
      </RouterLink>

      <p class="admin-side__kicker">MANAGE THE NIGHT</p>

      <nav class="admin-nav">
        <RouterLink
          v-for="item in navItems"
          :key="item.index"
          class="admin-nav__link"
          :class="{ 'is-active': isActive(item) }"
          :to="item.to"
          @click="closeMenu"
        >
          <span class="admin-nav__icon" aria-hidden="true">{{ item.icon }}</span>
          <span class="admin-nav__label">
            {{ item.label }}
            <span class="admin-nav__sub">{{ item.sub }}</span>
          </span>
          <span class="admin-nav__index" aria-hidden="true">{{ item.index }}</span>
        </RouterLink>
      </nav>

      <div class="admin-side__foot">
        <div class="admin-user">
          <span class="admin-user__avatar" aria-hidden="true">{{ initial }}</span>
          <span class="admin-user__copy">
            <span class="admin-user__name">{{ auth.user?.nickname || '站长' }}</span>
            <span class="admin-user__handle">@{{ auth.user?.username || 'owner' }}</span>
          </span>
        </div>
        <button
          class="admin-side__logout"
          type="button"
          :disabled="auth.loading"
          @click="handleLogout"
        >
          {{ auth.loading ? '退出中…' : '退出登录' }}
        </button>
        <RouterLink class="admin-side__back" to="/" target="_blank" rel="noopener noreferrer" @click="closeMenu">
          ← 回到夜色前台
        </RouterLink>
        <p v-if="logoutError" class="admin-error admin-mt">{{ logoutError }}</p>
      </div>
    </aside>

    <div class="admin-main">
      <header class="admin-topbar">
        <button
          class="admin-menu-btn"
          type="button"
          aria-label="打开管理菜单"
          @click="menuOpen = true"
        >
          <svg viewBox="0 0 24 24" width="17" height="17" aria-hidden="true">
            <path
              d="M4 7h16M4 12h16M4 17h16"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
            />
          </svg>
        </button>

        <div class="admin-topbar__copy">
          <p class="admin-topbar__eyebrow">START NIGHT / {{ pageCode }}</p>
          <h1 class="admin-topbar__title">{{ pageTitle }}</h1>
        </div>

        <div class="admin-topbar__actions">
          <span class="admin-live">SESSION ACTIVE</span>
          <RouterLink class="admin-ghost" to="/" target="_blank" rel="noopener noreferrer">前台 ↗</RouterLink>
        </div>
      </header>

      <section class="admin-content">
        <RouterView />
      </section>

      <footer class="admin-footer">
        <span>OWNER CONSOLE · 只对站长会话开放</span>
        <span>夜色管理室 · {{ currentYear }}</span>
      </footer>
    </div>
  </div>
</template>
