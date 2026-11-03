# Start Night · Frontend

Start Night 个人博客前端：**暗色二次元玻璃拟态**完整实现。

- Vue 3 + JavaScript + Vite + Vue Router + Pinia + Axios
- 九个公开 / 认证接口 + 管理后台接口全部接入
- 深空紫蓝背景 + 星空 / 花瓣粒子 + 粉紫青霓虹 + 毛玻璃卡片
- 首页、归档搜索、文章详情、评论提交、友链、后台登录 / 登出 / 会话恢复
- **站长管理后台**：概览、文章 CRUD、评论审核、友链管理、封面上传
- 公开站与后台入口隔离：用户端不显示登录按钮，后台从 `/admin` 进入
- 看板娘 `starNight.png` 统一用于 Hero、登录页、Header、Footer、About
- 导航项 Hover 时从下方弹出各自专属的 80px 小人（首页 / 文章 / 友链 / 关于），带回弹、底部光晕和切图淡入淡出
- 四张悬浮缩略图合计约 86KB，页面挂载时预加载
- 响应式玻璃布局 + 鼠标光斑 + 交错入场动效

![Start Night 首页](preview/home-desktop.png)
![导航悬浮小人](preview/nav-hover.png)

## 页面

| 路由 | 页面 | 接口 |
|---|---|---|
| `/` | 首页聚合 | `GET /api/home` |
| `/posts` | 文章归档（搜索 + 分页） | `GET /api/posts` |
| `/posts/:id` | 文章详情 | `GET /api/posts/{id}` |
| `/posts/:id` | 评论列表 / 提交 | `GET /api/posts/{id}/comments` · `POST /api/comments` |
| `/friends` | 友链 | `GET /api/friend-links` |
| `/admin/login` | 后台登录 | `POST /api/auth/login` · `GET /api/auth/me` · `POST /api/auth/logout` |
| `/admin` | 管理概览 | `GET /api/admin/stats` |
| `/admin/posts` | 文章管理 | `GET /api/admin/posts` · `PATCH /api/admin/posts/{id}/status` · `DELETE /api/admin/posts/{id}` |
| `/admin/posts/new` | 新建文章 | `POST /api/admin/posts` |
| `/admin/posts/:id/edit` | 编辑文章 | `GET /api/admin/posts/{id}` · `PUT /api/admin/posts/{id}` |
| `/admin/comments` | 评论审核 | `GET /api/admin/comments` · `PATCH /api/admin/comments/{id}/status` · `DELETE /api/admin/comments/{id}` |
| `/admin/friend-links` | 友链管理 | `GET/POST /api/admin/friend-links` · `PUT/DELETE /api/admin/friend-links/{id}` |
| 其他 | 404 | — |

## 管理后台

公开站不显示任何登录或账号入口。管理端直接访问：

```text
http://127.0.0.1:5173/admin
```

未登录会自动跳转到：

```text
/admin/login?redirect=/admin
```

登录后回到目标后台页面。旧地址 `/login` 已停用，访问会显示 404。`/admin/**` 由路由守卫和 Spring Session 双重保护。

后台登录页沿用 C 端同款 Header / Footer，并在右上角额外显示登录入口；登入后的 `/admin` 顶部继续显示 C 端 Header 和账号菜单，下方保留独立管理布局，右上角账号菜单可直接退出。

![后台登录](preview/admin-login.png)

![管理后台概览](preview/admin-dashboard.png)

- **概览**：文章 / 草稿 / 评论 / 待审核 / 友链数量。
- **文章**：搜索、状态筛选、分页、快速发布/转草稿、编辑、前台预览、删除。
- **编辑器**：标题、摘要、Markdown 正文、草稿/发布状态、封面 URL、本地图片上传。
- **评论**：待审核 / 已通过 / 垃圾 / 全部筛选，通过审核、两步删除。
- **友链**：新增、编辑、排序、显示/隐藏、头像上传、两步删除。
- 后台里的公开站链接（C 端 Header 导航、前台按钮、账号菜单回到首页）统一在新标签页打开，后台标签会保留。

后台截图：[概览](preview/admin-dashboard.png) · [文章](preview/admin-posts.png) · [编辑器](preview/admin-post-editor.png) · [评论](preview/admin-comments.png) · [友链](preview/admin-friend-links.png) · [移动端](preview/admin-mobile.png)

## 导航悬浮小人

每个导航项在 `src/config/navigation.js` 里有自己的 `hoverImg`：

| 导航 | 原图 | 悬浮缩略图 |
|---|---|---|
| 首页 | `public/images/character/home.png` | `/images/character/nav/home.webp` |
| 文章 | `public/images/character/post.png` | `/images/character/nav/post.webp` |
| 友链 | `public/images/character/friend_link.png` | `/images/character/nav/friend-link.webp` |
| 关于 | `public/images/character/guanyu.png` | `/images/character/nav/guanyu.webp` |

悬浮层是导航栏下方一个共用的绝对定位槽位：

- `activeHover` 记录当前导航项
- 根据当前导航项的中心计算 `--nav-mascot-x`，角色横向平滑移动
- `<Transition mode="out-in">` 做旧图淡出缩小、新图淡入放大
- `pointer-events: none` 防止挡住鼠标
- 组件挂载时用 `new Image()` 预加载四张缩略图

原始 PNG 保留给其他用途；`nav/` 下是 220px 高的 WebP 缩略图，避免为了 80px 的角色一次加载约 8MB 原图。修改角色后可用 Pillow 重新生成同尺寸缩略图。

## 设计系统

- 背景：`#0a0713` / `#0f0c1b`，叠加星云、月亮、星星、樱花花瓣和点阵。
- 玻璃：`rgba(255,255,255,.07)` + `backdrop-filter: blur(16px) saturate(140%)` + 细高光边框 + 浮起阴影。
- 强调色：樱粉 `#ff9eb5`、天青 `#7ec8e3`、电光紫 `#b967ff`；主按钮使用粉紫渐变。
- 字体：ZCOOL KuaiLe / Quicksand 优先，系统圆体回退。
- 动效：星空闪烁、花瓣飘落、首屏浮入、卡片交错上浮、鼠标光斑、按钮微缩放。
- `prefers-reduced-motion` 下关闭花瓣和大幅动效。

## 快速开始

环境要求：Node.js >= 20.19，npm；后端 Spring Boot 8080 + MySQL。

```bash
npm install
npm run dev
```

打开 http://127.0.0.1:5173/。

开发代理：

- `/api/**` → `http://localhost:8080`
- `/uploads/**` → `http://localhost:8080`

## 常用命令

```bash
npm run dev        # 开发
npm run build      # 生产构建（Vite）
npm run preview    # 预览 dist
```

## 角色插画

当前看板娘为 `public/images/character/starNight.png`（1225×1284 透明 PNG），已在以下位置统一使用：

- 首页 Hero 角色卡
- 登录页左侧角色卡
- Header / Footer 圆形小头像
- About 卡片头像

替换成新的看板娘：

1. 把新图片放到 `public/images/character/`。
2. 修改 `src/config/site.js`：

```js
characterImage: '/images/character/你的图片.png',
avatarImage: '/images/character/你的图片.png',
characterAlt: '你的角色插画描述',
```

加载失败会自动回退到内置夜空占位。当前 PNG 约 1.9MB，若要优化体积可转成 WebP。

## 接口注意事项

- 后端首页评论字段仍叫 `lastestComments`，文档写 `latestComments`；前端在 `src/api/home.js` 同时兼容。建议后端统一。
- 详情 `commentCount` 当前固定为 0，评论区实际条数使用 `/comments` 返回的 `total`。
- 评论提交成功后是 `pending`，不会立即显示；表单会显示审核提示并本地冷却 60 秒。
- 后端参数校验错误目前是 Spring 默认 JSON，前端会做客户端校验并提供友好 400 文案。
- 登录使用 HttpOnly Session Cookie；Axios 已开启 `withCredentials`。
- 后台登录页为 `/admin/login`；公开站不显示登录入口，旧 `/login` 直接 404。
- 后台接口全部在 `/api/admin/**`，未登录统一返回 401；管理页不能绕过登录态直接访问。
- 封面上传上限 5MB，支持 JPG / PNG / WebP / GIF，文件保存在后端 `uploads/yyyy/MM/`。
- 文章转为草稿后前台详情返回 404；评论通过审核后才会出现在前台。

## 目录

```text
src/
├─ api/              axios 封装 + 公开接口 + admin 接口
├─ components/       玻璃 UI / 角色 / 星空背景 / 评论区
├─ composables/      useHome
├─ config/           站点文案 / 导航
├─ router/           公开路由 + 后台路由 + 登录守卫
├─ stores/           Pinia 登录态
├─ styles/           tokens / 玻璃基础样式 / admin 管理台样式
├─ utils/            日期 / URL / Markdown
└─ views/            首页 / 归档 / 详情 / 友链 / 登录 / 404
   └─ admin/         后台布局 / 概览 / 文章 / 评论 / 友链
```

更多设计、接口、部署说明：[前端开发文档](../md开发文档/前端开发文档.md)。
