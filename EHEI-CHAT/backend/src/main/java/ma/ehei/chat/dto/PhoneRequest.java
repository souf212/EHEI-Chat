package ma.ehei.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneRequest {
    @NotBlank(message = "Le numéro de téléphone est requis")
    @Pattern(regexp = "^(06|07)\\d{8}$", message = "Format invalide. Utilisez 06 ou 07 suivi de 8 chiffres")
    private String phoneNumber;
}
