<script setup lang="ts">
import { computed } from 'vue'
import FriendLinks from '@/components/FriendLinks.vue'
import HeroSection from '@/components/HeroSection.vue'
import LatestComments from '@/components/LatestComments.vue'
import LatestPosts from '@/components/LatestPosts.vue'
import SidebarAbout from '@/components/SidebarAbout.vue'
import { useHome } from '@/composables/useHome'
import { formatDate } from '@/utils/date'

const { latestPosts, latestComments, friendLinks, loading, error, load } = useHome()

const lastUpdated = computed(() => formatDate(latestPosts.value[0]?.publishedAt))
</script>

<template>
  <div class="home">
    <HeroSection
      :last-updated="lastUpdated"
      :loading="loading"
      :error="error"
    />

    <div class="container home__layout">
      <LatestPosts
        :posts="latestPosts"
        :loading="loading"
        :error="error"
        :last-updated="lastUpdated"
        @retry="load"
      />

      <aside class="home__sidebar" aria-label="站点侧栏">
        <SidebarAbout />
        <LatestComments
          :comments="latestComments"
          :loading="loading"
          :error="error"
        />
        <FriendLinks :links="friendLinks" :loading="loading" :error="error" />
      </aside>
    </div>
  </div>
</template>

<style scoped>
.home__layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 330px;
  align-items: start;
  gap: 56px;
  padding-top: 64px;
}

.home__sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

@media (max-width: 1120px) {
  .home__layout {
    grid-template-columns: minmax(0, 1fr);
    gap: 38px;
  }

  .home__sidebar {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    align-items: start;
    gap: 20px;
  }
}

@media (max-width: 820px) {
  .home__sidebar {
    grid-template-columns: minmax(0, 1fr);
  }
}

@media (max-width: 640px) {
  .home__layout {
    padding-top: 46px;
    gap: 32px;
  }
}
</style>
