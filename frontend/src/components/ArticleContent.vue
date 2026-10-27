<script setup lang="ts">
import { computed } from 'vue'
import { renderMarkdown } from '@/utils/markdown'

const props = defineProps<{
  content: string
}>()

const html = computed(() => renderMarkdown(props.content))
</script>

<template>
  <!-- content 已经在 renderMarkdown 中经过 markdown-it + DOMPurify 处理 -->
  <div class="markdown-body" v-html="html" />
</template>

<style scoped>
.markdown-body {
  color: var(--ink-2);
  font-size: 0.98rem;
  line-height: 1.95;
  overflow-wrap: anywhere;
}

.markdown-body :deep(h1),
.markdown-body :deep(h2),
.markdown-body :deep(h3),
.markdown-body :deep(h4) {
  margin: 2.1em 0 0.7em;
  color: var(--ink);
  font-weight: 800;
  line-height: 1.45;
  letter-spacing: -0.02em;
}

.markdown-body :deep(h1) {
  font-size: 1.8rem;
}

.markdown-body :deep(h2) {
  padding-bottom: 0.35em;
  font-size: 1.4rem;
  border-bottom: 1px solid var(--line);
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
  color: var(--violet);
  text-decoration: underline;
  text-decoration-color: var(--violet-soft);
  text-underline-offset: 3px;
}

.markdown-body :deep(a:hover) {
  color: var(--violet-strong);
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
  color: var(--violet);
}

.markdown-body :deep(blockquote) {
  margin: 1.2em 0;
  padding: 0.7em 1.1em;
  color: var(--muted);
  background: var(--paper);
  border-left: 3px solid var(--violet);
  border-radius: 0 10px 10px 0;
}

.markdown-body :deep(blockquote p) {
  margin: 0;
}

.markdown-body :deep(code) {
  padding: 0.15em 0.4em;
  color: var(--violet-strong);
  font-family: var(--font-mono);
  font-size: 0.85em;
  background: var(--violet-soft);
  border-radius: 5px;
}

.markdown-body :deep(pre) {
  margin: 1.2em 0;
  padding: 18px 20px;
  overflow-x: auto;
  color: #e8e1d2;
  background: var(--night-1);
  border-radius: 12px;
}

.markdown-body :deep(pre code) {
  padding: 0;
  color: inherit;
  font-size: 0.82rem;
  line-height: 1.8;
  background: transparent;
}

.markdown-body :deep(img) {
  width: auto;
  max-width: 100%;
  height: auto;
  margin: 1.2em auto;
  border-radius: 12px;
}

.markdown-body :deep(hr) {
  margin: 2em 0;
  border: 0;
  border-top: 1px solid var(--line);
}

.markdown-body :deep(table) {
  display: block;
  width: 100%;
  overflow-x: auto;
  margin: 1.2em 0;
  border-collapse: collapse;
  font-size: 0.86rem;
}

.markdown-body :deep(th),
.markdown-body :deep(td) {
  padding: 8px 12px;
  border: 1px solid var(--line);
  text-align: left;
}

.markdown-body :deep(th) {
  color: var(--ink);
  background: var(--paper);
}

.markdown-body :deep(> :first-child) {
  margin-top: 0;
}

.markdown-body :deep(> :last-child) {
  margin-bottom: 0;
}

@media (max-width: 640px) {
  .markdown-body {
    font-size: 0.94rem;
    line-height: 1.9;
  }

  .markdown-body :deep(h2) {
    font-size: 1.25rem;
  }

  .markdown-body :deep(pre) {
    padding: 14px;
    font-size: 0.78rem;
  }
}
</style>
