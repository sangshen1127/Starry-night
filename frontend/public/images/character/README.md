# 角色插画放置说明

当前看板娘使用本目录下的 **`starNight.png`**，并在 `src/config/site.js` 中配置：

```js
characterImage: '/images/character/starNight.png',
avatarImage: '/images/character/starNight.png',
characterAlt: 'Start Night 的紫发少女看板娘',
```

同一个形象会用在：Hero、登录页、Header 小头像、Footer 小头像、About 头像位。
替换时可以直接覆盖 `starNight.png`，或者换文件名后同步修改 `src/config/site.js`。

## 当前图片规格

- 尺寸：1225 × 1284
- 背景：透明 PNG
- 建议：保持竖图 / 近方图，人物脸部在上半部分
- 可优化为 WebP 以减小体积

## 导航悬浮小人

`src/config/navigation.js` 的 `hoverImg` 使用 `nav/` 下的缩略图：

| 导航 | 原图 | 使用的缩略图 |
|---|---|---|
| 首页 | `home.png` | `nav/home.webp` |
| 文章 | `post.png` | `nav/post.webp` |
| 友链 | `friend_link.png` | `nav/friend-link.webp` |
| 关于 | `guanyu.png` | `nav/guanyu.webp` |

导航显示尺寸只有 80px，原图约 1.7～2.2MB；不要把四张原图直接一次性预加载。`nav/` 下的 WebP 是 220px 高的缩略图，四张合计约 86KB。替换原图后重新生成 `nav/` 缩略图即可。

## 放置位置

- 图片放在 `public/images/character/`
- 该目录构建后会原样复制到站点根路径，不走 Vite 打包
- 图片加载失败时会回退到内置夜空占位
