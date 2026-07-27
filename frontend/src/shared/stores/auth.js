import { reactive, computed } from 'vue'

const state = reactive({
  user: JSON.parse(localStorage.getItem('user')) || null,
  token: localStorage.getItem('token') || null,
  refreshToken: localStorage.getItem('refreshToken') || null
})

const authStore = {
  user: computed(() => state.user),
  token: computed(() => state.token),
  refreshToken: computed(() => state.refreshToken),
  isAuthenticated: computed(() => !!state.token),
  isAdmin: computed(() => state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isSuperAdmin: computed(() => state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isModerator: computed(() => state.user?.roles?.includes('ROLE_MODERATEUR') || state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isPoolAdmin: computed(() => state.user?.roles?.includes('ROLE_ADMIN_POOL') || state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isPaymentAdmin: computed(() => state.user?.roles?.includes('ROLE_ADMIN_PAYMENT') || state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  isSupportAgent: computed(() => state.user?.roles?.includes('ROLE_SUPPORT_AGENT') || state.user?.roles?.includes('ROLE_SUPER_ADMIN')),
  hasAdminAccess: computed(() => {
    const adminRoles = ['ROLE_SUPER_ADMIN', 'ROLE_MODERATEUR', 'ROLE_ADMIN_POOL', 'ROLE_ADMIN_PAYMENT', 'ROLE_SUPPORT_AGENT']
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

  setRefreshToken(refreshToken) {
    state.refreshToken = refreshToken
    if (refreshToken) {
      localStorage.setItem('refreshToken', refreshToken)
    } else {
      localStorage.removeItem('refreshToken')
    }
  },

  logout() {
    this.setUser(null)
    this.setToken(null)
    this.setRefreshToken(null)
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('guest_session_id')
  }
}

export default authStore
