<script setup lang="ts">
import { computed, ref } from 'vue'
import type { FriendLink } from '@/api/types'
import { getInitial, resolveAssetUrl, safeHttpUrl } from '@/utils/url'

const props = defineProps<{
  friend: FriendLink
}>()

const failed = ref(false)
const href = computed(() => safeHttpUrl(props.friend.url))
const avatar = computed(() => resolveAssetUrl(props.friend.avatarUrl))
const initial = computed(() => getInitial(props.friend.name))
</script>

<template>
  <a
    class="friend-card"
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
  </a>
</template>

<style scoped>
.friend-card {
  display: flex;
  align-items: center;
  gap: 14px;
  min-width: 0;
  padding: 18px;
  background: var(--surface);
  border: 1px solid var(--line);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  transition:
    transform 0.3s var(--ease),
    box-shadow 0.3s var(--ease),
    border-color 0.3s var(--ease);
}

.friend-card:hover {
  border-color: var(--line-strong);
  box-shadow: var(--shadow-md);
  transform: translateY(-3px);
}

.friend-card__avatar {
  position: relative;
  display: grid;
  place-items: center;
  flex: none;
  width: 48px;
  height: 48px;
  overflow: hidden;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.9rem;
  font-weight: 700;
  background: var(--violet-soft);
  border: 1px solid var(--line);
  border-radius: 50%;
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
  font-size: 0.88rem;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-card__desc {
  overflow: hidden;
  color: var(--muted);
  font-size: 0.72rem;
  line-height: 1.6;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend-card__arrow {
  flex: none;
  color: var(--muted);
  font-family: var(--font-mono);
  font-size: 0.8rem;
  transition: color 0.2s var(--ease), transform 0.2s var(--ease);
}

.friend-card:hover .friend-card__arrow {
  color: var(--violet);
  transform: translate(2px, -2px);
}

@media (max-width: 560px) {
  .friend-card {
    padding: 15px;
  }
}
</style>
