<template>
  <el-container class="layout-container">
    <el-aside width="220px" class="aside">
      <div class="logo">
        匠心善酿平台
      </div>

      <el-menu
        router
        :default-active="activeMenu"
        background-color="#1f2d3d"
        text-color="#bfcbd9"
        active-text-color="#409eff"
      >
        <el-menu-item
          v-for="item in menus"
          :key="item.path"
          :index="item.path"
        >
          {{ item.title }}
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header class="header">
        <div class="header-title">
          匠心善酿虚拟仿真实训智能研创平台
        </div>

        <div class="header-right">
          <span class="role-text">
            {{ username }} / {{ roleName }}
          </span>

          <el-button size="small" type="danger" plain @click="logout">
            退出登录
          </el-button>
        </div>
      </el-header>

      <el-main class="main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'

const router = useRouter()
const route = useRoute()

const currentRole = ref(localStorage.getItem('role') || 'teacher')
const username = ref(localStorage.getItem('username') || 'teacher')

const roleMap = {
  student: '学生',
  teacher: '教师',
  admin: '管理员'
}

const roleName = computed(() => {
  return roleMap[currentRole.value] || '教师'
})

const menus = computed(() => {
  if (currentRole.value === 'student') {
    return [
      {
        title: '智能指导与纠错',
        path: '/guide/correction'
      }
    ]
  }

  if (currentRole.value === 'teacher') {
    return [
      {
        title: '实训过程监控',
        path: '/training/monitor'
      },
      {
        title: '智能指导与纠错',
        path: '/guide/correction'
      }
    ]
  }

  if (currentRole.value === 'admin') {
    return [
      {
        title: '实训过程监控',
        path: '/training/monitor'
      },
      {
        title: '智能指导与纠错',
        path: '/guide/correction'
      },
      {
        title: '异常规则维护',
        path: '/rule/manage'
      }
    ]
  }

  return []
})

const activeMenu = computed(() => {
  if (route.path.startsWith('/training/detail')) {
    return '/training/monitor'
  }

  return route.path
})

const logout = () => {
  ElMessageBox.confirm(
    '确定要退出当前账号吗？',
    '退出登录',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  ).then(() => {
    localStorage.removeItem('token')
    localStorage.removeItem('role')
    localStorage.removeItem('username')

    ElMessage.success('已退出登录')
    router.push('/login')
  })
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
}

.aside {
  background-color: #1f2d3d;
}

.logo {
  height: 60px;
  line-height: 60px;
  color: #ffffff;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  border-bottom: 1px solid #32465a;
}

.header {
  height: 60px;
  background-color: #ffffff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-text {
  font-size: 14px;
  color: #606266;
  margin-right: 8px;
}

.main {
  background-color: #f5f7fa;
  padding: 0;
}
</style>