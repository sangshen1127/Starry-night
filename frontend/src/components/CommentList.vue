<script setup lang="ts">
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import Pagination from '@/components/Pagination.vue'
import type { PublicComment } from '@/api/types'
import { formatCommentDate, toDateTimeAttr } from '@/utils/date'
import { getInitial, safeHttpUrl } from '@/utils/url'

withDefaults(
  defineProps<{
    comments: PublicComment[]
    loading?: boolean
    error?: string
    total?: number
    page?: number
    pageSize?: number
  }>(),
  {
    loading: false,
    error: '',
    total: 0,
    page: 1,
    pageSize: 10,
  },
)

const emit = defineEmits<{
  retry: []
  'page-change': [page: number]
}>()

function websiteOf(comment: PublicComment): string {
  return safeHttpUrl(comment.website)
}
</script>

<template>
  <div class="comment-list">
    <div v-if="loading" class="comments-loading" aria-busy="true" aria-live="polite">
      <span class="sr-only">正在加载留言…</span>
      <span class="skeleton comments-loading__line" />
      <span class="skeleton comments-loading__line comments-loading__line--wide" />
      <span class="skeleton comments-loading__line" />
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
        <li v-for="comment in comments" :key="comment.id" class="comment">
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
  gap: 12px;
}

.comments-loading__line {
  height: 46px;
  border-radius: 10px;
}

.comments-loading__line--wide {
  width: 76%;
  height: 56px;
}

.comments {
  display: flex;
  flex-direction: column;
}

.comment {
  display: flex;
  gap: 13px;
  padding: 18px 0;
  border-top: 1px solid var(--line);
}

.comment:first-child {
  padding-top: 0;
  border-top: 0;
}

.comment__avatar {
  display: grid;
  place-items: center;
  flex: none;
  width: 36px;
  height: 36px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.72rem;
  font-weight: 700;
  background: var(--violet-soft);
  border: 1px solid var(--line);
  border-radius: 50%;
}

.comment__body {
  flex: 1;
  min-width: 0;
}

.comment__head {
  display: flex;
  flex-wrap: wrap;
  align-items: baseline;
  gap: 6px 10px;
  margin-bottom: 4px;
}

.comment__name {
  color: var(--ink);
  font-size: 0.82rem;
  font-weight: 600;
}

.comment__name--link {
  transition: color 0.2s var(--ease);
}

.comment__name--link:hover {
  color: var(--violet);
}

.comment__time {
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.6rem;
}

.comment__content {
  color: var(--ink-2);
  font-size: 0.86rem;
  line-height: 1.8;
  white-space: pre-wrap;
  overflow-wrap: anywhere;
}
</style>
