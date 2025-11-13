# EHEI Chat - Plateforme de Communication en Temps Réel

Plateforme de chat en temps réel pour l'EHEI (École d'enseignement supérieur) à Oujda, Maroc.

## 🚀 Fonctionnalités

### Authentification
- ✅ Inscription/Connexion avec numéro de téléphone marocain (06XX ou 07XX)
- ✅ Vérification par code à 6 chiffres envoyé via WhatsApp (Twilio)
- ✅ Profil utilisateur : nom, rôle (Étudiant/Professeur/Staff)
- ✅ JWT pour l'authentification

### Chat en Temps Réel
- ✅ Canaux de groupe publics (ex: #général, #projets-2024)
- ✅ Messages directs entre utilisateurs
- ✅ WebSocket (STOMP) pour messages instantanés
- ✅ Historique des messages persistant
- ✅ Indicateurs de messages non lus

### Notifications
- ✅ Notifications contextuelles pour nouveaux messages
- ✅ Notifications d'événements
- ✅ Badge de compteur de notifications non lues

## 🛠️ Stack Technique

### Backend
- Spring Boot 3.2.0 (Java 17)
- MongoDB 7.0 (base de données principale)
- Redis 7.2 (cache et codes de vérification)
- WebSocket (STOMP)
- Twilio WhatsApp Business API
- JWT (jjwt 0.12.5)
- Maven

### Frontend
- React 18 avec Vite
- Tailwind CSS
- Axios (API calls)
- @stomp/stompjs + sockjs-client (WebSocket)
- Lucide React (icônes)
- React Router DOM

### Infrastructure
- Docker & Docker Compose
- MongoDB 7.0
- Redis 7.2
- Nginx (pour le frontend en production)

## 📋 Prérequis

- Java 17 ou supérieur
- Node.js 18 ou supérieur
- Maven 3.8 ou supérieur
- Docker et Docker Compose (optionnel)
- Compte Twilio avec WhatsApp Business API activé

## 🔧 Installation

### Option 1 : Installation avec Docker (Recommandé)

1. **Cloner le projet**
```bash
git clone <repository-url>
cd EHEI-CHAT
```

2. **Configurer les variables d'environnement**

Créer un fichier `.env` à la racine du projet :
```env
TWILIO_ACCOUNT_SID=votre_account_sid
TWILIO_AUTH_TOKEN=votre_auth_token
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
JWT_SECRET=votre_secret_key_256_bits_minimum
```

3. **Démarrer les services**
```bash
docker-compose up -d
```

Les services seront disponibles sur :
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- MongoDB: localhost:27017
- Redis: localhost:6379

### Option 2 : Installation Manuelle

#### Backend

1. **Naviguer vers le dossier backend**
```bash
cd backend
```

2. **Configurer application.properties**
```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/ehei_chat

# Redis
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Twilio
twilio.account.sid=VOTRE_SID
twilio.auth.token=VOTRE_TOKEN
twilio.whatsapp.from=whatsapp:+14155238886

# JWT
jwt.secret=votre_secret_key_256_bits_minimum
jwt.expiration=86400000
```

3. **Compiler et lancer l'application**
```bash
mvn clean package
mvn spring-boot:run
```

Le backend sera disponible sur http://localhost:8080

#### Frontend

1. **Naviguer vers le dossier frontend**
```bash
cd frontend
```

2. **Installer les dépendances**
```bash
npm install
```

3. **Configurer les variables d'environnement**

Créer un fichier `.env` :
```env
VITE_API_URL=http://localhost:8080
VITE_WS_URL=ws://localhost:8080/ws-chat
```

4. **Lancer l'application**
```bash
npm run dev
```

Le frontend sera disponible sur http://localhost:5173

## 📱 Configuration Twilio

1. **Créer un compte Twilio**
   - Aller sur https://www.twilio.com
   - Créer un compte
   - Obtenir votre Account SID et Auth Token

2. **Activer WhatsApp Business API**
   - Aller dans la console Twilio
   - Activer WhatsApp Business API
   - Joindre le sandbox : `join farmer-possible`
   - Ajouter votre numéro de téléphone au sandbox

3. **Configurer le numéro WhatsApp**
   - Utiliser le numéro de sandbox : `+1 415 523 8886`
   - Ou configurer votre propre numéro WhatsApp Business

## 🔐 Sécurité

- Codes de vérification expirent après 5 minutes
- JWT tokens avec expiration 24h
- CORS configuré correctement
- Validation côté client ET serveur
- Clé secrète JWT de 256 bits minimum

## 📡 API Endpoints

### Authentification

- `POST /api/auth/send-code` - Envoyer code WhatsApp
  ```json
  {
    "phoneNumber": "0612345678"
  }
  ```

- `POST /api/auth/verify-code` - Vérifier le code
  ```json
  {
    "phoneNumber": "0612345678",
    "code": "123456"
  }
  ```

- `POST /api/auth/complete-profile` - Créer profil utilisateur
  ```json
  {
    "phoneNumber": "0612345678",
    "name": "Mohamed Alami",
    "role": "STUDENT"
  }
  ```

### Chat

- `GET /api/chat/channels` - Liste des canaux
- `GET /api/chat/channels/{id}/messages` - Messages d'un canal
- `POST /api/chat/channels` - Créer un canal
- `GET /api/users/{id}/channels` - Canaux d'un utilisateur

### Notifications

- `GET /api/chat/notifications/{userId}` - Notifications d'un utilisateur
- `GET /api/chat/notifications/{userId}/unread` - Notifications non lues
- `GET /api/chat/notifications/{userId}/count` - Compte de notifications non lues

### WebSocket

- **CONNECT**: `/ws-chat`
- **SEND**: `/app/message`
- **SUBSCRIBE**: `/topic/messages/{channelId}`

## 🧪 Tests

### Tester l'authentification

```bash
# Envoyer un code
curl -X POST http://localhost:8080/api/auth/send-code \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "0612345678"}'

# Vérifier le code
curl -X POST http://localhost:8080/api/auth/verify-code \
  -H "Content-Type: application/json" \
  -d '{"phoneNumber": "0612345678", "code": "123456"}'
```

### Tester les canaux

```bash
# Obtenir les canaux
curl -X GET http://localhost:8080/api/chat/channels \
  -H "Authorization: Bearer YOUR_TOKEN"

# Créer un canal
curl -X POST http://localhost:8080/api/chat/channels \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{"name": "général", "type": "GROUP", "createdBy": "USER_ID"}'
```

## 🐛 Dépannage

### Problèmes courants

1. **Erreur de connexion MongoDB**
   - Vérifier que MongoDB est lancé : `docker ps` ou `mongod --version`
   - Vérifier l'URI dans `application.properties`

2. **Erreur de connexion Redis**
   - Vérifier que Redis est lancé : `docker ps` ou `redis-cli ping`
   - Vérifier la configuration dans `application.properties`

3. **Erreur Twilio**
   - Vérifier les credentials Twilio
   - Vérifier que le numéro est dans le sandbox
   - Vérifier les logs : `docker logs ehei-backend`

4. **Erreur WebSocket**
   - Vérifier que le backend est accessible
   - Vérifier la configuration CORS
   - Vérifier les logs du navigateur (F12)

## 📝 Structure du Projet

```
ehei-chat/
├── backend/
│   ├── src/main/java/ma/ehei/chat/
│   │   ├── EheiChatApplication.java
│   │   ├── config/
│   │   │   ├── CorsConfig.java
│   │   │   ├── RedisConfig.java
│   │   │   ├── TwilioConfig.java
│   │   │   └── WebSocketConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── ChatController.java
│   │   │   └── UserController.java
│   │   ├── dto/
│   │   │   ├── PhoneRequest.java
│   │   │   ├── VerifyRequest.java
│   │   │   ├── ProfileRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── Message.java
│   │   │   ├── Channel.java
│   │   │   └── Notification.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── MessageRepository.java
│   │   │   └── ChannelRepository.java
│   │   ├── service/
│   │   │   ├── WhatsAppService.java
│   │   │   ├── AuthService.java
│   │   │   ├── JwtService.java
│   │   │   └── ChatService.java
│   │   └── exception/
│   │       ├── CustomExceptions.java
│   │       └── GlobalExceptionHandler.java
│   ├── src/main/resources/
│   │   └── application.properties
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── auth/
│   │   │   │   ├── PhoneAuth.jsx
│   │   │   │   ├── CodeVerification.jsx
│   │   │   │   └── ProfileSetup.jsx
│   │   │   ├── chat/
│   │   │   │   ├── ChatWindow.jsx
│   │   │   │   ├── MessageList.jsx
│   │   │   │   ├── MessageInput.jsx
│   │   │   │   └── ChannelList.jsx
│   │   │   └── layout/
│   │   │       └── Sidebar.jsx
│   │   ├── services/
│   │   │   ├── api.js
│   │   │   ├── authService.js
│   │   │   ├── chatService.js
│   │   │   └── websocketService.js
│   │   ├── context/
│   │   │   └── AuthContext.jsx
│   │   ├── App.jsx
│   │   └── index.css
│   ├── package.json
│   ├── tailwind.config.js
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

## 🚀 Déploiement

### Production

1. **Configurer les variables d'environnement**
   - Utiliser des secrets sécurisés
   - Configurer les URLs de production
   - Configurer les certificats SSL

2. **Build des images Docker**
```bash
docker-compose build
```

3. **Démarrer les services**
```bash
docker-compose up -d
```

4. **Vérifier les logs**
```bash
docker-compose logs -f
```

## 📄 Licence

Ce projet est propriétaire de l'EHEI.

## 👥 Auteur

École d'enseignement supérieur EHEI - Oujda, Maroc

## 📞 Support

Pour toute question ou problème, contactez l'équipe de développement.

---

**Note**: Assurez-vous de configurer correctement Twilio et les variables d'environnement avant de lancer l'application.
