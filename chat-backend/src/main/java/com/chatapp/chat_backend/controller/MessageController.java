package com.chatapp.chat_backend.controller;


import com.chatapp.chat_backend.model.ChatMessage;
import com.chatapp.chat_backend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller pour les opérations HTTP sur les messages
 * Complémente le WebSocket pour les opérations non temps-réel
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final ChatMessageService messageService;

    /**
     * Récupère l'historique des messages entre deux utilisateurs
     * GET /api/messages/{senderId}/{recipientId}
     */
    @GetMapping("/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> getMessages(
            @PathVariable String senderId,
            @PathVariable String recipientId) {

        log.info("Récupération des messages entre {} et {}", senderId, recipientId);
        List<ChatMessage> messages = messageService.getChatMessages(senderId, recipientId);

        return ResponseEntity.ok(messages);
    }

    /**
     * Compte les messages non lus pour un utilisateur
     * GET /api/messages/unread/{userId}
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Long> getUnreadCount(@PathVariable String userId) {
        log.info("Comptage des messages non lus pour {}", userId);
        long count = messageService.countUnreadMessages(userId);

        return ResponseEntity.ok(count);
    }

    /**
     * Endpoint de test pour vérifier que l'API fonctionne
     * GET /api/messages/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Chat API is running! 🚀");
    }
}
