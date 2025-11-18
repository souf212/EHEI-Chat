# Plateforme de Communication Interne EHEI – Clone WhatsApp Simplifié

## 📋 Vue d'ensemble du Projet

Cette application est une plateforme de communication interne pour l'EHEI d'Oujda, permettant aux utilisateurs de communiquer en temps réel via des chats individuels et de groupe, avec authentification par numéro de téléphone marocain et vérification via WhatsApp.

---

## 🏗️ PARTIE 1 — Architecture du Projet

### 1.1 Architecture Globale

```
┌─────────────────────────────────────────────────────────────┐
│                    ARCHITECTURE GLOBALE                      │
└─────────────────────────────────────────────────────────────┘

┌──────────────┐         ┌──────────────┐         ┌──────────────┐
│   REACT      │ ◄─────► │ SPRING BOOT  │ ◄─────► │   MONGODB    │
│  Frontend    │ HTTP    │   Backend    │         │   Database   │
│              │ WebSocket│              │         │              │
└──────────────┘         └──────────────┘         └──────────────┘
     │                          │
     │                          │
     └──────────────────────────┘
            WhatsApp API
         (Vérification SMS)
```

**Composants principaux :**

1. **Frontend React** : Interface utilisateur moderne et réactive
2. **Backend Spring Boot** : API REST + WebSocket pour le temps réel
3. **MongoDB** : Base de données NoSQL pour stocker utilisateurs et messages
4. **Docker** : Conteneurisation pour un déploiement simplifié

### 1.2 Pourquoi MongoDB ?

**Avantages pour ce projet :**
- **Flexibilité** : Structure de données flexible pour les messages (texte, images, fichiers)
- **Performance** : Excellente performance pour les opérations de lecture/écriture fréquentes
- **Scalabilité** : Facilement extensible pour gérer de nombreux utilisateurs et messages
- **Collections** : Organisation naturelle (users, messages, groups)

**Structure des collections :**
```javascript
// Collection: users
{
  _id: ObjectId,
  phoneNumber: "+212612345678",
  verified: true,
  createdAt: ISODate
}

// Collection: messages
{
  _id: ObjectId,
  senderId: ObjectId,
  receiverId: ObjectId, // ou groupId
  content: "Message texte",
  timestamp: ISODate,
  type: "INDIVIDUAL" | "GROUP"
}
```

### 1.3 Pourquoi WebSockets ?

**Problème avec HTTP classique :**
- HTTP est **unidirectionnel** : le client demande, le serveur répond
- Pour le chat temps réel, il faudrait **polling** (vérifier toutes les X secondes) → inefficace

**Solution WebSocket :**
- **Communication bidirectionnelle** : client et serveur peuvent envoyer des messages à tout moment
- **Temps réel** : messages instantanés sans délai
- **Efficace** : une seule connexion persistante au lieu de multiples requêtes HTTP

**Flux WebSocket :**
```
Client ──[Connexion]──► Serveur
Client ◄──[Message]──── Serveur
Client ──[Message]──► Serveur
Client ◄──[Message]──── Serveur
```

### 1.4 Comment Docker Simplifie le Déploiement ?

**Sans Docker :**
- Installer Java, Node.js, MongoDB manuellement
- Configurer chaque service séparément
- Problèmes de compatibilité entre environnements

**Avec Docker :**
- **Un seul fichier** `docker-compose.yml` lance tout
- **Environnement isolé** : chaque service dans son conteneur
- **Reproductible** : fonctionne de la même manière partout
- **Simple** : `docker-compose up` et c'est tout !

**Architecture Docker :**
```
┌─────────────────────────────────────┐
│      Docker Compose                 │
│  ┌──────────┐  ┌──────────┐        │
│  │ Backend  │  │ Frontend │        │
│  │ Container│  │ Container│        │
│  └──────────┘  └──────────┘        │
│  ┌──────────┐                      │
│  │ MongoDB  │                      │
│  │ Container│                      │
│  └──────────┘                      │
└─────────────────────────────────────┘
```

### 1.5 Organigramme Technique Simple

```
┌─────────────────────────────────────────────────────────────┐
│                        UTILISATEUR                           │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    FRONTEND (React)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Pages      │  │  Composants  │  │  WebSocket   │     │
│  │  - Login     │  │  - Chat      │  │   Client     │     │
│  │  - Register  │  │  - Message   │  │              │     │
│  │  - Chat      │  │  - Contact   │  │              │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTP / WebSocket
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                 BACKEND (Spring Boot)                       │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ Contrôleurs  │  │   Services   │  │  Repository  │     │
│  │  - Auth      │  │  - Auth      │  │  - User      │     │
│  │  - Chat      │  │  - Chat      │  │  - Message   │     │
│  │  - WebSocket │  │  - WhatsApp  │  │  - Group     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    MONGODB                                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   users      │  │   messages   │  │    groups    │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│              WHATSAPP CLOUD API (Meta)                      │
│              (Vérification des codes)                       │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 Démarrage Rapide

### Prérequis
- Docker et Docker Compose installés
- Compte Meta Business avec accès à WhatsApp Cloud API (pour la vérification)

### Installation

1. **Cloner le projet**
```bash
cd CHAT
```

2. **Lancer avec Docker**
```bash
docker-compose up --build
```

3. **Accéder à l'application**
- Frontend : http://localhost:3000
- Backend API : http://localhost:8080

---

## 📚 Documentation Complète

Consultez les fichiers suivants pour plus de détails :

- **PARTIE 2** : `docs/PARTIE_2_BACKEND.md` - Backend Spring Boot
- **PARTIE 3** : `docs/PARTIE_3_FRONTEND.md` - Frontend React
- **PARTIE 4** : `docs/PARTIE_4_FONCTIONNALITES.md` - Fonctionnalités
- **PARTIE 5** : `docs/PARTIE_5_DOCKER.md` - Configuration Docker
- **PARTIE 6** : `docs/PARTIE_6_GUIDE_APPRENTISSAGE.md` - Guide d'Apprentissage

---

## 🛠️ Technologies Utilisées

- **Backend** : Spring Boot 3.x, Spring WebSocket, Spring Data MongoDB
- **Frontend** : React 18, Vite, Socket.IO Client
- **Base de données** : MongoDB
- **Conteneurisation** : Docker, Docker Compose

---

## 📝 Licence

Projet éducatif pour l'EHEI d'Oujda

