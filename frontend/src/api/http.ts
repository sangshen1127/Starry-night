import axios, { type AxiosRequestConfig } from 'axios'
import type { ApiResponse } from './types'

const STATUS_MESSAGES: Record<number, string> = {
  400: '提交内容不符合要求，请检查后重试',
  401: '登录已过期，请重新登录',
  403: '没有权限进行这个操作',
  404: '内容不存在或已被移除',
  409: '数据冲突，请刷新后再试',
  429: '操作太频繁，请稍后再试',
  500: '服务器开小差了，请稍后再试',
}

export interface MutationResult<T> {
  data: T
  message: string
}

export class ApiError extends Error {
  status?: number
  code?: number

  constructor(message: string, status?: number, code?: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
  }
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 12000,
  // 首页接口不需要登录态，但保留 cookie，方便后续评论/后台复用同一个客户端。
  withCredentials: true,
  headers: {
    Accept: 'application/json',
  },
})

function resolveApiError(error: unknown): ApiError {
  if (error instanceof ApiError) return error

  if (axios.isAxiosError(error)) {
    const status = error.response?.status
    const body = error.response?.data as Partial<ApiResponse<unknown>> | undefined
    const fallback = status ? STATUS_MESSAGES[status] : ''
    const message = body?.message || fallback

    if (message) {
      return new ApiError(message, status, body?.code)
    }

    return new ApiError(
      status ? '请求失败，请稍后再试' : '网络连接失败，请检查后端服务是否启动',
      status,
      body?.code,
    )
  }

  return new ApiError('未知错误，请稍后再试')
}

/**
 * 统一处理后端的 { code, message, data } 契约。
 * 成功返回 data；失败抛 ApiError，页面负责展示错误态。
 */
export async function get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
  try {
    const response = await apiClient.get<ApiResponse<T>>(url, config)
    const body = response.data

    if (!body || typeof body.code !== 'number') {
      throw new ApiError('接口返回格式不正确', response.status)
    }

    if (body.code !== 1) {
      throw new ApiError(
        body.message || STATUS_MESSAGES[response.status] || '请求失败',
        response.status,
        body.code,
      )
    }

    return body.data
  } catch (error) {
    throw resolveApiError(error)
  }
}

/**
 * 写请求：除了 data，还要保留后端文案。
 * 评论提交成功时后端把提示放在 message 里、data 为 null，所以不能只返回 data。
 */
export async function post<T>(
  url: string,
  payload?: unknown,
  config?: AxiosRequestConfig,
): Promise<MutationResult<T>> {
  try {
    const response = await apiClient.post<ApiResponse<T>>(url, payload, config)
    const body = response.data

    if (!body || typeof body.code !== 'number') {
      throw new ApiError('接口返回格式不正确', response.status)
    }

    if (body.code !== 1) {
      throw new ApiError(
        body.message || STATUS_MESSAGES[response.status] || '请求失败',
        response.status,
        body.code,
      )
    }

    return { data: body.data, message: body.message }
  } catch (error) {
    throw resolveApiError(error)
  }
}
