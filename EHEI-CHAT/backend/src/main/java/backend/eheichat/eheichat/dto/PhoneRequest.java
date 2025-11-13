package backend.eheichat.eheichat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneRequest {
    @NotBlank(message = "Le numéro de téléphone est requis")
    @Pattern(regexp = "^(06|07)\\d{8}$",
            message = "Format invalide. Utilisez: 06XXXXXXXX ou 07XXXXXXXX")
    private String phoneNumber;
}