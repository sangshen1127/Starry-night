import { del, get, patch, post, put, upload } from './http'

export function fetchAdminStats() {
  return get('/admin/stats')
}

export function fetchAdminPosts(params) {
  return get('/admin/posts', { params })
}

export function fetchAdminPost(id) {
  return get(`/admin/posts/${id}`)
}

export function createAdminPost(payload) {
  return post('/admin/posts', payload)
}

export function updateAdminPost(id, payload) {
  return put(`/admin/posts/${id}`, payload)
}

export function updateAdminPostStatus(id, status) {
  return patch(`/admin/posts/${id}/status`, { status })
}

export function deleteAdminPost(id) {
  return del(`/admin/posts/${id}`)
}

export function fetchAdminComments(params) {
  return get('/admin/comments', { params })
}

export function approveAdminComment(id) {
  return patch(`/admin/comments/${id}/status`)
}

export function deleteAdminComment(id) {
  return del(`/admin/comments/${id}`)
}

export function fetchAdminFriendLinks() {
  return get('/admin/friend-links')
}

export function createAdminFriendLink(payload) {
  return post('/admin/friend-links', payload)
}

export function updateAdminFriendLink(id, payload) {
  return put(`/admin/friend-links/${id}`, payload)
}

export function deleteAdminFriendLink(id) {
  return del(`/admin/friend-links/${id}`)
}

export function uploadAdminImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return upload('/admin/upload', formData)
}
