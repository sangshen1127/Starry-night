# 后端优化与 Bug 清单

> 版本：v1.0
> 日期：2026-11-03
> 核对范围：`back/blogDemo` **工作区当前代码**（已包含登录限流改造）
> 编译核对：`mvn -o -DskipTests compile` 通过（EXIT=0）
> 说明：**本文只覆盖后端**。前端（代码高亮、TOC、图片粘贴等）另开一份，别混在一起改。

---

## 0. 怎么读这份清单

| 严重度 | 含义 | 处理时机 |
|---|---|---|
| **P0** | 现在就是错的，用户能直接看到异常行为 | 立刻修，修完再干别的 |
| **P1** | 本地看不出问题，**上线/部署后必炸** | 下一批修 |
| **P2** | 不炸，但会留坑：脏数据、难排查、体验差 | 本周内 |
| **P3** | 代码质量、一致性、可读性 | 有空就收拾 |

每条都给了：**现象 → 位置 → 影响 → 修法 → 验证**。
第 9 节有推荐的执行顺序，附录 A 是本轮已经确认修好的（别重复劳动）。

---

## 1. 先说结论

- **1 个 P0**：登录输错密码会返回 **500**（不是 401），前端会显示「接口返回格式不正确」
- **1 个 P1**：拿不到真实 IP，上线后**评论限流和登录限流都会失效**，而且可以被用来把所有人锁在门外
- 其余是 P2/P3：友链并发重复 500、DTO 缺校验、评论只能通过不能标垃圾、无全局异常兜底等
- **做得好的地方**：接口分层、统一 `Result`、401/403 统一 JSON、分页封装、`@Valid` + 全局校验异常处理、评论限流、登录限流（滑动窗口）——这套基本功已经比大多数练手项目干净了

---

## 2. P0：输入错误密码返回 500

### 2.1 现象

现在用 `POST /api/auth/login` 输错密码：

- **期望**：401 + `{"code":0,"message":"用户名或密码错误"}`
- **实际**：500，响应体是 Spring 默认错误结构（没有 `code` 字段）
- 前端 [http.js](frontend/src/api/http.js) 里 `body.code` 为 `undefined` → 抛「接口返回格式不正确」

### 2.2 代码链路（三处，缺一不可）

```
AuthService.java:28   catch (AuthenticationException e) {
AuthService.java:31        throw new RuntimeException(e);   ← 把 AuthenticationException 包成了 RuntimeException
                             }
        ↓ 向上抛
AuthController.java:54  catch (AuthenticationException e)     ← 抓不到了！类型不匹配
        ↓ 继续向容器抛
GlobalExceptionHandler  ← 没有 Exception 兜底，无人接手
        ↓
Spring 默认 /error → 500
```

**根因**：`RuntimeException` 不是 `AuthenticationException` 的子类。你原本靠「service 抛、controller 抓」的约定，被这层包装切断了。

### 2.3 修法（一行）

```java
// service/Auth/AuthService.java:28-32
} catch (AuthenticationException e) {
    loginAttemptService.recordFailure(ip, username);
    throw e;                       // ← 原样抛，别包
}
```

`AuthenticationException` 本身是 `RuntimeException` 的子类，方法签名不用加 `throws`。

### 2.4 回归验证（5 条，缺一不可）

```http
### 1. 密码错 → 401（不是 500）
POST /api/auth/login
{"username":"admin","password":"wrong"}
# 期望 401 + {"code":0,"message":"用户名或密码错误"}

### 2. 连续错 5 次 → 第 6 次 429
# 期望 429 + "登录失败次数过多，请 X 秒后重试"

### 3. 锁定期内输正确密码 → 仍然 429
# 这条证明 429 走的是 ensureAllowed，没被 catch 吞掉

### 4. 正确密码（窗口过期后）→ 200 + Set-Cookie
### 5. 未登录访问 /api/auth/me → 401
```

### 2.5 为什么 429 那条路没坏

`LoginAttemptService.ensureAllowed()` 是在 `try` **外面**调用的（[AuthService.java:20](back/blogDemo/src/main/java/com/sangshen/service/Auth/AuthService.java#L20)），抛的是 `TooManyRequestException`，直接落到 `GlobalExceptionHandler` 的 429 分支。这一段是对的，别动。

### 2.6 顺带建议（P3）

`LoginAttemptService` 里两个小地方：

- 阈值 `MAX_FAILURES` / `WINDOW` 硬编码在类里，以后想调要改代码。可以挪到 `application.yml`（`blog.login.max-failures` / `window-minutes`）
- [第 53 行](back/blogDemo/src/main/java/com/sangshen/service/Auth/LoginAttemptService.java#L53) 的 `records.size()` 在 `synchronized` 块外读，日志里的数字可能差一两个。把 `log` 挪进同步块即可（不影响功能）

---

## 3. P1：拿不到真实 IP，限流上线即失效

### 3.1 位置

| 文件 | 行 | 代码 |
|---|---|---|
| [CommentController.java](back/blogDemo/src/main/java/com/sangshen/controller/CommentController.java#L20) | 20 | `request.getRemoteAddr()` |
| [AuthController.java](back/blogDemo/src/main/java/com/sangshen/controller/AuthController.java#L42) | 42 | `httpRequest.getRemoteAddr()` |

### 3.2 为什么是 P1

部署到 Nginx 后面时，Java 看到的连接来自 Nginx 本机，`getRemoteAddr()` 永远是 `127.0.0.1`：

- 评论限流：全站共用一个桶 → **每 60 秒只允许一条评论**，功能等于坏了
- 登录限流：全站共用一个桶 → 攻击者**输错 5 次，你自己也登不进来**（账号 DoS）

本地用 `localhost:8080` 直连时完全正常，所以这个坑只会在上线第一天爆。

### 3.3 修法

**第一步**，`application.yml` 和 `application.example.yml` 都加：

```yaml
server:
  forward-headers-strategy: native
```

**第二步**，Nginx 里必须有这一行（否则后端白配）：

```nginx
proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
proxy_set_header X-Forwarded-Proto $scheme;
```

**不要**自己去 `request.getHeader("X-Forwarded-For")` 然后 split 取第一个 —— 客户端能伪造这个头，取错反而更危险。交给容器（`native` 策略）处理。

### 3.4 验证

本地用 `curl.exe -H "X-Forwarded-For: 1.2.3.4" http://localhost:8080/api/auth/login ...`，日志里 `ip = 1.2.3.4` 即生效。

---

## 4. P2：正确性与健壮性

### 4.1 友链并发重复 → 500

[AdminFriendLinkServiceImpl.java:27-29](back/blogDemo/src/main/java/com/sangshen/service/admin/impl/AdminFriendLinkServiceImpl.java#L27-L29) 是「先查再插」（check-then-act），两个请求同时通过检查就会撞 `uk_friend_links_url` 唯一索引 → `DuplicateKeyException` → 无人处理 → **500**。

**修法**：在 `GlobalExceptionHandler` 加

```java
@ExceptionHandler(DuplicateKeyException.class)
public ResponseEntity<Result> handle(DuplicateKeyException e) {
    log.warn("唯一约束冲突：{}", e.getMostSpecificCause().getMessage());
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .body(Result.error("数据已存在，请勿重复提交"));   // 与项目统一的 429 口径
}
```

> 说明：标准语义是 409，但本项目已在接口文档 1.3 约定统一用 429，保持一致。

### 4.2 友链更新不查重 → 500

[AdminFriendLinkMapper.java:21-30](back/blogDemo/src/main/java/com/sangshen/mapper/Admin/AdminFriendLinkMapper.java#L21-L30) 的 `updateFriendLink` 直接改 `url`，不检查是否与其他记录重复。把友链 B 的 URL 改成 A 的 → 唯一索引报错 → 500。

**修法**：更新前执行 `existsUrlExcludingId(url, id)`；或者干脆依赖 4.1 的 `DuplicateKeyException` 处理器兜底（省事，推荐后者）。

### 4.3 友链 DTO 完全没有校验

- [AdminFriendLinkController.java:29](back/blogDemo/src/main/java/com/sangshen/controller/adminController/AdminFriendLinkController.java#L29)、[:36](back/blogDemo/src/main/java/com/sangshen/controller/adminController/AdminFriendLinkController.java#L36) **没有 `@Valid`**
- [AdminAddFriendLink.java](back/blogDemo/src/main/java/com/sangshen/entity/admin/AdminAddFriendLink.java) 一个注解都没有

后果：`name` 传空能建出「无名友链」；`url` 传 `abc` 能存进去，前台渲染成死链；`url` 超 255 → 数据库报错 → 500。

**修法**：

```java
@Data
public class AdminAddFriendLink {
    @NotBlank(message = "站点名称不能为空")
    @Size(max = 64)
    private String name;
    @NotBlank(message = "站点地址不能为空")
    @Size(max = 255)
    @Pattern(regexp = "^https?://.+", message = "地址必须以 http:// 或 https:// 开头")
    private String url;
    @Size(max = 255) private String avatarUrl;
    @Size(max = 200) private String description;
    private Integer sortOrder;
    private Boolean visible;
}
```

Controller 两个方法都加 `@Valid`。

### 4.4 审核 / 删除不存在的评论返回成功

[AdminCommentServiceImpl.java:31-39](back/blogDemo/src/main/java/com/sangshen/service/admin/impl/AdminCommentServiceImpl.java#L31-L39) 直接执行 SQL，不判存在。删不存在的 id → 影响 0 行 → 照样返回「删除成功」。

**修法**：先 `selectById`，为 null 抛 `NotFoundException`（友链那边就是这么写的，保持一致）。

### 4.5 评论只能「通过」，不能「标垃圾」

- 表结构有 `pending / approved / spam` 三态（[schema.sql:58](back/blogDemo/db/schema.sql#L58)）
- 但 [AdminCommentMapper.java:19](back/blogDemo/src/main/java/com/sangshen/mapper/Admin/AdminCommentMapper.java#L19) 写死 `set status = 'approved'`
- 接口是 `PATCH /api/admin/comments/{id}/status`，却没有 `status` 入参 —— 用户以为能选状态，实际只能通过

**修法**：`PATCH` 加一个 `AdminCommentStatusRequest { status }`（draft 里已有的 `CommentStatusResponse` 可以改造），SQL 改成 `set status = #{status}`，并校验取值在 `approved / spam / pending` 之内。

### 4.6 评论限流的三处小问题

| 问题 | 位置 | 影响 |
|---|---|---|
| Map 永不清理 | [CommentServiceImpl.java:24](back/blogDemo/src/main/java/com/sangshen/service/impl/CommentServiceImpl.java#L24) | 长期运行内存只增不减。**登录限流已经写了 `@Scheduled purge()`，照搬过来即可** |
| 先限流后校验 | [CommentServiceImpl.java:32](back/blogDemo/src/main/java/com/sangshen/service/impl/CommentServiceImpl.java#L32) | 文章不存在、URL 非法、内容超长的请求也会消耗配额，用户改两次就直接被 429 |
| 错别字 | [CommentServiceImpl.java:89](back/blogDemo/src/main/java/com/sangshen/service/impl/CommentServiceImpl.java#L89) | 「请60秒后**在**尝试」→「再」 |

**建议顺序**：先做业务校验（文章存在、URL 合法），最后再 `checkRate` + `insert`。这样合法请求才计数。

### 4.7 文章 DTO 缺长度校验

[AdminAddPostItem.java](back/blogDemo/src/main/java/com/sangshen/entity/admin/AdminAddPostItem.java) 只有 `@NotBlank`，而表结构是 `title VARCHAR(120)`、`summary VARCHAR(300)`、`cover_url VARCHAR(255)`。

超长 → 数据库报错 → **500**（而不是 400「标题过长」）。

**修法**：补 `@Size(max = 120)` / `@Size(max = 300)` / `@Size(max = 255)`。

同类问题：`CommentCreateRequest.content` 限制 500，但表是 `VARCHAR(1000)`。**两边统一成一个数**（建议都用 500，表可以不动）。

### 4.8 没有全局异常兜底

[GlobalExceptionHandler.java](back/blogDemo/src/main/java/com/sangshen/common/GlobalExceptionHandler.java) 只处理 5 种异常。任何 NPE / 类型转换 / SQL 异常都会返回 Spring 默认错误体（**没有 `code` 字段**），前端统一报「接口返回格式不正确」。

**修法**：加一个兜底（放在最后，别抢具体异常的处理权）

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<Result> handle(Exception e) {
    log.error("未捕获异常", e);                       // 日志打全栈
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Result.error("服务器开小差了，请稍后再试"));   // 响应别给堆栈
}
```

顺带补两个高频的：`HttpMessageNotReadableException`（JSON 写错）→ 400、`MethodArgumentTypeMismatchException`（`/api/posts/abc`）→ 400。

> **提醒**：先修 2.1 的 P0 再加兜底。否则兜底会把「500 本该是 401」这件事藏得更好看。

### 4.9 浏览量：读接口里做写操作

[PostServiceImpl.java:46-57](back/blogDemo/src/main/java/com/sangshen/service/impl/PostServiceImpl.java#L46-L57)：

- `@Transactional` 包着一次 `UPDATE`，每个详情页访问都写一次库
- 没有防刷：刷新 100 次就是 100 次

**可选修法（按投入排序）**：

1. 去掉 `@Transactional`（单条 UPDATE 自带原子性，不需要事务）
2. 同 IP 10 分钟内只计一次（内存 Map + `@Scheduled` 清理，和限流一个套路）
3. 真要扛量再考虑「内存计数 + 定时回写」，现在不用

### 4.10 `addPost` 的时间字段逻辑不一致

[AdminPostServiceImpl.java:76-83](back/blogDemo/src/main/java/com/sangshen/service/admin/impl/AdminPostServiceImpl.java#L76-L83)：

- 发布：手工设 `publishedAt` + `createdAt`
- 草稿：只设 `updatedAt`，`createdAt` 靠数据库 `DEFAULT CURRENT_TIMESTAMP`

能跑，但两条路走法不同，以后加「定时发布」容易乱。**建议统一**：两个字段都由数据库默认值负责，代码里只设 `publishedAt`。

### 4.11 分页 size 超限直接 400

[PageQueryParam.java:24-26](back/blogDemo/src/main/java/com/sangshen/entity/PageQueryParam.java#L24-L26)：`size=100` → 抛 `IllegalArgumentException` → 400。

前端已经 clamp 了，但接口直连（Apifox、脚本）会撞。两种都行：

- 保持 400（严格，符合接口文档 1.5）
- 或 clamp 到 50（宽容）

**选一个写进文档**，别让代码和文档各说一套。

### 4.12 上传：阈值重复定义 + media 表空转

- [UploadServiceImpl.java:19](back/blogDemo/src/main/java/com/sangshen/service/admin/impl/UploadServiceImpl.java#L19) 硬编码 `MAX_SIZE = 5MB`，`application.yml` 里又有一份 `multipart.max-file-size: 5MB`。改一处忘另一处 → 用户看到「文件过大」但不知道哪个限制触发
- `media` 表建了却**没有任何写入**。上传的图无法列出、无法删除、无法复用
- 只校验前 12 字节（够用），但读取时 `read < 12` 会拒绝合法的极小图（比如 1x1 的 GIF 只有 42 字节，没问题；但 12 字节以下的 png 不存在，实际影响≈0）。可以改成 `read <= 0` 判断

**修法**：图片大小从配置读（`@Value("${spring.servlet.multipart.max-file-size}")` 或自定义 `blog.upload.max-size`），上传成功后往 `media` 插一条记录。

### 4.13 删除文章会连带删评论

[schema.sql:65](back/blogDemo/db/schema.sql#L65) 有 `ON DELETE CASCADE`。这是**设计选择**，不是 bug —— 但你要知道：删文章 = 评论永久消失，且后台不会有任何提示。前端删除确认文案最好写清楚。

---

## 5. P3：代码整洁度

| # | 问题 | 位置 | 说明 |
|---|---|---|---|
| 1 | `Result.success(String)` 重载陷阱 | [Result.java:30](back/blogDemo/src/main/java/com/sangshen/entity/Result.java#L30) | `success("abc")` 走的是「消息」而不是「data」。`Result.success("评论提交成功…")` 实际 `data=null`。建议拆成 `ok(data)` / `ok(data, message)` |
| 2 | 包名大小写不统一 | `controller.adminController`、`service.Auth`、`mapper.Admin` | `Service` 已经改成 `service` 了，剩这三个。Linux 上大小写敏感，趁早统一成全小写 |
| 3 | 死代码 | [TestController.java](back/blogDemo/src/main/java/com/sangshen/controller/TestController.java)（全注释）、[HomeMapper.java](back/blogDemo/src/main/java/com/sangshen/mapper/HomeMapper.java)（`BaseMapper<Home>`，Home 不是表） | 直接删。留着会让人以为「这里有东西」 |
| 4 | 日期注解用错 | [Comment.java:25](back/blogDemo/src/main/java/com/sangshen/entity/Comment.java#L25) 用的是 `@DateTimeFormat` | 那是 Web 入参格式化，不是 Jackson 序列化。`Post` / `FriendLink` / `PublicComment` 都已改 `@JsonFormat`，`Comment` 漏了（目前没接口直接返回它，属于潜在坑） |
| 5 | 文案里带表情 | `Result.success("删除成功(○｀ 3′○)")`、`"更新成功^o^y"` | 学习项目无所谓，但 API 文案建议中性；而且 `AdminFriendLinkController.add` 返回的是 `message="success"`，和接口文档写的「添加成功」不一致 |
| 6 | 日志没有 requestId | 全局 | 加一个 Filter 生成 `traceId` 塞进 MDC，排错时能串起来 |
| 7 | SQL 日志全开 | [application.yml:58](back/blogDemo/src/main/resources/application.yml#L58) `log-impl: StdOutImpl` | 上线前关掉 |
| 8 | 命名 `showComment` / `showComments` 单复混用 | `service/admin` 与 `service/impl` | 同一功能叫法不一致 |
| 9 | `@Transactional` 位置随意 | `AdminPostServiceImpl:89`（注解夹在注释和方法之间） | 虽然能编译，但读起来像「注解贴错地方」 |
| 10 | 无任何自动化测试 | `src/test` 只有一个空壳 | 至少补 3 个冒烟：登录 401、未登录后台 401、评论提交 pending |

---

## 6. 性能（现在不用管，但要知道边界）

| 项 | 现状 | 什么时候会疼 |
|---|---|---|
| 文章搜索 | `LIKE '%keyword%'`，`title` 无索引 | 几千篇以后 |
| 首页 | 3 次独立查询（文章/评论/友链） | 数据量小，无感；真慢再加 Caffeine 缓存 |
| 浏览量 | 每次详情写一次库 | 被刷/爬虫扫描时 |
| 数据库连接池 | Hikari 10 连接 | 单机个人博客绰绰有余 |
| 图片 | 原图直出，无缩略图 | 手机流量、首屏最明显 |

**结论**：除「浏览量」外，**现在都不值得动**。别为了假想的流量上 Redis / 缓存框架。

---

## 7. 后端还缺的能力（按价值排）

| 功能 | 为什么值得 | 成本 |
|---|---|---|
| **改密码接口** | 目前**完全没有**。忘了密码只能进数据库手改，改了还得重启初始化逻辑（实际不会重跑，因为表非空） | 半天 |
| **账号启用/禁用（`enabled`）** | 接口文档遗留项；也是「被入侵后第一件事」 | 半天（含建表） |
| **评论邮件通知** | 现在有人留言你根本不知道，评论功能等于半瘫 | 3h |
| **媒体库（`media` 表）** | 图片只进不出，孤儿文件无法清理 | 1 天 |
| **定时发布** | `published_at` 支持未来时间，查询加一个条件即可 | 半天 |
| **RSS / sitemap.xml** | 博客的命根子，SEO 全靠它 | 2h |
| 健康检查 | `spring-boot-starter-actuator` + `/actuator/health`，给 Nginx/监控探活 | 1h |
| 备份脚本 | `mysqldump` + `uploads/` 打包，定时任务 | 2h |
| 文章标签/分类 | 要 2 张表 + 关联 + 前端筛选，**至少 2 天** | 等文章 > 20 篇再说 |
| 访问统计 | 有了 `view_count` 其实够用 | 不急 |

---

## 8. 明确不建议做的

| 不做 | 原因 |
|---|---|
| Redis | 所有限流都是内存 Map，单机够用到天荒地老 |
| JWT | 已经讨论过：收益是学习，不是「更好」 |
| Docker / K8s | 单机 `jar + systemd` 完全够 |
| 多用户 / 注册 | 个人博客不加戏 |
| 全局搜索（ES / Meili） | `LIKE` 在几百篇量级毫无压力 |
| 提前抽象「通用限流器」 | 现在只有两处，等第三处出现再抽。**重复 20 行 < 提前抽象** |
| 在限流里加验证码 | 没被真打过就别加，加了自己天天点 |

---

## 9. 建议执行顺序

```
第 1 轮（30 分钟 · 止血）
  1. AuthService:31  throw e;                    ← P0，一行
  2. 跑 2.4 的 5 条回归                          ← 确认 401 / 429 都对

第 2 轮（1 小时 · 上线准备）
  3. forward-headers-strategy: native + Nginx 头  ← P1
  4. GlobalExceptionHandler 加兜底 + DuplicateKey/类型不匹配
  5. 好友链/文章 DTO 补 @Valid + @Size

第 3 轮（1.5 小时 · 数据正确性）
  6. 评论审核/删除判存在
  7. 评论支持标 spam
  8. 评论限流：挪到校验之后 + 加 purge + 改错字
  9. 浏览量去掉事务（可选）

第 4 轮（半天 · 功能）
  10. 改密码接口
  11. enabled 字段
  12. 评论邮件通知

第 5 轮（半天 · 收拾屋子）
  13. 包名统一小写、删死代码、Result 重载拆分
  14. 3 个冒烟测试
```

每轮做完：跑一遍手工回归 → commit 一次。**别把「修 bug」和「重命名」混在同一个 commit 里**。

---

## 附录 A：本轮已确认修好（别重复修）

| 项 | 状态 |
|---|---|
| 评论列表用错 id（`post_id = NULL`） | ✅ 已修（[PostServiceImpl.java:72](back/blogDemo/src/main/java/com/sangshen/service/impl/PostServiceImpl.java#L72) 改用路径 id；`CommentQueryParam.id` 已删） |
| 友链重复的 HTTP 语义 | ✅ 已统一为 429，接口文档 1.3 / 5.11 已同步 |
| 登录限流（滑动窗口 + 定时清理 + 成功清零） | ✅ 已实现，但被 2.1 的 P0 拖累 |
| `@EnableScheduling` | ✅ 已加 |
| `AdminPostController` 路径缺前导斜杠（`api/admin/posts`） | ✅ 已修 |
| `FriendLink` 的 `@DateTimeFormat` 用错 | ✅ 已改 `@JsonFormat` |
| 包名 `Service` → `service` | ✅ 已改（剩 `adminController` / `Auth` / `Admin` 未改） |

---

## 附录 B：问题索引（文件:行 → 优先级）

| 文件:行 | 问题 | 级别 |
|---|---|---|
| `service/Auth/AuthService.java:31` | 包装异常导致 401 变 500 | **P0** |
| `controller/AuthController.java:42` | `getRemoteAddr` 取不到真实 IP | **P1** |
| `controller/CommentController.java:20` | 同上 | **P1** |
| `common/GlobalExceptionHandler.java` | 缺 `Exception` / `DuplicateKey` / 类型不匹配兜底 | P2 |
| `service/admin/impl/AdminFriendLinkServiceImpl.java:27` | check-then-act 并发撞唯一索引 | P2 |
| `service/admin/impl/AdminFriendLinkServiceImpl.java:44` | 更新不查重 | P2 |
| `controller/adminController/AdminFriendLinkController.java:29,36` | 缺 `@Valid` | P2 |
| `entity/admin/AdminAddFriendLink.java` | 零校验 | P2 |
| `service/admin/impl/AdminCommentServiceImpl.java:31,37` | 不判存在 | P2 |
| `mapper/Admin/AdminCommentMapper.java:19` | 只能通过，不能标 spam | P2 |
| `service/impl/CommentServiceImpl.java:24,32,89` | Map 不清理 / 限流顺序 / 错字 | P2 |
| `entity/admin/AdminAddPostItem.java` | 缺 `@Size` | P2 |
| `service/impl/PostServiceImpl.java:46` | 详情页带事务写库 | P2 |
| `service/admin/impl/AdminPostServiceImpl.java:76` | 时间字段两条路不一致 | P3 |
| `entity/PageQueryParam.java:24` | size>50 直接 400 | P3 |
| `service/admin/impl/UploadServiceImpl.java:19` | 阈值与 yml 重复 | P3 |
| `entity/Result.java:30` | `success(String)` 重载陷阱 | P3 |
| `controller/TestController.java` | 死代码 | P3 |
| `mapper/HomeMapper.java` | 死代码 | P3 |
| `entity/Comment.java:25` | `@DateTimeFormat` 应为 `@JsonFormat` | P3 |
| `application.yml:58` | SQL 日志全开 | P3（上线前） |
| `src/test` | 无自动化测试 | P3 |
