<script setup>
defineProps({
  featured: { type: Boolean, default: false },
})
</script>

<template>
  <article
    class="post-skeleton"
    :class="{ 'post-skeleton--featured': featured }"
    aria-hidden="true"
  >
    <div class="skeleton post-skeleton__thumb" />
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
  display: grid;
  grid-template-columns: 92px minmax(0, 1fr);
  gap: 16px;
  align-items: center;
  padding: 16px;
  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 18px;
}

.post-skeleton__thumb {
  width: 92px;
  height: 92px;
  border-radius: 50%;
}

.post-skeleton__body {
  display: flex;
  flex-direction: column;
  gap: 9px;
}

.post-skeleton__line {
  height: 10px;
  border-radius: 5px;
}

.post-skeleton__line--meta {
  width: 34%;
  height: 7px;
}

.post-skeleton__line--title {
  width: 82%;
  height: 15px;
}

.post-skeleton__line--title-short {
  width: 55%;
  height: 15px;
}

.post-skeleton__line--short {
  width: 62%;
}

.post-skeleton--featured {
  display: flex;
  flex-direction: column;
  gap: 0;
  min-height: 300px;
  padding: 0;
  overflow: hidden;
}

.post-skeleton--featured .post-skeleton__thumb {
  width: 100%;
  height: 220px;
  border-radius: 0;
}

.post-skeleton--featured .post-skeleton__body {
  padding: 24px;
}

@media (max-width: 560px) {
  .post-skeleton {
    grid-template-columns: 76px minmax(0, 1fr);
    gap: 12px;
    padding: 13px;
  }

  .post-skeleton__thumb {
    width: 76px;
    height: 76px;
  }
}
</style>
