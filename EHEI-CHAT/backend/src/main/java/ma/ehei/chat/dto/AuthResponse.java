package ma.ehei.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ma.ehei.chat.model.User;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private User user;
    private boolean isNewUser;
}
