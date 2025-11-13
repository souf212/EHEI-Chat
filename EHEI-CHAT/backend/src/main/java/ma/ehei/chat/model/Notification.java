package ma.ehei.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
public class Notification {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String type; // MESSAGE, EVENT, SYSTEM

    private String title;
    private String message;

    @Indexed
    private LocalDateTime createdAt = LocalDateTime.now();

    private boolean isRead = false;

    private String channelId;
    private String relatedId; // ID of related message or event
}
