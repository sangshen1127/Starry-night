# 角色插画放置说明

把动漫人物插画放在本目录，然后在 `src/config/site.ts` 里设置：

```ts
characterImage: '/images/character/hero.webp',
```

首屏右侧的 `CharacterArt` 组件会自动切换为图片；图片加载失败或留空时，会显示内置的夜空占位图。

## 建议规格

- 比例：3:4 或 4:5（竖图），首屏容器按右侧高度裁切
- 尺寸：1200 × 1600 以内，WebP/AVIF，建议 < 400KB
- 构图：人物脸部放在画面上方 1/3，底部留出空间，避免重要元素贴边
- 背景：透明 PNG/WebP 或深色夜空背景都可以；透明图会透出首屏星空
- 不放进 `src/assets`：这个目录在 `public` 下，构建后会原样复制到站点根路径

侧栏小头像同理，放到本目录后设置：

```ts
avatarImage: '/images/character/avatar.webp',
```
