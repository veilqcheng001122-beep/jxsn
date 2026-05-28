<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>智能指导与纠错</span>
        </div>
      </template>

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

        <el-form-item label="只看异常">
          <el-switch v-model="queryForm.onlyError" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadData">
            查询
          </el-button>

          <el-button @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
      >
        <el-table-column prop="studentName" label="学生姓名" width="100" />
        <el-table-column prop="studentNo" label="学号" width="120" />
        <el-table-column prop="processName" label="实训工序" width="120" />
        <el-table-column prop="stepIndex" label="步骤" width="80" />
        <el-table-column prop="realTimeValue" label="实时参数值" width="150" />
        <el-table-column prop="errorType" label="错误类型" width="140" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.isCorrect === 1 ? 'success' : 'danger'">
              {{ row.isCorrect === 1 ? '正常' : '异常' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="aiFeedback" label="AI指导建议" min-width="280" />

        <el-table-column label="操作时间" width="180">
          <template #default="{ row }">
            {{ formatTime(row.opTime) }}
          </template>
        </el-table-column>

        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="showAdvice(row)">
              查看建议
            </el-button>

            <el-button type="warning" link @click="createAdvice(row)">
              生成指导
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getGuideList, generateAdvice } from '@/api/guide'

const loading = ref(false)
const tableData = ref([])

const queryForm = reactive({
  studentName: '',
  studentNo: '',
  processName: '',
  onlyError: true
})

const loadData = async () => {
  loading.value = true

  try {
    const data = await getGuideList({
      studentName: queryForm.studentName,
      studentNo: queryForm.studentNo,
      processName: queryForm.processName,
      onlyError: queryForm.onlyError
    })

    tableData.value = data || []
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.studentName = ''
  queryForm.studentNo = ''
  queryForm.processName = ''
  queryForm.onlyError = true
  loadData()
}

const showAdvice = row => {
  ElMessageBox.alert(
    row.aiFeedback || '暂无指导建议，请点击“生成指导”。',
    'AI指导建议',
    {
      confirmButtonText: '确定'
    }
  )
}

const createAdvice = async row => {
  const advice = await generateAdvice({
    logId: row.logId,
    sessionId: row.sessionId,
    recordId: row.recordId,
    realTimeValue: row.realTimeValue,
    errorType: row.errorType
  })

  ElMessageBox.alert(advice, '生成的指导建议', {
    confirmButtonText: '确定'
  })

  ElMessage.success('指导建议生成成功')
  loadData()
}

const formatTime = time => {
  if (!time) return ''

  return time
    .replace('T', ' ')
    .substring(0, 19)
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.query-form {
  margin-bottom: 16px;
}
</style>