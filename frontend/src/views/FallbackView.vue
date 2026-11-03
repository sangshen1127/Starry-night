<template>
  <section class="fallback">
    <div class="container fallback__inner">
      <div class="fallback__card glass-panel">
        <span class="fallback__mark" aria-hidden="true">✦</span>
        <p class="fallback__code">404 · LOST IN THE NIGHT</p>
        <h1 class="fallback__title">这条小径还没有灯</h1>
        <p class="fallback__text">
          你访问的页面不存在，或者已经被夜色收起来了。
        </p>

        <div class="fallback__actions">
          <RouterLink class="btn btn--primary" to="/">回到首页</RouterLink>
          <RouterLink class="btn btn--ghost" to="/posts">逛逛全部文章</RouterLink>
        </div>

        <span class="fallback__deco" aria-hidden="true">♡ にゃ</span>
      </div>
    </div>
  </section>
</template>

<style scoped>
.fallback {
  display: flex;
  align-items: center;
  min-height: calc(100vh - var(--header-h));
  padding: 48px 0;
}

.fallback__inner {
  max-width: 680px;
}

.fallback__card {
  position: relative;
  overflow: hidden;
  padding: 48px 44px;
  border-radius: 26px;
}

.fallback__card::after {
  position: absolute;
  right: -80px;
  bottom: -100px;
  width: 270px;
  height: 270px;
  border: 1px solid rgba(255, 158, 181, 0.12);
  border-radius: 50%;
  box-shadow:
    0 0 0 26px rgba(185, 103, 255, 0.03),
    0 0 0 60px rgba(126, 200, 227, 0.02);
  content: '';
}

.fallback__mark {
  display: grid;
  place-items: center;
  width: 54px;
  height: 54px;
  margin-bottom: 20px;
  color: var(--pink);
  font-size: 1.1rem;
  background: rgba(255, 158, 181, 0.09);
  border: 1px solid rgba(255, 158, 181, 0.18);
  border-radius: 50%;
  box-shadow: 0 0 28px rgba(255, 158, 181, 0.16);
  animation: glow-pulse 4s ease-in-out infinite;
}

.fallback__code {
  margin-bottom: 8px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.55rem;
  letter-spacing: 0.14em;
}

.fallback__title {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(2rem, 4vw, 2.7rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.35;
  text-shadow: 0 0 26px rgba(255, 158, 181, 0.16);
}

.fallback__text {
  max-width: 32em;
  margin-top: 10px;
  color: var(--muted);
  font-size: 0.82rem;
  line-height: 1.85;
}

.fallback__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 24px;
}

.fallback__deco {
  position: absolute;
  right: 24px;
  bottom: 20px;
  color: rgba(255, 158, 181, 0.35);
  font-family: var(--font-display);
  font-size: 0.65rem;
  letter-spacing: 0.08em;
}

@media (max-width: 560px) {
  .fallback__card {
    padding: 28px 22px;
    border-radius: 20px;
  }
}
</style>
