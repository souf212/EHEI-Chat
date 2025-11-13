package backend.eheichat.eheichat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyRequest {
    @NotBlank
    private String phoneNumber;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$", message = "Le code doit contenir 6 chiffres")
    private String code;
}