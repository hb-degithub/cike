import { defineStore } from 'pinia'

const LIKES_KEY = 'cike_likes'
const COLLECTS_KEY = 'cike_collects'
const FOLLOWS_KEY = 'cike_follows'
const MY_NOTES_KEY = 'cike_my_notes'
const COMMENTS_KEY = 'cike_comments'

function read(key, fallback) {
  try { return JSON.parse(localStorage.getItem(key)) ?? fallback } catch { return fallback }
}

export const useDataStore = defineStore('data', {
  state: () => ({
    likes: read(LIKES_KEY, []),        // 已点赞笔记 id
    collects: read(COLLECTS_KEY, []),  // 已收藏笔记 id
    follows: read(FOLLOWS_KEY, []),    // 已关注作者昵称
    myNotes: read(MY_NOTES_KEY, []),   // 我发布的笔记
    comments: read(COMMENTS_KEY, {}),  // { noteId: [comment] }
  }),
  actions: {
    _save(key, val) { localStorage.setItem(key, JSON.stringify(val)) },
    toggleLike(id) {
      const i = this.likes.indexOf(id)
      i >= 0 ? this.likes.splice(i, 1) : this.likes.push(id)
      this._save(LIKES_KEY, this.likes)
      return i < 0
    },
    toggleCollect(id) {
      const i = this.collects.indexOf(id)
      i >= 0 ? this.collects.splice(i, 1) : this.collects.push(id)
      this._save(COLLECTS_KEY, this.collects)
      return i < 0
    },
    toggleFollow(name) {
      const i = this.follows.indexOf(name)
      i >= 0 ? this.follows.splice(i, 1) : this.follows.push(name)
      this._save(FOLLOWS_KEY, this.follows)
      return i < 0
    },
    addNote(note) {
      this.myNotes.unshift(note)
      this._save(MY_NOTES_KEY, this.myNotes)
    },
    deleteNote(id) {
      this.myNotes = this.myNotes.filter((n) => n.id !== id)
      this._save(MY_NOTES_KEY, this.myNotes)
    },
    addComment(noteId, comment) {
      if (!this.comments[noteId]) this.comments[noteId] = []
      this.comments[noteId].unshift(comment)
      this._save(COMMENTS_KEY, this.comments)
    },
    deleteComment(noteId, commentId) {
      if (!this.comments[noteId]) return
      this.comments[noteId] = this.comments[noteId].filter((c) => c.id !== commentId)
      this._save(COMMENTS_KEY, this.comments)
    },
  },
})
