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
            placeholder="请输入账号：student / teacher / admin"
          />
        </el-form-item>

        <el-form-item label="密码">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码：123456"
            show-password
          />
        </el-form-item>

        <el-form-item label="身份">
          <el-select v-model="loginForm.role" placeholder="请选择身份" style="width: 100%">
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
  student: '/guide/correction',
  teacher: '/training/monitor',
  admin: '/training/monitor'
}

const handleLogin = () => {
  if (!loginForm.username || !loginForm.password) {
    ElMessage.warning('请输入账号和密码')
    return
  }

  if (loginForm.password !== '123456') {
    ElMessage.error('密码错误')
    return
  }

  const allowUsers = ['student', 'teacher', 'admin']

  if (!allowUsers.includes(loginForm.username)) {
    ElMessage.error('账号不存在')
    return
  }

  if (loginForm.username !== loginForm.role) {
    ElMessage.warning('账号与身份不匹配')
    return
  }

  localStorage.setItem('token', 'mock-token')
  localStorage.setItem('role', loginForm.role)
  localStorage.setItem('username', loginForm.username)

  ElMessage.success('登录成功')

  router.push(defaultPathMap[loginForm.role])
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  background: linear-gradient(135deg, #1f2d3d, #ffffff);
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