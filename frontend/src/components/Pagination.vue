<script setup>
import { computed } from 'vue'

const props = defineProps({
  page: { type: Number, required: true },
  totalPages: { type: Number, required: true },
  compact: { type: Boolean, default: false },
})

const emit = defineEmits(['change'])

const pages = computed(() => {
  const total = props.totalPages
  if (total <= 1) return []

  if (total <= 7) {
    return Array.from({ length: total }, (_, index) => index + 1)
  }

  const set = new Set([1, total, props.page - 1, props.page, props.page + 1])
  if (props.page <= 3) {
    ;[2, 3, 4].forEach((page) => set.add(page))
  }
  if (props.page >= total - 2) {
    ;[total - 3, total - 2, total - 1].forEach((page) => set.add(page))
  }

  const sorted = [...set].filter((page) => page >= 1 && page <= total).sort((a, b) => a - b)
  const result = []

  sorted.forEach((page, index) => {
    if (index > 0 && page - sorted[index - 1] > 1) result.push('gap')
    result.push(page)
  })

  return result
})

function go(page) {
  if (page < 1 || page > props.totalPages || page === props.page) return
  emit('change', page)
}
</script>

<template>
  <nav
    v-if="totalPages > 1"
    class="pagination"
    :class="{ 'pagination--compact': compact }"
    aria-label="分页"
  >
    <button
      class="pagination__arrow"
      type="button"
      :disabled="page <= 1"
      aria-label="上一页"
      @click="go(page - 1)"
    >
      ←
    </button>

    <template v-for="(item, index) in pages" :key="`${item}-${index}`">
      <span v-if="item === 'gap'" class="pagination__gap" aria-hidden="true">…</span>
      <button
        v-else
        class="pagination__page"
        type="button"
        :aria-current="item === page ? 'page' : undefined"
        :aria-label="`第 ${item} 页`"
        @click="go(item)"
      >
        {{ item }}
      </button>
    </template>

    <button
      class="pagination__arrow"
      type="button"
      :disabled="page >= totalPages"
      aria-label="下一页"
      @click="go(page + 1)"
    >
      →
    </button>
  </nav>
</template>

<style scoped>
.pagination {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: center;
  gap: 7px;
  margin-top: 32px;
}

.pagination button {
  display: grid;
  place-items: center;
  min-width: 38px;
  height: 38px;
  padding: 0 9px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.68rem;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 11px;
  -webkit-backdrop-filter: blur(12px);
  backdrop-filter: blur(12px);
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease),
    border-color 0.2s var(--ease),
    transform 0.2s var(--ease),
    box-shadow 0.2s var(--ease);
}

.pagination button:hover:not(:disabled) {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.1);
  border-color: rgba(255, 158, 181, 0.3);
  box-shadow: 0 0 20px rgba(255, 158, 181, 0.12);
  transform: translateY(-1px);
}

.pagination button:disabled {
  opacity: 0.3;
}

.pagination__page[aria-current='page'] {
  color: #24102a;
  background: var(--gradient);
  border-color: transparent;
  box-shadow: 0 6px 20px rgba(185, 103, 255, 0.25);
}

.pagination__gap {
  padding: 0 2px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.66rem;
}

.pagination--compact {
  justify-content: flex-start;
  margin-top: 20px;
}

.pagination--compact button {
  min-width: 32px;
  height: 32px;
  font-size: 0.62rem;
  border-radius: 9px;
}
</style>
