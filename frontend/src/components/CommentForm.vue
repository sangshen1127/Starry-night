<script setup lang="ts">
import { onBeforeUnmount, reactive, ref } from 'vue'
import { submitComment } from '@/api/comments'
import { ApiError } from '@/api/http'

const props = defineProps<{
  postId: number
}>()

const form = reactive({
  nickname: '',
  email: '',
  website: '',
  content: '',
})

const errors = reactive<{
  nickname?: string
  email?: string
  website?: string
  content?: string
}>({})

const honeypot = ref('')
const submitting = ref(false)
const success = ref('')
const failure = ref('')
const cooldown = ref(false)
let cooldownTimer: number | undefined

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function clearErrors(): void {
  errors.nickname = undefined
  errors.email = undefined
  errors.website = undefined
  errors.content = undefined
}

function validate(): boolean {
  clearErrors()

  const nickname = form.nickname.trim()
  const email = form.email.trim()
  const website = form.website.trim()
  const content = form.content.trim()

  if (!nickname) errors.nickname = '请留下昵称'
  else if (nickname.length > 32) errors.nickname = '昵称不能超过 32 个字符'

  if (email) {
    if (email.length > 128) errors.email = '邮箱不能超过 128 个字符'
    else if (!EMAIL_PATTERN.test(email)) errors.email = '邮箱格式不正确'
  }

  if (website) {
    if (website.length > 255) errors.website = '网站地址不能超过 255 个字符'
    else if (!/^https?:\/\//i.test(website)) {
      errors.website = '网站地址必须以 http:// 或 https:// 开头'
    }
  }

  if (!content) errors.content = '写点什么再发送吧'
  else if (content.length > 500) errors.content = '留言不能超过 500 个字符'

  return !errors.nickname && !errors.email && !errors.website && !errors.content
}

function startCooldown(): void {
  cooldown.value = true
  window.clearTimeout(cooldownTimer)
  cooldownTimer = window.setTimeout(() => {
    cooldown.value = false
  }, 60_000)
}

async function handleSubmit(): Promise<void> {
  success.value = ''
  failure.value = ''

  if (!validate() || submitting.value || cooldown.value) return

  // 隐藏蜜罐：正常访客不会填；填入后静默丢弃，不请求后端。
  if (honeypot.value.trim()) {
    success.value = '评论提交成功，请等待审核通过显示。'
    form.content = ''
    startCooldown()
    return
  }

  submitting.value = true
  try {
    const result = await submitComment({
      postId: props.postId,
      nickname: form.nickname.trim(),
      email: form.email.trim() || undefined,
      website: form.website.trim() || undefined,
      content: form.content.trim(),
    })

    success.value = result.message || '评论提交成功，请等待审核通过显示。'
    form.content = ''
    startCooldown()
  } catch (caught) {
    failure.value =
      caught instanceof ApiError ? caught.message : '提交失败，请稍后再试。'
    if (caught instanceof ApiError && caught.status === 429) {
      startCooldown()
    }
  } finally {
    submitting.value = false
  }
}

onBeforeUnmount(() => {
  window.clearTimeout(cooldownTimer)
})
</script>

<template>
  <form class="comment-form" novalidate @submit.prevent="handleSubmit">
    <div class="comment-form__head">
      <div>
        <p class="comment-form__kicker">LEAVE A NOTE</p>
        <h3 class="comment-form__title">写下想说的话</h3>
      </div>
      <p class="comment-form__hint">提交后需要审核，通过后才会显示</p>
    </div>

    <div class="comment-form__row">
      <div class="field">
        <label class="field__label" for="comment-nickname">昵称 *</label>
        <input
          id="comment-nickname"
          v-model="form.nickname"
          class="field__input"
          type="text"
          name="nickname"
          maxlength="32"
          autocomplete="nickname"
          placeholder="怎么称呼你？"
          :aria-invalid="Boolean(errors.nickname)"
          :aria-describedby="errors.nickname ? 'comment-nickname-error' : undefined"
        />
        <p v-if="errors.nickname" id="comment-nickname-error" class="field__error">
          {{ errors.nickname }}
        </p>
      </div>

      <div class="field">
        <label class="field__label" for="comment-email">邮箱（可选）</label>
        <input
          id="comment-email"
          v-model="form.email"
          class="field__input"
          type="email"
          name="email"
          maxlength="128"
          autocomplete="email"
          placeholder="不会公开显示"
          :aria-invalid="Boolean(errors.email)"
          :aria-describedby="errors.email ? 'comment-email-error' : undefined"
        />
        <p v-if="errors.email" id="comment-email-error" class="field__error">
          {{ errors.email }}
        </p>
      </div>
    </div>

    <div class="field">
      <label class="field__label" for="comment-website">网站（可选）</label>
      <input
        id="comment-website"
        v-model="form.website"
        class="field__input"
        type="url"
        name="website"
        maxlength="255"
        autocomplete="url"
        placeholder="https://example.com"
        :aria-invalid="Boolean(errors.website)"
        :aria-describedby="errors.website ? 'comment-website-error' : undefined"
      />
      <p v-if="errors.website" id="comment-website-error" class="field__error">
        {{ errors.website }}
      </p>
    </div>

    <div class="field">
      <div class="field__label-row">
        <label class="field__label" for="comment-content">留言 *</label>
        <span class="field__counter">{{ form.content.length }} / 500</span>
      </div>
      <textarea
        id="comment-content"
        v-model="form.content"
        class="field__input field__textarea"
        name="content"
        rows="6"
        maxlength="500"
        placeholder="把想说的话留在这里吧…"
        :aria-invalid="Boolean(errors.content)"
        :aria-describedby="errors.content ? 'comment-content-error' : undefined"
      />
      <p v-if="errors.content" id="comment-content-error" class="field__error">
        {{ errors.content }}
      </p>
    </div>

    <!-- 蜜罐字段：正常用户不可见也不会聚焦 -->
    <div class="comment-form__hp" aria-hidden="true">
      <label for="comment-company">公司</label>
      <input
        id="comment-company"
        v-model="honeypot"
        type="text"
        name="company"
        tabindex="-1"
        autocomplete="off"
      />
    </div>

    <p v-if="success" class="comment-form__success" role="status">
      {{ success }}
      <span v-if="cooldown">（60 秒后可再次留言）</span>
    </p>
    <p v-if="failure" class="comment-form__failure" role="alert">{{ failure }}</p>

    <div class="comment-form__actions">
      <button class="btn btn--primary" type="submit" :disabled="submitting || cooldown">
        {{ submitting ? '提交中…' : cooldown ? '已提交' : '发送留言' }}
      </button>
      <span class="comment-form__note">纯文本，支持换行；请勿提交敏感信息。</span>
    </div>
  </form>
</template>

<style scoped>
.comment-form {
  padding: 26px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.comment-form__head {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 8px 20px;
  margin-bottom: 20px;
}

.comment-form__kicker {
  margin-bottom: 4px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.56rem;
  letter-spacing: 0.18em;
}

.comment-form__title {
  color: var(--ink);
  font-size: 1.15rem;
  font-weight: 700;
}

.comment-form__hint {
  color: var(--muted);
  font-size: 0.68rem;
}

.comment-form__row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}

.field {
  margin-bottom: 14px;
}

.field__label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
}

.field__label {
  display: block;
  margin-bottom: 6px;
  color: var(--ink-2);
  font-size: 0.72rem;
  font-weight: 600;
}

.field__counter {
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.6rem;
}

.field__input {
  display: block;
  width: 100%;
  padding: 10px 12px;
  color: var(--ink);
  font-family: inherit;
  font-size: 0.84rem;
  line-height: 1.6;
  background: var(--paper);
  border: 1px solid var(--line);
  border-radius: 10px;
  outline: none;
  transition:
    border-color 0.2s var(--ease),
    background 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.field__input::placeholder {
  color: var(--muted);
  opacity: 0.75;
}

.field__input:focus {
  background: var(--surface);
  border-color: var(--violet);
  box-shadow: 0 0 0 3px var(--violet-soft);
}

.field__input[aria-invalid='true'] {
  border-color: #c75454;
}

.field__textarea {
  min-height: 132px;
  resize: vertical;
}

.field__error {
  margin-top: 4px;
  color: #b54141;
  font-size: 0.66rem;
}

.comment-form__hp {
  position: absolute;
  left: -9999px;
  width: 1px;
  height: 1px;
  overflow: hidden;
}

.comment-form__success,
.comment-form__failure {
  margin-bottom: 12px;
  padding: 9px 12px;
  font-size: 0.75rem;
  line-height: 1.6;
  border-radius: 9px;
}

.comment-form__success {
  color: #2b6f56;
  background: #e9f5ee;
}

.comment-form__failure {
  color: #a53f3f;
  background: #faecec;
}

.comment-form__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.comment-form__actions .btn {
  min-height: 38px;
  padding: 8px 20px;
  font-size: 0.78rem;
}

.comment-form__actions .btn:disabled {
  opacity: 0.55;
}

.comment-form__note {
  color: var(--muted);
  font-size: 0.62rem;
}

@media (max-width: 640px) {
  .comment-form {
    padding: 20px;
  }

  .comment-form__row {
    grid-template-columns: minmax(0, 1fr);
    gap: 0;
  }
}
</style>
