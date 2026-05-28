import request from './request'

export function getGuideList(params) {
    return request({
        url: '/guide/list',
        method: 'get',
        params
    })
}

export function generateAdvice(data) {
    return request({
        url: '/guide/advice/generate',
        method: 'post',
        data
    })
}