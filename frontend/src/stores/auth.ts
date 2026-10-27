import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  fetchCurrentUser,
  loginRequest,
  logoutRequest,
} from '@/api/auth'
import { ApiError } from '@/api/http'
import type { UserInfo } from '@/api/types'

export const useAuthStore = defineStore('auth', () => {
  const user = ref<UserInfo | null>(null)
  const loading = ref(false)
  const restored = ref(false)
  const error = ref('')

  const isAuthenticated = computed(() => Boolean(user.value))

  function errorMessage(caught: unknown, fallback: string): string {
    return caught instanceof ApiError ? caught.message : fallback
  }

  async function restore(): Promise<void> {
    if (restored.value || loading.value) return

    loading.value = true
    try {
      user.value = await fetchCurrentUser()
    } catch {
      // 未登录是首页访客的常态；网络异常也不打断浏览，按游客处理。
      user.value = null
    } finally {
      restored.value = true
      loading.value = false
    }
  }

  async function login(username: string, password: string): Promise<UserInfo> {
    loading.value = true
    error.value = ''

    try {
      const result = await loginRequest({
        username: username.trim(),
        password,
      })
      user.value = result.data
      restored.value = true
      return result.data
    } catch (caught) {
      error.value = errorMessage(caught, '登录失败，请稍后再试')
      throw caught
    } finally {
      loading.value = false
    }
  }

  async function logout(): Promise<void> {
    loading.value = true
    error.value = ''

    try {
      await logoutRequest()
      user.value = null
      restored.value = true
    } catch (caught) {
      // 会话已经失效时，清掉本地状态即可；网络/服务器错误则保留状态并提示重试。
      if (caught instanceof ApiError && caught.status === 401) {
        user.value = null
        restored.value = true
      } else {
        error.value = errorMessage(caught, '退出登录失败，请稍后再试')
        throw caught
      }
    } finally {
      loading.value = false
    }
  }

  function clearError(): void {
    error.value = ''
  }

  return {
    user,
    loading,
    restored,
    error,
    isAuthenticated,
    restore,
    login,
    logout,
    clearError,
  }
})
