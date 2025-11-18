package com.ehei.chat.service;

import com.ehei.chat.model.Message;
import com.ehei.chat.model.User;
import com.ehei.chat.repository.MessageRepository;
import com.ehei.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les chats et messages
 */
@Service
public class ChatService {
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Envoyer un message individuel
     * 
     * @param senderId ID de l'expéditeur
     * @param receiverId ID du destinataire
     * @param content Contenu du message
     * @return Le message sauvegardé
     */
    public Message sendIndividualMessage(String senderId, String receiverId, String content) {
        // Vérifier que les utilisateurs existent
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);
        
        if (sender.isEmpty() || receiver.isEmpty()) {
            throw new IllegalArgumentException("Utilisateur non trouvé");
        }
        
        // Créer et sauvegarder le message
        Message message = new Message(senderId, receiverId, content);
        return messageRepository.save(message);
    }
    
    /**
     * Envoyer un message dans un groupe
     * 
     * @param senderId ID de l'expéditeur
     * @param groupId ID du groupe
     * @param content Contenu du message
     * @return Le message sauvegardé
     */
    public Message sendGroupMessage(String senderId, String groupId, String content) {
        Message message = new Message(senderId, groupId, content, Message.MessageType.GROUP);
        return messageRepository.save(message);
    }
    
    /**
     * Récupérer l'historique d'un chat individuel
     * 
     * @param userId1 ID du premier utilisateur
     * @param userId2 ID du second utilisateur
     * @return Liste des messages entre les deux utilisateurs
     */
    public List<Message> getChatHistory(String userId1, String userId2) {
        return messageRepository.findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
            userId1, userId2, userId2, userId1
        );
    }
    
    /**
     * Récupérer l'historique d'un groupe
     * 
     * @param groupId ID du groupe
     * @return Liste des messages du groupe
     */
    public List<Message> getGroupHistory(String groupId) {
        return messageRepository.findByGroupIdOrderByTimestampAsc(groupId);
    }
    
    /**
     * Marquer un message comme lu
     */
    public void markAsRead(String messageId) {
        Optional<Message> message = messageRepository.findById(messageId);
        if (message.isPresent()) {
            Message msg = message.get();
            msg.setRead(true);
            messageRepository.save(msg);
        }
    }
}

