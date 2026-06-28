import { defineRouter } from '#q-app/wrappers'
import { createRouter, createMemoryHistory, createWebHistory, createWebHashHistory } from 'vue-router'
import routes from './routes'

/*
 * If not building with SSR mode, you can
 * directly export the Router instantiation;
 *
 * The function below can be async too; either use
 * async/await or return a Promise which resolves
 * with the Router instance.
 */

export default defineRouter((/* { store, ssrContext } */) => {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : (process.env.VUE_ROUTER_MODE === 'history' ? createWebHistory : createWebHashHistory)

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    // Leave this as is and make changes in quasar.conf.js instead!
    // quasar.conf.js -> build -> vueRouterMode
    // quasar.conf.js -> build -> publicPath
    history: createHistory(process.env.VUE_ROUTER_BASE)
  })

  Router.beforeEach((to) => {
    const token = localStorage.getItem('token')
    const userStr = localStorage.getItem('user')
    const user = userStr ? JSON.parse(userStr) : null
    const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
    const isAdminPath = to.path.startsWith('/admin')

    if (requiresAuth && !token) {
      return '/login'
    }

    if (isAdminPath) {
      if (!token) {
        return '/login'
      }
      const adminRoles = ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_MODERATEUR', 'ROLE_ADMIN_POOL', 'ROLE_ADMIN_PAYMENT']
      const hasAdminRole = user?.roles?.some(role => adminRoles.includes(role))
      if (!hasAdminRole) {
        return '/' // Non-authorized redirects to homepage
      }
    }
    
    if ((to.path === '/login' || to.path === '/register') && token) {
      return '/'
    }
    
    return true
  })

  return Router
})
