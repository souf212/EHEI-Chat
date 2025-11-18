package com.ehei.chat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Service pour envoyer des notifications en temps réel
 * 
 * Utilise WebSocket pour notifier les clients d'événements :
 * - Nouveau message
 * - Nouveau groupe créé
 * - Utilisateur ajouté à un groupe
 * etc.
 */
@Service
public class NotificationService {
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Envoyer une notification à un utilisateur spécifique
     * 
     * @param userId ID de l'utilisateur
     * @param event Type d'événement
     * @param data Données de l'événement
     */
    public void notifyUser(String userId, String event, Object data) {
        String destination = "/user/" + userId + "/notifications";
        Map<String, Object> notification = new HashMap<>();
        notification.put("event", event);
        notification.put("data", data);
        notification.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend(destination, notification);
    }
    
    /**
     * Notifier qu'un nouveau groupe a été créé
     */
    public void notifyNewGroup(String userId, Object groupData) {
        notifyUser(userId, "NEW_GROUP", groupData);
    }
    
    /**
     * Notifier qu'un nouveau message a été reçu
     */
    public void notifyNewMessage(String userId, Object messageData) {
        notifyUser(userId, "NEW_MESSAGE", messageData);
    }
}

