# Session → JWT 登录迁移方案（v1.1 · 已按当前代码核对）

> 版本：v1.1（2026-11-02 重写；v1.0 是初稿）
> 核对对象：`D:\myBlog\back\blogDemo`（Spring Boot 4.1.1 / Spring Security 7.1.1 / JDK 17）
> 前端：`D:\myBlog\frontend`（Vue 3 + axios，走 Vite 代理，同源）
> 结论先放这儿：**可行，后端改动面很小，约 1 个工作日；但"登出立即失效""无感续期"不在这次范围里。**

---

## 0. 先确认一个词

你说的"iwt 登录"，按上下文我理解成 **JWT（JSON Web Token）登录**。如果不是（比如某个公司/平台的统一认证 SSO），告我一声，这份方案的重心要整个换掉。

---

## 1. 一句话结论

**推荐：JWT 放进 HttpOnly Cookie，12 小时，不做 refresh token，Security 切 STATELESS。**

- 前端**零改动**（现在就是靠 Cookie 自动携带，`withCredentials: true` 已经写好了）
- 避开 `localStorage` 被 XSS 读走
- 单人博客不需要"多端打通""无感续期"这些能力

但先说清楚一句实话，免得白高兴：

> **JWT 放 Cookie ≠ 无状态安全。** 它只是把"服务端记着谁登录了"换成"浏览器带着一张自签的票"，票还是走 Cookie。你得到的是「服务端不用存 Session、多实例天然共享」，代价是「登出杀不掉旧票、续期要自己写」。

如果目标只是"部署到单台服务器、少点配置"，**其实 Session 更省事**；如果目标是**学 Token、给简历加一条、为以后多实例/多端留路**，那这次迁移值得做。

---

## 2. 现状盘点（我实际看到的，不是猜的）

| 关注点 | 现状 | 迁移后 |
|---|---|---|
| 登录态载体 | `JSESSIONID` + `HttpSessionSecurityContextRepository` | `access_token` Cookie（JWT） |
| 认证核心 | `AuthenticationManager` + `BCrypt` 比对 | **完全不动** |
| 权限规则 | `SecurityConfig` 里 5 条 matcher | **完全不动** |
| 401 / 403 | `RestAuthenticationEntryPoint` / `RestAccessDeniedHandler` 输出 `Result` JSON | **完全不动** |
| `/api/auth/me` | 从 `SecurityContextHolder` 取 `authentication.getName()` | **完全不动** |
| `/api/auth/logout` | `clearContext()` + `session.invalidate()` | 改成清 Cookie |
| 前端请求 | `apiClient` 带 `withCredentials: true` | **完全不动**（Cookie 模式） |
| 前端登录态恢复 | 刷新页面调 `/auth/me`，失败当未登录 | **完全不动** |
| 部署形态 | Vite 代理 `/api` → `localhost:8080`；生产 Nginx 同域反代 | 同域，**没有跨域 Cookie 问题** |

需要动手的只有 **2 个已有文件 + 3 个新文件 + 1 份配置**：

```
改  back/blogDemo/pom.xml                                  加 jjwt 三个坐标
改  .../resources/application.yml                          加 blog.jwt.*
改  .../config/SecurityConfig.java                         删 Session 仓库，切 STATELESS，挂 Filter
改  .../controller/AuthController.java                     发 Cookie / 清 Cookie，删 saveContext
新  .../config/JwtProperties.java                          密钥、有效期、issuer
新  .../security/JwtService.java                           签发 / 解析
新  .../security/JwtAuthenticationFilter.java              每个请求读 Cookie → SecurityContext
```

`UserDetailsServiceImpl`、`PasswordEncoderConfig`、`AdminInitializer`、`UserMapper`、所有 admin 接口、前台接口：**一行都不用改**。

---

## 3. 三种落地方式，选一个

| | A. Cookie 里的 JWT（推荐） | B. Bearer Token | C. 继续 Session（+Redis） |
|---|---|---|---|
| token 放哪 | HttpOnly Cookie | `Authorization: Bearer`，内存或 localStorage | 服务端 |
| 前端改动 | **0** | 请求拦截器 + 存储 + 刷新页丢 token 问题 | 0 |
| CSRF | 仍有理论风险（`SameSite=Lax` 挡住大部分） | **没有** | 仍有（现在靠 SameSite 兜底） |
| XSS | `HttpOnly` 防读，**不防冒用** | localStorage 方案被读走就等于被登录 | 防读 |
| 登出即时失效 | 做不到（除非黑名单） | 同左 | **能做到** |
| 多实例 | 天然支持 | 天然支持 | 要 Redis |
| 移动端/小程序 | 麻烦 | 友好 | 麻烦 |

- **选 A**：你的场景是同域 SPA + 单人博客，A 的收益/成本比最高。
- **选 B 的唯一理由**：以后要做 App / 小程序 / 前后端分域部署。
- **选 C 的唯一理由**：你的真实需求是"多台服务器共享登录态"——那其实该上 `spring-session-data-redis`，而不是 JWT。

本方案按 **A** 展开，B 的差异放附录 A。

---

## 4. Token 设计

| 项 | 值 |
|---|---|
| 算法 | HS256（对称签名） |
| 有效期 | 12 小时（和现在 `session.timeout: 12h` 对齐） |
| 存放 | Cookie `access_token` |
| Cookie 属性 | `HttpOnly; SameSite=Lax; Path=/; Max-Age=43200`，生产加 `Secure` |
| 密钥 | 至少 32 字节，**走环境变量，不进 git** |

Payload：

```json
{
  "sub": "1",
  "username": "admin",
  "iss": "myblog",
  "iat": 1762000000,
  "exp": 1762043200,
  "jti": "6f1c2a9e-..."
}
```

- `sub`：用户 id；`username`：`/me` 和 `authentication.getName()` 要用
- `jti`：唯一 id，将来做黑名单/主动失效才用得上，现在先留着
- **不放密码、邮箱、手机号**——JWT 只签名不加密，谁都能解开看

**不做 refresh token**。单人博客不需要"15 分钟 access + 7 天 refresh + 轮换 + 重放检测"那一套，12 小时到期重新登录完全可以接受。

---

## 5. 请求流程

### 5.1 登录

```
POST /api/auth/login {username, password}
  → AuthenticationManager.authenticate()        （和现在一模一样）
  → 查 user 拿 id
  → JwtService.generate(id, username)
  → Set-Cookie: access_token=xxx; HttpOnly; SameSite=Lax; Max-Age=43200
  → 200 {code:1, data:{id,username,nickname}}
```

**要删掉的**（`AuthController.java:44-48`）：

```java
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);
securityContextRepository.saveContext(context, httpRequest, httpResponse);
```

### 5.2 访问受保护接口

```
请求带 Cookie: access_token=xxx
  → JwtAuthenticationFilter
      ├─ 没 token         → 当匿名，继续过滤链
      ├─ 签名错/过期      → 清上下文，当匿名，继续过滤链
      └─ 有效             → 构造 Authentication 塞进 SecurityContext
  → AuthorizationFilter 按规则判断
      ├─ 需要登录但匿名   → RestAuthenticationEntryPoint → 401 JSON
      └─ 通过             → Controller
```

**关键：Filter 绝不抛异常。** 否则一个过期 token 会让所有公开接口（文章列表、详情、评论）一起 400/500，前台直接白屏。

### 5.3 登出

```
POST /api/auth/logout
  → Set-Cookie: access_token=; Max-Age=0; Path=/
  → 200 {code:1, "已退出登录"}
```

无状态，服务端删不掉已经发出去的票。清 Cookie 只是让浏览器不再带。**接受"登出后旧 token 在 12 小时内理论上仍可用"**，要真失效见第 9 节。

---

## 6. 逐文件改动（可直接照抄）

### 6.1 `pom.xml` 加依赖

```xml
<!-- JWT：jjwt 0.12.6。Boot 4.1.1 同时管理 Jackson 3 和 Jackson 2（jackson-2-bom 2.21.5），
     jjwt-jackson 依赖的 Jackson 2 不会被版本顶飞 -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.6</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.6</version>
    <scope>runtime</scope>
</dependency>
```

> 这三个包在你的本地仓库 `~/.m2` 里已经有了（0.12.6），断网也能拉起来。
> 不推荐换 Nimbus（`spring-security-oauth2-jose`）：本机没缓存，多一次下载，收益为零。

### 6.2 `application.yml` 加配置

```yaml
blog:
  jwt:
    # 至少 32 字节。上线必须换成环境变量里的随机串，别把默认值带上生产
    secret: ${JWT_SECRET:dev-only-secret-change-me-32bytes-min!!}
    expire-hours: 12
    issuer: myblog
    # 生产 https 后改 true
    cookie-secure: false
```

`server.servlet.session.*` 那几行可以留着（不再生效，无害），也可以删干净。

### 6.3 新增 `config/JwtProperties.java`

```java
package com.sangshen.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "blog.jwt")
public class JwtProperties {
    private String secret;
    private long expireHours = 12;
    private String issuer = "myblog";
    private boolean cookieSecure = false;
}
```

### 6.4 新增 `security/JwtService.java`

```java
package com.sangshen.security;

import com.sangshen.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final Duration ttl;
    private final String issuer;

    public JwtService(JwtProperties props) {
        byte[] secret = props.getSecret().getBytes(StandardCharsets.UTF_8);
        // HS256 要求密钥 >= 256 bit，短了会抛 WeakKeyException，启动就炸
        if (secret.length < 32) {
            throw new IllegalStateException("blog.jwt.secret 至少 32 字节");
        }
        this.key = Keys.hmacShaKeyFor(secret);
        this.ttl = Duration.ofHours(props.getExpireHours());
        this.issuer = props.getIssuer();
    }

    public String generate(Long userId, String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("username", username)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(ttl)))
                .id(UUID.randomUUID().toString())
                .signWith(key)
                .compact();
    }

    /** 解析失败会抛 JwtException，调用方自己决定当匿名还是报错 */
    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Duration ttl() {
        return ttl;
    }
}
```

### 6.5 新增 `security/JwtAuthenticationFilter.java`

```java
package com.sangshen.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String COOKIE_NAME = "access_token";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String token = resolveToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.parse(token);
                var auth = UsernamePasswordAuthenticationToken.authenticated(
                        claims.get("username", String.class),   // 和现在 authentication.getName() 一致
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (JwtException | IllegalArgumentException e) {
                // 过期/被改/格式错 → 当匿名，交给 AuthorizationFilter 决定要不要 401
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (COOKIE_NAME.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
```

### 6.6 改 `config/SecurityConfig.java`

```java
import org.springframework.security.config.http.SessionCreationPolicy;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity,
                                               JwtAuthenticationFilter jwtAuthenticationFilter,
                                               RestAuthenticationEntryPoint restAuthenticationEntryPoint,
                                               RestAccessDeniedHandler restAccessDeniedHandler) throws Exception {
    httpSecurity
            // 无状态：不创建、不读写 HttpSession
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth ->
                    auth.requestMatchers("/api/auth/login").permitAll()
                            .requestMatchers("/api/auth/me", "/api/auth/logout").authenticated()
                            .requestMatchers("/api/admin/**").authenticated()
                            .requestMatchers("/api/**", "/error", "/uploads/**").permitAll()
                            .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint)
                    .accessDeniedHandler(restAccessDeniedHandler))
            // 放在用户名密码过滤器之前，任何请求先过它
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return httpSecurity.build();
}
```

同时**删掉**：
- `.securityContext(sc -> sc.securityContextRepository(securityContextRepository))`
- 整个 `securityContextRepository()` Bean 和相关 import
- 方法参数里的 `SecurityContextRepository`

> 为什么不用再配 `SecurityContextRepository`：`STATELESS` 会让 Security 用 `NullSecurityContextRepository`，压根不落盘；Filter 塞进 `SecurityContextHolder` 的认证信息在同一次请求里对下游 `AuthorizationFilter` 立即可见，请求结束框架自动清空。

### 6.7 改 `controller/AuthController.java`

```java
// 登录
Authentication authentication = authService.login(request.getUsername(), request.getPassword());
User user = userMapper.selectUserByName(request.getUsername());
String token = jwtService.generate(user.getId(), user.getUsername());

ResponseCookie cookie = ResponseCookie.from(JwtAuthenticationFilter.COOKIE_NAME, token)
        .httpOnly(true)
        .sameSite("Lax")
        .path("/")
        .maxAge(jwtService.ttl())
        .secure(jwtProperties.isCookieSecure())
        .build();

UserInfo info = new UserInfo(user.getId(), user.getUsername(), user.getNickname());
return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body(Result.success(info));
```

```java
// 登出
@PostMapping("/logout")
public ResponseEntity<Result> logout() {
    ResponseCookie cookie = ResponseCookie.from(JwtAuthenticationFilter.COOKIE_NAME, "")
            .httpOnly(true).sameSite("Lax").path("/").maxAge(0)
            .secure(jwtProperties.isCookieSecure())
            .build();
    return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(Result.success("已退出登录"));
}
```

- 删掉 `HttpSession` / `SecurityContextRepository` / `SecurityContextHolder`（`/me` 里的不删）
- `me()` 保持原样，`authentication.getName()` 拿到的还是 username

### 6.8 前端

**代码零改动。** 只有两处文案过时了，顺手改：

- `frontend/src/views/LoginView.vue:171` — "登录态由 HttpOnly Session Cookie 保持" → "登录态由 HttpOnly JWT Cookie 保持"
- `frontend/src/views/admin/AdminLayout.vue:194` — `SESSION ACTIVE` → `TOKEN ACTIVE`

### 6.9 文档同步

- `接口文档.md` 4.1 / 4.2 / 顶部说明：`JSESSIONID` → `access_token`，并注明"登出只清浏览器 Cookie，服务端不保存吊销记录"
- `开发路线图.md` 第 8 节"不要做的事"里"上 JWT"这条，改成"已选 JWT，不再切回 Session"

---

## 7. 实施顺序（每步都能跑起来）

| 阶段 | 干什么 | 验收 |
|---|---|---|
| 1（1h） | 开分支 `feature/jwt`；加依赖 + 配置；写 `JwtProperties` + `JwtService` | `mvnw compile` 通过；写个临时测试打印 token，能解析出 username |
| 2（1h） | 写 `JwtAuthenticationFilter`；`SecurityConfig` 切 STATELESS、挂 Filter、删仓库 | 启动无报错；无 token 访问 `/api/admin/ping` 仍 401 JSON |
| 3（1h） | 改 `AuthController` 登录/登出 | 登录响应头有 `Set-Cookie: access_token=...`；带 Cookie 请求 `/api/auth/me` 200 |
| 4（1h） | 前端联调 + 全量回归（见第 8 节） | 24 个接口逐条过（见 `接口文档.md` 第 2 节） |
| 5（0.5h） | 文档同步、commit | `git log` 干净 |

**总工期：约 1 个工作日（按你每天 2～3 小时的节奏，两个晚上）。**

---

## 8. 测试清单

```http
### 1. 未登录访问后台 → 401 JSON
GET http://localhost:8080/api/admin/ping

### 2. 错误密码 → 401 "用户名或密码错误"
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{"username":"admin","password":"wrong"}

### 3. 正确登录 → 200 + Set-Cookie: access_token
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{"username":"admin","password":"你的密码"}

### 4. 当前用户 → 200
GET http://localhost:8080/api/auth/me

### 5. 后台接口 → 200
GET http://localhost:8080/api/admin/ping

### 6. 登出 → 200 + Set-Cookie: access_token=; Max-Age=0
POST http://localhost:8080/api/auth/logout

### 7. 再访问 → 401
GET http://localhost:8080/api/auth/me
```

必须额外验证的：

- [ ] 篡改 token 一个字符 → 401（不是 500）
- [ ] 过期 token（`expire-hours` 临时改成 0）→ 访问公开接口仍是 200，访问 `/api/auth/me` 是 401
- [ ] 前台匿名接口（文章列表/详情/评论/友链/首页）全部 200
- [ ] 上传接口带 Cookie 可用
- [ ] 刷新页面后 `/me` 恢复登录态（前端 `auth.restore()` 逻辑不变）
- [ ] 响应体里没有 `password` / `passwordHash` / 完整 token

---

## 9. 风险与坑（针对你这套代码）

| 风险 | 说明 | 处理 |
|---|---|---|
| **登出杀不掉旧票** | JWT 无状态，清 Cookie 后旧 token 12 小时内仍有效 | 接受；或加 jti 内存黑名单（见 9.1） |
| **密钥必须稳定** | devtools 热重启不会换密钥，但你一改 `secret`，所有已登录用户立刻掉线 | 密钥放环境变量，改了就是全体重登 |
| **密钥长度** | HS256 要求 ≥ 32 字节，短了启动直接抛 `WeakKeyException` | `JwtService` 构造里先校验（已写） |
| **CSRF 仍存在** | Cookie 自动携带，`SameSite=Lax` 能挡住跨站 POST，挡不住顶层 GET 导航 | 后台全是 POST/PUT/DELETE 且无副作用 GET，够用；想彻底解决要 Bearer |
| **前后端分域就废** | 以后前端 `app.xx.com`、接口 `api.xx.com`，`SameSite=Lax` 不再携带 Cookie | 到那天换 `SameSite=None; Secure` 或改 Bearer |
| **JWT ≠ 加密** | payload 谁都能 base64 解开 | 不放敏感信息（已写） |
| **无感续期缺失** | 连续用 12 小时才断，但正好干到一半掉线会烦 | 可选：Filter 里剩余 < 2h 时顺手重发 Cookie（滑动续期，10 行） |
| **`STATELESS` 与旧 Session 并存** | 迁移中如果有人还带旧 `JSESSIONID`，会被忽略 | 正常现象；上线后可以不管 |
| **Boot 4 的 Jackson 3** | Boot 4.1.1 默认 Jackson 3（`tools.jackson`），而 jjwt-jackson 要 Jackson 2 | Boot 的 `jackson-2-bom` 仍管理到 2.21.5，两套共存没问题（已核对 pom） |

### 9.1 可选：登出立即失效（jti 黑名单）

```
logout 时：解析 token 拿 jti + exp，写进 ConcurrentHashMap<String, Long>
Filter 校验时：jti 在表里 → 拒绝
定时/惰性清理：清掉 exp 已过的条目
```

单机内存版约 30 行，重启即清空。**建议先不做**，等真有"我要随时把谁踢下线"的需求再说，上多实例时天然要换 Redis。

---

## 10. 回滚

保持 `feature/jwt` 分支独立。做完发现下面任意一条，直接切回 `main`，Session 版本一直在：

- 401 行为不符合预期，前端到处弹"登录已过期"
- 你更想要"立刻踢人下线"
- 联调时间超出预期

```bash
git checkout main
```

---

## 11. 到底值不值得（老实说）

| 你的真实动机 | 建议 |
|---|---|
| 想学 Token / 简历上有"JWT 认证"这一条 | **做**，方案 A，一天搞定 |
| 想让部署简单点 | 别做。单机 Session 更简单，改完你还得管密钥、管续期 |
| 以后要上多台服务器 | 做，但顺便考虑 `spring-session-data-redis`，那才是"共享登录态"的正解 |
| 以后要做 App / 小程序 | 做，但选 **Bearer**（附录 A），别选 Cookie |
| 只是听说"现在都用 JWT" | 先别做。你现在的 Session + 401 JSON + BCrypt 已经是很规范的实现了 |

---

## 附录 A：Bearer Token 方案（备选）

| 项 | Cookie 方案 | Bearer 方案 |
|---|---|---|
| token 放哪 | HttpOnly Cookie | `Authorization: Bearer xxx` |
| 前端存储 | 不用管 | 内存（刷新即失）或 localStorage（有 XSS 风险） |
| 前端改动 | 0 | 请求拦截器 + 存储 + 刷新页恢复 |
| CSRF | 有（SameSite 缓解） | 无 |
| 跨域/移动端 | 麻烦 | 友好 |

Bearer 需要额外做的：

1. 登录响应体里直接返回 `token`
2. `frontend/src/api/http.js` 加请求拦截器：`config.headers.Authorization = 'Bearer ' + token`
3. 刷新页面 token 丢失 → 要么加 refresh token，要么每次刷新都重新登录（不可接受）
4. 若存 localStorage，接受 XSS 风险

**除了要做 App/小程序，别选 B。**

---

## 附录 B：一页检查表

```
[ ] 开分支 feature/jwt
[ ] pom：jjwt-api / jjwt-impl / jjwt-jackson 0.12.6
[ ] application.yml：blog.jwt.secret(≥32字节) / expire-hours / issuer / cookie-secure
[ ] JwtProperties
[ ] JwtService（generate / parse / ttl）
[ ] JwtAuthenticationFilter（读 Cookie，失败当匿名）
[ ] SecurityConfig：STATELESS + addFilterBefore，删 SecurityContextRepository
[ ] AuthController.login：发 Set-Cookie，删 saveContext
[ ] AuthController.logout：清 Cookie，删 session.invalidate()
[ ] 前端两处文案
[ ] 接口文档 4.1/4.2 更新
[ ] 24 个接口回归 + 篡改/过期 token 用例
[ ] 决定合并 main 还是回滚
```
