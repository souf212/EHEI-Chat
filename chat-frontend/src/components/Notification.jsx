import React, { useEffect, useState } from 'react';
import './Notification.css';

/**
 * Composant Notification - Affiche une notification toast
 * Se ferme automatiquement après 5 secondes
 */
const Notification = ({ notification, onClose }) => {
  const [isVisible, setIsVisible] = useState(true);

  useEffect(() => {
    // Fermeture automatique après 5 secondes
    const timer = setTimeout(() => {
      setIsVisible(false);
      setTimeout(onClose, 300); // Attend la fin de l'animation
    }, 5000);

    return () => clearTimeout(timer);
  }, [onClose]);

  const handleClose = () => {
    setIsVisible(false);
    setTimeout(onClose, 300);
  };

  return (
    <div className={`notification-toast ${isVisible ? 'show' : 'hide'}`}>
      <div className="notification-avatar">
        {notification.senderName?.charAt(0).toUpperCase() || '?'}
      </div>
      <div className="notification-content">
        <div className="notification-sender">{notification.senderName}</div>
        <div className="notification-message">{notification.content}</div>
      </div>
      <button className="notification-close" onClick={handleClose}>
        ✕
      </button>
    </div>
  );
};

/**
 * Conteneur pour gérer plusieurs notifications
 */
const NotificationContainer = ({ notifications, onDismiss }) => {
  return (
    <div className="notification-container">
      {notifications.map((notif, index) => (
        <Notification
          key={`${notif.senderId}-${index}`}
          notification={notif}
          onClose={() => onDismiss(index)}
        />
      ))}
    </div>
  );
};

export default NotificationContainer;