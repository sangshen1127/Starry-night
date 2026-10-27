<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import FriendCard from '@/components/FriendCard.vue'
import { fetchFriendLinks } from '@/api/friendLinks'
import { ApiError } from '@/api/http'
import type { FriendLink } from '@/api/types'
import { safeHttpUrl } from '@/utils/url'

const links = ref<FriendLink[]>([])
const loading = ref(true)
const error = ref('')

const friends = computed(() =>
  links.value.filter((friend) => Boolean(safeHttpUrl(friend.url))),
)

async function load(): Promise<void> {
  loading.value = true
  error.value = ''

  try {
    links.value = await fetchFriendLinks()
  } catch (caught) {
    links.value = []
    error.value =
      caught instanceof ApiError ? caught.message : '友链加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="friends-page">
    <div class="container friends-inner">
      <header class="friends-head">
        <p class="friends-head__kicker">
          <span aria-hidden="true">✦</span>
          FRIENDS · ともだち
        </p>
        <h1>友链</h1>
        <p class="friends-head__lead">
          一些在同一个夜空下发光的邻居。去他们的小屋坐坐吧。
        </p>
      </header>

      <ErrorState
        v-if="error"
        title="友链暂时没有加载出来"
        :message="error"
        @retry="load"
      />

      <div v-else-if="loading" class="friends-grid" aria-busy="true" aria-live="polite">
        <span class="sr-only">正在加载友链…</span>
        <span v-for="index in 6" :key="index" class="skeleton friend-skeleton" />
      </div>

      <EmptyState
        v-else-if="!friends.length"
        title="友链还在布置中"
        message="等几位邻居搬进来，这里就会亮起来。"
      />

      <div v-else class="friends-grid">
        <FriendCard v-for="friend in friends" :key="friend.id" :friend="friend" />
      </div>

      <p class="friends-note">
        想交换友链？
        <RouterLink :to="{ name: 'home', hash: '#comments' }">在留言板</RouterLink>
        留下你的站点地址，审核后我会加上去。
      </p>
    </div>
  </div>
</template>

<style scoped>
.friends-page {
  padding: 52px 0 24px;
}

.friends-inner {
  max-width: 1100px;
}

.friends-head {
  max-width: 640px;
  margin-bottom: 30px;
}

.friends-head__kicker {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
  color: var(--violet);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  letter-spacing: 0.18em;
}

.friends-head__kicker span {
  color: var(--gold);
}

.friends-head h1 {
  color: var(--ink);
  font-size: clamp(2.4rem, 4.4vw, 3.2rem);
  font-weight: 800;
  letter-spacing: -0.035em;
  line-height: 1.25;
}

.friends-head__lead {
  margin-top: 8px;
  color: var(--muted);
  font-size: 0.88rem;
  line-height: 1.8;
}

.friends-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 18px;
}

.friend-skeleton {
  height: 86px;
  border-radius: var(--radius-md);
}

.friends-note {
  margin-top: 28px;
  padding-top: 18px;
  color: var(--muted);
  font-size: 0.75rem;
  border-top: 1px solid var(--line);
}

.friends-note a {
  color: var(--violet);
  border-bottom: 1px solid var(--violet-soft);
  transition: color 0.2s var(--ease);
}

.friends-note a:hover {
  color: var(--violet-strong);
}

@media (max-width: 1024px) {
  .friends-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 640px) {
  .friends-page {
    padding-top: 38px;
  }

  .friends-head h1 {
    font-size: 2.2rem;
  }

  .friends-grid {
    grid-template-columns: minmax(0, 1fr);
    gap: 14px;
  }
}
</style>
