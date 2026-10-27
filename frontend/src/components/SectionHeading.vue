<script setup lang="ts">
import { useSlots } from 'vue'

withDefaults(
  defineProps<{
    kicker: string
    title: string
    meta?: string
    headingId?: string
  }>(),
  {
    meta: '',
    headingId: '',
  },
)

const slots = useSlots()
</script>

<template>
  <div class="section-heading">
    <div class="section-heading__copy">
      <p class="section-heading__kicker">
        <span class="section-heading__star" aria-hidden="true">✦</span>
        {{ kicker }}
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
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  margin-bottom: 26px;
}

.section-heading__kicker {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 7px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.18em;
}

.section-heading__star {
  color: var(--gold);
  font-size: 0.7rem;
}

.section-heading__title {
  color: var(--ink);
  font-size: 1.9rem;
  font-weight: 800;
  letter-spacing: -0.025em;
  line-height: 1.3;
}

.section-heading__aside {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  padding-bottom: 4px;
}

.section-heading__meta {
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.08em;
  white-space: nowrap;
}

@media (max-width: 640px) {
  .section-heading {
    align-items: flex-start;
    flex-direction: column;
    gap: 8px;
    margin-bottom: 20px;
  }

  .section-heading__title {
    font-size: 1.6rem;
  }

  .section-heading__aside {
    align-items: flex-start;
    padding-bottom: 0;
  }
}
</style>
