# PARTIE 2 — Back-end Spring Boot

## 📚 Introduction

Cette partie explique comment créer le backend Spring Boot pour notre plateforme de chat. Nous allons construire une API REST qui gère l'authentification, les messages et la communication en temps réel via WebSocket.

---

## 1. Démarrer un Projet Spring Boot Minimal

### 1.1 Structure du Projet

```
backend/
├── pom.xml                    # Configuration Maven
├── Dockerfile                 # Image Docker
└── src/
    └── main/
        ├── java/
        │   └── com/ehei/chat/
        │       ├── ChatApplication.java
        │       ├── model/          # Modèles de données
        │       ├── repository/     # Accès aux données
        │       ├── service/        # Logique métier
        │       ├── controller/     # Endpoints API
        │       └── config/         # Configurations
        └── resources/
            └── application.properties
```

### 1.2 Dépendances Principales

Dans `pom.xml`, nous avons besoin de :

- **Spring Web** : Pour créer des API REST
- **Spring Data MongoDB** : Pour interagir avec MongoDB
- **Spring WebSocket** : Pour le chat en temps réel
- **Spring Security** : Pour sécuriser l'application
- **Lombok** : Pour réduire le code boilerplate

---

## 2. Modèle `User`

### 2.1 Structure du Modèle

```java
@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;                    // ID unique MongoDB
    private String phoneNumber;            // +212612345678
    private boolean verified;              // Vérifié via WhatsApp ?
    private String verificationCode;      // Code temporaire
    private LocalDateTime codeExpiration;  // Expiration du code
    private LocalDateTime createdAt;      // Date de création
}
```

### 2.2 Explications

- **@Document** : Indique que cette classe est un document MongoDB
- **@Id** : Champ ID unique généré par MongoDB
- **@Data** : Lombok génère automatiquement getters, setters, etc.

---

## 3. Validation du Numéro Marocain

### 3.1 Pattern de Validation

```java
// Format attendu : +212XXXXXXXXX
// Exemples valides :
// - +212612345678
// - +212712345678

Pattern MOROCCAN_PHONE_PATTERN = Pattern.compile("^\\+212[67]\\d{8}$");
```

### 3.2 Fonction de Validation

```java
public static boolean isValid(String phoneNumber) {
    if (phoneNumber == null) return false;
    return MOROCCAN_PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
}
```

**Explication** :
- `^\\+212` : Commence par +212
- `[67]` : Suivi de 6 ou 7
- `\\d{8}` : Suivi de 8 chiffres
- `$` : Fin de la chaîne

---

## 4. Génération d'un Code 6 Chiffres

### 4.1 Code Simple

```java
public static String generateVerificationCode() {
    Random random = new Random();
    // Génère un nombre entre 100000 et 999999
    int code = 100000 + random.nextInt(900000);
    return String.valueOf(code);
}
```

**Exemple de codes générés** : `123456`, `789012`, `456789`

---

## 5. Envoi du Code via WhatsApp

### 5.1 Configuration WhatsApp Cloud API

Pour utiliser l'API Meta WhatsApp, vous devez :

1. Créer un compte Meta Business
2. Obtenir un **Access Token**
3. Obtenir un **Phone Number ID**

### 5.2 Service WhatsApp

```java
@Service
public class WhatsAppService {
    
    @Value("${whatsapp.api.token}")
    private String apiToken;
    
    public boolean sendVerificationCode(String phoneNumber, String code) {
        // Construire l'URL de l'API
        String url = "https://graph.facebook.com/v18.0/{phone-number-id}/messages";
        
        // Préparer le message
        Map<String, Object> message = new HashMap<>();
        message.put("messaging_product", "whatsapp");
        message.put("to", phoneNumber);
        message.put("type", "template");
        // ... configuration du template
        
        // Envoyer via WebClient (Spring WebFlux)
        webClient.post()
            .uri(url)
            .header("Authorization", "Bearer " + apiToken)
            .bodyValue(message)
            .retrieve()
            .bodyToMono(String.class)
            .block();
        
        return true;
    }
}
```

**Note** : En mode développement, on peut simuler l'envoi pour tester sans API réelle.

---

## 6. Endpoints d'Authentification

### 6.1 POST `/api/auth/register`

**Fonction** : Inscription d'un nouvel utilisateur

**Requête** :
```json
{
  "phoneNumber": "+212612345678"
}
```

**Réponse** :
```json
{
  "success": true,
  "message": "Code de vérification envoyé via WhatsApp",
  "userId": "507f1f77bcf86cd799439011"
}
```

**Code du contrôleur** :
```java
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
    String phoneNumber = request.get("phoneNumber");
    User user = authService.register(phoneNumber);
    return ResponseEntity.ok(Map.of("success", true, "userId", user.getId()));
}
```

### 6.2 POST `/api/auth/verify`

**Fonction** : Vérifier le code reçu

**Requête** :
```json
{
  "phoneNumber": "+212612345678",
  "code": "123456"
}
```

**Réponse** :
```json
{
  "success": true,
  "message": "Numéro vérifié avec succès",
  "user": {
    "id": "507f1f77bcf86cd799439011",
    "phoneNumber": "+212612345678",
    "verified": true
  }
}
```

### 6.3 POST `/api/auth/login`

**Fonction** : Connexion d'un utilisateur vérifié

**Requête** :
```json
{
  "phoneNumber": "+212612345678"
}
```

**Réponse** : Similaire à `/verify`

---

## 7. WebSocket Simple

### 7.1 Configuration WebSocket

```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activer un broker simple en mémoire
        config.enableSimpleBroker("/topic", "/user");
        // Préfixe pour les messages du client
        config.setApplicationDestinationPrefixes("/app");
    }
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket
        registry.addEndpoint("/ws/chat")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}
```

**Explications** :
- **Broker** : Route les messages vers les bonnes destinations
- **/topic** : Pour les messages publics (groupes)
- **/user** : Pour les messages privés (chat individuel)
- **SockJS** : Fallback si WebSocket n'est pas supporté

### 7.2 Contrôleur WebSocket

```java
@Controller
public class WebSocketController {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> messageData) {
        String senderId = (String) messageData.get("senderId");
        String receiverId = (String) messageData.get("receiverId");
        String content = (String) messageData.get("content");
        
        // Sauvegarder le message
        Message message = chatService.sendIndividualMessage(
            senderId, receiverId, content
        );
        
        // Envoyer au destinataire
        messagingTemplate.convertAndSend(
            "/user/" + receiverId + "/queue/messages",
            message
        );
    }
}
```

**Flux** :
1. Client envoie à `/app/chat.send`
2. Serveur traite et sauvegarde
3. Serveur envoie au destinataire via `/user/{userId}/queue/messages`

---

## 8. Stockage dans MongoDB

### 8.1 Repository

```java
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    // Trouver les messages entre deux utilisateurs
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
        String senderId1, String receiverId1,
        String senderId2, String receiverId2
    );
}
```

**Explication** : Spring Data génère automatiquement la requête MongoDB.

### 8.2 Service de Chat

```java
@Service
public class ChatService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    public Message sendIndividualMessage(String senderId, String receiverId, String content) {
        Message message = new Message(senderId, receiverId, content);
        return messageRepository.save(message);
    }
    
    public List<Message> getChatHistory(String userId1, String userId2) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            userId1, userId2, userId2, userId1
        );
    }
}
```

---

## 9. Service de Notification

### 9.1 Notifications Internes

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
        messagingTemplate.convertAndSend(destination, notification);
    }
}
```

**Utilisation** :
- Notifier un nouveau groupe créé
- Notifier un nouveau message
- Notifier un utilisateur ajouté à un groupe

---

## 📝 Résumé des Concepts

### Contrôleur (Controller)
- **Rôle** : Reçoit les requêtes HTTP et retourne des réponses
- **Annotation** : `@RestController`, `@RequestMapping`

### Service
- **Rôle** : Contient la logique métier
- **Annotation** : `@Service`

### Repository
- **Rôle** : Accède aux données dans MongoDB
- **Annotation** : `@Repository`
- **Hérite de** : `MongoRepository<T, ID>`

### WebSocket
- **Rôle** : Communication bidirectionnelle en temps réel
- **Protocole** : STOMP sur WebSocket
- **Endpoints** : `/ws/chat` pour la connexion

---

## 🎯 Points Clés à Retenir

1. **Spring Boot** simplifie la configuration
2. **MongoDB** stocke les données en format JSON (documents)
3. **WebSocket** permet le chat en temps réel
4. **Repository** génère automatiquement les requêtes
5. **Service** contient la logique métier
6. **Contrôleur** expose les endpoints API

---

## 🔗 Prochaines Étapes

Consultez :
- **PARTIE 3** : Frontend React
- **PARTIE 4** : Fonctionnalités détaillées
- **PARTIE 6** : Guide d'apprentissage approfondi

