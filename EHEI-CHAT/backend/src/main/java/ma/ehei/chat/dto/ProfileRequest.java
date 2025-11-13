package ma.ehei.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileRequest {
    @NotBlank(message = "Le numéro de téléphone est requis")
    private String phoneNumber;

    @NotBlank(message = "Le nom est requis")
    private String name;

    @NotBlank(message = "Le rôle est requis")
    @Pattern(regexp = "^(STUDENT|TEACHER|STAFF)$", message = "Rôle invalide. Utilisez STUDENT, TEACHER ou STAFF")
    private String role;
}
