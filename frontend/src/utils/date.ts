/** 把后端固定格式 yyyy-MM-dd HH:mm:ss 按“墙上时间”解析，避免时区二次偏移。 */
function parseBackendDate(value?: string | null): Date | null {
  if (!value) return null

  const match = value.match(
    /^(\d{4})-(\d{2})-(\d{2})(?:[ T](\d{2}):(\d{2})(?::(\d{2}))?)?$/,
  )
  if (!match) return null

  const date = new Date(
    Number(match[1]),
    Number(match[2]) - 1,
    Number(match[3]),
    Number(match[4] || 0),
    Number(match[5] || 0),
    Number(match[6] || 0),
  )

  return Number.isNaN(date.getTime()) ? null : date
}

function pad(value: number): string {
  return String(value).padStart(2, '0')
}

/** 2026.10.21 */
export function formatDate(value?: string | null): string {
  const date = parseBackendDate(value)
  if (!date) return ''
  return `${date.getFullYear()}.${pad(date.getMonth() + 1)}.${pad(date.getDate())}`
}

/** 10.24 21:42 —— 用于侧栏留言。 */
export function formatCommentDate(value?: string | null): string {
  const date = parseBackendDate(value)
  if (!date) return ''
  return `${pad(date.getMonth() + 1)}.${pad(date.getDate())} ${pad(date.getHours())}:${pad(date.getMinutes())}`
}

/** 给 <time datetime> 用的基础格式；无有效时间时返回空字符串。 */
export function toDateTimeAttr(value?: string | null): string {
  const date = parseBackendDate(value)
  if (!date) return ''
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(
    date.getHours(),
  )}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`
}

/** 1.2k / 3.4万；0 或没有数值时返回空字符串，避免首页出现“0 次阅读”。 */
export function formatViewCount(value?: number | null): string {
  if (typeof value !== 'number' || !Number.isFinite(value) || value <= 0) return ''
  if (value >= 10000) {
    return `${(value / 10000).toFixed(1).replace(/\.0$/, '')}万`
  }
  if (value >= 1000) {
    return `${(value / 1000).toFixed(1).replace(/\.0$/, '')}k`
  }
  return String(value)
}
