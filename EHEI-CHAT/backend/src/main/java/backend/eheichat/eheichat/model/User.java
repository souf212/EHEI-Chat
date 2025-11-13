package backend.eheichat.eheichat.model;

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
    private String role;

    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private boolean isActive = true;
}