<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import Pagination from '@/components/Pagination.vue'
import { deleteAdminPost, fetchAdminPosts, updateAdminPostStatus } from '@/api/admin'
import { ApiError } from '@/api/http'
import { formatDate, formatViewCount } from '@/utils/date'

const PAGE_SIZE = 10

const filters = reactive({
  keyword: '',
  status: '',
})
const page = ref(1)
const result = ref(null)
const loading = ref(true)
const error = ref('')
const operationError = ref('')
const operationMessage = ref('')
const deletingId = ref(null)
const statusUpdatingId = ref(null)
const pendingDelete = ref(null)
let deleteTimer = 0
let requestId = 0

const statusOptions = [
  { value: '', label: '全部状态' },
  { value: 'published', label: '已发布' },
  { value: 'draft', label: '草稿' },
]

function statusLabel(status) {
  return status === 'published' ? '已发布' : '草稿'
}

function statusClass(status) {
  return status === 'published' ? 'published' : 'draft'
}

async function load() {
  const request = ++requestId
  loading.value = true
  error.value = ''

  try {
    const data = await fetchAdminPosts({
      page: page.value,
      size: PAGE_SIZE,
      keyword: filters.keyword.trim() || undefined,
      status: filters.status || undefined,
    })
    if (request !== requestId) return

    const totalPages = Math.max(1, Math.ceil((data.total || 0) / PAGE_SIZE))
    if (page.value > totalPages) {
      page.value = totalPages
      return load()
    }
    result.value = data
  } catch (caught) {
    if (request !== requestId) return
    error.value =
      caught instanceof ApiError ? caught.message : '文章列表加载失败，请稍后再试'
  } finally {
    if (request === requestId) loading.value = false
  }
}

function search() {
  page.value = 1
  void load()
}

function resetFilters() {
  filters.keyword = ''
  filters.status = ''
  page.value = 1
  void load()
}

function changePage(next) {
  page.value = next
  void load()
}

function askDelete(post) {
  operationError.value = ''
  window.clearTimeout(deleteTimer)
  if (pendingDelete.value === post.id) {
    void removePost(post)
    return
  }
  pendingDelete.value = post.id
  deleteTimer = window.setTimeout(() => {
    if (pendingDelete.value === post.id) pendingDelete.value = null
  }, 4200)
}

async function removePost(post) {
  pendingDelete.value = null
  deletingId.value = post.id
  operationError.value = ''

  try {
    await deleteAdminPost(post.id)
    if (result.value?.list?.length === 1 && page.value > 1) page.value -= 1
    await load()
  } catch (caught) {
    operationError.value =
      caught instanceof ApiError ? caught.message : '删除失败，请稍后再试'
  } finally {
    deletingId.value = null
  }
}

async function toggleStatus(post) {
  const nextStatus = post.status === 'published' ? 'draft' : 'published'
  statusUpdatingId.value = post.id
  operationError.value = ''
  operationMessage.value = ''

  try {
    const response = await updateAdminPostStatus(post.id, nextStatus)
    operationMessage.value = response.message || (nextStatus === 'published' ? '已发布' : '已转为草稿')
    await load()
  } catch (caught) {
    operationError.value =
      caught instanceof ApiError ? caught.message : '状态更新失败，请稍后再试'
  } finally {
    statusUpdatingId.value = null
  }
}

onMounted(load)
onBeforeUnmount(() => window.clearTimeout(deleteTimer))
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div class="admin-page-head__copy">
        <p class="admin-eyebrow">POST ARCHIVE · 星屑归档</p>
        <h2 class="admin-page-title">文章管理</h2>
        <p class="admin-page-lead">
          草稿和发布稿都收在这里。改标题、换封面，或者把写完的文章推向前台。
        </p>
      </div>
      <div class="admin-page-head__actions">
        <RouterLink class="admin-btn admin-btn--primary" :to="{ name: 'admin-post-new' }">
          <span aria-hidden="true">＋</span>
          写新文章
        </RouterLink>
      </div>
    </header>

    <section class="admin-toolbar">
      <form class="admin-filters" role="search" @submit.prevent="search">
        <label class="admin-search">
          <span class="sr-only">搜索文章标题</span>
          <svg class="admin-search__icon" viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="11" cy="11" r="6.4" fill="none" stroke="currentColor" stroke-width="1.8" />
            <path d="m16 16 4 4" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" />
          </svg>
          <input
            v-model="filters.keyword"
            class="admin-input"
            type="search"
            placeholder="搜索标题…"
            aria-label="搜索文章标题"
          />
        </label>

        <select v-model="filters.status" class="admin-select" aria-label="文章状态" @change="search">
          <option v-for="option in statusOptions" :key="option.value" :value="option.value">
            {{ option.label }}
          </option>
        </select>

        <button class="admin-btn admin-btn--sm" type="submit">筛选</button>
        <button class="admin-btn admin-btn--sm" type="button" @click="resetFilters">重置</button>
      </form>
    </section>

    <p v-if="operationError" class="admin-error" role="alert">{{ operationError }}</p>
    <p v-if="operationMessage" class="admin-success" role="status">{{ operationMessage }}</p>

    <section class="admin-panel admin-panel--flush">
      <div v-if="loading" class="admin-panel" style="border: 0; background: transparent">
        <div v-for="index in 5" :key="index" class="admin-skeleton" style="margin-bottom: 14px" />
      </div>

      <div v-else-if="error" class="admin-state">
        <span class="admin-state__mark" aria-hidden="true">✧</span>
        <div class="admin-state__copy">
          <p class="admin-state__title">文章列表暂时断线</p>
          <p>{{ error }}</p>
        </div>
        <button class="admin-btn admin-btn--sm" type="button" @click="load">重新加载</button>
      </div>

      <div v-else-if="!result?.list?.length" class="admin-state">
        <span class="admin-state__mark" aria-hidden="true">✦</span>
        <div class="admin-state__copy">
          <p class="admin-state__title">这里还没有文章</p>
          <p>{{ filters.keyword || filters.status ? '换个筛选条件看看，或者写一篇新的。' : '写下第一篇夜色笔记吧。' }}</p>
        </div>
        <RouterLink class="admin-btn admin-btn--primary admin-btn--sm" :to="{ name: 'admin-post-new' }">
          写第一篇
        </RouterLink>
      </div>

      <div v-else class="admin-table-wrap">
        <table class="admin-table">
          <thead>
            <tr>
              <th>文章</th>
              <th>状态</th>
              <th>发布时间</th>
              <th>浏览</th>
              <th style="text-align: right">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="post in result.list" :key="post.id">
              <td>
                <RouterLink
                  class="admin-table__title"
                  :to="{ name: 'admin-post-edit', params: { id: post.id } }"
                >
                  {{ post.title || '未命名文章' }}
                </RouterLink>
                <span class="admin-table__meta">#{{ post.id }} · {{ formatDate(post.updatedAt) || '还没有更新时间' }}</span>
              </td>
              <td>
                <span class="admin-badge" :class="`admin-badge--${statusClass(post.status)}`">
                  {{ statusLabel(post.status) }}
                </span>
              </td>
              <td>{{ formatDate(post.publishedAt) || '—' }}</td>
              <td>{{ formatViewCount(post.viewCount) || 0 }}</td>
              <td>
                <div class="admin-table__actions">
                  <button
                    class="admin-text-btn"
                    type="button"
                    :disabled="statusUpdatingId === post.id"
                    @click="toggleStatus(post)"
                  >
                    {{
                      statusUpdatingId === post.id
                        ? '更新中…'
                        : post.status === 'published'
                          ? '转草稿'
                          : '发布'
                    }}
                  </button>
                  <RouterLink
                    class="admin-text-btn"
                    :to="{ name: 'admin-post-edit', params: { id: post.id } }"
                  >
                    编辑
                  </RouterLink>
                  <RouterLink
                    class="admin-text-btn"
                    :to="{ name: 'post', params: { id: post.id } }"
                    target="_blank"
                  >
                    预览
                  </RouterLink>
                  <button
                    class="admin-text-btn admin-text-btn--danger"
                    type="button"
                    :disabled="deletingId === post.id"
                    @click="askDelete(post)"
                  >
                    {{
                      deletingId === post.id
                        ? '删除中…'
                        : pendingDelete === post.id
                          ? '确认删除？'
                          : '删除'
                    }}
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <div v-if="!loading && !error && result" class="admin-pagination-wrap">
      <Pagination
        :page="Number(result.page) || page"
        :total-pages="Math.max(1, Math.ceil((result.total || 0) / PAGE_SIZE))"
        @change="changePage"
      />
    </div>
  </div>
</template>
