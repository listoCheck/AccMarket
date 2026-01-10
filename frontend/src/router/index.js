import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue')
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue')
  },
  {
    path: '/advertisements',
    name: 'Advertisements',
    component: () => import('../views/Advertisements.vue')
  },
  {
    path: '/advertisements/create',
    name: 'CreateAdvertisement',
    component: () => import('../views/CreateAdvertisement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/advertisements/:id/edit',
    name: 'EditAdvertisement',
    component: () => import('../views/EditAdvertisement.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/balance',
    name: 'Balance',
    component: () => import('../views/Balance.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/notifications',
    name: 'Notifications',
    component: () => import('../views/Notifications.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/admin/AdminPanel.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'AdminUsers',
    component: () => import('../views/admin/Users.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/moderation',
    name: 'Moderation',
    component: () => import('../views/admin/Moderation.vue'),
    meta: { requiresAuth: true, requiresModerator: true }
  },
  {
    path: '/appeals',
    name: 'Appeals',
    component: () => import('../views/Appeals.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()
  
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    next('/')
  } else if (to.meta.requiresModerator && !authStore.isModerator) {
    next('/')
  } else {
    next()
  }
})

export default router