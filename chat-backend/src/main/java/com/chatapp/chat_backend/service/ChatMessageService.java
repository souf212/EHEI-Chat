package com.chatapp.chat_backend.service;

import com.chatapp.chat_backend.model.ChatMessage;
import com.chatapp.chat_backend.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service gérant la logique métier des messages
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository messageRepository;

    /**
     * Sauvegarde un nouveau message dans la base de données
     * @param chatMessage Message à sauvegarder
     * @return Message sauvegardé avec son ID généré
     */
    public ChatMessage save(ChatMessage chatMessage) {
        // Génère un chatId unique basé sur les deux utilisateurs
        chatMessage.setChatId(generateChatId(chatMessage.getSenderId(), chatMessage.getRecipientId()));
        chatMessage.setTimestamp(LocalDateTime.now());
        chatMessage.setStatus(ChatMessage.MessageStatus.SENT);

        ChatMessage savedMessage = messageRepository.save(chatMessage);
        log.info("Message sauvegardé: {} -> {}", chatMessage.getSenderId(), chatMessage.getRecipientId());

        return savedMessage;
    }

    /**
     * Récupère l'historique des messages entre deux utilisateurs
     * @param senderId ID de l'expéditeur
     * @param recipientId ID du destinataire
     * @return Liste des messages
     */
    public List<ChatMessage> getChatMessages(String senderId, String recipientId) {
        String chatId = generateChatId(senderId, recipientId);
        List<ChatMessage> messages = messageRepository.findByChatIdOrderByTimestampAsc(chatId);

        log.info("Récupération de {} messages pour la conversation {}", messages.size(), chatId);
        return messages;
    }

    /**
     * Compte les messages non lus pour un utilisateur
     * @param userId ID de l'utilisateur
     * @return Nombre de messages non lus
     */
    public long countUnreadMessages(String userId) {
        return messageRepository.countByRecipientIdAndStatus(userId, ChatMessage.MessageStatus.SENT);
    }

    /**
     * Génère un ID unique pour une conversation entre deux utilisateurs
     * L'ordre des IDs est normalisé pour garantir le même chatId quelle que soit la direction
     * @param userId1 Premier utilisateur
     * @param userId2 Second utilisateur
     * @return chatId unique
     */
    private String generateChatId(String userId1, String userId2) {
        // Trie les IDs pour avoir toujours le même chatId
        List<String> ids = new ArrayList<>();
        ids.add(userId1);
        ids.add(userId2);
        ids.sort(String::compareTo);

        return ids.get(0) + "_" + ids.get(1);
    }
}
