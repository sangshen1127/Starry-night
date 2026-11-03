<script setup>
import EmptyState from '@/components/EmptyState.vue'
import { formatCommentDate, toDateTimeAttr } from '@/utils/date'
import { getInitial } from '@/utils/url'

defineProps({
  comments: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
})
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
        <span class="comment__avatar" aria-hidden="true">
          {{ getInitial(comment.nickname) }}
        </span>
        <div class="comment__body">
          <p class="comment__text">{{ comment.content }}</p>
          <div class="comment__meta">
            <span class="comment__name">{{ comment.nickname }}</span>
            <time
              v-if="comment.createdAt"
              :datetime="toDateTimeAttr(comment.createdAt) || undefined"
            >
              {{ formatCommentDate(comment.createdAt) }}
            </time>
          </div>
          <RouterLink
            v-if="comment.postTitle"
            class="comment__post"
            :to="{ name: 'post', params: { id: comment.postId } }"
          >
            <span aria-hidden="true">↳</span>
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
  gap: 10px;
}

.comments-loading__line {
  height: 42px;
  border-radius: 10px;
}

.comments-loading__line--wide {
  width: 76%;
  height: 52px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.comment {
  display: flex;
  gap: 10px;
  padding: 11px 8px;
  border-radius: 12px;
  transition: background 0.2s var(--ease);
}

.comment:hover {
  background: rgba(255, 255, 255, 0.04);
}

.comment__avatar {
  display: grid;
  place-items: center;
  flex: none;
  width: 30px;
  height: 30px;
  color: #26102c;
  font-family: var(--font-mono);
  font-size: 0.62rem;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(255, 158, 181, 0.9), rgba(185, 103, 255, 0.9));
  border-radius: 50%;
  box-shadow: 0 0 16px rgba(255, 158, 181, 0.14);
}

.comment__body {
  flex: 1;
  min-width: 0;
}

.comment__text {
  display: -webkit-box;
  overflow: hidden;
  color: var(--ink-2);
  font-size: 0.75rem;
  line-height: 1.65;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.comment__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
  margin-top: 4px;
}

.comment__name {
  color: var(--pink);
  font-size: 0.62rem;
  font-weight: 600;
}

.comment__meta time {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.5rem;
}

.comment__post {
  display: flex;
  align-items: center;
  gap: 3px;
  max-width: 100%;
  margin-top: 4px;
  overflow: hidden;
  color: var(--cyan);
  font-size: 0.6rem;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.2s var(--ease);
}

.comment__post:hover {
  color: var(--pink);
}
</style>
