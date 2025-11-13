package backend.eheichat.eheichat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import backend.eheichat.eheichat.model.User;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
    private boolean isNewUser;
}