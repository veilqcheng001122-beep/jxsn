<template>
  <div class="login-container">
    <el-card class="login-card">
      <template #header>
        <div class="login-title">
          匠心善酿虚拟仿真实训智能研创平台
        </div>
      </template>

      <el-form :model="loginForm" label-width="70px">
        <el-form-item label="账号">
          <el-input
            v-model="loginForm.username"
            placeholder="student / teacher / admin"
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            type="password"
            show-password
            placeholder="123456"
          />
        </el-form-item>

        <el-form-item label="身份">
          <el-select v-model="loginForm.role" style="width: 100%">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
            <el-option label="管理员" value="admin" />
          </el-select>
        </el-form-item>

        <el-button type="primary" class="login-button" @click="handleLogin">
          登录
        </el-button>
      </el-form>

      <div class="tip">
        测试账号：student / teacher / admin，密码均为 123456
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const loginForm = reactive({
  username: '',
  password: '',
  role: 'teacher'
})

const defaultPathMap = {
  student: '/student/training',
  teacher: '/training/monitor',
  admin: '/training/monitor'
}

const handleLogin = () => {
  const username = loginForm.username.trim().toLowerCase()
  const password = loginForm.password.trim()
  const role = loginForm.role.trim().toLowerCase()

  if (!username || !password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  if (password !== '123456') {
    ElMessage.error('密码错误')
    return
  }

  const allowUsers = ['student', 'teacher', 'admin']

  if (!allowUsers.includes(username)) {
    ElMessage.error('账号不存在')
    return
  }

  if (username !== role) {
    ElMessage.warning('账号与身份不匹配')
    return
  }

  // 模拟保存登录状态和 Token
  sessionStorage.setItem('token', 'mock-token')
  sessionStorage.setItem('role', role)
  sessionStorage.setItem('username', username)

  ElMessage.success('登录成功')

  // 根据身份跳转到对应页面
  router.replace(defaultPathMap[role])
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #1f2d3d, #fbfbfc);
  display: flex;
  justify-content: center;
  align-items: center;
}

.login-card {
  width: 430px;
}

.login-title {
  text-align: center;
  font-size: 18px;
  font-weight: bold;
}

.login-button {
  width: 100%;
}

.tip {
  margin-top: 16px;
  color: #909399;
  font-size: 13px;
  text-align: center;
}
</style>