<script setup lang="ts">
import { computed } from 'vue'
import CoverImage from '@/components/CoverImage.vue'
import type { PostSummary } from '@/api/types'
import { formatDate, formatViewCount, toDateTimeAttr } from '@/utils/date'

const props = defineProps<{
  post: PostSummary
}>()

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
const titleId = computed(() => `featured-title-${props.post.id}`)
</script>

<template>
  <article
    class="featured"
    :aria-labelledby="titleId"
  >
    <div class="featured__media">
      <div class="featured__cover">
        <CoverImage :src="post.coverUrl" :tone="tone" loading="eager" />
      </div>
      <span class="featured__badge">
        <span aria-hidden="true">✦</span>
        LATEST NOTE
      </span>
    </div>

    <div class="featured__body">
      <p class="featured__meta">
        <time v-if="dateTime" :datetime="dateTime">{{ published }}</time>
        <span v-if="dateTime && views" class="featured__dot" aria-hidden="true">·</span>
        <span v-if="views">{{ views }} 次阅读</span>
        <span v-if="!dateTime && !views">START NIGHT</span>
      </p>

      <h3 :id="titleId" class="featured__title">
        <RouterLink :to="postRoute">{{ post.title }}</RouterLink>
      </h3>

      <p class="featured__summary">{{ summary }}</p>

      <span class="featured__more" aria-hidden="true">
        阅读全文
        <svg viewBox="0 0 24 24">
          <path
            d="M5 12h13M13 6l6 6-6 6"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
      </span>
    </div>
  </article>
</template>

<style scoped>
.featured {
  position: relative;
  display: grid;
  grid-template-columns: 1.04fr 0.96fr;
  overflow: hidden;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: 24px;
  box-shadow: var(--shadow-sm);
  transition:
    transform 0.3s var(--ease),
    box-shadow 0.3s var(--ease),
    border-color 0.3s var(--ease);
}

.featured:hover {
  border-color: var(--line-strong);
  box-shadow: var(--shadow-md);
  transform: translateY(-3px);
}

.featured__media {
  position: relative;
  min-height: 350px;
}

.featured__cover {
  position: absolute;
  inset: 0;
}

.featured__badge {
  position: absolute;
  top: 18px;
  left: 18px;
  z-index: 2;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 10px;
  color: var(--moon);
  font-family: var(--font-mono);
  font-size: 0.54rem;
  letter-spacing: 0.13em;
  background: rgba(7, 10, 20, 0.55);
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 999px;
  -webkit-backdrop-filter: blur(8px);
  backdrop-filter: blur(8px);
}

.featured__badge span {
  color: var(--star);
}

.featured__body {
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 14px;
  padding: 34px 34px 30px;
}

.featured__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.03em;
}

.featured__dot {
  color: var(--line-strong);
}

.featured__title {
  color: var(--ink);
  font-size: clamp(1.45rem, 2.5vw, 1.8rem);
  font-weight: 800;
  letter-spacing: -0.025em;
  line-height: 1.42;
  text-wrap: balance;
}

.featured__title a {
  text-decoration: none;
  transition: color 0.2s var(--ease);
}

.featured__title a::after {
  position: absolute;
  inset: 0;
  z-index: 3;
  content: '';
  border-radius: inherit;
}

.featured__title a:hover {
  color: var(--violet);
}

.featured__summary {
  display: -webkit-box;
  overflow: hidden;
  color: var(--muted);
  font-size: 0.88rem;
  line-height: 1.85;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
}

.featured__more {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-top: 2px;
  color: var(--violet);
  font-size: 0.78rem;
  font-weight: 600;
}

.featured__more svg {
  width: 14px;
  height: 14px;
  transition: transform 0.25s var(--ease);
}

.featured:hover .featured__more svg {
  transform: translateX(3px);
}

@media (max-width: 860px) {
  .featured {
    grid-template-columns: minmax(0, 1fr);
  }

  .featured__media {
    min-height: 0;
    aspect-ratio: 16 / 10;
  }

  .featured__body {
    padding: 24px 22px 26px;
  }
}

@media (max-width: 560px) {
  .featured {
    border-radius: 20px;
  }

  .featured__title {
    font-size: 1.35rem;
  }

  .featured__summary {
    -webkit-line-clamp: 4;
  }
}
</style>
