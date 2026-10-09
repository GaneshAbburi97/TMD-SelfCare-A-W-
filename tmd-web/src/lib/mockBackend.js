import {
  INITIAL_DEMO_USER,
  INITIAL_PAIN_RECORDS,
  INITIAL_EXERCISE_RECORDS,
  INITIAL_SLEEP_RECORDS,
  INITIAL_WELLNESS_RECORDS,
  getMockAiResponse
} from './demoData'

const DEMO_DB_KEY = 'tmd_demo_db_v2'
const DEMO_MODE_FLAG = 'tmd_demo_mode_active'

// Simulated realistic network delay
const delay = (ms = 180) => new Promise(resolve => setTimeout(resolve, ms))

/**
 * Retrieve or initialize the in-browser mock database
 */
export const getDemoDB = () => {
  try {
    const raw = localStorage.getItem(DEMO_DB_KEY)
    if (raw) {
      return JSON.parse(raw)
    }
  } catch (err) {
    console.error('Failed to parse demo DB, resetting:', err)
  }

  const freshDB = {
    user: { ...INITIAL_DEMO_USER },
    pain: [...INITIAL_PAIN_RECORDS],
    exercise: [...INITIAL_EXERCISE_RECORDS],
    sleep: [...INITIAL_SLEEP_RECORDS],
    wellness: [...INITIAL_WELLNESS_RECORDS],
    reports: [],
    messages: []
  }

  saveDemoDB(freshDB)
  return freshDB
}

/**
 * Save current state back to LocalStorage
 */
export const saveDemoDB = (db) => {
  try {
    localStorage.setItem(DEMO_DB_KEY, JSON.stringify(db))
  } catch (err) {
    console.error('Failed to save demo DB:', err)
  }
}

/**
 * Reset demo database to initial state
 */
export const resetDemoDB = () => {
  localStorage.removeItem(DEMO_DB_KEY)
  return getDemoDB()
}

/**
 * Check if demo mode is active
 */
export const isDemoModeActive = () => {
  return localStorage.getItem(DEMO_MODE_FLAG) === 'true'
}

/**
 * Set demo mode state
 */
export const setDemoModeActive = (active) => {
  if (active) {
    localStorage.setItem(DEMO_MODE_FLAG, 'true')
    localStorage.setItem('tmd_token', 'demo-mock-jwt-token-active')
  } else {
    localStorage.removeItem(DEMO_MODE_FLAG)
  }
}

/**
 * Comprehensive in-browser REST router
 */
export const handleMockRequest = async (method, endpoint, data = null) => {
  await delay(180)
  const db = getDemoDB()
  const cleanEndpoint = endpoint.split('?')[0]

  // 1. AUTH ENDPOINTS
  if (cleanEndpoint === '/auth/profile') {
    if (method === 'GET') {
      return { user: db.user, success: true }
    }
    if (method === 'PUT') {
      db.user = {
        ...db.user,
        ...data,
        user_metadata: {
          ...(db.user.user_metadata || {}),
          ...data
        }
      }
      saveDemoDB(db)
      return db.user
    }
    if (method === 'DELETE') {
      resetDemoDB()
      localStorage.removeItem('tmd_token')
      return { success: true, message: 'Account deleted in demo environment.' }
    }
  }

  if (cleanEndpoint === '/auth/login' || cleanEndpoint === '/auth/register' || cleanEndpoint === '/auth/google') {
    setDemoModeActive(true)
    if (data?.name || data?.email) {
      db.user.name = data.name || db.user.name
      db.user.email = data.email || db.user.email
      saveDemoDB(db)
    }
    return {
      token: 'demo-mock-jwt-token-active',
      user: db.user,
      success: true
    }
  }

  if (cleanEndpoint === '/auth/forgot-password' || cleanEndpoint === '/auth/verify-otp' || cleanEndpoint === '/auth/reset-password') {
    return { success: true, message: 'Mock OTP and password reset processed successfully.' }
  }

  // 2. PAIN RECORDS
  if (cleanEndpoint === '/pain') {
    if (method === 'GET') {
      return [...db.pain]
    }
    if (method === 'POST') {
      const newRecord = {
        id: `pain-${Date.now()}`,
        user_id: db.user.id,
        created_at: new Date().toISOString(),
        ...data
      }
      db.pain.unshift(newRecord)
      saveDemoDB(db)
      return newRecord
    }
  }

  // 3. EXERCISE RECORDS
  if (cleanEndpoint === '/exercise') {
    if (method === 'GET') {
      return [...db.exercise]
    }
    if (method === 'POST') {
      const newRecord = {
        id: `ex-${Date.now()}`,
        created_at: new Date().toISOString(),
        ...data
      }
      db.exercise.unshift(newRecord)
      saveDemoDB(db)
      return newRecord
    }
  }

  // 4. SLEEP RECORDS
  if (cleanEndpoint === '/sleep') {
    if (method === 'GET') {
      return [...db.sleep]
    }
    if (method === 'POST') {
      const newRecord = {
        id: `sleep-${Date.now()}`,
        created_at: new Date().toISOString(),
        ...data
      }
      db.sleep.unshift(newRecord)
      saveDemoDB(db)
      return newRecord
    }
  }

  // 5. WELLNESS RECORDS
  if (cleanEndpoint === '/wellness') {
    if (method === 'GET') {
      return [...db.wellness]
    }
    if (method === 'POST') {
      const newRecord = {
        id: `well-${Date.now()}`,
        created_at: new Date().toISOString(),
        ...data
      }
      db.wellness.unshift(newRecord)
      saveDemoDB(db)
      return newRecord
    }
  }

  // 6. REPORTS
  if (cleanEndpoint === '/reports') {
    if (method === 'GET') {
      return [...db.reports]
    }
    if (method === 'POST') {
      const newReport = {
        id: `report-${Date.now()}`,
        created_at: new Date().toISOString(),
        pdf_data: data?.pdf_data ? 'data:application/pdf;base64,...' : null
      }
      db.reports.unshift(newReport)
      saveDemoDB(db)
      return { success: true, report: newReport }
    }
  }

  // 7. AI CLINICAL CHATBOT
  if (cleanEndpoint === '/chat') {
    await delay(350) // simulate thoughtful AI synthesis
    const messages = data?.messages || []
    const lastUserMsg = [...messages].reverse().find(m => m.role === 'user')?.content || ''
    const botReply = getMockAiResponse(lastUserMsg)
    return {
      reply: botReply,
      choices: [{ message: { role: 'assistant', content: botReply } }]
    }
  }

  // 8. CONTACT SUPPORT
  if (cleanEndpoint === '/contact') {
    return {
      success: true,
      message: 'Support request logged in demo mode. Our team will contact you.'
    }
  }

  // Default fallback for any unknown endpoint
  return { success: true, data: null, message: 'Mock endpoint handled' }
}
