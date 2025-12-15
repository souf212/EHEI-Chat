## Chat App – Spring Boot, React, MongoDB

Application de messagerie temps réel avec un backend Java/Spring Boot et un frontend React moderne.

### Stack technique

- **Backend**: Spring Boot, WebSocket (STOMP + SockJS), REST API, MongoDB
- **Frontend**: React (Vite), Context API, STOMP client, Axios
- **Base de données**: MongoDB (collection `messages`)

### Fonctionnalités principales

- Connexion simple par pseudo ou via profils de démo (Alice, Bob, Charlie, Diana)
- Liste de contacts avec statut en ligne / hors ligne
- Conversations privées en temps réel via WebSocket
- Chargement de l’historique des messages depuis MongoDB
- Notifications visuelles pour les nouveaux messages (badge + toast)
- Interface moderne inspirée des apps de chat (bulles, animations, thème bleu/teal)

### Lancer le projet en local

1. **Pré-requis**
   - Java 17+
   - Node.js 18+
   - MongoDB en local (port par défaut `27017`)

2. **Backend**
   ```bash
   cd chat-backend
   ./mvnw spring-boot:run   # ou mvnw.cmd sous Windows
   ```
   Le backend démarre sur `http://localhost:8080`.

3. **Frontend**
   ```bash
   cd chat-frontend
   npm install
   npm run dev
   ```
   Le frontend est accessible sur `http://localhost:3000`.

### Architecture rapide

- **Backend**
  - `WebSocketConfig`: configuration STOMP (`/ws`, `/app`, `/user`, `/queue`)
  - `ChatController`: réception et diffusion des messages temps réel
  - `MessageController`: endpoints REST pour l’historique et la santé de l’API
  - `ChatMessage`, `ChatMessageRepository`, `ChatMessageService`: persistance MongoDB

- **Frontend**
  - `WebSocketContext`: gestion globale de la connexion WebSocket/ STOMP
  - `Chatcontext`: état global du chat (utilisateur sélectionné, messages, notifications)
  - Composants principaux : `Login`, `Chat`, `UserList`, `ChatWindow`, `Notification`

Pour une explication plus détaillée de tous les concepts (flux complet d’un message, WebSocket, REST, MongoDB, Context API), voir le fichier `ARCHITECTURE.md` à la racine du projet.


