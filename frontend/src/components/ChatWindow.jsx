import React, { useState, useRef, useEffect } from 'react'
import MessageBubble from './MessageBubble'
import './ChatWindow.css'

/**
 * Composant : Fenêtre de chat
 * 
 * Affiche :
 * - En-tête avec le nom du contact/groupe
 * - Zone des messages
 * - Zone de saisie
 */
function ChatWindow({ chat, user, messages, onSendMessage, loading, messagesEndRef }) {
  const [inputValue, setInputValue] = useState('')
  const inputRef = useRef(null)

  // Focus sur l'input au chargement
  useEffect(() => {
    inputRef.current?.focus()
  }, [chat])

  /**
   * Gérer l'envoi d'un message
   */
  const handleSubmit = (e) => {
    e.preventDefault()
    if (inputValue.trim()) {
      onSendMessage(inputValue.trim())
      setInputValue('')
      inputRef.current?.focus()
    }
  }

  /**
   * Gérer la touche Entrée (sans Shift = envoyer, avec Shift = nouvelle ligne)
   */
  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault()
      handleSubmit(e)
    }
  }

  return (
    <div className="chat-window">
      {/* En-tête */}
      <div className="chat-window-header">
        <div className="header-info">
          <span className="header-avatar">
            {chat.type === 'GROUP' ? '👥' : '👤'}
          </span>
          <div>
            <h3>{chat.name}</h3>
            {chat.type === 'INDIVIDUAL' && (
              <span className="header-subtitle">{chat.phoneNumber}</span>
            )}
          </div>
        </div>
      </div>

      {/* Zone des messages */}
      <div className="chat-messages">
        {loading ? (
          <div className="loading-messages">Chargement des messages...</div>
        ) : messages.length === 0 ? (
          <div className="no-messages">
            <p>Aucun message pour le moment</p>
            <p className="hint">Commencez la conversation !</p>
          </div>
        ) : (
          <>
            {messages.map((message) => (
              <MessageBubble
                key={message.id}
                message={message}
                isOwn={message.senderId === user.id}
              />
            ))}
            <div ref={messagesEndRef} />
          </>
        )}
      </div>

      {/* Zone de saisie */}
      <div className="chat-input-container">
        <form onSubmit={handleSubmit} className="chat-input-form">
          <textarea
            ref={inputRef}
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            onKeyPress={handleKeyPress}
            placeholder="Tapez un message..."
            rows={1}
            className="chat-input"
          />
          <button
            type="submit"
            className="send-button"
            disabled={!inputValue.trim()}
          >
            ➤
          </button>
        </form>
      </div>
    </div>
  )
}

export default ChatWindow

