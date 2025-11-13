package ma.ehei.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ehei.chat.model.Channel;
import ma.ehei.chat.model.Message;
import ma.ehei.chat.model.Notification;
import ma.ehei.chat.repository.ChannelRepository;
import ma.ehei.chat.repository.MessageRepository;
import ma.ehei.chat.repository.NotificationRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // ===== MESSAGES =====

    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);

        // Send notification to channel members (except sender)
        sendNotificationToChannelMembers(savedMessage);

        // Broadcast message to WebSocket subscribers
        messagingTemplate.convertAndSend("/topic/messages/" + message.getChannelId(), savedMessage);

        log.debug("Message sauvegardé: {} dans channel {}", savedMessage.getId(), savedMessage.getChannelId());
        return savedMessage;
    }

    public List<Message> getMessagesByChannel(String channelId, int limit) {
        return messageRepository.findByChannelIdOrderByTimestampDesc(
                channelId,
                PageRequest.of(0, limit)
        );
    }

    public List<Message> getAllMessagesByChannel(String channelId) {
        return messageRepository.findByChannelIdOrderByTimestampDesc(channelId);
    }

    public long getMessageCount(String channelId) {
        return messageRepository.countByChannelId(channelId);
    }

    private void sendNotificationToChannelMembers(Message message) {
        Optional<Channel> channelOpt = channelRepository.findById(message.getChannelId());
        if (channelOpt.isPresent()) {
            Channel channel = channelOpt.get();
            for (String memberId : channel.getMemberIds()) {
                if (!memberId.equals(message.getUserId())) {
                    // Create notification
                    Notification notification = new Notification();
                    notification.setUserId(memberId);
                    notification.setType("MESSAGE");
                    notification.setTitle("Nouveau message dans " + channel.getName());
                    notification.setMessage(message.getUserName() + ": " + message.getText());
                    notification.setChannelId(message.getChannelId());
                    notification.setRelatedId(message.getId());
                    notification.setCreatedAt(LocalDateTime.now());
                    notification.setRead(false);
                    notificationRepository.save(notification);

                    // Send WebSocket notification
                    messagingTemplate.convertAndSendToUser(
                            memberId,
                            "/queue/notifications",
                            notification
                    );
                }
            }
        }
    }

    // ===== CHANNELS =====

    public Channel createChannel(String name, String type, String createdBy, String description) {
        if (channelRepository.existsByName(name)) {
            throw new IllegalArgumentException("Un canal avec ce nom existe déjà");
        }

        Channel channel = new Channel();
        channel.setName(name);
        channel.setType(type);
        channel.setDescription(description);
        channel.setCreatedBy(createdBy);
        channel.setCreatedAt(LocalDateTime.now());
        channel.setActive(true);

        // Add creator as member
        channel.getMemberIds().add(createdBy);

        Channel savedChannel = channelRepository.save(channel);
        log.info("Canal créé: {} (type: {})", savedChannel.getName(), savedChannel.getType());

        // Broadcast new channel to all users
        messagingTemplate.convertAndSend("/topic/channels", savedChannel);

        return savedChannel;
    }

    public Optional<Channel> getChannelById(String channelId) {
        return channelRepository.findById(channelId);
    }

    public Optional<Channel> getChannelByName(String name) {
        return channelRepository.findByName(name);
    }

    public List<Channel> getUserChannels(String userId) {
        List<Channel> groupChannels = channelRepository.findByMemberIdsContaining(userId);
        List<Channel> directChannels = channelRepository.findByParticipantIdsContaining(userId);
        groupChannels.addAll(directChannels);
        return groupChannels;
    }

    public List<Channel> getAllActiveChannels() {
        return channelRepository.findByTypeAndIsActive("GROUP", true);
    }

    public void addMemberToChannel(String channelId, String userId) {
        channelRepository.findById(channelId).ifPresent(channel -> {
            if (!channel.getMemberIds().contains(userId)) {
                channel.getMemberIds().add(userId);
                channelRepository.save(channel);
                log.info("Utilisateur {} ajouté au canal {}", userId, channel.getName());
            }
        });
    }

    public void removeMemberFromChannel(String channelId, String userId) {
        channelRepository.findById(channelId).ifPresent(channel -> {
            channel.getMemberIds().remove(userId);
            channelRepository.save(channel);
            log.info("Utilisateur {} retiré du canal {}", userId, channel.getName());
        });
    }

    // ===== NOTIFICATIONS =====

    public List<Notification> getUserNotifications(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    public long getUnreadNotificationCount(String userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    public void markNotificationAsRead(String notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }

    public void markAllNotificationsAsRead(String userId) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        notifications.forEach(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
