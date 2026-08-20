<template>
  <!-- 移动端 / 平板端：底部 tab 导航 -->
  <nav class="tabbar">
    <router-link
      v-for="t in tabs"
      :key="t.name"
      :to="t.path"
      class="tab"
      :class="{ on: isActive(t) }"
    >
      <el-icon :size="20"><component :is="t.icon" /></el-icon>
      <span>{{ t.label }}</span>
    </router-link>
  </nav>

  <!-- 电脑端：左侧固定导航 -->
  <aside class="sidenav">
    <div class="side-logo" @click="$router.push('/')">此刻</div>
    <router-link
      v-for="t in tabs"
      :key="t.name"
      :to="t.path"
      class="side-item"
      :class="{ on: isActive(t) }"
    >
      <el-icon :size="20"><component :is="t.icon" /></el-icon>
      <span>{{ t.label }}</span>
    </router-link>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'
import { HomeFilled, CirclePlus, User } from '@element-plus/icons-vue'

const route = useRoute()

const tabs = [
  { name: 'home', path: '/', label: '首页', icon: HomeFilled },
  { name: 'publish', path: '/publish', label: '发布', icon: CirclePlus },
  { name: 'profile', path: '/profile', label: '我的', icon: User },
]

const isActive = (t) => route.name === t.name
</script>

<style scoped>
/* 底部 tab：移动端/平板端显示 */
.tabbar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: calc(56px + env(safe-area-inset-bottom));
  padding-bottom: env(safe-area-inset-bottom);
  background: #fff;
  border-top: 1px solid var(--cike-border);
  display: flex;
  z-index: 100;
}
.tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  font-size: 10px;
  color: var(--cike-text-faint);
  transition: color 0.15s;
}
.tab.on { color: var(--cike-primary); }

/* 侧边导航：默认隐藏，电脑端显示 */
.sidenav { display: none; }

@media (min-width: 1201px) {
  .tabbar { display: none; }
  .sidenav {
    display: flex;
    flex-direction: column;
    gap: 8px;
    position: fixed;
    left: 0;
    top: 0;
    bottom: 0;
    width: 220px;
    background: #fff;
    border-right: 1px solid var(--cike-border);
    padding: 24px 16px;
    z-index: 100;
  }
  .side-logo {
    font-size: 26px;
    font-weight: 800;
    color: var(--cike-primary);
    letter-spacing: 2px;
    padding: 8px 12px 24px;
    cursor: pointer;
  }
  .side-item {
    display: flex;
    align-items: center;
    gap: 12px;
    padding: 12px 16px;
    border-radius: 12px;
    font-size: 15px;
    color: var(--cike-text-sub);
    transition: all 0.15s;
  }
  .side-item:hover { background: var(--cike-bg); color: var(--cike-text); }
  .side-item.on {
    background: var(--cike-primary-soft);
    color: var(--cike-primary);
    font-weight: 600;
  }
}
</style>
