<template>
  <div class="page-container">
    <el-card class="stat-card">
      <template #header>
        <span>实训过程监控</span>
      </template>

      <el-row :gutter="16">
        <el-col :span="6">
          <el-card>
            <div class="stat-title">在线学生</div>
            <div class="stat-value">{{ monitorData.onlineCount }}</div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card>
            <div class="stat-title">进行中任务</div>
            <div class="stat-value">{{ monitorData.runningCount }}</div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card>
            <div class="stat-title">异常预警</div>
            <div class="stat-value warning">{{ monitorData.warningCount }}</div>
          </el-card>
        </el-col>

        <el-col :span="6">
          <el-card>
            <div class="stat-title">已完成实训</div>
            <div class="stat-value success">{{ monitorData.finishedCount }}</div>
          </el-card>
        </el-col>
      </el-row>
    </el-card>

    <el-form :inline="true" :model="queryForm" class="query-form">
      <el-form-item label="学生姓名">
        <el-input
          v-model="queryForm.studentName"
          placeholder="请输入学生姓名"
          clearable
        />
      </el-form-item>

      <el-form-item label="学号">
        <el-input
          v-model="queryForm.studentNo"
          placeholder="请输入学号"
          clearable
        />
      </el-form-item>

      <el-form-item label="实训工序">
        <el-select
          v-model="queryForm.processName"
          placeholder="请选择工序"
          clearable
          style="width: 160px"
        >
          <el-option label="原料处理" value="原料处理" />
          <el-option label="发酵控制" value="发酵控制" />
          <el-option label="蒸馏操作" value="蒸馏操作" />
          <el-option label="陈酿管理" value="陈酿管理" />
        </el-select>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="loadTrainingRecords(true)">
          查询
        </el-button>
        <el-button @click="resetQuery">
          重置
        </el-button>
      </el-form-item>
    </el-form>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>学生实训记录</span>
        </div>
      </template>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
      >
        <el-table-column prop="studentName" label="学生姓名" />
        <el-table-column prop="studentNo" label="学号" />
        <el-table-column prop="processName" label="当前工序" />

        <el-table-column label="实训进度" width="160">
          <template #default="{ row }">
            <el-progress
              :percentage="Number(row.progress) || 0"
              :stroke-width="10"
            />
          </template>
        </el-table-column>

        <el-table-column label="实训状态" width="110">
          <template #default="{ row }">
            <el-tag :type="row.status === '已完成' ? 'success' : 'primary'">
              {{ row.status || '未知' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="异常状态" width="130">
          <template #default="{ row }">
            <el-tag :type="getWarningTagType(row.warningStatus)">
              {{ row.warningStatus || '暂无操作' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">
              查看详情
            </el-button>

            <el-button
              type="warning"
              link
              :disabled="row.warningStatus !== '异常待处理'"
              @click="openIntervene(row)"
            >
              远程干预
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="interveneDialogVisible"
      title="教师远程干预"
      width="680px"
    >
      <el-form :model="interveneForm" label-width="120px">
        <el-form-item label="学生姓名">
          <el-input v-model="interveneForm.studentName" disabled />
        </el-form-item>

        <el-form-item label="修正参数">
          <div style="width: 100%">
            <div
              v-for="(item, index) in interveneForm.paramList"
              :key="index"
              class="param-row"
            >
              <el-select
                v-model="item.paramName"
                placeholder="请选择参数"
                style="width: 220px"
              >
                <el-option label="温度 temperature" value="temperature" />
                <el-option label="湿度 humidity" value="humidity" />
                <el-option label="压力 pressure" value="pressure" />
                <el-option label="时长 duration" value="duration" />
                <el-option label="微生物浓度 microbe" value="microbe" />
                <el-option label="密封状态 seal_status" value="seal_status" />
              </el-select>

              <el-input
                v-model="item.paramValue"
                placeholder="修正值，例如：36、85、已密封"
              />

              <el-button
                type="danger"
                plain
                :disabled="interveneForm.paramList.length === 1"
                @click="removeInterveneParam(index)"
              >
                删除
              </el-button>
            </div>

            <el-button type="primary" plain @click="addInterveneParam">
              添加参数
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="指导话语">
          <el-input
            v-model="interveneForm.guidanceText"
            type="textarea"
            :rows="4"
            placeholder="例如：当前参数存在偏差，请根据教师修正值重新确认设备状态后继续实训。"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="interveneDialogVisible = false">
          取消
        </el-button>

        <el-button type="primary" @click="submitIntervene">
          下发干预
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  getTrainingMonitor,
  getTrainingRecords,
  sendIntervention
} from '@/api/training'

const router = useRouter()

let pollIntervalId = null

const loading = ref(false)
const tableData = ref([])

const monitorData = ref({
  onlineCount: 0,
  runningCount: 0,
  warningCount: 0,
  finishedCount: 0
})

const queryForm = reactive({
  studentName: '',
  studentNo: '',
  processName: '',
  timeRange: []
})

const interveneDialogVisible = ref(false)

const interveneForm = reactive({
  recordId: '',
  sessionId: '',
  studentName: '',
  paramList: [
    {
      paramName: '',
      paramValue: ''
    }
  ],
  guidanceText: ''
})

const buildQueryParams = () => {
  return {
    studentName: queryForm.studentName,
    studentNo: queryForm.studentNo,
    processName: queryForm.processName,
    startTime: queryForm.timeRange?.[0] || '',
    endTime: queryForm.timeRange?.[1] || ''
  }
}

const loadMonitorData = async () => {
  try {
    const data = await getTrainingMonitor()

    monitorData.value = data || {
      onlineCount: 0,
      runningCount: 0,
      warningCount: 0,
      finishedCount: 0
    }
  } catch (error) {
    console.error('加载监控统计失败：', error)
  }
}

const loadTrainingRecords = async (showLoading = false) => {
  if (showLoading) {
    loading.value = true
  }

  try {
    const data = await getTrainingRecords(buildQueryParams())
    tableData.value = data?.records || []
  } catch (error) {
    console.error('加载实训记录失败：', error)
    tableData.value = []
  } finally {
    if (showLoading) {
      loading.value = false
    }
  }
}

const resetQuery = () => {
  queryForm.studentName = ''
  queryForm.studentNo = ''
  queryForm.processName = ''
  queryForm.timeRange = []

  loadTrainingRecords(true)
}

const viewDetail = row => {
  router.push(`/training/detail/${row.recordId || row.sessionId}`)
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

const addInterveneParam = () => {
  interveneForm.paramList.push({
    paramName: '',
    paramValue: ''
  })
}

const removeInterveneParam = index => {
  interveneForm.paramList.splice(index, 1)
}

const openIntervene = row => {
  interveneForm.recordId = row.recordId || row.sessionId
  interveneForm.sessionId = row.sessionId || row.recordId
  interveneForm.studentName = row.studentName
  interveneForm.paramList = [
    {
      paramName: '',
      paramValue: ''
    }
  ]
  interveneForm.guidanceText = ''

  interveneDialogVisible.value = true
}

const submitIntervene = async () => {
  const invalidItem = interveneForm.paramList.find(item => {
    return !item.paramName || !item.paramValue
  })

  if (invalidItem) {
    ElMessage.warning('请完整填写所有修正参数')
    return
  }

  if (!interveneForm.guidanceText) {
    ElMessage.warning('请输入指导话语')
    return
  }

  try {
    await sendIntervention({
      recordId: interveneForm.recordId,
      sessionId: interveneForm.sessionId || interveneForm.recordId,
      studentName: interveneForm.studentName,
      paramList: interveneForm.paramList,
      guidanceText: interveneForm.guidanceText
    })

    ElMessage.success('教师干预建议已下发')
    interveneDialogVisible.value = false

    loadMonitorData()
    loadTrainingRecords(false)
  } catch (error) {
    console.error('下发远程干预失败：', error)
    ElMessage.error('远程干预下发失败，请检查后端接口')
  }
}

onMounted(() => {
  loadMonitorData()
  loadTrainingRecords(true)

  pollIntervalId = setInterval(() => {
    loadMonitorData()
    loadTrainingRecords(false)
  }, 2000)
})

onUnmounted(() => {
  if (pollIntervalId) {
    clearInterval(pollIntervalId)
    pollIntervalId = null
  }
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 16px;
}

.query-form {
  margin-bottom: 20px;
}

.table-card {
  margin-top: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.warning {
  color: #e6a23c;
}

.success {
  color: #67c23a;
}

.param-row {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}
</style>