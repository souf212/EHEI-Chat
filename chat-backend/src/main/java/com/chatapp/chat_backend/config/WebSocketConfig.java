package com.chatapp.chat_backend.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuration WebSocket pour la communication temps réel
 * Active STOMP over WebSocket avec SockJS comme fallback
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configure le message broker
     * - /user: pour les messages privés (point-à-point)
     * - /queue: pour les files d'attente de messages
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Active un simple broker en mémoire pour les destinations
        registry.enableSimpleBroker("/user", "/queue");

        // Préfixe pour les messages envoyés depuis le client vers le serveur
        registry.setApplicationDestinationPrefixes("/app");

        // Préfixe pour les messages utilisateur-spécifiques
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * Enregistre les endpoints STOMP avec SockJS activé
     * - Endpoint: /ws
     * - CORS: Autorise toutes les origines (pour le développement)
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // Autorise toutes les origines
                .withSockJS(); // Active SockJS pour la compatibilité navigateur
    }
}
