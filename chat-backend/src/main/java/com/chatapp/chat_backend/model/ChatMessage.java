package com.chatapp.chat_backend.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Modèle représentant un message dans la base de données MongoDB
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;

    // Identifiant unique de la conversation (combinaison des deux utilisateurs)
    private String chatId;

    // ID de l'expéditeur
    private String senderId;

    // ID du destinataire
    private String recipientId;

    // Contenu du message
    private String content;

    // Date et heure d'envoi
    private LocalDateTime timestamp;

    // Statut du message (SENT, DELIVERED, READ)
    private MessageStatus status;

    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }
}