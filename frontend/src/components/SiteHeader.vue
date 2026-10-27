<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import AuthMenu from '@/components/AuthMenu.vue'
import { navLinks } from '@/config/navigation'
import { siteConfig } from '@/config/site'

const route = useRoute()
const menuOpen = ref(false)
const headerRef = ref<HTMLElement | null>(null)

function closeMenu(): void {
  menuOpen.value = false
}

function toggleMenu(): void {
  menuOpen.value = !menuOpen.value
}

function handlePointerDown(event: PointerEvent): void {
  if (!menuOpen.value) return

  const target = event.target as Node | null
  if (target && headerRef.value?.contains(target)) return

  closeMenu()
}

function handleKeydown(event: KeyboardEvent): void {
  if (event.key === 'Escape') closeMenu()
}

watch(() => route.fullPath, closeMenu)

onMounted(() => {
  document.addEventListener('pointerdown', handlePointerDown)
  document.addEventListener('keydown', handleKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('pointerdown', handlePointerDown)
  document.removeEventListener('keydown', handleKeydown)
})
</script>

<template>
  <header ref="headerRef" class="site-header">
    <div class="container header-inner">
      <RouterLink to="/" class="brand" aria-label="Start Night 首页" @click="closeMenu">
        <span class="brand__mark" aria-hidden="true">
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
        <span class="brand__text">
          <span class="brand__name">{{ siteConfig.brand }}</span>
          <span class="brand__tagline">{{ siteConfig.tagline }}</span>
        </span>
      </RouterLink>

      <nav id="site-nav" class="nav" :class="{ 'is-open': menuOpen }" aria-label="主导航">
        <RouterLink
          v-for="link in navLinks"
          :key="link.label"
          class="nav__link"
          :to="link.to"
          @click="closeMenu"
        >
          {{ link.label }}
        </RouterLink>
      </nav>

      <div class="header-tools">
        <AuthMenu />
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
              d="M4 7h16M4 12h16M4 17h16"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
              stroke-linecap="round"
            />
          </svg>
          <svg v-else viewBox="0 0 24 24" aria-hidden="true">
            <path
              d="M6 6l12 12M18 6 6 18"
              fill="none"
              stroke="currentColor"
              stroke-width="1.8"
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
  z-index: 50;
  background: rgba(247, 244, 238, 0.88);
  border-bottom: 1px solid var(--line);
  -webkit-backdrop-filter: blur(16px) saturate(1.15);
  backdrop-filter: blur(16px) saturate(1.15);
}

.header-inner {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  min-height: var(--header-h);
}

.brand {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  flex: none;
}

.brand__mark {
  display: grid;
  place-items: center;
  width: 34px;
  height: 34px;
  color: var(--moon);
  background: var(--night-1);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 50%;
  box-shadow: 0 0 0 4px rgba(100, 84, 200, 0.08);
}

.brand__mark svg {
  width: 19px;
  height: 19px;
}

.brand__text {
  display: flex;
  flex-direction: column;
  line-height: 1.15;
}

.brand__name {
  color: var(--ink);
  font-size: 0.84rem;
  font-weight: 700;
  letter-spacing: 0.18em;
}

.brand__tagline {
  margin-top: 3px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.5rem;
  letter-spacing: 0.12em;
}

.nav {
  display: flex;
  align-items: center;
  gap: 28px;
}

.nav__link {
  position: relative;
  padding: 7px 0;
  color: var(--ink-2);
  font-size: 0.8rem;
  transition: color 0.2s var(--ease);
}

.nav__link::after {
  position: absolute;
  right: 0;
  bottom: 0;
  left: 0;
  height: 1px;
  background: var(--violet);
  content: '';
  transform: scaleX(0);
  transform-origin: left;
  transition: transform 0.25s var(--ease);
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 6px;
}

.nav__link:hover {
  color: var(--violet);
}

.nav__link:hover::after {
  transform: scaleX(1);
}

.nav-toggle {
  display: none;
  place-items: center;
  width: 38px;
  height: 38px;
  color: var(--ink-2);
  border: 1px solid var(--line);
  border-radius: 50%;
  transition:
    color 0.2s var(--ease),
    background 0.2s var(--ease),
    border-color 0.2s var(--ease);
}

.nav-toggle:hover {
  color: var(--violet);
  background: var(--violet-soft);
  border-color: var(--violet-soft);
}

.nav-toggle svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 1024px) {
  .brand__tagline {
    display: none;
  }

  .nav {
    gap: 22px;
  }
}

@media (max-width: 860px) {
  .site-header {
    min-height: 68px;
  }

  .header-inner {
    min-height: 68px;
  }

  .nav-toggle {
    display: grid;
  }

  .nav {
    position: absolute;
    top: calc(100% + 10px);
    right: 0;
    left: 0;
    z-index: 55;
    display: flex;
    flex-direction: column;
    align-items: stretch;
    gap: 2px;
    width: 100%;
    padding: 10px;
    background: var(--surface);
    border: 1px solid var(--line);
    border-radius: 16px;
    box-shadow: var(--shadow-sm);
    opacity: 0;
    visibility: hidden;
    transform: translateY(-6px);
    transition:
      opacity 0.22s var(--ease),
      visibility 0.22s var(--ease),
      transform 0.22s var(--ease);
  }

  .nav.is-open {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
  }

  .nav__link {
    padding: 12px 14px;
    font-size: 0.9rem;
    border-radius: 10px;
  }

  .nav__link::after {
    display: none;
  }

  .nav__link:hover {
    background: var(--violet-soft);
  }
}

@media (max-width: 480px) {
  .brand__mark {
    width: 30px;
    height: 30px;
  }

  .brand__mark svg {
    width: 17px;
    height: 17px;
  }

  .brand__name {
    font-size: 0.78rem;
  }
}
</style>
