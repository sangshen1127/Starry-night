<script setup lang="ts">
withDefaults(
  defineProps<{
    title?: string
    message: string
    compact?: boolean
  }>(),
  {
    title: '夜色暂时断线了',
    compact: false,
  },
)

const emit = defineEmits<{
  retry: []
}>()
</script>

<template>
  <div class="error-state" :class="{ 'error-state--compact': compact }" role="alert">
    <span class="error-state__mark" aria-hidden="true">✧</span>
    <div class="error-state__copy">
      <p class="error-state__title">{{ title }}</p>
      <p class="error-state__message">{{ message }}</p>
    </div>
    <button class="btn btn--primary error-state__retry" type="button" @click="emit('retry')">
      重新加载
    </button>
  </div>
</template>

<style scoped>
.error-state {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 14px 18px;
  padding: 24px 26px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.error-state__mark {
  display: grid;
  place-items: center;
  flex: none;
  width: 38px;
  height: 38px;
  color: var(--gold);
  background: var(--gold-soft);
  border-radius: 50%;
  font-size: 1rem;
}

.error-state__copy {
  flex: 1;
  min-width: 180px;
}

.error-state__title {
  color: var(--ink);
  font-size: 0.9rem;
  font-weight: 700;
}

.error-state__message {
  margin-top: 3px;
  color: var(--muted);
  font-size: 0.78rem;
  line-height: 1.7;
}

.error-state__retry {
  min-height: 36px;
  padding: 7px 16px;
  font-size: 0.76rem;
}

.error-state--compact {
  gap: 10px;
  padding: 0;
  background: transparent;
  border: 0;
  box-shadow: none;
}

.error-state--compact .error-state__mark {
  width: 26px;
  height: 26px;
  font-size: 0.76rem;
}

.error-state--compact .error-state__title {
  font-size: 0.8rem;
}

.error-state--compact .error-state__message {
  font-size: 0.72rem;
}

.error-state--compact .error-state__retry {
  min-height: 30px;
  padding: 4px 12px;
  font-size: 0.7rem;
}
</style>
