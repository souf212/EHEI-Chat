package com.ehei.chat.controller;

import com.ehei.chat.model.Message;
import com.ehei.chat.service.ChatService;
import com.ehei.chat.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur WebSocket pour gérer les messages en temps réel
 * 
 * Les clients envoient des messages à /app/chat.send
 * Le serveur les route vers les destinataires appropriés
 */
@Controller
public class WebSocketController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Gérer l'envoi d'un message individuel
     * 
     * Le client envoie à : /app/chat.send
     * Format du message :
     * {
     *   "type": "INDIVIDUAL",
     *   "senderId": "xxx",
     *   "receiverId": "yyy",
     *   "content": "Bonjour !"
     * }
     */
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload Map<String, Object> messageData) {
        try {
            String type = (String) messageData.get("type");
            String senderId = (String) messageData.get("senderId");
            String content = (String) messageData.get("content");
            
            Message savedMessage;
            
            if ("INDIVIDUAL".equals(type)) {
                // Message individuel
                String receiverId = (String) messageData.get("receiverId");
                savedMessage = chatService.sendIndividualMessage(senderId, receiverId, content);
                
                // Envoyer le message au destinataire
                messagingTemplate.convertAndSend("/user/" + receiverId + "/queue/messages", savedMessage);
                
                // Confirmer l'envoi à l'expéditeur
                messagingTemplate.convertAndSend("/user/" + senderId + "/queue/messages", savedMessage);
                
                // Notifier le destinataire
                notificationService.notifyNewMessage(receiverId, savedMessage);
                
            } else if ("GROUP".equals(type)) {
                // Message de groupe
                String groupId = (String) messageData.get("groupId");
                savedMessage = chatService.sendGroupMessage(senderId, groupId, content);
                
                // Envoyer le message à tous les membres du groupe
                messagingTemplate.convertAndSend("/topic/group/" + groupId, savedMessage);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du message : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gérer la connexion d'un utilisateur
     * 
     * Le client s'abonne à : /user/{userId}/queue/messages
     * pour recevoir ses messages privés
     */
    @MessageMapping("/chat.connect")
    public void handleUserConnect(@Payload Map<String, String> data) {
        String userId = data.get("userId");
        System.out.println("✅ Utilisateur connecté : " + userId);
        
        // Envoyer une confirmation
        Map<String, Object> response = new HashMap<>();
        response.put("type", "CONNECTION_CONFIRMED");
        response.put("userId", userId);
        messagingTemplate.convertAndSend("/user/" + userId + "/queue/status", response);
    }
}

