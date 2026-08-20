import { createRouter, createWebHashHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  { path: '/', name: 'home', component: () => import('../views/HomeView.vue'), meta: { title: '首页' } },
  { path: '/login', name: 'login', component: () => import('../views/LoginView.vue'), meta: { title: '登录注册' } },
  { path: '/publish', name: 'publish', component: () => import('../views/PublishView.vue'), meta: { title: '发布笔记', auth: true } },
  { path: '/note/:id', name: 'detail', component: () => import('../views/DetailView.vue'), meta: { title: '笔记详情' } },
  { path: '/profile', name: 'profile', component: () => import('../views/ProfileView.vue'), meta: { title: '个人中心', auth: true } },
  { path: '/:pathMatch(.*)*', redirect: '/' },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to) => {
  document.title = to.meta.title ? `${to.meta.title} · 此刻` : '此刻'
  const token = localStorage.getItem('cike_token')
  if (to.meta.auth && !token) {
    ElMessage.warning('请先登录后再访问')
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.name === 'login' && token) {
    return { path: '/' }
  }
  return true
})

export default router
