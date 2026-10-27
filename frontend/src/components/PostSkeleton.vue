<script setup lang="ts">
withDefaults(
  defineProps<{
    featured?: boolean
  }>(),
  {
    featured: false,
  },
)
</script>

<template>
  <article
    class="post-skeleton"
    :class="{ 'post-skeleton--featured': featured }"
    aria-hidden="true"
  >
    <div class="skeleton post-skeleton__media" />
    <div class="post-skeleton__body">
      <span class="skeleton post-skeleton__line post-skeleton__line--meta" />
      <span class="skeleton post-skeleton__line post-skeleton__line--title" />
      <span class="skeleton post-skeleton__line post-skeleton__line--title-short" />
      <span class="skeleton post-skeleton__line" />
      <span class="skeleton post-skeleton__line post-skeleton__line--short" />
    </div>
  </article>
</template>

<style scoped>
.post-skeleton {
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.post-skeleton__media {
  width: 100%;
  aspect-ratio: 16 / 10;
  border-radius: 0;
}

.post-skeleton__body {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 20px;
}

.post-skeleton__line {
  height: 10px;
  border-radius: 4px;
}

.post-skeleton__line--meta {
  width: 35%;
  height: 8px;
}

.post-skeleton__line--title {
  width: 88%;
  height: 15px;
}

.post-skeleton__line--title-short {
  width: 62%;
  height: 15px;
}

.post-skeleton__line--short {
  width: 48%;
}

.post-skeleton--featured {
  display: grid;
  grid-template-columns: 1.04fr 0.96fr;
}

.post-skeleton--featured .post-skeleton__media {
  height: 100%;
  min-height: 340px;
  aspect-ratio: auto;
}

.post-skeleton--featured .post-skeleton__body {
  justify-content: center;
  padding: 34px;
  gap: 13px;
}

.post-skeleton--featured .post-skeleton__line--meta {
  width: 28%;
}

.post-skeleton--featured .post-skeleton__line--title {
  width: 92%;
  height: 22px;
}

.post-skeleton--featured .post-skeleton__line--title-short {
  width: 68%;
  height: 22px;
}

.post-skeleton--featured .post-skeleton__line {
  height: 11px;
}

@media (max-width: 860px) {
  .post-skeleton--featured {
    grid-template-columns: minmax(0, 1fr);
  }

  .post-skeleton--featured .post-skeleton__media {
    min-height: 0;
    aspect-ratio: 16 / 10;
  }

  .post-skeleton--featured .post-skeleton__body {
    padding: 24px 20px;
  }
}
</style>
