import React, { useState, useEffect, useRef } from 'react'
import webSocketService from '../services/websocket'
import { chatService } from '../services/api'
import ChatList from '../components/ChatList'
import ChatWindow from '../components/ChatWindow'
import './Chat.css'

/**
 * Page principale de chat
 * 
 * Affiche :
 * - Liste des conversations (gauche)
 * - Fenêtre de chat (droite)
 * 
 * Gère la connexion WebSocket et la synchronisation des messages
 */
function Chat({ user, onLogout }) {
  const [selectedChat, setSelectedChat] = useState(null)
  const [messages, setMessages] = useState([])
  const [contacts, setContacts] = useState([])
  const [loading, setLoading] = useState(false)
  const messagesEndRef = useRef(null)

  // Connexion WebSocket au chargement
  useEffect(() => {
    if (user && user.id) {
      // Se connecter au WebSocket
      webSocketService.connect(
        user.id,
        // Callback pour recevoir des messages
        (message) => {
          console.log('📨 Message reçu:', message)
          
          if (message.type === 'NOTIFICATION') {
            // Gérer les notifications
            handleNotification(message)
          } else {
            // Ajouter le message à la liste
            setMessages((prev) => {
              // Éviter les doublons
              const exists = prev.some((m) => m.id === message.id)
              if (exists) return prev
              return [...prev, message]
            })
          }
        },
        // Callback d'erreur
        (error) => {
          console.error('Erreur WebSocket:', error)
        }
      )
    }

    // Nettoyage à la déconnexion
    return () => {
      webSocketService.disconnect()
    }
  }, [user])

  /**
   * Gérer les notifications reçues
   */
  const handleNotification = (notification) => {
    console.log('🔔 Notification:', notification)
    // Ici, on pourrait afficher une notification toast
    // ou mettre à jour la liste des conversations
  }

  /**
   * Charger l'historique d'un chat
   */
  const loadChatHistory = async (chatId, chatType = 'INDIVIDUAL') => {
    setLoading(true)
    try {
      let history = []
      
      if (chatType === 'INDIVIDUAL') {
        // Chat individuel : chatId est l'ID de l'autre utilisateur
        const response = await chatService.getChatHistory(user.id, chatId)
        if (response.success) {
          history = response.messages || []
        }
      } else if (chatType === 'GROUP') {
        // Chat de groupe
        const response = await chatService.getGroupHistory(chatId)
        if (response.success) {
          history = response.messages || []
        }
      }

      setMessages(history)
    } catch (error) {
      console.error('Erreur lors du chargement de l\'historique:', error)
    } finally {
      setLoading(false)
    }
  }

  /**
   * Sélectionner un chat
   */
  const handleSelectChat = (chat) => {
    setSelectedChat(chat)
    if (chat) {
      loadChatHistory(chat.id, chat.type)
    } else {
      setMessages([])
    }
  }

  /**
   * Envoyer un message
   */
  const handleSendMessage = (content) => {
    if (!selectedChat || !content.trim()) return

    if (selectedChat.type === 'INDIVIDUAL') {
      // Message individuel
      webSocketService.sendIndividualMessage(
        user.id,
        selectedChat.id,
        content
      )
    } else if (selectedChat.type === 'GROUP') {
      // Message de groupe
      webSocketService.sendGroupMessage(
        user.id,
        selectedChat.id,
        content
      )
    }

    // Ajouter le message localement (optimistic update)
    const newMessage = {
      id: Date.now().toString(), // ID temporaire
      senderId: user.id,
      receiverId: selectedChat.type === 'INDIVIDUAL' ? selectedChat.id : null,
      groupId: selectedChat.type === 'GROUP' ? selectedChat.id : null,
      content,
      timestamp: new Date().toISOString(),
      type: selectedChat.type,
      read: false,
    }

    setMessages((prev) => [...prev, newMessage])
  }

  /**
   * Scroll vers le bas quand de nouveaux messages arrivent
   */
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  return (
    <div className="chat-container">
      {/* Liste des conversations (gauche) */}
      <div className="chat-sidebar">
        <div className="chat-header">
          <h2>💬 EHEI Chat</h2>
          <div className="user-info">
            <span>{user.phoneNumber}</span>
            <button onClick={onLogout} className="btn-logout">
              Déconnexion
            </button>
          </div>
        </div>
        <ChatList
          user={user}
          selectedChat={selectedChat}
          onSelectChat={handleSelectChat}
        />
      </div>

      {/* Fenêtre de chat (droite) */}
      <div className="chat-main">
        {selectedChat ? (
          <ChatWindow
            chat={selectedChat}
            user={user}
            messages={messages}
            onSendMessage={handleSendMessage}
            loading={loading}
            messagesEndRef={messagesEndRef}
          />
        ) : (
          <div className="chat-placeholder">
            <div className="placeholder-content">
              <h3>👋 Bienvenue sur EHEI Chat</h3>
              <p>Sélectionnez une conversation pour commencer</p>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default Chat

