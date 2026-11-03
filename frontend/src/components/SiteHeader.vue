<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AuthMenu from '@/components/AuthMenu.vue'
import { navLinks } from '@/config/navigation'
import { siteConfig } from '@/config/site'
import { resolveAssetUrl } from '@/utils/url'

const route = useRoute()
const menuOpen = ref(false)
const scrolled = ref(false)
const headerRef = ref(null)
const navRef = ref(null)
const brandImageFailed = ref(false)
const activeHover = ref(null)
const hoverX = ref(0)
const preloadedImages = []

const brandImage = computed(() =>
  resolveAssetUrl(siteConfig.avatarImage || siteConfig.characterImage),
)
const activeHoverImage = computed(() =>
  resolveAssetUrl(activeHover.value?.hoverImg || siteConfig.navCharacterImage),
)
const showAuthMenu = computed(() => route.meta.admin === true)
const isAdminRoute = computed(() => route.meta.admin === true)
const publicLinkTarget = computed(() => (isAdminRoute.value ? '_blank' : undefined))
const publicLinkRel = computed(() =>
  isAdminRoute.value ? 'noopener noreferrer' : undefined,
)

function clearHover() {
  activeHover.value = null
}

function activateHover(link, event) {
  activeHover.value = link

  const nav = navRef.value
  const target = event?.currentTarget
  if (!nav || !target) return

  const navRect = nav.getBoundingClientRect()
  const linkRect = target.getBoundingClientRect()
  hoverX.value = linkRect.left - navRect.left + linkRect.width / 2
}

function preloadHoverImages() {
  navLinks.forEach((link) => {
    const src = resolveAssetUrl(link.hoverImg)
    if (!src) return
    const image = new Image()
    image.decoding = 'async'
    image.src = src
    preloadedImages.push(image)
  })
}

function closeMenu() {
  menuOpen.value = false
}

function toggleMenu() {
  menuOpen.value = !menuOpen.value
}

function handlePointerDown(event) {
  if (!menuOpen.value) return
  const target = event.target
  if (target && headerRef.value?.contains(target)) return
  closeMenu()
}

function handleKeydown(event) {
  if (event.key === 'Escape') closeMenu()
}

function handleScroll() {
  scrolled.value = window.scrollY > 14
}

watch(() => route.fullPath, () => {
  closeMenu()
  clearHover()
})

onMounted(() => {
  handleScroll()
  window.addEventListener('scroll', handleScroll, { passive: true })
  document.addEventListener('pointerdown', handlePointerDown)
  document.addEventListener('keydown', handleKeydown)
  preloadHoverImages()
})

onBeforeUnmount(() => {
  window.removeEventListener('scroll', handleScroll)
  document.removeEventListener('pointerdown', handlePointerDown)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <header
    ref="headerRef"
    class="site-header"
    :class="{ 'is-scrolled': scrolled }"
  >
    <div class="container header-inner">
      <RouterLink
        to="/"
        class="brand"
        aria-label="Start Night 首页"
        :target="publicLinkTarget"
        :rel="publicLinkRel"
        @click="closeMenu"
      >
        <span class="brand__mark" aria-hidden="true">
          <img
            v-if="brandImage && !brandImageFailed"
            :src="brandImage"
            alt=""
            loading="eager"
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
        <span class="brand__text">
          <span class="brand__name">{{ siteConfig.brand }}</span>
          <span class="brand__tagline">{{ siteConfig.tagline }}</span>
        </span>
      </RouterLink>

      <nav
        id="site-nav"
        ref="navRef"
        class="nav"
        :class="{ 'is-open': menuOpen }"
        aria-label="主导航"
        @mouseleave="clearHover"
      >
        <RouterLink
          v-for="(link, index) in navLinks"
          :key="link.label"
          class="nav__link"
          :to="link.to"
          :target="publicLinkTarget"
          :rel="publicLinkRel"
          @mouseenter="activateHover(link, $event)"
          @focusin="activateHover(link, $event)"
          @focusout="clearHover"
          @click="closeMenu"
        >
          <span class="nav__index" aria-hidden="true">0{{ index + 1 }}</span>
          <span class="nav__label">{{ link.label }}</span>
        </RouterLink>

        <div
          class="nav__hover-character"
          :class="{ 'is-visible': activeHover }"
          :style="{ '--nav-mascot-x': `${hoverX}px` }"
          aria-hidden="true"
        >
          <Transition name="nav-mascot" mode="out-in">
            <img
              v-if="activeHover && activeHoverImage"
              :key="activeHover.label"
              class="nav__hover-image"
              :src="activeHoverImage"
              alt=""
              width="80"
              height="83"
              decoding="async"
            />
          </Transition>
        </div>
      </nav>

      <div class="header-tools">
        <AuthMenu v-if="showAuthMenu" />
        <button
          class="nav-toggle"
          type="button"
          :aria-expanded="menuOpen"
          aria-controls="site-nav"
          :aria-label="menuOpen ? '关闭菜单' : '打开菜单'"
          @click="toggleMenu"
        >
          <svg v-if="!menuOpen" viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="M5 7h14M5 12h10M5 17h14"
              fill="none"
              stroke="currentColor"
              stroke-width="1.7"
              stroke-linecap="round"
            />
          </svg>
          <svg v-else viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="m6 6 12 12M18 6 6 18"
              fill="none"
              stroke="currentColor"
              stroke-width="1.7"
              stroke-linecap="round"
            />
          </svg>
        </button>
      </div>
    </div>
  </header>
</template>

<style scoped>
.site-header {
  position: sticky;
  top: 0;
  z-index: 90;
  background: rgba(10, 7, 19, 0.5);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.08);
  -webkit-backdrop-filter: blur(20px) saturate(150%);
  backdrop-filter: blur(20px) saturate(150%);
  transition:
    background 0.3s var(--ease),
    box-shadow 0.3s var(--ease);
}

.site-header.is-scrolled {
  background: rgba(10, 7, 19, 0.78);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.22);
}

.header-inner {
  position: relative;
  display: flex;
  align-items: center;
  gap: 26px;
  min-height: var(--header-h);
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 11px;
  flex: none;
}

.brand__mark {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.22);
  border-radius: 50%;
  box-shadow:
    0 0 0 4px rgba(255, 158, 181, 0.08),
    0 0 24px rgba(185, 103, 255, 0.25);
  transition:
    transform 0.3s var(--ease),
    box-shadow 0.3s var(--ease);
}

.brand:hover .brand__mark {
  box-shadow:
    0 0 0 4px rgba(255, 158, 181, 0.15),
    0 0 30px rgba(185, 103, 255, 0.45);
  transform: rotate(-5deg) scale(1.04);
}

.brand__mark svg {
  width: 100%;
  height: 100%;
}

.brand__mark img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 20%;
}

.brand__text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand__name {
  color: var(--ink);
  font-family: var(--font-mono);
  font-size: 0.82rem;
  font-weight: 700;
  letter-spacing: 0.2em;
  text-shadow: 0 0 20px rgba(255, 158, 181, 0.24);
}

.brand__tagline {
  margin-top: 3px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.48rem;
  letter-spacing: 0.12em;
}

.nav {
  position: relative;
  display: flex;
  align-items: center;
  gap: 3px;
  margin-left: auto;
  padding: 4px;
  background: rgba(255, 255, 255, 0.035);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 999px;
  -webkit-backdrop-filter: blur(14px);
  backdrop-filter: blur(14px);
}

.nav__link {
  position: relative;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0;
  padding: 8px 18px;
  color: var(--muted);
  font-size: 0.78rem;
  border-radius: 999px;
  transition:
    color 0.22s var(--ease),
    background 0.22s var(--ease),
    text-shadow 0.22s var(--ease);
}

.nav__hover-character {
  position: absolute;
  top: calc(100% + 8px);
  left: var(--nav-mascot-x, 50%);
  z-index: 60;
  width: 80px;
  height: auto;
  pointer-events: none;
  transform: translateX(-50%);
  transition: left 0.28s var(--ease);
  filter:
    drop-shadow(0 10px 14px rgba(0, 0, 0, 0.42))
    drop-shadow(0 0 18px rgba(255, 158, 181, 0.28));
}

.nav__hover-character::before {
  position: absolute;
  right: 8px;
  bottom: 1px;
  left: 8px;
  z-index: 0;
  height: 16px;
  background: radial-gradient(ellipse, rgba(255, 158, 181, 0.44), transparent 72%);
  filter: blur(4px);
  content: '';
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.25s var(--ease);
}

.nav__hover-character.is-visible::before {
  opacity: 1;
}

.nav__hover-image {
  position: relative;
  z-index: 1;
  display: block;
  width: 100%;
  height: auto;
  pointer-events: none;
}

.nav-mascot-enter-active,
.nav-mascot-leave-active {
  transition:
    opacity 0.22s var(--ease),
    transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.nav-mascot-enter-from {
  opacity: 0;
  transform: translateY(10px) scale(0.9);
}

.nav-mascot-leave-to {
  opacity: 0;
  transform: translateY(4px) scale(0.94);
}

.nav__index {
  position: absolute;
  top: 2px;
  right: 7px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.4rem;
  line-height: 1;
  opacity: 0.72;
  pointer-events: none;
}

.nav__link:hover {
  color: var(--ink);
  background: rgba(255, 255, 255, 0.06);
  text-shadow: 0 0 18px rgba(255, 158, 181, 0.45);
}

.nav__link::after {
  position: absolute;
  right: 16px;
  bottom: 4px;
  left: 16px;
  height: 1px;
  background: var(--gradient);
  content: '';
  opacity: 0;
  transform: scaleX(0.4);
  transform-origin: left;
  transition:
    opacity 0.25s var(--ease),
    transform 0.25s var(--ease);
}

.nav__link:hover::after {
  opacity: 1;
  transform: scaleX(1);
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 6px;
  flex: none;
}

.nav-toggle {
  display: none;
  place-items: center;
  width: 38px;
  height: 38px;
  color: var(--ink-2);
  border: 1px solid var(--glass-border);
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.045);
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease),
    border-color 0.2s var(--ease);
}

.nav-toggle:hover {
  color: var(--pink);
  background: rgba(255, 158, 181, 0.1);
  border-color: rgba(255, 158, 181, 0.35);
}

.nav-toggle svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 1020px) {
  .brand__tagline {
    display: none;
  }

  .nav {
    gap: 0;
  }

  .nav__link {
    padding-inline: 10px;
  }

  .nav__hover-character {
    width: 72px;
  }
}

@media (max-width: 900px), (hover: none) {
  .nav__hover-character {
    display: none;
  }
}

@media (max-width: 900px) {
  .nav-toggle {
    display: grid;
  }

  .nav {
    position: absolute;
    top: calc(100% + 12px);
    right: 0;
    left: 0;
    z-index: 95;
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 4px;
    width: 100%;
    padding: 10px;
    background: rgba(11, 7, 20, 0.97);
    border-color: rgba(255, 255, 255, 0.16);
    border-radius: 20px;
    box-shadow:
      0 24px 70px rgba(0, 0, 0, 0.65),
      0 0 32px rgba(185, 103, 255, 0.1);
    -webkit-backdrop-filter: blur(26px) saturate(150%);
    backdrop-filter: blur(26px) saturate(150%);
    opacity: 0;
    visibility: hidden;
    transform: translateY(-8px) scale(0.99);
    transition:
      opacity 0.22s var(--ease),
      visibility 0.22s var(--ease),
      transform 0.22s var(--ease);
  }

  .nav.is-open {
    opacity: 1;
    visibility: visible;
    transform: translateY(0) scale(1);
  }

  .nav__link {
    justify-content: flex-start;
    padding: 13px 16px;
    font-size: 0.9rem;
    border-radius: 12px;
  }

  .nav__index {
    top: 10px;
    right: 12px;
    font-size: 0.58rem;
  }

  .nav__link::after {
    display: none;
  }

  .nav__link:hover {
    background: rgba(255, 255, 255, 0.08);
  }
}

@media (max-width: 480px) {
  .header-inner {
    gap: 12px;
  }

  .brand__mark {
    width: 36px;
    height: 36px;
  }

  .brand__name {
    font-size: 0.72rem;
    letter-spacing: 0.14em;
  }
}
</style>
