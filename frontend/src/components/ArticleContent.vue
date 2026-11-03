<script setup>
import { computed } from 'vue'
import { renderMarkdown } from '@/utils/markdown'

const props = defineProps({
  content: { type: String, required: true },
})

const html = computed(() => renderMarkdown(props.content))
</script>

<template>
  <div class="markdown-body" v-html="html" />
</template>

<style scoped>
.markdown-body {
  color: var(--ink-2);
  font-size: 0.94rem;
  line-height: 2;
  overflow-wrap: anywhere;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4) {
  margin: 2.1em 0 0.7em;
  color: var(--ink);
  font-family: var(--font-display);
  font-weight: 400;
  line-height: 1.45;
  letter-spacing: 0.01em;
}

.markdown-body :deep(h1) {
  font-size: 1.85rem;
}

.markdown-body :deep(h2) {
  padding-bottom: 0.42em;
  font-size: 1.45rem;
  border-bottom: 1px solid rgba(255, 158, 181, 0.16);
}

.markdown-body :deep(h3) {
  font-size: 1.15rem;
}

.markdown-body :deep(h4) {
  font-size: 1rem;
}

.markdown-body :deep(p) {
  margin: 0.9em 0;
}

.markdown-body :deep(a) {
  color: var(--cyan);
  text-decoration: underline;
  text-decoration-color: rgba(126, 200, 227, 0.35);
  text-underline-offset: 3px;
  transition: color 0.2s var(--ease);
}

.markdown-body :deep(a:hover) {
  color: var(--pink);
}

.markdown-body :deep(strong) {
  color: var(--ink);
  font-weight: 700;
}

.markdown-body :deep(ul),
.markdown-body :deep(ol) {
  margin: 0.9em 0;
  padding-left: 1.45em;
}

.markdown-body :deep(ul) {
  list-style: disc;
}

.markdown-body :deep(ol) {
  list-style: decimal;
}

.markdown-body :deep(li) {
  margin: 0.3em 0;
}

.markdown-body :deep(li::marker) {
  color: var(--pink);
}

.markdown-body :deep(blockquote) {
  margin: 1.2em 0;
  padding: 0.8em 1.1em;
  color: var(--muted);
  background: rgba(185, 103, 255, 0.08);
  border-left: 2px solid var(--pink);
  border-radius: 0 12px 12px 0;
}

.markdown-body :deep(blockquote p) {
  margin: 0;
}

.markdown-body :deep(code) {
  padding: 0.16em 0.42em;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.84em;
  background: rgba(255, 158, 181, 0.1);
  border: 1px solid rgba(255, 158, 181, 0.12);
  border-radius: 5px;
}

.markdown-body :deep(pre) {
  margin: 1.2em 0;
  padding: 18px 20px;
  overflow-x: auto;
  color: rgba(255, 255, 255, 0.88);
  background:
    linear-gradient(180deg, rgba(255, 255, 255, 0.035), transparent),
    #0b0714;
  border: 1px solid rgba(255, 255, 255, 0.09);
  border-radius: 14px;
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.05);
}

.markdown-body :deep(pre code) {
  padding: 0;
  color: inherit;
  font-size: 0.8rem;
  line-height: 1.85;
  background: transparent;
  border: 0;
}

.markdown-body :deep(img) {
  width: auto;
  max-width: 100%;
  height: auto;
  margin: 1.2em auto;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 14px;
}

.markdown-body :deep(hr) {
  margin: 2em 0;
  border: 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.markdown-body :deep(table) {
  display: block;
  width: 100%;
  overflow-x: auto;
  margin: 1.2em 0;
  border-collapse: collapse;
  font-size: 0.82rem;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  text-align: left;
}

.markdown-body :deep(th) {
  color: var(--ink);
  background: rgba(255, 255, 255, 0.04);
}

.markdown-body :deep(> :first-child) {
  margin-top: 0;
}

.markdown-body :deep(> :last-child) {
  margin-bottom: 0;
}

@media (max-width: 640px) {
  .markdown-body {
    font-size: 0.9rem;
    line-height: 1.95;
  }

  .markdown-body :deep(h2) {
    font-size: 1.27rem;
  }

  .markdown-body :deep(pre) {
    padding: 14px;
  }
}
</style>
