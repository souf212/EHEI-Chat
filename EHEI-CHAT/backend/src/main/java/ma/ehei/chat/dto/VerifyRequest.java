package ma.ehei.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank(message = "Le numéro de téléphone est requis")
    private String phoneNumber;

    @NotBlank(message = "Le code de vérification est requis")
    @Pattern(regexp = "^\\d{6}$", message = "Le code doit contenir 6 chiffres")
    private String code;
}
