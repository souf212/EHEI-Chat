import React, { useState, useEffect, useRef } from 'react';
import { useChatContext } from '../context/Chatcontext';
import { useWebSocket } from '../context/WebSocketContext';
import axios from 'axios';
import './ChatWindow.css';

/**
 * Composant ChatWindow - Fenêtre de conversation principale
 * Affiche les messages et permet d'envoyer de nouveaux messages
 */
const ChatWindow = () => {
  const { selectedUser, messages, loadMessages, addMessage, clearNotifications } = useChatContext();
  const { currentUser, sendMessage } = useWebSocket();
  const [inputMessage, setInputMessage] = useState('');
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  /**
   * Charge l'historique des messages lors de la sélection d'un utilisateur
   */
  useEffect(() => {
    if (!selectedUser || !currentUser) return;

    // Efface les notifications pour cet utilisateur
    clearNotifications(selectedUser.id);

    // Charge l'historique depuis l'API
    axios
      .get(`http://localhost:8080/api/messages/${currentUser}/${selectedUser.id}`)
      .then(response => {
        loadMessages(response.data);
        console.log(`📥 ${response.data.length} messages chargés`);
      })
      .catch(error => {
        console.error('Erreur lors du chargement des messages:', error);
        loadMessages([]);
      });
  // On ne dépend que de l'identifiant de l'utilisateur sélectionné et de l'utilisateur courant
  }, [selectedUser?.id, currentUser]);

  /**
   * Scroll automatique vers le dernier message
   */
  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  /**
   * Gère l'envoi d'un message
   */
  const handleSendMessage = (e) => {
    e.preventDefault();

    if (!inputMessage.trim() || !selectedUser) {
      return;
    }

    const message = {
      senderId: currentUser,
      recipientId: selectedUser.id,
      content: inputMessage.trim(),
      timestamp: new Date().toISOString()
    };

    // Envoie via WebSocket
    sendMessage(message);

    // Ajoute le message à l'interface (optimistic update)
    addMessage({
      ...message,
      id: Date.now().toString(),
      status: 'SENT'
    });

    setInputMessage('');
  };

  /**
   * Gère la touche Enter pour envoyer
   */
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage(e);
    }
  };

  /**
   * Formate l'heure d'un message
   */
  const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  // Affichage si aucun utilisateur n'est sélectionné
  if (!selectedUser) {
    return (
      <div className="chat-window">
        <div className="no-chat-selected">
          <div className="no-chat-icon">💬</div>
          <h2>Sélectionnez une conversation</h2>
          <p>Choisissez un contact dans la liste pour commencer à chatter</p>
        </div>
      </div>
    );
  }

  return (
    <div className="chat-window">
      {/* Header avec info utilisateur */}
      <div className="chat-header">
        <div className="chat-user-avatar">
          {selectedUser.name.charAt(0).toUpperCase()}
        </div>
        <div className="chat-user-info">
          <div className="chat-user-name">{selectedUser.name}</div>
          <div className="chat-user-status">
            {selectedUser.online ? '🟢 En ligne' : '⚪ Hors ligne'}
          </div>
        </div>
      </div>

      {/* Zone des messages */}
      <div className="chat-messages">
        {messages.length === 0 ? (
          <div className="no-messages">
            Aucun message. Envoyez le premier ! 👋
          </div>
        ) : (
          messages.map((msg) => (
            <div
              key={msg.id}
              className={`message ${msg.senderId === currentUser ? 'sent' : 'received'}`}
            >
              <div className="message-content">
                {msg.content}
              </div>
              <div className="message-time">
                {formatTime(msg.timestamp)}
              </div>
            </div>
          ))
        )}
        <div ref={messagesEndRef} />
      </div>

      {/* Zone de saisie */}
      <form className="chat-input-container" onSubmit={handleSendMessage}>
        <input
          type="text"
          placeholder="Tapez votre message..."
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          onKeyPress={handleKeyPress}
          className="chat-input"
        />
        <button 
          type="submit" 
          className="send-button"
          disabled={!inputMessage.trim()}
        >
          📤
        </button>
      </form>
    </div>
  );
};

export default ChatWindow;