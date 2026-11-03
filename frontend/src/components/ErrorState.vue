<script setup>
defineProps({
  title: { type: String, default: '夜色暂时断线了' },
  message: { type: String, required: true },
  compact: { type: Boolean, default: false },
})

const emit = defineEmits(['retry'])
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
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 14px 18px;
  overflow: hidden;
  padding: 22px 24px;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(255, 158, 181, 0.16);
  border-radius: var(--radius-md);
  box-shadow: var(--glass-shadow);
  -webkit-backdrop-filter: blur(14px);
  backdrop-filter: blur(14px);
}

.error-state__mark {
  display: grid;
  place-items: center;
  flex: none;
  width: 42px;
  height: 42px;
  color: var(--pink);
  font-size: 1.1rem;
  background: rgba(255, 158, 181, 0.08);
  border: 1px solid rgba(255, 158, 181, 0.16);
  border-radius: 50%;
  box-shadow: 0 0 22px rgba(255, 158, 181, 0.12);
}

.error-state__copy {
  flex: 1;
  min-width: 180px;
}

.error-state__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 0.95rem;
  font-weight: 400;
}

.error-state__message {
  margin-top: 3px;
  color: var(--muted);
  font-size: 0.72rem;
  line-height: 1.7;
}

.error-state__retry {
  min-height: 34px;
  padding: 7px 15px;
  font-size: 0.66rem;
}

.error-state--compact {
  padding: 14px;
  gap: 10px;
  border-radius: 13px;
}

.error-state--compact .error-state__mark {
  width: 32px;
  height: 32px;
  font-size: 0.85rem;
}

.error-state--compact .error-state__title {
  font-size: 0.82rem;
}

.error-state--compact .error-state__message {
  font-size: 0.66rem;
}

.error-state--compact .error-state__retry {
  min-height: 29px;
  padding: 5px 11px;
  font-size: 0.6rem;
}

@media (max-width: 560px) {
  .error-state {
    align-items: flex-start;
    padding: 16px;
  }

  .error-state__retry {
    margin-left: 52px;
  }
}
</style>
