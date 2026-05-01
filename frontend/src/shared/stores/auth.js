import { reactive, computed } from 'vue'

const state = reactive({
  user: JSON.parse(localStorage.getItem('user')) || null,
  token: localStorage.getItem('token') || null
})

const authStore = {
  user: computed(() => state.user),
  token: computed(() => state.token),
  isAuthenticated: computed(() => !!state.token),
  isAdmin: computed(() => state.user?.roles?.includes('ROLE_ADMIN')),

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
