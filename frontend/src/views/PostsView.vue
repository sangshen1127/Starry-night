<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import Pagination from '@/components/Pagination.vue'
import PostCard from '@/components/PostCard.vue'
import PostSkeleton from '@/components/PostSkeleton.vue'
import { fetchPosts } from '@/api/posts'
import { ApiError } from '@/api/http'
import type { PageResult, PostSummary } from '@/api/types'

const PAGE_SIZE = 8

const route = useRoute()
const router = useRouter()

const keyword = computed(() =>
  typeof route.query.keyword === 'string' ? route.query.keyword.trim() : '',
)
const page = computed(() => {
  const parsed = Number(typeof route.query.page === 'string' ? route.query.page : 1)
  return Number.isFinite(parsed) && parsed >= 1 ? Math.trunc(parsed) : 1
})

const searchInput = ref(keyword.value)
const result = ref<PageResult<PostSummary> | null>(null)
const loading = ref(true)
const error = ref('')

let requestId = 0

function buildQuery(pageNumber: number, term: string): Record<string, string> {
  const query: Record<string, string> = {}
  if (term) query.keyword = term
  if (pageNumber > 1) query.page = String(pageNumber)
  return query
}

async function load(): Promise<void> {
  const current = ++requestId
  const requestPage = page.value
  const requestKeyword = keyword.value

  loading.value = true
  error.value = ''

  try {
    const data = await fetchPosts({
      page: requestPage,
      size: PAGE_SIZE,
      keyword: requestKeyword,
    })

    if (current !== requestId) return

    const lastPage = Math.max(1, Math.ceil((data.total || 0) / PAGE_SIZE))
    if (requestPage > lastPage) {
      await router.replace({
        name: 'posts',
        query: buildQuery(lastPage, requestKeyword),
      })
      return
    }

    result.value = data
  } catch (caught) {
    if (current !== requestId) return
    result.value = null
    error.value =
      caught instanceof ApiError ? caught.message : '文章列表加载失败，请稍后再试'
  } finally {
    if (current === requestId) loading.value = false
  }
}

watch(
  [page, keyword],
  () => {
    searchInput.value = keyword.value
    void load()
  },
  { immediate: true },
)

function submitSearch(): void {
  router.push({ name: 'posts', query: buildQuery(1, searchInput.value.trim()) })
}

function clearSearch(): void {
  searchInput.value = ''
  router.push({ name: 'posts' })
}

function goPage(nextPage: number): void {
  router.push({ name: 'posts', query: buildQuery(nextPage, keyword.value) })
}
</script>

<template>
  <div class="posts-page">
    <div class="container">
      <header class="posts-head">
        <div class="posts-head__copy">
          <p class="posts-head__kicker">
            <span aria-hidden="true">✦</span>
            ARCHIVE · 文章
          </p>
          <h1 class="posts-head__title">全部文章</h1>
          <p class="posts-head__lead">
            翻开的每一页，都是某个夜晚留下来的记录。
          </p>
        </div>

        <form class="search-form" role="search" @submit.prevent="submitSearch">
          <label class="sr-only" for="posts-search">搜索文章标题</label>
          <svg class="search-form__icon" viewBox="0 0 24 24" aria-hidden="true">
            <circle cx="11" cy="11" r="6.4" fill="none" stroke="currentColor" stroke-width="1.8" />
            <path
              d="m16 16 4 4"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
            />
          </svg>
          <input
            id="posts-search"
            v-model="searchInput"
            class="search-form__input"
            type="search"
            name="keyword"
            maxlength="50"
            placeholder="搜索文章标题…"
            autocomplete="off"
          />
          <button
            v-if="searchInput || keyword"
            class="search-form__clear"
            type="button"
            aria-label="清空搜索"
            @click="clearSearch"
          >
            ×
          </button>
          <button class="search-form__submit" type="submit">搜索</button>
        </form>
      </header>

      <div class="posts-meta">
        <span v-if="loading">正在整理夜色…</span>
        <span v-else-if="error">列表暂时没有送达</span>
        <template v-else>
          <span>共 {{ result?.total ?? 0 }} 篇文章</span>
          <span v-if="keyword" class="posts-meta__keyword">关键词「{{ keyword }}」</span>
        </template>
      </div>

      <ErrorState
        v-if="error"
        title="文章列表没能从夜色里送达"
        :message="error"
        @retry="load"
      />

      <div v-else-if="loading" class="posts-grid" aria-busy="true" aria-live="polite">
        <span class="sr-only">正在加载文章列表…</span>
        <PostSkeleton v-for="index in PAGE_SIZE" :key="index" />
      </div>

      <EmptyState
        v-else-if="!result?.list.length"
        :title="keyword ? '没有找到相关的文章' : '夜色还没落下'"
        :message="
          keyword
            ? '换个关键词试试，或者去首页看看最新文章。'
            : '这里还没有文章，等第一篇写完，再一起看星星。'
        "
      />

      <div v-else class="posts-grid">
        <PostCard v-for="post in result.list" :key="post.id" :post="post" />
      </div>

      <Pagination
        v-if="!loading && !error && result && result.total > PAGE_SIZE"
        :page="page"
        :total-pages="Math.max(1, Math.ceil(result.total / PAGE_SIZE))"
        @change="goPage"
      />
    </div>
  </div>
</template>

<style scoped>
.posts-page {
  padding: 52px 0 24px;
}

.posts-head {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  margin-bottom: 24px;
}

.posts-head__copy {
  max-width: 540px;
}

.posts-head__kicker {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.18em;
}

.posts-head__kicker span {
  color: var(--gold);
}

.posts-head__title {
  color: var(--ink);
  font-size: clamp(2.4rem, 4.4vw, 3.2rem);
  font-weight: 800;
  letter-spacing: -0.035em;
  line-height: 1.25;
}

.posts-head__lead {
  margin-top: 8px;
  color: var(--muted);
  font-size: 0.88rem;
  line-height: 1.8;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 7px;
  width: min(100%, 380px);
  min-height: 46px;
  padding: 5px 6px 5px 14px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 999px;
  box-shadow: var(--shadow-sm);
  transition:
    border-color 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.search-form:focus-within {
  border-color: var(--violet);
  box-shadow: 0 0 0 4px var(--violet-soft);
}

.search-form__icon {
  flex: none;
  width: 16px;
  height: 16px;
  color: var(--muted);
}

.search-form__input {
  flex: 1;
  min-width: 0;
  padding: 6px 0;
  color: var(--ink);
  font-family: inherit;
  font-size: 0.82rem;
  background: transparent;
  border: 0;
  outline: none;
}

.search-form__input::placeholder {
  color: var(--muted);
  opacity: 0.8;
}

.search-form__input::-webkit-search-cancel-button {
  display: none;
}

.search-form__clear {
  flex: none;
  width: 26px;
  height: 26px;
  color: var(--muted);
  font-size: 1rem;
  line-height: 1;
  border-radius: 50%;
  transition: color 0.2s var(--ease), background 0.2s var(--ease);
}

.search-form__clear:hover {
  color: var(--violet);
  background: var(--violet-soft);
}

.search-form__submit {
  flex: none;
  min-height: 34px;
  padding: 0 16px;
  color: #fff;
  font-size: 0.76rem;
  font-weight: 600;
  background: var(--violet);
  border-radius: 999px;
  transition: background 0.2s var(--ease);
}

.search-form__submit:hover {
  background: var(--violet-strong);
}

.posts-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  padding: 14px 0;
  margin-bottom: 24px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  border-top: 1px solid var(--line);
  border-bottom: 1px solid var(--line);
}

.posts-meta__keyword {
  color: var(--violet);
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 22px;
}

@media (max-width: 720px) {
  .posts-page {
    padding-top: 38px;
  }

  .posts-head {
    align-items: stretch;
    flex-direction: column;
    gap: 20px;
  }

  .posts-head__title {
    font-size: 2.2rem;
  }

  .search-form {
    width: 100%;
  }

  .posts-grid {
    grid-template-columns: minmax(0, 1fr);
    gap: 18px;
  }
}
</style>
