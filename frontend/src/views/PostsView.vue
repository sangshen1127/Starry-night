<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import Pagination from '@/components/Pagination.vue'
import PostCard from '@/components/PostCard.vue'
import PostSkeleton from '@/components/PostSkeleton.vue'
import { fetchPosts } from '@/api/posts'
import { ApiError } from '@/api/http'

const PAGE_SIZE = 8

const route = useRoute()  // 获取当前路由，负责读取当前页面的路由信息
const router = useRouter()  // 获取当前路由控制器，负责控制页面跳转，修改URL

const keyword = computed(() =>
  typeof route.query.keyword === 'string' ? route.query.keyword.trim() : '',//从当前URL中获取keyword参数
)
const page = computed(() => {
  const parsed = Number(typeof route.query.page === 'string' ? route.query.page : 1)
  return Number.isFinite(parsed) && parsed >= 1 ? Math.trunc(parsed) : 1  //Number.isFinite(parsed) ：判断转换后的数字是否有效； Math.trunc(parsed) ：将数字向下取整，返回整数部分
})

const searchInput = ref(keyword.value)  // 搜索输入框的值，初始值为当前URL中的keyword参数
const result = ref(null)  //保存后端返回的文章列表数据，初始值为null
const loading = ref(true) //它用来表示文章列表是否正在加载。初始值为true，表示正在加载中。
const error = ref('') //这个变量用来保存请求失败时的错误信息。

let requestId = 0 //给每次文章请求分配一个编号，用来识别请求是否过期。

//这个函数负责根据页码和关键词，生成 Vue Router 所需要的查询参数对象。
function buildQuery(pageNumber, term) {
  const query = {}
  if (term) query.keyword = term  //如果term不为空，则将其作为keyword参数添加到query对象中
  if (pageNumber > 1) query.page = String(pageNumber)  //如果pageNumber大于1，则将其作为page参数添加到query对象中
  return query
}

//加载文章列表
async function load() {
  const current = ++requestId //为当前请求分配编号，来判断请求是否过期
  const requestPage = page.value  //获取当前页码
  const requestKeyword = keyword.value  //获取当前搜索关键字

  loading.value = true
  error.value = ''

  try {
    const data = await fetchPosts({
      page: requestPage,
      size: PAGE_SIZE,
      keyword: requestKeyword,
    })

    if (current !== requestId) return

    //这个函数就是在计算总页数，若请求页码大于本来的总页数，则直接跳转到最后一页
    const lastPage = Math.max(1, Math.ceil((data.total || 0) / PAGE_SIZE))  //计算总页数，确保至少有1页
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

function submitSearch() {
  router.push({ name: 'posts', query: buildQuery(1, searchInput.value.trim()) })
}

function clearSearch() {
  searchInput.value = ''
  router.push({ name: 'posts' })
}

function goPage(nextPage) {
  router.push({ name: 'posts', query: buildQuery(nextPage, keyword.value) })  //跳转到下一页
}
</script>

<template>
  <div class="posts-page">
    <div class="container">
      <header class="posts-head glass-panel">
        <div class="posts-head__copy">
          <p class="posts-head__kicker">
            <span aria-hidden="true">✦</span>
            ARCHIVE · 星屑归档
            <span class="posts-head__jp" aria-hidden="true">ページ</span>
          </p>
          <h1 class="posts-head__title">全部文章</h1>
          <p class="posts-head__lead">
            把每一个夜晚写下的片段，折成一张张会发光的玻璃卡片。
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
            placeholder="搜索标题，找到那颗星…"
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

        <span class="posts-head__deco" aria-hidden="true">♡</span>
      </header>

      <div class="posts-meta">
        <span v-if="loading">正在把星屑收集起来…</span>
        <span v-else-if="error">星屑暂时没有送达</span>
        <template v-else>
          <span>共 {{ result?.total ?? 0 }} 篇文章</span>
          <span v-if="keyword" class="posts-meta__keyword">
            关键词「{{ keyword }}」
          </span>
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
        :title="keyword ? '没有找到这颗星' : '夜色还没落下'"
        :message="
          keyword
            ? '换个关键词试试，也许它藏在另一片夜空里。'
            : '这里还没有文章，等第一篇写完，再一起看星星。'
        "
      />

      <div
        v-else
        class="posts-grid"
        :class="{ 'posts-grid--single': result.list.length === 1 }"
      >
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
  padding: 42px 0 24px;
}

.posts-head {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  justify-content: space-between;
  gap: 26px;
  overflow: hidden;
  padding: 36px 38px;
  background:
    linear-gradient(125deg, rgba(255, 158, 181, 0.08), transparent 48%),
    rgba(255, 255, 255, 0.045);
  border-radius: 26px;
}

.posts-head::after {
  position: absolute;
  right: -80px;
  bottom: -120px;
  width: 300px;
  height: 300px;
  border: 1px solid rgba(255, 158, 181, 0.11);
  border-radius: 50%;
  box-shadow:
    0 0 0 30px rgba(185, 103, 255, 0.03),
    0 0 0 64px rgba(126, 200, 227, 0.02);
  content: '';
}

.posts-head__copy {
  position: relative;
  z-index: 1;
  max-width: 580px;
}

.posts-head__kicker {
  display: flex;
  align-items: center;
  gap: 7px;
  width: fit-content;
  margin-bottom: 10px;
  padding: 4px 9px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.54rem;
  letter-spacing: 0.13em;
  background: rgba(255, 158, 181, 0.08);
  border: 1px solid rgba(255, 158, 181, 0.14);
  border-radius: 999px;
}

.posts-head__kicker > span:first-child {
  text-shadow: 0 0 14px rgba(255, 158, 181, 0.65);
  animation: twinkle 3.4s ease-in-out infinite;
}

.posts-head__jp {
  padding-left: 7px;
  color: var(--faint);
  border-left: 1px solid rgba(255, 255, 255, 0.12);
  font-family: var(--font-display);
  letter-spacing: 0.08em;
}

.posts-head__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(2.2rem, 4vw, 2.85rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.3;
  text-shadow: 0 0 28px rgba(255, 158, 181, 0.16);
}

.posts-head__lead {
  max-width: 36em;
  margin-top: 8px;
  color: var(--muted);
  font-size: 0.8rem;
  line-height: 1.85;
}

.posts-head__deco {
  position: absolute;
  top: 22px;
  right: 28px;
  color: rgba(255, 158, 181, 0.28);
  font-size: 1.1rem;
  animation: float-soft 5s ease-in-out infinite;
}

.search-form {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 7px;
  width: min(100%, 390px);
  min-height: 46px;
  padding: 5px 6px 5px 14px;
  background: rgba(10, 7, 19, 0.35);
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 999px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.06);
  -webkit-backdrop-filter: blur(14px);
  backdrop-filter: blur(14px);
  transition:
    border-color 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.search-form:focus-within {
  border-color: rgba(255, 158, 181, 0.5);
  box-shadow:
    0 0 0 4px rgba(255, 158, 181, 0.08),
    0 0 28px rgba(185, 103, 255, 0.08);
}

.search-form__icon {
  flex: none;
  width: 16px;
  height: 16px;
  color: var(--faint);
}

.search-form__input {
  flex: 1;
  min-width: 0;
  padding: 6px 0;
  color: var(--ink);
  font-family: inherit;
  font-size: 0.78rem;
  background: transparent;
  border: 0;
  outline: none;
}

.search-form__input::placeholder {
  color: var(--faint);
}

.search-form__input::-webkit-search-cancel-button {
  display: none;
}

.search-form__clear {
  flex: none;
  width: 25px;
  height: 25px;
  color: var(--faint);
  font-size: 0.95rem;
  border-radius: 50%;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease);
}

.search-form__clear:hover {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.1);
}

.search-form__submit {
  flex: none;
  min-height: 34px;
  padding: 0 16px;
  color: #24102a;
  font-size: 0.7rem;
  font-weight: 700;
  background: var(--gradient);
  border-radius: 999px;
  transition:
    transform 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.search-form__submit:hover {
  box-shadow: 0 6px 20px rgba(185, 103, 255, 0.25);
  transform: scale(1.03);
}

.posts-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 14px;
  padding: 14px 2px;
  margin-bottom: 18px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.58rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.posts-meta__keyword {
  color: var(--pink);
}

.posts-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.posts-grid--single {
  grid-template-columns: minmax(0, 1fr);
}

@media (max-width: 820px) {
  .posts-head {
    padding: 26px;
  }

  .search-form {
    width: 100%;
  }
}

@media (max-width: 720px) {
  .posts-page {
    padding-top: 28px;
  }

  .posts-grid {
    grid-template-columns: minmax(0, 1fr);
    gap: 12px;
  }
}

@media (max-width: 640px) {
  .posts-head {
    align-items: stretch;
    flex-direction: column;
    gap: 18px;
    padding: 22px;
    border-radius: 20px;
  }

  .posts-head__title {
    font-size: 2.2rem;
  }

  .posts-head__deco {
    top: 14px;
    right: 16px;
  }
}
</style>
