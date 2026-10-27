<script setup lang="ts">
import EmptyState from '@/components/EmptyState.vue'
import type { HomeComment } from '@/api/types'
import { formatCommentDate, toDateTimeAttr } from '@/utils/date'

withDefaults(
  defineProps<{
    comments: HomeComment[]
    loading?: boolean
    error?: string
  }>(),
  {
    loading: false,
    error: '',
  },
)
</script>

<template>
  <section id="comments" class="side-card comments-card">
    <div class="side-head">
      <div>
        <p class="side-kicker">COMMENTS · こめんと</p>
        <h2 class="side-title">最新留言</h2>
      </div>
      <span v-if="comments.length" class="side-count">{{ comments.length }}</span>
    </div>

    <div v-if="loading" class="comments-loading" aria-busy="true" aria-live="polite">
      <span class="sr-only">正在加载留言…</span>
      <span class="skeleton comments-loading__line" />
      <span class="skeleton comments-loading__line comments-loading__line--wide" />
      <span class="skeleton comments-loading__line" />
    </div>

    <p v-else-if="error" class="side-note">留言暂时没有加载出来。</p>

    <EmptyState
      v-else-if="!comments.length"
      compact
      title="还没有留言"
      message="等第一句晚安落下。"
    />

    <ul v-else class="comment-list">
      <li v-for="comment in comments" :key="comment.id" class="comment">
        <p class="comment__text">{{ comment.content }}</p>
        <div class="comment__meta">
          <span class="comment__name">{{ comment.nickname }}</span>
          <time
            v-if="comment.createdAt"
            :datetime="toDateTimeAttr(comment.createdAt) || undefined"
          >
            {{ formatCommentDate(comment.createdAt) }}
          </time>
          <RouterLink
            v-if="comment.postTitle"
            class="comment__post"
            :to="{ name: 'post', params: { id: comment.postId } }"
          >
            {{ comment.postTitle }}
          </RouterLink>
        </div>
      </li>
    </ul>
  </section>
</template>

<style scoped>
.comments-loading {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.comments-loading__line {
  height: 38px;
  border-radius: 8px;
}

.comments-loading__line--wide {
  width: 82%;
  height: 46px;
}

.comment-list {
  display: flex;
  flex-direction: column;
}

.comment {
  padding: 14px 0;
  border-top: 1px solid var(--line);
}

.comment:first-child {
  padding-top: 0;
  border-top: 0;
}

.comment:last-child {
  padding-bottom: 0;
}

.comment__text {
  display: -webkit-box;
  overflow: hidden;
  color: var(--ink-2);
  font-size: 0.8rem;
  line-height: 1.75;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.comment__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 5px 8px;
  margin-top: 7px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.58rem;
}

.comment__name {
  color: var(--ink-2);
  font-family: var(--font-sans);
  font-weight: 600;
}

.comment__post {
  flex-basis: 100%;
  max-width: 100%;
  margin-top: 1px;
  overflow: hidden;
  color: var(--violet);
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s var(--ease);
}

.comment__post:hover {
  color: var(--violet-strong);
}
</style>
