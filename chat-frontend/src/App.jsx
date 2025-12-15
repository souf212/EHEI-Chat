import React, { useState } from 'react';
import { WebSocketProvider } from './context/WebSocketContext';
import { ChatProvider } from './context/Chatcontext';
import Login from './components/Login';
import Chat from './components/Chat';
import './App.css';

/**
 * Composant App - Point d'entrée de l'application
 * Gère l'état de connexion et le routing entre Login et Chat
 */
function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  /**
   * Gère la connexion utilisateur
   */
  const handleLogin = (id, name) => {
    setIsLoggedIn(true);
    console.log(`✅ Utilisateur connecté: ${name} (${id})`);
  };

  /**
   * Gère la déconnexion utilisateur
   */
  const handleLogout = () => {
    setIsLoggedIn(false);
    console.log('👋 Utilisateur déconnecté');
  };

  return (
    <WebSocketProvider>
      <ChatProvider>
        <div className="app">
          {!isLoggedIn ? (
            <Login onLogin={handleLogin} />
          ) : (
            <Chat onLogout={handleLogout} />
          )}
        </div>
      </ChatProvider>
    </WebSocketProvider>
  );
}

export default App;