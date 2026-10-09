import { handleMockRequest, isDemoModeActive, setDemoModeActive } from './mockBackend'

const BASE_URL = '/api'

const getHeaders = () => {
  const token = localStorage.getItem('tmd_token')
  const headers = {
    'Content-Type': 'application/json',
  }
  if (token) {
    headers['Authorization'] = `Bearer ${token}`
  }
  return headers
}

const handleResponse = async (response) => {
  if (!response.ok) {
    let errorMsg = 'An error occurred'
    try {
      const errorData = await response.json()
      errorMsg = errorData.message || errorData.error || errorMsg
    } catch (e) {
      errorMsg = response.statusText
    }
    throw new Error(errorMsg)
  }
  try {
    return await response.json()
  } catch (e) {
    return null
  }
}

/**
 * Universal API gateway with automatic fallback to in-browser Mock Engine
 */
export const api = {
  get: async (endpoint) => {
    if (isDemoModeActive()) {
      return handleMockRequest('GET', endpoint)
    }

    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'GET',
        headers: getHeaders(),
      })
      return await handleResponse(response)
    } catch (err) {
      console.warn(`[TMD API] Live backend unavailable for GET ${endpoint}. Falling back to In-Browser Demo Engine.`, err)
      setDemoModeActive(true)
      return handleMockRequest('GET', endpoint)
    }
  },

  post: async (endpoint, data) => {
    if (isDemoModeActive()) {
      return handleMockRequest('POST', endpoint, data)
    }

    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'POST',
        headers: getHeaders(),
        body: JSON.stringify(data),
      })
      return await handleResponse(response)
    } catch (err) {
      console.warn(`[TMD API] Live backend unavailable for POST ${endpoint}. Falling back to In-Browser Demo Engine.`, err)
      setDemoModeActive(true)
      return handleMockRequest('POST', endpoint, data)
    }
  },

  put: async (endpoint, data) => {
    if (isDemoModeActive()) {
      return handleMockRequest('PUT', endpoint, data)
    }

    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'PUT',
        headers: getHeaders(),
        body: JSON.stringify(data),
      })
      return await handleResponse(response)
    } catch (err) {
      console.warn(`[TMD API] Live backend unavailable for PUT ${endpoint}. Falling back to In-Browser Demo Engine.`, err)
      setDemoModeActive(true)
      return handleMockRequest('PUT', endpoint, data)
    }
  },

  delete: async (endpoint) => {
    if (isDemoModeActive()) {
      return handleMockRequest('DELETE', endpoint)
    }

    try {
      const response = await fetch(`${BASE_URL}${endpoint}`, {
        method: 'DELETE',
        headers: getHeaders(),
      })
      return await handleResponse(response)
    } catch (err) {
      console.warn(`[TMD API] Live backend unavailable for DELETE ${endpoint}. Falling back to In-Browser Demo Engine.`, err)
      setDemoModeActive(true)
      return handleMockRequest('DELETE', endpoint)
    }
  },
}
