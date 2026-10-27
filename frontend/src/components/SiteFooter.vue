<script setup lang="ts">
import { computed } from 'vue'
import { navLinks } from '@/config/navigation'
import { siteConfig } from '@/config/site'

const year = computed(() => new Date().getFullYear())

function backToTop(): void {
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({ top: 0, behavior: reduceMotion ? 'auto' : 'smooth' })
}
</script>

<template>
  <footer class="site-footer">
    <div class="container footer__main">
      <div class="footer__brand">
        <span class="footer__mark" aria-hidden="true">
          <svg viewBox="0 0 32 32">
            <path
              d="M20.8 5.3A11.2 11.2 0 1 0 26.7 21 9 9 0 0 1 20.8 5.3Z"
              fill="currentColor"
            />
            <path
              d="M24.5 11.5l.9 2.1 2.1.9-2.1.9-.9 2.1-.9-2.1-2.1-.9 2.1-.9z"
              fill="var(--star)"
            />
          </svg>
        </span>
        <span class="footer__brand-text">
          <strong>{{ siteConfig.brand }}</strong>
          <span>{{ siteConfig.footerNote }}</span>
        </span>
      </div>

      <nav class="footer__nav" aria-label="页脚导航">
        <RouterLink
          v-for="link in navLinks"
          :key="`footer-${link.label}`"
          class="footer__link"
          :to="link.to"
        >
          {{ link.label }}
        </RouterLink>
      </nav>

      <button class="footer__top" type="button" @click="backToTop">
        回到顶部
        <span aria-hidden="true">↑</span>
      </button>
    </div>

    <div class="container footer__bottom">
      <span>© {{ year }} {{ siteConfig.name }}. All rights reserved.</span>
      <span>Vue 3 · Spring Boot</span>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  position: relative;
  overflow: hidden;
  margin-top: 72px;
  color: var(--night-text);
  background:
    radial-gradient(700px 300px at 82% 0%, rgba(100, 84, 200, 0.16), transparent 70%),
    var(--night-1);
}

.site-footer::before {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(1.2px 1.2px at 12% 36%, rgba(255, 255, 255, 0.5), transparent 70%),
    radial-gradient(1px 1px at 72% 24%, rgba(255, 255, 255, 0.42), transparent 70%),
    radial-gradient(1.4px 1.4px at 88% 70%, rgba(244, 213, 141, 0.4), transparent 70%),
    radial-gradient(1px 1px at 35% 78%, rgba(255, 255, 255, 0.35), transparent 70%);
  content: '';
  pointer-events: none;
}

.footer__main,
.footer__bottom {
  position: relative;
  z-index: 1;
}

.footer__main {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding: 34px 0 26px;
}

.footer__brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.footer__mark {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  color: var(--moon);
  border: 1px solid var(--night-line);
  border-radius: 50%;
}

.footer__mark svg {
  width: 19px;
  height: 19px;
}

.footer__brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.footer__brand-text strong {
  color: var(--night-text);
  font-size: 0.78rem;
  font-weight: 600;
  letter-spacing: 0.16em;
}

.footer__brand-text span {
  color: var(--night-muted);
  font-size: 0.7rem;
}

.footer__nav {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.footer__link {
  color: var(--night-muted);
  font-size: 0.76rem;
  transition: color 0.2s var(--ease);
}

.footer__link:hover {
  color: var(--star);
}

.footer__top {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--night-muted);
  font-size: 0.72rem;
  transition: color 0.2s var(--ease);
}

.footer__top:hover {
  color: var(--star);
}

.footer__bottom {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 16px 0 20px;
  color: var(--night-muted);
  font-family: var(--font-mono);
  font-size: 0.64rem;
  border-top: 1px solid var(--night-line);
}

@media (max-width: 720px) {
  .site-footer {
    margin-top: 56px;
  }

  .footer__main {
    align-items: flex-start;
    flex-direction: column;
    gap: 18px;
    padding-top: 28px;
  }

  .footer__bottom {
    justify-content: flex-start;
  }
}
</style>
