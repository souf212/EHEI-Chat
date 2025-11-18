package com.ehei.chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket pour le chat en temps réel
 * 
 * WebSocket permet une communication bidirectionnelle entre le client et le serveur
 * 
 * STOMP (Simple Text Oriented Messaging Protocol) est un protocole de messagerie
 * qui fonctionne au-dessus de WebSocket pour structurer les messages
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    
    /**
     * Configuration du broker de messages
     * 
     * Le broker permet de router les messages vers les bonnes destinations
     * 
     * - /topic : Pour les messages publics (ex: groupes)
     * - /user : Pour les messages privés (ex: chat individuel)
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Activer un broker simple en mémoire
        // En production, utiliser RabbitMQ ou Redis pour la scalabilité
        config.enableSimpleBroker("/topic", "/user");
        
        // Préfixe pour les messages envoyés depuis le client vers le serveur
        config.setApplicationDestinationPrefixes("/app");
    }
    
    /**
     * Enregistrer l'endpoint WebSocket
     * 
     * Les clients se connecteront à : ws://localhost:8080/ws/chat
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint pour la connexion WebSocket
        registry.addEndpoint("/ws/chat")
            .setAllowedOriginPatterns("*") // En production, spécifier les origines exactes
            .withSockJS(); // SockJS permet un fallback si WebSocket n'est pas supporté
    }
}

