<script setup>
import { computed, ref, watch } from 'vue'
import { resolveAssetUrl } from '@/utils/url'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: '' },
  loading: { type: String, default: 'lazy' },
  tone: { type: Number, default: 0 },
})

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
  <div class="cover-image" :class="toneClass">
    <div class="cover-image__fallback" aria-hidden="true">
      <span class="cover-image__halo" />
      <span class="cover-image__moon" />
      <span class="cover-image__star cover-image__star--one" />
      <span class="cover-image__star cover-image__star--two" />
      <span class="cover-image__star cover-image__star--three" />
      <span class="cover-image__grid" />
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
  min-height: 100%;
  overflow: hidden;
  background: #161025;
  border-radius: inherit;
}

.cover-image__fallback {
  position: absolute;
  inset: 0;
  overflow: hidden;
  background:
    radial-gradient(circle at 72% 22%, rgba(255, 158, 181, 0.18), transparent 44%),
    radial-gradient(circle at 22% 86%, rgba(126, 200, 227, 0.1), transparent 48%),
    linear-gradient(145deg, #1b1230, #27173f 52%, #211532);
}

.cover-image__halo {
  position: absolute;
  top: 20%;
  left: 60%;
  width: 68%;
  aspect-ratio: 1;
  background: radial-gradient(circle, rgba(255, 158, 181, 0.18), transparent 68%);
  border-radius: 50%;
  filter: blur(14px);
  transform: translateX(-50%);
}

.cover-image__moon {
  position: absolute;
  top: 24%;
  left: 58%;
  width: 30%;
  aspect-ratio: 1;
  background: linear-gradient(145deg, #fff4ed, #ffc6dc);
  border-radius: 50%;
  box-shadow:
    0 0 22px rgba(255, 214, 231, 0.55),
    inset -8px -5px 0 rgba(185, 103, 255, 0.12);
  transform: translateX(-50%);
}

.cover-image__star {
  position: absolute;
  width: 4px;
  height: 4px;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 9px rgba(255, 255, 255, 0.8);
}

.cover-image__star--one {
  top: 18%;
  left: 24%;
}

.cover-image__star--two {
  top: 64%;
  left: 78%;
  width: 3px;
  height: 3px;
  background: var(--cyan);
}

.cover-image__star--three {
  top: 76%;
  left: 22%;
  width: 2px;
  height: 2px;
  background: var(--pink);
}

.cover-image__grid {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(255, 255, 255, 0.2) 0.5px, transparent 0.5px);
  background-size: 5px 5px;
  opacity: 0.08;
}

.cover-image__img {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center;
  transition: transform 0.5s var(--ease);
}

.cover-image:hover .cover-image__img {
  transform: scale(1.04);
}

.tone-1 .cover-image__fallback {
  background:
    radial-gradient(circle at 74% 20%, rgba(126, 200, 227, 0.18), transparent 44%),
    radial-gradient(circle at 20% 84%, rgba(255, 158, 181, 0.12), transparent 48%),
    linear-gradient(145deg, #111c31, #1b2947 52%, #2b1b45);
}

.tone-1 .cover-image__moon {
  left: 28%;
  background: linear-gradient(145deg, #e6fbff, #9edcf0);
  box-shadow:
    0 0 22px rgba(126, 200, 227, 0.45),
    inset -8px -5px 0 rgba(255, 158, 181, 0.12);
}

.tone-1 .cover-image__star--one {
  left: 70%;
}

.tone-2 .cover-image__fallback {
  background:
    radial-gradient(circle at 70% 24%, rgba(185, 103, 255, 0.22), transparent 46%),
    linear-gradient(145deg, #1d102d, #2a1648 54%, #151a35);
}

.tone-2 .cover-image__moon {
  left: 44%;
  width: 34%;
  background: linear-gradient(145deg, #f7eaff, #d0a8ff);
  box-shadow:
    0 0 24px rgba(185, 103, 255, 0.48),
    inset -8px -5px 0 rgba(126, 200, 227, 0.12);
}

.tone-3 .cover-image__fallback {
  background:
    radial-gradient(circle at 22% 22%, rgba(255, 158, 181, 0.16), transparent 44%),
    radial-gradient(circle at 78% 76%, rgba(126, 200, 227, 0.12), transparent 48%),
    linear-gradient(145deg, #23152c, #301d45 52%, #171b34);
}

.tone-3 .cover-image__moon {
  top: 32%;
  left: 34%;
  width: 26%;
  background: linear-gradient(145deg, #fff3fa, #ffb8d0);
}

.tone-3 .cover-image__star--two {
  left: 30%;
}
</style>
