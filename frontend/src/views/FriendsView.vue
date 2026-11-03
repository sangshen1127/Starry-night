<script setup>
import { computed, onMounted, ref } from 'vue'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import FriendCard from '@/components/FriendCard.vue'
import { fetchFriendLinks } from '@/api/friendLinks'
import { ApiError } from '@/api/http'
import { safeHttpUrl } from '@/utils/url'

const links = ref([])
const loading = ref(true)
const error = ref('')

const friends = computed(() =>
  links.value.filter((friend) => Boolean(safeHttpUrl(friend.url))),
)

async function load() {
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
      <header class="friends-head glass-panel">
        <div>
          <p class="friends-head__kicker">
            <span aria-hidden="true">✦</span>
            FRIENDS · ともだち
            <span class="friends-head__jp" aria-hidden="true">つながり</span>
          </p>
          <h1>友链星图</h1>
          <p class="friends-head__lead">
            一些在同一个夜空下发光的邻居。推开玻璃门，去他们的小屋坐坐吧。
          </p>
        </div>
        <span class="friends-head__deco" aria-hidden="true">♡</span>
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
  padding: 42px 0 24px;
}

.friends-inner {
  max-width: 1100px;
}

.friends-head {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 22px;
  overflow: hidden;
  padding: 34px 38px;
  margin-bottom: 22px;
  background:
    linear-gradient(125deg, rgba(126, 200, 227, 0.07), transparent 48%),
    rgba(255, 255, 255, 0.045);
  border-radius: 26px;
}

.friends-head::after {
  position: absolute;
  right: -70px;
  bottom: -110px;
  width: 280px;
  height: 280px;
  border: 1px solid rgba(126, 200, 227, 0.12);
  border-radius: 50%;
  box-shadow:
    0 0 0 26px rgba(126, 200, 227, 0.025),
    0 0 0 56px rgba(185, 103, 255, 0.02);
  content: '';
}

.friends-head__kicker {
  display: flex;
  align-items: center;
  gap: 7px;
  width: fit-content;
  margin-bottom: 10px;
  padding: 4px 9px;
  color: var(--cyan);
  font-family: var(--font-mono);
  font-size: 0.54rem;
  letter-spacing: 0.13em;
  background: rgba(126, 200, 227, 0.08);
  border: 1px solid rgba(126, 200, 227, 0.14);
  border-radius: 999px;
}

.friends-head__kicker > span:first-child {
  color: var(--pink);
  animation: twinkle 3.4s ease-in-out infinite;
}

.friends-head__jp {
  padding-left: 7px;
  color: var(--faint);
  border-left: 1px solid rgba(255, 255, 255, 0.12);
  font-family: var(--font-display);
}

.friends-head h1 {
  color: var(--ink);
  font-family: var(--font-display);
  font-size: clamp(2.2rem, 4vw, 2.8rem);
  font-weight: 400;
  letter-spacing: 0.01em;
  line-height: 1.3;
  text-shadow: 0 0 26px rgba(126, 200, 227, 0.15);
}

.friends-head__lead {
  max-width: 40em;
  margin-top: 8px;
  color: var(--muted);
  font-size: 0.8rem;
  line-height: 1.85;
}

.friends-head__deco {
  position: absolute;
  top: 20px;
  right: 24px;
  color: rgba(255, 158, 181, 0.3);
  font-size: 1.1rem;
  animation: float-soft 5s ease-in-out infinite;
}

.friends-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.friend-skeleton {
  height: 82px;
  border-radius: 18px;
}

.friends-note {
  margin-top: 22px;
  padding-top: 16px;
  color: var(--faint);
  font-size: 0.68rem;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.friends-note a {
  color: var(--pink);
  border-bottom: 1px solid rgba(255, 158, 181, 0.25);
  transition: color 0.2s var(--ease);
}

.friends-note a:hover {
  color: var(--cyan);
}

@media (max-width: 1024px) {
  .friends-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .friends-head {
    padding: 26px;
  }
}

@media (max-width: 640px) {
  .friends-page {
    padding-top: 28px;
  }

  .friends-head {
    padding: 22px;
    border-radius: 20px;
  }

  .friends-head h1 {
    font-size: 2.2rem;
  }

  .friends-grid {
    grid-template-columns: minmax(0, 1fr);
    gap: 12px;
  }
}
</style>
