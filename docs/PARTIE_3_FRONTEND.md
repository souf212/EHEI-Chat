# PARTIE 3 — Front-End React

## 📚 Introduction

Cette partie explique comment créer le frontend React pour notre plateforme de chat. Nous allons construire une interface utilisateur moderne et réactive qui communique avec le backend via HTTP et WebSocket.

---

## 1. Création du Projet React avec Vite

### 1.1 Pourquoi Vite ?

- **Rapide** : Build ultra-rapide grâce à ES modules natifs
- **Moderne** : Support natif des dernières fonctionnalités JavaScript
- **Simple** : Configuration minimale

### 1.2 Structure du Projet

```
frontend/
├── package.json           # Dépendances npm
├── vite.config.js         # Configuration Vite
├── index.html             # Point d'entrée HTML
├── Dockerfile             # Image Docker
└── src/
    ├── main.jsx           # Point d'entrée React
    ├── App.jsx            # Composant principal
    ├── index.css          # Styles globaux
    ├── pages/             # Pages de l'application
    │   ├── Register.jsx
    │   ├── Verify.jsx
    │   ├── Login.jsx
    │   └── Chat.jsx
    ├── components/        # Composants réutilisables
    │   ├── ChatList.jsx
    │   ├── ChatWindow.jsx
    │   └── MessageBubble.jsx
    ├── services/          # Services API
    │   ├── api.js
    │   └── websocket.js
    └── utils/             # Utilitaires
        └── phoneValidator.js
```

---

## 2. Écrans de l'Application

### 2.1 Page d'Inscription (`Register.jsx`)

**Fonctionnalités** :
- Saisie du numéro de téléphone marocain
- Validation du format
- Envoi du code via WhatsApp

**Code simplifié** :
```jsx
function Register() {
  const [phoneNumber, setPhoneNumber] = useState('')
  const [error, setError] = useState('')
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    
    // Valider le numéro
    if (!isValidMoroccanPhone(phoneNumber)) {
      setError('Numéro invalide')
      return
    }
    
    // Appeler l'API
    const response = await authService.register(phoneNumber)
    
    if (response.success) {
      // Rediriger vers la vérification
      navigate('/verify')
    }
  }
  
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="tel"
        value={phoneNumber}
        onChange={(e) => setPhoneNumber(e.target.value)}
        placeholder="+212612345678"
      />
      {error && <div className="error">{error}</div>}
      <button type="submit">Envoyer le code</button>
    </form>
  )
}
```

**Concepts React** :
- **useState** : Gérer l'état local (phoneNumber, error)
- **useNavigate** : Navigation entre les pages
- **async/await** : Appels API asynchrones

### 2.2 Page de Vérification (`Verify.jsx`)

**Fonctionnalités** :
- Saisie du code à 6 chiffres
- Vérification du code
- Redirection vers le chat après vérification

**Code simplifié** :
```jsx
function Verify() {
  const [code, setCode] = useState('')
  const phoneNumber = localStorage.getItem('pendingPhoneNumber')
  
  const handleSubmit = async (e) => {
    e.preventDefault()
    
    // Vérifier le code
    const response = await authService.verify(phoneNumber, code)
    
    if (response.success) {
      // Sauvegarder l'utilisateur et rediriger
      onVerified(response.user)
      navigate('/chat')
    }
  }
  
  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        value={code}
        onChange={(e) => {
          // Ne permettre que 6 chiffres
          const value = e.target.value.replace(/\D/g, '').slice(0, 6)
          setCode(value)
        }}
        maxLength={6}
      />
      <button type="submit">Vérifier</button>
    </form>
  )
}
```

### 2.3 Page de Chat (`Chat.jsx`)

**Fonctionnalités** :
- Liste des conversations (gauche)
- Fenêtre de chat (droite)
- Envoi/réception de messages en temps réel

**Structure** :
```jsx
function Chat({ user }) {
  const [selectedChat, setSelectedChat] = useState(null)
  const [messages, setMessages] = useState([])
  
  // Connexion WebSocket au chargement
  useEffect(() => {
    webSocketService.connect(user.id, (message) => {
      // Ajouter le message à la liste
      setMessages(prev => [...prev, message])
    })
    
    // Nettoyage à la déconnexion
    return () => webSocketService.disconnect()
  }, [user])
  
  return (
    <div className="chat-container">
      <ChatList onSelectChat={setSelectedChat} />
      <ChatWindow chat={selectedChat} messages={messages} />
    </div>
  )
}
```

---

## 3. Utilisation de WebSocket avec React

### 3.1 Service WebSocket (`websocket.js`)

**Technologies** :
- **SockJS** : Client WebSocket avec fallback
- **STOMP.js** : Protocole de messagerie

**Code simplifié** :
```javascript
import SockJS from 'sockjs-client'
import { Client } from 'stompjs'

class WebSocketService {
  connect(userId, onMessage) {
    // Créer la connexion
    const socket = new SockJS('http://localhost:8080/ws/chat')
    this.stompClient = Client.over(socket)
    
    // Se connecter
    this.stompClient.connect({}, () => {
      // S'abonner aux messages privés
      this.stompClient.subscribe(
        `/user/${userId}/queue/messages`,
        (message) => {
          const data = JSON.parse(message.body)
          onMessage(data)
        }
      )
    })
  }
  
  sendIndividualMessage(senderId, receiverId, content) {
    const message = {
      type: 'INDIVIDUAL',
      senderId,
      receiverId,
      content
    }
    this.stompClient.send('/app/chat.send', {}, JSON.stringify(message))
  }
}
```

**Explications** :
- **SockJS** : Crée une connexion WebSocket (ou fallback HTTP)
- **STOMP** : Structure les messages (destination, body)
- **subscribe** : S'abonner à un topic pour recevoir des messages
- **send** : Envoyer un message au serveur

### 3.2 Utilisation dans un Composant

```jsx
useEffect(() => {
  // Se connecter au chargement
  webSocketService.connect(user.id, (message) => {
    setMessages(prev => [...prev, message])
  })
  
  // Se déconnecter au démontage
  return () => webSocketService.disconnect()
}, [user])
```

---

## 4. Appels API avec Axios

### 4.1 Service API (`api.js`)

```javascript
import axios from 'axios'

const API_BASE_URL = 'http://localhost:8080/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const authService = {
  register: async (phoneNumber) => {
    const response = await api.post('/auth/register', { phoneNumber })
    return response.data
  },
  
  verify: async (phoneNumber, code) => {
    const response = await api.post('/auth/verify', { phoneNumber, code })
    return response.data
  },
  
  login: async (phoneNumber) => {
    const response = await api.post('/auth/login', { phoneNumber })
    return response.data
  }
}
```

**Explications** :
- **axios.create** : Créer une instance avec configuration par défaut
- **api.post** : Faire une requête POST
- **async/await** : Gérer les promesses de manière synchrone

### 4.2 Utilisation dans un Composant

```jsx
const handleSubmit = async (e) => {
  e.preventDefault()
  
  try {
    const response = await authService.register(phoneNumber)
    if (response.success) {
      navigate('/verify')
    }
  } catch (error) {
    setError(error.response?.data?.message || 'Erreur')
  }
}
```

---

## 5. Interface Style WhatsApp

### 5.1 Layout 2 Colonnes

```css
.chat-container {
  display: flex;
  height: 100vh;
}

.chat-sidebar {
  width: 350px;
  background: white;
  border-right: 1px solid #e0e0e0;
}

.chat-main {
  flex: 1;
  background: #f0f2f5;
}
```

**Structure** :
- **Gauche** : Liste des conversations (350px)
- **Droite** : Fenêtre de chat (flexible)

### 5.2 Composants Principaux

#### ChatList
- Affiche la liste des conversations
- Recherche de conversations
- Badge de messages non lus

#### ChatWindow
- En-tête avec nom du contact
- Zone de messages (scrollable)
- Zone de saisie en bas

#### MessageBubble
- Bulle verte pour nos messages
- Bulle blanche pour les autres
- Timestamp

---

## 6. Gestion de l'État avec useState/useEffect

### 6.1 useState

**Déclaration** :
```jsx
const [phoneNumber, setPhoneNumber] = useState('')
const [messages, setMessages] = useState([])
const [loading, setLoading] = useState(false)
```

**Utilisation** :
```jsx
// Lire
console.log(phoneNumber)

// Modifier
setPhoneNumber('+212612345678')

// Modifier un tableau
setMessages(prev => [...prev, newMessage])
```

**Explication** :
- **useState** retourne `[valeur, setter]`
- Le setter déclenche un re-render
- Pour les tableaux/objets, utiliser le spread operator

### 6.2 useEffect

**Exemple 1 : Au chargement** :
```jsx
useEffect(() => {
  // Code exécuté au montage du composant
  loadData()
}, []) // Tableau vide = exécuté une seule fois
```

**Exemple 2 : Quand une dépendance change** :
```jsx
useEffect(() => {
  // Recharger quand user change
  if (user) {
    loadUserData(user.id)
  }
}, [user]) // Exécuté quand user change
```

**Exemple 3 : Nettoyage** :
```jsx
useEffect(() => {
  const subscription = webSocketService.connect(...)
  
  // Fonction de nettoyage
  return () => {
    subscription.unsubscribe()
  }
}, [])
```

---

## 📝 Résumé des Concepts React

### Composant
- **Fonction** : Retourne du JSX (HTML + JavaScript)
- **Props** : Données passées depuis le parent
- **État** : Données locales au composant

### Hooks
- **useState** : Gérer l'état local
- **useEffect** : Effets de bord (API, WebSocket, etc.)
- **useNavigate** : Navigation entre pages

### JSX
- Syntaxe qui ressemble à HTML
- Permet d'utiliser JavaScript dans le HTML
- Exemple : `<div>{phoneNumber}</div>`

### Communication
- **HTTP** : Axios pour les requêtes API
- **WebSocket** : SockJS + STOMP pour le temps réel

---

## 🎯 Points Clés à Retenir

1. **React** = Composants réutilisables
2. **useState** = État local du composant
3. **useEffect** = Effets de bord (API, WebSocket)
4. **Axios** = Requêtes HTTP
5. **WebSocket** = Communication temps réel
6. **JSX** = HTML + JavaScript

---

## 🔗 Prochaines Étapes

Consultez :
- **PARTIE 2** : Backend Spring Boot
- **PARTIE 4** : Fonctionnalités détaillées
- **PARTIE 6** : Guide d'apprentissage approfondi

