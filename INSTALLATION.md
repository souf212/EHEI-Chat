# Guide d'Installation

## 📋 Prérequis

- **Docker** et **Docker Compose** installés
- Compte **Meta Business** avec accès à WhatsApp Cloud API (pour la vérification)

## 🚀 Installation Rapide

### 1. Cloner ou Télécharger le Projet

```bash
cd CHAT
```

### 2. Configuration WhatsApp (Optionnel pour le développement)

Si vous avez un compte Meta Business avec WhatsApp Cloud API :

1. Créer un fichier `.env` à la racine :
```bash
cp .env.example .env
```

2. Éditer `.env` et remplir vos clés API :
```env
WHATSAPP_API_TOKEN=votre_token
WHATSAPP_API_PHONE_NUMBER_ID=votre_phone_id
```

**Note** : En mode développement, l'application simule l'envoi de codes WhatsApp si les clés ne sont pas configurées.

### 3. Lancer l'Application

```bash
# Démarrer tous les services
docker-compose up --build

# Ou en arrière-plan
docker-compose up -d --build
```

### 4. Accéder à l'Application

- **Frontend** : http://localhost:3000
- **Backend API** : http://localhost:8080
- **MongoDB** : localhost:27017

## 🛠️ Développement Local (Sans Docker)

### Backend

```bash
cd backend

# Installer Maven (si pas déjà installé)
# Windows : https://maven.apache.org/download.cgi
# Linux/Mac : sudo apt install maven

# Compiler et lancer
mvn spring-boot:run
```

### Frontend

```bash
cd frontend

# Installer les dépendances
npm install

# Lancer en mode développement
npm run dev
```

**Note** : Assurez-vous que MongoDB est en cours d'exécution (via Docker ou installation locale).

## 📝 Vérification

1. Ouvrir http://localhost:3000
2. S'inscrire avec un numéro marocain (ex: +212612345678)
3. En mode développement, le code de vérification sera affiché dans les logs du backend
4. Vérifier avec ce code
5. Accéder au chat

## 🔧 Dépannage

### Port déjà utilisé

Si le port 8080 ou 3000 est déjà utilisé :

```yaml
# Modifier dans docker-compose.yml
backend:
  ports:
    - "8081:8080"  # Changer 8080 en 8081
```

### Erreur de connexion MongoDB

Vérifier que le service MongoDB est démarré :
```bash
docker-compose ps
```

### Rebuild complet

```bash
docker-compose down -v
docker-compose up --build
```

## 📚 Documentation

Consultez les fichiers dans le dossier `docs/` pour plus de détails :
- `PARTIE_2_BACKEND.md` : Backend Spring Boot
- `PARTIE_3_FRONTEND.md` : Frontend React
- `PARTIE_5_DOCKER.md` : Configuration Docker

