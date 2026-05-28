import { createRouter, createWebHistory } from 'vue-router'

const getToken = () => {
    return localStorage.getItem('token')
}

const getRole = () => {
    return localStorage.getItem('role') || 'teacher'
}

const getDefaultPath = () => {
    const role = getRole()

    if (role === 'student') {
        return '/guide/correction'
    }

    if (role === 'teacher') {
        return '/training/monitor'
    }

    if (role === 'admin') {
        return '/training/monitor'
    }

    return '/training/monitor'
}

const routes = [
    {
        path: '/login',
        component: () => import('../views/Login.vue')
    },
    {
        path: '/',
        component: () => import('../views/Layout.vue'),
        redirect: () => getDefaultPath(),
        children: [
            {
                path: 'training/monitor',
                component: () => import('../views/TrainingMonitor.vue'),
                meta: {
                    roles: ['teacher', 'admin']
                }
            },
            {
                path: 'training/detail/:recordId',
                component: () => import('../views/TrainingDetail.vue'),
                meta: {
                    roles: ['teacher', 'admin']
                }
            },
            {
                path: 'guide/correction',
                component: () => import('../views/GuideCorrection.vue'),
                meta: {
                    roles: ['student', 'teacher', 'admin']
                }
            },
            {
                path: 'rule/manage',
                component: () => import('../views/RuleManage.vue'),
                meta: {
                    roles: ['admin']
                }
            }
        ]
    }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

router.beforeEach((to, from, next) => {
    const token = getToken()
    const role = getRole()

    if (to.path === '/login') {
        if (token) {
            next(getDefaultPath())
        } else {
            next()
        }
        return
    }

    if (!token) {
        next('/login')
        return
    }

    if (to.meta.roles && !to.meta.roles.includes(role)) {
        next(getDefaultPath())
        return
    }

    next()
})

export default router