<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getInitial } from '@/utils/url'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const rootRef = ref<HTMLElement | null>(null)
const open = ref(false)
const logoutError = ref('')

const displayName = computed(
  () => auth.user?.nickname || auth.user?.username || '登录',
)
const initial = computed(() => getInitial(auth.user?.nickname || auth.user?.username || ''))

function closeMenu(): void {
  open.value = false
}

function toggleMenu(): void {
  if (!auth.user) return
  open.value = !open.value
}

function goLogin(): void {
  closeMenu()
  const current = route.fullPath
  const query = current && current !== '/login' ? { redirect: current } : undefined
  void router.push({ name: 'login', query })
}

async function handleLogout(): Promise<void> {
  logoutError.value = ''
  try {
    await auth.logout()
    closeMenu()
    if (route.name === 'login') {
      await router.replace('/')
    }
  } catch {
    logoutError.value = auth.error || '退出登录失败，请稍后再试'
  }
}

function handlePointerDown(event: PointerEvent): void {
  if (!open.value) return
  const target = event.target as Node | null
  if (target && rootRef.value?.contains(target)) return
  closeMenu()
}

function handleKeydown(event: KeyboardEvent): void {
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
      <p class="auth__panel-kicker">SIGNED IN AS</p>
      <p class="auth__panel-name">{{ auth.user.nickname }}</p>
      <p class="auth__panel-handle">@{{ auth.user.username }}</p>

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
  height: 36px;
  padding: 0 9px;
  color: var(--ink-2);
  font-size: 0.74rem;
  border-radius: 999px;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease);
}

.auth__button:hover {
  color: var(--violet);
  background: var(--violet-soft);
}

.auth__icon {
  width: 17px;
  height: 17px;
}

.auth__avatar {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.68rem;
  font-weight: 700;
  background: var(--violet-soft);
  border: 1px solid var(--line);
  border-radius: 50%;
}

.auth__label {
  max-width: 110px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.auth__panel {
  position: absolute;
  top: calc(100% + 10px);
  right: 0;
  z-index: 70;
  width: 220px;
  padding: 16px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 15px;
  box-shadow: var(--shadow-md);
}

.auth__panel-kicker {
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.52rem;
  letter-spacing: 0.15em;
}

.auth__panel-name {
  margin-top: 6px;
  color: var(--ink);
  font-size: 0.92rem;
  font-weight: 700;
  line-height: 1.45;
}

.auth__panel-handle {
  margin-top: 2px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.64rem;
}

.auth__logout {
  width: 100%;
  margin-top: 14px;
  padding: 9px 12px;
  color: var(--ink-2);
  font-size: 0.74rem;
  border: 1px solid var(--line);
  border-radius: 9px;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease),
    border-color 0.2s var(--ease);
}

.auth__logout:hover:not(:disabled) {
  color: var(--violet);
  background: var(--violet-soft);
  border-color: var(--violet-soft);
}

.auth__logout:disabled {
  opacity: 0.55;
}

.auth__error {
  margin-top: 8px;
  color: #b54141;
  font-size: 0.68rem;
  line-height: 1.5;
}

@media (max-width: 640px) {
  .auth__button {
    padding: 0 6px;
  }

  .auth__label {
    display: none;
  }
}
</style>
