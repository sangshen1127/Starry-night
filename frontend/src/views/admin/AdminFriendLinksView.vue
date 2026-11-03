<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import {
  createAdminFriendLink,
  deleteAdminFriendLink,
  fetchAdminFriendLinks,
  updateAdminFriendLink,
  uploadAdminImage,
} from '@/api/admin'
import { ApiError } from '@/api/http'
import { getInitial, resolveAssetUrl } from '@/utils/url'

const links = ref([])
const loading = ref(true)
const error = ref('')
const operationError = ref('')
const operationMessage = ref('')
const pendingDelete = ref(null)
const deletingId = ref(null)
const saving = ref(false)
const uploading = ref(false)
const formError = ref('')
const editing = ref(null)
const fileInput = ref(null)
let deleteTimer = 0

const form = reactive({
  name: '',
  url: '',
  avatarUrl: '',
  description: '',
  sortOrder: 0,
  visible: true,
})

const avatarPreview = ref('')

function isWebUrl(value) {
  return /^https?:\/\//i.test(value || '')
}

function resetForm() {
  form.name = ''
  form.url = ''
  form.avatarUrl = ''
  form.description = ''
  form.sortOrder = 0
  form.visible = true
  avatarPreview.value = ''
  formError.value = ''
}

async function load() {
  loading.value = true
  error.value = ''
  try {
    links.value = await fetchAdminFriendLinks()
  } catch (caught) {
    error.value =
      caught instanceof ApiError ? caught.message : '友链加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

function openCreate() {
  resetForm()
  editing.value = { id: null }
  operationError.value = ''
  operationMessage.value = ''
}

function openEdit(link) {
  resetForm()
  editing.value = { id: link.id }
  form.name = link.name || ''
  form.url = link.url || ''
  form.avatarUrl = link.avatarUrl || ''
  form.description = link.description || ''
  form.sortOrder = Number(link.sortOrder) || 0
  form.visible = Boolean(link.visible)
  avatarPreview.value = resolveAssetUrl(link.avatarUrl)
  operationError.value = ''
  operationMessage.value = ''
}

function closeModal() {
  if (saving.value) return
  editing.value = null
  formError.value = ''
}

function pickAvatar() {
  fileInput.value?.click()
}

async function onAvatarChange(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return
  if (!file.type.startsWith('image/')) {
    formError.value = '头像需要是图片文件'
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    formError.value = '头像图片不能超过 5MB'
    return
  }

  uploading.value = true
  formError.value = ''
  try {
    const response = await uploadAdminImage(file)
    form.avatarUrl = response.data?.url || ''
    avatarPreview.value = resolveAssetUrl(form.avatarUrl)
  } catch (caught) {
    formError.value =
      caught instanceof ApiError ? caught.message : '头像上传失败，请稍后再试'
  } finally {
    uploading.value = false
  }
}

async function save() {
  formError.value = ''
  if (!form.name.trim()) {
    formError.value = '请填写站点名称'
    return
  }
  if (!isWebUrl(form.url.trim())) {
    formError.value = '站点地址需要以 http:// 或 https:// 开头'
    return
  }

  saving.value = true
  const payload = {
    name: form.name.trim(),
    url: form.url.trim(),
    avatarUrl: form.avatarUrl.trim() || null,
    description: form.description.trim() || null,
    sortOrder: Number(form.sortOrder) || 0,
    visible: Boolean(form.visible),
  }

  try {
    if (editing.value?.id) {
      await updateAdminFriendLink(editing.value.id, payload)
      operationMessage.value = '友链已更新'
    } else {
      await createAdminFriendLink(payload)
      operationMessage.value = '友链已添加'
    }
    editing.value = null
    await load()
  } catch (caught) {
    formError.value =
      caught instanceof ApiError ? caught.message : '保存失败，请稍后再试'
  } finally {
    saving.value = false
  }
}

function askDelete(link) {
  operationError.value = ''
  window.clearTimeout(deleteTimer)
  if (pendingDelete.value === link.id) {
    void removeLink(link)
    return
  }
  pendingDelete.value = link.id
  deleteTimer = window.setTimeout(() => {
    if (pendingDelete.value === link.id) pendingDelete.value = null
  }, 4200)
}

async function removeLink(link) {
  pendingDelete.value = null
  deletingId.value = link.id
  operationError.value = ''
  try {
    await deleteAdminFriendLink(link.id)
    await load()
  } catch (caught) {
    operationError.value =
      caught instanceof ApiError ? caught.message : '删除失败，请稍后再试'
  } finally {
    deletingId.value = null
  }
}

function handleKeydown(event) {
  if (event.key === 'Escape') closeModal()
}

onMounted(() => {
  load()
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  window.clearTimeout(deleteTimer)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div class="admin-page-head__copy">
        <p class="admin-eyebrow">LINK REGISTRY · 同夜邻居</p>
        <h2 class="admin-page-title">友链管理</h2>
        <p class="admin-page-lead">
          邻居们的小屋。排序数字越小越靠前，隐藏的条目不会出现在前台友链页。
        </p>
      </div>
      <div class="admin-page-head__actions">
        <button class="admin-btn admin-btn--primary" type="button" @click="openCreate">
          <span aria-hidden="true">＋</span>
          添加友链
        </button>
      </div>
    </header>

    <p v-if="operationError" class="admin-error" role="alert">{{ operationError }}</p>
    <p v-if="operationMessage" class="admin-success" role="status">{{ operationMessage }}</p>

    <section v-if="loading" class="admin-panel">
      <div class="admin-skeleton admin-skeleton--lg" style="margin-bottom: 10px" />
      <div class="admin-skeleton admin-skeleton--lg" />
    </section>

    <section v-else-if="error" class="admin-state">
      <span class="admin-state__mark" aria-hidden="true">✧</span>
      <div class="admin-state__copy">
        <p class="admin-state__title">友链星图暂时断线</p>
        <p>{{ error }}</p>
      </div>
      <button class="admin-btn admin-btn--sm" type="button" @click="load">重新加载</button>
    </section>

    <section v-else-if="!links.length" class="admin-state">
      <span class="admin-state__mark" aria-hidden="true">↗</span>
      <div class="admin-state__copy">
        <p class="admin-state__title">还没有邻居留下地址</p>
        <p>添加第一条友链，让夜色里多一点灯光。</p>
      </div>
      <button class="admin-btn admin-btn--primary admin-btn--sm" type="button" @click="openCreate">
        添加第一条
      </button>
    </section>

    <section v-else class="admin-friends">
      <article v-for="link in links" :key="link.id" class="admin-friend">
        <div class="admin-friend__head">
          <span class="admin-friend__avatar" aria-hidden="true">
            <img
              v-if="link.avatarUrl"
              :src="resolveAssetUrl(link.avatarUrl)"
              alt=""
              loading="lazy"
            />
            <template v-else>{{ getInitial(link.name || '?') }}</template>
          </span>
          <span style="min-width: 0">
            <span class="admin-friend__name">{{ link.name }}</span>
            <a
              v-if="isWebUrl(link.url)"
              class="admin-friend__url"
              :href="link.url"
              target="_blank"
              rel="noopener noreferrer"
            >
              {{ link.url }}
            </a>
            <span v-else class="admin-friend__url">{{ link.url }}</span>
          </span>
        </div>

        <p class="admin-friend__desc">{{ link.description || '这位邻居还没有写介绍。' }}</p>

        <div class="admin-friend__foot">
          <span class="admin-comment__details">
            <span>SORT {{ link.sortOrder ?? 0 }}</span>
            <span class="admin-badge" :class="`admin-badge--${link.visible ? 'visible' : 'hidden'}`">
              {{ link.visible ? '显示中' : '已隐藏' }}
            </span>
          </span>
          <div class="admin-comment__actions">
            <button class="admin-text-btn" type="button" @click="openEdit(link)">编辑</button>
            <button
              class="admin-text-btn admin-text-btn--danger"
              type="button"
              :disabled="deletingId === link.id"
              @click="askDelete(link)"
            >
              {{
                deletingId === link.id
                  ? '删除中…'
                  : pendingDelete === link.id
                    ? '确认删除？'
                    : '删除'
              }}
            </button>
          </div>
        </div>
      </article>
    </section>

    <div v-if="editing" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal" role="dialog" aria-modal="true" aria-labelledby="friend-modal-title">
        <div class="admin-modal__head">
          <div>
            <p class="admin-eyebrow">FRIEND LINK</p>
            <h3 id="friend-modal-title" class="admin-modal__title">
              {{ editing.id ? '编辑友链' : '添加友链' }}
            </h3>
          </div>
          <button class="admin-modal__close" type="button" aria-label="关闭" @click="closeModal">
            ✕
          </button>
        </div>

        <form class="admin-modal__form" novalidate @submit.prevent="save">
          <div class="admin-form-grid">
            <div class="admin-field admin-field--full">
              <label class="admin-label" for="friend-name">站点名称 <span>必填</span></label>
              <input id="friend-name" v-model="form.name" class="admin-input" type="text" maxlength="64" placeholder="邻居的小屋" />
            </div>

            <div class="admin-field admin-field--full">
              <label class="admin-label" for="friend-url">站点地址 <span>必填</span></label>
              <input id="friend-url" v-model="form.url" class="admin-input" type="url" placeholder="https://example.com/" />
            </div>

            <div class="admin-field admin-field--full">
              <label class="admin-label" for="friend-desc">介绍 <span>可选</span></label>
              <textarea id="friend-desc" v-model="form.description" class="admin-textarea" maxlength="200" style="min-height: 74px" placeholder="一句话介绍这位邻居…" />
            </div>

            <div class="admin-field admin-field--full">
              <label class="admin-label" for="friend-avatar">头像地址 <span>可选</span></label>
              <div style="display: flex; flex-wrap: wrap; gap: 8px; align-items: center">
                <input id="friend-avatar" v-model="form.avatarUrl" class="admin-input" type="text" inputmode="url" spellcheck="false" placeholder="/uploads/2026/11/avatar.png" style="flex: 1; min-width: 150px" />
                <button class="admin-btn admin-btn--sm" type="button" :disabled="uploading" @click="pickAvatar">
                  {{ uploading ? '上传中…' : '上传头像' }}
                </button>
                <input ref="fileInput" class="sr-only" type="file" accept="image/png,image/jpeg,image/webp,image/gif" @change="onAvatarChange" />
              </div>
              <img v-if="avatarPreview" class="admin-avatar-preview" :src="avatarPreview" alt="友链头像预览" style="margin-top: 8px" />
            </div>

            <div class="admin-field">
              <label class="admin-label" for="friend-sort">排序 <span>越小越靠前</span></label>
              <input id="friend-sort" v-model.number="form.sortOrder" class="admin-input" type="number" step="1" />
            </div>

            <div class="admin-field" style="justify-content: flex-end">
              <label class="admin-checkbox">
                <input v-model="form.visible" type="checkbox" />
                在前台显示
              </label>
            </div>
          </div>

          <p v-if="formError" class="admin-error" role="alert">{{ formError }}</p>

          <div class="admin-modal__actions">
            <button class="admin-btn" type="button" :disabled="saving" @click="closeModal">取消</button>
            <button class="admin-btn admin-btn--primary" type="submit" :disabled="saving">
              {{ saving ? '保存中…' : editing.id ? '保存修改' : '添加友链' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
