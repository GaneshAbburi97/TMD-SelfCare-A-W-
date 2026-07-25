import { useState } from 'react'
import { Calendar, Ruler, Weight, UserCheck, X } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { api } from '../lib/api'

export default function OnboardingModal({ isOpen, onClose }) {
  const { user, setUser, setOnboardingCompleted } = useAuth()
  
  const [formData, setFormData] = useState({
    date_of_birth: '',
    height_cm: '',
    weight_kg: ''
  })
  const [isSaving, setIsSaving] = useState(false)
  const [error, setError] = useState(null)

  if (!isOpen) return null

  const handleChange = (e) => {
    const { name, value } = e.target
    setFormData(prev => ({ ...prev, [name]: value }))
  }

  const handleSave = async (e) => {
    e.preventDefault()
    setIsSaving(true)
    setError(null)

    try {
      const updatePayload = {
        name: user?.name || user?.user_metadata?.name || '',
        email: user?.email || '',
        date_of_birth: formData.date_of_birth,
        height_cm: formData.height_cm,
        weight_kg: formData.weight_kg
      }

      const updatedUser = await api.put('/auth/profile', updatePayload)
      if (updatedUser?.error) throw new Error(updatedUser.error)

      if (updatedUser) {
        setUser({ ...user, ...updatedUser, ...updatePayload })
      } else {
        setUser({ ...user, ...updatePayload })
      }

      setOnboardingCompleted()
      onClose()
    } catch (err) {
      console.error('Failed to save onboarding data', err)
      // Fallback: update local state anyway so user experience is smooth
      setUser({ ...user, ...formData })
      setOnboardingCompleted()
      onClose()
    } finally {
      setIsSaving(false)
    }
  }

  const handleSkip = () => {
    setOnboardingCompleted()
    onClose()
  }

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(15, 23, 42, 0.75)',
      backdropFilter: 'blur(8px)',
      zIndex: 10000,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '1rem'
    }}>
      <div className="card animate-fade-in" style={{
        maxWidth: '480px',
        width: '100%',
        backgroundColor: 'var(--surface, #FFFFFF)',
        borderRadius: '16px',
        boxShadow: 'var(--shadow-lg, 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04))',
        padding: '2rem',
        position: 'relative'
      }}>
        <button 
          onClick={handleSkip}
          style={{
            position: 'absolute',
            top: '1rem',
            right: '1rem',
            background: 'none',
            border: 'none',
            cursor: 'pointer',
            color: 'var(--text-secondary)',
            padding: '4px',
            borderRadius: '50%'
          }}
          title="Skip onboarding"
        >
          <X size={20} />
        </button>

        <div style={{ textAlign: 'center', marginBottom: '1.5rem' }}>
          <div style={{
            width: '56px',
            height: '56px',
            borderRadius: '50%',
            backgroundColor: 'var(--brand-light, #EFF6FF)',
            color: 'var(--brand-primary, #2563EB)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            margin: '0 auto 1rem auto'
          }}>
            <UserCheck size={28} />
          </div>
          <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
            Welcome to TMD Self-Care! 👋
          </h2>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', lineHeight: '1.5', margin: 0 }}>
            Let's complete your profile details to personalize your therapy recommendations and clinical reports.
          </p>
        </div>

        {error && (
          <div style={{
            padding: '0.75rem 1rem',
            backgroundColor: '#FEF2F2',
            color: '#DC2626',
            borderRadius: '8px',
            fontSize: '0.875rem',
            marginBottom: '1rem',
            border: '1px solid #FECACA'
          }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSave} style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
          
          {/* Date of Birth */}
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.875rem', color: 'var(--text-primary)' }}>
              Date of Birth
            </label>
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

          {/* Height & Weight */}
          <div style={{ display: 'flex', gap: '1rem' }}>
            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.875rem', color: 'var(--text-primary)' }}>
                Height (cm)
              </label>
              <div style={{ position: 'relative' }}>
                <Ruler size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
                <input 
                  type="number"
                  name="height_cm"
                  placeholder="e.g. 175"
                  value={formData.height_cm}
                  onChange={handleChange}
                  className="input-field"
                  style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
                />
              </div>
            </div>

            <div style={{ flex: 1 }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.875rem', color: 'var(--text-primary)' }}>
                Weight (kg)
              </label>
              <div style={{ position: 'relative' }}>
                <Weight size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
                <input 
                  type="number"
                  name="weight_kg"
                  placeholder="e.g. 70"
                  value={formData.weight_kg}
                  onChange={handleChange}
                  className="input-field"
                  style={{ paddingLeft: '2.5rem', width: '100%', boxSizing: 'border-box' }}
                />
              </div>
            </div>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem', marginTop: '0.75rem' }}>
            <button 
              type="submit" 
              className="btn btn-primary"
              disabled={isSaving}
              style={{ width: '100%', padding: '0.75rem', fontSize: '0.9375rem', fontWeight: 600 }}
            >
              {isSaving ? 'Saving Profile...' : 'Save & Continue'}
            </button>

            <button 
              type="button" 
              onClick={handleSkip}
              className="btn btn-ghost"
              style={{ width: '100%', color: 'var(--text-secondary)' }}
            >
              Skip for now
            </button>
          </div>

        </form>
      </div>
    </div>
  )
}
