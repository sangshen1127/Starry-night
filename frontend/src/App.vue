<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import NightBackdrop from '@/components/NightBackdrop.vue'
import SiteFooter from '@/components/SiteFooter.vue'
import SiteHeader from '@/components/SiteHeader.vue'
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
const route = useRoute()
const isAdmin = computed(() => route.meta.admin === true)
const showSiteFooter = computed(
  () => !isAdmin.value || route.meta.siteChrome === true,
)

onMounted(() => {
  void auth.restore()
})
</script>

<template>
  <NightBackdrop />
  <div class="site-shell">
    <a class="skip-link" href="#main">跳到主要内容</a>
    <SiteHeader />
    <main id="main" tabindex="-1" :class="{ 'site-main--admin': isAdmin }">
      <RouterView />
    </main>
    <SiteFooter v-if="showSiteFooter" />
  </div>
</template>

<style scoped>
.site-shell {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.site-main--admin {
  flex: 1;
}
</style>
