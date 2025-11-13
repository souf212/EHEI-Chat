package ma.ehei.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ehei.chat.model.Channel;
import ma.ehei.chat.model.Message;
import ma.ehei.chat.model.Notification;
import ma.ehei.chat.service.ChatService;
import ma.ehei.chat.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final JwtService jwtService;

    // ===== MESSAGES =====

    @MessageMapping("/message")
    public void sendMessage(@Payload Message message) {
        log.debug("Message reçu via WebSocket: {}", message.getText());
        chatService.saveMessage(message);
        // Message is broadcasted in ChatService.saveMessage() via SimpMessagingTemplate
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "50") int limit,
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        // Validate token if provided
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            if (!jwtService.isTokenValid(jwt)) {
                return ResponseEntity.status(401).build();
            }
        }

        List<Message> messages = chatService.getMessagesByChannel(channelId, limit);
        return ResponseEntity.ok(messages);
    }

    // ===== CHANNELS =====

    @GetMapping("/channels")
    public ResponseEntity<List<Channel>> getAllChannels(
            @RequestHeader(value = "Authorization", required = false) String token) {
        
        // Validate token if provided
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            if (!jwtService.isTokenValid(jwt)) {
                return ResponseEntity.status(401).build();
            }
        }

        List<Channel> channels = chatService.getAllActiveChannels();
        return ResponseEntity.ok(channels);
    }

    @GetMapping("/channels/{channelId}")
    public ResponseEntity<Channel> getChannel(@PathVariable String channelId) {
        return chatService.getChannelById(channelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/channels")
    public ResponseEntity<Channel> createChannel(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String type = request.getOrDefault("type", "GROUP");
        String createdBy = request.get("createdBy");
        String description = request.getOrDefault("description", "");

        if (name == null || createdBy == null) {
            return ResponseEntity.badRequest().build();
        }

        Channel channel = chatService.createChannel(name, type, createdBy, description);
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/users/{userId}/channels")
    public ResponseEntity<List<Channel>> getUserChannels(@PathVariable String userId) {
        List<Channel> channels = chatService.getUserChannels(userId);
        return ResponseEntity.ok(channels);
    }

    @PostMapping("/channels/{channelId}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable String channelId,
            @PathVariable String userId) {
        chatService.addMemberToChannel(channelId, userId);
        return ResponseEntity.ok().build();
    }

    // ===== NOTIFICATIONS =====

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        List<Notification> notifications = chatService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/{userId}/unread")
    public ResponseEntity<List<Notification>> getUnreadNotifications(@PathVariable String userId) {
        List<Notification> notifications = chatService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/notifications/{userId}/count")
    public ResponseEntity<Map<String, Long>> getUnreadNotificationCount(@PathVariable String userId) {
        long count = chatService.getUnreadNotificationCount(userId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/notifications/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(@PathVariable String notificationId) {
        chatService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/notifications/{userId}/read-all")
    public ResponseEntity<Void> markAllNotificationsAsRead(@PathVariable String userId) {
        chatService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok().build();
    }
}
