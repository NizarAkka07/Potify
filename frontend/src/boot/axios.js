import { boot } from 'quasar/wrappers'
import axios from 'axios'

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const host = window.location.hostname
const api = axios.create({ baseURL: `http://${host}:8081/api` })
const poolApi = axios.create({ baseURL: `http://${host}:8082/api` })
const paymentApi = axios.create({ baseURL: `http://${host}:8083/api` })
const notificationApi = axios.create({ baseURL: `http://${host}:8084/api` })

export default boot(({ app }) => {
  // for use inside Vue files (Options API) through this.$axios and this.$api
  app.config.globalProperties.$axios = axios
  app.config.globalProperties.$api = api
  app.config.globalProperties.$poolApi = poolApi
  app.config.globalProperties.$paymentApi = paymentApi
  app.config.globalProperties.$notificationApi = notificationApi
})

// Request interceptor for API calls
const requestInterceptor = (config) => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`
  }
  return config
}

api.interceptors.request.use(requestInterceptor, (error) => Promise.reject(error))
poolApi.interceptors.request.use(requestInterceptor, (error) => Promise.reject(error))
paymentApi.interceptors.request.use(requestInterceptor, (error) => Promise.reject(error))
notificationApi.interceptors.request.use(requestInterceptor, (error) => Promise.reject(error))

// Response interceptor for handling 401 errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token expired or invalid
      localStorage.removeItem('token')
      window.location.href = '#/login' // Redirect to login
    }
    return Promise.reject(error)
  }
)

paymentApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '#/login'
    }
    return Promise.reject(error)
  }
)

notificationApi.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      window.location.href = '#/login'
    }
    return Promise.reject(error)
  }
)

export { api, poolApi, paymentApi, notificationApi }
