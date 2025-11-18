import React, { useState, useEffect } from 'react'
import './ChatList.css'

/**
 * Composant : Liste des conversations
 * 
 * Affiche la liste des chats (individuels et groupes)
 * Pour simplifier, on affiche une liste statique
 * En production, charger depuis l'API
 */
function ChatList({ user, selectedChat, onSelectChat }) {
  const [chats, setChats] = useState([])
  const [searchQuery, setSearchQuery] = useState('')

  // Exemple de données (en production, charger depuis l'API)
  useEffect(() => {
    // Simuler des conversations
    // En production, faire un appel API pour récupérer les conversations
    const mockChats = [
      {
        id: 'contact1',
        name: 'Contact 1',
        phoneNumber: '+212612345678',
        type: 'INDIVIDUAL',
        lastMessage: 'Bonjour !',
        timestamp: new Date(),
        unread: 2,
      },
      {
        id: 'contact2',
        name: 'Contact 2',
        phoneNumber: '+212712345678',
        type: 'INDIVIDUAL',
        lastMessage: 'Comment allez-vous ?',
        timestamp: new Date(),
        unread: 0,
      },
      {
        id: 'group1',
        name: 'Groupe EHEI',
        type: 'GROUP',
        lastMessage: 'Nouveau message dans le groupe',
        timestamp: new Date(),
        unread: 5,
      },
    ]

    setChats(mockChats)
  }, [])

  // Filtrer les chats selon la recherche
  const filteredChats = chats.filter((chat) =>
    chat.name.toLowerCase().includes(searchQuery.toLowerCase())
  )

  return (
    <div className="chat-list">
      {/* Barre de recherche */}
      <div className="chat-search">
        <input
          type="text"
          placeholder="Rechercher une conversation..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      {/* Liste des conversations */}
      <div className="chat-items">
        {filteredChats.length === 0 ? (
          <div className="no-chats">
            <p>Aucune conversation trouvée</p>
          </div>
        ) : (
          filteredChats.map((chat) => (
            <div
              key={chat.id}
              className={`chat-item ${selectedChat?.id === chat.id ? 'active' : ''}`}
              onClick={() => onSelectChat(chat)}
            >
              <div className="chat-avatar">
                {chat.type === 'GROUP' ? '👥' : '👤'}
              </div>
              <div className="chat-info">
                <div className="chat-name-row">
                  <span className="chat-name">{chat.name}</span>
                  {chat.unread > 0 && (
                    <span className="unread-badge">{chat.unread}</span>
                  )}
                </div>
                <div className="chat-preview">
                  <span className="last-message">{chat.lastMessage}</span>
                  <span className="chat-time">
                    {formatTime(chat.timestamp)}
                  </span>
                </div>
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  )
}

/**
 * Formater l'heure pour l'affichage
 */
function formatTime(date) {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diff = now - d

  // Aujourd'hui : afficher l'heure
  if (diff < 24 * 60 * 60 * 1000) {
    return d.toLocaleTimeString('fr-FR', { hour: '2-digit', minute: '2-digit' })
  }

  // Cette semaine : afficher le jour
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    return d.toLocaleDateString('fr-FR', { weekday: 'short' })
  }

  // Plus ancien : afficher la date
  return d.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit' })
}

export default ChatList

