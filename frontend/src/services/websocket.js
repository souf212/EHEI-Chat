import SockJS from 'sockjs-client'
import { Client } from 'stompjs'

/**
 * Service WebSocket pour la communication en temps réel
 * 
 * Utilise SockJS et STOMP pour communiquer avec le backend Spring Boot
 * 
 * SockJS : Fournit un fallback si WebSocket n'est pas supporté
 * STOMP : Protocole de messagerie pour structurer les messages
 */

class WebSocketService {
  constructor() {
    this.stompClient = null
    this.connected = false
    this.subscriptions = new Map() // Stocker les abonnements
  }

  /**
   * Se connecter au serveur WebSocket
   * @param {string} userId - ID de l'utilisateur connecté
   * @param {Function} onMessage - Callback appelé lors de la réception d'un message
   * @param {Function} onError - Callback appelé en cas d'erreur
   */
  connect(userId, onMessage, onError) {
    if (this.connected) {
      console.log('Déjà connecté au WebSocket')
      return
    }

    // Créer la connexion SockJS
    const socket = new SockJS('http://localhost:8080/ws/chat')
    
    // Créer le client STOMP
    this.stompClient = Client.over(socket)
    
    // Désactiver le logging (optionnel)
    this.stompClient.debug = () => {}

    // Callback de connexion
    this.stompClient.connect({}, () => {
      console.log('✅ Connecté au WebSocket')
      this.connected = true

      // Notifier le serveur de la connexion
      this.stompClient.send('/app/chat.connect', {}, JSON.stringify({ userId }))

      // S'abonner aux messages privés de l'utilisateur
      const subscription = this.stompClient.subscribe(
        `/user/${userId}/queue/messages`,
        (message) => {
          const data = JSON.parse(message.body)
          if (onMessage) {
            onMessage(data)
          }
        }
      )

      // Stocker l'abonnement
      this.subscriptions.set(`/user/${userId}/queue/messages`, subscription)

      // S'abonner aux notifications
      const notificationSubscription = this.stompClient.subscribe(
        `/user/${userId}/notifications`,
        (notification) => {
          const data = JSON.parse(notification.body)
          console.log('🔔 Notification reçue:', data)
          if (onMessage) {
            onMessage({ type: 'NOTIFICATION', ...data })
          }
        }
      )

      this.subscriptions.set(`/user/${userId}/notifications`, notificationSubscription)
    }, (error) => {
      console.error('❌ Erreur de connexion WebSocket:', error)
      this.connected = false
      if (onError) {
        onError(error)
      }
    })
  }

  /**
   * Envoyer un message individuel
   * @param {string} senderId - ID de l'expéditeur
   * @param {string} receiverId - ID du destinataire
   * @param {string} content - Contenu du message
   */
  sendIndividualMessage(senderId, receiverId, content) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket non connecté')
      return
    }

    const message = {
      type: 'INDIVIDUAL',
      senderId,
      receiverId,
      content,
    }

    this.stompClient.send('/app/chat.send', {}, JSON.stringify(message))
  }

  /**
   * Envoyer un message de groupe
   * @param {string} senderId - ID de l'expéditeur
   * @param {string} groupId - ID du groupe
   * @param {string} content - Contenu du message
   */
  sendGroupMessage(senderId, groupId, content) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket non connecté')
      return
    }

    const message = {
      type: 'GROUP',
      senderId,
      groupId,
      content,
    }

    this.stompClient.send('/app/chat.send', {}, JSON.stringify(message))
  }

  /**
   * S'abonner aux messages d'un groupe
   * @param {string} groupId - ID du groupe
   * @param {Function} onMessage - Callback pour les messages du groupe
   */
  subscribeToGroup(groupId, onMessage) {
    if (!this.connected || !this.stompClient) {
      console.error('WebSocket non connecté')
      return
    }

    const topic = `/topic/group/${groupId}`
    const subscription = this.stompClient.subscribe(topic, (message) => {
      const data = JSON.parse(message.body)
      if (onMessage) {
        onMessage(data)
      }
    })

    this.subscriptions.set(topic, subscription)
  }

  /**
   * Se déconnecter
   */
  disconnect() {
    if (this.stompClient) {
      // Se désabonner de tous les topics
      this.subscriptions.forEach((subscription) => {
        subscription.unsubscribe()
      })
      this.subscriptions.clear()

      // Déconnecter
      this.stompClient.disconnect()
      this.stompClient = null
      this.connected = false
      console.log('Déconnecté du WebSocket')
    }
  }
}

// Exporter une instance unique (singleton)
export default new WebSocketService()

