package com.chatapp.chat_backend.controller;


import com.chatapp.chat_backend.model.ChatMessage;
import com.chatapp.chat_backend.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * Controller WebSocket pour gérer les messages en temps réel
 * Utilise STOMP pour la communication bidirectionnelle
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageService messageService;

    /**
     * Gère les messages entrants via WebSocket
     * Endpoint: /app/chat (préfixe /app défini dans WebSocketConfig)
     *
     * @param chatMessage Message reçu du client
     */
    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {
        log.info("Message reçu de {} vers {}: {}",
                chatMessage.getSenderId(),
                chatMessage.getRecipientId(),
                chatMessage.getContent());

        // Sauvegarde le message dans MongoDB
        ChatMessage savedMessage = messageService.save(chatMessage);

        // Envoie le message au destinataire spécifique
        // Le message sera reçu sur: /user/{recipientId}/queue/messages
        messagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/messages",
                savedMessage
        );

        log.info("Message envoyé avec succès à l'utilisateur {}", chatMessage.getRecipientId());
    }
}
