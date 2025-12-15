package com.chatapp.chat_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application Chat Backend
 * Point d'entrée de l'application Spring Boot
 */
@SpringBootApplication
public class ChatAppBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(ChatAppBackendApplication.class, args);
        System.out.println("🚀 Chat Backend démarré avec succès sur http://localhost:8080");
    }
}