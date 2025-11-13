package backend.eheichat.eheichat.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import backend.eheichat.eheichat.model.Channel;
import backend.eheichat.eheichat.model.Message;
import backend.eheichat.eheichat.repository.ChannelRepository;
import backend.eheichat.eheichat.repository.MessageRepository;
import org.springframework.data.domain.PageRequest;
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

    // ===== MESSAGES =====

    public Message saveMessage(Message message) {
        message.setTimestamp(LocalDateTime.now());
        Message savedMessage = messageRepository.save(message);
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

    // ===== CHANNELS =====

    public Channel createChannel(String name, String type, String createdBy) {
        if (channelRepository.existsByName(name)) {
            throw new RuntimeException("Un canal avec ce nom existe déjà");
        }

        Channel channel = new Channel();
        channel.setName(name);
        channel.setType(type);
        channel.setCreatedBy(createdBy);
        channel.setCreatedAt(LocalDateTime.now());
        channel.setActive(true);

        Channel savedChannel = channelRepository.save(channel);
        log.info("Canal créé: {} (type: {})", savedChannel.getName(), savedChannel.getType());
        return savedChannel;
    }

    public Optional<Channel> getChannelById(String channelId) {
        return channelRepository.findById(channelId);
    }

    public Optional<Channel> getChannelByName(String name) {
        return channelRepository.findByName(name);
    }

    public List<Channel> getUserChannels(String userId) {
        return channelRepository.findByMemberIdsContaining(userId);
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
}