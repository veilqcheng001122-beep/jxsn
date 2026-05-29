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
          {{ detail.studentName || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="学号">
          {{ detail.studentNo || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="当前工序">
          {{ detail.processName || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="实训进度">
          <el-progress
            :percentage="Number(detail.progress) || 0"
            :stroke-width="10"
          />
        </el-descriptions-item>

        <el-descriptions-item label="实训状态">
          <el-tag :type="detail.status === '已完成' ? 'success' : 'primary'">
            {{ detail.status || '未知' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="异常状态">
          <el-tag :type="getWarningTagType(warningStatus)">
            {{ warningStatus }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="历史异常次数">
          {{ warningCount }}
        </el-descriptions-item>

        <el-descriptions-item label="最新更新时间">
          {{ formatTime(latestLog?.opTime) || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="教师干预次数">
          {{ interventionList.length }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span>最新操作</span>
          <el-tag type="info">优先展示</el-tag>
        </div>
      </template>

      <el-empty
        v-if="!latestLog"
        description="暂无操作记录"
      />

      <el-descriptions
        v-else
        :column="2"
        border
      >
        <el-descriptions-item label="步骤">
          第 {{ latestLog.stepIndex }} 步
        </el-descriptions-item>

        <el-descriptions-item label="操作名称">
          {{ latestLog.operationName || `第${latestLog.stepIndex}步操作` }}
        </el-descriptions-item>

        <el-descriptions-item label="提交参数">
          {{ latestLog.paramValue || '-' }}
        </el-descriptions-item>

        <el-descriptions-item label="判断结果">
          <el-tag :type="isCorrectValue(latestLog.isCorrect) ? 'success' : 'danger'">
            {{ isCorrectValue(latestLog.isCorrect) ? '合规' : '异常' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="AI指导建议" :span="2">
          <el-alert
            :title="latestLog.aiFeedback || '暂无AI指导建议'"
            :type="isCorrectValue(latestLog.isCorrect) ? 'success' : 'warning'"
            show-icon
            :closable="false"
          />
        </el-descriptions-item>

        <el-descriptions-item label="操作时间" :span="2">
          {{ formatTime(latestLog.opTime) }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span>历史操作记录</span>
          <el-tag type="info">按步骤折叠查看</el-tag>
        </div>
      </template>

      <el-empty
        v-if="groupedLogs.length === 0"
        description="暂无历史操作记录"
      />

      <el-collapse
        v-else
        v-model="activeStepNames"
      >
        <el-collapse-item
          v-for="group in groupedLogs"
          :key="group.stepIndex"
          :name="String(group.stepIndex)"
        >
          <template #title>
            <div class="collapse-title">
              <span>第 {{ group.stepIndex }} 步：{{ group.operationName }}</span>

              <el-tag
                :type="getStepStatusTagType(group.stepStatus)"
                size="small"
              >
                {{ group.stepStatus }}
              </el-tag>

              <span class="collapse-subtitle">
                共 {{ group.logs.length }} 条记录，最新：{{ formatTime(group.latestLog?.opTime) }}
              </span>
            </div>
          </template>

          <el-table
            :data="group.logs"
            border
            stripe
          >
            <el-table-column
              prop="paramValue"
              label="提交参数"
              min-width="160"
            />

            <el-table-column
              label="是否合规"
              width="100"
            >
              <template #default="{ row }">
                <el-tag :type="isCorrectValue(row.isCorrect) ? 'success' : 'danger'">
                  {{ isCorrectValue(row.isCorrect) ? '合规' : '异常' }}
                </el-tag>
              </template>
            </el-table-column>

            <el-table-column
              label="AI指导建议"
              min-width="260"
            >
              <template #default="{ row }">
                <span>{{ row.aiFeedback || '暂无AI指导建议' }}</span>
              </template>
            </el-table-column>

            <el-table-column
              label="操作时间"
              width="180"
            >
              <template #default="{ row }">
                {{ formatTime(row.opTime) }}
              </template>
            </el-table-column>
          </el-table>
        </el-collapse-item>
      </el-collapse>
    </el-card>

    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span>教师干预记录</span>
          <el-tag type="warning">教师人工建议</el-tag>
        </div>
      </template>

      <el-empty
        v-if="interventionList.length === 0"
        description="暂无教师干预记录"
      />

      <el-collapse
        v-else
        v-model="activeInterventionNames"
      >
        <el-collapse-item
          v-for="(item, index) in interventionList"
          :key="getInterventionKey(item, index)"
          :name="String(getInterventionKey(item, index))"
        >
          <template #title>
            <div class="collapse-title">
              <span>教师干预 {{ index + 1 }}</span>

              <el-tag
                v-if="index === 0"
                type="danger"
                size="small"
              >
                最新
              </el-tag>

              <span class="collapse-subtitle">
                {{ formatTime(item.interventionTime || item.createTime) }}
              </span>
            </div>
          </template>

          <el-alert
            :title="getInterventionContent(item)"
            type="warning"
            show-icon
            :closable="false"
          />
        </el-collapse-item>
      </el-collapse>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  getTrainingDetail,
  getInterventionList
} from '@/api/training'

const route = useRoute()
const router = useRouter()

const detail = ref({
  logs: []
})

const interventionList = ref([])
const activeStepNames = ref([])
const activeInterventionNames = ref([])

const isCorrectValue = value => {
  return value === true || value === 1 || value === '1'
}

const sortedLogs = computed(() => {
  const logs = detail.value.logs || []

  return [...logs].sort((a, b) => {
    const timeA = new Date(a.opTime || 0).getTime()
    const timeB = new Date(b.opTime || 0).getTime()

    if (timeB !== timeA) {
      return timeB - timeA
    }

    return Number(b.logId || 0) - Number(a.logId || 0)
  })
})

const latestLog = computed(() => {
  return sortedLogs.value[0] || null
})

const warningCount = computed(() => {
  return (detail.value.logs || []).filter(item => {
    return !isCorrectValue(item.isCorrect)
  }).length
})

const warningStatus = computed(() => {
  if (!latestLog.value) {
    return '暂无操作'
  }

  if (!isCorrectValue(latestLog.value.isCorrect)) {
    return '异常待处理'
  }

  if (warningCount.value > 0) {
    return '已纠正'
  }

  return '正常'
})

const groupedLogs = computed(() => {
  const map = new Map()

  sortedLogs.value.forEach(log => {
    const stepIndex = log.stepIndex || 0

    if (!map.has(stepIndex)) {
      map.set(stepIndex, {
        stepIndex,
        operationName: log.operationName || `第${stepIndex}步操作`,
        logs: [],
        latestLog: null,
        hasHistoryError: false,
        stepStatus: '全部合规'
      })
    }

    const group = map.get(stepIndex)
    group.logs.push(log)

    if (!isCorrectValue(log.isCorrect)) {
      group.hasHistoryError = true
    }

    if (!group.latestLog) {
      group.latestLog = log
    }
  })

  const groups = Array.from(map.values())

  groups.forEach(group => {
    const latestIsCorrect = isCorrectValue(group.latestLog?.isCorrect)

    if (!latestIsCorrect) {
      group.stepStatus = '出现异常'
      return
    }

    if (group.hasHistoryError) {
      group.stepStatus = '已纠正'
      return
    }

    group.stepStatus = '全部合规'
  })

  return groups.sort((a, b) => {
    return Number(b.stepIndex) - Number(a.stepIndex)
  })
})

const loadDetail = async () => {
  const recordId = route.params.recordId
  const data = await getTrainingDetail(recordId)

  detail.value = {
    ...data,
    logs: data?.logs || []
  }

  const sessionId = detail.value.sessionId || recordId
  await loadInterventionList(sessionId)

  setDefaultOpenedPanels()
}

const loadInterventionList = async sessionId => {
  const data = await getInterventionList({
    sessionId
  })

  interventionList.value = Array.isArray(data) ? data : []
}

const setDefaultOpenedPanels = () => {
  if (latestLog.value) {
    activeStepNames.value = [String(latestLog.value.stepIndex)]
  }

  if (interventionList.value.length > 0) {
    activeInterventionNames.value = [
      String(getInterventionKey(interventionList.value[0], 0))
    ]
  }
}

const getWarningTagType = status => {
  if (status === '异常待处理') {
    return 'danger'
  }

  if (status === '已纠正') {
    return 'warning'
  }

  if (status === '正常') {
    return 'success'
  }

  return 'info'
}

const getStepStatusTagType = status => {
  if (status === '出现异常') {
    return 'danger'
  }

  if (status === '已纠正') {
    return 'warning'
  }

  if (status === '全部合规') {
    return 'success'
  }

  return 'info'
}

const getInterventionKey = (item, index) => {
  return (
    item.interventionId ||
    item.id ||
    item.interventionTime ||
    item.createTime ||
    index
  )
}

const getInterventionContent = item => {
  return (
    item.interventionAction ||
    item.guidanceText ||
    item.content ||
    '教师已下发干预建议'
  )
}

const formatTime = time => {
  if (!time) return ''

  if (typeof time === 'number') {
    return new Date(time).toLocaleString()
  }

  return String(time)
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

.header,
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.section-card {
  margin-top: 16px;
}

.collapse-title {
  width: 100%;
  display: flex;
  align-items: center;
  gap: 10px;
}

.collapse-subtitle {
  margin-left: auto;
  color: #909399;
  font-size: 13px;
}

:deep(.el-collapse-item__header) {
  padding-right: 12px;
}
</style>