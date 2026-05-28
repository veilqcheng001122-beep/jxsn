import request from './request'

// 查询异常规则
export function getRuleList(params) {
    return request({
        url: '/rule/list',
        method: 'get',
        params
    })
}

// 新增异常规则
export function createRule(data) {
    return request({
        url: '/rule/create',
        method: 'post',
        data
    })
}

// 修改异常规则
export function updateRule(data) {
    return request({
        url: '/rule/update',
        method: 'put',
        data
    })
}

// 删除异常规则
export function deleteRule(ruleId) {
    return request({
        url: '/rule/delete',
        method: 'delete',
        params: {
            ruleId
        }
    })
}

// 启用 / 停用异常规则
export function updateRuleStatus(data) {
    return request({
        url: '/rule/status',
        method: 'put',
        data
    })
}