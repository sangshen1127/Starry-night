<script setup>
import { computed } from 'vue'
import EmptyState from '@/components/EmptyState.vue'
import ErrorState from '@/components/ErrorState.vue'
import FeaturedPost from '@/components/FeaturedPost.vue'
import PostCard from '@/components/PostCard.vue'
import PostSkeleton from '@/components/PostSkeleton.vue'
import SectionHeading from '@/components/SectionHeading.vue'

const props = defineProps({
  posts: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: String, default: '' },
  lastUpdated: { type: String, default: '' },
})

const emit = defineEmits(['retry'])

const featured = computed(() => props.posts[0] ?? null)
const rest = computed(() => props.posts.slice(1))

const meta = computed(() => {
  if (props.loading) return 'SYNCING THE NIGHT…'
  if (props.error) return 'NIGHT SKY OFFLINE'
  return props.lastUpdated ? `LAST UPDATE · ${props.lastUpdated}` : ''
})
</script>

<template>
  <section id="posts" class="latest" aria-labelledby="latest-heading">
    <SectionHeading
      kicker="LATEST NOTES · 最新笔记"
      title="最新文章"
      heading-id="latest-heading"
      :meta="meta"
    >
      <template #action>
        <RouterLink class="section-link" :to="{ name: 'posts' }">
          全部文章
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
        </RouterLink>
      </template>
    </SectionHeading>

    <p class="latest__lead">把夜里发光的片段，收进一张张玻璃卡片。</p>

    <ErrorState
      v-if="error"
      :message="error"
      title="文章没能从夜色里送达"
      @retry="emit('retry')"
    />

    <div v-else-if="loading" class="latest__loading" aria-busy="true" aria-live="polite">
      <span class="sr-only">正在加载最新文章…</span>
      <PostSkeleton featured />
      <div class="post-grid">
        <PostSkeleton v-for="index in 4" :key="index" />
      </div>
    </div>

    <EmptyState
      v-else-if="!posts.length"
      title="夜色还没落下"
      message="这里还没有文章，等第一篇写完，再一起看星星。"
    />

    <template v-else>
      <FeaturedPost v-if="featured" :post="featured" />
      <div
        v-if="rest.length"
        class="post-grid"
        :class="{ 'post-grid--single': rest.length === 1 }"
      >
        <PostCard
          v-for="post in rest"
          :key="post.id"
          :post="post"
          :wide="rest.length === 1"
        />
      </div>
    </template>
  </section>
</template>

<style scoped>
.latest {
  scroll-margin-top: 100px;
}

.latest__lead {
  margin-top: -12px;
  margin-bottom: 18px;
  color: var(--faint);
  font-size: 0.75rem;
  line-height: 1.8;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.post-grid--single {
  grid-template-columns: minmax(0, 1fr);
}

.latest__loading {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.section-link {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  color: var(--pink);
  font-family: var(--font-mono);
  font-size: 0.62rem;
  transition:
    color 0.2s var(--ease),
    text-shadow 0.2s var(--ease);
}

.section-link svg {
  width: 12px;
  height: 12px;
  transition: transform 0.2s var(--ease);
}

.section-link:hover {
  color: var(--cyan);
  text-shadow: 0 0 16px rgba(126, 200, 227, 0.4);
}

.section-link:hover svg {
  transform: translateX(3px);
}

@media (max-width: 720px) {
  .post-grid {
    grid-template-columns: minmax(0, 1fr);
    gap: 12px;
  }
}
</style>
