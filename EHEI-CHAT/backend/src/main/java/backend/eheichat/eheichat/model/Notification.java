package backend.eheichat.eheichat.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "notifications")
public class Notification {
    @Id
    private String id;

    @Indexed
    private String userId;

    private String type; // MESSAGE, EVENT, SYSTEM
    private String title;
    private String text;

    private String channelId;
    private String messageId;

    private boolean read = false;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime readAt;
}
