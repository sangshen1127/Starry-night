<script setup lang="ts">
const stars = Array.from({ length: 32 }, (_, index) => ({
  left: `${(index * 37 + 9) % 100}%`,
  top: `${(index * 53 + 5) % 92}%`,
  width: `${index % 9 === 0 ? 2.4 : 1.2}px`,
  height: `${index % 9 === 0 ? 2.4 : 1.2}px`,
  opacity: 0.3 + (index % 4) * 0.14,
  animationDelay: `${(index % 8) * 0.55}s`,
}))
</script>

<template>
  <div class="starfield" aria-hidden="true">
    <span v-for="(star, index) in stars" :key="index" class="star" :style="star" />
    <span class="meteor meteor--one" />
    <span class="meteor meteor--two" />
  </div>
</template>

<style scoped>
.starfield {
  position: absolute;
  inset: 0;
  z-index: 0;
  overflow: hidden;
  pointer-events: none;
}

.star {
  position: absolute;
  display: block;
  background: #fff;
  border-radius: 50%;
  box-shadow: 0 0 7px rgba(255, 255, 255, 0.55);
  animation: twinkle 4.5s ease-in-out infinite;
}

.meteor {
  position: absolute;
  top: 18%;
  left: 58%;
  width: 110px;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.8), transparent);
  opacity: 0;
  transform: rotate(-30deg);
  animation: meteor-fall 16s linear infinite;
}

.meteor--two {
  top: 64%;
  left: 14%;
  width: 78px;
  animation-delay: 8s;
  animation-duration: 21s;
}

@keyframes twinkle {
  0%,
  100% {
    opacity: 0.25;
    transform: scale(0.85);
  }
  50% {
    opacity: 0.9;
    transform: scale(1);
  }
}

@keyframes meteor-fall {
  0%,
  86% {
    opacity: 0;
    transform: translate3d(0, 0, 0) rotate(-30deg);
  }
  88% {
    opacity: 0.65;
  }
  93%,
  100% {
    opacity: 0;
    transform: translate3d(150px, -86px, 0) rotate(-30deg);
  }
}

@media (prefers-reduced-motion: reduce) {
  .meteor {
    display: none;
  }
}
</style>
