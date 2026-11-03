<script setup>
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import { navLinks } from '@/config/navigation'
import { siteConfig } from '@/config/site'
import { resolveAssetUrl } from '@/utils/url'

const route = useRoute()
const year = computed(() => new Date().getFullYear())
const isAdminRoute = computed(() => route.meta.admin === true)
const publicLinkTarget = computed(() => (isAdminRoute.value ? '_blank' : undefined))
const publicLinkRel = computed(() =>
  isAdminRoute.value ? 'noopener noreferrer' : undefined,
)
const brandImageFailed = ref(false)
const brandImage = computed(() =>
  resolveAssetUrl(siteConfig.avatarImage || siteConfig.characterImage),
)

function backToTop() {
  const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
  window.scrollTo({ top: 0, behavior: reduceMotion ? 'auto' : 'smooth' })
}
</script>

<template>
  <footer class="site-footer">
    <div class="footer-aura" aria-hidden="true" />
    <div class="container footer__main">
      <div class="footer__brand">
        <span class="footer__mark" aria-hidden="true">
          <img
            v-if="brandImage && !brandImageFailed"
            :src="brandImage"
            alt=""
            loading="lazy"
            decoding="async"
            @error="brandImageFailed = true"
          />
          <svg v-else viewBox="0 0 32 32">
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
          :target="publicLinkTarget"
          :rel="publicLinkRel"
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
      <span>Vue 3 · Spring Boot · Made for the night</span>
    </div>
  </footer>
</template>

<style scoped>
.site-footer {
  position: relative;
  z-index: 2;
  overflow: hidden;
  margin-top: 80px;
  background: rgba(9, 6, 17, 0.62);
  border-top: 1px solid rgba(255, 255, 255, 0.09);
  -webkit-backdrop-filter: blur(20px) saturate(140%);
  backdrop-filter: blur(20px) saturate(140%);
}

.footer-aura {
  position: absolute;
  top: -80px;
  right: 8%;
  width: 360px;
  height: 200px;
  background: radial-gradient(ellipse, rgba(185, 103, 255, 0.14), transparent 68%);
  filter: blur(30px);
  pointer-events: none;
}

.footer__main {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 22px;
  padding-top: 34px;
  padding-bottom: 24px;
}

.footer__brand {
  display: flex;
  align-items: center;
  gap: 11px;
}

.footer__mark {
  display: grid;
  place-items: center;
  width: 40px;
  height: 40px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 50%;
  box-shadow: 0 0 24px rgba(185, 103, 255, 0.18);
}

.footer__mark svg {
  width: 100%;
  height: 100%;
}

.footer__mark img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 20%;
}

.footer__brand-text {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.footer__brand-text strong {
  color: var(--ink);
  font-family: var(--font-mono);
  font-size: 0.72rem;
  letter-spacing: 0.16em;
}

.footer__brand-text span {
  color: var(--muted);
  font-size: 0.68rem;
}

.footer__nav {
  display: flex;
  flex-wrap: wrap;
  gap: 6px 18px;
}

.footer__link {
  color: var(--muted);
  font-size: 0.72rem;
  transition:
    color 0.2s var(--ease),
    text-shadow 0.2s var(--ease);
}

.footer__link:hover {
  color: var(--pink);
  text-shadow: 0 0 16px rgba(255, 158, 181, 0.4);
}

.footer__top {
  display: inline-flex;
  align-items: center;
  gap: 7px;
  padding: 8px 13px;
  color: var(--ink-2);
  font-size: 0.68rem;
  border: 1px solid rgba(255, 255, 255, 0.11);
  border-radius: 999px;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease);
}

.footer__top span {
  color: var(--pink);
}

.footer__top:hover {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.08);
}

.footer__bottom {
  position: relative;
  z-index: 1;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 8px 20px;
  padding-top: 16px;
  padding-bottom: 24px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.55rem;
  border-top: 1px solid rgba(255, 255, 255, 0.07);
}

@media (max-width: 720px) {
  .site-footer {
    margin-top: 52px;
  }

  .footer__main {
    align-items: flex-start;
    padding-top: 26px;
  }

  .footer__nav {
    order: 3;
    width: 100%;
  }

  .footer__bottom {
    font-size: 0.5rem;
  }
}
</style>
