<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getInitial } from '@/utils/url'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const rootRef = ref(null)
const open = ref(false)
const logoutError = ref('')

const displayName = computed(
  () => auth.user?.nickname || auth.user?.username || '登录',
)
const initial = computed(() =>
  getInitial(auth.user?.nickname || auth.user?.username || ''),
)

function closeMenu() {
  open.value = false
}

function toggleMenu() {
  if (!auth.user) return
  open.value = !open.value
}

function goLogin() {
  closeMenu()
  if (route.name === 'admin-login') {
    document.querySelector('#login-username')?.focus()
    return
  }
  const current = route.fullPath
  const query =
    current && current !== '/admin/login' ? { redirect: current } : undefined
  void router.push({ name: 'admin-login', query })
}

async function handleLogout() {
  logoutError.value = ''
  try {
    const current = route.fullPath
    const inAdmin = route.meta.admin === true
    await auth.logout()
    closeMenu()
    if (inAdmin) {
      await router.replace({
        name: 'admin-login',
        query: { redirect: current.startsWith('/admin/login') ? '/admin' : current },
      })
    }
  } catch {
    logoutError.value = auth.error || '退出登录失败，请稍后再试'
  }
}

function handlePointerDown(event) {
  if (!open.value) return
  const target = event.target
  if (target && rootRef.value?.contains(target)) return
  closeMenu()
}

function handleKeydown(event) {
  if (event.key === 'Escape') closeMenu()
}

watch(() => route.fullPath, closeMenu)

onMounted(() => {
  document.addEventListener('pointerdown', handlePointerDown)
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('pointerdown', handlePointerDown)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <div ref="rootRef" class="auth">
    <button
      v-if="auth.user"
      class="auth__button"
      type="button"
      :aria-label="`账号菜单：${displayName}`"
      aria-haspopup="true"
      :aria-expanded="open"
      @click="toggleMenu"
    >
      <span class="auth__avatar" aria-hidden="true">{{ initial }}</span>
      <span class="auth__label">{{ displayName }}</span>
      <span class="auth__chevron" aria-hidden="true">⌄</span>
    </button>

    <button
      v-else
      class="auth__button"
      type="button"
      aria-label="站长登录"
      @click="goLogin"
    >
      <svg class="auth__icon" viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="12" cy="8" r="3.4" fill="none" stroke="currentColor" stroke-width="1.7" />
        <path
          d="M5.5 20c.7-3.6 3.2-5.4 6.5-5.4s5.8 1.8 6.5 5.4"
          fill="none"
          stroke="currentColor"
          stroke-width="1.7"
          stroke-linecap="round"
        />
      </svg>
      <span class="auth__label">登录</span>
    </button>

    <div v-if="open && auth.user" class="auth__panel">
      <div class="auth__panel-head">
        <span class="auth__panel-avatar" aria-hidden="true">{{ initial }}</span>
        <div>
          <p class="auth__panel-kicker">SIGNED IN AS</p>
          <p class="auth__panel-name">{{ auth.user.nickname }}</p>
          <p class="auth__panel-handle">@{{ auth.user.username }}</p>
        </div>
      </div>

      <RouterLink
        class="auth__panel-link auth__panel-link--admin"
        :to="{ name: 'admin-dashboard' }"
        @click="closeMenu"
      >
        <span>◇</span>
        进入管理后台
      </RouterLink>

      <RouterLink
        class="auth__panel-link"
        to="/"
        target="_blank"
        rel="noopener noreferrer"
        @click="closeMenu"
      >
        <span>✦</span>
        回到夜色首页
      </RouterLink>

      <button
        class="auth__logout"
        type="button"
        :disabled="auth.loading"
        @click="handleLogout"
      >
        {{ auth.loading ? '退出中…' : '退出登录' }}
      </button>

      <p v-if="logoutError" class="auth__error" role="alert">{{ logoutError }}</p>
    </div>
  </div>
</template>

<style scoped>
.auth {
  position: relative;
  display: flex;
  align-items: center;
}

.auth__button {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  min-height: 38px;
  padding: 0 11px;
  color: var(--ink-2);
  font-size: 0.72rem;
  border: 1px solid transparent;
  border-radius: 999px;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease),
    border-color 0.2s var(--ease);
}

.auth__button:hover {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.09);
  border-color: rgba(255, 158, 181, 0.2);
}

.auth__icon {
  width: 17px;
  height: 17px;
}

.auth__avatar,
.auth__panel-avatar {
  display: grid;
  place-items: center;
  flex: none;
  color: #24102a;
  font-family: var(--font-mono);
  font-weight: 700;
  background: var(--gradient);
  border-radius: 50%;
  box-shadow: 0 0 18px rgba(255, 158, 181, 0.25);
}

.auth__avatar {
  width: 25px;
  height: 25px;
  font-size: 0.66rem;
}

.auth__label {
  max-width: 108px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auth__chevron {
  color: var(--muted);
  font-size: 0.8rem;
  line-height: 1;
}

.auth__panel {
  position: absolute;
  top: calc(100% + 12px);
  right: 0;
  z-index: 110;
  width: 245px;
  padding: 16px;
  background: rgba(20, 14, 36, 0.92);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 18px;
  box-shadow:
    0 24px 70px rgba(0, 0, 0, 0.55),
    0 0 36px rgba(185, 103, 255, 0.12);
  -webkit-backdrop-filter: blur(22px) saturate(150%);
  backdrop-filter: blur(22px) saturate(150%);
  animation: rise-in 0.2s var(--ease) both;
}

.auth__panel-head {
  display: flex;
  align-items: center;
  gap: 10px;
  padding-bottom: 13px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.auth__panel-avatar {
  width: 38px;
  height: 38px;
  font-size: 0.85rem;
}

.auth__panel-kicker {
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.48rem;
  letter-spacing: 0.12em;
}

.auth__panel-name {
  overflow: hidden;
  margin-top: 2px;
  color: var(--ink);
  font-size: 0.82rem;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auth__panel-handle {
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.58rem;
}

.auth__panel-link {
  display: flex;
  align-items: center;
  gap: 7px;
  margin-top: 12px;
  padding: 8px 9px;
  color: var(--ink-2);
  font-size: 0.7rem;
  border-radius: 9px;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease);
}

.auth__panel-link span {
  color: var(--pink);
}

.auth__panel-link--admin span {
  color: var(--cyan);
}

.auth__panel-link:hover {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.08);
}

.auth__logout {
  width: 100%;
  margin-top: 6px;
  padding: 9px 12px;
  color: var(--ink-2);
  font-size: 0.72rem;
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease),
    border-color 0.2s var(--ease);
}

.auth__logout:hover:not(:disabled) {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.09);
  border-color: rgba(255, 158, 181, 0.25);
}

.auth__logout:disabled {
  opacity: 0.55;
}

.auth__error {
  margin-top: 8px;
  color: #ffc4c4;
  font-size: 0.66rem;
  line-height: 1.5;
}

@media (max-width: 640px) {
  .auth__button {
    padding: 0 7px;
  }

  .auth__label,
  .auth__chevron {
    display: none;
  }

  .auth__avatar {
    width: 28px;
    height: 28px;
  }
}
</style>
