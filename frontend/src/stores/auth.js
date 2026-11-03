import { computed, ref } from 'vue'
import { defineStore } from 'pinia'
import {
  fetchCurrentUser,
  loginRequest,
  logoutRequest,
} from '@/api/auth'
import { ApiError } from '@/api/http'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const loading = ref(false)
  const restored = ref(false)
  const error = ref('')

  const isAuthenticated = computed(() => Boolean(user.value))

  let restorePromise = null

  function errorMessage(caught, fallback) {
    return caught instanceof ApiError ? caught.message : fallback
  }

  function restore() {
    if (restored.value) return Promise.resolve()
    if (restorePromise) return restorePromise

    restorePromise = (async () => {
      loading.value = true
      try {
        user.value = await fetchCurrentUser()
      } catch {
        user.value = null
      } finally {
        restored.value = true
        loading.value = false
        restorePromise = null
      }
    })()

    return restorePromise
  }

  async function login(username, password) {
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

  async function logout() {
    loading.value = true
    error.value = ''

    try {
      await logoutRequest()
      user.value = null
      restored.value = true
    } catch (caught) {
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

  function clearError() {
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
