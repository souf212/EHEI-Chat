# PARTIE 5 — Docker

## 📚 Introduction

Docker permet de conteneuriser notre application pour un déploiement simplifié et reproductible. Cette partie explique comment configurer Docker pour notre projet.

---

## 1. Dockerfile pour Spring Boot

### 1.1 Structure Multi-Stage

Un Dockerfile multi-stage permet de :
- Réduire la taille de l'image finale
- Séparer la compilation de l'exécution

### 1.2 Dockerfile Backend

```dockerfile
# Étape 1 : Build de l'application
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copier pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler et créer le JAR
RUN mvn clean package -DskipTests

# Étape 2 : Image finale avec Java
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Exposer le port 8080
EXPOSE 8080

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Explications** :
- **Étape 1** : Utilise Maven pour compiler le projet
- **Étape 2** : Utilise une image Java légère (Alpine)
- **COPY --from=build** : Copie le JAR depuis l'étape de build
- **EXPOSE 8080** : Indique que l'application écoute sur le port 8080

### 1.3 Build de l'Image

```bash
cd backend
docker build -t ehei-backend .
```

---

## 2. Dockerfile pour React

### 2.1 Structure Multi-Stage

```dockerfile
# Étape 1 : Build de l'application
FROM node:18-alpine AS build

WORKDIR /app

# Copier les fichiers de dépendances
COPY package.json package-lock.json* ./

# Installer les dépendances
RUN npm ci

# Copier le code source
COPY . .

# Build de l'application React
RUN npm run build

# Étape 2 : Serveur web pour servir les fichiers statiques
FROM nginx:alpine

# Copier les fichiers buildés
COPY --from=build /app/dist /usr/share/nginx/html

# Copier la configuration Nginx
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Exposer le port 80
EXPOSE 80

# Démarrer Nginx
CMD ["nginx", "-g", "daemon off;"]
```

**Explications** :
- **Étape 1** : Utilise Node.js pour compiler React
- **Étape 2** : Utilise Nginx pour servir les fichiers statiques
- **npm run build** : Crée les fichiers optimisés dans `dist/`
- **Nginx** : Serveur web léger et performant

### 2.2 Configuration Nginx

```nginx
server {
    listen 80;
    server_name localhost;
    root /usr/share/nginx/html;
    index index.html;

    # Gérer les routes React (SPA)
    location / {
        try_files $uri $uri/ /index.html;
    }

    # Cache pour les assets statiques
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

**Explications** :
- **try_files** : Pour les Single Page Applications (SPA)
- **Cache** : Optimise le chargement des assets statiques

### 2.3 Build de l'Image

```bash
cd frontend
docker build -t ehei-frontend .
```

---

## 3. Docker Compose

### 3.1 Fichier docker-compose.yml

```yaml
version: '3.8'

services:
  # Service MongoDB
  mongodb:
    image: mongo:7.0
    container_name: ehei_mongodb
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db
    environment:
      MONGO_INITDB_DATABASE: ehei_chat
    networks:
      - ehei_network
    restart: unless-stopped

  # Service Backend
  backend:
    build:
      context: ./backend
      dockerfile: Dockerfile
    container_name: ehei_backend
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATA_MONGODB_URI=mongodb://mongodb:27017/ehei_chat
    depends_on:
      - mongodb
    networks:
      - ehei_network
    restart: unless-stopped

  # Service Frontend
  frontend:
    build:
      context: ./frontend
      dockerfile: Dockerfile
    container_name: ehei_frontend
    ports:
      - "3000:80"
    depends_on:
      - backend
    networks:
      - ehei_network
    restart: unless-stopped

# Réseau pour la communication entre les services
networks:
  ehei_network:
    driver: bridge

# Volumes pour persister les données
volumes:
  mongodb_data:
```

### 3.2 Explications

**Services** :
- **mongodb** : Base de données MongoDB
- **backend** : Application Spring Boot
- **frontend** : Application React

**Réseau** :
- **ehei_network** : Réseau privé pour la communication entre services
- Les services peuvent communiquer par leur nom (ex: `mongodb`, `backend`)

**Volumes** :
- **mongodb_data** : Persiste les données MongoDB même après arrêt du conteneur

**Ports** :
- **27017** : MongoDB (exposé pour accès externe si besoin)
- **8080** : Backend Spring Boot
- **3000** : Frontend React (mappé depuis le port 80 de Nginx)

**Dépendances** :
- **depends_on** : Assure que MongoDB démarre avant le backend
- **depends_on** : Assure que le backend démarre avant le frontend

---

## 4. Utilisation

### 4.1 Démarrer l'Application

```bash
# Démarrer tous les services
docker-compose up

# Démarrer en arrière-plan
docker-compose up -d

# Rebuild les images
docker-compose up --build
```

### 4.2 Arrêter l'Application

```bash
# Arrêter les services
docker-compose down

# Arrêter et supprimer les volumes (⚠️ supprime les données)
docker-compose down -v
```

### 4.3 Voir les Logs

```bash
# Logs de tous les services
docker-compose logs

# Logs d'un service spécifique
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mongodb

# Suivre les logs en temps réel
docker-compose logs -f
```

### 4.4 Commandes Utiles

```bash
# Voir les conteneurs en cours d'exécution
docker-compose ps

# Redémarrer un service
docker-compose restart backend

# Exécuter une commande dans un conteneur
docker-compose exec backend sh
docker-compose exec mongodb mongo

# Voir l'utilisation des ressources
docker stats
```

---

## 5. Avantages de Docker

### 5.1 Reproductibilité

- **Même environnement** : Fonctionne de la même manière sur toutes les machines
- **Pas de "ça marche sur ma machine"** : L'environnement est identique partout

### 5.2 Simplicité

- **Un seul commande** : `docker-compose up` lance tout
- **Pas d'installation** : Pas besoin d'installer Java, Node.js, MongoDB manuellement

### 5.3 Isolation

- **Conteneurs séparés** : Chaque service est isolé
- **Pas de conflits** : Les dépendances ne se mélangent pas

### 5.4 Scalabilité

- **Facile à étendre** : Ajouter de nouveaux services est simple
- **Orchestration** : Docker Compose gère les dépendances automatiquement

---

## 6. Structure du Projet avec Docker

```
CHAT/
├── docker-compose.yml      # Configuration Docker Compose
├── backend/
│   ├── Dockerfile          # Image Docker backend
│   └── ...
├── frontend/
│   ├── Dockerfile          # Image Docker frontend
│   ├── nginx.conf          # Configuration Nginx
│   └── ...
└── README.md
```

---

## 7. Variables d'Environnement

### 7.1 Fichier .env (Optionnel)

Créer un fichier `.env` à la racine :

```env
WHATSAPP_API_TOKEN=votre_token_ici
WHATSAPP_API_PHONE_NUMBER_ID=votre_phone_id_ici
```

### 7.2 Utilisation dans docker-compose.yml

```yaml
backend:
  environment:
    - WHATSAPP_API_TOKEN=${WHATSAPP_API_TOKEN}
    - WHATSAPP_API_PHONE_NUMBER_ID=${WHATSAPP_API_PHONE_NUMBER_ID}
```

---

## 📝 Résumé

| Service | Image | Port | Description |
|---------|-------|------|-------------|
| MongoDB | mongo:7.0 | 27017 | Base de données |
| Backend | Build local | 8080 | API Spring Boot |
| Frontend | Build local | 3000 | Interface React |

**Commandes principales** :
- `docker-compose up` : Démarrer
- `docker-compose down` : Arrêter
- `docker-compose logs` : Voir les logs
- `docker-compose ps` : Voir les conteneurs

---

## 🎯 Points Clés

1. **Dockerfile** : Définit comment construire une image
2. **Docker Compose** : Orchestre plusieurs conteneurs
3. **Multi-stage** : Réduit la taille des images
4. **Volumes** : Persiste les données
5. **Réseaux** : Permet la communication entre services

---

## 🔗 Prochaines Étapes

Consultez :
- **PARTIE 6** : Guide d'apprentissage approfondi
- **README.md** : Documentation générale

