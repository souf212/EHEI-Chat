package com.ehei.chat.repository;

import com.ehei.chat.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les messages
 */
@Repository
public interface MessageRepository extends MongoRepository<Message, String> {
    
    /**
     * Trouver tous les messages entre deux utilisateurs (chat individuel)
     * Trier par date croissante (plus ancien en premier)
     */
    List<Message> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
        String senderId1, String receiverId1, 
        String senderId2, String receiverId2
    );
    
    /**
     * Trouver tous les messages d'un groupe
     * Trier par date croissante
     */
    List<Message> findByGroupIdOrderByTimestampAsc(String groupId);
    
    /**
     * Trouver tous les messages non lus pour un utilisateur
     */
    List<Message> findByReceiverIdAndReadFalse(String receiverId);
}

