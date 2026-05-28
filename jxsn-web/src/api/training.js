import request from './request'

// 获取监控大屏统计数据
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

export function sendIntervention(data) {
    return request({
        url: '/training/intervene',
        method: 'post',
        data
    })
}

export function getTrainingDetail(recordId) {
    return request({
        url: '/training/detail',
        method: 'get',
        params: {
            recordId
        }
    })
}