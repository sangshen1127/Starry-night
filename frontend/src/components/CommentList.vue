<script setup>
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import Pagination from '@/components/Pagination.vue'
import { formatCommentDate, toDateTimeAttr } from '@/utils/date'
import { getInitial, safeHttpUrl } from '@/utils/url'

defineProps({
  comments: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
  total: { type: Number, default: 0 },
  page: { type: Number, default: 1 },
  pageSize: { type: Number, default: 10 },
})

const emit = defineEmits(['retry', 'page-change'])

function websiteOf(comment) {
  return safeHttpUrl(comment.website)
}
</script>

<template>
  <div class="comment-list">
    <div v-if="loading" class="comments-loading" aria-busy="true" aria-live="polite">
      <span class="sr-only">正在加载留言…</span>
      <span class="skeleton comments-loading__item" />
      <span class="skeleton comments-loading__item" />
      <span class="skeleton comments-loading__item comments-loading__item--short" />
    </div>

    <ErrorState
      v-else-if="error"
      compact
      title="留言暂时没有加载出来"
      :message="error"
      @retry="emit('retry')"
    />

    <EmptyState
      v-else-if="!comments.length"
      compact
      title="还没有留言"
      message="来做第一个留下夜话的人吧。"
    />

    <template v-else>
      <ul class="comments">
        <li v-for="comment in comments" :key="comment.id" class="comment glass-card">
          <span class="comment__quote" aria-hidden="true">“</span>
          <span class="comment__avatar" aria-hidden="true">
            {{ getInitial(comment.nickname) }}
          </span>

          <div class="comment__body">
            <div class="comment__head">
              <a
                v-if="websiteOf(comment)"
                class="comment__name comment__name--link"
                :href="websiteOf(comment)"
                target="_blank"
                rel="noopener noreferrer"
              >
                {{ comment.nickname }}
              </a>
              <span v-else class="comment__name">{{ comment.nickname }}</span>

              <span class="comment__dot" aria-hidden="true">·</span>
              <time
                v-if="comment.createdAt"
                class="comment__time"
                :datetime="toDateTimeAttr(comment.createdAt) || undefined"
              >
                {{ formatCommentDate(comment.createdAt) }}
              </time>
            </div>

            <p class="comment__content">{{ comment.content }}</p>
          </div>
        </li>
      </ul>

      <Pagination
        compact
        :page="page"
        :total-pages="Math.max(1, Math.ceil(total / pageSize))"
        @change="emit('page-change', $event)"
      />
    </template>
  </div>
</template>

<style scoped>
.comments-loading {
  display: flex;
  flex-direction: column;
  gap: 11px;
}

.comments-loading__item {
  height: 82px;
  border-radius: 14px;
}

.comments-loading__item--short {
  width: 78%;
}

.comments {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment {
  position: relative;
  display: flex;
  gap: 12px;
  padding: 16px;
  overflow: hidden;
  border-radius: 16px;
}

.comment__quote {
  position: absolute;
  top: -10px;
  right: 12px;
  color: rgba(255, 158, 181, 0.12);
  font-family: Georgia, serif;
  font-size: 4.2rem;
  line-height: 1;
  pointer-events: none;
}

.comment__avatar {
  display: grid;
  place-items: center;
  flex: none;
  width: 38px;
  height: 38px;
  color: #25102b;
  font-family: var(--font-mono);
  font-size: 0.7rem;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(255, 158, 181, 0.9), rgba(185, 103, 255, 0.85));
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  box-shadow: 0 0 18px rgba(255, 158, 181, 0.15);
}

.comment__body {
  position: relative;
  z-index: 1;
  flex: 1;
  min-width: 0;
}

.comment__head {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 4px 7px;
  margin-bottom: 5px;
}

.comment__name {
  color: var(--ink);
  font-size: 0.76rem;
  font-weight: 600;
}

.comment__name--link {
  color: var(--pink);
  transition: color 0.2s var(--ease);
}

.comment__name--link:hover {
  color: var(--cyan);
}

.comment__dot,
.comment__time {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.55rem;
}

.comment__content {
  color: var(--ink-2);
  font-size: 0.8rem;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}

@media (max-width: 560px) {
  .comment {
    padding: 14px;
    gap: 9px;
  }

  .comment__avatar {
    width: 32px;
    height: 32px;
    font-size: 0.62rem;
  }
}
</style>
