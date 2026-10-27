export interface SocialLink {
  label: string
  href: string
}

export const siteConfig = {
  name: 'Start Night',
  brand: 'START NIGHT',
  tagline: 'A PERSONAL BLOG · 夜の記録',
  description: '在夜色里记录代码、动画与日常的个人博客。',
  author: 'Start Night 的站长',
  startYear: 2026,

  hero: {
    kicker: 'A PERSONAL BLOG · 夜の記録',
    titleTop: '把每一个夜晚，',
    titleBottom: '写成一点光。',
    lead: '这里记录代码、动画、读书，和那些睡不着时冒出来的念头。愿每一次深夜打开，都能遇见一点属于自己的光。',
    primaryCta: '读最新文章',
    secondaryCta: '关于这座小屋',
  },

  about:
    '你好，我是 Start Night 的站长。白天写代码，夜里写一点文字。这里没有标准答案，只有慢慢长大的记录。',

  /**
   * 角色插画：先把图片放到 public/images/character/ 下，
   * 再把这里改成 '/images/character/hero.webp'。
   * 留空时会显示内置的夜空占位图。
   */
  characterImage: '',
  characterAlt: 'Start Night 的动漫角色插画',

  /** 侧栏小头像，需自行提供；留空时显示月亮标记。 */
  avatarImage: '',

  socialLinks: [] as SocialLink[],

  footerNote: '在夜色里，记录一点光。',
} as const
