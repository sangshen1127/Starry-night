<script setup lang="ts">
withDefaults(
  defineProps<{
    title?: string
    message?: string
    compact?: boolean
  }>(),
  {
    title: '夜色还没落下',
    message: '',
    compact: false,
  },
)
</script>

<template>
  <div class="empty-state" :class="{ 'empty-state--compact': compact }" role="status">
    <span class="empty-state__mark" aria-hidden="true">
      <svg viewBox="0 0 32 32">
        <path
          d="M20.8 5.3A11.2 11.2 0 1 0 26.7 21 9 9 0 0 1 20.8 5.3Z"
          fill="currentColor"
        />
        <path
          d="M24.6 11.3l.8 2 2 .8-2 .8-.8 2-.8-2-2-.8 2-.8z"
          fill="var(--star)"
        />
      </svg>
    </span>
    <div>
      <p class="empty-state__title">{{ title }}</p>
      <p v-if="message" class="empty-state__message">{{ message }}</p>
    </div>
  </div>
</template>

<style scoped>
.empty-state {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 38px 32px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
}

.empty-state__mark {
  display: grid;
  place-items: center;
  flex: none;
  width: 42px;
  height: 42px;
  color: var(--violet);
  background: var(--violet-soft);
  border-radius: 50%;
}

.empty-state__mark svg {
  width: 22px;
  height: 22px;
}

.empty-state__title {
  color: var(--ink);
  font-size: 0.95rem;
  font-weight: 700;
  line-height: 1.5;
}

.empty-state__message {
  margin-top: 5px;
  color: var(--muted);
  font-size: 0.82rem;
  line-height: 1.8;
}

.empty-state--compact {
  gap: 10px;
  padding: 0;
  background: transparent;
  border: 0;
}

.empty-state--compact .empty-state__mark {
  width: 28px;
  height: 28px;
}

.empty-state--compact .empty-state__mark svg {
  width: 16px;
  height: 16px;
}

.empty-state--compact .empty-state__title {
  font-size: 0.8rem;
}

.empty-state--compact .empty-state__message {
  margin-top: 3px;
  font-size: 0.72rem;
}
</style>
