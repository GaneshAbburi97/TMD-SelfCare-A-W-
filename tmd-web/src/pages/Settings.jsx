import { useState } from 'react'
import { useTheme } from '../context/ThemeContext'
import { useAuth } from '../context/AuthContext'
import { Moon, Sun, Bell, Globe, Database, User, Sparkles, RotateCcw, Check } from 'lucide-react'
import { Link } from 'react-router-dom'
import { api } from '../lib/api'

const SettingRow = ({ icon: Icon, title, description, action }) => (
  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '1.5rem', borderBottom: '1px solid var(--surface-border)' }}>
    <div style={{ display: 'flex', gap: '1rem', alignItems: 'flex-start' }}>
      <div style={{ padding: '0.5rem', backgroundColor: 'var(--brand-light)', borderRadius: '8px', color: 'var(--brand-primary)' }}>
        <Icon size={20} />
      </div>
      <div>
        <h4 style={{ marginBottom: '0.25rem', fontWeight: 600 }}>{title}</h4>
        <p className="text-secondary text-sm" style={{ margin: 0 }}>{description}</p>
      </div>
    </div>
    <div>{action}</div>
  </div>
)

export default function Settings() {
  const { theme, toggleTheme, isDark } = useTheme()
  const { isDemoMode, resetDemoState } = useAuth()
  const [resetDone, setResetDone] = useState(false)

  const handleDataExport = async () => {
    try {
      // Fetch all user data
      const pain = await api.get('/pain') || []
      const sleep = await api.get('/sleep') || []
      const exercise = await api.get('/exercise') || []
      const wellness = await api.get('/wellness') || []
      
      const exportData = {
        exportedAt: new Date().toISOString(),
        environment: isDemoMode ? 'Demo Mode (In-Browser Mock DB)' : 'Production / Live Backend',
        records: {
          painLogs: pain,
          sleepLogs: sleep,
          exerciseLogs: exercise,
          wellnessLogs: wellness
        }
      }
      
      // Create and trigger download
      const blob = new Blob([JSON.stringify(exportData, null, 2)], { type: 'application/json' })
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = `tmd_health_data_${new Date().toISOString().split('T')[0]}.json`
      document.body.appendChild(a)
      a.click()
      document.body.removeChild(a)
      URL.revokeObjectURL(url)
    } catch (error) {
      console.error("Export failed", error)
      alert("Failed to securely export data. Please try again.")
    }
  }

  const handleResetDemo = () => {
    resetDemoState()
    setResetDone(true)
    setTimeout(() => {
      setResetDone(false)
      window.location.reload()
    }, 1200)
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ marginBottom: '0.25rem' }}>Platform Settings</h1>
        <p className="text-secondary">Manage your preferences, demo environment, and platform configuration.</p>
      </div>

      <div className="card" style={{ padding: 0, overflow: 'hidden' }}>
        <SettingRow 
          icon={isDark ? Moon : Sun}
          title="Interface Theme"
          description={`Currently using ${theme} mode. Toggle to switch the global platform theme.`}
          action={
            <button onClick={toggleTheme} className="btn btn-outline">
              Toggle to {isDark ? 'Light' : 'Dark'}
            </button>
          }
        />

        <SettingRow 
          icon={Sparkles}
          title="Demo Mode Environment"
          description="Self-contained in-browser clinical database for portfolio review and demonstrations."
          action={
            <button 
              onClick={handleResetDemo} 
              className="btn btn-outline"
              style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#059669', borderColor: '#059669' }}
            >
              {resetDone ? <><Check size={16} /> Reset Done!</> : <><RotateCcw size={16} /> Reset Demo Data</>}
            </button>
          }
        />

        <SettingRow 
          icon={Bell}
          title="Email Notifications"
          description="Receive weekly health reports and exercise reminders."
          action={
            <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
              <input type="checkbox" defaultChecked style={{ width: '20px', height: '20px', accentColor: 'var(--brand-primary)' }} />
            </label>
          }
        />

        <SettingRow 
          icon={Globe}
          title="Measurement Units"
          description="Choose between Metric (kg, cm) and Imperial (lbs, in)."
          action={
            <select className="input-field" style={{ width: 'auto' }}>
              <option>Metric</option>
              <option>Imperial</option>
            </select>
          }
        />

        <SettingRow 
          icon={Database}
          title="Data Export"
          description="Download all your logs and configurations as a JSON file."
          action={
            <button className="btn btn-outline" onClick={handleDataExport}>Download JSON</button>
          }
        />

        <SettingRow 
          icon={User}
          title="Update Profile"
          description="Manage your account details and personal clinical information."
          action={
            <Link to="/profile" className="btn btn-ghost" style={{ color: 'var(--brand-primary)', textDecoration: 'none' }}>Manage</Link>
          }
        />
      </div>
    </div>
  )
}
