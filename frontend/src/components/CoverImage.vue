<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { resolveAssetUrl } from '@/utils/url'

const props = withDefaults(
  defineProps<{
    src?: string | null
    alt?: string
    loading?: 'lazy' | 'eager'
    tone?: number
  }>(),
  {
    src: '',
    alt: '',
    loading: 'lazy',
    tone: 0,
  },
)

const failed = ref(false)
const imageSrc = computed(() => resolveAssetUrl(props.src))
const toneClass = computed(() => `tone-${Math.abs(props.tone) % 4}`)

watch(
  () => props.src,
  () => {
    failed.value = false
  },
)
</script>

<template>
  <div class="cover-image">
    <div class="cover-image__fallback" :class="toneClass" aria-hidden="true">
      <span class="cover-image__moon" />
      <span class="cover-image__star cover-image__star--one" />
      <span class="cover-image__star cover-image__star--two" />
      <span class="cover-image__star cover-image__star--three" />
    </div>

    <img
      v-if="src && !failed"
      class="cover-image__img"
      :src="imageSrc"
      :alt="alt"
      :loading="loading"
      decoding="async"
      @error="failed = true"
    />
  </div>
</template>

<style scoped>
.cover-image {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: var(--night-1);
}

.cover-image::after {
  position: absolute;
  inset: 12px;
  z-index: 1;
  content: '';
  pointer-events: none;
  border: 1px solid rgba(255, 255, 255, 0.06);
  border-radius: 2px;
}

.cover-image__fallback {
  position: absolute;
  inset: 0;
}

.tone-0 {
  background:
    radial-gradient(320px 220px at 76% 18%, rgba(244, 213, 141, 0.13), transparent 68%),
    linear-gradient(135deg, #0b1020 0%, #1c2442 56%, #3b3766 100%);
}

.tone-1 {
  background:
    radial-gradient(300px 220px at 22% 22%, rgba(100, 84, 200, 0.24), transparent 68%),
    linear-gradient(135deg, #111827 0%, #273349 56%, #464f6d 100%);
}

.tone-2 {
  background:
    radial-gradient(320px 240px at 82% 72%, rgba(196, 121, 142, 0.18), transparent 70%),
    linear-gradient(135deg, #14101f 0%, #2a223a 56%, #4b3a54 100%);
}

.tone-3 {
  background:
    radial-gradient(300px 220px at 20% 18%, rgba(126, 159, 167, 0.2), transparent 68%),
    linear-gradient(135deg, #0d1620 0%, #172936 56%, #354c55 100%);
}

.cover-image__moon {
  position: absolute;
  top: 24%;
  right: 20%;
  width: 64px;
  height: 64px;
  background: radial-gradient(circle at 34% 32%, #fff8e6 0%, #f2dcae 58%, #d5b679 100%);
  border-radius: 50%;
  box-shadow: 0 0 42px rgba(244, 213, 141, 0.24);
  opacity: 0.82;
}

.cover-image__star {
  position: absolute;
  width: 3px;
  height: 3px;
  background: rgba(255, 255, 255, 0.65);
  border-radius: 50%;
  box-shadow: 0 0 8px rgba(255, 255, 255, 0.5);
}

.cover-image__star--one {
  top: 24%;
  left: 22%;
}

.cover-image__star--two {
  top: 64%;
  left: 72%;
  width: 2px;
  height: 2px;
}

.cover-image__star--three {
  top: 76%;
  left: 30%;
  width: 2px;
  height: 2px;
  opacity: 0.7;
}

.cover-image__img {
  position: absolute;
  inset: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s var(--ease);
}
</style>
