import { supportApi } from 'boot/axios'

// User Endpoints
export const getActiveConversation = () => supportApi.get('/v1/support/user/active')
export const sendMessageUser = (data) => supportApi.post('/v1/support/user/send', data)
export const escalateConversation = (conversationId) => supportApi.post(`/v1/support/user/escalate/${conversationId}`)
export const getMessagesUser = (conversationId, page = 0, size = 50) =>
  supportApi.get(`/v1/support/user/messages/${conversationId}?page=${page}&size=${size}`)
export const submitRating = (conversationId, data) => supportApi.post(`/v1/support/user/rate/${conversationId}`, data)
export const resolveConversationUser = (conversationId) => supportApi.post(`/v1/support/user/resolve/${conversationId}`)
export const checkAdminAvailability = () => supportApi.get('/v1/support/user/admin-availability')

// Admin Desk Endpoints
export const sendAdminHeartbeat = () => supportApi.post('/v1/support/admin/heartbeat')
export const sendAdminOffline = () => supportApi.post('/v1/support/admin/logout')
export const getPendingConversationsAdmin = () => supportApi.get('/v1/support/admin/pending')
export const getAssignedConversationsAdmin = () => supportApi.get('/v1/support/admin/assigned')
export const assignConversationAdmin = (conversationId) => supportApi.post(`/v1/support/admin/assign/${conversationId}`)
export const sendMessageAdmin = (data) => supportApi.post('/v1/support/admin/send', data)
export const transferConversationAdmin = (conversationId, data) => supportApi.post(`/v1/support/admin/transfer/${conversationId}`, data)
export const leaveConversationAdmin = (conversationId) => supportApi.post(`/v1/support/admin/leave/${conversationId}`)
export const resolveConversationAdmin = (conversationId) => supportApi.post(`/v1/support/admin/resolve/${conversationId}`)
export const getMessagesAdmin = (conversationId, page = 0, size = 100) =>
  supportApi.get(`/v1/support/admin/messages/${conversationId}?page=${page}&size=${size}`)
export const getCannedResponsesAdmin = () => supportApi.get('/v1/support/admin/canned-responses')
