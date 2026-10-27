<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import ArticleContent from '@/components/ArticleContent.vue'
import CommentForm from '@/components/CommentForm.vue'
import CommentList from '@/components/CommentList.vue'
import CoverImage from '@/components/CoverImage.vue'
import ErrorState from '@/components/ErrorState.vue'
import SectionHeading from '@/components/SectionHeading.vue'
import { ApiError } from '@/api/http'
import { fetchPostComments } from '@/api/comments'
import { fetchPostDetail } from '@/api/posts'
import type { PostDetail, PublicComment } from '@/api/types'
import { formatDate, formatViewCount, toDateTimeAttr } from '@/utils/date'

const COMMENT_PAGE_SIZE = 10

const route = useRoute()

const postId = computed(() => Number(route.params.id))
const post = ref<PostDetail | null>(null)
const postLoading = ref(true)
const postError = ref('')
const notFound = ref(false)

const comments = ref<PublicComment[]>([])
const commentPage = ref(1)
const commentTotal = ref(0)
const commentsLoading = ref(true)
const commentsError = ref('')

let postRequestId = 0
let commentsRequestId = 0

const published = computed(() => formatDate(post.value?.publishedAt))
const dateTime = computed(() => toDateTimeAttr(post.value?.publishedAt))
const views = computed(() => formatViewCount(post.value?.viewCount))
const tone = computed(() => Math.abs(Number(post.value?.id) || 0) % 4)

const commentMeta = computed(() => {
  if (commentsLoading.value) return 'SYNCING COMMENTS…'
  if (commentsError.value) return 'COMMENTS OFFLINE'
  return commentTotal.value ? `${commentTotal.value} 条已通过` : '还没有留言'
})

function isInvalidId(id: number): boolean {
  return !Number.isInteger(id) || id < 1
}

async function loadPost(): Promise<void> {
  const id = postId.value
  const request = ++postRequestId

  postLoading.value = true
  postError.value = ''
  notFound.value = false
  post.value = null

  if (isInvalidId(id)) {
    notFound.value = true
    postLoading.value = false
    return
  }

  try {
    const data = await fetchPostDetail(id)
    if (request !== postRequestId || id !== postId.value) return
    post.value = data
    document.title = `${data.title} · Start Night`
  } catch (caught) {
    if (request !== postRequestId || id !== postId.value) return
    if (caught instanceof ApiError && caught.status === 404) {
      notFound.value = true
    } else {
      postError.value =
        caught instanceof ApiError ? caught.message : '文章加载失败，请稍后再试'
    }
  } finally {
    if (request === postRequestId) postLoading.value = false
  }
}

async function loadComments(): Promise<void> {
  const id = postId.value
  if (isInvalidId(id)) return

  const request = ++commentsRequestId
  commentsLoading.value = true
  commentsError.value = ''

  try {
    const data = await fetchPostComments(id, {
      page: commentPage.value,
      size: COMMENT_PAGE_SIZE,
    })
    if (request !== commentsRequestId || id !== postId.value) return
    comments.value = data.list
    commentTotal.value = data.total
  } catch (caught) {
    if (request !== commentsRequestId || id !== postId.value) return
    comments.value = []
    commentTotal.value = 0
    commentsError.value =
      caught instanceof ApiError ? caught.message : '留言加载失败，请稍后再试'
  } finally {
    if (request === commentsRequestId) commentsLoading.value = false
  }
}

function changeCommentPage(nextPage: number): void {
  commentPage.value = nextPage
  void loadComments()
}

watch(
  postId,
  () => {
    commentPage.value = 1
    comments.value = []
    commentTotal.value = 0
    void loadPost()
    void loadComments()
  },
  { immediate: true },
)
</script>

<template>
  <div class="detail-page">
    <div class="container detail-container">
      <RouterLink class="back-link" to="/posts">← 返回文章列表</RouterLink>

      <div v-if="postLoading" class="detail-loading" aria-busy="true" aria-live="polite">
        <span class="sr-only">正在加载文章…</span>
        <span class="skeleton detail-loading__title" />
        <span class="skeleton detail-loading__title-short" />
        <span class="skeleton detail-loading__meta" />
        <div class="detail-loading__body">
          <span class="skeleton" />
          <span class="skeleton" />
          <span class="skeleton skeleton--short" />
          <span class="skeleton" />
        </div>
      </div>

      <div v-else-if="notFound" class="detail-state">
        <span class="detail-state__mark" aria-hidden="true">✦</span>
        <p class="detail-state__kicker">404 · ARTICLE</p>
        <h1>这篇文章不在这片夜色里</h1>
        <p>它可能被删除、转为草稿，或者链接写错了。</p>
        <div class="detail-state__actions">
          <RouterLink class="btn btn--primary" to="/posts">回到文章列表</RouterLink>
          <RouterLink class="btn btn--quiet" to="/">回到首页</RouterLink>
        </div>
      </div>

      <div v-else-if="postError" class="detail-error">
        <ErrorState
          title="文章加载失败"
          :message="postError"
          @retry="loadPost"
        />
        <RouterLink class="back-link" to="/posts">← 返回文章列表</RouterLink>
      </div>

      <template v-else-if="post">
        <article class="article" :aria-labelledby="`article-title-${post.id}`">
          <header class="article__head">
            <p class="article__kicker">
              <span aria-hidden="true">✦</span>
              NIGHT NOTE · 夜色笔记
            </p>
            <h1 :id="`article-title-${post.id}`" class="article__title">
              {{ post.title }}
            </h1>
            <p v-if="post.summary" class="article__summary">{{ post.summary }}</p>

            <div class="article__meta">
              <time v-if="dateTime" :datetime="dateTime">{{ published }}</time>
              <span v-if="dateTime && views" aria-hidden="true">·</span>
              <span v-if="views">{{ views }} 次阅读</span>
              <span aria-hidden="true">·</span>
              <span>{{ commentMeta }}</span>
            </div>
          </header>

          <div v-if="post.coverUrl" class="article__cover">
            <CoverImage
              :src="post.coverUrl"
              :tone="tone"
              loading="eager"
            />
          </div>

          <div class="article__content">
            <ArticleContent :content="post.content" />
          </div>

          <footer class="article__foot">
            <RouterLink class="back-link" to="/posts">← 所有文章</RouterLink>
            <span class="article__foot-note">Start Night · {{ published }}</span>
          </footer>
        </article>

        <section id="comments" class="comments-section" aria-labelledby="comments-heading">
          <SectionHeading
            kicker="COMMENTS · 留言"
            title="留言"
            heading-id="comments-heading"
            :meta="commentMeta"
          />

          <CommentList
            :comments="comments"
            :loading="commentsLoading"
            :error="commentsError"
            :total="commentTotal"
            :page="commentPage"
            :page-size="COMMENT_PAGE_SIZE"
            @retry="loadComments"
            @page-change="changeCommentPage"
          />

          <CommentForm :post-id="post.id" />
        </section>
      </template>
    </div>
  </div>
</template>

<style scoped>
.detail-page {
  padding: 42px 0 24px;
}

.detail-container {
  max-width: 860px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.68rem;
  letter-spacing: 0.02em;
  transition: color 0.2s var(--ease);
}

.back-link:hover {
  color: var(--violet);
}

.article {
  margin-top: 22px;
}

.article__kicker {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.16em;
}

.article__kicker span {
  color: var(--gold);
}

.article__title {
  color: var(--ink);
  font-size: clamp(2.2rem, 4.5vw, 3.1rem);
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.3;
  text-wrap: balance;
}

.article__summary {
  max-width: 42em;
  margin-top: 14px;
  color: var(--muted);
  font-size: 0.92rem;
  line-height: 1.9;
}

.article__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 20px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  letter-spacing: 0.02em;
}

.article__cover {
  position: relative;
  overflow: hidden;
  margin: 30px 0;
  aspect-ratio: 16 / 9;
  background: var(--night-1);
  border: 1px solid var(--line);
  border-radius: 20px;
}

.article__content {
  margin-top: 30px;
}

.article__foot {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 46px;
  padding-top: 18px;
  border-top: 1px solid var(--line);
}

.article__foot-note {
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.6rem;
}

.comments-section {
  padding-top: 48px;
  margin-top: 56px;
  border-top: 1px solid var(--line);
}

.comments-section :deep(.comment-list) {
  margin-bottom: 28px;
}

.detail-loading {
  padding-top: 30px;
}

.detail-loading__title {
  display: block;
  width: 78%;
  height: 34px;
  margin-bottom: 12px;
  border-radius: 7px;
}

.detail-loading__title-short {
  display: block;
  width: 48%;
  height: 34px;
  margin-bottom: 18px;
  border-radius: 7px;
}

.detail-loading__meta {
  display: block;
  width: 28%;
  height: 9px;
  margin-bottom: 34px;
  border-radius: 4px;
}

.detail-loading__body {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.detail-loading__body .skeleton {
  height: 12px;
  border-radius: 4px;
}

.detail-loading__body .skeleton--short {
  width: 62%;
}

.detail-state {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 54px 0 40px;
}

.detail-state__mark {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  margin-bottom: 20px;
  color: var(--star);
  background: var(--night-1);
  border-radius: 50%;
  box-shadow: 0 0 0 8px rgba(100, 84, 200, 0.08);
}

.detail-state__kicker {
  margin-bottom: 8px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.15em;
}

.detail-state h1 {
  color: var(--ink);
  font-size: clamp(2rem, 4vw, 2.7rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  line-height: 1.3;
}

.detail-state p {
  max-width: 34em;
  margin-top: 12px;
  color: var(--muted);
  font-size: 0.9rem;
  line-height: 1.8;
}

.detail-state__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 26px;
}

.btn--quiet {
  color: var(--ink-2);
  border-color: var(--line-strong);
}

.btn--quiet:hover {
  color: var(--violet);
  background: var(--violet-soft);
  border-color: var(--violet-soft);
}

.detail-error {
  padding: 24px 0;
}

.detail-error .back-link {
  margin-top: 20px;
}

@media (max-width: 640px) {
  .detail-page {
    padding-top: 30px;
  }

  .article__title {
    font-size: 2rem;
  }

  .article__cover {
    margin: 24px 0;
    aspect-ratio: 16 / 10;
    border-radius: 16px;
  }

  .article__foot {
    margin-top: 34px;
  }

  .comments-section {
    padding-top: 34px;
    margin-top: 40px;
  }
}
</style>
