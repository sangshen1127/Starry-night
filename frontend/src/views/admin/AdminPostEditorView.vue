<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  createAdminPost,
  fetchAdminPost,
  updateAdminPost,
  uploadAdminImage,
} from '@/api/admin'
import { ApiError } from '@/api/http'
import { resolveAssetUrl } from '@/utils/url'

const route = useRoute()
const router = useRouter()
const fileInput = ref(null)

const postId = computed(() => {
  const value = Number(route.params.id)
  return Number.isInteger(value) && value > 0 ? value : null
})
const isEdit = computed(() => Boolean(postId.value))

const form = reactive({
  title: '',
  summary: '',
  coverUrl: '',
  content: '',
  status: 'draft',
})

const loading = ref(false)
const loadError = ref('')
const saving = ref(false)
const uploading = ref(false)
const formError = ref('')
const savedMessage = ref('')

const coverPreview = computed(() => resolveAssetUrl(form.coverUrl))

function resetForm() {
  form.title = ''
  form.summary = ''
  form.coverUrl = ''
  form.content = ''
  form.status = 'draft'
}

async function load() {
  if (!isEdit.value) {
    resetForm()
    return
  }

  loading.value = true
  loadError.value = ''
  formError.value = ''

  try {
    const post = await fetchAdminPost(postId.value)
    form.title = post.title || ''
    form.summary = post.summary || ''
    form.coverUrl = post.coverUrl || ''
    form.content = post.content || ''
    form.status = post.status === 'published' ? 'published' : 'draft'
  } catch (caught) {
    loadError.value =
      caught instanceof ApiError ? caught.message : '文章加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

async function save() {
  formError.value = ''
  savedMessage.value = ''

  if (!form.title.trim()) {
    formError.value = '文章标题不得为空'
    return
  }
  if (!form.content.trim()) {
    formError.value = '文章内容不得为空'
    return
  }

  saving.value = true
  const payload = {
    title: form.title.trim(),
    summary: form.summary.trim() || null,
    coverUrl: form.coverUrl.trim() || null,
    content: form.content,
    status: form.status,
  }

  try {
    if (isEdit.value) {
      const response = await updateAdminPost(postId.value, payload)
      savedMessage.value = response.message || '文章已保存'
    } else {
      const response = await createAdminPost(payload)
      const newId = response.data?.id
      if (newId) {
        savedMessage.value = '文章已创建，正在打开编辑器…'
        await router.replace({
          name: 'admin-post-edit',
          params: { id: newId },
        })
        await load()
      } else {
        savedMessage.value = '文章已创建'
      }
    }
  } catch (caught) {
    formError.value =
      caught instanceof ApiError ? caught.message : '保存失败，请稍后再试'
  } finally {
    saving.value = false
  }
}

function pickCover() {
  fileInput.value?.click()
}

async function onCoverChange(event) {
  const file = event.target.files?.[0]
  event.target.value = ''
  if (!file) return

  if (!file.type.startsWith('image/')) {
    formError.value = '封面需要是图片文件'
    return
  }
  if (file.size > 5 * 1024 * 1024) {
    formError.value = '封面图片不能超过 5MB'
    return
  }

  uploading.value = true
  formError.value = ''
  try {
    const response = await uploadAdminImage(file)
    form.coverUrl = response.data?.url || ''
  } catch (caught) {
    formError.value =
      caught instanceof ApiError ? caught.message : '封面上传失败，请稍后再试'
  } finally {
    uploading.value = false
  }
}

function goList() {
  void router.push({ name: 'admin-posts' })
}

watch(postId, () => {
  void load()
})

onMounted(load)
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div class="admin-page-head__copy">
        <p class="admin-eyebrow">POST EDITOR · {{ isEdit ? 'EDIT' : 'NEW' }}</p>
        <h2 class="admin-page-title">
          {{ isEdit ? '编辑文章' : '写一篇夜色笔记' }}
        </h2>
        <p class="admin-page-lead">
          正文支持 Markdown。先存草稿，等月光合适的时候再发布。
        </p>
      </div>
      <div class="admin-page-head__actions">
        <button class="admin-btn" type="button" @click="goList">← 返回列表</button>
        <RouterLink
          v-if="isEdit && form.status === 'published'"
          class="admin-btn"
          :to="{ name: 'post', params: { id: postId } }"
          target="_blank"
        >
          前台预览 ↗
        </RouterLink>
      </div>
    </header>

    <p v-if="loadError" class="admin-error" role="alert">{{ loadError }}</p>
    <p v-if="formError" class="admin-error" role="alert">{{ formError }}</p>
    <p v-if="savedMessage" class="admin-success" role="status">{{ savedMessage }}</p>

    <div v-if="loading" class="admin-panel">
      <div class="admin-skeleton" style="width: 44%; height: 26px; margin-bottom: 18px" />
      <div class="admin-skeleton admin-skeleton--lg" style="margin-bottom: 12px" />
      <div class="admin-skeleton" style="width: 88%; margin-bottom: 10px" />
      <div class="admin-skeleton" style="width: 72%" />
    </div>

    <form
      v-else
      class="admin-editor"
      novalidate
      @submit.prevent="save"
      @keydown.ctrl.s.prevent="save"
      @keydown.meta.s.prevent="save"
    >
      <div class="admin-editor__main">
        <section class="admin-panel">
          <div class="admin-panel-head">
            <div>
              <h3 class="admin-panel-title">正文</h3>
              <p class="admin-panel-sub">MARKDOWN · 夜里写下的内容</p>
            </div>
            <span class="admin-table__meta">{{ form.content.length }} 字符</span>
          </div>

          <div class="admin-field">
            <label class="admin-label" for="admin-post-title">标题 <span>必填</span></label>
            <input
              id="admin-post-title"
              v-model="form.title"
              class="admin-input"
              type="text"
              maxlength="120"
              placeholder="给今晚的文章取一个名字"
            />
          </div>

          <div class="admin-field" style="margin-top: 13px">
            <label class="admin-label" for="admin-post-content">正文 <span>必填 · Markdown</span></label>
            <textarea
              id="admin-post-content"
              v-model="form.content"
              class="admin-textarea admin-textarea--editor"
              placeholder="# 夜色笔记&#10;&#10;在这里写下正文…"
            />
          </div>
        </section>

        <section class="admin-panel">
          <div class="admin-panel-head">
            <div>
              <h3 class="admin-panel-title">摘要</h3>
              <p class="admin-panel-sub">SUMMARY · 列表页展示</p>
            </div>
          </div>
          <div class="admin-field">
            <label class="admin-label" for="admin-post-summary">文章摘要 <span>可选</span></label>
            <textarea
              id="admin-post-summary"
              v-model="form.summary"
              class="admin-textarea"
              maxlength="300"
              style="min-height: 76px"
              placeholder="用一两句话介绍这篇夜色笔记…"
            />
            <p class="admin-help">{{ form.summary.length }}/300</p>
          </div>
        </section>
      </div>

      <aside class="admin-editor__side">
        <section class="admin-panel">
          <div class="admin-panel-head">
            <div>
              <h3 class="admin-panel-title">发布设置</h3>
              <p class="admin-panel-sub">PUBLISH SETTINGS</p>
            </div>
          </div>

          <div class="admin-field">
            <label class="admin-label" for="admin-post-status">文章状态</label>
            <select id="admin-post-status" v-model="form.status" class="admin-select" style="width: 100%">
              <option value="draft">草稿 · 不公开</option>
              <option value="published">已发布 · 公开</option>
            </select>
            <p class="admin-help">
              第一次发布时，后端会自动记录发布时间；改回草稿不会删除已有发布时间。
            </p>
          </div>

          <div class="admin-editor__bar" style="margin-top: 16px">
            <span class="admin-editor__hint">CTRL / ⌘ + S 保存</span>
            <button class="admin-btn admin-btn--primary" type="submit" :disabled="saving">
              {{ saving ? '保存中…' : isEdit ? '保存修改' : '创建文章' }}
            </button>
          </div>
        </section>

        <section class="admin-panel">
          <div class="admin-panel-head">
            <div>
              <h3 class="admin-panel-title">封面</h3>
              <p class="admin-panel-sub">COVER · 可选</p>
            </div>
          </div>

          <img v-if="coverPreview" class="admin-cover" :src="coverPreview" alt="文章封面预览" />
          <div v-else class="admin-cover-empty">NO COVER · 夜色占位</div>

          <div class="admin-field" style="margin-top: 12px">
            <label class="admin-label" for="admin-post-cover">封面地址</label>
            <input
              id="admin-post-cover"
              v-model="form.coverUrl"
              class="admin-input"
              type="text"
              inputmode="url"
              spellcheck="false"
              placeholder="/uploads/2026/11/cover.png 或 https://..."
            />
          </div>

          <input
            ref="fileInput"
            class="sr-only"
            type="file"
            accept="image/png,image/jpeg,image/webp,image/gif"
            @change="onCoverChange"
          />
          <button class="admin-btn admin-btn--sm" type="button" :disabled="uploading" @click="pickCover">
            {{ uploading ? '上传中…' : '上传本地图片' }}
          </button>
          <p class="admin-help">支持 JPG / PNG / WebP / GIF，上限 5MB。</p>
        </section>

        <section class="admin-panel">
          <div class="admin-panel-head">
            <div>
              <h3 class="admin-panel-title">写作提示</h3>
              <p class="admin-panel-sub">A NOTE FOR THE NIGHT</p>
            </div>
          </div>
          <ul class="admin-hint-list">
            <li>标题尽量短，留在卡片上会更像一颗星。</li>
            <li>摘要可以留空，前台会显示默认文案。</li>
            <li>发布前建议点一次「前台预览」看看排版。</li>
          </ul>
        </section>
      </aside>
    </form>
  </div>
</template>
