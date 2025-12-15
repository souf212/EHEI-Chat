import React from 'react';
import { useChatContext } from '../context/Chatcontext';
import { useWebSocket } from '../context/WebSocketContext';
import './UserList.css';

/**
 * Composant UserList - Affiche la liste des utilisateurs disponibles
 * Permet de sélectionner un utilisateur pour commencer une conversation
 */
const UserList = () => {
  const { users, selectedUser, setSelectedUser, getNotificationCount } = useChatContext();
  const { currentUser } = useWebSocket();

  /**
   * Gère la sélection d'un utilisateur
   */
  const handleSelectUser = (user) => {
    setSelectedUser(user);
  };

  return (
    <div className="user-list">
      <div className="user-list-header">
        <h3>💬 Contacts</h3>
        <div className="current-user">
          <span className="user-badge">{currentUser}</span>
        </div>
      </div>

      <div className="users">
        {users
          .filter(user => user.id !== currentUser)
          .map(user => {
            const notifCount = getNotificationCount(user.id);
            const isSelected = selectedUser?.id === user.id;

            return (
              <div
                key={user.id}
                className={`user-item ${isSelected ? 'selected' : ''}`}
                onClick={() => handleSelectUser(user)}
              >
                <div className="user-avatar">
                  {user.name.charAt(0).toUpperCase()}
                  {user.online && <span className="online-indicator"></span>}
                </div>

                <div className="user-info">
                  <div className="user-name">{user.name}</div>
                  <div className="user-status">
                    {user.online ? 'En ligne' : 'Hors ligne'}
                  </div>
                </div>

                {notifCount > 0 && (
                  <div className="notification-badge">
                    {notifCount}
                  </div>
                )}
              </div>
            );
          })}
      </div>

      {users.filter(u => u.id !== currentUser).length === 0 && (
        <div className="no-users">
          Aucun utilisateur disponible
        </div>
      )}
    </div>
  );
};

export default UserList;