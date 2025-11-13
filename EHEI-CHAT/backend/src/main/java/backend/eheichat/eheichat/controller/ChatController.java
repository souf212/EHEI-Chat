package backend.eheichat.eheichat.controller;

import lombok.RequiredArgsConstructor;
import backend.eheichat.eheichat.model.Channel;
import backend.eheichat.eheichat.model.Message;
import backend.eheichat.eheichat.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;

    // ===== MESSAGES =====

    @MessageMapping("/message")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return chatService.saveMessage(message);
    }

    @GetMapping("/channels/{channelId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String channelId,
            @RequestParam(defaultValue = "50") int limit) {
        return ResponseEntity.ok(chatService.getMessagesByChannel(channelId, limit));
    }

    // ===== CHANNELS =====

    @GetMapping("/channels")
    public ResponseEntity<List<Channel>> getAllChannels() {
        return ResponseEntity.ok(chatService.getAllActiveChannels());
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

        Channel channel = chatService.createChannel(name, type, createdBy);
        return ResponseEntity.ok(channel);
    }

    @GetMapping("/users/{userId}/channels")
    public ResponseEntity<List<Channel>> getUserChannels(@PathVariable String userId) {
        return ResponseEntity.ok(chatService.getUserChannels(userId));
    }

    @PostMapping("/channels/{channelId}/members/{userId}")
    public ResponseEntity<Void> addMember(
            @PathVariable String channelId,
            @PathVariable String userId) {
        chatService.addMemberToChannel(channelId, userId);
        return ResponseEntity.ok().build();
    }
}