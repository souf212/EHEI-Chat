import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authService } from '../services/api'
import { isValidMoroccanPhone, normalizePhone } from '../utils/phoneValidator'
import './Auth.css'

/**
 * Page de connexion
 * 
 * Permet à un utilisateur vérifié de se connecter
 */
function Login({ onLogin }) {
  const navigate = useNavigate()
  const [phoneNumber, setPhoneNumber] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  /**
   * Gérer la soumission du formulaire
   */
  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')

    // Valider le numéro
    const normalized = normalizePhone(phoneNumber)
    if (!isValidMoroccanPhone(normalized)) {
      setError('Numéro invalide. Format attendu : +212XXXXXXXXX')
      return
    }

    setLoading(true)

    try {
      // Appeler l'API de connexion
      const response = await authService.login(normalized)

      if (response.success && response.user) {
        // Notifier le parent et rediriger
        onLogin(response.user)
        navigate('/chat')
      } else {
        setError(response.message || 'Erreur lors de la connexion')
      }
    } catch (err) {
      console.error('Erreur connexion:', err)
      const errorMessage = err.response?.data?.message || 'Erreur lors de la connexion'
      
      // Si l'utilisateur n'est pas vérifié, proposer de s'inscrire
      if (errorMessage.includes('vérifier')) {
        setError(`${errorMessage}. Veuillez vous inscrire d'abord.`)
      } else {
        setError(errorMessage)
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>🔐 Connexion</h1>
        <p className="subtitle">Plateforme de Communication EHEI</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="phone">Numéro de téléphone</label>
            <input
              type="tel"
              id="phone"
              placeholder="+212612345678"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              disabled={loading}
              required
            />
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Connexion...' : 'Se connecter'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Pas encore inscrit ?{' '}
            <a href="/register" onClick={(e) => {
              e.preventDefault()
              navigate('/register')
            }}>
              S'inscrire
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Login

