<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="header">
          <span>实训详情</span>
          <el-button @click="goBack">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="3" border>
        <el-descriptions-item label="学生姓名">
          {{ detail.studentName }}
        </el-descriptions-item>

        <el-descriptions-item label="学号">
          {{ detail.studentNo }}
        </el-descriptions-item>

        <el-descriptions-item label="当前工序">
          {{ detail.processName }}
        </el-descriptions-item>

        <el-descriptions-item label="实训进度">
          {{ detail.progress }}%
        </el-descriptions-item>

        <el-descriptions-item label="状态">
          <el-tag :type="detail.status === '异常' ? 'danger' : 'success'">
            {{ detail.status }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="log-card">
      <template #header>
        操作记录
      </template>

      <el-table :data="detail.logs" border stripe>
        <el-table-column prop="stepIndex" label="步骤序号" width="100" />
        <el-table-column prop="operationName" label="操作名称" />
        <el-table-column prop="paramValue" label="参数值" />

        <el-table-column label="是否合规" width="120">
          <template #default="{ row }">
            <el-tag :type="row.isCorrect ? 'success' : 'danger'">
              {{ row.isCorrect ? '合规' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="aiFeedback" label="AI指导建议" />
        <el-table-column label="操作时间" width="180">
            <template #default="{ row }">
                {{ formatTime(row.opTime) }}
            </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getTrainingDetail } from '@/api/training'

const route = useRoute()
const router = useRouter()

const detail = ref({
  logs: []
})

const loadDetail = async () => {
  const recordId = route.params.recordId
  const data = await getTrainingDetail(recordId)
  detail.value = data
}

const formatTime = time => {
  if (!time) return ''

  return time
    .replace('T', ' ')
    .substring(0, 19)
}

const goBack = () => {
  router.back()
}

onMounted(() => {
  loadDetail()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.log-card {
  margin-top: 16px;
}
</style>