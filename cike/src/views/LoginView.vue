<template>
  <div class="login-page">
    <div class="login-card">
      <div class="logo-area">
        <div class="logo-mark">刻</div>
        <h1>此刻</h1>
        <p class="slogan">记录生活，分享美好</p>
      </div>

      <div class="tabs">
        <span :class="{ on: mode === 'login' }" @click="switchMode('login')">登录</span>
        <span :class="{ on: mode === 'register' }" @click="switchMode('register')">注册</span>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" label-position="top" size="large">
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" :prefix-icon="Iphone" />
        </el-form-item>

        <el-form-item v-if="mode === 'register' || loginType === 'code'" prop="code">
          <el-input v-model="form.code" placeholder="请输入验证码" maxlength="6" :prefix-icon="Key">
            <template #append>
              <el-button :disabled="countdown > 0" @click="sendCode" link type="primary">
                {{ countdown > 0 ? `${countdown}s 后重发` : '获取验证码' }}
              </el-button>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item v-if="mode === 'register'" prop="nickname">
          <el-input v-model="form.nickname" placeholder="设置你的昵称" maxlength="12" :prefix-icon="User" />
        </el-form-item>

        <el-form-item v-if="mode === 'register' || loginType === 'password'" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码（6-20 位）"
            maxlength="20"
            :prefix-icon="Lock"
          />
        </el-form-item>

        <el-button class="submit-btn" type="primary" size="large" :loading="loading" @click="submit">
          {{ mode === 'login' ? '登 录' : '注册并登录' }}
        </el-button>
      </el-form>

      <p v-if="mode === 'login'" class="switch-line">
        <span :class="{ on: loginType === 'password' }" @click="loginType = 'password'">密码登录</span>
        <i>|</i>
        <span :class="{ on: loginType === 'code' }" @click="loginType = 'code'">验证码登录</span>
      </p>

      <p class="agree">登录即代表同意 <b>《用户协议》</b> 与 <b>《隐私政策》</b></p>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Iphone, Key, Lock, User } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const mode = ref('login')
const loginType = ref('password')
const loading = ref(false)
const countdown = ref(0)
const formRef = ref()

const form = reactive({ phone: '', code: '', nickname: '', password: '' })

const phoneRule = { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
const rules = reactive({
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, phoneRule],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }, { len: 6, message: '验证码为 6 位数字', trigger: 'blur' }],
  nickname: [{ required: true, message: '请设置昵称', trigger: 'blur' }, { min: 2, max: 12, message: '昵称 2-12 个字符', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '密码长度 6-20 位', trigger: 'blur' }],
})

const switchMode = (m) => {
  mode.value = m
  formRef.value?.clearValidate()
}

let timer = null
const sendCode = async () => {
  try {
    await formRef.value.validateField('phone')
  } catch {
    return
  }
  countdown.value = 60
  ElMessage.success('验证码已发送：123456（演示环境）')
  timer = setInterval(() => {
    countdown.value--
    if (countdown.value <= 0) clearInterval(timer)
  }, 1000)
}

const submit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  if ((mode.value === 'register' || loginType.value === 'code') && form.code !== '123456') {
    ElMessage.error('验证码错误或已过期（演示环境请输入 123456）')
    return
  }
  loading.value = true
  setTimeout(() => {
    loading.value = false
    userStore.login(form.phone)
    if (form.nickname) userStore.updateProfile({ nickname: form.nickname, avatarChar: form.nickname[0] })
    ElMessage.success(mode.value === 'login' ? '登录成功，欢迎回来' : '注册成功')
    router.push(route.query.redirect || '/')
  }, 600)
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--cike-bg);
  padding: 24px 16px;
}
.login-card {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 20px;
  padding: 44px 32px 32px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.06);
}
.logo-area { text-align: center; margin-bottom: 32px; }
.logo-mark {
  width: 68px;
  height: 68px;
  border-radius: 18px;
  background: var(--cike-gradient);
  margin: 0 auto 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 32px;
  font-weight: 800;
  box-shadow: 0 10px 24px rgba(255, 46, 81, 0.3);
}
h1 { font-size: 24px; font-weight: 800; }
.slogan { font-size: 13px; color: var(--cike-text-faint); margin-top: 6px; }

.tabs { display: flex; gap: 32px; justify-content: center; margin-bottom: 26px; }
.tabs span { font-size: 16px; color: var(--cike-text-faint); padding-bottom: 6px; cursor: pointer; }
.tabs span.on { color: var(--cike-text); font-weight: 700; border-bottom: 3px solid var(--cike-primary); }

.submit-btn { width: 100%; height: 46px; font-size: 16px; border-radius: 23px; margin-top: 6px; }

.switch-line { text-align: center; font-size: 13px; color: var(--cike-text-faint); margin-top: 18px; }
.switch-line span { cursor: pointer; padding: 0 4px; }
.switch-line span.on { color: var(--cike-primary); font-weight: 600; }
.switch-line i { margin: 0 8px; font-style: normal; opacity: 0.4; }

.agree { text-align: center; font-size: 11px; color: #bbb; margin-top: 24px; }
.agree b { color: var(--cike-topic); font-weight: 400; cursor: pointer; }

/* 移动端紧凑布局 */
@media (max-width: 768px) {
  .login-card { box-shadow: none; border-radius: 0; padding: 40px 8px; background: transparent; }
  .login-page { background: #fff; align-items: flex-start; padding-top: 72px; }
}

/* 平板端 */
@media (min-width: 769px) and (max-width: 1200px) {
  .login-card { max-width: 460px; }
}
</style>
