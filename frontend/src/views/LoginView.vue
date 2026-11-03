<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import CharacterArt from '@/components/CharacterArt.vue'
import { siteConfig } from '@/config/site'
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

const initial = computed(() =>
  getInitial(auth.user?.nickname || auth.user?.username || ''),
)
const checkingSession = computed(() => auth.loading && !auth.restored)

function safeRedirect() {
  const redirect = route.query.redirect
  if (typeof redirect !== 'string') return '/admin'
  if (!redirect.startsWith('/admin')) return '/admin'
  if (redirect.startsWith('//') || redirect.startsWith('/admin/login')) return '/admin'
  return redirect
}

async function submit() {
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

async function handleLogout() {
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
      <div class="login-card glass-panel">
        <aside class="login-aside">
          <RouterLink class="login-brand" to="/" target="_blank" rel="noopener noreferrer">
            <span class="login-brand__mark" aria-hidden="true">✦</span>
            START NIGHT
          </RouterLink>

          <p class="login-aside__kicker">OWNER ONLY · 夜の入口</p>
          <h1>
            欢迎回到
            <em>夜色管理室</em>
          </h1>
          <p class="login-aside__lead">
            这扇玻璃门只为你保留。登录后，月光会记住你的名字。
          </p>

          <div class="login-aside__art" aria-hidden="true">
            <CharacterArt
              :src="siteConfig.characterImage"
              alt="Start Night 角色插画"
            />
          </div>

          <span class="login-aside__deco" aria-hidden="true">きらめく ♡</span>
        </aside>

        <div class="login-main">
          <div v-if="checkingSession" class="login-check">
            <span class="login-check__orb" aria-hidden="true">✦</span>
            <p class="login-kicker">CHECKING SESSION</p>
            <h2>正在确认登录态…</h2>
            <p>星星正在翻找你的通行证。</p>
          </div>

          <div v-else-if="auth.user" class="login-user">
            <p class="login-kicker">SIGNED IN · 欢迎回来</p>
            <span class="login-user__avatar" aria-hidden="true">{{ initial }}</span>
            <h2>{{ auth.user.nickname }}</h2>
            <p class="login-user__handle">@{{ auth.user.username }}</p>
            <p class="login-user__note">
              站长会话已经恢复。后台的灯还亮着，文章、评论和友链都在等你。
            </p>

            <div class="login-user__actions">
              <RouterLink class="btn btn--primary" :to="{ name: 'admin-dashboard' }">
                进入管理后台
              </RouterLink>
              <RouterLink class="btn btn--ghost" to="/" target="_blank" rel="noopener noreferrer">回到首页</RouterLink>
              <button
                class="btn btn--ghost"
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
            <p class="login-kicker">SIGN IN · 站长入口</p>
            <h2>登录 Start Night</h2>
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
                  placeholder="输入密码"
                />
              </div>

              <p v-if="formError" class="login-error" role="alert">{{ formError }}</p>

              <button
                class="btn btn--primary login-submit"
                type="submit"
                :disabled="auth.loading"
              >
                {{ auth.loading ? '登录中…' : '进入夜色' }}
              </button>
            </form>

            <p class="login-note">仅站长使用。登录态由 HttpOnly Session Cookie 保持。</p>
          </div>

          <RouterLink class="login-back" to="/" target="_blank" rel="noopener noreferrer">← 回到首页</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-page {
  display: flex;
  align-items: center;
  min-height: calc(100vh - var(--header-h));
  padding: 42px 0;
}

.login-page__inner {
  max-width: 980px;
}

.login-card {
  display: grid;
  grid-template-columns: minmax(0, 0.92fr) minmax(0, 1.08fr);
  overflow: hidden;
  background:
    radial-gradient(520px 380px at 10% 0%, rgba(255, 158, 181, 0.1), transparent 65%),
    rgba(255, 255, 255, 0.045);
  border-radius: 28px;
}

.login-aside {
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  padding: 34px;
  background:
    radial-gradient(420px 340px at 20% 10%, rgba(185, 103, 255, 0.16), transparent 66%),
    rgba(255, 255, 255, 0.025);
  border-right: 1px solid rgba(255, 255, 255, 0.09);
}

.login-aside::after {
  position: absolute;
  right: -80px;
  bottom: -90px;
  width: 240px;
  height: 240px;
  border: 1px solid rgba(255, 158, 181, 0.14);
  border-radius: 50%;
  box-shadow:
    0 0 0 22px rgba(185, 103, 255, 0.03),
    0 0 0 52px rgba(126, 200, 227, 0.02);
  content: '';
}

.login-brand {
  position: relative;
  z-index: 1;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  width: fit-content;
  margin-bottom: 34px;
  color: var(--ink-2);
  font-family: var(--font-mono);
  font-size: 0.58rem;
  letter-spacing: 0.16em;
}

.login-brand__mark {
  display: grid;
  place-items: center;
  width: 26px;
  height: 26px;
  color: var(--pink);
  border: 1px solid rgba(255, 158, 181, 0.28);
  border-radius: 50%;
  box-shadow: 0 0 18px rgba(255, 158, 181, 0.18);
}

.login-aside__kicker {
  position: relative;
  z-index: 1;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.53rem;
  letter-spacing: 0.13em;
}

.login-aside h1 {
  position: relative;
  z-index: 1;
  margin-top: 9px;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(1.8rem, 3.3vw, 2.35rem);
  font-weight: 400;
  line-height: 1.4;
  text-shadow: 0 0 24px rgba(255, 158, 181, 0.15);
}

.login-aside h1 em {
  display: block;
  color: transparent;
  font-style: normal;
  background: var(--gradient);
  -webkit-background-clip: text;
  background-clip: text;
}

.login-aside__lead {
  position: relative;
  z-index: 1;
  max-width: 28em;
  margin-top: 12px;
  color: var(--muted);
  font-size: 0.76rem;
  line-height: 1.85;
}

.login-aside__art {
  position: relative;
  z-index: 1;
  flex: none;
  width: 100%;
  max-width: 360px;
  height: clamp(220px, 21vw, 295px);
  margin: 14px auto 0;
  overflow: hidden;
}

.login-aside__art :deep(.character-art) {
  min-height: 0;
  height: 100%;
  background: transparent;
}

.login-aside__art :deep(.character-art)::after {
  display: none;
}

.login-aside__art :deep(.character-art__image) {
  width: 100%;
  height: 100%;
  object-fit: contain;
  object-position: center center;
  mask-image: linear-gradient(180deg, #000 0%, #000 90%, transparent 100%);
  -webkit-mask-image: linear-gradient(180deg, #000 0%, #000 90%, transparent 100%);
}

.login-aside__art :deep(.character-art__label) {
  right: 10px;
  bottom: 10px;
  transform: scale(0.82);
  transform-origin: right bottom;
}

.login-aside__art :deep(.character-art__vertical) {
  top: 12px;
  left: 8px;
  font-size: 0.5rem;
}

.login-aside__art :deep(.character-art__art) {
  max-height: 280px;
  object-fit: contain;
}

.login-aside__deco {
  position: absolute;
  right: 22px;
  bottom: 18px;
  z-index: 2;
  color: rgba(255, 158, 181, 0.48);
  font-family: var(--font-display);
  font-size: 0.62rem;
  letter-spacing: 0.12em;
}

.login-main {
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 42px 44px;
}

.login-kicker {
  margin-bottom: 8px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.53rem;
  letter-spacing: 0.14em;
}

.login-main h2 {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(1.8rem, 3.4vw, 2.35rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.35;
}

.login-lead {
  margin-top: 9px;
  color: var(--muted);
  font-size: 0.78rem;
  line-height: 1.8;
}

.login-form {
  margin-top: 24px;
}

.login-field {
  margin-bottom: 13px;
}

.login-field__label {
  display: block;
  margin-bottom: 6px;
  color: var(--ink-2);
  font-size: 0.7rem;
  font-weight: 600;
}

.login-field__input {
  display: block;
  width: 100%;
  padding: 11px 13px;
  color: var(--ink);
  font-family: inherit;
  font-size: 0.82rem;
  background: rgba(10, 7, 19, 0.34);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  outline: none;
  transition:
    border-color 0.2s var(--ease),
    background 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.login-field__input::placeholder {
  color: rgba(255, 255, 255, 0.28);
}

.login-field__input:focus {
  background: rgba(10, 7, 19, 0.45);
  border-color: rgba(255, 158, 181, 0.55);
  box-shadow: 0 0 0 3px rgba(255, 158, 181, 0.08);
}

.login-submit {
  width: 100%;
  min-height: 43px;
  margin-top: 6px;
}

.login-error {
  margin: 5px 0 11px;
  padding: 8px 11px;
  color: #ffc0c0;
  font-size: 0.7rem;
  line-height: 1.6;
  background: rgba(200, 70, 80, 0.1);
  border: 1px solid rgba(255, 120, 120, 0.16);
  border-radius: 8px;
}

.login-note {
  margin-top: 14px;
  color: var(--faint);
  font-size: 0.58rem;
  line-height: 1.7;
}

.login-back {
  display: inline-flex;
  width: fit-content;
  margin-top: 22px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.58rem;
  transition: color 0.2s var(--ease);
}

.login-back:hover {
  color: var(--pink);
}

.login-check {
  padding: 20px 0;
}

.login-check__orb {
  display: grid;
  place-items: center;
  width: 48px;
  height: 48px;
  margin-bottom: 16px;
  color: var(--pink);
  background: rgba(255, 158, 181, 0.09);
  border: 1px solid rgba(255, 158, 181, 0.18);
  border-radius: 50%;
  box-shadow: 0 0 28px rgba(255, 158, 181, 0.16);
  animation: glow-pulse 2.8s ease-in-out infinite;
}

.login-check p:not(.login-kicker) {
  margin-top: 8px;
  color: var(--muted);
  font-size: 0.75rem;
}

.login-user__avatar {
  display: grid;
  place-items: center;
  width: 58px;
  height: 58px;
  margin: 18px 0 14px;
  color: #25102b;
  font-family: var(--font-mono);
  font-size: 1.05rem;
  font-weight: 700;
  background: var(--gradient);
  border-radius: 50%;
  box-shadow: 0 0 30px rgba(255, 158, 181, 0.22);
}

.login-user__handle {
  margin-top: 3px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.64rem;
}

.login-user__note {
  margin-top: 15px;
  color: var(--muted);
  font-size: 0.75rem;
  line-height: 1.8;
}

.login-user__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 22px;
}

@media (max-width: 780px) {
  .login-page {
    padding: 28px 0;
  }

  .login-card {
    grid-template-columns: minmax(0, 1fr);
  }

  .login-aside {
    padding: 24px 24px 18px;
    border-right: 0;
    border-bottom: 1px solid rgba(255, 255, 255, 0.09);
  }

  .login-aside__art {
    display: block;
    width: min(100%, 280px);
    height: 190px;
    margin: 6px auto 0;
  }

  .login-aside__deco {
    bottom: 14px;
  }

  .login-main {
    padding: 26px 24px;
  }
}

@media (max-width: 480px) {
  .login-card {
    border-radius: 20px;
  }

  .login-aside {
    padding: 20px;
  }

  .login-aside__art {
    width: min(100%, 230px);
    height: 165px;
  }

  .login-main {
    padding: 22px 20px;
  }
}
</style>
