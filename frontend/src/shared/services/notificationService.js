import { notificationApi } from 'boot/axios'

export default {
  async getUserNotifications(userId) {
    const response = await notificationApi.get(`/notifications/user/${userId}`)
    return response.data
  },

  async markNotificationRead(id) {
    const response = await notificationApi.patch(`/notifications/${id}/read`)
    return response.data
  },

  async sendWarning(userId, title, message) {
    const response = await notificationApi.post('/notifications/warn', { userId, title, message })
    return response.data
  },

  subscribeToNotifications(userId, onNotification, onError) {
    if (!userId) return null
    const host = window.location.hostname
    const sseUrl = `http://${host}:8084/api/notifications/stream/${userId}`
    const eventSource = new EventSource(sseUrl)

    eventSource.addEventListener('notification', (event) => {
      try {
        const notifData = JSON.parse(event.data)
        if (onNotification) onNotification(notifData)
      } catch (e) {
        console.error('Erreur parsing notification SSE:', e)
      }
    })

    eventSource.onerror = (err) => {
      console.warn('Connexion SSE notifications interrompue, reconnexion automatique...', err)
      if (onError) onError(err)
    }

    return eventSource
  }
}
