@echo off
REM Script de démarrage pour EHEI Chat (Windows)

echo 🚀 Démarrage de EHEI Chat...

REM Vérifier si Docker est installé
where docker >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker n'est pas installé. Veuillez installer Docker pour continuer.
    exit /b 1
)

REM Vérifier si Docker Compose est installé
where docker-compose >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ❌ Docker Compose n'est pas installé. Veuillez installer Docker Compose pour continuer.
    exit /b 1
)

REM Vérifier si le fichier .env existe
if not exist .env (
    echo ⚠️  Le fichier .env n'existe pas. Création d'un fichier .env.example...
    (
        echo TWILIO_ACCOUNT_SID=YOUR_SID
        echo TWILIO_AUTH_TOKEN=YOUR_TOKEN
        echo TWILIO_WHATSAPP_FROM=whatsapp:+14155238886
        echo JWT_SECRET=ehei_chat_super_secret_key_change_in_production_2024_minimum_256_bits_required_for_security
    ) > .env
    echo ✅ Fichier .env créé. Veuillez le configurer avec vos vraies clés Twilio.
    exit /b 1
)

REM Construire et démarrer les services
echo 📦 Construction des images Docker...
docker-compose build

echo 🚀 Démarrage des services...
docker-compose up -d

echo ⏳ Attente du démarrage des services...
timeout /t 10 /nobreak >nul

REM Vérifier si les services sont en cours d'exécution
docker-compose ps | findstr "Up" >nul
if %ERRORLEVEL% EQU 0 (
    echo ✅ Services démarrés avec succès!
    echo.
    echo 📱 Frontend: http://localhost:3000
    echo 🔧 Backend: http://localhost:8080
    echo 💾 MongoDB: localhost:27017
    echo 🔴 Redis: localhost:6379
    echo.
    echo Pour voir les logs: docker-compose logs -f
    echo Pour arrêter les services: docker-compose down
) else (
    echo ❌ Erreur lors du démarrage des services. Vérifiez les logs avec: docker-compose logs
    exit /b 1
)
