

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

    <el-form :inline="true" :model="queryForm" style="margin-bottom: 20px">
        <el-form-item label="学生姓名">
            <el-input v-model="queryForm.studentName" placeholder="请输入学生姓名" clearable />
        </el-form-item>
        <el-form-item label="学号">
            <el-input v-model="queryForm.studentNo" placeholder="请输入学号" clearable />
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
            <el-button type="primary" @click="loadTrainingRecords">查询</el-button>
            <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
    </el-form>

    <el-card class="table-card">
      <template #header>
        <span>学生实训记录</span>
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
        <el-table-column prop="progress" label="实训进度" />

        <el-table-column label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === '异常' ? 'danger' : 'success'">
              {{ row.status }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="warningCount" label="异常次数" />
        <el-table-column prop="latestOperation" label="最新操作" />
        <el-table-column label="更新时间" width="180">
            <template #default="{ row }">
                {{ formatTime(row.updateTime) }}
            </template>
        </el-table-column>

        <el-table-column label="操作" width="220">
            <template #default="{ row }">
                <el-button type="primary" link @click="viewDetail(row)">
                    查看详情
                </el-button>
                <el-button type="warning" link @click="openIntervene(row)">
                    远程干预
                </el-button>
            </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="interveneDialogVisible" title="教师远程干预" width="500px">
  <el-form :model="interveneForm" label-width="100px">
    <el-form-item label="学生姓名">
      <el-input v-model="interveneForm.studentName" disabled />
    </el-form-item>

    <el-form-item label="干预内容">
      <el-input
        v-model="interveneForm.command"
        type="textarea"
        :rows="4"
        placeholder="请输入干预建议，例如：请降低发酵温度"
      />
    </el-form-item>
  </el-form>

  <template #footer>
    <el-button @click="interveneDialogVisible = false">取消</el-button>
    <el-button type="primary" @click="submitIntervene">下发指令</el-button>
  </template>
</el-dialog>

  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { getTrainingMonitor, getTrainingRecords, sendIntervention } from '@/api/training'
import { reactive } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

const router = useRouter()
const viewDetail = (row) => {
  router.push(`/training/detail/${row.recordId}`)
}
const queryForm = reactive({
  studentName: '',
  studentNo: '',
  processType: '',
  timeRange: []
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

const loadTrainingRecords = async () => {
  loading.value = true

  try {
    const data = await getTrainingRecords(buildQueryParams())
    tableData.value = data.records || []
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.studentName = ''
  queryForm.studentNo = ''
  queryForm.processName = ''
  queryForm.timeRange = []

  loadTrainingRecords()
}

const loading = ref(false)

const monitorData = ref({
  onlineCount: 0,
  runningCount: 0,
  warningCount: 0,
  finishedCount: 0
})

const tableData = ref([])



const loadMonitorData = async () => {
  const data = await getTrainingMonitor()
  monitorData.value = data
}


const interveneDialogVisible = ref(false)

const interveneForm = reactive({
  recordId: '',
  studentName: '',
  command: ''
})

const openIntervene = (row) => {
  interveneForm.recordId = row.recordId
  interveneForm.studentName = row.studentName
  interveneForm.command = ''
  interveneDialogVisible.value = true
}

const submitIntervene = async () => {
  if (!interveneForm.command) {
    ElMessage.warning('请输入干预内容')
    return
  }

  await sendIntervention({
    recordId: interveneForm.recordId,
    studentName: interveneForm.studentName,
    command: interveneForm.command
  })

  ElMessage.success('远程干预指令已下发')
  interveneDialogVisible.value = false
}

const formatTime = time => {
  if (!time) return ''

  return time
    .replace('T', ' ')
    .substring(0, 19)
}
onMounted(() => {
  loadMonitorData()
  loadTrainingRecords()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.stat-card {
  margin-bottom: 16px;
}

.table-card {
  margin-top: 16px;
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
</style>