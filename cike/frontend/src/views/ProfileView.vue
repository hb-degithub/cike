<template>
  <div class="page-wrap">
    <AppNav />
    <main class="page-main">
      <div class="profile-layout">
        <!-- 个人信息卡片 -->
        <section class="info-col">
          <div class="head-card">
            <div class="head-bg"></div>
            <div class="head-body">
              <span class="cike-avatar p-avatar">{{ user.avatarChar }}</span>
              <div class="p-main">
                <div class="p-name">{{ user.nickname }}</div>
                <div class="p-bio">{{ user.bio }}</div>
              </div>
              <el-button round size="small" plain @click="editDialog = true">编辑资料</el-button>
            </div>
            <div class="stats">
              <div class="stat"><b>{{ myNotes.length || user.works }}</b><span>作品</span></div>
              <div class="stat"><b>{{ fmtNum(user.liked) }}</b><span>获赞</span></div>
              <div class="stat"><b>{{ collectNotes.length || user.collects }}</b><span>收藏</span></div>
              <div class="stat"><b>{{ user.follows + data.follows.length }}</b><span>关注</span></div>
            </div>
          </div>

          <div class="setting-card">
            <div class="item" @click="editDialog = true">
              <span>修改头像 / 昵称 / 简介</span><el-icon><ArrowRight /></el-icon>
            </div>
            <div class="item" @click="ElMessage.info('演示环境暂未开放')">
              <span>账号设置</span><el-icon><ArrowRight /></el-icon>
            </div>
            <div class="logout" @click="logout">退出登录</div>
          </div>
        </section>

        <!-- 内容 tab -->
        <section class="content-col">
          <div class="ptabs">
            <span v-for="t in tabs" :key="t.key" :class="{ on: tab === t.key }" @click="tab = t.key">{{ t.label }}</span>
          </div>

          <div v-if="currentList.length" class="pgrid">
            <div v-for="n in currentList" :key="n.id" class="pgrid-item">
              <NoteCard :note="n" />
              <div v-if="tab === 'works' && n.mine" class="ops">
                <span class="vis-tag" :class="{ pri: n.visibility === 'private' }">
                  {{ n.visibility === 'private' ? '仅自己可见' : '公开' }}
                </span>
                <span class="op" @click.stop="toggleVis(n)">{{ n.visibility === 'private' ? '设为公开' : '设为私密' }}</span>
                <span class="op danger" @click.stop="removeNote(n)">删除</span>
              </div>
            </div>
          </div>
          <el-empty v-else :description="emptyText" />
        </section>
      </div>

      <!-- 编辑资料弹窗 -->
      <el-dialog v-model="editDialog" title="编辑资料" :width="dialogWidth">
        <el-form label-position="top">
          <el-form-item label="昵称">
            <el-input v-model="editForm.nickname" maxlength="12" />
          </el-form-item>
          <el-form-item label="个人简介">
            <el-input v-model="editForm.bio" type="textarea" :rows="2" maxlength="60" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialog = false">取消</el-button>
          <el-button type="primary" @click="saveProfile">保存</el-button>
        </template>
      </el-dialog>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, reactive, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowRight } from '@element-plus/icons-vue'
import AppNav from '../components/AppNav.vue'
import NoteCard from '../components/NoteCard.vue'
import { NOTES, fmtNum } from '../mock/notes'
import { useUserStore } from '../stores/user'
import { useDataStore } from '../stores/data'

const router = useRouter()
const userStore = useUserStore()
const data = useDataStore()

const user = computed(() => userStore.user || {})

const tabs = [
  { key: 'works', label: '作品' },
  { key: 'collects', label: '收藏' },
  { key: 'likes', label: '点赞' },
]
const tab = ref('works')

const allNotes = computed(() => [...data.myNotes, ...NOTES])
const myNotes = computed(() => data.myNotes)
const collectNotes = computed(() => allNotes.value.filter((n) => data.collects.includes(n.id)))
const likeNotes = computed(() => allNotes.value.filter((n) => data.likes.includes(n.id)))

const currentList = computed(() =>
  tab.value === 'works' ? myNotes.value : tab.value === 'collects' ? collectNotes.value : likeNotes.value
)
const emptyText = computed(() =>
  tab.value === 'works' ? '还没有发布作品，去发布第一条笔记吧' : tab.value === 'collects' ? '还没有收藏任何笔记' : '还没有点赞任何笔记'
)

const toggleVis = (n) => {
  n.visibility = n.visibility === 'private' ? 'public' : 'private'
  data._save('cike_my_notes', data.myNotes)
  ElMessage.success(n.visibility === 'private' ? '已设为仅自己可见' : '已设为公开')
}
const removeNote = async (n) => {
  try {
    await ElMessageBox.confirm('删除后不可恢复，确定删除吗？', '警告', { type: 'warning' })
    data.deleteNote(n.id)
    ElMessage.success('已删除')
  } catch { /* 取消 */ }
}

// 编辑资料
const editDialog = ref(false)
const dialogWidth = computed(() => (window.innerWidth <= 768 ? '90%' : '420px'))
const editForm = reactive({ nickname: '', bio: '' })
const openEdit = () => {
  editForm.nickname = user.value.nickname
  editForm.bio = user.value.bio
}
const saveProfile = () => {
  if (!editForm.nickname.trim()) {
    ElMessage.warning('昵称不能为空')
    return
  }
  userStore.updateProfile({ nickname: editForm.nickname.trim(), bio: editForm.bio.trim(), avatarChar: editForm.nickname.trim()[0] })
  editDialog.value = false
  ElMessage.success('资料已更新')
}

const logout = async () => {
  try {
    await ElMessageBox.confirm('确定退出登录吗？', '提示', { type: 'warning' })
    userStore.logout()
    ElMessage.success('已退出登录')
    router.push('/login')
  } catch { /* 取消 */ }
}

watch(editDialog, (val) => { if (val) openEdit() })
</script>

<style scoped>
.page-main { padding: 0 0 32px; }
.profile-layout { display: flex; flex-direction: column; }

.info-col { display: flex; flex-direction: column; gap: 12px; padding: 0 0 16px; }
.head-card { background: #fff; border-radius: 0 0 16px 16px; overflow: hidden; }
.head-bg { height: 110px; background: var(--cike-gradient); }
.head-body { display: flex; align-items: center; gap: 14px; padding: 0 20px; margin-top: -28px; }
.p-avatar {
  width: 64px;
  height: 64px;
  font-size: 24px;
  background: rgba(255, 255, 255, 0.25);
  border: 3px solid #fff;
  background: var(--cike-gradient);
}
.p-main { flex: 1; min-width: 0; padding-top: 30px; }
.p-name { font-size: 18px; font-weight: 700; }
.p-bio { font-size: 12px; color: var(--cike-text-faint); margin-top: 2px; }
.head-body .el-button { margin-top: 30px; }
.stats { display: flex; padding: 16px 0 6px; }
.stat { flex: 1; text-align: center; }
.stat b { font-size: 17px; display: block; }
.stat span { font-size: 11px; color: var(--cike-text-faint); }

.setting-card { background: #fff; border-radius: 16px; margin: 0 12px; padding: 4px 16px; }
.item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 2px;
  font-size: 14px;
  border-bottom: 1px solid var(--cike-border);
  cursor: pointer;
  color: var(--cike-text);
}
.item .el-icon { color: #ccc; }
.logout { text-align: center; color: var(--cike-primary); font-weight: 600; padding: 15px; font-size: 14px; cursor: pointer; }

.content-col { padding: 0 12px; }
.ptabs { display: flex; background: #fff; border-radius: 12px; margin-bottom: 12px; overflow: hidden; }
.ptabs span {
  flex: 1;
  text-align: center;
  font-size: 14px;
  color: var(--cike-text-faint);
  padding: 13px 0;
  cursor: pointer;
  transition: all 0.15s;
}
.ptabs span.on { color: var(--cike-primary); font-weight: 700; box-shadow: inset 0 -2.5px 0 var(--cike-primary); }

.pgrid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 10px; }
.pgrid-item { position: relative; }
.ops { display: flex; align-items: center; gap: 12px; padding: 8px 4px 0; font-size: 12px; }
.vis-tag { background: #e8f5e9; color: #43a047; border-radius: 4px; padding: 1px 6px; font-size: 10px; }
.vis-tag.pri { background: #f5f5f5; color: #999; }
.op { color: var(--cike-topic); cursor: pointer; }
.op.danger { color: var(--el-color-danger); }

/* 平板端 */
@media (min-width: 769px) {
  .page-main { padding-top: 20px; }
  .head-card { border-radius: 16px; margin: 0 12px; }
  .pgrid { grid-template-columns: repeat(3, 1fr); }
}
/* 电脑端：左侧固定信息栏 + 右侧内容 */
@media (min-width: 1201px) {
  .profile-layout { flex-direction: row; align-items: flex-start; gap: 24px; }
  .info-col { flex: 0 0 300px; position: sticky; top: 24px; }
  .head-card { margin: 0; }
  .setting-card { margin: 0; }
  .content-col { flex: 1; min-width: 0; padding: 0; }
  .pgrid { grid-template-columns: repeat(3, 1fr); }
}
</style>
