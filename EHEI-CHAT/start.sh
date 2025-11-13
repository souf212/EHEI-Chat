#!/bin/bash

# Script de démarrage pour EHEI Chat

echo "🚀 Démarrage de EHEI Chat..."

# Vérifier si Docker est installé
if ! command -v docker &> /dev/null; then
    echo "❌ Docker n'est pas installé. Veuillez installer Docker pour continuer."
    exit 1
fi

# Vérifier si Docker Compose est installé
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose n'est pas installé. Veuillez installer Docker Compose pour continuer."
    exit 1
fi

# Vérifier si le fichier .env existe
if [ ! -f .env ]; then
    echo "⚠️  Le fichier .env n'existe pas. Création d'un fichier .env.example..."
    cat > .env << EOF
TWILIO_ACCOUNT_SID=YOUR_SID
TWILIO_AUTH_TOKEN=YOUR_TOKEN
TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
JWT_SECRET=ehei_chat_super_secret_key_change_in_production_2024_minimum_256_bits_required_for_security
EOF
    echo "✅ Fichier .env créé. Veuillez le configurer avec vos vraies clés Twilio."
    exit 1
fi

# Construire et démarrer les services
echo "📦 Construction des images Docker..."
docker-compose build

echo "🚀 Démarrage des services..."
docker-compose up -d

echo "⏳ Attente du démarrage des services..."
sleep 10

# Vérifier si les services sont en cours d'exécution
if docker-compose ps | grep -q "Up"; then
    echo "✅ Services démarrés avec succès!"
    echo ""
    echo "📱 Frontend: http://localhost:3000"
    echo "🔧 Backend: http://localhost:8080"
    echo "💾 MongoDB: localhost:27017"
    echo "🔴 Redis: localhost:6379"
    echo ""
    echo "Pour voir les logs: docker-compose logs -f"
    echo "Pour arrêter les services: docker-compose down"
else
    echo "❌ Erreur lors du démarrage des services. Vérifiez les logs avec: docker-compose logs"
    exit 1
fi
