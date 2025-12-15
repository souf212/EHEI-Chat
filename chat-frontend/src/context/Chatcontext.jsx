/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useContext, useState } from 'react';

/**
 * Contexte Chat pour gérer l'état global de l'application
 * Gère les conversations, messages, et utilisateurs
 */
const ChatContext = createContext(null);

export const useChatContext = () => {
  const context = useContext(ChatContext);
  if (!context) {
    throw new Error('useChatContext doit être utilisé dans un ChatProvider');
  }
  return context;
};

export const ChatProvider = ({ children }) => {
  // Utilisateur actuellement sélectionné pour la conversation
  const [selectedUser, setSelectedUser] = useState(null);
  
  // Messages de la conversation active
  const [messages, setMessages] = useState([]);
  
  // Notifications de nouveaux messages (pour les conversations non actives)
  const [notifications, setNotifications] = useState([]);
  
  // Liste des utilisateurs disponibles (simulé pour le MVP)
  const [users] = useState([
    { id: 'alice', name: 'Alice', online: true },
    { id: 'bob', name: 'Bob', online: true },
    { id: 'charlie', name: 'Charlie', online: false },
    { id: 'diana', name: 'Diana', online: true }
  ]);

  /**
   * Ajoute un message à la conversation active
   * @param message - Message à ajouter
   */
  const addMessage = (message) => {
    setMessages(prev => [...prev, message]);
  };

  /**
   * Charge les messages d'une conversation
   * @param messageList - Liste des messages à afficher
   */
  const loadMessages = (messageList) => {
    setMessages(messageList);
  };

  /**
   * Ajoute une notification pour un nouveau message
   * @param notification - Objet notification {senderId, senderName, content}
   */
  const addNotification = (notification) => {
    setNotifications(prev => [...prev, notification]);
  };

  /**
   * Supprime les notifications d'un utilisateur spécifique
   * @param userId - ID de l'utilisateur
   */
  const clearNotifications = (userId) => {
    setNotifications(prev => 
      prev.filter(notif => notif.senderId !== userId)
    );
  };

  /**
   * Compte le nombre de notifications pour un utilisateur
   * @param userId - ID de l'utilisateur
   */
  const getNotificationCount = (userId) => {
    return notifications.filter(n => n.senderId === userId).length;
  };

  const value = {
    selectedUser,
    setSelectedUser,
    messages,
    addMessage,
    loadMessages,
    notifications,
    addNotification,
    clearNotifications,
    getNotificationCount,
    users
  };

  return (
    <ChatContext.Provider value={value}>
      {children}
    </ChatContext.Provider>
  );
};