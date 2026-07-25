import { createContext, useContext, useEffect, useState } from 'react'
import { api } from '../lib/api'

const AuthContext = createContext({})

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchUser = async () => {
      const token = localStorage.getItem('tmd_token')
      if (token) {
        try {
          const userData = await api.get('/auth/profile')
          // Assume backend returns { user: {...} } or just the user object
          setUser(userData.user || userData)
        } catch (error) {
          console.error('Failed to fetch profile', error)
          localStorage.removeItem('tmd_token')
          setUser(null)
        }
      } else {
        setUser(null)
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

  // Will be passed down to AuthContext.Provider
  const value = {
    signUp: async (data) => {
      // Assuming data is { email, password, options: { data: { name } } } from supabase
      const payload = {
        email: data.email,
        password: data.password,
        name: data.options?.data?.full_name || data.options?.data?.name || data.name || 'User'
      }
      const response = await api.post('/auth/register', payload)
      if (response.token) {
        localStorage.setItem('tmd_token', response.token)
        setUser(response.user)
      }
      return { data: response, error: null }
    },
    signIn: async (data) => {
      const response = await api.post('/auth/login', { email: data.email, password: data.password })
      if (response.token) {
        localStorage.setItem('tmd_token', response.token)
        setUser(response.user)
      }
      return { data: response, error: null }
    },
    signOut: async () => {
      localStorage.removeItem('tmd_token')
      setUser(null)
    },
    setUser: (userData, token) => {
      if (token) localStorage.setItem('tmd_token', token)
      setUser(userData)
    },
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
