import axios from 'axios'
import { ElMessage } from 'element-plus'

const request = axios.create({
    baseURL: '/api',
    timeout: 20000
})

request.interceptors.response.use(
    response => {
        const res = response.data

        if (res.code !== 200) {
            ElMessage.error(res.message || '请求失败')
            return Promise.reject(res)
        }

        return res.data
    },
    error => {
        ElMessage.error(error.message || '服务器异常')
        return Promise.reject(error)
    }
)

export default request