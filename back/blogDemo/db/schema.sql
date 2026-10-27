-- ============================================================
-- 个人博客数据库结构 v1
-- 适用：MySQL 8.0+
-- 执行：Navicat 里打开本文件 → 右键「运行 SQL 文件」
--       或在查询窗口里全选粘贴执行
-- ============================================================

CREATE DATABASE IF NOT EXISTS `my_blog`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_0900_ai_ci;

USE `my_blog`;

-- ------------------------------------------------------------
-- 1. 管理员账号（单人博客，最终只有 1 行）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `username`      VARCHAR(32)     NOT NULL                COMMENT '登录名',
  `password_hash` VARCHAR(100)    NOT NULL                COMMENT 'BCrypt 哈希，禁止存明文',
  `nickname`      VARCHAR(32)     DEFAULT NULL            COMMENT '显示名',
  `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='管理员账号';

-- ------------------------------------------------------------
-- 2. 文章
-- status: draft（草稿） / published（已发布）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `posts` (
  `id`           BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title`        VARCHAR(120)    NOT NULL                COMMENT '标题',
  `summary`      VARCHAR(300)    DEFAULT NULL            COMMENT '列表页摘要，可空',
  `cover_url`    VARCHAR(255)    DEFAULT NULL            COMMENT '封面图地址',
  `content`      MEDIUMTEXT      NOT NULL                COMMENT 'Markdown 原文',
  `status`       VARCHAR(16)     NOT NULL DEFAULT 'draft' COMMENT 'draft / published',
  `view_count`   INT UNSIGNED    NOT NULL DEFAULT 0      COMMENT '浏览次数',
  `published_at` DATETIME        DEFAULT NULL            COMMENT '发布时间，草稿为 NULL',
  `created_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_posts_status_published` (`status`, `published_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='文章';

-- ------------------------------------------------------------
-- 3. 评论
-- status: pending（待审核） / approved（已通过） / spam（垃圾）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comments` (
  `id`         BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `post_id`    BIGINT UNSIGNED NOT NULL                COMMENT '所属文章',
  `nickname`   VARCHAR(32)     NOT NULL                COMMENT '昵称',
  `email`      VARCHAR(128)    DEFAULT NULL            COMMENT '邮箱，仅后台可见',
  `website`    VARCHAR(255)    DEFAULT NULL            COMMENT '个人网站，可空',
  `content`    VARCHAR(1000)   NOT NULL                COMMENT '评论正文，纯文本',
  `status`     VARCHAR(16)     NOT NULL DEFAULT 'pending' COMMENT 'pending / approved / spam',
  `ip`         VARCHAR(45)     DEFAULT NULL            COMMENT '兼容 IPv6',
  `user_agent` VARCHAR(255)    DEFAULT NULL            COMMENT '浏览器标识，防灌水用',
  `created_at` DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_comments_post` (`post_id`, `status`, `created_at`),
  KEY `idx_comments_status_created` (`status`, `created_at`),
  CONSTRAINT `fk_comments_post` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='评论';

-- ------------------------------------------------------------
-- 4. 友链
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `friend_links` (
  `id`          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name`        VARCHAR(64)     NOT NULL                COMMENT '站点名称',
  `url`         VARCHAR(255)    NOT NULL                COMMENT '站点地址',
  `avatar_url`  VARCHAR(255)    DEFAULT NULL            COMMENT '头像/站点图标',
  `description` VARCHAR(200)    DEFAULT NULL            COMMENT '一句话介绍',
  `sort_order`  INT             NOT NULL DEFAULT 0      COMMENT '排序，越小越靠前',
  `visible`     TINYINT         NOT NULL DEFAULT 1      COMMENT '1 显示 / 0 隐藏',
  `created_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at`  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_friend_links_url` (`url`),
  KEY `idx_friend_links_visible_sort` (`visible`, `sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='友情链接';

-- ------------------------------------------------------------
-- 5. 上传的图片（可选，第一版可以先不建）
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS `media` (
  `id`            BIGINT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键',
  `original_name` VARCHAR(255)    NOT NULL                COMMENT '上传时的原始文件名',
  `url`           VARCHAR(255)    NOT NULL                COMMENT '对外访问地址',
  `mime`          VARCHAR(100)    NOT NULL                COMMENT 'MIME 类型',
  `size_bytes`    BIGINT UNSIGNED NOT NULL                COMMENT '字节数',
  `created_at`    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_media_created` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='上传文件记录';

INSERT INTO posts (title, summary, cover_url, content, status, published_at)
VALUES
    ('我的第一篇博客', '这是摘要', NULL, '# 你好\n\n这是第一篇的正文。', 'published', NOW()),
    ('关于数据库设计', NULL, NULL, '第二篇的正文内容。', 'published', NOW());

INSERT INTO comments (post_id, nickname, content, status, created_at)
VALUES
    (1, '小明', '写得好', 'approved', NOW()),
    (1, '小红', '学到了', 'approved', NOW()),
    (1, '路人', '这篇不行', 'pending', NOW()),
    (1, '广告机', '加微信 xxx', 'spam', NOW());

INSERT INTO friend_links (name, url, avatar_url, description, sort_order, visible)
VALUES
    ('阮一峰的网络日志', 'https://www.ruanyifeng.com/blog/', NULL, '每周分享科技动态', 1, 1),
    ('酷壳 CoolShell', 'https://coolshell.cn/', NULL, '技术、职业与思考', 2, 1),
    ('MDN Web Docs', 'https://developer.mozilla.org/', NULL, '前端开发者的参考书', 3, 1),
    ('Halo', 'https://www.halo.run/', NULL, '开源建站工具', 4, 1),
    ('测试友链（隐藏）', 'https://example.com/', NULL, '这条 visible=0，前台不会显示', 99, 0);