<script setup lang="ts">
import { computed, ref } from 'vue'
import { siteConfig } from '@/config/site'
import { resolveAssetUrl } from '@/utils/url'

const avatarFailed = ref(false)
const avatar = computed(() => resolveAssetUrl(siteConfig.avatarImage))
</script>

<template>
  <section id="about" class="side-card about">
    <div class="side-head">
      <div>
        <p class="side-kicker">ABOUT · 关于</p>
        <h2 class="side-title">这座小屋</h2>
      </div>

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
            fill="var(--star)"
          />
        </svg>
      </span>
    </div>

    <p class="about__text">{{ siteConfig.about }}</p>
    <p class="about__sign">— {{ siteConfig.name }}</p>

    <ul v-if="siteConfig.socialLinks.length" class="about__social">
      <li v-for="link in siteConfig.socialLinks" :key="link.label">
        <a :href="link.href" target="_blank" rel="noopener noreferrer">
          {{ link.label }}
        </a>
      </li>
    </ul>
    <p v-else class="about__since">{{ siteConfig.brand }} / SINCE {{ siteConfig.startYear }}</p>
  </section>
</template>

<style scoped>
.about__orb {
  display: grid;
  place-items: center;
  flex: none;
  width: 46px;
  height: 46px;
  overflow: hidden;
  color: var(--moon);
  background: var(--night-1);
  border: 1px solid var(--line);
  border-radius: 50%;
}

.about__orb img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.about__orb svg {
  width: 23px;
  height: 23px;
}

.about__text {
  color: var(--ink-2);
  font-size: 0.83rem;
  line-height: 1.9;
}

.about__sign {
  margin-top: 10px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.05em;
}

.about__social {
  display: flex;
  flex-wrap: wrap;
  gap: 8px 16px;
  margin-top: 16px;
  padding-top: 14px;
  border-top: 1px solid var(--line);
}

.about__social a {
  color: var(--ink-2);
  font-size: 0.74rem;
  transition: color 0.2s var(--ease);
}

.about__social a:hover {
  color: var(--violet);
}

.about__since {
  margin-top: 16px;
  padding-top: 14px;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.56rem;
  letter-spacing: 0.1em;
  border-top: 1px solid var(--line);
}
</style>
