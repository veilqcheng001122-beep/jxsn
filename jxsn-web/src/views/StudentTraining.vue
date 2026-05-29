<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>我的实训操作</span>
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
          <el-input-number
            v-model="form.stepIndex"
            :min="1"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="参数类型">
          <el-select
            v-model="form.paramName"
            placeholder="请选择参数类型"
            style="width: 100%"
          >
            <el-option label="温度 temperature" value="temperature" />
            <el-option label="湿度 humidity" value="humidity" />
            <el-option label="压力 pressure" value="pressure" />
            <el-option label="时长 duration" value="duration" />
            <el-option label="微生物浓度 microbe" value="microbe" />
            <el-option label="密封状态 seal_status" value="seal_status" />
          </el-select>
        </el-form-item>

        <el-form-item label="参数值">
          <el-input
            v-model="form.paramValue"
            placeholder="例如：36、95、0.32、已密封"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="submitOperation">
            提交实训参数
          </el-button>

          <el-button @click="resetForm">
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card v-if="result" class="result-card">
      <template #header>
        提交结果
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="参数值">
          {{ result.realTimeValue }}
        </el-descriptions-item>

        <el-descriptions-item label="判断结果">
          <el-tag :type="result.isCorrect === 1 ? 'success' : 'danger'">
            {{ result.isCorrect === 1 ? '合规' : '异常' }}
          </el-tag>
        </el-descriptions-item>

        <el-descriptions-item label="AI指导建议" :span="2">
          {{ result.aiFeedback }}
        </el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { submitTrainingOperation } from '@/api/training'

const result = ref(null)

const form = reactive({
  sessionId: 1,
  stepIndex: 1,
  paramName: '',
  paramValue: ''
})

const submitOperation = async () => {
  if (!form.sessionId) {
    ElMessage.warning('请输入实训会话ID')
    return
  }

  if (!form.stepIndex) {
    ElMessage.warning('请输入操作步骤')
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

  const data = await submitTrainingOperation({
    sessionId: form.sessionId,
    stepIndex: form.stepIndex,
    paramName: form.paramName,
    paramValue: form.paramValue
  })

  result.value = data

  if (data.isCorrect === 1) {
    ElMessage.success('参数提交成功，当前操作合规')
  } else {
    ElMessage.warning('参数提交成功，但系统检测到异常')
  }
}

const resetForm = () => {
  form.stepIndex = 1
  form.paramName = ''
  form.paramValue = ''
  result.value = null
}
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

.training-form {
  max-width: 620px;
}

.result-card {
  margin-top: 16px;
}
</style>