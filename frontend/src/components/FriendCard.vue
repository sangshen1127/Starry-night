<script setup>
import { computed, ref } from 'vue'
import { getInitial, resolveAssetUrl, safeHttpUrl } from '@/utils/url'

const props = defineProps({
  friend: { type: Object, required: true },
})

const failed = ref(false)
const href = computed(() => safeHttpUrl(props.friend.url))
const avatar = computed(() => resolveAssetUrl(props.friend.avatarUrl))
const initial = computed(() => getInitial(props.friend.name))
</script>

<template>
  <a
    class="friend-card glass-card"
    :href="href"
    target="_blank"
    rel="noopener noreferrer"
  >
    <span class="friend-card__avatar" aria-hidden="true">
      <span class="friend-card__initial">{{ initial }}</span>
      <img
        v-if="avatar && !failed"
        :src="avatar"
        alt=""
        loading="lazy"
        @error="failed = true"
      />
    </span>

    <span class="friend-card__copy">
      <span class="friend-card__name">{{ friend.name }}</span>
      <span v-if="friend.description" class="friend-card__desc">
        {{ friend.description }}
      </span>
    </span>

    <span class="friend-card__arrow" aria-hidden="true">↗</span>
    <span class="friend-card__star" aria-hidden="true">✦</span>
  </a>
</template>

<style scoped>
.friend-card {
  position: relative;
  display: flex;
  align-items: center;
  gap: 13px;
  min-width: 0;
  padding: 16px;
  overflow: hidden;
  border-radius: 18px;
}

.friend-card__avatar {
  position: relative;
  display: grid;
  place-items: center;
  flex: none;
  width: 50px;
  height: 50px;
  overflow: hidden;
  color: #25102b;
  font-family: var(--font-mono);
  font-size: 0.82rem;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(255, 158, 181, 0.9), rgba(126, 200, 227, 0.8));
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 50%;
  box-shadow: 0 0 22px rgba(185, 103, 255, 0.16);
}

.friend-card__initial {
  position: relative;
  z-index: 0;
}

.friend-card__avatar img {
  position: absolute;
  inset: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.friend-card__copy {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  gap: 3px;
}

.friend-card__name {
  overflow: hidden;
  color: var(--ink);
  font-size: 0.84rem;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-card__desc {
  display: -webkit-box;
  overflow: hidden;
  color: var(--muted);
  font-size: 0.68rem;
  line-height: 1.6;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
}

.friend-card__arrow {
  flex: none;
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.8rem;
  transition:
    color 0.2s var(--ease),
    transform 0.2s var(--ease);
}

.friend-card:hover .friend-card__arrow {
  color: var(--pink);
  transform: translate(2px, -2px);
}

.friend-card__star {
  position: absolute;
  top: 10px;
  right: 12px;
  color: rgba(255, 158, 181, 0.35);
  font-size: 0.55rem;
}

@media (max-width: 560px) {
  .friend-card {
    padding: 13px;
  }

  .friend-card__avatar {
    width: 44px;
    height: 44px;
  }
}
</style>
