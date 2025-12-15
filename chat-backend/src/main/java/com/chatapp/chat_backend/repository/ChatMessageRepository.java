package com.chatapp.chat_backend.repository;

import com.chatapp.chat_backend.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour gérer les opérations CRUD sur les messages
 */
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    /**
     * Récupère tous les messages d'une conversation spécifique
     * @param chatId Identifiant de la conversation
     * @return Liste des messages triés par timestamp
     */
    List<ChatMessage> findByChatIdOrderByTimestampAsc(String chatId);

    /**
     * Compte le nombre de messages non lus pour un destinataire
     * @param recipientId ID du destinataire
     * @param status Statut du message
     * @return Nombre de messages
     */
    long countByRecipientIdAndStatus(String recipientId, ChatMessage.MessageStatus status);
}
