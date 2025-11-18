package com.ehei.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Modèle Message : Représente un message dans un chat
 * 
 * Types de messages :
 * - INDIVIDUAL : Chat entre deux utilisateurs
 * - GROUP : Message dans un groupe
 */
@Document(collection = "messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    
    @Id
    private String id;
    
    /**
     * ID de l'utilisateur qui envoie le message
     */
    private String senderId;
    
    /**
     * ID du destinataire (pour chat individuel)
     * null si c'est un message de groupe
     */
    private String receiverId;
    
    /**
     * ID du groupe (pour chat de groupe)
     * null si c'est un message individuel
     */
    private String groupId;
    
    /**
     * Contenu du message
     */
    private String content;
    
    /**
     * Type de message : INDIVIDUAL ou GROUP
     */
    private MessageType type;
    
    /**
     * Date et heure d'envoi
     */
    private LocalDateTime timestamp;
    
    /**
     * Indique si le message a été lu
     */
    private boolean read;
    
    /**
     * Enum pour le type de message
     */
    public enum MessageType {
        INDIVIDUAL,  // Chat entre deux personnes
        GROUP        // Message dans un groupe
    }
    
    /**
     * Constructeur pour un message individuel
     */
    public Message(String senderId, String receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.type = MessageType.INDIVIDUAL;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }
    
    /**
     * Constructeur pour un message de groupe
     */
    public Message(String senderId, String groupId, String content, MessageType type) {
        this.senderId = senderId;
        this.groupId = groupId;
        this.content = content;
        this.type = type;
        this.timestamp = LocalDateTime.now();
        this.read = false;
    }
}

