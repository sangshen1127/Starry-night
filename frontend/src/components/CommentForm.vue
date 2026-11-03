<script setup>
import { onBeforeUnmount, reactive, ref } from 'vue'
import { submitComment } from '@/api/comments'
import { ApiError } from '@/api/http'

const props = defineProps({
  postId: { type: Number, required: true },
})

const form = reactive({
  nickname: '',
  email: '',
  website: '',
  content: '',
})

const errors = reactive({})

const honeypot = ref('')
const submitting = ref(false)
const success = ref('')
const failure = ref('')
const cooldown = ref(false)
let cooldownTimer

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function clearErrors() {
  errors.nickname = undefined
  errors.email = undefined
  errors.website = undefined
  errors.content = undefined
}

function validate() {
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

function startCooldown() {
  cooldown.value = true
  window.clearTimeout(cooldownTimer)
  cooldownTimer = window.setTimeout(() => {
    cooldown.value = false
  }, 60_000)
}

async function handleSubmit() {
  success.value = ''
  failure.value = ''

  if (!validate() || submitting.value || cooldown.value) return

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
  <form class="comment-form glass-card" novalidate @submit.prevent="handleSubmit">
    <div class="comment-form__head">
      <div>
        <p class="comment-form__kicker">LEAVE A NOTE · 夜话</p>
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
  padding: 24px;
  border-radius: 20px;
}

.comment-form:hover {
  transform: none;
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
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.53rem;
  letter-spacing: 0.14em;
}

.comment-form__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 1.2rem;
  font-weight: 400;
}

.comment-form__hint {
  color: var(--faint);
  font-size: 0.62rem;
  line-height: 1.6;
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
  font-size: 0.68rem;
  font-weight: 600;
}

.field__counter {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.55rem;
}

.field__input {
  display: block;
  width: 100%;
  padding: 11px 13px;
  color: var(--ink);
  font-family: inherit;
  font-size: 0.8rem;
  line-height: 1.6;
  background: rgba(10, 7, 19, 0.32);
  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 10px;
  outline: none;
  transition:
    border-color 0.2s var(--ease),
    background 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.field__input::placeholder {
  color: rgba(255, 255, 255, 0.28);
}

.field__input:focus {
  background: rgba(10, 7, 19, 0.45);
  border-color: rgba(255, 158, 181, 0.55);
  box-shadow: 0 0 0 3px rgba(255, 158, 181, 0.08);
}

.field__input[aria-invalid='true'] {
  border-color: rgba(255, 120, 120, 0.65);
}

.field__textarea {
  min-height: 132px;
  resize: vertical;
}

.field__error {
  margin-top: 4px;
  color: #ffb4b4;
  font-size: 0.62rem;
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
  font-size: 0.7rem;
  line-height: 1.6;
  border-radius: 9px;
}

.comment-form__success {
  color: #a8f0d0;
  background: rgba(64, 180, 128, 0.1);
  border: 1px solid rgba(64, 180, 128, 0.16);
}

.comment-form__failure {
  color: #ffc0c0;
  background: rgba(200, 70, 80, 0.1);
  border: 1px solid rgba(255, 120, 120, 0.16);
}

.comment-form__actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;
}

.comment-form__actions .btn {
  min-height: 37px;
  padding: 7px 19px;
  font-size: 0.7rem;
}

.comment-form__note {
  color: var(--faint);
  font-size: 0.58rem;
}

@media (max-width: 640px) {
  .comment-form {
    padding: 18px;
  }

  .comment-form__row {
    grid-template-columns: minmax(0, 1fr);
    gap: 0;
  }
}
</style>
