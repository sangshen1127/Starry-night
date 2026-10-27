<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import StarField from '@/components/StarField.vue'
import { useAuthStore } from '@/stores/auth'
import { getInitial } from '@/utils/url'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()

const form = reactive({
  username: '',
  password: '',
})
const formError = ref('')
const logoutError = ref('')

const initial = computed(() => getInitial(auth.user?.nickname || auth.user?.username || ''))
const checkingSession = computed(() => auth.loading && !auth.restored)

function safeRedirect(): string {
  const redirect = route.query.redirect
  if (typeof redirect !== 'string') return '/'
  if (!redirect.startsWith('/') || redirect.startsWith('//')) return '/'
  if (redirect.startsWith('/login')) return '/'
  return redirect
}

async function submit(): Promise<void> {
  formError.value = ''

  if (!form.username.trim() || !form.password) {
    formError.value = '请输入用户名和密码'
    return
  }

  try {
    await auth.login(form.username, form.password)
    await router.replace(safeRedirect())
  } catch {
    formError.value = auth.error || '登录失败，请稍后再试'
  }
}

async function handleLogout(): Promise<void> {
  logoutError.value = ''
  try {
    await auth.logout()
  } catch {
    logoutError.value = auth.error || '退出登录失败，请稍后再试'
  }
}

onMounted(() => {
  auth.clearError()
})
</script>

<template>
  <div class="login-page">
    <div class="container login-page__inner">
      <div class="login-card">
        <StarField />

        <div class="login-card__content">
          <RouterLink class="login-brand" to="/">
            <span class="login-brand__mark" aria-hidden="true">✦</span>
            START NIGHT
          </RouterLink>

          <div v-if="checkingSession" class="login-check">
            <p class="login-kicker">CHECKING SESSION</p>
            <h1>正在确认登录态…</h1>
          </div>

          <div v-else-if="auth.user" class="login-user">
            <p class="login-kicker">SIGNED IN</p>
            <span class="login-user__avatar" aria-hidden="true">{{ initial }}</span>
            <h1>欢迎回来，{{ auth.user.nickname }}</h1>
            <p class="login-user__handle">@{{ auth.user.username }}</p>
            <p class="login-user__note">
              当前浏览器已经保持站长会话。内容管理功能会在下一阶段接入。
            </p>

            <div class="login-user__actions">
              <RouterLink class="btn btn--light" to="/">回到首页</RouterLink>
              <button
                class="btn login-user__logout"
                type="button"
                :disabled="auth.loading"
                @click="handleLogout"
              >
                {{ auth.loading ? '退出中…' : '退出登录' }}
              </button>
            </div>
            <p v-if="logoutError" class="login-error" role="alert">{{ logoutError }}</p>
          </div>

          <div v-else class="login-form-wrap">
            <p class="login-kicker">OWNER ONLY · 站长入口</p>
            <h1>登录 Start Night</h1>
            <p class="login-lead">
              登录后保持站长会话，用于后续的文章、评论与友链管理。
            </p>

            <form class="login-form" novalidate @submit.prevent="submit">
              <div class="login-field">
                <label class="login-field__label" for="login-username">用户名</label>
                <input
                  id="login-username"
                  v-model="form.username"
                  class="login-field__input"
                  type="text"
                  name="username"
                  autocomplete="username"
                  placeholder="站长账号"
                />
              </div>

              <div class="login-field">
                <label class="login-field__label" for="login-password">密码</label>
                <input
                  id="login-password"
                  v-model="form.password"
                  class="login-field__input"
                  type="password"
                  name="password"
                  autocomplete="current-password"
                  placeholder="密码"
                />
              </div>

              <p v-if="formError" class="login-error" role="alert">{{ formError }}</p>

              <button
                class="btn btn--light login-submit"
                type="submit"
                :disabled="auth.loading"
              >
                {{ auth.loading ? '登录中…' : '登录' }}
              </button>
            </form>

            <p class="login-note">仅站长使用。登录后保持当前浏览器的站长会话。</p>
          </div>

          <RouterLink class="login-back" to="/">← 回到首页</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  padding: 56px 0 24px;
}

.login-page__inner {
  max-width: 480px;
}

.login-card {
  position: relative;
  overflow: hidden;
  padding: 36px;
  color: var(--night-text);
  background:
    radial-gradient(520px 360px at 82% 0%, rgba(100, 84, 200, 0.32), transparent 70%),
    linear-gradient(155deg, var(--night-0), var(--night-1) 58%, var(--night-2));
  border: 1px solid rgba(255, 255, 255, 0.09);
  border-radius: 28px;
  box-shadow: var(--shadow-night);
}

.login-card__content {
  position: relative;
  z-index: 2;
}

.login-brand {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 32px;
  color: rgba(244, 240, 230, 0.72);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.18em;
  transition: color 0.2s var(--ease);
}

.login-brand:hover {
  color: var(--star);
}

.login-brand__mark {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  color: var(--star);
  border: 1px solid var(--night-line);
  border-radius: 50%;
}

.login-kicker {
  margin-bottom: 8px;
  color: var(--star);
  font-family: var(--font-mono);
  font-size: 0.57rem;
  letter-spacing: 0.16em;
}

.login-card h1 {
  color: var(--night-text);
  font-size: clamp(1.75rem, 4vw, 2.2rem);
  font-weight: 800;
  letter-spacing: -0.035em;
  line-height: 1.3;
}

.login-lead {
  margin-top: 10px;
  color: var(--night-muted);
  font-size: 0.8rem;
  line-height: 1.8;
}

.login-form {
  margin-top: 26px;
}

.login-field {
  margin-bottom: 14px;
}

.login-field__label {
  display: block;
  margin-bottom: 6px;
  color: rgba(244, 240, 230, 0.72);
  font-size: 0.7rem;
  font-weight: 600;
}

.login-field__input {
  display: block;
  width: 100%;
  padding: 11px 13px;
  color: var(--night-text);
  font-family: inherit;
  font-size: 0.86rem;
  background: rgba(255, 255, 255, 0.055);
  border: 1px solid var(--night-line);
  border-radius: 10px;
  outline: none;
  transition:
    border-color 0.2s var(--ease),
    background 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.login-field__input::placeholder {
  color: rgba(184, 180, 201, 0.55);
}

.login-field__input:focus {
  background: rgba(255, 255, 255, 0.09);
  border-color: rgba(244, 213, 141, 0.65);
  box-shadow: 0 0 0 3px rgba(244, 213, 141, 0.08);
}

.login-submit {
  width: 100%;
  min-height: 44px;
  margin-top: 7px;
}

.login-submit:disabled {
  opacity: 0.6;
}

.login-error {
  margin: 6px 0 12px;
  padding: 8px 11px;
  color: #ffc4bd;
  font-size: 0.72rem;
  line-height: 1.6;
  background: rgba(181, 65, 65, 0.18);
  border: 1px solid rgba(255, 167, 157, 0.2);
  border-radius: 8px;
}

.login-note {
  margin-top: 15px;
  color: rgba(184, 180, 201, 0.6);
  font-size: 0.6rem;
  line-height: 1.7;
}

.login-check {
  padding: 36px 0;
}

.login-back {
  display: inline-flex;
  margin-top: 26px;
  color: rgba(184, 180, 201, 0.65);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  transition: color 0.2s var(--ease);
}

.login-back:hover {
  color: var(--star);
}

.login-user {
  padding-top: 4px;
}

.login-user__avatar {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  margin: 18px 0 16px;
  color: var(--night-1);
  font-family: var(--font-mono);
  font-size: 1.15rem;
  font-weight: 800;
  background: var(--moon);
  border-radius: 50%;
  box-shadow: 0 0 0 8px rgba(244, 213, 141, 0.08);
}

.login-user__handle {
  margin-top: 5px;
  color: var(--night-muted);
  font-family: var(--font-mono);
  font-size: 0.68rem;
}

.login-user__note {
  margin-top: 16px;
  color: var(--night-muted);
  font-size: 0.78rem;
  line-height: 1.8;
}

.login-user__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 24px;
}

.login-user__logout {
  color: var(--night-text);
  border-color: rgba(255, 255, 255, 0.2);
}

.login-user__logout:hover {
  background: rgba(255, 255, 255, 0.08);
}

@media (max-width: 560px) {
  .login-page {
    padding-top: 30px;
  }

  .login-card {
    padding: 26px 22px;
    border-radius: 22px;
  }

  .login-brand {
    margin-bottom: 24px;
  }
}
</style>
