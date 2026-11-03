<script setup>
import { onBeforeUnmount, onMounted } from 'vue'

const stars = Array.from({ length: 96 }, (_, index) => ({
  left: `${(index * 41 + 7) % 100}%`,
  top: `${(index * 57 + 11) % 100}%`,
  size: `${index % 11 === 0 ? 3 : index % 5 === 0 ? 2 : 1.2}px`,
  opacity: 0.28 + (index % 5) * 0.12,
  delay: `${(index % 9) * 0.7}s`,
  duration: `${5 + (index % 7)}s`,
}))

const petals = Array.from({ length: 24 }, (_, index) => ({
  left: `${(index * 29 + 8) % 100}%`,
  top: `${-8 - (index % 7) * 5}%`,
  size: `${6 + (index % 4) * 2}px`,
  delay: `${(index % 8) * 1.3}s`,
  duration: `${15 + (index % 6) * 4}s`,
  drift: `${(index % 2 ? 1 : -1) * (20 + (index % 5) * 14)}px`,
}))

const dust = Array.from({ length: 12 }, (_, index) => ({
  left: `${(index * 47 + 18) % 100}%`,
  top: `${(index * 31 + 22) % 100}%`,
  delay: `${(index % 5) * 0.9}s`,
}))

let raf = 0
let current = null
let pointerX = 0
let pointerY = 0

const GLOW_TARGETS =
  '.glass-card, .glass-panel, .side-card, .post-card, .featured, .comment-form, .login-card, .search-form'

function updateSpotlight() {
  raf = 0
  if (!current) return
  const rect = current.getBoundingClientRect()
  current.style.setProperty('--spot-x', `${pointerX - rect.left}px`)
  current.style.setProperty('--spot-y', `${pointerY - rect.top}px`)
}

function onPointerMove(event) {
  const target = event.target?.closest?.(GLOW_TARGETS)
  if (!target) return
  current = target
  pointerX = event.clientX
  pointerY = event.clientY
  if (!raf) raf = requestAnimationFrame(updateSpotlight)
}

onMounted(() => {
  if (window.matchMedia('(pointer: coarse)').matches) return
  if (window.matchMedia('(prefers-reduced-motion: reduce)').matches) return
  document.addEventListener('pointermove', onPointerMove, { passive: true })
})

onBeforeUnmount(() => {
  document.removeEventListener('pointermove', onPointerMove)
  if (raf) cancelAnimationFrame(raf)
})
</script>

<template>
  <div class="ambient" aria-hidden="true">
    <div class="ambient__base" />
    <div class="ambient__aurora ambient__aurora--pink" />
    <div class="ambient__aurora ambient__aurora--purple" />
    <div class="ambient__aurora ambient__aurora--cyan" />
    <div class="ambient__aurora ambient__aurora--rose" />
    <div class="ambient__aurora ambient__aurora--teal" />

    <div class="ambient__moon">
      <span class="ambient__moon-core" />
      <span class="ambient__moon-halo" />
    </div>

    <div class="ambient__stars">
      <span
        v-for="(star, index) in stars"
        :key="`star-${index}`"
        class="ambient__star"
        :style="{
          left: star.left,
          top: star.top,
          width: star.size,
          height: star.size,
          opacity: star.opacity,
          animationDelay: star.delay,
          animationDuration: star.duration,
        }"
      />
    </div>

    <div class="ambient__petals">
      <span
        v-for="(petal, index) in petals"
        :key="`petal-${index}`"
        class="ambient__petal"
        :style="{
          left: petal.left,
          top: petal.top,
          width: petal.size,
          height: petal.size,
          animationDelay: petal.delay,
          animationDuration: petal.duration,
          '--drift': petal.drift,
        }"
      />
    </div>

    <div class="ambient__dust">
      <span
        v-for="(item, index) in dust"
        :key="`dust-${index}`"
        class="ambient__dust-orb"
        :style="{ left: item.left, top: item.top, animationDelay: item.delay }"
      />
    </div>

    <div class="ambient__grid" />
    <div class="ambient__vignette" />
  </div>
</template>

<style scoped>
.ambient {
  position: fixed;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
  background: #0a0713;
}

.ambient__base {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(1000px 620px at 50% -180px, rgba(185, 103, 255, 0.3), transparent 65%),
    radial-gradient(860px 560px at 92% 18%, rgba(255, 158, 181, 0.15), transparent 68%),
    radial-gradient(720px 480px at 12% 58%, rgba(126, 200, 227, 0.09), transparent 72%),
    linear-gradient(180deg, #0d0919 0%, #0f0c1b 45%, #0b0814 100%);
}

.ambient__aurora {
  position: absolute;
  border-radius: 50%;
  filter: blur(100px);
  opacity: 0.48;
  mix-blend-mode: screen;
  animation: aurora-drift 20s ease-in-out infinite alternate;
}

.ambient__aurora--pink {
  top: 8%;
  left: -10%;
  width: 52vw;
  height: 38vw;
  background: rgba(255, 158, 181, 0.5);
}

.ambient__aurora--purple {
  top: 32%;
  right: -12%;
  width: 58vw;
  height: 44vw;
  background: rgba(185, 103, 255, 0.42);
  animation-delay: -7s;
}

.ambient__aurora--cyan {
  bottom: -14%;
  left: 20%;
  width: 50vw;
  height: 34vw;
  background: rgba(126, 200, 227, 0.26);
  animation-delay: -13s;
}

.ambient__aurora--rose {
  top: 47%;
  left: 5%;
  width: 50vw;
  height: 30vw;
  background: rgba(255, 158, 181, 0.28);
  animation-delay: -4s;
}

.ambient__aurora--teal {
  right: -6%;
  bottom: -10%;
  width: 46vw;
  height: 30vw;
  background: rgba(126, 200, 227, 0.22);
  animation-delay: -17s;
}

.ambient__moon {
  position: absolute;
  top: 12%;
  right: 8%;
  width: 300px;
  height: 300px;
  opacity: 0.8;
}

.ambient__moon-core {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 124px;
  height: 124px;
  background: linear-gradient(145deg, #fff8ef, #ffc7dc);
  border-radius: 50%;
  box-shadow:
    0 0 45px rgba(255, 214, 231, 0.55),
    0 0 120px rgba(255, 158, 181, 0.22);
  transform: translate(-50%, -50%);
}

.ambient__moon-halo {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 280px;
  height: 280px;
  border: 1px solid rgba(255, 214, 231, 0.18);
  border-radius: 50%;
  transform: translate(-50%, -50%);
  box-shadow:
    0 0 0 22px rgba(255, 214, 231, 0.04),
    0 0 0 52px rgba(185, 103, 255, 0.035);
}

.ambient__stars,
.ambient__petals,
.ambient__dust {
  position: absolute;
  inset: 0;
}

.ambient__star {
  position: absolute;
  display: block;
  background: #fff;
  border-radius: 50%;
  box-shadow:
    0 0 7px rgba(255, 255, 255, 0.72),
    0 0 16px rgba(255, 158, 181, 0.18);
  animation: twinkle 6s ease-in-out infinite;
}

.ambient__petal {
  position: absolute;
  display: block;
  background: linear-gradient(135deg, #ffd3e1, #ff9eb5);
  border-radius: 80% 0 65% 0;
  box-shadow: 0 0 12px rgba(255, 158, 181, 0.3);
  opacity: 0.52;
  animation: petal-fall 20s linear infinite;
}

.ambient__dust-orb {
  position: absolute;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, rgba(255, 158, 181, 0.16), transparent 68%);
  border-radius: 50%;
  filter: blur(3px);
  animation: glow-pulse 8s ease-in-out infinite;
}

.ambient__grid {
  position: absolute;
  inset: 0;
  background-image:
    radial-gradient(rgba(255, 255, 255, 0.16) 0.6px, transparent 0.6px),
    linear-gradient(rgba(255, 255, 255, 0.028) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 255, 255, 0.028) 1px, transparent 1px);
  background-size:
    38px 38px,
    120px 120px,
    120px 120px;
  mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.55), transparent 82%);
  opacity: 0.36;
}

.ambient__vignette {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at center, transparent 38%, rgba(5, 3, 10, 0.32) 100%),
    linear-gradient(180deg, rgba(6, 4, 12, 0.18), transparent 35%, rgba(6, 4, 12, 0.44));
}

@keyframes twinkle {
  0%,
  100% {
    opacity: 0.22;
    transform: scale(0.8);
  }
  50% {
    opacity: 0.95;
    transform: scale(1.15);
  }
}

@keyframes petal-fall {
  0% {
    opacity: 0;
    transform: translate3d(0, -10vh, 0) rotate(0deg);
  }
  12% {
    opacity: 0.5;
  }
  100% {
    opacity: 0;
    transform: translate3d(var(--drift, 40px), 112vh, 0) rotate(520deg);
  }
}

@keyframes aurora-drift {
  from {
    transform: translate3d(-3%, -2%, 0) scale(0.95);
  }
  to {
    transform: translate3d(4%, 3%, 0) scale(1.08);
  }
}

@media (max-width: 760px) {
  .ambient__moon {
    top: 9%;
    right: -34px;
    width: 220px;
    height: 220px;
  }

  .ambient__moon-core {
    width: 86px;
    height: 86px;
  }

  .ambient__moon-halo {
    width: 200px;
    height: 200px;
  }

  .ambient__petal:nth-child(n + 10) {
    display: none;
  }

  .ambient__aurora {
    opacity: 0.28;
  }
}

@media (prefers-reduced-motion: reduce) {
  .ambient__star,
  .ambient__petal,
  .ambient__dust-orb,
  .ambient__aurora {
    animation: none;
  }

  .ambient__petal {
    display: none;
  }
}
</style>
