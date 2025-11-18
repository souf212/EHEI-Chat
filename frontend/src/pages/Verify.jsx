import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { authService } from '../services/api'
import './Auth.css'

/**
 * Page de vérification
 * 
 * Permet à l'utilisateur de saisir le code reçu via WhatsApp
 */
function Verify({ onVerified }) {
  const navigate = useNavigate()
  const [code, setCode] = useState('')
  const [phoneNumber, setPhoneNumber] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  // Charger le numéro depuis localStorage
  useEffect(() => {
    const savedPhone = localStorage.getItem('pendingPhoneNumber')
    if (savedPhone) {
      setPhoneNumber(savedPhone)
    } else {
      // Si pas de numéro, rediriger vers l'inscription
      navigate('/register')
    }
  }, [navigate])

  /**
   * Gérer la soumission du code
   */
  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    // Valider le code (6 chiffres)
    if (!/^\d{6}$/.test(code)) {
      setError('Le code doit contenir 6 chiffres')
      return
    }

    setLoading(true)

    try {
      // Appeler l'API de vérification
      const response = await authService.verify(phoneNumber, code)

      if (response.success && response.user) {
        // Nettoyer les données temporaires
        localStorage.removeItem('pendingPhoneNumber')
        localStorage.removeItem('pendingUserId')

        // Notifier le parent et rediriger
        onVerified(response.user)
        navigate('/chat')
      } else {
        setError(response.message || 'Code invalide')
      }
    } catch (err) {
      console.error('Erreur vérification:', err)
      setError(
        err.response?.data?.message || 
        'Code invalide ou expiré. Demandez un nouveau code.'
      )
    } finally {
      setLoading(false)
    }
  }

  /**
   * Demander un nouveau code
   */
  const handleResend = async () => {
    setError('')
    setLoading(true)

    try {
      await authService.register(phoneNumber)
      setError('')
      alert('Nouveau code envoyé !')
    } catch (err) {
      setError('Erreur lors de l\'envoi du nouveau code')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>✅ Vérification</h1>
        <p className="subtitle">
          Entrez le code reçu sur WhatsApp
          <br />
          <small>{phoneNumber}</small>
        </p>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="code">Code de vérification</label>
            <input
              type="text"
              id="code"
              placeholder="123456"
              value={code}
              onChange={(e) => {
                // Ne permettre que les chiffres, max 6
                const value = e.target.value.replace(/\D/g, '').slice(0, 6)
                setCode(value)
              }}
              disabled={loading}
              maxLength={6}
              required
              autoFocus
            />
            <small>Code à 6 chiffres</small>
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn-primary" disabled={loading || code.length !== 6}>
            {loading ? 'Vérification...' : 'Vérifier'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Pas reçu le code ?{' '}
            <button type="button" onClick={handleResend} className="link-button" disabled={loading}>
              Renvoyer
            </button>
          </p>
          <p>
            <a href="/register" onClick={(e) => {
              e.preventDefault()
              navigate('/register')
            }}>
              Changer de numéro
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Verify

