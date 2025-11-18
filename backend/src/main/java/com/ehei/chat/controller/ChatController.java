package com.ehei.chat.controller;

import com.ehei.chat.model.Message;
import com.ehei.chat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur pour les opérations de chat
 * 
 * Gère les endpoints :
 * - GET /api/chat/history : Récupérer l'historique
 * - POST /api/chat/send : Envoyer un message (aussi via WebSocket)
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    /**
     * Récupérer l'historique d'un chat individuel
     * 
     * GET /api/chat/history?userId1=xxx&userId2=yyy
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        try {
            List<Message> messages = chatService.getChatHistory(userId1, userId2);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Récupérer l'historique d'un groupe
     * 
     * GET /api/chat/group/{groupId}/history
     */
    @GetMapping("/group/{groupId}/history")
    public ResponseEntity<Map<String, Object>> getGroupHistory(@PathVariable String groupId) {
        try {
            List<Message> messages = chatService.getGroupHistory(groupId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messages", messages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}

