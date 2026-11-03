<script setup>
import { computed, ref, watch } from 'vue'
import { resolveAssetUrl } from '@/utils/url'

const props = defineProps({
  src: { type: String, default: '' },
  alt: { type: String, default: 'Start Night 的动漫角色插画' },
})

const failed = ref(false)
const imageSrc = computed(() => resolveAssetUrl(props.src))
const showImage = computed(() => Boolean(imageSrc.value) && !failed.value)

watch(
  () => props.src,
  () => {
    failed.value = false
  },
)
</script>

<template>
  <div class="character-art" :class="{ 'character-art--image': showImage }">
    <div class="character-art__halo" aria-hidden="true" />
    <div class="character-art__orbit character-art__orbit--outer" aria-hidden="true" />
    <div class="character-art__orbit character-art__orbit--inner" aria-hidden="true" />
    <span class="character-art__star character-art__star--one" aria-hidden="true">✦</span>
    <span class="character-art__star character-art__star--two" aria-hidden="true">✦</span>
    <span class="character-art__star character-art__star--three" aria-hidden="true">✧</span>

    <img
      v-if="showImage"
      class="character-art__image"
      :src="imageSrc"
      :alt="alt"
      loading="eager"
      decoding="async"
      fetchpriority="high"
      @error="failed = true"
    />

    <div v-else class="character-art__placeholder" aria-hidden="true">
      <span class="character-art__placeholder-moon" />
      <span class="character-art__placeholder-star">✦</span>
      <span class="character-art__placeholder-copy">NIGHT SKY<br />CHARACTER ART</span>
    </div>

    <span class="character-art__vertical" aria-hidden="true">夜がはじまる</span>
    <div class="character-art__label">
      <span class="character-art__label-star" aria-hidden="true">✦</span>
      <span>
        <strong>きらめく少女</strong>
        <small>ORIGINAL CHARACTER</small>
      </span>
    </div>
    <span class="character-art__grain" aria-hidden="true" />
  </div>
</template>

<style scoped>
.character-art {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  width: 100%;
  height: 100%;
  min-height: 530px;
  overflow: hidden;
  background:
    radial-gradient(ellipse at 50% 34%, rgba(255, 158, 181, 0.17), transparent 62%),
    radial-gradient(ellipse at 58% 84%, rgba(185, 103, 255, 0.12), transparent 68%),
    linear-gradient(170deg, rgba(255, 255, 255, 0.035), transparent 55%);
  border-radius: 22px;
}

.character-art::after {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  z-index: 2;
  height: 36%;
  background: linear-gradient(
    180deg,
    transparent 0%,
    rgba(12, 8, 22, 0.14) 48%,
    rgba(11, 8, 20, 0.72) 100%
  );
  content: '';
  pointer-events: none;
}

.character-art__halo {
  position: absolute;
  top: 7%;
  left: 50%;
  width: 82%;
  aspect-ratio: 1;
  background:
    radial-gradient(circle at 50% 44%, rgba(255, 158, 181, 0.25), rgba(185, 103, 255, 0.12) 46%, transparent 70%);
  border-radius: 50%;
  filter: blur(24px);
  transform: translateX(-50%);
}

.character-art__orbit {
  position: absolute;
  top: 50%;
  left: 50%;
  border: 1px dashed rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  transform: translate(-50%, -50%);
}

.character-art__orbit--outer {
  width: 76%;
  aspect-ratio: 1;
  animation: orbit-spin 40s linear infinite;
}

.character-art__orbit--inner {
  width: 55%;
  aspect-ratio: 1;
  border-color: rgba(255, 158, 181, 0.18);
  animation: orbit-spin 28s linear infinite reverse;
}

.character-art__star {
  position: absolute;
  z-index: 3;
  color: var(--pink);
  text-shadow: 0 0 20px rgba(255, 158, 181, 0.7);
  animation: float-soft 4.5s ease-in-out infinite;
}

.character-art__star--one {
  top: 18%;
  left: 11%;
  font-size: 1.15rem;
}

.character-art__star--two {
  top: 35%;
  right: 12%;
  color: var(--cyan);
  font-size: 0.8rem;
  animation-delay: -1.6s;
}

.character-art__star--three {
  right: 22%;
  bottom: 22%;
  color: var(--pink);
  font-size: 0.9rem;
  animation-delay: -0.8s;
}

.character-art__art {
  position: relative;
  z-index: 2;
  width: min(100%, 420px);
  height: 100%;
  max-height: 620px;
  object-fit: contain;
  filter: drop-shadow(0 18px 40px rgba(0, 0, 0, 0.38));
  animation: portrait-float 6s ease-in-out infinite;
}

.character-art__image {
  position: absolute;
  inset: 0;
  z-index: 2;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 22%;
  mask-image: linear-gradient(180deg, #000 0%, #000 72%, transparent 100%);
  -webkit-mask-image: linear-gradient(180deg, #000 0%, #000 72%, transparent 100%);
  filter: drop-shadow(0 20px 46px rgba(0, 0, 0, 0.4));
  animation: portrait-float 4s ease-in-out infinite;
  will-change: transform;
}

.character-art__placeholder {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: min(100%, 420px);
  min-height: 420px;
  height: 100%;
  gap: 18px;
}

.character-art__placeholder-moon {
  display: block;
  width: 94px;
  height: 94px;
  background: linear-gradient(145deg, #fff8ef, #ffc7dc);
  border-radius: 50%;
  box-shadow:
    0 0 34px rgba(255, 214, 231, 0.4),
    0 0 80px rgba(255, 158, 181, 0.18);
}

.character-art__placeholder-star {
  color: var(--pink);
  font-size: 1.4rem;
  text-shadow: 0 0 20px rgba(255, 158, 181, 0.6);
}

.character-art__placeholder-copy {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.5rem;
  letter-spacing: 0.18em;
  line-height: 1.8;
  text-align: center;
}

.character-art__vertical {
  position: absolute;
  top: 28px;
  left: 22px;
  z-index: 3;
  color: rgba(255, 255, 255, 0.5);
  font-family: var(--font-display);
  font-size: 0.6rem;
  letter-spacing: 0.2em;
  line-height: 1.7;
  text-shadow:
    0 0 10px rgba(255, 158, 181, 0.24),
    0 0 22px rgba(0, 0, 0, 0.35);
  writing-mode: vertical-rl;
}

.character-art__label {
  position: absolute;
  right: 16px;
  bottom: 16px;
  z-index: 3;
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 6px 11px;
  color: var(--ink-2);
  background: rgba(12, 8, 22, 0.36);
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 999px;
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.06),
    0 10px 28px rgba(0, 0, 0, 0.26);
  -webkit-backdrop-filter: blur(18px) saturate(160%);
  backdrop-filter: blur(18px) saturate(160%);
}

.character-art__label-star {
  color: var(--pink);
  font-size: 0.8rem;
}

.character-art__label span:last-child {
  display: flex;
  flex-direction: column;
  gap: 1px;
}

.character-art__label strong {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 0.66rem;
  font-weight: 400;
  letter-spacing: 0.08em;
}

.character-art__label small {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.42rem;
  letter-spacing: 0.1em;
}

.character-art__grain {
  position: absolute;
  inset: 0;
  z-index: 4;
  background-image: radial-gradient(rgba(255, 255, 255, 0.22) 0.5px, transparent 0.5px);
  background-size: 4px 4px;
  opacity: 0.12;
  pointer-events: none;
}

.character-art--image .character-art__orbit {
  z-index: 1;
}

@keyframes portrait-float {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

@keyframes orbit-spin {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@media (max-width: 1050px) {
  .character-art {
    min-height: 470px;
  }
}

@media (max-width: 860px) {
  .character-art {
    min-height: 0;
  }

  .character-art__art {
    max-height: none;
  }
}
</style>
