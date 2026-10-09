import { useState, useEffect } from 'react'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import { 
  Home, Activity, MapPin, BarChart2, MessageSquare, Menu,
  LogOut, Settings, Moon, Sun, User, BookOpen, Shield, FileText,
  Sparkles, RotateCcw, CheckCircle2, Info, X
} from 'lucide-react'
import { useAuth } from '../context/AuthContext'
import { useTheme } from '../context/ThemeContext'
import OnboardingModal from './OnboardingModal'

const navItems = [
  { name: 'Dashboard', path: '/dashboard', icon: Home },
  { name: 'Pain Map', path: '/pain-map', icon: MapPin },
  { name: 'Exercises', path: '/exercises', icon: Activity },
  { name: 'Analytics', path: '/progress', icon: BarChart2 },
  { name: 'Sleep Log', path: '/sleep', icon: Moon },
  { name: 'Wellness', path: '/wellness', icon: Shield },
  { name: 'Reports', path: '/reports', icon: FileText },
  { name: 'AI Assistant', path: '/ai-chat', icon: MessageSquare },
  { name: 'Support', path: '/support', icon: BookOpen },
  { name: 'Settings', path: '/settings', icon: Settings },
]

const NavItem = ({ item, isSidebarOpen, navigate, location }) => {
  const isActive = location.pathname.startsWith(item.path)
  const Icon = item.icon
  return (
    <div
      onClick={() => navigate(item.path)}
      className="animate-fade-in"
      style={{
        display: 'flex',
        alignItems: 'center',
        gap: '0.75rem',
        padding: '0.75rem 1rem',
        borderRadius: '0 8px 8px 0',
        cursor: 'pointer',
        backgroundColor: isActive ? 'var(--brand-light)' : 'transparent',
        color: isActive ? 'var(--brand-primary)' : 'var(--text-secondary)',
        borderLeft: isActive ? '4px solid var(--brand-primary)' : '4px solid transparent',
        fontWeight: isActive ? 600 : 500,
        marginBottom: '0.25rem',
        transition: 'all 0.2s',
        animationDelay: '0.1s'
      }}
      onMouseEnter={(e) => {
        if (!isActive) {
          e.currentTarget.style.backgroundColor = 'var(--surface-hover)'
          e.currentTarget.style.color = 'var(--text-primary)'
        }
      }}
      onMouseLeave={(e) => {
        if (!isActive) {
          e.currentTarget.style.backgroundColor = 'transparent'
          e.currentTarget.style.color = 'var(--text-secondary)'
        }
      }}
    >
      <Icon size={20} />
      {isSidebarOpen && <span>{item.name}</span>}
    </div>
  )
}

export default function Layout() {
  const [isSidebarOpen, setSidebarOpen] = useState(true)
  const [isProfileMenuOpen, setProfileMenuOpen] = useState(false)
  const [isDemoModalOpen, setDemoModalOpen] = useState(false)
  const [profilePhoto, setProfilePhoto] = useState(null)
  const [resetSuccess, setResetSuccess] = useState(false)
  
  const { user, signOut, hasCompletedOnboarding, isDemoMode, resetDemoState } = useAuth()
  const { toggleTheme, isDark } = useTheme()
  const navigate = useNavigate()
  const location = useLocation()
  const [showOnboarding, setShowOnboarding] = useState(false)

  useEffect(() => {
    if (user && !hasCompletedOnboarding && !isDemoMode) {
      setShowOnboarding(true)
    } else {
      setShowOnboarding(false)
    }
  }, [user, hasCompletedOnboarding, isDemoMode])

  useEffect(() => {
    if (user?.email) {
      const savedPhoto = localStorage.getItem(`profile_photo_${user.email}`)
      if (savedPhoto) setProfilePhoto(savedPhoto)
    }

    const handlePhotoUpdate = () => {
      if (user?.email) {
        setProfilePhoto(localStorage.getItem(`profile_photo_${user.email}`))
      }
    }

    window.addEventListener('profile_photo_updated', handlePhotoUpdate)
    return () => window.removeEventListener('profile_photo_updated', handlePhotoUpdate)
  }, [user])

  const handleLogout = async () => {
    await signOut()
    navigate('/login')
  }

  const handleResetDemoData = () => {
    resetDemoState()
    setResetSuccess(true)
    setTimeout(() => {
      setResetSuccess(false)
      setDemoModalOpen(false)
      window.location.reload()
    }, 1200)
  }

  return (
    <div style={{ display: 'flex', height: '100vh', width: '100vw', overflow: 'hidden' }}>
      
      {/* LEFT SIDEBAR */}
      <aside className="glass-panel" style={{
        width: isSidebarOpen ? '260px' : '72px',
        display: 'flex',
        flexDirection: 'column',
        transition: 'width 0.3s ease',
        zIndex: 50,
        border: 'none',
        borderRight: '1px solid var(--surface-border)'
      }}>
        {/* Sidebar Header */}
        <div style={{ 
          height: '64px', 
          display: 'flex', 
          alignItems: 'center', 
          padding: isSidebarOpen ? '0 1.5rem' : '0', 
          justifyContent: isSidebarOpen ? 'space-between' : 'center',
          borderBottom: '1px solid var(--surface-border)'
        }}>
          {isSidebarOpen && <h1 style={{ fontSize: '1.25rem', color: 'var(--brand-primary)', margin: 0, fontWeight: 700 }}>TMD Self-Care</h1>}
          <button 
            onClick={() => setSidebarOpen(!isSidebarOpen)}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}
            title="Toggle Sidebar"
          >
            <Menu size={20} />
          </button>
        </div>

        {/* Sidebar Nav */}
        <div style={{ flex: 1, padding: '1rem 0.5rem', overflowY: 'auto' }}>
          {navItems.map(item => <NavItem key={item.path} item={item} isSidebarOpen={isSidebarOpen} navigate={navigate} location={location} />)}
        </div>
      </aside>

      {/* MAIN CONTENT AREA */}
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
        
        {/* TOP HEADER */}
        <header className="glass-panel" style={{
          height: '64px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: '0 2rem',
          zIndex: 40,
          border: 'none',
          borderBottom: '1px solid var(--surface-border)',
          backdropFilter: 'blur(16px)'
        }}>
          <div>
            <h2 style={{ fontSize: '1.125rem', color: 'var(--text-primary)', margin: 0 }}>
              {navItems.find(n => location.pathname.startsWith(n.path))?.name || 'Portal'}
            </h2>
          </div>

          <div style={{ display: 'flex', alignItems: 'center', gap: '1.25rem' }}>
            
            {/* ✨ Demo Mode Badge */}
            <button
              onClick={() => setDemoModalOpen(true)}
              style={{
                display: 'flex',
                alignItems: 'center',
                gap: '0.4rem',
                padding: '0.35rem 0.75rem',
                borderRadius: '20px',
                background: 'linear-gradient(135deg, rgba(37, 99, 235, 0.1), rgba(16, 185, 129, 0.15))',
                border: '1px solid rgba(16, 185, 129, 0.4)',
                color: '#059669',
                fontSize: '0.8rem',
                fontWeight: 700,
                cursor: 'pointer',
                transition: 'transform 0.2s, box-shadow 0.2s'
              }}
              title="Click to view Demo Mode status"
            >
              <Sparkles size={14} color="#059669" />
              <span>Demo Mode</span>
            </button>

            {/* Theme Toggle */}
            <button onClick={toggleTheme} style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }} title="Toggle Theme">
              {isDark ? <Sun size={20} /> : <Moon size={20} />}
            </button>

            {/* User Profile Dropdown */}
            <div style={{ position: 'relative' }}>
              <div 
                onClick={() => setProfileMenuOpen(!isProfileMenuOpen)}
                style={{
                  display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer',
                  padding: '0.25rem 0.5rem', borderRadius: '24px', backgroundColor: 'var(--surface-hover)'
                }}
              >
                <div style={{
                  width: '32px', height: '32px', borderRadius: '50%',
                  backgroundColor: 'var(--brand-primary)', color: 'white',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontWeight: 'bold', fontSize: '0.875rem',
                  overflow: 'hidden'
                }}>
                  {profilePhoto ? (
                    <img src={profilePhoto} alt="Profile" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
                  ) : (
                    (user?.name || user?.user_metadata?.name || user?.email || 'U').charAt(0).toUpperCase()
                  )}
                </div>
                <span style={{ fontSize: '0.875rem', fontWeight: 500, color: 'var(--text-primary)' }}>
                  {user?.name || user?.user_metadata?.name || 'User'}
                </span>
              </div>

              {isProfileMenuOpen && (
                <div style={{
                  position: 'absolute', top: '120%', right: 0, width: '200px',
                  backgroundColor: 'var(--surface)', border: '1px solid var(--surface-border)',
                  borderRadius: '8px', boxShadow: 'var(--shadow-md)', padding: '0.5rem', zIndex: 100
                }}>
                  <div 
                    onClick={() => { navigate('/profile'); setProfileMenuOpen(false) }}
                    style={{ padding: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', borderRadius: '4px', color: 'var(--text-primary)' }}
                  >
                    <User size={16} /> Profile
                  </div>
                  <div 
                    onClick={handleLogout}
                    style={{ padding: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer', borderRadius: '4px', color: 'var(--accent-red)' }}
                  >
                    <LogOut size={16} /> Logout
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>

        {/* PAGE CONTENT */}
        <main style={{ flex: 1, overflowY: 'auto', padding: '2rem', backgroundColor: 'var(--bg-primary)' }}>
          <Outlet />
        </main>
      </div>

      {/* Demo Mode Info Modal */}
      {isDemoModalOpen && (
        <div style={{
          position: 'fixed', top: 0, left: 0, right: 0, bottom: 0,
          backgroundColor: 'rgba(15, 23, 42, 0.75)', backdropFilter: 'blur(6px)',
          zIndex: 10000, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '1rem'
        }}>
          <div className="card animate-fade-in" style={{ maxWidth: '480px', width: '100%', padding: '2rem', position: 'relative' }}>
            <button 
              onClick={() => setDemoModalOpen(false)}
              style={{ position: 'absolute', top: '1rem', right: '1rem', background: 'none', border: 'none', cursor: 'pointer', color: 'var(--text-secondary)' }}
            >
              <X size={20} />
            </button>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
              <div style={{ padding: '0.5rem', borderRadius: '50%', backgroundColor: 'rgba(16, 185, 129, 0.1)', color: '#059669' }}>
                <Sparkles size={24} />
              </div>
              <div>
                <h3 style={{ margin: 0, fontSize: '1.25rem' }}>Interactive Demo Mode Active</h3>
                <span style={{ fontSize: '0.8rem', color: '#059669', fontWeight: 600 }}>Backend-Independent Portfolio Tour</span>
              </div>
            </div>

            <p style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', lineHeight: '1.5', margin: '0 0 1.25rem 0' }}>
              This platform is running in self-contained demonstration mode. You can interact with all features:
            </p>

            <div style={{ display: 'flex', flexDirection: 'column', gap: '0.5rem', marginBottom: '1.5rem', fontSize: '0.85rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-primary)' }}>
                <CheckCircle2 size={16} color="#059669" />
                <span><strong>14-Day Clinical History:</strong> Populated trends in Analytics & Reports.</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-primary)' }}>
                <CheckCircle2 size={16} color="#059669" />
                <span><strong>Interactive Logging:</strong> Log new pain maps, sleep, or exercises in real-time.</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-primary)' }}>
                <CheckCircle2 size={16} color="#059669" />
                <span><strong>AI Clinical Assistant:</strong> Instant medical guidance & safety disclaimers.</span>
              </div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: 'var(--text-primary)' }}>
                <CheckCircle2 size={16} color="#059669" />
                <span><strong>PDF Generation:</strong> Download physician-ready health reports.</span>
              </div>
            </div>

            {resetSuccess ? (
              <div style={{ padding: '0.75rem', backgroundColor: '#ecfdf5', color: '#059669', borderRadius: '8px', textAlign: 'center', fontWeight: 600, fontSize: '0.875rem' }}>
                Demo data reset to fresh 14-day clinical state! Reloading...
              </div>
            ) : (
              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end' }}>
                <button 
                  type="button" 
                  onClick={handleResetDemoData}
                  className="btn btn-outline"
                  style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem' }}
                >
                  <RotateCcw size={16} /> Reset Demo Data
                </button>
                <button 
                  type="button" 
                  onClick={() => setDemoModalOpen(false)}
                  className="btn btn-primary"
                  style={{ fontSize: '0.875rem' }}
                >
                  Continue Tour
                </button>
              </div>
            )}
          </div>
        </div>
      )}

      <OnboardingModal isOpen={showOnboarding} onClose={() => setShowOnboarding(false)} />
    </div>
  )
}
