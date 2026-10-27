<script setup lang="ts">
import { computed, reactive } from 'vue'
import EmptyState from '@/components/EmptyState.vue'
import type { FriendLink } from '@/api/types'
import { getInitial, resolveAssetUrl, safeHttpUrl } from '@/utils/url'

const props = withDefaults(
  defineProps<{
    links: FriendLink[]
    loading?: boolean
    error?: string
  }>(),
  {
    loading: false,
    error: '',
  },
)

const failedAvatars = reactive<Record<number, boolean>>({})

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
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 10px;
}

.friends-loading__item {
  height: 54px;
  border-radius: 10px;
}

.friend-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr);
  gap: 4px;
}

.friend {
  display: flex;
  align-items: center;
  gap: 9px;
  min-width: 0;
  padding: 8px;
  border-radius: 12px;
  transition: background 0.2s var(--ease);
}

.friend:hover {
  background: var(--violet-soft);
}

.friend__avatar {
  position: relative;
  display: grid;
  place-items: center;
  flex: none;
  width: 34px;
  height: 34px;
  overflow: hidden;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.72rem;
  font-weight: 700;
  background: var(--violet-soft);
  border: 1px solid var(--line);
  border-radius: 50%;
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
  min-width: 0;
  line-height: 1.35;
}

.friend__name {
  overflow: hidden;
  color: var(--ink-2);
  font-size: 0.76rem;
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friend__desc {
  overflow: hidden;
  margin-top: 2px;
  color: var(--muted);
  font-size: 0.6rem;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.friends-more {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  margin-top: 14px;
  padding-top: 12px;
  color: var(--violet);
  font-size: 0.7rem;
  border-top: 1px solid var(--line);
  transition: color 0.2s var(--ease);
}

.friends-more:hover {
  color: var(--violet-strong);
}
</style>
