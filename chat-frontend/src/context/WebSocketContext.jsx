/* eslint-disable react-refresh/only-export-components */
import React, { createContext, useContext, useEffect, useState, useRef } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

/**
 * Contexte WebSocket pour gérer la connexion STOMP globale
 * Fournit les fonctions de connexion, déconnexion et envoi de messages
 */
const WebSocketContext = createContext(null);

export const useWebSocket = () => {
  const context = useContext(WebSocketContext);
  if (!context) {
    throw new Error('useWebSocket doit être utilisé dans un WebSocketProvider');
  }
  return context;
};

export const WebSocketProvider = ({ children }) => {
  const [stompClient, setStompClient] = useState(null);
  const [connected, setConnected] = useState(false);
  const [currentUser, setCurrentUser] = useState(null);
  const clientRef = useRef(null);
  
  /**
   * Initialise la connexion WebSocket avec STOMP
   * @param userId - Identifiant de l'utilisateur
   */
  const connect = (userId) => {
    if (clientRef.current?.connected) {
      console.log('Déjà connecté');
      return;
    }

    // Création du client STOMP avec SockJS
    const client = new Client({
      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),
      
      // Callback de connexion réussie
      onConnect: () => {
        console.log('✅ Connecté au WebSocket');
        setConnected(true);
        setCurrentUser(userId);
        setStompClient(client);
      },
      
      // Callback d'erreur
      onStompError: (frame) => {
        console.error('❌ Erreur STOMP:', frame.headers['message']);
        console.error('Détails:', frame.body);
      },
      
      // Callback de déconnexion
      onDisconnect: () => {
        console.log('🔌 Déconnecté du WebSocket');
        setConnected(false);
        setCurrentUser(null);
      },
      
      // Désactive les logs de debug
      debug: () => {
        // console.log('Debug STOMP');
      }
    });

    // Active la connexion
    client.activate();
    clientRef.current = client;
  };

  /**
   * Déconnecte le client WebSocket
   */
  const disconnect = () => {
    if (clientRef.current) {
      clientRef.current.deactivate();
      clientRef.current = null;
      setStompClient(null);
      setConnected(false);
      setCurrentUser(null);
    }
  };

  /**
   * Envoie un message via WebSocket
   * @param message - Objet message à envoyer
   */
  const sendMessage = (message) => {
    if (clientRef.current?.connected) {
      clientRef.current.publish({
        destination: '/app/chat',
        body: JSON.stringify(message)
      });
      console.log('📤 Message envoyé:', message);
    } else {
      console.error('❌ Non connecté au WebSocket');
    }
  };

  /**
   * S'abonne aux messages privés de l'utilisateur
   * @param userId - ID de l'utilisateur
   * @param callback - Fonction appelée lors de la réception d'un message
   */
  const subscribe = (userId, callback) => {
    if (clientRef.current?.connected) {
      const subscription = clientRef.current.subscribe(
        `/user/${userId}/queue/messages`,
        (message) => {
          const receivedMessage = JSON.parse(message.body);
          console.log('📨 Message reçu:', receivedMessage);
          callback(receivedMessage);
        }
      );
      return subscription;
    }
    return null;
  };

  // Nettoyage lors du démontage du composant
  useEffect(() => {
    return () => {
      if (clientRef.current) {
        clientRef.current.deactivate();
      }
    };
  }, []);

  const value = {
    stompClient,
    connected,
    currentUser,
    connect,
    disconnect,
    sendMessage,
    subscribe
  };

  return (
    <WebSocketContext.Provider value={value}>
      {children}
    </WebSocketContext.Provider>
  );
};