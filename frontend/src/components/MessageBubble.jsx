import React from 'react'
import './MessageBubble.css'

/**
 * Composant : Bulle de message
 * 
 * Affiche un message avec :
 * - Style différent selon si c'est notre message ou celui de l'autre
 * - Timestamp
 * - Statut de lecture (optionnel)
 */
function MessageBubble({ message, isOwn }) {
  const formatTime = (timestamp) => {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return date.toLocaleTimeString('fr-FR', {
      hour: '2-digit',
      minute: '2-digit',
    })
  }

  return (
    <div className={`message-bubble ${isOwn ? 'own' : 'other'}`}>
      <div className="message-content">
        <p>{message.content}</p>
        <span className="message-time">{formatTime(message.timestamp)}</span>
      </div>
    </div>
  )
}

export default MessageBubble

