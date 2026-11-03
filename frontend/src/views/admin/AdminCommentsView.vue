<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'
import Pagination from '@/components/Pagination.vue'
import {
  approveAdminComment,
  deleteAdminComment,
  fetchAdminComments,
} from '@/api/admin'
import { ApiError } from '@/api/http'
import { formatCommentDate } from '@/utils/date'
import { getInitial } from '@/utils/url'

const PAGE_SIZE = 10

const statusTabs = [
  { value: 'pending', label: '待审核' },
  { value: 'approved', label: '已通过' },
  { value: 'spam', label: '垃圾评论' },
  { value: '', label: '全部' },
]

const status = ref('pending')
const page = ref(1)
const result = ref(null)
const loading = ref(true)
const error = ref('')
const operationError = ref('')
const operationMessage = ref('')
const actingId = ref(null)
const deletingId = ref(null)
const pendingDelete = ref(null)
let deleteTimer = 0
let requestId = 0

const totalPages = computed(() =>
  Math.max(1, Math.ceil((result.value?.total || 0) / PAGE_SIZE)),
)

function statusLabel(value) {
  if (value === 'approved') return '已通过'
  if (value === 'spam') return '垃圾'
  return '待审核'
}

function statusClass(value) {
  if (value === 'approved') return 'approved'
  if (value === 'spam') return 'spam'
  return 'pending'
}

function isWebUrl(value) {
  return /^https?:\/\//i.test(value || '')
}

async function load() {
  const request = ++requestId
  loading.value = true
  error.value = ''

  try {
    const data = await fetchAdminComments({
      page: page.value,
      size: PAGE_SIZE,
      status: status.value || undefined,
    })
    if (request !== requestId) return

    const lastPage = Math.max(1, Math.ceil((data.total || 0) / PAGE_SIZE))
    if (page.value > lastPage) {
      page.value = lastPage
      return load()
    }
    result.value = data
  } catch (caught) {
    if (request !== requestId) return
    error.value =
      caught instanceof ApiError ? caught.message : '评论列表加载失败，请稍后再试'
  } finally {
    if (request === requestId) loading.value = false
  }
}

function selectStatus(nextStatus) {
  status.value = nextStatus
  page.value = 1
  operationMessage.value = ''
  operationError.value = ''
  void load()
}

function changePage(next) {
  page.value = next
  void load()
}

async function approve(comment) {
  actingId.value = comment.id
  operationError.value = ''
  operationMessage.value = ''

  try {
    const response = await approveAdminComment(comment.id)
    operationMessage.value = response.message || '评论已通过'
    if (status.value === 'pending') {
      await load()
    } else {
      comment.status = 'approved'
    }
  } catch (caught) {
    operationError.value =
      caught instanceof ApiError ? caught.message : '审核失败，请稍后再试'
  } finally {
    actingId.value = null
  }
}

function askDelete(comment) {
  operationError.value = ''
  window.clearTimeout(deleteTimer)
  if (pendingDelete.value === comment.id) {
    void removeComment(comment)
    return
  }
  pendingDelete.value = comment.id
  deleteTimer = window.setTimeout(() => {
    if (pendingDelete.value === comment.id) pendingDelete.value = null
  }, 4200)
}

async function removeComment(comment) {
  pendingDelete.value = null
  deletingId.value = comment.id
  operationError.value = ''

  try {
    await deleteAdminComment(comment.id)
    if (result.value?.list?.length === 1 && page.value > 1) page.value -= 1
    await load()
  } catch (caught) {
    operationError.value =
      caught instanceof ApiError ? caught.message : '删除失败，请稍后再试'
  } finally {
    deletingId.value = null
  }
}

onMounted(load)
onBeforeUnmount(() => window.clearTimeout(deleteTimer))
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div class="admin-page-head__copy">
        <p class="admin-eyebrow">COMMENT QUEUE · 门外的人</p>
        <h2 class="admin-page-title">评论审核</h2>
        <p class="admin-page-lead">
          留言默认在门外等一等。通过后才会出现在文章下面；垃圾评论可以直接删掉。
        </p>
      </div>
      <div class="admin-page-head__actions">
        <button class="admin-btn" type="button" :disabled="loading" @click="load">
          {{ loading ? '刷新中…' : '刷新队列' }}
        </button>
      </div>
    </header>

    <section class="admin-toolbar">
      <div class="admin-tabs" role="tablist" aria-label="评论状态">
        <button
          v-for="tab in statusTabs"
          :key="tab.value"
          class="admin-tab"
          :class="{ 'is-active': status === tab.value }"
          type="button"
          role="tab"
          :aria-selected="status === tab.value"
          @click="selectStatus(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>
    </section>

    <p v-if="operationError" class="admin-error" role="alert">{{ operationError }}</p>
    <p v-if="operationMessage" class="admin-success" role="status">{{ operationMessage }}</p>

    <section v-if="loading" class="admin-panel">
      <div v-for="index in 4" :key="index" class="admin-skeleton admin-skeleton--lg" style="margin-bottom: 10px" />
    </section>

    <section v-else-if="error" class="admin-state">
      <span class="admin-state__mark" aria-hidden="true">✧</span>
      <div class="admin-state__copy">
        <p class="admin-state__title">评论队列暂时断线</p>
        <p>{{ error }}</p>
      </div>
      <button class="admin-btn admin-btn--sm" type="button" @click="load">重新加载</button>
    </section>

    <section v-else-if="!result?.list?.length" class="admin-state">
      <span class="admin-state__mark" aria-hidden="true">◌</span>
      <div class="admin-state__copy">
        <p class="admin-state__title">这里很安静</p>
        <p>当前筛选下没有评论。可以换一个状态看看。</p>
      </div>
      <button class="admin-btn admin-btn--sm" type="button" @click="selectStatus('')">
        查看全部
      </button>
    </section>

    <template v-else>
      <article v-for="comment in result.list" :key="comment.id" class="admin-comment">
        <div class="admin-comment__head">
          <span class="admin-comment__avatar" aria-hidden="true">
            {{ getInitial(comment.nickname || '?') }}
          </span>
          <span class="admin-comment__name">{{ comment.nickname || '匿名旅人' }}</span>
          <span class="admin-comment__meta">{{ formatCommentDate(comment.createdAt) }}</span>
          <span class="admin-comment__badges">
            <span class="admin-badge" :class="`admin-badge--${statusClass(comment.status)}`">
              {{ statusLabel(comment.status) }}
            </span>
          </span>
        </div>

        <p class="admin-comment__content">{{ comment.content }}</p>

        <div class="admin-comment__foot">
          <div class="admin-comment__details">
            <span>POST #{{ comment.postId }}</span>
            <RouterLink
              v-if="comment.postId"
              class="admin-link"
              :to="{ name: 'post', params: { id: comment.postId } }"
              target="_blank"
            >
              {{ comment.postTitle || '查看文章' }} ↗
            </RouterLink>
            <span v-if="comment.email">MAIL {{ comment.email }}</span>
            <span v-if="comment.ip">IP {{ comment.ip }}</span>
            <a
              v-if="isWebUrl(comment.website)"
              class="admin-link"
              :href="comment.website"
              target="_blank"
              rel="noopener noreferrer nofollow"
            >
              WEBSITE ↗
            </a>
          </div>

          <div class="admin-comment__actions">
            <button
              v-if="comment.status !== 'approved'"
              class="admin-btn admin-btn--sm"
              type="button"
              :disabled="actingId === comment.id"
              @click="approve(comment)"
            >
              {{ actingId === comment.id ? '通过中…' : '通过审核' }}
            </button>
            <button
              class="admin-btn admin-btn--sm admin-btn--danger"
              type="button"
              :disabled="deletingId === comment.id"
              @click="askDelete(comment)"
            >
              {{
                deletingId === comment.id
                  ? '删除中…'
                  : pendingDelete === comment.id
                    ? '确认删除？'
                    : '删除'
              }}
            </button>
          </div>
        </div>
      </article>
    </template>

    <div v-if="!loading && !error && result" class="admin-pagination-wrap">
      <Pagination
        :page="Number(result.page) || page"
        :total-pages="totalPages"
        @change="changePage"
      />
    </div>
  </div>
</template>
