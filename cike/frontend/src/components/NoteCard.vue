<template>
  <div class="note-card" @click="goDetail">
    <div
      class="cike-cover"
      :style="{ height: coverHeight + 'px', background: `linear-gradient(135deg, ${note.cover[0]}, ${note.cover[1]})` }"
    >
      封面图
    </div>
    <div class="note-body">
      <div class="note-title line-clamp-2">{{ note.title }}</div>
      <div class="note-meta">
        <span class="cike-avatar" :style="avatarStyle">{{ note.author.char }}</span>
        <span class="name">{{ note.author.name }}</span>
        <span class="like" :class="{ on: liked }">
          <el-icon size="12"><component :is="liked ? StarFilled : Star" /></el-icon>
          {{ fmtNum(displayLikes) }}
        </span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { useDataStore } from '../stores/data'
import { fmtNum } from '../mock/notes'

const props = defineProps({
  note: { type: Object, required: true },
  columnWidth: { type: Number, default: 0 },
})

const router = useRouter()
const data = useDataStore()

const liked = computed(() => data.likes.includes(props.note.id))
const displayLikes = computed(() => props.note.likes + (liked.value ? 1 : 0))

// 按列宽等比缩放封面高度，保持瀑布流比例协调
const coverHeight = computed(() => {
  if (!props.columnWidth) return props.note.coverH
  return Math.round(props.note.coverH * (props.columnWidth / 170))
})

const avatarStyle = computed(() => ({
  width: '18px',
  height: '18px',
  fontSize: '9px',
  background: `linear-gradient(135deg, ${props.note.cover[0]}, ${props.note.cover[1]})`,
}))

const goDetail = () => router.push(`/note/${props.note.id}`)
</script>

<style scoped>
.note-card {
  background: var(--cike-card);
  border-radius: var(--cike-radius);
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  break-inside: avoid;
}
.note-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}
.note-body { padding: 8px 10px 10px; }
.note-title { font-size: 13px; font-weight: 600; line-height: 1.4; }
.note-meta { display: flex; align-items: center; gap: 6px; margin-top: 8px; }
.name {
  font-size: 11px;
  color: var(--cike-text-faint);
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.like { font-size: 11px; color: var(--cike-text-faint); display: inline-flex; align-items: center; gap: 3px; }
.like.on { color: var(--cike-primary); }
</style>
