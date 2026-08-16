import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { viteSingleFile } from 'vite-plugin-singlefile'

// https://vite.dev/config/
export default defineConfig({
  base: './', // 相对路径
  plugins: [
    vue(),
    // 将所有 JS/CSS 内联进单个 index.html，双击 file:// 即可离线打开
    viteSingleFile(),
  ],
})
