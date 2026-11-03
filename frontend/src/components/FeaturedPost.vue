<script setup>
import { computed } from 'vue'
import CoverImage from '@/components/CoverImage.vue'
import { formatDate, formatViewCount, toDateTimeAttr } from '@/utils/date'

const props = defineProps({
  post: { type: Object, required: true },
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
const titleId = computed(() => `featured-title-${props.post.id}`)
</script>

<template>
  <article class="featured glass-card" :aria-labelledby="titleId">
    <div class="featured__media">
      <CoverImage :src="post.coverUrl" :tone="tone" loading="eager" />
      <div class="featured__media-shade" aria-hidden="true" />
      <span class="featured__badge">
        <span aria-hidden="true">✦</span>
        FEATURED NOTE
      </span>
      <span class="featured__vertical" aria-hidden="true">いちばん星</span>
      <span class="featured__sticker" aria-hidden="true">♡</span>
    </div>

    <div class="featured__body">
      <div class="featured__meta">
        <span class="featured__tag">LATEST</span>
        <time v-if="dateTime" :datetime="dateTime">{{ published }}</time>
        <span v-if="dateTime && views" class="featured__dot" aria-hidden="true">·</span>
        <span v-if="views">{{ views }} 次阅读</span>
        <span v-if="!dateTime && !views">START NIGHT</span>
      </div>

      <h3 :id="titleId" class="featured__title">
        <RouterLink :to="postRoute">{{ post.title }}</RouterLink>
      </h3>

      <p class="featured__summary">{{ summary }}</p>

      <div class="featured__foot">
        <span class="featured__more" aria-hidden="true">
          进入这篇夜色笔记
          <span>→</span>
        </span>
        <span class="featured__deco" aria-hidden="true">✧ 夜の記録 ✧</span>
      </div>
    </div>
  </article>
</template>

<style scoped>
.featured {
  position: relative;
  display: grid;
  grid-template-columns: minmax(0, 1.06fr) minmax(0, 0.94fr);
  min-height: 342px;
  overflow: hidden;
  margin-bottom: 16px;
  border-radius: 24px;
  animation: rise-in 0.6s var(--ease) both;
}

.featured__media {
  position: relative;
  min-height: 300px;
  overflow: hidden;
}

.featured__media :deep(.cover-image) {
  position: absolute;
  inset: 0;
}

.featured__media-shade {
  position: absolute;
  inset: 0;
  background:
    linear-gradient(90deg, transparent 40%, rgba(13, 9, 24, 0.62) 100%),
    linear-gradient(0deg, rgba(8, 5, 15, 0.4), transparent 52%);
  pointer-events: none;
}

.featured__badge {
  position: absolute;
  top: 18px;
  left: 18px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 5px 9px;
  color: #2b102c;
  font-family: var(--font-mono);
  font-size: 0.46rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  background: var(--gradient);
  border-radius: 999px;
  box-shadow: 0 6px 22px rgba(185, 103, 255, 0.24);
}

.featured__vertical {
  position: absolute;
  top: 24px;
  right: 18px;
  color: rgba(255, 255, 255, 0.38);
  font-family: var(--font-display);
  font-size: 0.58rem;
  letter-spacing: 0.16em;
  writing-mode: vertical-rl;
}

.featured__sticker {
  position: absolute;
  bottom: 18px;
  left: 18px;
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  color: var(--pink);
  font-size: 0.95rem;
  background: rgba(18, 10, 28, 0.5);
  border: 1px solid rgba(255, 158, 181, 0.22);
  border-radius: 50%;
  box-shadow: 0 0 20px rgba(255, 158, 181, 0.14);
  -webkit-backdrop-filter: blur(10px);
  backdrop-filter: blur(10px);
  animation: float-soft 4s ease-in-out infinite;
}

.featured__body {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 32px 34px;
  background:
    radial-gradient(280px 240px at 90% 0%, rgba(255, 158, 181, 0.09), transparent 65%),
    rgba(255, 255, 255, 0.018);
}

.featured__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 7px;
  margin-bottom: 14px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.56rem;
}

.featured__tag {
  padding: 2px 8px;
  color: var(--cyan);
  font-size: 0.48rem;
  letter-spacing: 0.1em;
  background: rgba(126, 200, 227, 0.12);
  border: 1px solid rgba(126, 200, 227, 0.2);
  border-radius: 999px;
}

.featured__dot {
  color: rgba(255, 255, 255, 0.22);
}

.featured__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(1.55rem, 2.4vw, 2.05rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.42;
  text-shadow: 0 0 22px rgba(255, 158, 181, 0.13);
}

.featured__title a {
  transition:
    color 0.2s var(--ease),
    text-shadow 0.2s var(--ease);
}

.featured__title a::after {
  position: absolute;
  inset: 0;
  z-index: 3;
  content: '';
  border-radius: inherit;
}

.featured__title a:hover {
  color: var(--pink);
  text-shadow: 0 0 24px rgba(255, 158, 181, 0.26);
}

.featured__summary {
  display: -webkit-box;
  overflow: hidden;
  margin-top: 13px;
  color: var(--muted);
  font-size: 0.82rem;
  line-height: 1.85;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 3;
}

.featured__foot {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-top: 22px;
}

.featured__more {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.6rem;
}

.featured__more span {
  transition: transform 0.2s var(--ease);
}

.featured:hover .featured__more span {
  transform: translateX(4px);
}

.featured__deco {
  color: var(--faint);
  font-family: var(--font-display);
  font-size: 0.56rem;
  letter-spacing: 0.08em;
}

@media (max-width: 720px) {
  .featured {
    grid-template-columns: minmax(0, 1fr);
    min-height: 0;
  }

  .featured__media {
    min-height: 210px;
    aspect-ratio: 16 / 10;
  }

  .featured__media-shade {
    background: linear-gradient(0deg, rgba(13, 9, 24, 0.5), transparent 62%);
  }

  .featured__body {
    padding: 22px;
  }

  .featured__vertical {
    display: none;
  }

  .featured__summary {
    -webkit-line-clamp: 2;
  }

  .featured__deco {
    display: none;
  }
}
</style>
