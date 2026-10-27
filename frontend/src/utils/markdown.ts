import MarkdownIt from 'markdown-it'
import DOMPurify from 'dompurify'

const markdown = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true,
  typographer: false,
})

const renderToken = markdown.renderer.rules.link_open ?? ((tokens, idx, options, _env, self) =>
  self.renderToken(tokens, idx, options))

// 站外链接新窗口打开；内链保持当前页。
markdown.renderer.rules.link_open = (tokens, idx, options, env, self) => {
  const href = String(tokens[idx].attrGet('href') ?? '')
  if (/^https?:\/\//i.test(href)) {
    tokens[idx].attrSet('target', '_blank')
    tokens[idx].attrSet('rel', 'noopener noreferrer')
  }
  return renderToken(tokens, idx, options, env, self)
}

/**
 * 文章正文来自后端 Markdown 原文。
 * markdown-it 关闭原始 HTML，DOMPurify 再做一次白名单过滤，最后才交给 v-html。
 */
export function renderMarkdown(source?: string | null): string {
  if (!source) return ''
  const html = markdown.render(source)
  return DOMPurify.sanitize(html, {
    USE_PROFILES: { html: true },
    ADD_ATTR: ['target', 'rel'],
  })
}
