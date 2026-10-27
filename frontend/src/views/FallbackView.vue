<template>
  <section class="fallback">
    <div class="container fallback__inner">
      <span class="fallback__mark" aria-hidden="true">✦</span>
      <p class="fallback__code">404</p>
      <h1 class="fallback__title">这条小径还没有灯</h1>
      <p class="fallback__text">
        你访问的页面不存在，或者已经被夜色收起来了。
      </p>

      <div class="fallback__actions">
        <RouterLink class="btn btn--primary" to="/">回到首页</RouterLink>
        <RouterLink class="btn fallback__ghost" to="/posts">
          逛逛全部文章
        </RouterLink>
      </div>
    </div>
  </section>
</template>

<style scoped>
.fallback {
  display: flex;
  align-items: center;
  min-height: 58vh;
  padding: 72px 0;
}

.fallback__inner {
  max-width: 640px;
}

.fallback__mark {
  display: inline-grid;
  place-items: center;
  width: 56px;
  height: 56px;
  margin-bottom: 22px;
  color: var(--star);
  background: var(--night-1);
  border-radius: 50%;
  box-shadow: 0 0 0 8px rgba(100, 84, 200, 0.08);
}

.fallback__code {
  margin-bottom: 10px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  letter-spacing: 0.16em;
}

.fallback__title {
  color: var(--ink);
  font-size: clamp(2rem, 4vw, 2.8rem);
  font-weight: 800;
  letter-spacing: -0.03em;
  line-height: 1.3;
}

.fallback__text {
  max-width: 32em;
  margin-top: 14px;
  color: var(--muted);
  font-size: 0.9rem;
  line-height: 1.9;
}

.fallback__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 30px;
}

.fallback__ghost {
  color: var(--ink-2);
  border-color: var(--line-strong);
}

.fallback__ghost:hover {
  color: var(--violet);
  background: var(--violet-soft);
  border-color: var(--violet-soft);
}

@media (max-width: 560px) {
  .fallback {
    padding: 48px 0;
  }
}
</style>
