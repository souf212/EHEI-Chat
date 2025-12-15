import React, { useEffect, useState } from 'react';
import { useWebSocket } from '../context/WebSocketContext';
import { useChatContext } from '../context/Chatcontext';
import UserList from './UserList';
import ChatWindow from './ChatWindow';
import NotificationContainer from './Notification';
import './Chat.css';

/**
 * Composant Chat - Interface principale de l'application
 * Gère la communication WebSocket et les notifications
 */
const Chat = ({ onLogout }) => {
  const { currentUser, subscribe, disconnect } = useWebSocket();
  const { addMessage, selectedUser, addNotification, users } = useChatContext();
  const [displayNotifications, setDisplayNotifications] = useState([]);

  /**
   * S'abonne aux messages WebSocket lors du montage
   */
  useEffect(() => {
    if (!currentUser) return;

    console.log('🔌 Abonnement aux messages pour:', currentUser);

    // S'abonne aux messages privés
    const subscription = subscribe(currentUser, (message) => {
      console.log('📨 Nouveau message reçu:', message);

      // Si on parle déjà à cet utilisateur, ajouter le message direct
      if (selectedUser && message.senderId === selectedUser.id) {
        addMessage(message);
      } else {
        // Sinon, créer une notification
        const sender = users.find(u => u.id === message.senderId);
        const notification = {
          senderId: message.senderId,
          senderName: sender?.name || message.senderId,
          content: message.content
        };

        // Ajoute aux notifications persistantes (badge)
        addNotification(notification);

        // Ajoute aux notifications toast
        setDisplayNotifications(prev => [...prev, notification]);
      }
    });

    // Cleanup lors du démontage
    return () => {
      if (subscription) {
        subscription.unsubscribe();
        console.log('🔌 Désabonnement des messages');
      }
    };
  }, [currentUser, selectedUser, subscribe, addMessage, addNotification, users]);

  /**
   * Gère la déconnexion
   */
  const handleLogout = () => {
    disconnect();
    onLogout();
  };

  /**
   * Supprime une notification toast
   */
  const dismissNotification = (index) => {
    setDisplayNotifications(prev => prev.filter((_, i) => i !== index));
  };

  return (
    <div className="chat-container">
      <UserList />
      <ChatWindow />

      {/* Bouton de déconnexion */}
      <button className="logout-button" onClick={handleLogout} title="Se déconnecter">
        🚪 Déconnexion
      </button>

      {/* Notifications toast */}
      <NotificationContainer
        notifications={displayNotifications}
        onDismiss={dismissNotification}
      />
    </div>
  );
};

export default Chat;