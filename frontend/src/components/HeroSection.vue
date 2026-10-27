<script setup lang="ts">
import { computed } from 'vue'
import CharacterArt from '@/components/CharacterArt.vue'
import StarField from '@/components/StarField.vue'
import { siteConfig } from '@/config/site'

const props = withDefaults(
  defineProps<{
    lastUpdated?: string
    loading?: boolean
    error?: string
  }>(),
  {
    lastUpdated: '',
    loading: false,
    error: '',
  },
)

const statusText = computed(() => {
  if (props.loading) return 'CONNECTING TO THE NIGHT…'
  if (props.error) return 'NIGHT SKY OFFLINE'
  if (props.lastUpdated) return `LATEST UPDATE · ${props.lastUpdated}`
  return 'WAITING FOR THE FIRST NOTE'
})
</script>

<template>
  <section class="hero" aria-labelledby="hero-title">
    <div class="container">
      <div class="hero__card">
        <StarField />
        <div class="hero__grid">
          <div class="hero__copy">
            <p class="hero__kicker">
              <span class="hero__dot" aria-hidden="true" />
              {{ siteConfig.hero.kicker }}
            </p>

            <h1 id="hero-title" class="hero__title">
              {{ siteConfig.hero.titleTop }}<br />
              <em>{{ siteConfig.hero.titleBottom }}</em>
            </h1>

            <p class="hero__lead">{{ siteConfig.hero.lead }}</p>

            <div class="hero__actions">
              <a class="btn btn--light" href="#posts">
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

            <p class="hero__meta">
              <span class="hero__meta-star" aria-hidden="true">✦</span>
              {{ statusText }}
            </p>
          </div>

          <div class="hero__art">
            <CharacterArt
              :src="siteConfig.characterImage"
              :alt="siteConfig.characterAlt"
            />
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.hero {
  padding-top: 26px;
}

.hero__card {
  position: relative;
  overflow: hidden;
  background: linear-gradient(135deg, var(--night-0) 0%, var(--night-1) 52%, var(--night-2) 100%);
  border: 1px solid rgba(255, 255, 255, 0.09);
  border-radius: 32px;
  box-shadow: var(--shadow-night);
}

.hero__card::before {
  position: absolute;
  inset: 0;
  z-index: 0;
  background:
    radial-gradient(620px 420px at 84% 12%, rgba(100, 84, 200, 0.26), transparent 68%),
    radial-gradient(480px 360px at 48% 110%, rgba(198, 150, 61, 0.08), transparent 70%);
  content: '';
  pointer-events: none;
}

.hero__grid {
  position: relative;
  z-index: 1;
  display: grid;
  grid-template-columns: minmax(0, 1.22fr) minmax(300px, 0.78fr);
  gap: 48px;
  align-items: stretch;
  min-height: 600px;
  padding: 54px 54px 54px 56px;
}

.hero__copy {
  align-self: center;
  max-width: 600px;
  animation: rise-in 0.8s var(--ease) both;
}

.hero__kicker {
  display: flex;
  align-items: center;
  gap: 9px;
  margin-bottom: 20px;
  color: var(--star);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.19em;
}

.hero__dot {
  width: 5px;
  height: 5px;
  background: var(--star);
  border-radius: 50%;
  box-shadow: 0 0 10px rgba(244, 213, 141, 0.8);
}

.hero__title {
  margin-bottom: 22px;
  color: var(--night-text);
  font-size: clamp(2.7rem, 5.2vw, 4.4rem);
  font-weight: 800;
  letter-spacing: -0.04em;
  line-height: 1.14;
}

.hero__title em {
  color: var(--star);
  font-style: normal;
}

.hero__lead {
  max-width: 31em;
  color: var(--night-muted);
  font-size: 0.94rem;
  line-height: 1.95;
}

.hero__actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-top: 30px;
}

.hero__actions svg {
  width: 15px;
  height: 15px;
}

.hero__meta {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 27px;
  color: rgba(184, 180, 201, 0.8);
  font-family: var(--font-mono);
  font-size: 0.6rem;
  letter-spacing: 0.1em;
}

.hero__meta-star {
  color: var(--gold);
  font-size: 0.66rem;
}

.hero__art {
  min-height: 480px;
  animation: rise-in 0.95s 0.12s var(--ease) both;
}

@media (max-width: 1100px) {
  .hero__grid {
    grid-template-columns: minmax(0, 1.1fr) minmax(280px, 0.9fr);
    gap: 30px;
    padding: 44px;
  }

  .hero__title {
    font-size: clamp(2.7rem, 5.4vw, 3.9rem);
  }
}

@media (max-width: 1000px) {
  .hero__grid {
    grid-template-columns: minmax(0, 1fr);
    min-height: 0;
    padding: 42px 38px 38px;
  }

  .hero__copy {
    max-width: none;
  }

  .hero__art {
    width: min(100%, 420px);
    min-height: 0;
    aspect-ratio: 4 / 5;
    margin: 0 auto;
  }
}

@media (max-width: 640px) {
  .hero {
    padding-top: 18px;
  }

  .hero__card {
    border-radius: 24px;
  }

  .hero__grid {
    gap: 28px;
    padding: 32px 22px 22px;
  }

  .hero__kicker {
    margin-bottom: 16px;
    font-size: 0.57rem;
    letter-spacing: 0.14em;
  }

  .hero__title {
    margin-bottom: 18px;
    font-size: clamp(2.15rem, 10vw, 2.7rem);
    line-height: 1.18;
  }

  .hero__lead {
    font-size: 0.88rem;
    line-height: 1.9;
  }

  .hero__actions {
    margin-top: 24px;
  }

  .hero__meta {
    margin-top: 22px;
    font-size: 0.56rem;
  }

  .hero__art {
    width: min(100%, 330px);
  }
}

@media (max-width: 420px) {
  .hero__grid {
    padding: 28px 18px 18px;
  }

  .hero__actions .btn {
    padding-right: 17px;
    padding-left: 17px;
  }
}
</style>
