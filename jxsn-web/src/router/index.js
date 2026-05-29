import { createRouter, createWebHistory } from 'vue-router'

const getToken = () => {
    return sessionStorage.getItem('token')
}

const getRole = () => {
    return (sessionStorage.getItem('role') || '').toLowerCase()
}

const getDefaultPath = () => {
    const role = getRole()

    if (role === 'student') {
        return '/student/training'
    }

    if (role === 'teacher') {
        return '/training/monitor'
    }

    if (role === 'admin' || role === 'researcher') {
        return '/training/monitor'
    }

    return '/login'
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
                path: '/student/training',
                component: () => import('../views/StudentTraining.vue'),
                meta: {
                    roles: ['student']
                }
            },
            {
                path: '/training/monitor',
                component: () => import('../views/TrainingMonitor.vue'),
                meta: {
                    roles: ['teacher', 'admin', 'researcher']
                }
            },
            {
                path: '/training/detail/:recordId',
                component: () => import('../views/TrainingDetail.vue'),
                meta: {
                    roles: ['teacher', 'admin', 'researcher']
                }
            },
            {
                path: '/guide/correction',
                component: () => import('../views/GuideCorrection.vue'),
                meta: {
                    roles: ['student', 'teacher', 'admin', 'researcher']
                }
            },
            {
                path: '/rule/manage',
                component: () => import('../views/RuleManage.vue'),
                meta: {
                    roles: ['admin', 'researcher']
                }
            }
        ]
    },
    {
        path: '/:pathMatch(.*)*',
        redirect: '/login'
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
        if (token && role) {
            next(getDefaultPath())
        } else {
            next()
        }
        return
    }

    if (!token || !role) {
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