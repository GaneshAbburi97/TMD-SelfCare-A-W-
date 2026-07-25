import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Save, User, Mail, Ruler, Weight, Calendar } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { api } from '../lib/api'

export default function EditProfile() {
  const navigate = useNavigate()
  const { user, setUser } = useAuth()
  
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    date_of_birth: '',
    height_cm: '',
    weight_kg: ''
  })
  
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)

  // Load existing data
  useEffect(() => {
    if (user) {
      setFormData({
        name: user.name || user.user_metadata?.name || '',
        email: user.email || '',
        date_of_birth: user.date_of_birth || user.user_metadata?.date_of_birth || '',
        height_cm: user.height_cm || '',
        weight_kg: user.weight_kg || ''
      })
    }
  }, [user])

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null)
    setSuccess(false)

    const trimmedEmail = formData.email.trim()
    
    if (trimmedEmail !== trimmedEmail.toLowerCase()) {
      setError('Email must be entirely in lowercase letters.')
      setIsLoading(false)
      return
    }

    if (!/^[a-z]/.test(trimmedEmail)) {
      setError('Email must start with a lowercase letter (a-z). Numbers at the start are not allowed.')
      setIsLoading(false)
      return
    }

    const emailRegex = /^[a-z][a-z0-9._%+-]*@[a-z0-9.-]+\.[a-z]{2,}$/
    if (!emailRegex.test(trimmedEmail)) {
      setError('Please enter a valid email address.')
      setIsLoading(false)
      return
    }

    const domain = trimmedEmail.split('@')[1]
    
    // Strict Whitelist of Allowed Email Providers
    const allowedDomains = [
      'gmail.com', 'yahoo.com', 'hotmail.com', 'outlook.com', 
      'icloud.com', 'aol.com', 'protonmail.com', 'zoho.com'
    ]
    
    if (!allowedDomains.includes(domain)) {
      setError(`Email domain @${domain} is not allowed. Please use a recognized provider like @gmail.com or @yahoo.com.`)
      setIsLoading(false)
      return
    }

    try {
      // Backend expects PUT /auth/profile
      const result = await api.put('/auth/profile', formData)
      
      if (result.error) throw new Error(result.error)
      
      // Update global user state
      setUser(result)
      setSuccess(true)
      
      setTimeout(() => {
        navigate('/profile')
      }, 1500)
      
    } catch (err) {
      setError(err.message || 'Failed to update profile')
    } finally {
      setIsLoading(false)
    }
  }

  const handleDeleteAccount = async () => {
    setIsDeleting(true)
    try {
      await api.delete('/auth/profile')
      localStorage.removeItem('tmd_token')
      window.location.href = '/login'
    } catch (err) {
      setError(err.message || 'Failed to delete account')
      setIsDeleting(false)
      setShowDeleteModal(false)
    }
  }

  return (
    <div style={{ maxWidth: '600px', margin: '0 auto', paddingBottom: '2rem' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '2rem', gap: '1rem' }}>
        <button 
          onClick={() => navigate('/profile')} 
          className="btn btn-ghost" 
          style={{ padding: '0.5rem', borderRadius: '50%', minWidth: 'unset' }}
        >
          <ArrowLeft size={22} />
        </button>
        <div>
          <h1 style={{ margin: 0 }}>Edit Clinical Profile</h1>
          <p className="text-secondary" style={{ margin: '0.25rem 0 0 0' }}>
            Update your personal and clinical details.
          </p>
        </div>
      </div>

      <div className="card">
        {error && (
          <div style={{ padding: '1rem', backgroundColor: '#fef2f2', color: '#dc2626', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #fecaca' }}>
            {error}
          </div>
        )}
        
        {success && (
          <div style={{ padding: '1rem', backgroundColor: '#ecfdf5', color: '#059669', borderRadius: '8px', marginBottom: '1.5rem', border: '1px solid #a7f3d0' }}>
            Profile updated successfully! Redirecting...
          </div>
        )}

        <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>Full Name</label>
            <div style={{ position: 'relative' }}>
              <User size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
              <input 
                type="text" 
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
                className="input-field"
                style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
              />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>Email Address</label>
            <div style={{ position: 'relative' }}>
              <Mail size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
              <input 
                type="email" 
                name="email"
                value={formData.email}
                onChange={handleChange}
                required
                className="input-field"
                style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
              />
            </div>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>Date of Birth</label>
            <div style={{ position: 'relative' }}>
              <Calendar size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
              <input 
                type="date" 
                name="date_of_birth"
                value={formData.date_of_birth}
                onChange={handleChange}
                className="input-field"
                style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
              />
            </div>
          </div>

          <div style={{ display: 'flex', gap: '1rem' }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>Height (cm)</label>
              <div style={{ position: 'relative' }}>
                <Ruler size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
                <input 
                  type="number" 
                  name="height_cm"
                  value={formData.height_cm}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="e.g. 175"
                  style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
                />
              </div>
            </div>

            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600 }}>Weight (kg)</label>
              <div style={{ position: 'relative' }}>
                <Weight size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
                <input 
                  type="number" 
                  name="weight_kg"
                  value={formData.weight_kg}
                  onChange={handleChange}
                  className="input-field"
                  placeholder="e.g. 70"
                  style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
                />
              </div>
            </div>
          </div>

          <button 
            type="submit" 
            className="btn btn-primary" 
            disabled={isLoading}
            style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: '0.5rem', marginTop: '1rem' }}
          >
            <Save size={18} />
            {isLoading ? 'Saving...' : 'Save Profile Changes'}
          </button>
        </form>

        <div style={{ marginTop: '3rem', paddingTop: '1.5rem', borderTop: '1px solid var(--surface-border)' }}>
          <h3 style={{ color: 'var(--accent-red)', marginBottom: '0.5rem', fontSize: '1.125rem' }}>Danger Zone</h3>
          <p className="text-secondary" style={{ fontSize: '0.875rem', marginBottom: '1rem' }}>
            Once you delete your account, there is no going back. Please be certain.
          </p>
          <button 
            type="button" 
            onClick={() => setShowDeleteModal(true)}
            className="btn hover-scale" 
            style={{ backgroundColor: '#fef2f2', color: 'var(--accent-red)', border: '1px solid #fecaca' }}
          >
            Delete Account
          </button>
        </div>
      </div>

      {/* Delete Confirmation Modal */}
      {showDeleteModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 9999,
          display: 'flex', alignItems: 'center', justifyContent: 'center'
        }}>
          <div className="card" style={{ width: '90%', maxWidth: '400px', padding: '2rem' }}>
            <h3 style={{ marginTop: 0 }}>Delete Account</h3>
            <p className="text-secondary" style={{ margin: '1rem 0' }}>
              Are you sure you want to delete your account? All of your data will be permanently removed. This action cannot be undone.
            </p>
            <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '2rem' }}>
              <button 
                className="btn btn-ghost" 
                onClick={() => setShowDeleteModal(false)}
                disabled={isDeleting}
              >
                Cancel
              </button>
              <button 
                className="btn" 
                style={{ backgroundColor: 'var(--accent-red)', color: 'white' }}
                onClick={handleDeleteAccount}
                disabled={isDeleting}
              >
                {isDeleting ? 'Deleting...' : 'Yes, Delete Account'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
