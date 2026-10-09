import { useState, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'
import { UserPlus, Sparkles, ShieldCheck, ArrowRight } from 'lucide-react'
import GoogleAuthButton from '../../components/GoogleAuthButton'

const PASSWORD_SPACE_ERROR = 'Password cannot contain spaces.'

const getAuthRedirectError = () => {
  const searchParams = new URLSearchParams(window.location.search)
  const hashParams = new URLSearchParams(window.location.hash.substring(1))
  const errorDesc = searchParams.get('error_description') || searchParams.get('error') ||
                    hashParams.get('error_description') || hashParams.get('error')

  return errorDesc ? decodeURIComponent(errorDesc.replace(/\+/g, ' ')) : null
}

export default function Signup() {
  const [name, setName] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState(() => getAuthRedirectError())
  const [loading, setLoading] = useState(false)
  const [demoLoading, setDemoLoading] = useState(false)
  const [successMsg, setSuccessMsg] = useState('')
  
  const { signUp, demoLogin, user } = useAuth()
  const navigate = useNavigate()

  useEffect(() => {
    if (getAuthRedirectError()) {
      window.history.replaceState({}, document.title, window.location.pathname)
    }

    if (user) {
      navigate('/dashboard')
    }
  }, [user, navigate])

  const handleDemoAccess = async () => {
    setDemoLoading(true)
    setError(null)
    try {
      await demoLogin()
      navigate('/dashboard')
    } catch (err) {
      setError('Failed to initialize demo mode: ' + err.message)
    } finally {
      setDemoLoading(false)
    }
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setSuccessMsg('')

    if (!name.trim() || !email.trim() || !password) {
      setError('Please fill out all required fields.')
      return
    }

    const trimmedName = name.trim()
    const trimmedEmail = email.trim()
    
    // Name Validation
    if (!/^[a-zA-Z]/.test(trimmedName)) {
      setError('Name must start with a letter (no numbers or symbols at the beginning).')
      return
    }
    if (!/^[a-zA-Z\s]+$/.test(trimmedName)) {
      setError('Name can only contain letters and spaces.')
      return
    }

    // Email Validation
    if (trimmedEmail !== trimmedEmail.toLowerCase()) {
      setError('Email must be entirely in lowercase letters.')
      return
    }

    if (!/^[a-z]/.test(trimmedEmail)) {
      setError('Email must start with a lowercase letter (a-z). Numbers at the start are not allowed.')
      return
    }

    const emailRegex = /^[a-z][a-z0-9._%+-]*@[a-z0-9.-]+\.[a-z]{2,}$/
    if (!emailRegex.test(trimmedEmail)) {
      setError('Please enter a valid email address.')
      return
    }

    const domain = trimmedEmail.split('@')[1]
    
    // Strict Whitelist of Allowed Email Providers
    const allowedDomains = [
      'gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com', 
      'icloud.com', 'aol.com', 'protonmail.com', 'zoho.com', 'tmdcare.com'
    ]
    
    if (!allowedDomains.includes(domain)) {
      setError(`Email domain @${domain} is not allowed. Please use a recognized provider like @gmail.com or @yahoo.com.`)
      return
    }

    if (password.length < 6) {
      setError('Password must be at least 6 characters long.')
      return
    }

    if (/\s/.test(password)) {
      setError(PASSWORD_SPACE_ERROR)
      return
    }

    setLoading(true)

    try {
      const { data, error } = await signUp({ 
        email: trimmedEmail, 
        password,
        options: {
          data: {
            full_name: trimmedName
          }
        }
      })
      if (error) throw error
      
      if (data?.user?.identities?.length === 0) {
        setError('This email is already registered.')
      } else {
        setSuccessMsg('Successfully signed up! Proceeding to your portal...')
        setTimeout(() => navigate('/dashboard'), 1500)
      }
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handlePasswordChange = (e) => {
    const nextPassword = e.target.value
    if (/\s/.test(nextPassword)) {
      setError(PASSWORD_SPACE_ERROR)
      setPassword(nextPassword.replace(/\s/g, ''))
      return
    }
    if (error === PASSWORD_SPACE_ERROR) setError(null)
    setPassword(nextPassword)
  }

  return (
    <div className="app-container" style={{ alignItems: 'center', justifyContent: 'center', padding: '1.5rem 1rem' }}>
      <div className="glass-panel" style={{ padding: '2rem', width: '100%', maxWidth: '420px', borderRadius: '16px' }}>
        
        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <h1 className="gradient-text" style={{ fontSize: '2rem', marginBottom: '0.25rem' }}>Create Account</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem' }}>Join the TMD Self-Care Platform</p>
        </div>

        {/* ⚡ One-Click Instant Demo Box */}
        <div style={{
          background: 'linear-gradient(135deg, rgba(37, 99, 235, 0.12), rgba(16, 185, 129, 0.12))',
          border: '1px solid rgba(37, 99, 235, 0.25)',
          borderRadius: '12px',
          padding: '1rem',
          marginBottom: '1.5rem',
          textAlign: 'center'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginBottom: '0.25rem', color: 'var(--brand-primary)', fontWeight: 700, fontSize: '0.95rem' }}>
            <Sparkles size={18} />
            <span>Fast Reviewer Access</span>
          </div>
          <p style={{ fontSize: '0.78rem', color: 'var(--text-secondary)', margin: '0 0 0.75rem 0' }}>
            Skip registration and immediately tour all features with pre-seeded clinical data.
          </p>
          <button 
            type="button" 
            onClick={handleDemoAccess}
            disabled={demoLoading}
            className="btn btn-primary"
            style={{ 
              width: '100%', 
              background: 'linear-gradient(135deg, var(--brand-primary), #059669)',
              border: 'none',
              fontWeight: 700,
              fontSize: '0.925rem',
              padding: '0.75rem',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              gap: '0.5rem'
            }}
          >
            {demoLoading ? 'Initializing Demo...' : (
              <>
                <ShieldCheck size={18} />
                Try Demo Mode (1-Click)
                <ArrowRight size={16} />
              </>
            )}
          </button>
        </div>

        {error && (
          <div style={{ backgroundColor: 'rgba(239, 68, 68, 0.1)', color: 'var(--error-color)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1.25rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}

        {successMsg && (
          <div style={{ backgroundColor: 'rgba(16, 185, 129, 0.1)', color: 'var(--success-color)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1.25rem', fontSize: '0.875rem' }}>
            {successMsg}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="input-group">
            <label className="input-label" htmlFor="name">Full Name</label>
            <input 
              id="name"
              type="text" 
              className="input-field" 
              placeholder="John Doe"
              value={name}
              onChange={(e) => setName(e.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label className="input-label" htmlFor="email">Email</label>
            <input 
              id="email"
              type="email" 
              className="input-field" 
              placeholder="your@email.com"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </div>

          <div className="input-group">
            <label className="input-label" htmlFor="password">Password</label>
            <input 
              id="password"
              type="password" 
              className="input-field" 
              placeholder="••••••••"
              value={password}
              onChange={handlePasswordChange}
              onKeyDown={(e) => {
                if (e.key === ' ') e.preventDefault()
              }}
              pattern="\S+"
              title="Password cannot contain spaces."
              required
            />
          </div>

          <button type="submit" className="btn btn-outline" style={{ width: '100%', marginTop: '0.75rem', justifyContent: 'center' }} disabled={loading}>
            {loading ? 'Creating account...' : (
              <>
                <UserPlus size={18} style={{ marginRight: '8px' }} />
                Sign Up with Email
              </>
            )}
          </button>
        </form>

        <div style={{ display: 'flex', alignItems: 'center', margin: '1.25rem 0' }}>
          <div style={{ flex: 1, height: '1px', backgroundColor: 'var(--surface-border)' }}></div>
          <span style={{ padding: '0 0.75rem', color: 'var(--text-muted)', fontSize: '0.8rem' }}>or</span>
          <div style={{ flex: 1, height: '1px', backgroundColor: 'var(--surface-border)' }}></div>
        </div>

        <GoogleAuthButton
          mode="signup"
          onSuccess={() => navigate('/dashboard')}
          onError={(err) => setError(err.message)}
        />

        <div style={{ textAlign: 'center', marginTop: '1.25rem', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
          Already have an account? <Link to="/login" style={{ fontWeight: 600, color: 'var(--brand-primary)' }}>Sign in</Link>
        </div>
      </div>
    </div>
  )
}
