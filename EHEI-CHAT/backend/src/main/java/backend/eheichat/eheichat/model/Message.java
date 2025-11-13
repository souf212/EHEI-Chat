package backend.eheichat.eheichat.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "messages")
public class Message {
    @Id
    private String id;

    @Indexed
    private String channelId;

    private String userId;
    private String userName;
    private String text;

    @Indexed
    private LocalDateTime timestamp = LocalDateTime.now();

    private boolean edited = false;
    private LocalDateTime editedAt;
}
