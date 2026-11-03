<script setup>
defineProps({
  title: { type: String, default: '夜色还没落下' },
  message: { type: String, default: '' },
  compact: { type: Boolean, default: false },
})
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
          d="M24.5 11.5l.9 2.1 2.1.9-2.1.9-.9 2.1-.9-2.1-2.1-.9 2.1-.9z"
          fill="var(--pink)"
        />
      </svg>
    </span>

    <div class="empty-state__copy">
      <h3 class="empty-state__title">{{ title }}</h3>
      <p v-if="message" class="empty-state__message">{{ message }}</p>
    </div>

    <span class="empty-state__spark" aria-hidden="true">✦</span>
  </div>
</template>

<style scoped>
.empty-state {
  position: relative;
  display: flex;
  align-items: flex-start;
  gap: 14px;
  overflow: hidden;
  padding: 24px;
  background: rgba(255, 255, 255, 0.045);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: var(--radius-md);
  box-shadow: var(--glass-shadow);
  -webkit-backdrop-filter: blur(14px);
  backdrop-filter: blur(14px);
}

.empty-state::after {
  position: absolute;
  right: -40px;
  bottom: -70px;
  width: 170px;
  height: 170px;
  border: 1px solid rgba(255, 158, 181, 0.1);
  border-radius: 50%;
  box-shadow: 0 0 0 22px rgba(185, 103, 255, 0.035);
  content: '';
}

.empty-state__mark {
  display: grid;
  place-items: center;
  flex: none;
  width: 46px;
  height: 46px;
  color: var(--pink);
  background:
    radial-gradient(circle at 36% 30%, rgba(255, 255, 255, 0.2), transparent 46%),
    rgba(255, 158, 181, 0.1);
  border: 1px solid rgba(255, 158, 181, 0.18);
  border-radius: 50%;
  box-shadow: 0 0 24px rgba(255, 158, 181, 0.14);
}

.empty-state__mark svg {
  width: 26px;
  height: 26px;
}

.empty-state__copy {
  position: relative;
  z-index: 1;
}

.empty-state__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 1.05rem;
  font-weight: 400;
  letter-spacing: 0.02em;
}

.empty-state__message {
  max-width: 42em;
  margin-top: 5px;
  color: var(--muted);
  font-size: 0.74rem;
  line-height: 1.8;
}

.empty-state__spark {
  position: absolute;
  top: 18px;
  right: 20px;
  color: rgba(255, 158, 181, 0.45);
  font-size: 0.8rem;
  animation: twinkle 4s ease-in-out infinite;
}

.empty-state--compact {
  padding: 15px;
  gap: 11px;
  border-radius: 14px;
}

.empty-state--compact .empty-state__mark {
  width: 36px;
  height: 36px;
}

.empty-state--compact .empty-state__mark svg {
  width: 21px;
  height: 21px;
}

.empty-state--compact .empty-state__title {
  font-size: 0.85rem;
}

.empty-state--compact .empty-state__message {
  font-size: 0.68rem;
}

@media (max-width: 640px) {
  .empty-state {
    padding: 18px;
  }
}
</style>
