import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 20000
})

request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')

        if (token) {
            config.headers.Authorization = token
        }

        return config
    },
    error => {
        return Promise.reject(error)
    }
)

request.interceptors.response.use(
    response => {
        const res = response.data

        // 兼容后端统一返回格式：{ code: 200, data: xxx, message: xxx }
        if (res && typeof res === 'object' && 'code' in res) {
            if (res.code !== 200) {
                ElMessage.error(res.message || '请求失败')
                return Promise.reject(res)
            }

            return res.data
        }

        // 如果后端直接返回数据，也允许通过
        return res
    },
    error => {
        console.error('请求失败：', error)

        if (error.response) {
            ElMessage.error(error.response.data?.message || '请求失败')
        } else {
            ElMessage.error('服务器异常，请检查后端是否启动')
        }

        return Promise.reject(error)
    }
)

export default request