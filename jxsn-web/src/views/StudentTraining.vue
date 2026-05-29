<template>
  <div class="page-container">
    <el-card class="process-card">
      <template #header>
        <div class="card-header">
          <span>我的实训操作</span>
          <el-tag type="primary">学生端</el-tag>
        </div>
      </template>

      <el-steps
        :active="form.stepIndex - 1"
        finish-status="success"
        align-center
      >
        <el-step
          v-for="step in trainingSteps"
          :key="step.stepIndex"
          :title="step.title"
          :description="step.description"
        />
      </el-steps>
    </el-card>

    <el-card class="form-card">
      <template #header>
        <div class="card-header">
          <span>实训参数提交</span>
          <el-tag type="info">当前步骤：{{ currentStep?.title || '-' }}</el-tag>
        </div>
      </template>

      <el-form :model="form" label-width="120px" class="training-form">
        <el-form-item label="实训会话ID">
          <el-input-number
            v-model="form.sessionId"
            :min="1"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="操作步骤">
          <el-select
            v-model="form.stepIndex"
            placeholder="请选择当前实训步骤"
            style="width: 100%"
          >
            <el-option
              v-for="step in trainingSteps"
              :key="step.stepIndex"
              :label="`${step.stepIndex}. ${step.title}`"
              :value="step.stepIndex"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="参数类型">
          <el-select
            v-model="form.paramName"
            placeholder="请选择参数类型"
            style="width: 100%"
          >
            <el-option
              v-for="item in paramOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>

        <el-form-item v-if="currentParamInfo" label="标准范围">
          <el-alert
            :title="currentParamInfo.standard"
            :description="currentParamInfo.description"
            type="info"
            show-icon
            :closable="false"
          />
        </el-form-item>

        <el-form-item label="参数值">
          <el-input
            v-model="form.paramValue"
            :placeholder="currentParamInfo?.placeholder || '请输入参数值'"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitOperation">
            提交实训参数
          </el-button>

          <el-button @click="resetForm">
            重置
          </el-button>

          <el-button type="success" plain @click="loadInterventionList(false)">
            刷新教师建议
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="result" class="result-card">
      <template #header>
        <div class="card-header">
          <span>AI即时反馈</span>
          <el-tag type="success">AI自动生成</el-tag>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="提交参数">
          {{ result.realTimeValue }}
        </el-descriptions-item>

        <el-descriptions-item label="判断结果">
          <el-tag :type="Number(result.isCorrect) === 1 ? 'success' : 'danger'">
            {{ Number(result.isCorrect) === 1 ? '合规' : '异常' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="AI指导建议" :span="2">
          {{ result.aiFeedback }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <el-card class="teacher-card">
      <template #header>
        <div class="card-header">
          <span>教师干预建议</span>
          <el-tag type="warning">教师人工下发</el-tag>
        </div>
      </template>

      <el-empty
        v-if="interventionList.length === 0"
        description="暂无教师干预建议"
      />

      <el-timeline v-else>
        <el-timeline-item
          v-for="item in interventionList"
          :key="getInterventionKey(item)"
          :timestamp="formatTime(item.interventionTime || item.createTime)"
          type="warning"
        >
          <div class="intervention-item">
            <div class="intervention-title">
              <el-tag type="warning">教师干预</el-tag>
              <el-tag
                v-if="isInterventionRead(item)"
                type="success"
                size="small"
              >
                已查看
              </el-tag>
              <el-tag
                v-else
                type="danger"
                size="small"
              >
                未查看
              </el-tag>
            </div>

            <div class="intervention-content">
              {{ getInterventionContent(item) }}
            </div>

            <el-button
              v-if="!isInterventionRead(item)"
              type="primary"
              size="small"
              plain
              @click="markInterventionRead(item)"
            >
              我已查看该建议
            </el-button>
          </div>
        </el-timeline-item>
      </el-timeline>
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElNotification } from 'element-plus'
import {
  submitTrainingOperation,
  getInterventionList
} from '@/api/training'

let interventionTimer = null

const result = ref(null)
const interventionList = ref([])
const lastInterventionKey = ref('')
const readInterventionKeys = ref([])

const form = reactive({
  sessionId: 1,
  stepIndex: 1,
  paramName: '',
  paramValue: ''
})

const trainingSteps = [
  {
    stepIndex: 1,
    title: '原料处理',
    description: '称量、清洗、粉碎'
  },
  {
    stepIndex: 2,
    title: '蒸煮糊化',
    description: '控制温度与时长'
  },
  {
    stepIndex: 3,
    title: '摊凉加曲',
    description: '控制温度与接种条件'
  },
  {
    stepIndex: 4,
    title: '入池发酵',
    description: '控制湿度、密封与微生物'
  },
  {
    stepIndex: 5,
    title: '蒸馏操作',
    description: '控制压力与蒸馏状态'
  },
  {
    stepIndex: 6,
    title: '陈酿管理',
    description: '记录时长与环境条件'
  }
]

const paramOptions = [
  {
    label: '温度 temperature',
    value: 'temperature',
    standard: '标准范围：30℃ - 38℃',
    description: '温度过高或过低都会影响发酵、蒸煮或蒸馏效果。',
    placeholder: '例如：36'
  },
  {
    label: '湿度 humidity',
    value: 'humidity',
    standard: '标准范围：60% - 85%',
    description: '湿度异常可能影响发酵环境稳定性。',
    placeholder: '例如：75'
  },
  {
    label: '压力 pressure',
    value: 'pressure',
    standard: '标准范围：0.10MPa - 0.25MPa',
    description: '压力异常可能影响蒸馏安全与出酒质量。',
    placeholder: '例如：0.18'
  },
  {
    label: '时长 duration',
    value: 'duration',
    standard: '标准范围：60 - 120分钟',
    description: '工艺时长过短或过长都会影响实训结果。',
    placeholder: '例如：90'
  },
  {
    label: '微生物浓度 microbe',
    value: 'microbe',
    standard: '标准范围：0.3 - 0.8',
    description: '微生物浓度异常会影响发酵质量。',
    placeholder: '例如：0.5'
  },
  {
    label: '密封状态 seal_status',
    value: 'seal_status',
    standard: '标准要求：已密封 / 正常',
    description: '密封状态异常会影响发酵过程和安全性。',
    placeholder: '例如：已密封'
  }
]

const currentStep = computed(() => {
  return trainingSteps.find(item => item.stepIndex === form.stepIndex)
})

const currentParamInfo = computed(() => {
  return paramOptions.find(item => item.value === form.paramName)
})

const submitOperation = async () => {
  if (!form.sessionId) {
    ElMessage.warning('请输入实训会话ID')
    return
  }

  if (!form.stepIndex) {
    ElMessage.warning('请选择操作步骤')
    return
  }

  if (!form.paramName) {
    ElMessage.warning('请选择参数类型')
    return
  }

  if (!form.paramValue) {
    ElMessage.warning('请输入参数值')
    return
  }

  try {
    const data = await submitTrainingOperation({
      sessionId: form.sessionId,
      stepIndex: form.stepIndex,
      paramName: form.paramName,
      paramValue: form.paramValue
    })

    result.value = data

    if (Number(data.isCorrect) === 1) {
      ElMessage.success('参数提交成功，当前操作合规')
    } else {
      ElMessage.warning('参数提交成功，但系统检测到异常，请查看AI建议')
    }

    loadInterventionList(false)
  } catch (error) {
    console.error('提交实训参数失败：', error)
    ElMessage.error('提交失败，请检查后端接口或实训会话ID')
  }
}

const resetForm = () => {
  form.stepIndex = 1
  form.paramName = ''
  form.paramValue = ''
  result.value = null
}

const loadInterventionList = async (showNotice = true) => {
  if (!form.sessionId) return

  try {
    const data = await getInterventionList({
      sessionId: form.sessionId
    })

    const list = Array.isArray(data) ? data : []

    const latestItem = list[0]
    const latestKey = latestItem ? String(getInterventionKey(latestItem)) : ''

    if (
      showNotice &&
      latestKey &&
      lastInterventionKey.value &&
      latestKey !== lastInterventionKey.value
    ) {
      ElNotification({
        title: '收到教师干预建议',
        message: getInterventionContent(latestItem) || '教师已下发新的实训指导',
        type: 'warning',
        duration: 5000
      })
    }

    if (latestKey) {
      lastInterventionKey.value = latestKey
    }

    interventionList.value = list
  } catch (error) {
    console.error('获取教师干预记录失败：', error)
  }
}

const getInterventionKey = item => {
  return (
    item.interventionId ||
    item.id ||
    item.interventionTime ||
    item.createTime ||
    item.interventionAction
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

const isInterventionRead = item => {
  const key = String(getInterventionKey(item))
  return readInterventionKeys.value.includes(key)
}

const markInterventionRead = item => {
  const key = String(getInterventionKey(item))

  if (!readInterventionKeys.value.includes(key)) {
    readInterventionKeys.value.push(key)
  }

  ElMessage.success('已标记为查看')
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

watch(
  () => form.sessionId,
  () => {
    result.value = null
    interventionList.value = []
    lastInterventionKey.value = ''
    loadInterventionList(false)
  }
)

watch(
  () => form.paramName,
  () => {
    form.paramValue = ''
  }
)

onMounted(() => {
  loadInterventionList(false)

  interventionTimer = setInterval(() => {
    loadInterventionList(true)
  }, 2000)
})

onUnmounted(() => {
  if (interventionTimer) {
    clearInterval(interventionTimer)
    interventionTimer = null
  }
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.process-card,
.form-card,
.result-card,
.teacher-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.training-form {
  max-width: 720px;
}

.intervention-item {
  padding: 10px 0;
}

.intervention-title {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.intervention-content {
  margin-bottom: 10px;
  line-height: 1.6;
}
</style>