import React, { useState } from 'react';
import { useWebSocket } from '../context/WebSocketContext';
import './Login.css';

/**
 * Composant Login - Écran d'authentification simple
 * Permet à l'utilisateur de saisir son nom d'utilisateur
 */
const Login = ({ onLogin }) => {
  const [username, setUsername] = useState('');
  const [error, setError] = useState('');
  const { connect } = useWebSocket();

  /**
   * Gère la soumission du formulaire de login
   */
  const handleSubmit = (e) => {
    e.preventDefault();
    
    // Validation basique
    if (!username.trim()) {
      setError('Veuillez entrer un nom d\'utilisateur');
      return;
    }

    if (username.trim().length < 2) {
      setError('Le nom doit contenir au moins 2 caractères');
      return;
    }

    // Connexion WebSocket et callback
    const userId = username.toLowerCase().trim();
    connect(userId);
    onLogin(userId, username);
  };

  /**
   * Permet de se connecter avec un utilisateur prédéfini
   */
  const quickLogin = (userId, displayName) => {
    connect(userId);
    onLogin(userId, displayName);
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="login-hero">
          <div className="login-title">
            <div className="login-title-icon">💬</div>
            <span>Realtime Chat</span>
          </div>
          <p className="login-subtitle">
            Discutez instantanément avec votre équipe dans une interface moderne, fluide et agréable.
          </p>
          <div className="login-hero-highlight">
            <span>⚡ Temps réel</span>
            <span>•</span>
            <span>Notifications discrètes</span>
            <span>•</span>
            <span>Conversations privées</span>
          </div>
          <p className="login-hero-footnote">
            Choisissez un pseudo ou utilisez la connexion rapide pour tester le chat en quelques secondes.
          </p>
        </div>

        <div className="login-form-wrapper">
          <h2>Se connecter</h2>
          <p className="login-subtitle">Entrez un nom d'utilisateur pour rejoindre la conversation.</p>

          <form onSubmit={handleSubmit}>
            <div className="form-group">
              <input
                type="text"
                placeholder="Nom d'utilisateur"
                value={username}
                onChange={(e) => {
                  setUsername(e.target.value);
                  setError('');
                }}
                className="login-input"
                autoFocus
              />
            </div>

            {error && <div className="error-message">{error}</div>}

            <button type="submit" className="login-button">
              Continuer
            </button>
          </form>

          <div className="quick-login">
            <p>Connexion rapide</p>
            <div className="quick-login-buttons">
              <button onClick={() => quickLogin('alice', 'Alice')} className="quick-button">
                Alice
              </button>
              <button onClick={() => quickLogin('bob', 'Bob')} className="quick-button">
                Bob
              </button>
              <button onClick={() => quickLogin('charlie', 'Charlie')} className="quick-button">
                Charlie
              </button>
              <button onClick={() => quickLogin('diana', 'Diana')} className="quick-button">
                Diana
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;