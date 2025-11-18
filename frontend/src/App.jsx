import React, { useState, useEffect } from 'react'
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Register from './pages/Register'
import Verify from './pages/Verify'
import Login from './pages/Login'
import Chat from './pages/Chat'
import './App.css'

/**
 * Composant principal de l'application
 * 
 * Gère la navigation entre les différentes pages :
 * - /register : Inscription
 * - /verify : Vérification du code
 * - /login : Connexion
 * - /chat : Page de chat (protégée)
 */
function App() {
  // État pour stocker l'utilisateur connecté
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // Vérifier si un utilisateur est déjà connecté (depuis localStorage)
  useEffect(() => {
    const savedUser = localStorage.getItem('user')
    if (savedUser) {
      try {
        setUser(JSON.parse(savedUser))
      } catch (e) {
        console.error('Erreur lors du chargement de l\'utilisateur:', e)
      }
    }
    setLoading(false)
  }, [])

  // Sauvegarder l'utilisateur dans localStorage quand il change
  useEffect(() => {
    if (user) {
      localStorage.setItem('user', JSON.stringify(user))
    } else {
      localStorage.removeItem('user')
    }
  }, [user])

  // Composant pour protéger les routes (nécessite une connexion)
  const ProtectedRoute = ({ children }) => {
    if (loading) {
      return <div className="loading">Chargement...</div>
    }
    return user ? children : <Navigate to="/login" />
  }

  if (loading) {
    return <div className="loading">Chargement...</div>
  }

  return (
    <Router>
      <div className="app">
        <Routes>
          {/* Route publique : Inscription */}
          <Route 
            path="/register" 
            element={<Register onUserRegistered={(user) => setUser(user)} />} 
          />
          
          {/* Route publique : Vérification */}
          <Route 
            path="/verify" 
            element={<Verify onVerified={(user) => setUser(user)} />} 
          />
          
          {/* Route publique : Connexion */}
          <Route 
            path="/login" 
            element={
              user ? (
                <Navigate to="/chat" />
              ) : (
                <Login onLogin={(user) => setUser(user)} />
              )
            } 
          />
          
          {/* Route protégée : Chat */}
          <Route 
            path="/chat" 
            element={
              <ProtectedRoute>
                <Chat user={user} onLogout={() => setUser(null)} />
              </ProtectedRoute>
            } 
          />
          
          {/* Route par défaut : Rediriger vers /login */}
          <Route path="/" element={<Navigate to="/login" />} />
        </Routes>
      </div>
    </Router>
  )
}

export default App

