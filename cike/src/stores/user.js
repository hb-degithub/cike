import { defineStore } from 'pinia'

const TOKEN_KEY = 'cike_token'
const USER_KEY = 'cike_user'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem(TOKEN_KEY) || '',
    user: JSON.parse(localStorage.getItem(USER_KEY) || 'null'),
  }),
  getters: {
    isLogin: (s) => !!s.token,
  },
  actions: {
    login(phone) {
      this.token = 'mock-token-' + Date.now()
      this.user = {
        id: 1,
        nickname: '胡澜译',
        phone,
        bio: '记录生活的每一天 ✨',
        avatarChar: '澜',
        works: 12,
        liked: 3600,
        collects: 48,
        follows: 96,
      }
      localStorage.setItem(TOKEN_KEY, this.token)
      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    },
    logout() {
      this.token = ''
      this.user = null
      localStorage.removeItem(TOKEN_KEY)
      localStorage.removeItem(USER_KEY)
    },
    updateProfile(patch) {
      if (!this.user) return
      this.user = { ...this.user, ...patch }
      localStorage.setItem(USER_KEY, JSON.stringify(this.user))
    },
  },
})
