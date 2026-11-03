<script setup>
import { computed, ref } from 'vue'
import { siteConfig } from '@/config/site'
import { resolveAssetUrl } from '@/utils/url'

const avatarFailed = ref(false)
const avatar = computed(() => resolveAssetUrl(siteConfig.avatarImage))
</script>

<template>
  <section id="about" class="side-card about">
    <div class="about__texture" aria-hidden="true" />
    <div class="side-head">
      <div>
        <p class="side-kicker">ABOUT · 关于</p>
        <h2 class="side-title">这座小屋</h2>
      </div>
      <span class="side-count">PROFILE</span>
    </div>

    <div class="about__profile">
      <span class="about__orb" aria-hidden="true">
        <img
          v-if="avatar && !avatarFailed"
          :src="avatar"
          alt=""
          loading="lazy"
          @error="avatarFailed = true"
        />
        <svg v-else viewBox="0 0 32 32">
          <path
            d="M20.8 5.3A11.2 11.2 0 1 0 26.7 21 9 9 0 0 1 20.8 5.3Z"
            fill="currentColor"
          />
          <path
            d="M24.6 11.3l.8 2 2 .8-2 .8-.8 2-.8-2-2-.8 2-.8z"
            fill="var(--pink)"
          />
        </svg>
      </span>

      <div class="about__who">
        <p class="about__name">{{ siteConfig.name }}</p>
        <p class="about__role">A PERSONAL BLOG · 夜の記録</p>
      </div>
    </div>

    <p class="about__text">{{ siteConfig.about }}</p>
    <p class="about__sign">— {{ siteConfig.name }}</p>

    <div class="about__chips">
      <span>CODE</span>
      <span>ANIME</span>
      <span>NIGHT</span>
    </div>

    <div v-if="siteConfig.socialLinks.length" class="about__social">
      <a
        v-for="link in siteConfig.socialLinks"
        :key="link.label"
        :href="link.href"
        target="_blank"
        rel="noopener noreferrer"
      >
        {{ link.label }}
      </a>
    </div>
    <p v-else class="about__since">
      START NIGHT / SINCE {{ siteConfig.startYear }}
    </p>
  </section>
</template>

<style scoped>
.about {
  position: relative;
  overflow: hidden;
}

.about__texture {
  position: absolute;
  inset: 0;
  background-image: radial-gradient(rgba(255, 158, 181, 0.22) 0.6px, transparent 0.6px);
  background-size: 13px 13px;
  mask-image: linear-gradient(120deg, rgba(0, 0, 0, 0.45), transparent 57%);
  opacity: 0.22;
  pointer-events: none;
}

.about__profile {
  position: relative;
  display: flex;
  align-items: center;
  gap: 14px;
  margin-bottom: 16px;
}

.about__orb {
  position: relative;
  display: grid;
  place-items: center;
  flex: none;
  width: 58px;
  height: 58px;
  overflow: hidden;
  color: var(--pink);
  background:
    radial-gradient(circle at 34% 28%, rgba(255, 255, 255, 0.22), transparent 46%),
    rgba(185, 103, 255, 0.15);
  border: 1px solid rgba(255, 158, 181, 0.24);
  border-radius: 50%;
  box-shadow:
    0 0 0 5px rgba(185, 103, 255, 0.06),
    0 0 28px rgba(185, 103, 255, 0.24);
}

.about__orb::before {
  position: absolute;
  inset: -6px;
  border: 1px dashed rgba(255, 158, 181, 0.55);
  border-radius: 50%;
  content: '';
  animation: about-spin 16s linear infinite;
}

.about__orb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  object-position: center 20%;
}

.about__orb svg {
  width: 30px;
  height: 30px;
}

.about__who {
  min-width: 0;
}

.about__name {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: 1.15rem;
  letter-spacing: 0.04em;
}

.about__role {
  margin-top: 3px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.48rem;
  letter-spacing: 0.08em;
}

.about__text {
  position: relative;
  color: var(--muted);
  font-size: 0.76rem;
  line-height: 1.9;
}

.about__sign {
  margin-top: 10px;
  color: var(--faint);
  font-family: var(--font-display);
  font-size: 0.66rem;
  letter-spacing: 0.06em;
  text-align: right;
}

.about__chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-top: 14px;
  padding-top: 13px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.about__chips span {
  padding: 3px 8px;
  color: var(--cyan);
  font-family: var(--font-mono);
  font-size: 0.45rem;
  letter-spacing: 0.1em;
  background: rgba(126, 200, 227, 0.08);
  border: 1px solid rgba(126, 200, 227, 0.14);
  border-radius: 999px;
}

.about__social {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 12px;
}

.about__social a {
  color: var(--pink);
  font-size: 0.68rem;
}

.about__since {
  margin-top: 13px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.5rem;
  letter-spacing: 0.1em;
}

@keyframes about-spin {
  to {
    transform: rotate(360deg);
  }
}
</style>
