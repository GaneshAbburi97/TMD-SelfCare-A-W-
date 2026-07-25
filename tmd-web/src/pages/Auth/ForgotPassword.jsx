import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../../lib/api'
import { KeyRound, Mail, CheckCircle } from 'lucide-react'

export default function ForgotPassword() {
  const [step, setStep] = useState(1) // 1: Email, 2: OTP, 3: New Password, 4: Success
  const [email, setEmail] = useState('')
  const [otp, setOtp] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [confirmPassword, setConfirmPassword] = useState('')
  
  const [error, setError] = useState(null)
  const [loading, setLoading] = useState(false)
  const navigate = useNavigate()

  const handleSendEmail = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      await api.post('/auth/forgot-password', { email })
      setStep(2)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleVerifyOTP = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)

    try {
      await api.post('/auth/verify-otp', { email, otp })
      setStep(3)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  const handleResetPassword = async (e) => {
    e.preventDefault()
    if (newPassword !== confirmPassword) {
      setError("Passwords do not match.")
      return
    }
    if (newPassword.length < 6) {
      setError("Password must be at least 6 characters.")
      return
    }

    setError(null)
    setLoading(true)

    try {
      await api.post('/auth/reset-password', { email, otp, newPassword })
      setStep(4)
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="app-container" style={{ alignItems: 'center', justifyContent: 'center' }}>
      <div className="glass-panel" style={{ padding: '2rem', width: '100%', maxWidth: '400px' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <h1 className="gradient-text" style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>
            {step === 1 && "Reset Password"}
            {step === 2 && "Enter OTP"}
            {step === 3 && "New Password"}
            {step === 4 && "Success!"}
          </h1>
          <p style={{ color: 'var(--text-muted)' }}>
            {step === 1 && "We'll send you a 6-digit code to reset it."}
            {step === 2 && `Code sent to ${email}`}
            {step === 3 && "Choose a strong new password."}
            {step === 4 && "Your password has been successfully reset."}
          </p>
        </div>

        {error && (
          <div style={{ backgroundColor: 'rgba(239, 68, 68, 0.1)', color: 'var(--error-color)', padding: '0.75rem', borderRadius: '8px', marginBottom: '1.5rem', fontSize: '0.875rem' }}>
            {error}
          </div>
        )}

        {step === 1 && (
          <form onSubmit={handleSendEmail}>
            <div className="input-group">
              <label className="input-label" htmlFor="email">Email Address</label>
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
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
              {loading ? 'Sending...' : (
                <><Mail size={18} style={{ marginRight: '8px' }} /> Send Code</>
              )}
            </button>
          </form>
        )}

        {step === 2 && (
          <form onSubmit={handleVerifyOTP}>
            <div className="input-group">
              <label className="input-label" htmlFor="otp">6-Digit OTP</label>
              <input 
                id="otp"
                type="text" 
                className="input-field" 
                placeholder="123456"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                maxLength={6}
                required
                style={{ textAlign: 'center', letterSpacing: '5px', fontSize: '1.25rem' }}
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
              {loading ? 'Verifying...' : 'Verify Code'}
            </button>
            <button type="button" onClick={() => setStep(1)} className="btn btn-ghost" style={{ width: '100%', marginTop: '0.5rem' }}>
              Change Email
            </button>
          </form>
        )}

        {step === 3 && (
          <form onSubmit={handleResetPassword}>
            <div className="input-group">
              <label className="input-label" htmlFor="newPassword">New Password</label>
              <input 
                id="newPassword"
                type="password" 
                className="input-field" 
                placeholder="••••••••"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                required
              />
            </div>
            <div className="input-group" style={{ marginTop: '1rem' }}>
              <label className="input-label" htmlFor="confirmPassword">Confirm Password</label>
              <input 
                id="confirmPassword"
                type="password" 
                className="input-field" 
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                required
              />
            </div>
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '1rem' }} disabled={loading}>
              {loading ? 'Resetting...' : 'Reset Password'}
            </button>
          </form>
        )}

        {step === 4 && (
          <div style={{ textAlign: 'center' }}>
            <CheckCircle size={64} color="var(--success-color)" style={{ margin: '0 auto 1.5rem auto' }} />
            <button onClick={() => navigate('/login')} className="btn btn-primary" style={{ width: '100%' }}>
              Return to Sign In
            </button>
          </div>
        )}

        {step !== 4 && (
          <div style={{ textAlign: 'center', marginTop: '1.5rem', fontSize: '0.875rem', color: 'var(--text-muted)' }}>
            Remember your password? <Link to="/login" style={{ fontWeight: 600 }}>Sign in</Link>
          </div>
        )}
      </div>
    </div>
  )
}
