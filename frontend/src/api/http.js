import axios from 'axios'

const STATUS_MESSAGES = {
  400: '提交内容不符合要求，请检查后重试',
  401: '登录已过期，请重新登录',
  403: '没有权限进行这个操作',
  404: '内容不存在或已被移除',
  409: '数据冲突，请刷新后再试',
  429: '操作太频繁，请稍后再试',
  500: '服务器开小差了，请稍后再试',
}

export class ApiError extends Error {
  constructor(message, status, code) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.code = code
  }
}

export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 12000,
  withCredentials: true,
  headers: {
    Accept: 'application/json',
  },
})

// 处理 axios 错误，返回统一的 ApiError
function resolveApiError(error) {
  if (error instanceof ApiError) return error

  if (axios.isAxiosError(error)) {
    const status = error.response?.status
    const body = error.response?.data
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

// 封装 get 请求
export async function get(url, config) {
  try {
    const response = await apiClient.get(url, config)
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

export async function post(url, payload, config) {
  return writeRequest('post', url, payload, config)
}

async function writeRequest(method, url, payload, config) {
  try {
    const response = await apiClient.request({
      ...config,
      method,
      url,
      data: payload,
    })
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

export function put(url, payload, config) {
  return writeRequest('put', url, payload, config)
}

export function patch(url, payload, config) {
  return writeRequest('patch', url, payload, config)
}

export function del(url, config) {
  return writeRequest('delete', url, undefined, config)
}

export async function upload(url, formData, config) {
  try {
    const response = await apiClient.post(url, formData, {
      timeout: 30000,
      ...config,
    })
    const body = response.data

    if (!body || typeof body.code !== 'number') {
      throw new ApiError('接口返回格式不正确', response.status)
    }

    if (body.code !== 1) {
      throw new ApiError(
        body.message || STATUS_MESSAGES[response.status] || '上传失败',
        response.status,
        body.code,
      )
    }

    return { data: body.data, message: body.message }
  } catch (error) {
    throw resolveApiError(error)
  }
}
