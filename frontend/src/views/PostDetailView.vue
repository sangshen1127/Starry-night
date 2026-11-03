<script setup>
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
import { formatDate, formatViewCount, toDateTimeAttr } from '@/utils/date'

const COMMENT_PAGE_SIZE = 10

const route = useRoute()

const postId = computed(() => Number(route.params.id))
const post = ref(null)
const postLoading = ref(true)
const postError = ref('')
const notFound = ref(false)

const comments = ref([])
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

function isInvalidId(id) {
  return !Number.isInteger(id) || id < 1
}

async function loadPost() {
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

async function loadComments() {
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

function changeCommentPage(nextPage) {
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

      <div v-if="postLoading" class="detail-loading glass-panel" aria-busy="true" aria-live="polite">
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

      <div v-else-if="notFound" class="detail-state glass-panel">
        <span class="detail-state__mark" aria-hidden="true">✦</span>
        <p class="detail-state__kicker">404 · LOST IN THE NIGHT</p>
        <h1>这个夜晚没有这一页</h1>
        <p>它可能被删除、转为草稿，或者链接写错了。星星不会怪你。</p>
        <div class="detail-state__actions">
          <RouterLink class="btn btn--primary" to="/posts">回到文章列表</RouterLink>
          <RouterLink class="btn btn--ghost" to="/">回到首页</RouterLink>
        </div>
      </div>

      <div v-else-if="postError" class="detail-error">
        <ErrorState title="文章加载失败" :message="postError" @retry="loadPost" />
        <RouterLink class="back-link" to="/posts">← 返回文章列表</RouterLink>
      </div>

      <template v-else-if="post">
        <article class="article glass-panel" :aria-labelledby="`article-title-${post.id}`">
          <header class="article__head">
            <p class="article__kicker">
              <span aria-hidden="true">✦</span>
              NIGHT NOTE · 夜色笔记
              <span class="article__jp" aria-hidden="true">きらめき</span>
            </p>
            <h1 :id="`article-title-${post.id}`" class="article__title">
              {{ post.title }}
            </h1>
            <p v-if="post.summary" class="article__summary">{{ post.summary }}</p>

            <div class="article__meta">
              <span class="article__tag">NIGHT NOTE</span>
              <time v-if="dateTime" :datetime="dateTime">{{ published }}</time>
              <span v-if="dateTime && views" aria-hidden="true">·</span>
              <span v-if="views">{{ views }} 次阅读</span>
              <span class="article__meta-dot" aria-hidden="true">·</span>
              <span>{{ commentMeta }}</span>
            </div>
          </header>

          <div v-if="post.coverUrl" class="article__cover">
            <CoverImage :src="post.coverUrl" :tone="tone" loading="eager" />
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
            kicker="COMMENTS · 夜话"
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
  padding: 38px 0 20px;
}

.detail-container {
  max-width: 900px;
}

.back-link {
  display: inline-flex;
  align-items: center;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.02em;
  transition:
    color 0.2s var(--ease),
    text-shadow 0.2s var(--ease);
}

.back-link:hover {
  color: var(--pink);
  text-shadow: 0 0 16px rgba(255, 158, 181, 0.35);
}

.article {
  margin-top: 20px;
  padding: 38px 40px;
  border-radius: 26px;
}

.article__kicker {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
  width: fit-content;
  margin-bottom: 12px;
  padding: 4px 9px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.53rem;
  letter-spacing: 0.12em;
  background: rgba(255, 158, 181, 0.08);
  border: 1px solid rgba(255, 158, 181, 0.14);
  border-radius: 999px;
}

.article__kicker span:first-child {
  text-shadow: 0 0 14px rgba(255, 158, 181, 0.65);
}

.article__jp {
  padding-left: 7px;
  color: var(--faint);
  border-left: 1px solid rgba(255, 255, 255, 0.12);
  font-family: var(--font-display);
}

.article__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(2rem, 4.2vw, 2.8rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.35;
  text-wrap: balance;
  text-shadow: 0 0 26px rgba(255, 158, 181, 0.16);
}

.article__summary {
  max-width: 44em;
  margin-top: 12px;
  color: var(--muted);
  font-size: 0.86rem;
  line-height: 1.9;
}

.article__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
  margin-top: 18px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.56rem;
}

.article__tag {
  padding: 2px 8px;
  color: var(--cyan);
  font-size: 0.46rem;
  letter-spacing: 0.09em;
  background: rgba(126, 200, 227, 0.1);
  border: 1px solid rgba(126, 200, 227, 0.18);
  border-radius: 999px;
}

.article__meta-dot {
  color: rgba(255, 255, 255, 0.2);
}

.article__cover {
  position: relative;
  overflow: hidden;
  margin: 28px 0;
  aspect-ratio: 16 / 8.6;
  background: #161025;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 18px;
}

.article__content {
  margin-top: 28px;
}

.article__foot {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 40px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.article__foot-note {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.52rem;
}

.comments-section {
  padding-top: 42px;
  margin-top: 46px;
}

.comments-section :deep(.comment-list) {
  margin-bottom: 24px;
}

.detail-loading {
  margin-top: 20px;
  padding: 38px;
}

.detail-loading__title {
  display: block;
  width: 78%;
  height: 34px;
  margin-bottom: 12px;
  border-radius: 8px;
}

.detail-loading__title-short {
  display: block;
  width: 48%;
  height: 34px;
  margin-bottom: 18px;
  border-radius: 8px;
}

.detail-loading__meta {
  display: block;
  width: 28%;
  height: 9px;
  margin-bottom: 30px;
  border-radius: 4px;
}

.detail-loading__body {
  display: flex;
  flex-direction: column;
  gap: 13px;
}

.detail-loading__body .skeleton {
  height: 11px;
  border-radius: 4px;
}

.detail-loading__body .skeleton--short {
  width: 62%;
}

.detail-state {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  padding: 48px 40px;
  margin-top: 20px;
  border-radius: 24px;
}

.detail-state__mark {
  display: grid;
  place-items: center;
  width: 56px;
  height: 56px;
  margin-bottom: 20px;
  color: var(--pink);
  font-size: 1.1rem;
  background: rgba(255, 158, 181, 0.09);
  border: 1px solid rgba(255, 158, 181, 0.18);
  border-radius: 50%;
  box-shadow: 0 0 30px rgba(255, 158, 181, 0.16);
}

.detail-state__kicker {
  margin-bottom: 8px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.56rem;
  letter-spacing: 0.13em;
}

.detail-state h1 {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(1.8rem, 4vw, 2.5rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.35;
}

.detail-state p {
  max-width: 34em;
  margin-top: 10px;
  color: var(--muted);
  font-size: 0.82rem;
  line-height: 1.8;
}

.detail-state__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 24px;
}

.detail-error {
  padding: 22px 0;
}

.detail-error .back-link {
  margin-top: 18px;
}

@media (max-width: 720px) {
  .detail-page {
    padding-top: 28px;
  }

  .article {
    padding: 24px 20px;
    border-radius: 20px;
  }

  .article__title {
    font-size: 1.95rem;
  }

  .article__cover {
    margin: 22px 0;
    aspect-ratio: 16 / 10;
    border-radius: 15px;
  }

  .detail-state {
    padding: 34px 24px;
  }

  .comments-section {
    padding-top: 34px;
    margin-top: 36px;
  }
}
</style>
