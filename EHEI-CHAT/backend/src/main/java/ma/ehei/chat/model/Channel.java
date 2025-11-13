package ma.ehei.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "channels")
@Data
public class Channel {
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String type; // GROUP, DIRECT

    private String description;

    private List<String> memberIds = new ArrayList<>();

    private String createdBy;
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isActive = true;

    // For direct messages: store both user IDs
    private List<String> participantIds = new ArrayList<>();
}
