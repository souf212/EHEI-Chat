package ma.ehei.chat.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ehei.chat.model.Channel;
import ma.ehei.chat.repository.ChannelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ChannelRepository channelRepository;

    @Override
    public void run(String... args) {
        // Créer des canaux par défaut s'ils n'existent pas
        List<String> defaultChannels = Arrays.asList(
                "général",
                "projets-2024",
                "annonces",
                "aide"
        );

        for (String channelName : defaultChannels) {
            if (!channelRepository.existsByName(channelName)) {
                Channel channel = new Channel();
                channel.setName(channelName);
                channel.setType("GROUP");
                channel.setDescription("Canal " + channelName);
                channel.setCreatedAt(LocalDateTime.now());
                channel.setActive(true);
                channelRepository.save(channel);
                log.info("Canal par défaut créé: {}", channelName);
            }
        }
    }
}
