<script setup>
import { computed, onMounted, ref } from 'vue'
import { fetchAdminStats } from '@/api/admin'
import { ApiError } from '@/api/http'

const stats = ref(null)
const loading = ref(true)
const error = ref('')

const cards = computed(() => {
  const data = stats.value || {}
  return [
    {
      key: 'postCount',
      label: '全部文章',
      kicker: 'ALL POSTS',
      value: data.postCount ?? 0,
      tone: 'pink',
    },
    {
      key: 'draftCount',
      label: '草稿箱',
      kicker: 'DRAFTS',
      value: data.draftCount ?? 0,
      tone: 'purple',
    },
    {
      key: 'commentCount',
      label: '评论总数',
      kicker: 'COMMENTS',
      value: data.commentCount ?? 0,
      tone: 'cyan',
    },
    {
      key: 'pendingCommentCount',
      label: '待审核',
      kicker: 'PENDING',
      value: data.pendingCommentCount ?? 0,
      tone: 'amber',
    },
    {
      key: 'friendLinkCount',
      label: '友情链接',
      kicker: 'FRIEND LINKS',
      value: data.friendLinkCount ?? 0,
      tone: 'pink',
    },
  ]
})

async function load() {
  loading.value = true
  error.value = ''
  try {
    stats.value = await fetchAdminStats()
  } catch (caught) {
    error.value =
      caught instanceof ApiError ? caught.message : '后台数据加载失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="admin-page">
    <header class="admin-page-head">
      <div class="admin-page-head__copy">
        <p class="admin-eyebrow">DASHBOARD · 夜色管理室</p>
        <h2 class="admin-page-title">站长，晚上好。</h2>
        <p class="admin-page-lead">
          这里收着文章、评论和友链的灯火。写一点、审核一点，让这座小屋持续亮着。
        </p>
      </div>
      <div class="admin-page-head__actions">
        <RouterLink class="admin-btn admin-btn--primary" :to="{ name: 'admin-post-new' }">
          <span aria-hidden="true">＋</span>
          写新文章
        </RouterLink>
        <RouterLink class="admin-btn" :to="{ name: 'admin-comments' }">
          审核评论
        </RouterLink>
      </div>
    </header>

    <p v-if="error" class="admin-error" role="alert">{{ error }}</p>

    <section class="admin-stats" aria-label="后台数据概览">
      <article
        v-for="card in cards"
        :key="card.key"
        class="admin-stat"
        :class="`admin-stat--${card.tone}`"
      >
        <p class="admin-stat__kicker">{{ card.kicker }}</p>
        <template v-if="loading">
          <div class="admin-skeleton admin-stat__value" style="width: 58%" />
        </template>
        <template v-else>
          <p class="admin-stat__value">{{ card.value }}</p>
        </template>
        <p class="admin-stat__label">{{ card.label }}</p>
      </article>
    </section>

    <section v-if="!loading && stats?.pendingCommentCount > 0" class="admin-pending">
      <div class="admin-pending__copy">
        <span class="admin-pending__mark" aria-hidden="true">◌</span>
        <span>
          还有 <strong class="admin-pending__count">{{ stats.pendingCommentCount }}</strong>
          条留言在门口等待审核。
        </span>
      </div>
      <RouterLink class="admin-btn admin-btn--sm" :to="{ name: 'admin-comments' }">
        去审核 →
      </RouterLink>
    </section>

    <section class="admin-quick" aria-label="快捷操作">
      <RouterLink class="admin-quick__link" :to="{ name: 'admin-posts' }">
        <span class="admin-quick__icon" aria-hidden="true">✎</span>
        <span class="admin-quick__title">文章归档</span>
        <span class="admin-quick__sub">POST ARCHIVE / 查找、编辑、归档</span>
      </RouterLink>
      <RouterLink class="admin-quick__link" :to="{ name: 'admin-comments' }">
        <span class="admin-quick__icon" aria-hidden="true">◌</span>
        <span class="admin-quick__title">评论审核</span>
        <span class="admin-quick__sub">COMMENT QUEUE / 通过或删除</span>
      </RouterLink>
      <RouterLink class="admin-quick__link" :to="{ name: 'admin-friend-links' }">
        <span class="admin-quick__icon" aria-hidden="true">↗</span>
        <span class="admin-quick__title">友链管理</span>
        <span class="admin-quick__sub">FRIEND LINKS / 排序与可见性</span>
      </RouterLink>
    </section>

    <section class="admin-panel">
      <div class="admin-panel-head">
        <div>
          <h3 class="admin-panel-title">今晚可以做什么</h3>
          <p class="admin-panel-sub">A SMALL CHECKLIST FOR THE NIGHT</p>
        </div>
        <button class="admin-btn admin-btn--sm" type="button" :disabled="loading" @click="load">
          {{ loading ? '刷新中…' : '刷新数据' }}
        </button>
      </div>
      <ul class="admin-hint-list">
        <li>写完的文章可以先存为草稿，想好再发布。</li>
        <li>评论默认进入待审核；通过后才会出现在前台。</li>
        <li>友链可以设置排序和可见性，隐藏的不会出现在友链页。</li>
      </ul>
    </section>
  </div>
</template>
