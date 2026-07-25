import { useState, useRef, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { ArrowLeft, Send, ShieldAlert, CheckCircle, Info, AlertTriangle, Lightbulb } from 'lucide-react'
import { api } from '../lib/api'

export default function AiChat() {
  const navigate = useNavigate()
  const [messages, setMessages] = useState([])
  const [inputText, setInputText] = useState('')
  const [isLoading, setIsLoading] = useState(false)
  const messagesEndRef = useRef(null)

  const scrollToBottom = () => {
    setTimeout(() => {
      messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, 100)
  }

  useEffect(() => {
    scrollToBottom()
  }, [messages, isLoading])

  const suggestions = [
    "How can I reduce jaw pain?",
    "What exercises help TMD?",
    "How does stress affect TMD?",
    "Tips for improving sleep quality?",
    "When should I consult a doctor?"
  ]

  const formatMarkdown = (text) => {
    // Split into lines
    const lines = text.split('\n')
    const elements = []
    let listItems = []
    let listType = null // 'ul' or 'ol'
    let key = 0

    const flushList = () => {
      if (listItems.length > 0) {
        if (listType === 'ol') {
          elements.push(<ol key={key++} style={{ margin: '0.5rem 0', paddingLeft: '1.5rem', lineHeight: '1.7' }}>{listItems}</ol>)
        } else {
          elements.push(<ul key={key++} style={{ margin: '0.5rem 0', paddingLeft: '1.5rem', lineHeight: '1.7' }}>{listItems}</ul>)
        }
        listItems = []
        listType = null
      }
    }

    const formatInline = (str) => {
      // Bold: **text** or __text__
      const parts = []
      const regex = /(\*\*|__)(.+?)\1/g
      let lastIndex = 0
      let match

      while ((match = regex.exec(str)) !== null) {
        if (match.index > lastIndex) {
          parts.push(str.slice(lastIndex, match.index))
        }
        parts.push(<strong key={`b-${match.index}`}>{match[2]}</strong>)
        lastIndex = regex.lastIndex
      }
      if (lastIndex < str.length) {
        parts.push(str.slice(lastIndex))
      }
      return parts.length > 0 ? parts : str
    }

    lines.forEach((line) => {
      const trimmed = line.trim()

      // Empty line — flush and add spacing
      if (!trimmed) {
        flushList()
        return
      }

      // Headings: ### text, ## text, # text
      if (trimmed.startsWith('### ')) {
        flushList()
        elements.push(<h4 key={key++} style={{ margin: '0.75rem 0 0.25rem 0', fontSize: '0.95rem', fontWeight: 700 }}>{formatInline(trimmed.slice(4))}</h4>)
        return
      }
      if (trimmed.startsWith('## ')) {
        flushList()
        elements.push(<h3 key={key++} style={{ margin: '0.75rem 0 0.25rem 0', fontSize: '1.05rem', fontWeight: 700 }}>{formatInline(trimmed.slice(3))}</h3>)
        return
      }
      if (trimmed.startsWith('# ')) {
        flushList()
        elements.push(<h2 key={key++} style={{ margin: '0.75rem 0 0.25rem 0', fontSize: '1.15rem', fontWeight: 700 }}>{formatInline(trimmed.slice(2))}</h2>)
        return
      }

      // Horizontal rule: --- or ***
      if (/^[-*]{3,}$/.test(trimmed)) {
        flushList()
        elements.push(<hr key={key++} style={{ border: 'none', borderTop: '1px solid var(--surface-border)', margin: '0.75rem 0' }} />)
        return
      }

      // Unordered list: - item or * item
      const ulMatch = trimmed.match(/^[-*•]\s+(.+)/)
      if (ulMatch) {
        if (listType !== 'ul') flushList()
        listType = 'ul'
        listItems.push(<li key={key++} style={{ marginBottom: '0.25rem' }}>{formatInline(ulMatch[1])}</li>)
        return
      }

      // Ordered list: 1. item
      const olMatch = trimmed.match(/^\d+[.)]\s+(.+)/)
      if (olMatch) {
        if (listType !== 'ol') flushList()
        listType = 'ol'
        listItems.push(<li key={key++} style={{ marginBottom: '0.25rem' }}>{formatInline(olMatch[1])}</li>)
        return
      }

      // Regular paragraph
      flushList()
      elements.push(<p key={key++} style={{ margin: '0.35rem 0', lineHeight: '1.6' }}>{formatInline(trimmed)}</p>)
    })

    flushList()
    return elements
  }

  const parseMessageContent = (content) => {
    return <div style={{ fontSize: '0.925rem' }}>{formatMarkdown(content)}</div>
  }

  const handleSend = async (text) => {
    if (!text.trim() || isLoading) return

    const newMessages = [...messages, { role: 'user', content: text, timestamp: Date.now() }]
    setMessages(newMessages)
    setInputText('')
    setIsLoading(true)

    try {
      const history = newMessages.map(m => ({ role: m.role, content: m.content }))
      
      const result = await api.post('/chat', { messages: history })
      
      if (!result) throw new Error("No response from server")
      if (result.error) throw new Error(result.error)

      const botReply = result.choices?.[0]?.message?.content || result.reply || "Sorry, I couldn't process that."
      setMessages([...newMessages, { role: 'assistant', content: botReply, timestamp: Date.now() }])
      
    } catch (error) {
      console.error("Groq API Error:", error)
      // Robust Fallback if API key is invalid
      const fallbackResponse = `[WARNING] The Groq API key is invalid or missing.\n\n[INFO] For demonstration purposes, I am returning this mock response.\n\n[REC] Please ensure you are resting your jaw and applying a warm compress.`
      setMessages([...newMessages, { role: 'assistant', content: fallbackResponse, timestamp: Date.now() }])
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', height: '100%', maxWidth: '900px', margin: '0 auto', backgroundColor: 'var(--surface)', borderRadius: '12px', boxShadow: 'var(--shadow-sm)', overflow: 'hidden' }}>
      
      {/* Header */}
      <div style={{ padding: '1.25rem', borderBottom: '1px solid var(--surface-border)', display: 'flex', alignItems: 'center', backgroundColor: 'var(--bg-secondary)' }}>
        <h1 style={{ fontSize: '1.25rem', fontWeight: 'bold', color: 'var(--text-primary)', margin: 0, display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <ShieldAlert size={24} color="var(--brand-primary)" />
          TMD Self-Care Clinical Assistant
        </h1>
      </div>

      {/* Warning Banner */}
      <div style={{ backgroundColor: 'rgba(37, 99, 235, 0.05)', borderBottom: '1px solid rgba(37, 99, 235, 0.1)', color: 'var(--brand-primary)', padding: '0.75rem 1.25rem', fontSize: '0.8125rem', display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
        <Info size={16} />
        <span>This AI assistant provides general wellness guidance and is not a replacement for professional medical advice. Always consult a physician for severe pain.</span>
      </div>

      {/* Messages */}
      <div style={{ flex: 1, overflowY: 'auto', padding: '1.5rem', display: 'flex', flexDirection: 'column', gap: '1.5rem', backgroundColor: 'var(--bg-primary)' }}>
        {messages.length === 0 && (
          <div style={{ textAlign: 'center', marginTop: '2rem' }}>
            <div style={{ width: '64px', height: '64px', backgroundColor: 'var(--brand-light)', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 1rem auto' }}>
              <ShieldAlert size={32} color="var(--brand-primary)" />
            </div>
            <h2 style={{ color: 'var(--text-primary)', marginBottom: '0.5rem' }}>How can I help you today?</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '2rem' }}>Ask me about your symptoms, exercises, or general TMD wellness.</p>
            
            <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.75rem', justifyContent: 'center', maxWidth: '600px', margin: '0 auto' }}>
              {suggestions.map(s => (
                <button 
                  key={s} 
                  onClick={() => handleSend(s)}
                  className="btn btn-outline"
                  style={{ borderRadius: '24px', padding: '0.5rem 1rem', fontSize: '0.875rem' }}
                >
                  {s}
                </button>
              ))}
            </div>
          </div>
        )}

        {messages.map((msg, idx) => {
          const isUser = msg.role === 'user'
          return (
            <div key={idx} style={{ alignSelf: isUser ? 'flex-end' : 'flex-start', maxWidth: '85%' }}>
              <div style={{
                backgroundColor: isUser ? 'var(--brand-primary)' : 'var(--surface)',
                color: isUser ? 'white' : 'var(--text-primary)',
                padding: '1rem',
                borderRadius: isUser ? '16px 16px 4px 16px' : '16px 16px 16px 4px',
                boxShadow: 'var(--shadow-sm)',
                border: isUser ? 'none' : '1px solid var(--surface-border)'
              }}>
                {isUser ? msg.content : parseMessageContent(msg.content)}
              </div>
              <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '0.35rem', textAlign: isUser ? 'right' : 'left' }}>
                {new Date(msg.timestamp).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              </div>
            </div>
          )
        })}

        {isLoading && (
          <div style={{ alignSelf: 'flex-start', backgroundColor: 'var(--surface)', padding: '1rem', borderRadius: '16px 16px 16px 4px', border: '1px solid var(--surface-border)', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span className="typing-dot" style={{ animationDelay: '0s', width: '8px', height: '8px', backgroundColor: 'var(--brand-primary)', borderRadius: '50%', display: 'inline-block' }}></span>
            <span className="typing-dot" style={{ animationDelay: '0.2s', width: '8px', height: '8px', backgroundColor: 'var(--brand-primary)', borderRadius: '50%', display: 'inline-block' }}></span>
            <span className="typing-dot" style={{ animationDelay: '0.4s', width: '8px', height: '8px', backgroundColor: 'var(--brand-primary)', borderRadius: '50%', display: 'inline-block' }}></span>
          </div>
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Input Area */}
      <div style={{ padding: '1.25rem', backgroundColor: 'var(--surface)', borderTop: '1px solid var(--surface-border)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
          <input
            type="text"
            value={inputText}
            onChange={(e) => setInputText(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSend(inputText)}
            placeholder="Describe your symptoms or ask a question..."
            style={{
              flex: 1, padding: '1rem', borderRadius: '24px',
              border: '1px solid var(--surface-border)', outline: 'none', backgroundColor: 'var(--bg-secondary)', color: 'var(--text-primary)',
              fontSize: '0.9375rem'
            }}
          />
          <button
            onClick={() => handleSend(inputText)}
            disabled={!inputText.trim() || isLoading}
            style={{
              width: '52px', height: '52px', borderRadius: '50%',
              backgroundColor: inputText.trim() && !isLoading ? 'var(--brand-primary)' : 'var(--surface-border)',
              color: 'white', border: 'none', display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer',
              transition: 'all 0.2s'
            }}
          >
            <Send size={20} />
          </button>
        </div>
      </div>
      
      <style>{`
        @keyframes bounce { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-4px); } }
        .typing-dot { animation: bounce 1.4s infinite ease-in-out both; }
      `}</style>
    </div>
  )
}
