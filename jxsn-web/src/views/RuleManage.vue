<template>
  <div class="page-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>异常规则维护</span>

          <el-button type="primary" @click="openCreateDialog">
            新增规则
          </el-button>
        </div>
      </template>

      <!-- 查询区域 -->
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="规则名称">
          <el-input
            v-model="queryForm.ruleName"
            placeholder="请输入规则名称"
            clearable
          />
        </el-form-item>

        <el-form-item label="工序类型">
          <el-select
            v-model="queryForm.processType"
            placeholder="请选择工序"
            clearable
            style="width: 160px"
          >
            <el-option label="原料处理" value="raw_material" />
            <el-option label="发酵控制" value="fermentation" />
            <el-option label="蒸馏操作" value="distillation" />
            <el-option label="陈酿管理" value="aging" />
          </el-select>
        </el-form-item>

        <el-form-item label="状态">
          <el-select
            v-model="queryForm.status"
            placeholder="请选择状态"
            clearable
            style="width: 140px"
          >
            <el-option label="启用" value="enabled" />
            <el-option label="停用" value="disabled" />
          </el-select>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="loadRules">
            查询
          </el-button>

          <el-button @click="resetQuery">
            重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
      >
        <el-table-column prop="ruleId" label="规则ID" width="90" />
        <el-table-column prop="ruleName" label="规则名称" min-width="180" />
        <el-table-column prop="processName" label="工序类型" width="120" />
        <el-table-column prop="condition" label="触发条件" min-width="160" />
        <el-table-column prop="threshold" label="阈值范围" min-width="160" />
        <el-table-column prop="adviceTemplate" label="指导话术模板" min-width="280" />

        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'enabled' ? 'success' : 'info'">
              {{ row.status === 'enabled' ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="createTime" label="创建时间" width="180" />

        <el-table-column label="操作" width="230" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="openEditDialog(row)">
              编辑
            </el-button>

            <el-button type="warning" link @click="changeStatus(row)">
              {{ row.status === 'enabled' ? '停用' : '启用' }}
            </el-button>

            <el-button type="danger" link @click="removeRule(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增 / 编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '修改异常规则' : '新增异常规则'"
      width="620px"
    >
      <el-form :model="form" label-width="120px">
        <el-form-item label="规则名称">
          <el-input
            v-model="form.ruleName"
            placeholder="例如：发酵温度超阈值规则"
          />
        </el-form-item>

        <el-form-item label="工序类型">
          <el-select
            v-model="form.processType"
            placeholder="请选择工序类型"
            style="width: 100%"
          >
            <el-option label="原料处理" value="raw_material" />
            <el-option label="发酵控制" value="fermentation" />
            <el-option label="蒸馏操作" value="distillation" />
            <el-option label="陈酿管理" value="aging" />
          </el-select>
        </el-form-item>

        <el-form-item label="触发条件">
          <el-input
            v-model="form.condition"
            placeholder="例如：temperature > 38"
          />
        </el-form-item>

        <el-form-item label="阈值范围">
          <el-input
            v-model="form.threshold"
            placeholder="例如：30℃ - 38℃"
          />
        </el-form-item>

        <el-form-item label="指导话术模板">
          <el-input
            v-model="form.adviceTemplate"
            type="textarea"
            :rows="4"
            placeholder="例如：当前发酵温度偏高，请降低温度并重新观察发酵状态。"
          />
        </el-form-item>

        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="enabled">启用</el-radio>
            <el-radio label="disabled">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">
          取消
        </el-button>

        <el-button type="primary" @click="submitRule">
          保存
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getRuleList,
  createRule,
  updateRule,
  deleteRule,
  updateRuleStatus
} from '@/api/rule'

const loading = ref(false)
const tableData = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)

const queryForm = reactive({
  ruleName: '',
  processType: '',
  status: ''
})

const form = reactive({
  ruleId: '',
  ruleName: '',
  processType: '',
  condition: '',
  threshold: '',
  adviceTemplate: '',
  status: 'enabled'
})

const resetForm = () => {
  form.ruleId = ''
  form.ruleName = ''
  form.processType = ''
  form.condition = ''
  form.threshold = ''
  form.adviceTemplate = ''
  form.status = 'enabled'
}

const loadRules = async () => {
  loading.value = true

  try {
    const data = await getRuleList({
      ruleName: queryForm.ruleName,
      processType: queryForm.processType,
      status: queryForm.status
    })

    tableData.value = data || []
  } finally {
    loading.value = false
  }
}

const resetQuery = () => {
  queryForm.ruleName = ''
  queryForm.processType = ''
  queryForm.status = ''
  loadRules()
}

const openCreateDialog = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const openEditDialog = row => {
  isEdit.value = true

  form.ruleId = row.ruleId
  form.ruleName = row.ruleName
  form.processType = row.processType
  form.condition = row.condition
  form.threshold = row.threshold
  form.adviceTemplate = row.adviceTemplate
  form.status = row.status

  dialogVisible.value = true
}

const submitRule = async () => {
  if (!form.ruleName) {
    ElMessage.warning('请输入规则名称')
    return
  }

  if (!form.processType) {
    ElMessage.warning('请选择工序类型')
    return
  }

  if (!form.condition) {
    ElMessage.warning('请输入触发条件')
    return
  }

  if (!form.threshold) {
    ElMessage.warning('请输入阈值范围')
    return
  }

  if (!form.adviceTemplate) {
    ElMessage.warning('请输入指导话术模板')
    return
  }

  if (isEdit.value) {
    await updateRule({
      ruleId: form.ruleId,
      ruleName: form.ruleName,
      processType: form.processType,
      condition: form.condition,
      threshold: form.threshold,
      adviceTemplate: form.adviceTemplate,
      status: form.status
    })

    ElMessage.success('规则修改成功')
  } else {
    await createRule({
      ruleName: form.ruleName,
      processType: form.processType,
      condition: form.condition,
      threshold: form.threshold,
      adviceTemplate: form.adviceTemplate,
      status: form.status
    })

    ElMessage.success('规则新增成功')
  }

  dialogVisible.value = false
  loadRules()
}

const changeStatus = async row => {
  const newStatus = row.status === 'enabled' ? 'disabled' : 'enabled'

  await updateRuleStatus({
    ruleId: row.ruleId,
    status: newStatus
  })

  ElMessage.success(newStatus === 'enabled' ? '规则已启用' : '规则已停用')
  loadRules()
}

const removeRule = row => {
  ElMessageBox.confirm(
    `确定要删除规则「${row.ruleName}」吗？`,
    '删除确认',
    {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }
  ).then(async () => {
    await deleteRule(row.ruleId)
    ElMessage.success('规则删除成功')
    loadRules()
  })
}

onMounted(() => {
  loadRules()
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