<script setup>
import { computed } from 'vue'
import CoverImage from '@/components/CoverImage.vue'
import { formatDate, formatViewCount, toDateTimeAttr } from '@/utils/date'

const props = defineProps({
  post: { type: Object, required: true },
  wide: { type: Boolean, default: false },
})

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
const enterDelay = computed(() => `${(Math.abs(Number(props.post.id) || 0) % 5) * 55}ms`)
</script>

<template>
  <article
    class="post-card glass-card"
    :class="{ 'post-card--wide': wide }"
    :style="{ animationDelay: enterDelay }"
    :aria-labelledby="titleId"
  >
    <div class="post-card__thumb">
      <CoverImage :src="post.coverUrl" :tone="tone" />
      <span class="post-card__thumb-ring" aria-hidden="true" />
      <span class="post-card__thumb-star" aria-hidden="true">✦</span>
    </div>

    <div class="post-card__body">
      <div class="post-card__meta">
        <span class="post-card__tag">NOTE</span>
        <time v-if="dateTime" :datetime="dateTime">{{ published }}</time>
        <span v-if="dateTime && views" class="post-card__dot" aria-hidden="true">·</span>
        <span v-if="views">{{ views }} 阅读</span>
      </div>

      <h3 :id="titleId" class="post-card__title">
        <RouterLink :to="postRoute">{{ post.title }}</RouterLink>
      </h3>

      <p class="post-card__summary">{{ summary }}</p>

      <div class="post-card__foot">
        <span class="post-card__more" aria-hidden="true">
          阅读笔记
          <span>→</span>
        </span>
        <span class="post-card__heart" aria-hidden="true">♡</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.post-card {
  position: relative;
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  gap: 16px;
  align-items: center;
  min-width: 0;
  padding: 16px;
  overflow: hidden;
  border-radius: 18px;
  animation: rise-in 0.55s var(--ease) both;
}

.post-card--wide {
  grid-template-columns: 124px minmax(0, 1fr);
  padding: 18px;
}

.post-card__thumb {
  position: relative;
  width: 92px;
  height: 92px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 50%;
  box-shadow:
    0 0 0 5px rgba(255, 255, 255, 0.025),
    0 0 24px rgba(185, 103, 255, 0.14);
  transition:
    transform 0.35s var(--ease),
    box-shadow 0.35s var(--ease);
}

.post-card--wide .post-card__thumb {
  width: 124px;
  height: 124px;
}

.post-card:hover .post-card__thumb {
  box-shadow:
    0 0 0 5px rgba(255, 158, 181, 0.08),
    0 0 32px rgba(255, 158, 181, 0.22);
  transform: rotate(-2deg) scale(1.03);
}

.post-card__thumb-ring {
  position: absolute;
  inset: 5px;
  z-index: 2;
  border: 1px dashed rgba(255, 255, 255, 0.28);
  border-radius: 50%;
  pointer-events: none;
}

.post-card__thumb-star {
  position: absolute;
  right: 1px;
  bottom: 3px;
  z-index: 3;
  display: grid;
  place-items: center;
  width: 22px;
  height: 22px;
  color: #2a0f2b;
  font-size: 0.62rem;
  background: var(--pink);
  border-radius: 50%;
  box-shadow: 0 0 14px rgba(255, 158, 181, 0.4);
}

.post-card__body {
  display: flex;
  flex-direction: column;
  min-width: 0;
  gap: 7px;
}

.post-card__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.55rem;
  letter-spacing: 0.03em;
}

.post-card__tag {
  padding: 2px 7px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.48rem;
  letter-spacing: 0.1em;
  background: rgba(185, 103, 255, 0.14);
  border: 1px solid rgba(185, 103, 255, 0.2);
  border-radius: 999px;
}

.post-card__dot {
  color: rgba(255, 255, 255, 0.22);
}

.post-card__title {
  display: -webkit-box;
  overflow: hidden;
  color: var(--ink);
  font-size: 1.02rem;
  font-weight: 700;
  letter-spacing: -0.01em;
  line-height: 1.55;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.post-card--wide .post-card__title {
  font-size: 1.18rem;
}

.post-card__title a {
  text-decoration: none;
  transition:
    color 0.2s var(--ease),
    text-shadow 0.2s var(--ease);
}

.post-card__title a::after {
  position: absolute;
  inset: 0;
  z-index: 4;
  content: '';
  border-radius: inherit;
}

.post-card__title a:hover {
  color: var(--pink);
  text-shadow: 0 0 20px rgba(255, 158, 181, 0.25);
}

.post-card__summary {
  display: -webkit-box;
  overflow: hidden;
  color: var(--muted);
  font-size: 0.75rem;
  line-height: 1.7;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.post-card__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin-top: 2px;
}

.post-card__more {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.58rem;
  letter-spacing: 0.03em;
}

.post-card__more span {
  transition: transform 0.2s var(--ease);
}

.post-card:hover .post-card__more span {
  transform: translateX(3px);
}

.post-card__heart {
  color: rgba(255, 158, 181, 0.65);
  font-size: 0.8rem;
}

@media (max-width: 560px) {
  .post-card {
    grid-template-columns: 76px minmax(0, 1fr);
    gap: 12px;
    padding: 13px;
  }

  .post-card__thumb {
    width: 76px;
    height: 76px;
  }

  .post-card--wide {
    grid-template-columns: 90px minmax(0, 1fr);
  }

  .post-card--wide .post-card__thumb {
    width: 90px;
    height: 90px;
  }

  .post-card__title {
    font-size: 0.94rem;
  }

  .post-card__summary {
    font-size: 0.7rem;
  }

  .post-card__tag {
    display: none;
  }
}
</style>
