const HTTP_URL = /^https?:\/\//i

/** 后端返回 /uploads/** 时拼上静态资源前缀；绝对地址原样保留。 */
export function resolveAssetUrl(value?: string | null): string {
  if (!value) return ''
  if (HTTP_URL.test(value) || value.startsWith('data:') || value.startsWith('blob:')) {
    return value
  }

  const base = (import.meta.env.VITE_ASSET_BASE_URL || '').replace(/\/$/, '')
  if (!base) return value
  return `${base}${value.startsWith('/') ? value : `/${value}`}`
}

/** 只允许 http/https 外链，防止 javascript: 之类的内容进入友链。 */
export function safeHttpUrl(value?: string | null): string {
  if (!value) return ''
  try {
    const url = new URL(value)
    return url.protocol === 'http:' || url.protocol === 'https:' ? url.toString() : ''
  } catch {
    return ''
  }
}

/** 友链头像缺失时显示名字首字。 */
export function getInitial(name?: string | null): string {
  const text = name?.trim()
  if (!text) return '✦'
  return Array.from(text)[0] || '✦'
}
