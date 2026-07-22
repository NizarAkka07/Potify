import { api } from 'boot/axios'

// User endpoints
export const getUsers = () => api.get('/v1/users')
export const getUserById = (id) => api.get(`/v1/users/${id}`)
export const createUser = (data) => api.post('/v1/users', data)
export const updateUser = (id, data) => api.put(`/v1/users/${id}`, data)
export const updateProfile = (id, data) => api.put(`/v1/users/${id}/profile`, data)
export const changePassword = (id, data) => api.put(`/v1/users/${id}/password`, data)
export const assignRoleToUser = (userId, roleId) => api.post(`/v1/access/users/${userId}/roles/${roleId}`)
export const removeRoleFromUser = (userId, roleId) => api.delete(`/v1/access/users/${userId}/roles/${roleId}`)
export const getUserRoles = (userId) => api.get(`/v1/access/users/${userId}/roles`)
export const getPendingVerificationUsers = () => api.get('/v1/users/pending-verification')
export const approveUserVerification = (id) => api.post(`/v1/users/${id}/approve-verification`)
export const rejectUserVerification = (id) => api.post(`/v1/users/${id}/reject-verification`)
export const updateAdminVerification = (id, approved) => api.put(`/v1/users/${id}/admin-verification`, { approved })

// Role endpoints
export const getRoles = () => api.get('/v1/access/roles')
export const getRoleById = (id) => api.get(`/v1/access/roles/${id}`)
export const createRole = (data) => api.post('/v1/access/roles', data)
export const updateRole = (id, data) => api.put(`/v1/access/roles/${id}`, data)
export const deleteRole = (id) => api.delete(`/v1/access/roles/${id}`)
export const assignPermissionToRole = (roleId, permissionId) => api.post(`/v1/access/roles/${roleId}/permissions/${permissionId}`)
export const removePermissionFromRole = (roleId, permissionId) => api.delete(`/v1/access/roles/${roleId}/permissions/${permissionId}`)

// Permission endpoints
export const getPermissions = () => api.get('/v1/access/permissions')
export const getPermissionById = (id) => api.get(`/v1/access/permissions/${id}`)
export const createPermission = (data) => api.post('/v1/access/permissions', data)
export const updatePermission = (id, data) => api.put(`/v1/access/permissions/${id}`, data)
export const deletePermission = (id) => api.delete(`/v1/access/permissions/${id}`)
