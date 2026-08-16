<template>
  <div class="page-wrap">
    <AppNav />
    <main class="page-main publish-main">
      <div class="nav-row">
        <el-icon :size="22" class="back" @click="goBack"><ArrowLeft /></el-icon>
        <span class="nav-title">发布笔记</span>
        <el-button type="primary" round size="small" :disabled="!canPublish" :loading="publishing" @click="publish">
          发布
        </el-button>
      </div>

      <div class="editor">
        <!-- 图片上传区 -->
        <div class="img-section">
          <div class="img-grid">
            <div
              v-for="(img, i) in images"
              :key="i"
              class="img-cell"
              :style="{ background: `linear-gradient(135deg, ${img.from}, ${img.to})` }"
              draggable="true"
              @dragstart="dragFrom = i"
              @dragover.prevent
              @drop="onDrop(i)"
            >
              <template v-if="img.url"><img :src="img.url" class="real-img" alt="" /></template>
              <span v-else class="img-label">{{ img.label }}</span>
              <span class="del" @click.stop="removeImg(i)"><el-icon :size="10"><Close /></el-icon></span>
              <span class="order">{{ i + 1 }}</span>
            </div>
            <div v-if="images.length < 9" class="img-add" @click="fileInput.click()">
              <el-icon :size="22"><Plus /></el-icon>
              <span>添加图片</span>
              <span class="tip">{{ images.length }}/9</span>
            </div>
          </div>
          <input ref="fileInput" type="file" accept="image/*" multiple hidden @change="onFiles" />
          <p class="img-hint">最多 9 张，支持拖拽排序（电脑端可直接拖动，移动端按住拖动）</p>
        </div>

        <!-- 文字编辑区 -->
        <div class="form-section">
          <input v-model="title" class="title-input" placeholder="填写标题，会有更多人看到哦～" maxlength="40" />

          <textarea
            v-model="content"
            class="content-input"
            placeholder="分享此刻的想法与美好…"
            rows="5"
            maxlength="1000"
          ></textarea>

          <div class="topic-row">
            <el-select
              v-model="topics"
              multiple
              filterable
              allow-create
              default-first-option
              placeholder="# 添加话题"
              :max-collapse-tags="3"
              style="width: 100%"
            >
              <el-option v-for="t in HOT_TOPICS" :key="t" :label="'# ' + t" :value="t" />
            </el-select>
          </div>

          <div class="perm-row">
            <div class="perm" :class="{ on: visibility === 'public' }" @click="visibility = 'public'">
              <el-icon><Sunny /></el-icon> 公开
            </div>
            <div class="perm" :class="{ on: visibility === 'private' }" @click="visibility = 'private'">
              <el-icon><Lock /></el-icon> 仅自己可见
            </div>
          </div>

          <p class="save-tip" :class="{ show: savedTip }">草稿已自动保存 {{ savedTip }}</p>
        </div>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Close, Plus, Sunny, Lock } from '@element-plus/icons-vue'
import AppNav from '../components/AppNav.vue'
import { HOT_TOPICS } from '../mock/notes'
import { useDataStore } from '../stores/data'
import { useUserStore } from '../stores/user'

const router = useRouter()
const data = useDataStore()
const userStore = useUserStore()

const DRAFT_KEY = 'cike_draft'

const images = ref([])
const title = ref('')
const content = ref('')
const topics = ref([])
const visibility = ref('public')
const publishing = ref(false)
const savedTip = ref('')
const dragFrom = ref(-1)
const fileInput = ref()

const canPublish = computed(() => images.value.length > 0 && title.value.trim().length > 0)

// 读取本地图片为 dataURL 预览
const onFiles = (e) => {
  const files = [...e.target.files]
  e.target.value = ''
  const rest = 9 - images.value.length
  if (files.length > rest) ElMessage.warning(`最多还能添加 ${rest} 张图片`)
  files.slice(0, rest).forEach((f) => {
    if (f.size > 10 * 1024 * 1024) {
      ElMessage.warning(`「${f.name}」超过 10MB，已跳过`)
      return
    }
    const reader = new FileReader()
    reader.onload = () => {
      images.value.push({ url: reader.result, label: f.name })
    }
    reader.readAsDataURL(f)
  })
}

const removeImg = (i) => images.value.splice(i, 1)

const onDrop = (to) => {
  const from = dragFrom.value
  if (from < 0 || from === to) return
  const [moved] = images.value.splice(from, 1)
  images.value.splice(to, 0, moved)
  dragFrom.value = -1
}

// 草稿：变更后自动保存，进入页面时恢复
let saveTimer = null
watch([images, title, content, topics, visibility], () => {
  clearTimeout(saveTimer)
  saveTimer = setTimeout(() => {
    localStorage.setItem(DRAFT_KEY, JSON.stringify({
      images: images.value.filter((i) => !i.url || i.url.length < 200000),
      title: title.value, content: content.value,
      topics: topics.value, visibility: visibility.value,
    }))
    const d = new Date()
    savedTip.value = `${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}:${String(d.getSeconds()).padStart(2, '0')}`
  }, 600)
}, { deep: true })

onMounted(() => {
  try {
    const draft = JSON.parse(localStorage.getItem(DRAFT_KEY))
    if (draft && (draft.title || draft.content || draft.images?.length)) {
      images.value = draft.images || []
      title.value = draft.title || ''
      content.value = draft.content || ''
      topics.value = draft.topics || []
      visibility.value = draft.visibility || 'public'
      ElMessage.info('已恢复上次未完成的草稿')
    }
  } catch { /* 草稿损坏时忽略 */ }
})

const goBack = async () => {
  if (title.value || content.value || images.value.length) {
    try {
      await ElMessageBox.confirm('草稿已自动保存，确认离开吗？', '提示', {
        confirmButtonText: '离开',
        cancelButtonText: '继续编辑',
        type: 'warning',
      })
    } catch {
      return
    }
  }
  router.back()
}

const publish = () => {
  if (!canPublish.value) {
    ElMessage.warning(images.value.length === 0 ? '请先上传至少一张图片' : '请填写笔记标题')
    return
  }
  publishing.value = true
  // 模拟提交审核
  setTimeout(() => {
    publishing.value = false
    const first = images.value[0]
    data.addNote({
      id: Date.now(),
      title: title.value.trim(),
      desc: content.value.trim() || '这个人很懒，什么都没写。',
      category: topics.value[0] && ['美食', '穿搭', '风景', '干货'].includes(topics.value[0]) ? topics.value[0] : '推荐',
      likes: 0,
      collects: 0,
      commentCount: 0,
      author: {
        name: userStore.user?.nickname || '我',
        char: userStore.user?.avatarChar || '我',
        bio: userStore.user?.bio || '',
      },
      cover: first.url ? ['#f6b352', '#e2703a'] : [first.from || '#f6b352', first.to || '#e2703a'],
      coverUrl: first.url || '',
      coverH: 140,
      topics: topics.value,
      time: '刚刚',
      images: images.value.map((img, i) =>
        img.url
          ? { url: img.url, label: `图 ${i + 1}` }
          : { from: img.from || '#f6b352', to: img.to || '#e2703a', label: img.label }
      ),
      visibility: visibility.value,
      mine: true,
    })
    localStorage.removeItem(DRAFT_KEY)
    ElMessage.success('发布成功，审核通过后将公开展示')
    router.push('/profile')
  }, 800)
}
</script>

<style scoped>
.publish-main { padding: 0 16px 32px; }
.nav-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 0 18px;
}
.back { cursor: pointer; color: var(--cike-text); }
.nav-title { font-size: 15px; font-weight: 700; }

.editor { display: flex; flex-direction: column; gap: 20px; }

.img-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
.img-cell {
  aspect-ratio: 1;
  border-radius: 10px;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.9);
  font-size: 12px;
  cursor: grab;
  overflow: hidden;
}
.real-img { width: 100%; height: 100%; object-fit: cover; position: absolute; inset: 0; }
.img-label { position: relative; z-index: 1; padding: 0 6px; text-align: center; word-break: break-all; }
.del {
  position: absolute;
  top: 5px;
  right: 5px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 2;
}
.order {
  position: absolute;
  bottom: 5px;
  left: 5px;
  font-size: 10px;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  border-radius: 8px;
  padding: 1px 7px;
  z-index: 2;
}
.img-add {
  aspect-ratio: 1;
  border-radius: 10px;
  border: 1.5px dashed #ddd;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 3px;
  color: #bbb;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.15s;
}
.img-add:hover { border-color: var(--cike-primary); color: var(--cike-primary); }
.img-add .tip { font-size: 10px; opacity: 0.7; }
.img-hint { font-size: 11px; color: #ccc; margin-top: 8px; }

.form-section { display: flex; flex-direction: column; }
.title-input {
  border: none;
  border-bottom: 1px solid var(--cike-border);
  outline: none;
  font-size: 16px;
  font-weight: 600;
  padding: 12px 2px;
  color: var(--cike-text);
  background: transparent;
}
.content-input {
  border: none;
  outline: none;
  resize: vertical;
  font-size: 14px;
  font-family: inherit;
  line-height: 1.8;
  padding: 14px 2px;
  color: var(--cike-text);
  background: transparent;
  min-height: 100px;
}
.topic-row { padding: 8px 0 4px; }
.perm-row { display: flex; gap: 10px; padding: 14px 0; }
.perm {
  flex: 1;
  height: 40px;
  border-radius: 10px;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: var(--cike-text-sub);
  cursor: pointer;
  transition: all 0.15s;
}
.perm.on { background: var(--cike-primary-soft); color: var(--cike-primary); font-weight: 600; }
.save-tip { font-size: 11px; color: #ccc; opacity: 0; transition: opacity 0.3s; }
.save-tip.show { opacity: 1; }

/* 电脑端：左右分栏 */
@media (min-width: 1201px) {
  .editor { flex-direction: row; align-items: flex-start; gap: 40px; }
  .img-section { flex: 0 0 420px; position: sticky; top: 24px; }
  .form-section { flex: 1; background: #fff; border-radius: 16px; padding: 8px 24px 20px; }
}
/* 平板端 */
@media (min-width: 769px) and (max-width: 1200px) {
  .editor { flex-direction: row; align-items: flex-start; gap: 28px; }
  .img-section { flex: 0 0 320px; }
  .form-section { flex: 1; }
}
</style>
