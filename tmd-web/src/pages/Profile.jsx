import { useState, useRef, useEffect } from 'react'
import { useAuth } from '../context/AuthContext'
import { useNavigate } from 'react-router-dom'
import { calculateAge, formatDateOfBirth } from '../utils/dateUtils'

import { 
  Camera, Edit, Palette, Ruler, Bell, Shield, FileText, Download, 
  History, HelpCircle, MessageSquare, Stethoscope, Calendar, Settings,
  LogOut, Trash2, ChevronRight, Upload, X, Weight
} from 'lucide-react'

const SectionHeader = ({ title }) => (
  <div style={{
    fontSize: '0.875rem',
    fontWeight: 'bold',
    color: 'var(--text-secondary)',
    letterSpacing: '1px',
    marginBottom: '1rem',
    paddingLeft: '0.5rem',
    borderBottom: '1px solid var(--surface-border)',
    paddingBottom: '0.5rem'
  }}>
    {title}
  </div>
)

const LinkRow = ({ icon: Icon, title, subtitle, color, onClick }) => (
  <div 
    onClick={onClick}
    style={{
      display: 'flex',
      alignItems: 'center',
      padding: '1rem',
      cursor: 'pointer',
      borderBottom: '1px solid var(--surface-border)',
      transition: 'background-color 0.2s'
    }}
    onMouseEnter={e => e.currentTarget.style.backgroundColor = 'var(--surface-hover)'}
    onMouseLeave={e => e.currentTarget.style.backgroundColor = 'transparent'}
  >
    <div style={{ padding: '0.5rem', backgroundColor: color ? `${color}1A` : 'var(--brand-light)', borderRadius: '8px', color: color || 'var(--brand-primary)', marginRight: '1rem' }}>
      <Icon size={20} />
    </div>
    <div style={{ flex: 1 }}>
      <div style={{ color: color || 'var(--text-primary)', fontWeight: subtitle ? 600 : 500, fontSize: '0.9375rem' }}>{title}</div>
      {subtitle && <div style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', marginTop: '2px' }}>{subtitle}</div>}
    </div>
    <ChevronRight size={20} color="var(--text-muted)" opacity={0.5} />
  </div>
)

export default function Profile() {
  const { user, signOut } = useAuth()
  const navigate = useNavigate()

  const [photo, setPhoto] = useState(null)
  const fileInputRef = useRef(null)
  
  const videoRef = useRef(null)
  const [isCameraOpen, setIsCameraOpen] = useState(false)
  const [stream, setStream] = useState(null)
  const [showOptions, setShowOptions] = useState(false)
  const [showDeleteModal, setShowDeleteModal] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)

  useEffect(() => {
    if (user?.email) {
      const savedPhoto = localStorage.getItem(`profile_photo_${user.email}`)
      if (savedPhoto) setPhoto(savedPhoto)
    }
  }, [user])

  // Attach stream to video element when it opens
  useEffect(() => {
    if (isCameraOpen && videoRef.current && stream) {
      videoRef.current.srcObject = stream
    }
  }, [isCameraOpen, stream])

  const handleLogout = async () => {
    await signOut()
    navigate('/login')
  }

  const handleDeleteAccount = async () => {
    setIsDeleting(true)
    try {
      await api.delete('/auth/profile')
      localStorage.removeItem('tmd_token')
      window.location.href = '/login'
    } catch (err) {
      alert(err.message || 'Failed to delete account')
      setIsDeleting(false)
      setShowDeleteModal(false)
    }
  }

  const handlePhotoUpload = (e) => {
    const file = e.target.files[0]
    if (file) {
      const reader = new FileReader()
      reader.onloadend = () => {
        const base64String = reader.result
        setPhoto(base64String)
        if (user?.email) {
          localStorage.setItem(`profile_photo_${user.email}`, base64String)
          window.dispatchEvent(new Event('profile_photo_updated'))
        }
        setShowOptions(false)
      }
      reader.readAsDataURL(file)
    }
  }

  const openCamera = async () => {
    try {
      const mediaStream = await navigator.mediaDevices.getUserMedia({ video: true })
      setStream(mediaStream)
      setIsCameraOpen(true)
      setShowOptions(false)
    } catch (err) {
      alert("Could not access camera. Please allow camera permissions in your browser.")
    }
  }

  const closeCamera = () => {
    if (stream) {
      stream.getTracks().forEach(track => track.stop())
    }
    setStream(null)
    setIsCameraOpen(false)
  }

  const capturePhoto = () => {
    if (videoRef.current) {
      const canvas = document.createElement('canvas')
      canvas.width = videoRef.current.videoWidth
      canvas.height = videoRef.current.videoHeight
      const ctx = canvas.getContext('2d')
      ctx.drawImage(videoRef.current, 0, 0, canvas.width, canvas.height)
      const base64String = canvas.toDataURL('image/jpeg')
      
      setPhoto(base64String)
      if (user?.email) {
        localStorage.setItem(`profile_photo_${user.email}`, base64String)
        window.dispatchEvent(new Event('profile_photo_updated'))
      }
      closeCamera()
    }
  }

  return (
    <div style={{ maxWidth: '1000px', margin: '0 auto' }}>
      
      {/* Camera Modal */}
      {isCameraOpen && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.8)', zIndex: 9999,
          display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center'
        }}>
          <div style={{ position: 'relative', backgroundColor: '#000', borderRadius: '12px', overflow: 'hidden' }}>
            <video 
              ref={videoRef} 
              autoPlay 
              playsInline 
              style={{ width: '100%', maxWidth: '500px', transform: 'scaleX(-1)' }} 
            />
            <button 
              onClick={closeCamera}
              style={{ position: 'absolute', top: '10px', right: '10px', background: 'rgba(255,255,255,0.2)', border: 'none', borderRadius: '50%', padding: '8px', cursor: 'pointer', color: 'white' }}
            >
              <X size={20} />
            </button>
          </div>
          <div style={{ marginTop: '1.5rem', display: 'flex', gap: '1rem' }}>
            <button className="btn btn-outline" style={{ color: 'white', borderColor: 'white' }} onClick={closeCamera}>Cancel</button>
            <button className="btn btn-primary" onClick={capturePhoto}>Capture Photo</button>
          </div>
        </div>
      )}

      <header style={{ marginBottom: '2rem' }}>
        <h1 style={{ marginBottom: '0.25rem' }}>Patient Profile</h1>
        <p className="text-secondary">Manage your clinical details and platform settings.</p>
      </header>

      <div className="dashboard-grid">
        
        {/* Left Column: Account Info */}
        <div className="col-span-4" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <div className="card" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', textAlign: 'center', padding: '2.5rem 1.5rem' }}>
            <div 
              style={{ position: 'relative', marginBottom: '1.5rem', cursor: 'pointer' }}
              onClick={() => setShowOptions(!showOptions)}
            >
              <div style={{ 
                width: '120px', 
                height: '120px', 
                backgroundColor: 'var(--brand-primary)', 
                color: 'white', 
                borderRadius: '50%', 
                display: 'flex', 
                alignItems: 'center', 
                justifyContent: 'center',
                fontSize: '2.5rem',
                fontWeight: 'bold',
                boxShadow: 'var(--shadow-md)',
                overflow: 'hidden'
              }}>
                {photo ? (
                  <img src={photo} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                ) : (
                  (user?.name || user?.user_metadata?.name || user?.email || 'U').charAt(0).toUpperCase()
                )}
              </div>
              <div style={{
                position: 'absolute',
                bottom: 0,
                right: 0,
                backgroundColor: 'white',
                borderRadius: '50%',
                padding: '8px',
                boxShadow: 'var(--shadow-sm)',
                color: 'var(--brand-primary)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center'
              }}>
                <Camera size={18} />
              </div>

              {/* Photo Options Dropdown */}
              {showOptions && (
                <div style={{
                  position: 'absolute',
                  top: '100%',
                  left: '50%',
                  transform: 'translateX(-50%)',
                  marginTop: '0.5rem',
                  backgroundColor: 'var(--surface)',
                  borderRadius: '8px',
                  boxShadow: 'var(--shadow-lg)',
                  border: '1px solid var(--surface-border)',
                  zIndex: 999,
                  width: '160px',
                  display: 'flex',
                  flexDirection: 'column',
                  overflow: 'hidden'
                }}>
                  <div 
                    onClick={() => { fileInputRef.current?.click(); setShowOptions(false) }}
                    style={{ padding: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem', borderBottom: '1px solid var(--surface-border)', fontSize: '0.875rem', cursor: 'pointer', transition: 'background-color 0.2s' }}
                    onMouseEnter={e => e.currentTarget.style.backgroundColor = 'var(--surface-hover)'}
                    onMouseLeave={e => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <Upload size={16} /> Upload Photo
                  </div>
                  <div 
                    onClick={(e) => { e.stopPropagation(); openCamera() }}
                    style={{ padding: '0.75rem', display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem', cursor: 'pointer', transition: 'background-color 0.2s' }}
                    onMouseEnter={e => e.currentTarget.style.backgroundColor = 'var(--surface-hover)'}
                    onMouseLeave={e => e.currentTarget.style.backgroundColor = 'transparent'}
                  >
                    <Camera size={16} /> Take Photo
                  </div>
                </div>
              )}

              <input 
                type="file" 
                ref={fileInputRef} 
                onChange={handlePhotoUpload} 
                accept="image/*" 
                style={{ display: 'none' }} 
              />
            </div>

            <h2 style={{ fontSize: '1.5rem', fontWeight: 'bold', marginBottom: '0.25rem' }}>{user?.name || user?.user_metadata?.name || 'User Name'}</h2>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', marginBottom: '1.25rem' }}>{user?.email}</p>

            {/* Clinical Profile Details Card */}
            <div style={{
              width: '100%',
              backgroundColor: 'var(--surface-hover)',
              borderRadius: '8px',
              padding: '1rem',
              marginBottom: '1.5rem',
              display: 'grid',
              gridTemplateColumns: '1fr 1fr',
              gap: '0.75rem',
              textAlign: 'left',
              boxSizing: 'border-box'
            }}>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block' }}>Date of Birth</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                  {formatDateOfBirth(user?.date_of_birth || user?.user_metadata?.date_of_birth)}
                </span>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block' }}>Age</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                  {calculateAge(user?.date_of_birth || user?.user_metadata?.date_of_birth) !== null
                    ? `${calculateAge(user?.date_of_birth || user?.user_metadata?.date_of_birth)} yrs`
                    : 'N/A'}
                </span>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block' }}>Height</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                  {user?.height_cm ? `${user.height_cm} cm` : 'Not set'}
                </span>
              </div>
              <div>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'block' }}>Weight</span>
                <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-primary)' }}>
                  {user?.weight_kg ? `${user.weight_kg} kg` : 'Not set'}
                </span>
              </div>
            </div>

            <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => navigate('/profile/edit')}>
              <Edit size={16} /> Edit Clinical Profile
            </button>
          </div>

          {/* Account Management */}
          <div className="card" style={{ padding: '1rem' }}>
            <LinkRow icon={LogOut} title="Secure Logout" color="#EF4444" onClick={handleLogout} />
            <LinkRow icon={Trash2} title="Delete Account" color="#EF4444" onClick={() => setShowDeleteModal(true)} />
          </div>

          <div style={{ textAlign: 'center', color: 'var(--text-muted)', fontSize: '0.75rem', fontWeight: 500 }}>
            TMD Self-Care Platform v2.4.1 (Clinical Release)
          </div>

        </div>

        {/* Right Column: Settings & Links */}
        <div className="col-span-8" style={{ display: 'flex', flexDirection: 'column', gap: '1.5rem' }}>
          
          <div className="card" style={{ padding: '1.5rem' }}>
            <SectionHeader title="PLATFORM SETTINGS" />
            <div style={{ borderRadius: '8px', border: '1px solid var(--surface-border)', overflow: 'hidden' }}>
              <LinkRow icon={Settings} title="General Settings" subtitle="Theme, Units, Notifications" onClick={() => navigate('/settings')} />
              <LinkRow icon={Shield} title="Privacy & Security" subtitle="Data sharing and HIPAA compliance" onClick={() => navigate('/settings/privacy')} />
            </div>
          </div>

          <div className="card" style={{ padding: '1.5rem' }}>
            <SectionHeader title="CLINICAL REPORTS" />
            <div style={{ borderRadius: '8px', border: '1px solid var(--surface-border)', overflow: 'hidden' }}>
              <LinkRow icon={FileText} title="View Health Reports" onClick={() => navigate('/reports')} />
            </div>
          </div>

          <div className="card" style={{ padding: '1.5rem' }}>
            <SectionHeader title="SUPPORT & RESOURCES" />
            <div style={{ borderRadius: '8px', border: '1px solid var(--surface-border)', overflow: 'hidden' }}>
              <LinkRow icon={HelpCircle} title="Help Center & FAQs" onClick={() => navigate('/support')} />
            </div>
          </div>

        </div>

      </div>

      {/* Delete Confirmation Modal */}
      {showDeleteModal && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)', zIndex: 9999,
          display: 'flex', alignItems: 'center', justifyContent: 'center'
        }}>
          <div className="card" style={{ width: '90%', maxWidth: '400px', backgroundColor: 'var(--surface)', padding: '2rem' }}>
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
