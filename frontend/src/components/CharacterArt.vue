<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { resolveAssetUrl } from '@/utils/url'

const props = withDefaults(
  defineProps<{
    src?: string
    alt?: string
  }>(),
  {
    src: '',
    alt: 'Start Night 的动漫角色插画',
  },
)

const failed = ref(false)
const imageSrc = computed(() => resolveAssetUrl(props.src))
const hasImage = computed(() => Boolean(props.src) && !failed.value)

watch(
  () => props.src,
  () => {
    failed.value = false
  },
)
</script>

<template>
  <div class="character" :class="{ 'character--image': hasImage }">
    <img
      v-if="hasImage"
      class="character__image"
      :src="imageSrc"
      :alt="alt"
      loading="eager"
      decoding="async"
      fetchpriority="high"
      @error="failed = true"
    />

    <div
      v-else
      class="character__empty"
      role="img"
      aria-label="角色插画预留位：夜色、月亮与星星"
    >
      <span class="empty__star empty__star--one" aria-hidden="true">✦</span>
      <span class="empty__star empty__star--two" aria-hidden="true">✧</span>

      <svg
        class="empty__constellation"
        viewBox="0 0 600 600"
        fill="none"
        aria-hidden="true"
      >
        <path
          d="M78 158 174 106 258 182 362 128 472 196"
          stroke="currentColor"
          stroke-width="1"
        />
        <path
          d="M116 454 196 406 286 462 382 408 486 452"
          stroke="currentColor"
          stroke-width="1"
          opacity="0.55"
        />
        <circle cx="174" cy="106" r="3" fill="currentColor" />
        <circle cx="362" cy="128" r="2.2" fill="currentColor" />
        <circle cx="286" cy="462" r="2.4" fill="currentColor" />
        <circle cx="486" cy="452" r="2" fill="currentColor" />
      </svg>

      <div class="empty__moon-wrap">
        <span class="empty__orbit" aria-hidden="true" />
        <svg class="empty__moon" viewBox="0 0 120 120" aria-hidden="true">
          <path
            d="M80 12a48 48 0 1 0 28 78 40 40 0 0 1-28-78Z"
            fill="currentColor"
          />
        </svg>
      </div>

      <p class="empty__vertical" aria-hidden="true">夜がはじまる</p>

      <p class="empty__label">
        <span>CHARACTER ART</span>
        <small>角色插画位</small>
      </p>
    </div>

    <span v-if="hasImage" class="character__credit" aria-hidden="true">
      ILLUSTRATION / START NIGHT
    </span>
  </div>
</template>

<style scoped>
.character {
  position: relative;
  width: 100%;
  height: 100%;
  min-height: 430px;
  overflow: hidden;
  background: linear-gradient(150deg, #090e1c 0%, #151b33 54%, #282448 100%);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 24px;
}

.character::after {
  position: absolute;
  inset: 0;
  z-index: 2;
  content: '';
  pointer-events: none;
  border-radius: inherit;
  box-shadow: inset 0 0 90px rgba(4, 7, 16, 0.58);
}

.character__empty {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background:
    radial-gradient(430px 340px at 50% 42%, rgba(100, 84, 200, 0.26), transparent 72%),
    radial-gradient(300px 240px at 74% 16%, rgba(244, 213, 141, 0.08), transparent 70%);
}

.character__empty::before {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(1.4px 1.4px at 16% 24%, rgba(255, 255, 255, 0.55), transparent 70%),
    radial-gradient(1px 1px at 76% 18%, rgba(255, 255, 255, 0.5), transparent 70%),
    radial-gradient(1.2px 1.2px at 86% 62%, rgba(244, 213, 141, 0.4), transparent 70%),
    radial-gradient(1px 1px at 28% 74%, rgba(255, 255, 255, 0.36), transparent 70%),
    radial-gradient(1px 1px at 58% 84%, rgba(255, 255, 255, 0.32), transparent 70%);
  content: '';
}

.empty__star {
  position: absolute;
  color: var(--star);
  font-size: 12px;
  opacity: 0.65;
}

.empty__star--one {
  top: 22%;
  right: 23%;
}

.empty__star--two {
  bottom: 28%;
  left: 21%;
  font-size: 9px;
  opacity: 0.45;
}

.empty__constellation {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  color: rgba(255, 255, 255, 0.15);
}

.empty__moon-wrap {
  position: relative;
  display: grid;
  place-items: center;
  width: 172px;
  height: 172px;
}

.empty__orbit {
  position: absolute;
  inset: 0;
  border: 1px solid rgba(244, 213, 141, 0.18);
  border-radius: 50%;
}

.empty__orbit::after {
  position: absolute;
  top: 13%;
  right: 6%;
  width: 4px;
  height: 4px;
  background: var(--star);
  border-radius: 50%;
  box-shadow: 0 0 12px rgba(244, 213, 141, 0.8);
  content: '';
}

.empty__moon {
  width: 116px;
  height: 116px;
  color: var(--moon);
  filter: drop-shadow(0 0 28px rgba(244, 213, 141, 0.22));
}

.empty__vertical {
  position: absolute;
  top: 34px;
  right: 30px;
  color: rgba(244, 240, 230, 0.48);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  letter-spacing: 0.34em;
  writing-mode: vertical-rl;
}

.empty__label {
  position: absolute;
  bottom: 24px;
  left: 26px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: var(--night-muted);
  font-family: var(--font-mono);
  font-size: 0.57rem;
  letter-spacing: 0.2em;
}

.empty__label small {
  color: rgba(244, 240, 230, 0.42);
  font-family: var(--font-sans);
  font-size: 0.64rem;
  letter-spacing: 0.14em;
}

.character__image {
  position: absolute;
  inset: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 22%;
}

.character__credit {
  position: absolute;
  right: 18px;
  bottom: 16px;
  z-index: 3;
  color: rgba(244, 240, 230, 0.45);
  font-family: var(--font-mono);
  font-size: 0.54rem;
  letter-spacing: 0.14em;
}

@media (max-width: 960px) {
  .character {
    min-height: 340px;
  }
}

@media (max-width: 560px) {
  .character {
    min-height: 290px;
    border-radius: 20px;
  }

  .empty__moon-wrap {
    width: 140px;
    height: 140px;
  }

  .empty__moon {
    width: 96px;
    height: 96px;
  }

  .empty__vertical {
    top: 22px;
    right: 20px;
    font-size: 0.58rem;
  }

  .empty__label {
    bottom: 18px;
    left: 18px;
  }
}
</style>
