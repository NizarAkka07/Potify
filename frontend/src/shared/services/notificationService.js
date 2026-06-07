import { notificationApi } from 'boot/axios'

export default {
  async getUserNotifications(userId) {
    const response = await notificationApi.get(`/notifications/user/${userId}`)
    return response.data
  },

  async markNotificationRead(id) {
    const response = await notificationApi.patch(`/notifications/${id}/read`)
    return response.data
  }
}
