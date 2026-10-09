import { createContext, useContext, useEffect, useState } from 'react'
import { api } from '../lib/api'
import { isDemoModeActive, setDemoModeActive, resetDemoDB, getDemoDB } from '../lib/mockBackend'
import { INITIAL_DEMO_USER } from '../lib/demoData'

const AuthContext = createContext({})

export const AuthProvider = ({ children }) => {
  const [user, setUserState] = useState(null)
  const [loading, setLoading] = useState(true)
  const [isDemo, setIsDemo] = useState(isDemoModeActive())

  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem('tmd_token')
      if (token) {
        try {
          const userData = await api.get('/auth/profile')
          // Assume backend returns { user: {...} } or just the user object
          setUserState(userData?.user || userData || INITIAL_DEMO_USER)
          setIsDemo(isDemoModeActive())
        } catch (error) {
          console.error('Failed to fetch profile, checking demo mode:', error)
          if (isDemoModeActive() || token.includes('demo')) {
            const db = getDemoDB()
            setUserState(db.user)
            setIsDemo(true)
          } else {
            localStorage.removeItem('tmd_token')
            setUserState(null)
          }
        }
      } else {
        setUserState(null)
      }
      setLoading(false)
    }

    fetchUser()
  }, [])

  const hasCompletedOnboarding = () => {
    if (!user) return true
    if (user.email && localStorage.getItem(`tmd_onboarding_completed_${user.email}`) === 'true') {
      return true
    }
    if (user.date_of_birth || user.user_metadata?.date_of_birth) {
      return true
    }
    return false
  }

  const setOnboardingCompleted = () => {
    if (user?.email) {
      localStorage.setItem(`tmd_onboarding_completed_${user.email}`, 'true')
    }
  }

  const demoLogin = async () => {
    setDemoModeActive(true)
    setIsDemo(true)
    const db = getDemoDB()
    localStorage.setItem('tmd_token', 'demo-mock-jwt-token-active')
    localStorage.setItem(`tmd_onboarding_completed_${db.user.email}`, 'true')
    setUserState(db.user)
    return { data: { user: db.user, token: 'demo-mock-jwt-token-active' }, error: null }
  }

  const resetDemoState = () => {
    const freshDb = resetDemoDB()
    setUserState(freshDb.user)
    setIsDemo(true)
  }

  // Will be passed down to AuthContext.Provider
  const value = {
    signUp: async (data) => {
      const payload = {
        email: data.email,
        password: data.password,
        name: data.options?.data?.full_name || data.options?.data?.name || data.name || 'User'
      }
      const response = await api.post('/auth/register', payload)
      if (response?.token) {
        localStorage.setItem('tmd_token', response.token)
        setUserState(response.user)
      }
      return { data: response, error: null }
    },
    signIn: async (data) => {
      const response = await api.post('/auth/login', { email: data.email, password: data.password })
      if (response?.token) {
        localStorage.setItem('tmd_token', response.token)
        setUserState(response.user)
      }
      return { data: response, error: null }
    },
    signOut: async () => {
      localStorage.removeItem('tmd_token')
      setDemoModeActive(false)
      setIsDemo(false)
      setUserState(null)
    },
    setUser: (userData, token) => {
      if (token) localStorage.setItem('tmd_token', token)
      setUserState(userData)
    },
    demoLogin,
    resetDemoState,
    isDemoMode: isDemo,
    user,
    loading,
    hasCompletedOnboarding: hasCompletedOnboarding(),
    setOnboardingCompleted
  }

  return (
    <AuthContext.Provider value={value}>
      {!loading && children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => {
  return useContext(AuthContext)
}
