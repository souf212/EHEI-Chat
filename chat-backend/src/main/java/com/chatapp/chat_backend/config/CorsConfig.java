package com.chatapp.chat_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuration CORS pour autoriser les requêtes cross-origin
 * Nécessaire pour permettre au frontend React d'accéder au backend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Autorise les requêtes depuis localhost (dev)
        config.setAllowedOriginPatterns(Arrays.asList("*"));

        // Autorise tous les headers
        config.addAllowedHeader("*");

        // Autorise toutes les méthodes HTTP
        config.addAllowedMethod("*");

        // Autorise les credentials (cookies, headers d'authentification)
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}