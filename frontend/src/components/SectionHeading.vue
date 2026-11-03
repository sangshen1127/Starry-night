<script setup>
import { useSlots } from 'vue'

defineProps({
  kicker: { type: String, required: true },
  title: { type: String, required: true },
  meta: { type: String, default: '' },
  headingId: { type: String, default: '' },
})

const slots = useSlots()
</script>

<template>
  <div class="section-heading">
    <div class="section-heading__copy">
      <p class="section-heading__kicker">
        <span class="section-heading__star" aria-hidden="true">✦</span>
        <span>{{ kicker }}</span>
        <span class="section-heading__jp" aria-hidden="true">ひらめき</span>
      </p>
      <h2 :id="headingId || undefined" class="section-heading__title">{{ title }}</h2>
    </div>

    <div v-if="meta || slots.action" class="section-heading__aside">
      <p v-if="meta" class="section-heading__meta">{{ meta }}</p>
      <slot name="action" />
    </div>
  </div>
</template>

<style scoped>
.section-heading {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 24px;
}

.section-heading__kicker {
  display: flex;
  align-items: center;
  gap: 7px;
  width: fit-content;
  margin-bottom: 9px;
  padding: 4px 9px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.55rem;
  font-weight: 600;
  letter-spacing: 0.14em;
  background: rgba(255, 158, 181, 0.08);
  border: 1px solid rgba(255, 158, 181, 0.15);
  border-radius: 999px;
}

.section-heading__star {
  color: var(--pink);
  text-shadow: 0 0 14px rgba(255, 158, 181, 0.65);
  animation: twinkle 3.4s ease-in-out infinite;
}

.section-heading__jp {
  padding-left: 7px;
  color: rgba(255, 255, 255, 0.35);
  border-left: 1px solid rgba(255, 255, 255, 0.12);
  font-family: var(--font-display);
  font-size: 0.55rem;
  letter-spacing: 0.08em;
}

.section-heading__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(1.8rem, 3.3vw, 2.35rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.3;
  text-shadow: 0 0 28px rgba(185, 103, 255, 0.16);
}

.section-heading__aside {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 5px;
  padding-bottom: 3px;
}

.section-heading__meta {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.54rem;
  letter-spacing: 0.08em;
  white-space: nowrap;
}

.section-heading :deep(.section-link) {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  transition:
    color 0.2s var(--ease),
    text-shadow 0.2s var(--ease);
}

.section-heading :deep(.section-link svg) {
  width: 12px;
  height: 12px;
  transition: transform 0.2s var(--ease);
}

.section-heading :deep(.section-link:hover) {
  color: var(--cyan);
  text-shadow: 0 0 16px rgba(126, 200, 227, 0.4);
}

.section-heading :deep(.section-link:hover svg) {
  transform: translateX(3px);
}

@media (max-width: 640px) {
  .section-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 18px;
  }

  .section-heading__aside {
    align-items: flex-start;
    padding-bottom: 0;
  }

  .section-heading__title {
    font-size: 1.85rem;
  }
}
</style>
