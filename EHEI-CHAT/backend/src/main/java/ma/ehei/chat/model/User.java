package ma.ehei.chat.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "users")
@Data
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String phoneNumber;

    private String name;

    @Indexed
    private String role; // STUDENT, TEACHER, STAFF

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive = true;

    // Profile image URL (optional)
    private String profileImageUrl;
}
