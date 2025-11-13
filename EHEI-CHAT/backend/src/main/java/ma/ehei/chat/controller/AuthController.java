package ma.ehei.chat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ehei.chat.dto.*;
import ma.ehei.chat.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody PhoneRequest request) {
        try {
            String phone = request.getPhoneNumber().replaceAll("\\s", "");

            log.info("Envoi du code au numéro: {}", phone);
            authService.sendVerificationCode(phone);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Code envoyé sur WhatsApp");
            response.put("phoneNumber", phone);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du code: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Impossible d'envoyer le code"));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyRequest request) {
        try {
            String phone = request.getPhoneNumber().replaceAll("\\s", "");

            AuthResponse response = authService.verifyCode(phone, request.getCode());

            if (response.isNewUser()) {
                Map<String, Object> result = new HashMap<>();
                result.put("isNewUser", true);
                result.put("phoneNumber", phone);
                return ResponseEntity.ok(result);
            }

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Erreur de vérification: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la vérification: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la vérification"));
        }
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@Valid @RequestBody ProfileRequest request) {
        try {
            String phone = request.getPhoneNumber().replaceAll("\\s", "");

            AuthResponse response = authService.completeProfile(request);

            log.info("Profil complété pour: {}", phone);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Erreur de création de profil: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Erreur lors de la création du profil: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Erreur lors de la création du profil"));
        }
    }
}
