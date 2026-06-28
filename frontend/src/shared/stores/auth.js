import { reactive, computed } from 'vue'

const state = reactive({
  user: JSON.parse(localStorage.getItem('user')) || null,
  token: localStorage.getItem('token') || null
})

const authStore = {
  user: computed(() => state.user),
  token: computed(() => state.token),
  isAuthenticated: computed(() => !!state.token),
  isAdmin: computed(() => state.user?.roles?.includes('ROLE_ADMIN') || state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isSuperAdmin: computed(() => state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isModerator: computed(() => state.user?.roles?.includes('ROLE_MODERATEUR') || state.user?.roles?.includes('ROLE_SUPER_ADMIN') || state.user?.roles?.includes('ROLE_ADMIN')),
  isPoolAdmin: computed(() => state.user?.roles?.includes('ROLE_ADMIN_POOL') || state.user?.roles?.includes('ROLE_SUPER_ADMIN') || state.user?.roles?.includes('ROLE_ADMIN')),
  isPaymentAdmin: computed(() => state.user?.roles?.includes('ROLE_ADMIN_PAYMENT') || state.user?.roles?.includes('ROLE_SUPER_ADMIN') || state.user?.roles?.includes('ROLE_ADMIN')),
  hasAdminAccess: computed(() => {
    const adminRoles = ['ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_MODERATEUR', 'ROLE_ADMIN_POOL', 'ROLE_ADMIN_PAYMENT']
    return state.user?.roles?.some(role => adminRoles.includes(role))
  }),

  setUser(user) {
    state.user = user
    if (user) {
      localStorage.setItem('user', JSON.stringify(user))
    } else {
      localStorage.removeItem('user')
    }
  },

  setToken(token) {
    state.token = token
    if (token) {
      localStorage.setItem('token', token)
    } else {
      localStorage.removeItem('token')
    }
  },

  logout() {
    this.setUser(null)
    this.setToken(null)
  }
}

export default authStore
