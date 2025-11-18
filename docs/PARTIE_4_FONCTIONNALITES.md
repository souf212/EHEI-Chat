# PARTIE 4 — Fonctionnalités Demandées

## 📋 Vue d'ensemble

Cette partie détaille toutes les fonctionnalités implémentées dans la plateforme de communication EHEI.

---

## 1. Chat Individuel

### 1.1 Description

Permet à deux utilisateurs de communiquer en privé.

### 1.2 Fonctionnement

**Backend** :
1. Utilisateur A envoie un message à Utilisateur B
2. Le message est sauvegardé dans MongoDB
3. Le message est envoyé via WebSocket à Utilisateur B
4. Utilisateur B reçoit le message en temps réel

**Frontend** :
1. Sélectionner un contact dans la liste
2. Afficher l'historique des messages
3. Saisir et envoyer un nouveau message
4. Recevoir les nouveaux messages automatiquement

### 1.3 Code Backend

```java
// Service
public Message sendIndividualMessage(String senderId, String receiverId, String content) {
    Message message = new Message(senderId, receiverId, content);
    return messageRepository.save(message);
}

// WebSocket Controller
@MessageMapping("/chat.send")
public void sendMessage(@Payload Map<String, Object> messageData) {
    String type = (String) messageData.get("type");
    if ("INDIVIDUAL".equals(type)) {
        String senderId = (String) messageData.get("senderId");
        String receiverId = (String) messageData.get("receiverId");
        String content = (String) messageData.get("content");
        
        Message saved = chatService.sendIndividualMessage(senderId, receiverId, content);
        
        // Envoyer au destinataire
        messagingTemplate.convertAndSend(
            "/user/" + receiverId + "/queue/messages",
            saved
        );
    }
}
```

### 1.4 Code Frontend

```jsx
// Envoyer un message
const handleSendMessage = (content) => {
  webSocketService.sendIndividualMessage(
    user.id,
    selectedChat.id,
    content
  )
}

// Recevoir un message
useEffect(() => {
  webSocketService.connect(user.id, (message) => {
    if (message.type === 'INDIVIDUAL') {
      setMessages(prev => [...prev, message])
    }
  })
}, [user])
```

---

## 2. Chat de Groupe

### 2.1 Description

Permet à plusieurs utilisateurs de communiquer dans un groupe.

### 2.2 Fonctionnement

**Backend** :
1. Un utilisateur crée un groupe
2. D'autres utilisateurs sont ajoutés au groupe
3. Les messages sont envoyés à tous les membres
4. Utilisation du topic `/topic/group/{groupId}`

**Frontend** :
1. Afficher les groupes dans la liste
2. Sélectionner un groupe
3. Voir tous les messages du groupe
4. Envoyer un message visible par tous

### 2.3 Code Backend

```java
// Modèle Group
@Document(collection = "groups")
public class Group {
    private String id;
    private String name;
    private String adminId;
    private List<String> memberIds;
}

// Envoyer un message de groupe
public Message sendGroupMessage(String senderId, String groupId, String content) {
    Message message = new Message(senderId, groupId, content, MessageType.GROUP);
    return messageRepository.save(message);
}

// WebSocket : Envoyer à tous les membres
messagingTemplate.convertAndSend("/topic/group/" + groupId, message);
```

### 2.4 Code Frontend

```jsx
// S'abonner aux messages du groupe
useEffect(() => {
  if (selectedChat?.type === 'GROUP') {
    webSocketService.subscribeToGroup(selectedChat.id, (message) => {
      setMessages(prev => [...prev, message])
    })
  }
}, [selectedChat])

// Envoyer un message de groupe
const handleSendMessage = (content) => {
  webSocketService.sendGroupMessage(user.id, selectedChat.id, content)
}
```

---

## 3. WebSocket Temps Réel

### 3.1 Description

Communication bidirectionnelle en temps réel entre le client et le serveur.

### 3.2 Architecture

```
Client React                    Backend Spring Boot
     │                                │
     │──[Connexion]─────────────────►│
     │                                │
     │◄──[Confirmé]───────────────────│
     │                                │
     │──[Message]───────────────────►│
     │                                │──[Sauvegarder]──► MongoDB
     │                                │
     │◄──[Message]───────────────────│
     │                                │
```

### 3.3 Configuration

**Backend** :
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig {
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws/chat").withSockJS();
    }
}
```

**Frontend** :
```javascript
// Connexion
const socket = new SockJS('http://localhost:8080/ws/chat')
const stompClient = Client.over(socket)

stompClient.connect({}, () => {
  // S'abonner
  stompClient.subscribe('/user/' + userId + '/queue/messages', callback)
  
  // Envoyer
  stompClient.send('/app/chat.send', {}, JSON.stringify(message))
})
```

### 3.4 Avantages

- **Temps réel** : Messages instantanés
- **Efficace** : Une seule connexion persistante
- **Bidirectionnel** : Client et serveur peuvent envoyer

---

## 4. Notifications Internes

### 4.1 Description

Système de notifications pour informer les utilisateurs d'événements.

### 4.2 Types de Notifications

- **NEW_MESSAGE** : Nouveau message reçu
- **NEW_GROUP** : Nouveau groupe créé
- **USER_ADDED** : Utilisateur ajouté à un groupe
- **USER_REMOVED** : Utilisateur retiré d'un groupe

### 4.3 Code Backend

```java
@Service
public class NotificationService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    public void notifyUser(String userId, String event, Object data) {
        String destination = "/user/" + userId + "/notifications";
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", event);
        notification.put("data", data);
        notification.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend(destination, notification);
    }
    
    // Exemple : Notifier un nouveau groupe
    public void notifyNewGroup(String userId, Group group) {
        notifyUser(userId, "NEW_GROUP", group);
    }
}
```

### 4.4 Code Frontend

```jsx
// S'abonner aux notifications
useEffect(() => {
  webSocketService.connect(user.id, (message) => {
    if (message.type === 'NOTIFICATION') {
      handleNotification(message)
    }
  })
}, [user])

// Gérer les notifications
const handleNotification = (notification) => {
  switch (notification.event) {
    case 'NEW_GROUP':
      // Afficher une notification toast
      showToast('Nouveau groupe créé : ' + notification.data.name)
      break
    case 'NEW_MESSAGE':
      // Mettre à jour la liste des conversations
      updateChatList(notification.data)
      break
  }
}
```

---

## 5. Historique des Messages

### 5.1 Description

Conservation et affichage de l'historique des conversations.

### 5.2 Stockage

**MongoDB** :
```javascript
// Collection: messages
{
  _id: ObjectId("..."),
  senderId: "user1",
  receiverId: "user2",
  content: "Bonjour !",
  timestamp: ISODate("2024-01-15T10:30:00Z"),
  type: "INDIVIDUAL"
}
```

### 5.3 Récupération

**Backend** :
```java
// Repository
List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
    String senderId1, String receiverId1,
    String senderId2, String receiverId2
);

// Service
public List<Message> getChatHistory(String userId1, String userId2) {
    return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
        userId1, userId2, userId2, userId1
    );
}
```

**Frontend** :
```jsx
// Charger l'historique
const loadChatHistory = async (chatId) => {
  const response = await chatService.getChatHistory(user.id, chatId)
  if (response.success) {
    setMessages(response.messages)
  }
}

// Charger au changement de chat
useEffect(() => {
  if (selectedChat) {
    loadChatHistory(selectedChat.id)
  }
}, [selectedChat])
```

### 5.4 Affichage

- Messages triés par date (plus ancien en premier)
- Scroll automatique vers le bas
- Indicateur de chargement

---

## 6. Structure Évolutive (Scalable)

### 6.1 Architecture Modulaire

**Backend** :
```
com.ehei.chat/
├── model/          # Modèles de données
├── repository/     # Accès aux données
├── service/        # Logique métier
├── controller/     # Endpoints API
└── config/         # Configurations
```

**Frontend** :
```
src/
├── pages/          # Pages de l'application
├── components/     # Composants réutilisables
├── services/       # Services API
└── utils/          # Utilitaires
```

### 6.2 Extensibilité

**Facilement ajoutable** :
- Envoi de fichiers/images
- Messages vocaux
- Statuts en ligne/hors ligne
- Réactions aux messages
- Réponses à un message
- Recherche dans les messages

**Exemple : Ajouter l'envoi d'images**

```java
// Modèle Message
private String imageUrl;  // Nouveau champ

// Service
public Message sendImageMessage(String senderId, String receiverId, String imageUrl) {
    Message message = new Message();
    message.setSenderId(senderId);
    message.setReceiverId(receiverId);
    message.setImageUrl(imageUrl);
    message.setType(MessageType.IMAGE);
    return messageRepository.save(message);
}
```

### 6.3 Performance

**Optimisations possibles** :
- Pagination des messages (charger par lots)
- Cache Redis pour les sessions
- CDN pour les fichiers statiques
- Load balancing pour plusieurs instances

**Exemple : Pagination**

```java
// Repository
Page<Message> findBySenderIdAndReceiverIdOrderByTimestampDesc(
    String senderId, String receiverId, Pageable pageable
);

// Service
public Page<Message> getChatHistory(String userId1, String userId2, int page, int size) {
    Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
    return messageRepository.findBySenderIdAndReceiverIdOrderByTimestampDesc(
        userId1, userId2, pageable
    );
}
```

---

## 📝 Résumé

| Fonctionnalité | Backend | Frontend | Temps réel |
|----------------|---------|----------|------------|
| Chat individuel | ✅ | ✅ | ✅ |
| Chat de groupe | ✅ | ✅ | ✅ |
| WebSocket | ✅ | ✅ | ✅ |
| Notifications | ✅ | ✅ | ✅ |
| Historique | ✅ | ✅ | ❌ |
| Scalable | ✅ | ✅ | ✅ |

---

## 🎯 Points Clés

1. **Chat individuel** : Messages privés entre deux utilisateurs
2. **Chat de groupe** : Messages partagés dans un groupe
3. **WebSocket** : Communication temps réel bidirectionnelle
4. **Notifications** : Alertes pour les événements importants
5. **Historique** : Conservation de tous les messages
6. **Scalable** : Architecture modulaire et extensible

---

## 🔗 Prochaines Étapes

Consultez :
- **PARTIE 5** : Configuration Docker
- **PARTIE 6** : Guide d'apprentissage approfondi

