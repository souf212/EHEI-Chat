import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { authService } from '../services/api'
import { isValidMoroccanPhone, normalizePhone } from '../utils/phoneValidator'
import './Auth.css'

/**
 * Page d'inscription
 * 
 * Permet à l'utilisateur de s'inscrire avec son numéro de téléphone marocain
 * Envoie un code de vérification via WhatsApp
 */
function Register({ onUserRegistered }) {
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
      // Appeler l'API d'inscription
      const response = await authService.register(normalized)

      if (response.success) {
        // Stocker le numéro pour la page de vérification
        localStorage.setItem('pendingPhoneNumber', normalized)
        localStorage.setItem('pendingUserId', response.userId)

        // Rediriger vers la page de vérification
        navigate('/verify')
      } else {
        setError(response.message || 'Erreur lors de l\'inscription')
      }
    } catch (err) {
      console.error('Erreur inscription:', err)
      setError(
        err.response?.data?.message || 
        'Erreur lors de l\'inscription. Vérifiez votre connexion.'
      )
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h1>📱 Inscription</h1>
        <p className="subtitle">Plateforme de Communication EHEI</p>

        <form onSubmit={handleSubmit} className="auth-form">
          <div className="form-group">
            <label htmlFor="phone">Numéro de téléphone marocain</label>
            <input
              type="tel"
              id="phone"
              placeholder="+212612345678"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              disabled={loading}
              required
            />
            <small>Format : +212 suivi de 9 chiffres</small>
          </div>

          {error && <div className="error-message">{error}</div>}

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'Envoi en cours...' : 'Envoyer le code de vérification'}
          </button>
        </form>

        <div className="auth-footer">
          <p>
            Déjà inscrit ?{' '}
            <a href="/login" onClick={(e) => {
              e.preventDefault()
              navigate('/login')
            }}>
              Se connecter
            </a>
          </p>
        </div>
      </div>
    </div>
  )
}

export default Register

