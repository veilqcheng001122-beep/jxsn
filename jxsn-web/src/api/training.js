import request from './request'

// 获取教师端监控统计数据
export function getTrainingMonitor() {
    return request({
        url: '/training/monitor',
        method: 'get'
    })
}

// 获取实训记录列表
export function getTrainingRecords(params) {
    return request({
        url: '/training/records',
        method: 'get',
        params
    })
}

// 获取实训详情
export function getTrainingDetail(recordId) {
    return request({
        url: '/training/detail',
        method: 'get',
        params: {
            recordId
        }
    })
}

// 获取实训内容配置：工序、步骤、参数、标准范围
// 后续由队友提供真实接口；当前学生端会自动兜底，不影响页面使用
export function getTrainingContent(sessionId) {
    return request({
        url: '/training/content',
        method: 'get',
        params: {
            sessionId
        }
    })
}

// 教师远程干预
export function sendIntervention(data) {
    return request({
        url: '/training/intervene',
        method: 'post',
        data
    })
}

// 学生提交实训操作参数
export function submitTrainingOperation(data) {
    return request({
        url: '/training/operation/submit',
        method: 'post',
        data
    })
}

// 查询教师干预记录
export function getInterventionList(params) {
    return request({
        url: '/training/intervention/list',
        method: 'get',
        params
    })
}

// 学生标记教师干预为已读
export function markInterventionRead(interventionId) {
    return request({
        url: '/training/intervention/read',
        method: 'put',
        data: {
            interventionId
        }
    })
}