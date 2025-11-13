package backend.eheichat.eheichat.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import backend.eheichat.eheichat.dto.*;
import backend.eheichat.eheichat.model.User;
import backend.eheichat.eheichat.repository.UserRepository;
import backend.eheichat.eheichat.service.JwtService;
import backend.eheichat.eheichat.service.WhatsAppService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final WhatsAppService whatsAppService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@Valid @RequestBody PhoneRequest request) {
        try {
            String phone = request.getPhoneNumber().replaceAll("\\s", "");

            log.info("Envoi du code au numéro: {}", phone);
            whatsAppService.sendVerificationCode(phone);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Code envoyé sur WhatsApp");
            response.put("phoneNumber", phone);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du code: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Impossible d'envoyer le code"));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyRequest request) {
        String phone = request.getPhoneNumber().replaceAll("\\s", "");

        boolean isValid = whatsAppService.verifyCode(phone, request.getCode());

        if (!isValid) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Code incorrect ou expiré"));
        }

        // Vérifier si l'utilisateur existe déjà
        Optional<User> existingUser = userRepository.findByPhoneNumber(phone);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtService.generateToken(user);

            return ResponseEntity.ok(new AuthResponse(token, user, false));
        }

        // Nouvel utilisateur
        Map<String, Object> response = new HashMap<>();
        response.put("isNewUser", true);
        response.put("phoneNumber", phone);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete-profile")
    public ResponseEntity<?> completeProfile(@Valid @RequestBody ProfileRequest request) {
        String phone = request.getPhoneNumber().replaceAll("\\s", "");

        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByPhoneNumber(phone)) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Utilisateur déjà existant"));
        }

        User user = new User();
        user.setPhoneNumber(phone);
        user.setName(request.getName());
        user.setRole(request.getRole().toUpperCase());
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        log.info("Nouveau utilisateur créé: {}", savedUser.getName());

        return ResponseEntity.ok(new AuthResponse(token, savedUser, true));
    }
}