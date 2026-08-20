<template>
  <div class="page-wrap">
    <AppNav />

    <header class="home-header">
      <div class="header-inner">
        <span class="logo" @click="reload">此刻</span>
        <div class="search-box" :class="{ focus: searching }">
          <el-icon :size="14"><Search /></el-icon>
          <input
            v-model="keyword"
            placeholder="搜索笔记、作者"
            @focus="searching = true"
            @keyup.enter="doSearch"
            @keyup.esc="clearSearch"
          />
          <el-icon v-if="keyword" :size="14" class="clear" @click="clearSearch"><Close /></el-icon>
        </div>
      </div>
      <div class="cats">
        <span
          v-for="c in CATEGORIES"
          :key="c"
          class="chip"
          :class="{ on: category === c && !keyword }"
          @click="switchCat(c)"
        >{{ c }}</span>
      </div>
    </header>

    <main class="page-main">
      <p v-if="keyword" class="search-tip">
        「{{ keyword }}」的搜索结果：共 {{ filtered.length }} 条
        <span class="exit" @click="clearSearch">退出搜索</span>
      </p>

      <div v-if="visibleNotes.length" class="masonry" ref="masonryRef">
        <div v-for="(col, ci) in columns" :key="ci" class="mcol">
          <NoteCard v-for="n in col" :key="n.id" :note="n" :column-width="colWidth" />
        </div>
      </div>

      <el-empty v-else description="没有找到相关内容，换个关键词试试吧" />

      <div v-if="visibleNotes.length" class="load-more">
        <el-button v-if="hasMore" :loading="loading" round @click="loadMore">加载更多</el-button>
        <p v-else class="no-more">— 没有更多内容啦 —</p>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { Search, Close } from '@element-plus/icons-vue'
import AppNav from '../components/AppNav.vue'
import NoteCard from '../components/NoteCard.vue'
import { CATEGORIES, NOTES } from '../mock/notes'
import { useDataStore } from '../stores/data'

const data = useDataStore()

const category = ref('推荐')
const keyword = ref('')
const searching = ref(false)
const pageSize = 8
const page = ref(1)
const loading = ref(false)
const colCount = ref(2)
const colWidth = ref(0)
const masonryRef = ref()

// 全部笔记 = 种子数据 + 我发布的（公开）
const allNotes = computed(() => [
  ...data.myNotes.filter((n) => n.visibility === 'public'),
  ...NOTES,
])

const filtered = computed(() => {
  let list = allNotes.value
  if (keyword.value) {
    const k = keyword.value.trim().toLowerCase()
    list = list.filter(
      (n) =>
        n.title.toLowerCase().includes(k) ||
        n.desc.toLowerCase().includes(k) ||
        n.author.name.toLowerCase().includes(k)
    )
  } else if (category.value === '最新') {
    list = [...list]
  } else if (category.value === '热门') {
    list = [...list].sort((a, b) => b.likes - a.likes)
  } else if (category.value !== '推荐') {
    list = list.filter((n) => n.category === category.value)
  }
  return list
})

const visibleNotes = computed(() => filtered.value.slice(0, page.value * pageSize))
const hasMore = computed(() => visibleNotes.value.length < filtered.value.length)

// 瀑布流分列：贪心放入当前最矮的列
const columns = computed(() => {
  const cols = Array.from({ length: colCount.value }, () => [])
  const heights = new Array(colCount.value).fill(0)
  for (const n of visibleNotes.value) {
    const idx = heights.indexOf(Math.min(...heights))
    cols[idx].push(n)
    heights[idx] += n.coverH + 90
  }
  return cols
})

const switchCat = (c) => {
  category.value = c
  keyword.value = ''
  page.value = 1
}
const doSearch = () => { page.value = 1 }
const clearSearch = () => { keyword.value = ''; searching.value = false; page.value = 1 }
const reload = () => { clearSearch(); category.value = '推荐'; window.scrollTo({ top: 0, behavior: 'smooth' }) }

const loadMore = () => {
  loading.value = true
  setTimeout(() => { page.value++; loading.value = false }, 400)
}

// 滚动触底自动加载
const onScroll = () => {
  if (!hasMore.value || loading.value) return
  const { scrollTop, clientHeight, scrollHeight } = document.documentElement
  if (scrollTop + clientHeight >= scrollHeight - 120) loadMore()
}

// 按断点调整瀑布流列数：移动 2 / 平板 3 / 电脑 4
const updateCols = () => {
  const w = window.innerWidth
  colCount.value = w <= 768 ? 2 : w <= 1200 ? 3 : 4
  if (masonryRef.value) {
    const gap = 10
    colWidth.value = Math.floor((masonryRef.value.clientWidth - gap * (colCount.value - 1)) / colCount.value)
  }
}

onMounted(() => {
  updateCols()
  window.addEventListener('resize', updateCols)
  window.addEventListener('scroll', onScroll, { passive: true })
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', updateCols)
  window.removeEventListener('scroll', onScroll)
})
</script>

<style scoped>
.home-header {
  position: sticky;
  top: 0;
  z-index: 90;
  background: #fff;
  border-bottom: 1px solid var(--cike-border);
}
.header-inner {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px 6px;
  max-width: 1080px;
  margin: 0 auto;
}
.logo {
  font-size: 20px;
  font-weight: 800;
  color: var(--cike-primary);
  letter-spacing: 1px;
  cursor: pointer;
  flex-shrink: 0;
}
.search-box {
  flex: 1;
  max-width: 480px;
  height: 34px;
  background: var(--cike-bg);
  border-radius: 17px;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 0 12px;
  color: var(--cike-text-faint);
  border: 1px solid transparent;
  transition: border-color 0.15s;
}
.search-box.focus { border-color: var(--cike-primary); background: #fff; }
.search-box input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  font-size: 13px;
  color: var(--cike-text);
  min-width: 0;
}
.search-box .clear { cursor: pointer; }
.cats {
  display: flex;
  gap: 10px;
  padding: 6px 16px 10px;
  overflow-x: auto;
  max-width: 1080px;
  margin: 0 auto;
  scrollbar-width: none;
}
.cats::-webkit-scrollbar { display: none; }
.chip {
  height: 26px;
  padding: 0 14px;
  border-radius: 13px;
  background: var(--cike-bg);
  font-size: 12px;
  color: var(--cike-text-sub);
  display: inline-flex;
  align-items: center;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.15s;
}
.chip:hover { color: var(--cike-primary); }
.chip.on { background: var(--cike-primary-soft); color: var(--cike-primary); font-weight: 600; }

.page-main { padding: 14px 12px 24px; }
.search-tip { font-size: 13px; color: var(--cike-text-faint); padding: 0 4px 12px; }
.search-tip .exit { color: var(--cike-primary); cursor: pointer; margin-left: 8px; }

.masonry { display: flex; gap: 10px; align-items: flex-start; }
.mcol { flex: 1; display: flex; flex-direction: column; gap: 10px; min-width: 0; }

.load-more { text-align: center; padding: 20px 0 8px; }
.no-more { font-size: 12px; color: #ccc; }

/* 电脑端隐藏 logo（侧边栏已有） */
@media (min-width: 1201px) {
  .logo { display: none; }
  .search-box { max-width: 560px; }
}
</style>
