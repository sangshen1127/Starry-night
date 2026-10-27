# Start Night · Frontend

Start Night 个人博客前端：全站公开页面 + 站长登录态。

- Vue 3 + TypeScript + Vite + Vue Router + Pinia + Axios
- Markdown 正文使用 markdown-it 渲染，DOMPurify 做 XSS 过滤
- 九个后端接口全部接入
- 完整设计 / 接口 / 部署说明见：[前端开发文档](../md开发文档/前端开发文档.md)

## 页面与接口

| 路由 | 页面 | 接口 |
|---|---|---|
| `/` | 首页 | `GET /api/home` |
| `/posts` | 文章归档（搜索 + 分页） | `GET /api/posts` |
| `/posts/:id` | 文章详情 + 评论 | `GET /api/posts/{id}` / `GET /api/posts/{id}/comments` |
| `/posts/:id` | 提交留言 | `POST /api/comments` |
| `/friends` | 友链 | `GET /api/friend-links` |
| `/login` | 站长登录 | `POST /api/auth/login` |
| Header | 会话恢复 / 退出 | `GET /api/auth/me` / `POST /api/auth/logout` |

## 快速开始

环境要求：Node.js >= 20.19，npm。

```bash
npm install
npm run dev
```

打开 http://127.0.0.1:5173/。

开发代理已经配置：

- `/api/**` → `http://localhost:8080`
- `/uploads/**` → `http://localhost:8080`

启动前请先启动 MySQL 与后端 Spring Boot（默认端口 8080）。

## 常用命令

```bash
npm run dev        # 开发
npm run typecheck  # vue-tsc 类型检查
npm run build      # 类型检查 + 生产构建
npm run preview    # 预览 dist
```

## 接入角色插画

1. 把图片放到 `public/images/character/hero.webp`（推荐 3:4 / 4:5 竖图，WebP，< 400KB）。
2. 修改 `src/config/site.ts`：

```ts
characterImage: '/images/character/hero.webp',
characterAlt: '你的角色插画描述',
```

留空或加载失败时会自动显示内置夜空占位图。侧栏头像同理，使用 `avatarImage`。

## 接口注意事项

- 后端 `Home` record 目前返回的评论字段是 `lastestComments`（拼写少了一个 `t`），
  接口文档写的是 `latestComments`。前端在 `src/api/home.ts` 已经同时兼容两者；
  建议后端后续统一成 `latestComments`。
- 详情接口的 `commentCount` 当前固定为 0，前端改用评论接口的 `total` 显示真实条数。
- 评论提交后是 `pending`，不会立即出现在列表；页面会显示审核提示。
- 服务端评论 / 登录限流、统一参数校验错误体尚未实现；前端已有 429 / 400 的展示分支。

## 本期范围

已实现九个公开 / 认证接口对应的前端功能、响应式、加载 / 错误 / 空状态和登录态恢复。

未实现：后台内容管理（文章 / 评论 / 友链 CRUD），因为九个接口里不包含 admin 接口。
登录目前用于保持站长会话，后台页面下一阶段再做。
