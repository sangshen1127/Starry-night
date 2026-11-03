<script setup>
import { computed } from 'vue'
import CharacterArt from '@/components/CharacterArt.vue'
import { siteConfig } from '@/config/site'

const props = defineProps({
  lastUpdated: { type: String, default: '' },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
})

const statusText = computed(() => {
  if (props.loading) return 'CONNECTING TO THE NIGHT…'
  if (props.error) return 'NIGHT SKY OFFLINE'
  if (props.lastUpdated) return `LATEST UPDATE · ${props.lastUpdated}`
  return 'WAITING FOR THE FIRST NOTE'
})

const kickerParts = computed(() => siteConfig.hero.kicker.split(' · '))
</script>

<template>
  <section class="hero" aria-labelledby="hero-title">
    <div class="hero__orb hero__orb--pink" aria-hidden="true" />
    <div class="hero__orb hero__orb--cyan" aria-hidden="true" />
    <div class="hero__orb hero__orb--violet" aria-hidden="true" />

    <div class="container hero__inner">
      <div class="hero__copy glass-panel">
        <p class="hero__kicker">
          <span class="hero__dot" aria-hidden="true">✦</span>
          <template v-for="(part, index) in kickerParts" :key="part">
            <span class="hero__kicker-part">{{ part }}</span>
            <span
              v-if="index < kickerParts.length - 1"
              class="hero__kicker-sep"
              aria-hidden="true"
            >
              |
            </span>
          </template>
          <span class="hero__kicker-sep" aria-hidden="true">|</span>
          <span class="hero__jp" aria-hidden="true">きらめく</span>
        </p>

        <h1 id="hero-title" class="hero__title">
          {{ siteConfig.hero.titleTop }}<br />
          <em>{{ siteConfig.hero.titleBottom }}</em>
          <span class="hero__title-star" aria-hidden="true">✦</span>
        </h1>

        <p class="hero__lead">{{ siteConfig.hero.lead }}</p>

        <div class="hero__actions">
          <a class="btn btn--primary" href="#posts">
            {{ siteConfig.hero.primaryCta }}
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path
                d="M5 12h13M13 6l6 6-6 6"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </a>
          <a class="btn btn--ghost" href="#about">
            {{ siteConfig.hero.secondaryCta }}
          </a>
        </div>

        <div class="hero__meta">
          <span class="hero__status">
            <span class="hero__status-dot" aria-hidden="true" />
            {{ statusText }}
          </span>
          <span class="hero__meta-divider" aria-hidden="true">·</span>
          <span class="hero__meta-jp">夜の記録</span>
        </div>
      </div>

      <div class="hero__visual glass-panel">
        <div class="hero__visual-glow" aria-hidden="true" />
        <CharacterArt
          :src="siteConfig.characterImage"
          :alt="siteConfig.characterAlt"
        />
        <span class="hero__vertical" aria-hidden="true">夜がはじまる</span>
        <span class="hero__spark hero__spark--one" aria-hidden="true">✦</span>
        <span class="hero__spark hero__spark--two" aria-hidden="true">♡</span>
        <span class="hero__character-tag">ANIME PORTRAIT</span>
      </div>
    </div>

    <a class="hero__scroll" href="#posts" aria-label="向下浏览最新文章">
      <span class="hero__scroll-mouse" aria-hidden="true">
        <span class="hero__scroll-wheel" />
      </span>
      <span class="hero__scroll-text">SCROLL TO EXPLORE</span>
      <span class="hero__scroll-arrow" aria-hidden="true">⌄</span>
    </a>
  </section>
</template>

<style scoped>
.hero {
  position: relative;
  display: flex;
  align-items: center;
  min-height: calc(100vh - var(--header-h));
  padding: 34px 0 42px;
  overflow: hidden;
}

.hero__orb {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
  filter: blur(34px);
  opacity: 0.55;
}

.hero__orb--pink {
  top: 12%;
  left: -5%;
  width: 240px;
  height: 240px;
  background: rgba(255, 158, 181, 0.2);
  animation: float-soft 9s ease-in-out infinite;
}

.hero__orb--cyan {
  right: -4%;
  bottom: 10%;
  width: 220px;
  height: 220px;
  background: rgba(126, 200, 227, 0.2);
  animation: float-soft 7s ease-in-out infinite reverse;
}

.hero__orb--violet {
  top: 48%;
  right: 24%;
  width: 280px;
  height: 280px;
  background: rgba(185, 103, 255, 0.17);
  animation: float-soft 10s ease-in-out infinite;
}

.hero__inner {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.04fr) minmax(340px, 0.96fr);
  gap: 28px;
  align-items: stretch;
}

.hero__copy {
  display: flex;
  flex-direction: column;
  justify-content: center;
  min-height: 590px;
  padding: 50px 52px;
  overflow: hidden;
  background:
    linear-gradient(135deg, rgba(255, 158, 181, 0.1), transparent 42%),
    linear-gradient(315deg, rgba(126, 200, 227, 0.045), transparent 44%),
    rgba(255, 255, 255, 0.038);
  border-radius: 28px;
  animation: rise-in 0.8s var(--ease) both;
  transition:
    border-color 0.35s var(--ease),
    box-shadow 0.35s var(--ease),
    background 0.35s var(--ease),
    backdrop-filter 0.35s var(--ease);
}

.hero__copy:hover {
  background:
    linear-gradient(135deg, rgba(255, 158, 181, 0.13), transparent 42%),
    linear-gradient(315deg, rgba(126, 200, 227, 0.06), transparent 44%),
    rgba(255, 255, 255, 0.052);
  border-color: rgba(255, 158, 181, 0.3);
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.18),
    inset 0 1px 0 rgba(255, 255, 255, 0.24),
    0 26px 80px rgba(0, 0, 0, 0.34),
    0 0 52px rgba(185, 103, 255, 0.14);
  -webkit-backdrop-filter: blur(23px) saturate(165%);
  backdrop-filter: blur(23px) saturate(165%);
}

.hero__copy::after {
  position: absolute;
  right: -90px;
  bottom: -110px;
  width: 280px;
  height: 280px;
  border: 1px solid rgba(255, 158, 181, 0.12);
  border-radius: 50%;
  box-shadow:
    0 0 0 28px rgba(185, 103, 255, 0.035),
    0 0 0 60px rgba(255, 158, 181, 0.025);
  content: '';
  pointer-events: none;
}

.hero__kicker {
  display: inline-flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  width: fit-content;
  margin-bottom: 24px;
  padding: 8px 14px;
  color: var(--ink-2);
  font-family: var(--font-mono);
  font-size: 0.56rem;
  letter-spacing: 0.12em;
  background: rgba(255, 255, 255, 0.065);
  border: 1px solid rgba(255, 255, 255, 0.13);
  border-radius: 999px;
  box-shadow:
    inset 0 0 0 1px rgba(255, 255, 255, 0.06),
    inset 0 1px 0 rgba(255, 255, 255, 0.12),
    0 8px 30px rgba(0, 0, 0, 0.18);
  -webkit-backdrop-filter: blur(14px) saturate(145%);
  backdrop-filter: blur(14px) saturate(145%);
  text-shadow: 0 0 14px rgba(255, 158, 181, 0.18);
}

.hero__dot {
  color: var(--pink);
  font-size: 0.7rem;
  text-shadow: 0 0 16px rgba(255, 158, 181, 0.65);
}

.hero__kicker-part {
  color: var(--ink-2);
}

.hero__kicker-sep {
  color: rgba(255, 158, 181, 0.34);
  font-size: 0.72rem;
}

.hero__jp {
  padding-left: 1px;
  color: var(--pink);
  font-family: var(--font-display);
  text-shadow: 0 0 14px rgba(255, 158, 181, 0.3);
}

.hero__title {
  max-width: 660px;
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(2.8rem, 5vw, 3.65rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.26;
  text-shadow:
    0 0 26px rgba(255, 158, 181, 0.22),
    0 8px 34px rgba(0, 0, 0, 0.24);
}

.hero__title em {
  display: inline-block;
  color: transparent;
  font-style: normal;
  background: linear-gradient(
    100deg,
    #ffe1ed 0%,
    #ff9eb5 28%,
    #d7a7ff 54%,
    #9fe1f2 78%,
    #7ec8e3 100%
  );
  -webkit-background-clip: text;
  background-clip: text;
  filter:
    drop-shadow(0 0 20px rgba(255, 158, 181, 0.24))
    drop-shadow(0 0 34px rgba(126, 200, 227, 0.12));
}

.hero__title-star {
  display: inline-block;
  margin-left: 8px;
  color: var(--cyan);
  font-family: var(--font-sans);
  font-size: 0.7em;
  text-shadow: 0 0 24px rgba(126, 200, 227, 0.55);
  animation: twinkle 3.2s ease-in-out infinite;
}

.hero__lead {
  max-width: 35em;
  margin-top: 20px;
  color: var(--muted);
  font-size: 0.9rem;
  line-height: 1.95;
}

.hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 28px;
}

.hero__actions .btn {
  min-height: 41px;
  padding-inline: 19px;
  font-size: 0.76rem;
}

.hero__meta {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 8px;
  margin-top: 34px;
  color: rgba(255, 255, 255, 0.52);
  font-family: var(--font-mono);
  font-size: 0.58rem;
  letter-spacing: 0.07em;
  text-shadow: 0 0 14px rgba(255, 158, 181, 0.16);
}

.hero__status {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.hero__status-dot {
  width: 6px;
  height: 6px;
  background: var(--pink);
  border-radius: 50%;
  box-shadow: 0 0 12px rgba(255, 158, 181, 0.9);
  animation: status-pulse 2.4s ease-in-out infinite;
}

.hero__meta-divider {
  color: rgba(255, 255, 255, 0.2);
}

.hero__meta-jp {
  color: var(--pink);
  font-family: var(--font-display);
  font-size: 0.66rem;
  letter-spacing: 0.08em;
  text-shadow: 0 0 16px rgba(255, 158, 181, 0.35);
}

.hero__visual {
  position: relative;
  display: flex;
  min-height: 590px;
  padding: 14px;
  overflow: hidden;
  background:
    radial-gradient(430px 360px at 55% 26%, rgba(255, 158, 181, 0.19), transparent 64%),
    radial-gradient(340px 300px at 70% 80%, rgba(126, 200, 227, 0.08), transparent 70%),
    linear-gradient(160deg, rgba(255, 255, 255, 0.055), rgba(185, 103, 255, 0.035));
  border-radius: 28px;
  animation: rise-in 0.9s 0.1s var(--ease) both;
}

.hero__visual-glow {
  position: absolute;
  top: 16%;
  left: 10%;
  width: 78%;
  height: 68%;
  background:
    radial-gradient(ellipse at 48% 38%, rgba(255, 158, 181, 0.27), transparent 55%),
    radial-gradient(ellipse at 65% 72%, rgba(185, 103, 255, 0.19), transparent 65%);
  filter: blur(34px);
  opacity: 0.9;
  pointer-events: none;
}

.hero__visual :deep(.character-art) {
  flex: 1;
}

.hero__vertical {
  position: absolute;
  top: 26px;
  right: 24px;
  color: rgba(255, 255, 255, 0.56);
  font-family: var(--font-display);
  font-size: 0.62rem;
  letter-spacing: 0.16em;
  line-height: 1.6;
  text-shadow:
    0 0 12px rgba(255, 158, 181, 0.3),
    0 0 24px rgba(0, 0, 0, 0.4);
  writing-mode: vertical-rl;
}

.hero__spark {
  position: absolute;
  color: var(--pink);
  font-size: 1.1rem;
  text-shadow: 0 0 20px rgba(255, 158, 181, 0.7);
  animation: float-soft 4.8s ease-in-out infinite;
}

.hero__spark--one {
  top: 22%;
  left: 8%;
}

.hero__spark--two {
  right: 9%;
  bottom: 17%;
  color: var(--cyan);
  animation-delay: -1.5s;
}

.hero__character-tag {
  position: absolute;
  right: 26px;
  bottom: 24px;
  color: rgba(255, 255, 255, 0.48);
  font-family: var(--font-mono);
  font-size: 0.5rem;
  letter-spacing: 0.16em;
  text-shadow: 0 0 10px rgba(255, 158, 181, 0.2);
}

.hero__scroll {
  position: absolute;
  bottom: 18px;
  left: 50%;
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.5rem;
  letter-spacing: 0.18em;
  transform: translateX(-50%);
  transition: color 0.2s var(--ease);
}

.hero__scroll-mouse {
  position: relative;
  display: block;
  width: 16px;
  height: 24px;
  border: 1px solid rgba(255, 255, 255, 0.34);
  border-radius: 9px;
  box-shadow: 0 0 16px rgba(255, 158, 181, 0.1);
}

.hero__scroll-wheel {
  position: absolute;
  top: 5px;
  left: 50%;
  width: 2px;
  height: 5px;
  background: var(--pink);
  border-radius: 2px;
  box-shadow: 0 0 8px rgba(255, 158, 181, 0.8);
  transform: translateX(-50%);
  animation: wheel-scroll 1.8s ease-in-out infinite;
}

.hero__scroll-text {
  color: rgba(255, 255, 255, 0.4);
}

.hero__scroll-arrow {
  color: var(--pink);
  font-size: 1rem;
  line-height: 0.8;
  text-shadow: 0 0 16px rgba(255, 158, 181, 0.65);
  animation: scroll-bounce 1.9s ease-in-out infinite;
}

.hero__scroll:hover {
  color: var(--pink);
}

.hero__scroll:hover .hero__scroll-text {
  color: var(--pink);
}

@keyframes wheel-scroll {
  0%,
  100% {
    opacity: 0.4;
    transform: translate(-50%, 0);
  }
  45% {
    opacity: 1;
    transform: translate(-50%, 8px);
  }
}

@keyframes scroll-bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(5px);
  }
}

@keyframes status-pulse {
  0%,
  100% {
    opacity: 0.5;
    box-shadow: 0 0 8px rgba(255, 158, 181, 0.5);
  }
  50% {
    opacity: 1;
    box-shadow: 0 0 16px rgba(255, 158, 181, 1);
  }
}

@media (max-width: 1050px) {
  .hero__copy {
    min-height: 530px;
    padding: 38px;
  }

  .hero__visual {
    min-height: 530px;
  }

  .hero__title {
    font-size: clamp(2.6rem, 5.4vw, 3.2rem);
  }
}

@media (max-width: 860px) {
  .hero {
    min-height: auto;
    padding-top: 26px;
  }

  .hero__inner {
    grid-template-columns: minmax(0, 1fr);
    gap: 18px;
  }

  .hero__copy {
    min-height: auto;
    padding: 32px 28px;
  }

  .hero__visual {
    min-height: 0;
    max-width: 430px;
    aspect-ratio: 4 / 4.7;
    margin: 0 auto;
    width: 100%;
  }

  .hero__scroll {
    display: none;
  }
}

@media (max-width: 640px) {
  .hero {
    padding: 18px 0 28px;
  }

  .hero__copy {
    padding: 26px 22px;
    border-radius: 20px;
  }

  .hero__kicker {
    margin-bottom: 18px;
    font-size: 0.5rem;
  }

  .hero__title {
    font-size: clamp(2.25rem, 9vw, 2.8rem);
  }

  .hero__lead {
    margin-top: 14px;
    font-size: 0.82rem;
  }

  .hero__actions {
    margin-top: 22px;
  }

  .hero__actions .btn {
    flex: 1;
    padding-inline: 12px;
  }

  .hero__meta {
    margin-top: 24px;
    font-size: 0.52rem;
  }

  .hero__visual {
    max-width: 340px;
    aspect-ratio: 4 / 4.8;
    padding: 8px;
    border-radius: 20px;
  }

  .hero__vertical {
    top: 16px;
    right: 14px;
    font-size: 0.52rem;
  }

  .hero__character-tag {
    right: 15px;
    bottom: 15px;
    font-size: 0.44rem;
  }
}
</style>
