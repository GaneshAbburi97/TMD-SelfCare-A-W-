import { useState } from 'react'
import { Send, Mail, User, MessageSquare, ArrowLeft, CheckCircle, AlertCircle } from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { api } from '../lib/api'

export default function ContactSupport() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [name, setName] = useState(user?.user_metadata?.full_name || user?.email?.split('@')[0] || '')
  const [email, setEmail] = useState(user?.email || '')
  const [subject, setSubject] = useState('')
  const [message, setMessage] = useState('')
  const [isSending, setIsSending] = useState(false)
  const [isSent, setIsSent] = useState(false)
  const [error, setError] = useState('')

  const handleSend = async (e) => {
    e.preventDefault()
    if (!name || !email || !subject || !message) return

    setIsSending(true)
    setError('')

    try {
      const result = await api.post('/contact', { name, email, subject, message })
      if (result.error) throw new Error(result.error)
      setIsSent(true)
    } catch (err) {
      setError(err.message || 'Failed to send email. Please try again.')
    } finally {
      setIsSending(false)
    }
  }

  if (isSent) {
    return (
      <div style={{ maxWidth: '600px', margin: '0 auto', paddingBottom: '2rem' }}>
        <div className="card" style={{ textAlign: 'center', padding: '3rem 2rem' }}>
          <div style={{ 
            width: '80px', height: '80px', borderRadius: '50%', 
            background: 'linear-gradient(135deg, #10b981, #059669)', 
            display: 'flex', alignItems: 'center', justifyContent: 'center', 
            margin: '0 auto 1.5rem auto',
            animation: 'fadeIn 0.5s ease'
          }}>
            <CheckCircle size={40} color="#fff" />
          </div>
          <h2 style={{ marginBottom: '0.5rem' }}>Email Client Opened!</h2>
          <p className="text-secondary" style={{ marginBottom: '0.5rem', maxWidth: '400px', margin: '0 auto 1.5rem auto' }}>
            Your email app should have opened with your message pre-filled. Just hit <strong>Send</strong> in your email app to deliver it to our team.
          </p>
          <p className="text-secondary" style={{ marginBottom: '2rem', fontSize: '0.875rem' }}>
            We'll respond to <strong>{email}</strong> within 24 hours.
          </p>
          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'center', flexWrap: 'wrap' }}>
            <button className="btn btn-primary" onClick={() => navigate('/support')}>
              ← Back to Support
            </button>
            <button className="btn btn-ghost" onClick={() => { setIsSent(false); setSubject(''); setMessage(''); }}>
              Send Another
            </button>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div style={{ maxWidth: '700px', margin: '0 auto', paddingBottom: '2rem' }}>
      {/* Header */}
      <div style={{ display: 'flex', alignItems: 'center', marginBottom: '2rem', gap: '1rem' }}>
        <button 
          onClick={() => navigate('/support')} 
          className="btn btn-ghost" 
          style={{ padding: '0.5rem', borderRadius: '50%', minWidth: 'unset' }}
        >
          <ArrowLeft size={22} />
        </button>
        <div>
          <h1 style={{ margin: 0 }}>Contact Support</h1>
          <p className="text-secondary" style={{ margin: '0.25rem 0 0 0' }}>
            Write to our team and we'll get back to you soon.
          </p>
        </div>
      </div>

      {/* Contact Info Banner */}
      <div className="card" style={{ 
        background: 'linear-gradient(135deg, var(--brand-primary), var(--brand-dark))', 
        color: '#fff', marginBottom: '1.5rem', padding: '1.5rem'
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={{ 
            width: '48px', height: '48px', borderRadius: '50%', 
            background: 'rgba(255,255,255,0.2)', display: 'flex', 
            alignItems: 'center', justifyContent: 'center' 
          }}>
            <Mail size={24} color="#fff" />
          </div>
          <div>
            <h3 style={{ margin: 0, color: '#fff' }}>TMD Self-Care Support Team</h3>
            <p style={{ margin: '0.25rem 0 0 0', opacity: 0.9, fontSize: '0.9rem' }}>ganeshabburi97@gmail.com</p>
          </div>
        </div>
      </div>

      {/* Email Form */}
      <form onSubmit={handleSend}>
        <div className="card" style={{ marginBottom: '1.5rem' }}>
          <h3 style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <MessageSquare size={20} color="var(--brand-primary)" />
            Compose Your Message
          </h3>

          {/* Name */}
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.9rem' }}>
              Your Name
            </label>
            <div style={{ position: 'relative' }}>
              <User size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
              <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Enter your full name"
                required
                style={{
                  width: '100%', padding: '0.75rem 0.75rem 0.75rem 2.5rem',
                  border: '1px solid var(--surface-border)', borderRadius: '8px',
                  background: 'var(--bg-secondary)', color: 'var(--text-primary)',
                  fontSize: '0.95rem', outline: 'none', boxSizing: 'border-box',
                  transition: 'border-color 0.2s'
                }}
                onFocus={(e) => e.target.style.borderColor = 'var(--brand-primary)'}
                onBlur={(e) => e.target.style.borderColor = 'var(--surface-border)'}
              />
            </div>
          </div>

          {/* Email */}
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.9rem' }}>
              Your Email
            </label>
            <div style={{ position: 'relative' }}>
              <Mail size={18} style={{ position: 'absolute', left: '12px', top: '50%', transform: 'translateY(-50%)', color: 'var(--text-secondary)' }} />
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="Enter your email address"
                required
                style={{
                  width: '100%', padding: '0.75rem 0.75rem 0.75rem 2.5rem',
                  border: '1px solid var(--surface-border)', borderRadius: '8px',
                  background: 'var(--bg-secondary)', color: 'var(--text-primary)',
                  fontSize: '0.95rem', outline: 'none', boxSizing: 'border-box',
                  transition: 'border-color 0.2s'
                }}
                onFocus={(e) => e.target.style.borderColor = 'var(--brand-primary)'}
                onBlur={(e) => e.target.style.borderColor = 'var(--surface-border)'}
              />
            </div>
          </div>

          {/* Subject */}
          <div style={{ marginBottom: '1.25rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.9rem' }}>
              Subject
            </label>
            <select
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
              required
              style={{
                width: '100%', padding: '0.75rem',
                border: '1px solid var(--surface-border)', borderRadius: '8px',
                background: 'var(--bg-secondary)', color: 'var(--text-primary)',
                fontSize: '0.95rem', outline: 'none', boxSizing: 'border-box',
                cursor: 'pointer', transition: 'border-color 0.2s'
              }}
              onFocus={(e) => e.target.style.borderColor = 'var(--brand-primary)'}
              onBlur={(e) => e.target.style.borderColor = 'var(--surface-border)'}
            >
              <option value="">Select a topic...</option>
              <option value="General Inquiry">General Inquiry</option>
              <option value="Technical Issue / Bug Report">Technical Issue / Bug Report</option>
              <option value="Pain Tracker Help">Pain Tracker Help</option>
              <option value="Account / Login Issue">Account / Login Issue</option>
              <option value="Exercise Guidance">Exercise Guidance</option>
              <option value="Feature Request">Feature Request</option>
              <option value="Feedback / Suggestion">Feedback / Suggestion</option>
              <option value="Other">Other</option>
            </select>
          </div>

          {/* Message */}
          <div style={{ marginBottom: '1.5rem' }}>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: 600, fontSize: '0.9rem' }}>
              Your Message
            </label>
            <textarea
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="Describe your issue or question in detail..."
              required
              rows={6}
              style={{
                width: '100%', padding: '0.75rem',
                border: '1px solid var(--surface-border)', borderRadius: '8px',
                background: 'var(--bg-secondary)', color: 'var(--text-primary)',
                fontSize: '0.95rem', outline: 'none', boxSizing: 'border-box',
                resize: 'vertical', fontFamily: 'inherit',
                transition: 'border-color 0.2s'
              }}
              onFocus={(e) => e.target.style.borderColor = 'var(--brand-primary)'}
              onBlur={(e) => e.target.style.borderColor = 'var(--surface-border)'}
            />
          </div>

          {/* Error Display */}
          {error && (
            <div style={{ 
              display: 'flex', alignItems: 'center', gap: '0.5rem',
              padding: '0.75rem 1rem', marginBottom: '1rem',
              background: '#fef2f2', border: '1px solid #fecaca', borderRadius: '8px',
              color: '#dc2626', fontSize: '0.9rem'
            }}>
              <AlertCircle size={18} />
              {error}
            </div>
          )}

          {/* Submit */}
          <button 
            type="submit" 
            className="btn btn-primary" 
            disabled={isSending || !name || !email || !subject || !message}
            style={{ 
              width: '100%', display: 'flex', alignItems: 'center', 
              justifyContent: 'center', gap: '0.5rem', padding: '0.85rem',
              fontSize: '1rem'
            }}
          >
            <Send size={18} />
            {isSending ? 'Sending...' : 'Send Email to Support Team'}
          </button>
        </div>
      </form>

      {/* Help Text */}
      <div style={{ 
        textAlign: 'center', padding: '1rem', 
        color: 'var(--text-secondary)', fontSize: '0.85rem' 
      }}>
        <p style={{ margin: 0 }}>
          Your message will be sent directly to our support team.
        </p>
        <p style={{ margin: '0.25rem 0 0 0' }}>
          Our team typically responds within 24 hours.
        </p>
      </div>
    </div>
  )
}
