import { computed, onMounted, ref } from 'vue'
import { fetchHomeData } from '@/api/home'
import { ApiError } from '@/api/http'
import type { HomeData } from '@/api/types'

export function useHome() {
  const data = ref<HomeData | null>(null)
  const loading = ref(true)
  const error = ref('')

  async function load(): Promise<void> {
    loading.value = true
    error.value = ''

    try {
      data.value = await fetchHomeData()
    } catch (caught) {
      error.value =
        caught instanceof ApiError ? caught.message : '首页数据加载失败，请稍后再试'
    } finally {
      loading.value = false
    }
  }

  const latestPosts = computed(() => data.value?.latestPosts ?? [])
  const latestComments = computed(() => data.value?.latestComments ?? [])
  const friendLinks = computed(() => data.value?.friendLinks ?? [])

  onMounted(load)

  return {
    data,
    loading,
    error,
    latestPosts,
    latestComments,
    friendLinks,
    load,
  }
}
