import { get, post } from './http'

export function fetchCurrentUser() {
  return get('/auth/me')
}

export function loginRequest(payload) {
  return post('/auth/login', payload)
}

export function logoutRequest() {
  return post('/auth/logout')
}
