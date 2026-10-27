<script setup lang="ts">
import { computed } from 'vue'
import CoverImage from '@/components/CoverImage.vue'
import type { PostSummary } from '@/api/types'
import { formatDate, formatViewCount, toDateTimeAttr } from '@/utils/date'

const props = withDefaults(
  defineProps<{
    post: PostSummary
    wide?: boolean
  }>(),
  {
    wide: false,
  },
)

const summary = computed(
  () => props.post.summary?.trim() || '这篇文章还没有写摘要，点进去看看正文吧。',
)
const published = computed(() => formatDate(props.post.publishedAt))
const dateTime = computed(() => toDateTimeAttr(props.post.publishedAt))
const views = computed(() => formatViewCount(props.post.viewCount))
const tone = computed(() => Math.abs(Number(props.post.id) || 0) % 4)
const postRoute = computed(() => ({
  name: 'post',
  params: { id: props.post.id },
}))
const titleId = computed(() => `post-title-${props.post.id}`)
</script>

<template>
  <article
    class="post-card"
    :class="{ 'post-card--wide': wide }"
    :aria-labelledby="titleId"
  >
    <div class="post-card__media">
      <div class="post-card__cover">
        <CoverImage :src="post.coverUrl" :tone="tone" />
      </div>
    </div>

    <div class="post-card__body">
      <p class="post-card__meta">
        <time v-if="dateTime" :datetime="dateTime">{{ published }}</time>
        <span v-if="dateTime && views" aria-hidden="true">·</span>
        <span v-if="views">{{ views }} 次阅读</span>
      </p>

      <h3 :id="titleId" class="post-card__title">
        <RouterLink :to="postRoute">{{ post.title }}</RouterLink>
      </h3>

      <p class="post-card__summary">{{ summary }}</p>

      <span class="post-card__more" aria-hidden="true">
        阅读全文
        <span>→</span>
      </span>
    </div>
  </article>
</template>

<style scoped>
.post-card {
  position: relative;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  transition:
    transform 0.3s var(--ease),
    box-shadow 0.3s var(--ease),
    border-color 0.3s var(--ease);
}

.post-card:hover {
  border-color: var(--line-strong);
  box-shadow: var(--shadow-md);
  transform: translateY(-4px);
}

.post-card__media {
  position: relative;
  aspect-ratio: 16 / 10;
}

.post-card__cover {
  position: absolute;
  inset: 0;
}

.post-card__body {
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 9px;
  padding: 20px 20px 22px;
}

.post-card--wide {
  flex-direction: row;
}

.post-card--wide .post-card__media {
  flex: 0 0 42%;
  aspect-ratio: 4 / 3;
  align-self: stretch;
}

.post-card--wide .post-card__body {
  justify-content: center;
  gap: 11px;
  padding: 28px 30px;
}

.post-card--wide .post-card__title {
  min-height: 0;
  font-size: 1.18rem;
  -webkit-line-clamp: 3;
}

.post-card--wide .post-card__summary {
  min-height: 0;
  font-size: 0.84rem;
  -webkit-line-clamp: 3;
}

.post-card__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.02em;
}

.post-card__title {
  display: -webkit-box;
  min-height: 2.9em;
  overflow: hidden;
  color: var(--ink);
  font-size: 1.08rem;
  font-weight: 700;
  letter-spacing: -0.015em;
  line-height: 1.48;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-card__title a {
  text-decoration: none;
  transition: color 0.2s var(--ease);
}

.post-card__title a::after {
  position: absolute;
  inset: 0;
  z-index: 2;
  content: '';
  border-radius: inherit;
}

.post-card__title a:hover {
  color: var(--violet);
}

.post-card__summary {
  display: -webkit-box;
  min-height: 2.9em;
  overflow: hidden;
  color: var(--muted);
  font-size: 0.82rem;
  line-height: 1.8;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.post-card__more {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  margin-top: auto;
  padding-top: 8px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  letter-spacing: 0.03em;
}

.post-card__more span {
  transition: transform 0.2s var(--ease);
}

.post-card:hover .post-card__more span {
  transform: translateX(3px);
}

.post-card--wide .post-card__more {
  margin-top: 2px;
  padding-top: 2px;
}

@media (max-width: 720px) {
  .post-card--wide {
    flex-direction: column;
  }

  .post-card--wide .post-card__media {
    flex: none;
    aspect-ratio: 16 / 10;
  }

  .post-card--wide .post-card__body {
    padding: 20px 20px 22px;
  }

  .post-card--wide .post-card__title {
    min-height: 2.9em;
    font-size: 1.08rem;
    -webkit-line-clamp: 2;
  }

  .post-card--wide .post-card__summary {
    min-height: 2.9em;
    -webkit-line-clamp: 2;
  }
}

@media (max-width: 560px) {
  .post-card__body {
    padding: 18px 18px 20px;
  }

  .post-card__title {
    font-size: 1.02rem;
  }
}
</style>
