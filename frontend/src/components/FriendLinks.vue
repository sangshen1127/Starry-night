<script setup>
import { computed, reactive } from 'vue'
import EmptyState from '@/components/EmptyState.vue'
import { getInitial, resolveAssetUrl, safeHttpUrl } from '@/utils/url'

const props = defineProps({
  links: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
})

const failedAvatars = reactive({})

const friends = computed(() =>
  props.links
    .map((link) => ({
      ...link,
      href: safeHttpUrl(link.url),
      avatar: resolveAssetUrl(link.avatarUrl),
    }))
    .filter((link) => Boolean(link.href)),
)
</script>

<template>
  <section id="friends" class="side-card friends-card">
    <div class="side-head">
      <div>
        <p class="side-kicker">FRIENDS · ともだち</p>
        <h2 class="side-title">友链</h2>
      </div>
      <span v-if="friends.length" class="side-count">{{ friends.length }}</span>
    </div>

    <div v-if="loading" class="friends-loading" aria-busy="true" aria-live="polite">
      <span class="sr-only">正在加载友链…</span>
      <span v-for="index in 4" :key="index" class="skeleton friends-loading__item" />
    </div>

    <p v-else-if="error" class="side-note">友链暂时没有加载出来。</p>

    <EmptyState
      v-else-if="!friends.length"
      compact
      title="友链还在布置中"
      message="等几位邻居搬进来。"
    />

    <ul v-else class="friend-grid">
      <li v-for="friend in friends" :key="friend.id">
        <a
          class="friend"
          :href="friend.href"
          target="_blank"
          rel="noopener noreferrer"
        >
          <span class="friend__avatar" aria-hidden="true">
            <span class="friend__initial">{{ getInitial(friend.name) }}</span>
            <img
              v-if="friend.avatar && !failedAvatars[friend.id]"
              :src="friend.avatar"
              alt=""
              loading="lazy"
              @error="failedAvatars[friend.id] = true"
            />
          </span>
          <span class="friend__copy">
            <span class="friend__name">{{ friend.name }}</span>
            <span v-if="friend.description" class="friend__desc">
              {{ friend.description }}
            </span>
          </span>
          <span class="friend__arrow" aria-hidden="true">↗</span>
        </a>
      </li>
    </ul>

    <RouterLink v-if="friends.length" class="friends-more" :to="{ name: 'friends' }">
      全部友链
      <span aria-hidden="true">→</span>
    </RouterLink>
  </section>
</template>

<style scoped>
.friends-loading {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.friends-loading__item {
  height: 50px;
  border-radius: 10px;
}

.friend-grid {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.friend {
  display: flex;
  align-items: center;
  gap: 9px;
  min-width: 0;
  padding: 7px 8px;
  border-radius: 12px;
  transition:
    background 0.2s var(--ease),
    transform 0.2s var(--ease);
}

.friend:hover {
  background: rgba(255, 158, 181, 0.07);
  transform: translateX(2px);
}

.friend__avatar {
  position: relative;
  display: grid;
  place-items: center;
  flex: none;
  width: 34px;
  height: 34px;
  overflow: hidden;
  color: #25102b;
  font-family: var(--font-mono);
  font-size: 0.66rem;
  font-weight: 700;
  background: linear-gradient(135deg, rgba(255, 158, 181, 0.88), rgba(126, 200, 227, 0.75));
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 50%;
  box-shadow: 0 0 16px rgba(185, 103, 255, 0.12);
}

.friend__initial {
  position: relative;
  z-index: 0;
}

.friend__avatar img {
  position: absolute;
  inset: 0;
  z-index: 1;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.friend__copy {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  gap: 2px;
}

.friend__name {
  overflow: hidden;
  color: var(--ink-2);
  font-size: 0.72rem;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend__desc {
  overflow: hidden;
  color: var(--faint);
  font-size: 0.55rem;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend__arrow {
  color: var(--faint);
  font-family: var(--font-mono);
  font-size: 0.7rem;
  transition:
    color 0.2s var(--ease),
    transform 0.2s var(--ease);
}

.friend:hover .friend__arrow {
  color: var(--pink);
  transform: translate(2px, -2px);
}

.friends-more {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  margin-top: 12px;
  padding-top: 11px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.6rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  transition: color 0.2s var(--ease);
}

.friends-more:hover {
  color: var(--cyan);
}
</style>
