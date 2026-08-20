<template>
  <div class="page-wrap">
    <AppNav />
    <main class="page-main detail-main" v-if="note">
      <div class="detail-layout">
        <!-- 左：图文内容 -->
        <div class="content-col">
          <div class="img-viewer" :style="viewerStyle">
            <img v-if="currentImage.url" :src="currentImage.url" class="viewer-img" alt="" />
            <span v-else class="viewer-label">{{ currentImage.label }} · {{ imgIndex + 1 }}/{{ note.images.length }}</span>
            <span class="viewer-back" @click="$router.back()"><el-icon :size="18"><ArrowLeft /></el-icon></span>
            <template v-if="note.images.length > 1">
              <span class="viewer-arrow left" @click="prevImg"><el-icon :size="18"><ArrowLeft /></el-icon></span>
              <span class="viewer-arrow right" @click="nextImg"><el-icon :size="18"><ArrowRight /></el-icon></span>
              <span class="viewer-dots">
                <i v-for="(img, i) in note.images" :key="i" :class="{ on: i === imgIndex }" @click="imgIndex = i"></i>
              </span>
            </template>
          </div>

          <div class="body-card">
            <h1 class="d-title">{{ note.title }}</h1>
            <p class="d-text">{{ note.desc }}</p>
            <div class="d-topics">
              <span v-for="t in note.topics" :key="t" class="topic" @click="searchTopic(t)"># {{ t }}</span>
            </div>
            <p class="d-time">发布于 {{ note.time }}</p>

            <div v-if="isMine" class="mine-ops">
              <el-button size="small" round @click="editNote">编辑</el-button>
              <el-button size="small" round type="danger" plain @click="deleteNote">删除</el-button>
            </div>
          </div>

          <!-- 相关推荐 -->
          <div class="related" v-if="related.length">
            <h3 class="related-title">相关推荐</h3>
            <div class="related-grid">
              <NoteCard v-for="n in related" :key="n.id" :note="n" />
            </div>
          </div>
        </div>

        <!-- 右：作者 + 互动 + 评论（移动端显示在下方） -->
        <aside class="side-col">
          <div class="author-card">
            <span class="cike-avatar" :style="authorAvatar">{{ note.author.char }}</span>
            <div class="a-info">
              <div class="a-name">{{ note.author.name }}</div>
              <div class="a-bio">{{ note.author.bio || '这个人还没有简介' }}</div>
            </div>
            <el-button
              v-if="!isMine"
              :type="followed ? 'info' : 'primary'"
              round
              size="small"
              :plain="followed"
              @click="toggleFollow"
            >{{ followed ? '已关注' : '+ 关注' }}</el-button>
          </div>

          <div class="action-card">
            <div class="act" :class="{ on: liked }" @click="toggleLike">
              <el-icon :size="22"><component :is="liked ? StarFilled : Star" /></el-icon>
              <span>{{ fmtNum(displayLikes) }}</span>
              <em>{{ liked ? '已点赞' : '点赞' }}</em>
            </div>
            <div class="act" :class="{ on: collected }" @click="toggleCollect">
              <el-icon :size="22"><component :is="collected ? CollectionTag : Collection" /></el-icon>
              <span>{{ fmtNum(displayCollects) }}</span>
              <em>{{ collected ? '已收藏' : '收藏' }}</em>
            </div>
            <div class="act" @click="focusComment">
              <el-icon :size="22"><ChatDotRound /></el-icon>
              <span>{{ allComments.length }}</span>
              <em>评论</em>
            </div>
          </div>

          <div class="comment-card" ref="commentRef">
            <h4>评论 · {{ allComments.length }}</h4>
            <div class="comment-input">
              <el-input
                v-model="commentText"
                :placeholder="replyTo ? `回复 ${replyTo.name}：` : '说点什么…'"
                maxlength="200"
                @keyup.enter="sendComment"
              >
                <template #append>
                  <el-button type="primary" link :disabled="!commentText.trim()" @click="sendComment">发送</el-button>
                </template>
              </el-input>
              <p v-if="replyTo" class="reply-tip">
                正在回复 {{ replyTo.name }} <span @click="replyTo = null">取消</span>
              </p>
            </div>
            <div v-if="allComments.length" class="comment-list">
              <div v-for="c in allComments" :key="c.id" class="comment">
                <span class="cike-avatar c-avatar">{{ c.char }}</span>
                <div class="c-main">
                  <div class="c-name">{{ c.name }}<span v-if="c.mine" class="mine-tag">我</span></div>
                  <div class="c-text">
                    <template v-if="c.replyTo">回复 <b>@{{ c.replyTo }}</b>：</template>{{ c.text }}
                  </div>
                  <div class="c-foot">
                    <span>{{ c.time }}</span>
                    <span class="c-op" @click="replyTo = { name: c.name, id: c.id }">回复</span>
                    <span v-if="c.mine" class="c-op del" @click="removeComment(c.id)">删除</span>
                  </div>
                </div>
              </div>
            </div>
            <p v-else class="no-comment">还没有评论，来抢沙发～</p>
          </div>
        </aside>
      </div>
    </main>

    <el-empty v-else description="笔记不存在或已被删除">
      <el-button type="primary" round @click="$router.push('/')">回到首页</el-button>
    </el-empty>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, ArrowRight, Star, StarFilled, Collection, CollectionTag, ChatDotRound,
} from '@element-plus/icons-vue'
import AppNav from '../components/AppNav.vue'
import NoteCard from '../components/NoteCard.vue'
import { NOTES, SEED_COMMENTS, fmtNum } from '../mock/notes'
import { useDataStore } from '../stores/data'
import { useUserStore } from '../stores/user'

const route = useRoute()
const router = useRouter()
const data = useDataStore()
const userStore = useUserStore()

const noteId = Number(route.params.id)
const note = computed(() =>
  data.myNotes.find((n) => n.id === noteId) || NOTES.find((n) => n.id === noteId)
)

const imgIndex = ref(0)
const currentImage = computed(() => note.value?.images[imgIndex.value] || {})
const viewerStyle = computed(() => {
  const img = currentImage.value
  if (img.url) return { background: '#111' }
  return { background: `linear-gradient(135deg, ${img.from}, ${img.to})` }
})
const prevImg = () => { imgIndex.value = (imgIndex.value - 1 + note.value.images.length) % note.value.images.length }
const nextImg = () => { imgIndex.value = (imgIndex.value + 1) % note.value.images.length }

const isMine = computed(() => !!note.value?.mine)
const liked = computed(() => data.likes.includes(noteId))
const collected = computed(() => data.collects.includes(noteId))
const followed = computed(() => data.follows.includes(note.value?.author.name))
const displayLikes = computed(() => (note.value?.likes || 0) + (liked.value ? 1 : 0))
const displayCollects = computed(() => (note.value?.collects || 0) + (collected.value ? 1 : 0))

const requireLogin = () => {
  if (userStore.isLogin) return true
  ElMessage.warning('请先登录')
  router.push({ path: '/login', query: { redirect: route.fullPath } })
  return false
}

const toggleLike = () => { if (requireLogin()) data.toggleLike(noteId) }
const toggleCollect = () => {
  if (!requireLogin()) return
  const added = data.toggleCollect(noteId)
  ElMessage.success(added ? '收藏成功' : '已取消收藏')
}
const toggleFollow = () => {
  if (!requireLogin()) return
  const added = data.toggleFollow(note.value.author.name)
  ElMessage.success(added ? `已关注 ${note.value.author.name}` : '已取消关注')
}

const authorAvatar = computed(() => ({
  width: '44px', height: '44px', fontSize: '16px',
  background: note.value?.coverUrl
    ? 'linear-gradient(135deg,#ff2e51,#ff7a59)'
    : `linear-gradient(135deg, ${note.value?.cover[0]}, ${note.value?.cover[1]})`,
}))

// 评论：种子评论 + 本地新增
const commentText = ref('')
const replyTo = ref(null)
const commentRef = ref()
const allComments = computed(() => {
  const local = data.comments[noteId] || []
  const seed = SEED_COMMENTS.map((c, i) => ({ ...c, id: 'seed-' + i }))
  return [...local, ...seed]
})
const focusComment = () => commentRef.value?.scrollIntoView({ behavior: 'smooth' })
const sendComment = () => {
  if (!requireLogin()) return
  const text = commentText.value.trim()
  if (!text) return
  data.addComment(noteId, {
    id: Date.now(),
    name: userStore.user?.nickname || '我',
    char: userStore.user?.avatarChar || '我',
    text,
    time: '刚刚',
    mine: true,
    replyTo: replyTo.value?.name || '',
  })
  commentText.value = ''
  replyTo.value = null
  ElMessage.success('评论成功')
}
const removeComment = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除这条评论吗？', '提示', { type: 'warning' })
    data.deleteComment(noteId, id)
    ElMessage.success('已删除')
  } catch { /* 取消 */ }
}

// 相关推荐：同分类的其他笔记
const related = computed(() => {
  if (!note.value) return []
  return NOTES.filter((n) => n.id !== noteId && n.category === note.value.category).slice(0, 3)
})

const searchTopic = (t) => router.push('/')
const editNote = () => router.push('/publish')
const deleteNote = async () => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除这条笔记吗？', '警告', {
      type: 'warning', confirmButtonText: '删除', confirmButtonClass: 'el-button--danger',
    })
    data.deleteNote(noteId)
    ElMessage.success('笔记已删除')
    router.push('/')
  } catch { /* 取消 */ }
}
</script>

<style scoped>
.detail-main { padding: 16px 12px 32px; }
.detail-layout { display: flex; flex-direction: column; gap: 16px; }

.img-viewer {
  position: relative;
  border-radius: var(--cike-radius);
  overflow: hidden;
  height: 320px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: rgba(255, 255, 255, 0.92);
  font-weight: 600;
  letter-spacing: 1px;
}
.viewer-img { width: 100%; height: 100%; object-fit: contain; }
.viewer-back {
  position: absolute;
  top: 12px;
  left: 12px;
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.35);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.viewer-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 34px;
  height: 34px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.3);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background 0.15s;
}
.viewer-arrow:hover { background: rgba(0, 0, 0, 0.55); }
.viewer-arrow.left { left: 12px; }
.viewer-arrow.right { right: 12px; }
.viewer-dots { position: absolute; bottom: 12px; left: 50%; transform: translateX(-50%); display: flex; gap: 5px; }
.viewer-dots i { width: 6px; height: 6px; border-radius: 50%; background: rgba(255, 255, 255, 0.5); cursor: pointer; }
.viewer-dots i.on { background: #fff; }

.body-card { background: #fff; border-radius: var(--cike-radius); padding: 18px 16px; margin-top: 12px; }
.d-title { font-size: 18px; font-weight: 700; line-height: 1.5; }
.d-text { font-size: 14px; color: #444; line-height: 1.9; margin-top: 12px; white-space: pre-wrap; }
.d-topics { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 12px; }
.topic { color: var(--cike-topic); font-size: 13px; cursor: pointer; }
.d-time { font-size: 12px; color: #bbb; margin-top: 12px; }
.mine-ops { margin-top: 14px; padding-top: 14px; border-top: 1px dashed var(--cike-border); }

.related { margin-top: 20px; }
.related-title { font-size: 15px; font-weight: 700; margin-bottom: 12px; }
.related-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }

.side-col { display: flex; flex-direction: column; gap: 12px; }
.author-card, .action-card, .comment-card {
  background: #fff;
  border-radius: var(--cike-radius);
  padding: 16px;
}
.author-card { display: flex; align-items: center; gap: 12px; }
.a-info { flex: 1; min-width: 0; }
.a-name { font-size: 14px; font-weight: 600; }
.a-bio { font-size: 11px; color: var(--cike-text-faint); margin-top: 2px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }

.action-card { display: flex; justify-content: space-around; padding: 12px 16px; }
.act {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1px;
  cursor: pointer;
  color: var(--cike-text-sub);
  transition: color 0.15s, transform 0.1s;
}
.act:active { transform: scale(0.92); }
.act span { font-size: 13px; font-weight: 600; }
.act em { font-style: normal; font-size: 11px; color: var(--cike-text-faint); }
.act.on { color: var(--cike-primary); }
.act.on em { color: var(--cike-primary); }

.comment-card h4 { font-size: 14px; font-weight: 700; margin-bottom: 12px; }
.reply-tip { font-size: 11px; color: var(--cike-text-faint); margin-top: 6px; }
.reply-tip span { color: var(--cike-primary); cursor: pointer; margin-left: 6px; }
.comment-list { margin-top: 16px; display: flex; flex-direction: column; gap: 16px; }
.comment { display: flex; gap: 10px; }
.c-avatar { width: 30px; height: 30px; font-size: 11px; background: linear-gradient(135deg, #a29bfe, #6c5ce7); }
.c-main { flex: 1; min-width: 0; }
.c-name { font-size: 12px; color: var(--cike-text-faint); }
.mine-tag {
  font-size: 10px;
  background: var(--cike-primary-soft);
  color: var(--cike-primary);
  border-radius: 4px;
  padding: 0 5px;
  margin-left: 6px;
}
.c-text { font-size: 13px; color: var(--cike-text); margin-top: 3px; line-height: 1.6; }
.c-text b { color: var(--cike-topic); font-weight: 500; }
.c-foot { font-size: 11px; color: #ccc; margin-top: 5px; display: flex; gap: 12px; }
.c-op { cursor: pointer; }
.c-op:hover { color: var(--cike-primary); }
.c-op.del:hover { color: var(--el-color-danger); }
.no-comment { font-size: 12px; color: #ccc; text-align: center; padding: 16px 0 4px; }

/* 平板端 */
@media (min-width: 769px) {
  .img-viewer { height: 420px; }
  .related-grid { grid-template-columns: repeat(3, 1fr); }
}
/* 电脑端：左右布局 */
@media (min-width: 1201px) {
  .detail-layout { flex-direction: row; align-items: flex-start; gap: 24px; }
  .content-col { flex: 1; min-width: 0; }
  .side-col { flex: 0 0 340px; position: sticky; top: 24px; }
  .img-viewer { height: 480px; }
}
</style>
