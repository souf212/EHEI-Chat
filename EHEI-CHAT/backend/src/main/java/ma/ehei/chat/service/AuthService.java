package ma.ehei.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ehei.chat.dto.AuthResponse;
import ma.ehei.chat.dto.ProfileRequest;
import ma.ehei.chat.model.User;
import ma.ehei.chat.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final WhatsAppService whatsAppService;

    public void sendVerificationCode(String phoneNumber) {
        // Validate Moroccan phone number format
        String cleaned = phoneNumber.replaceAll("\\s", "");
        if (!cleaned.matches("^(06|07)\\d{8}$")) {
            throw new IllegalArgumentException("Format de numéro invalide. Utilisez 06 ou 07 suivi de 8 chiffres");
        }

        whatsAppService.sendVerificationCode(cleaned);
        log.info("Code de vérification envoyé au numéro: {}", cleaned);
    }

    public AuthResponse verifyCode(String phoneNumber, String code) {
        String cleaned = phoneNumber.replaceAll("\\s", "");

        boolean isValid = whatsAppService.verifyCode(cleaned, code);
        if (!isValid) {
            throw new IllegalArgumentException("Code incorrect ou expiré");
        }

        // Check if user exists
        Optional<User> existingUser = userRepository.findByPhoneNumber(cleaned);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            String token = jwtService.generateToken(user);
            log.info("Utilisateur connecté: {}", user.getName());

            return new AuthResponse(token, user, false);
        }

        // New user - return response indicating profile setup needed
        return new AuthResponse(null, null, true);
    }

    public AuthResponse completeProfile(ProfileRequest request) {
        String cleaned = request.getPhoneNumber().replaceAll("\\s", "");

        // Check if user already exists
        if (userRepository.existsByPhoneNumber(cleaned)) {
            throw new IllegalArgumentException("Utilisateur déjà existant");
        }

        User user = new User();
        user.setPhoneNumber(cleaned);
        user.setName(request.getName());
        user.setRole(request.getRole().toUpperCase());
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser);

        log.info("Nouveau utilisateur créé: {} ({})", savedUser.getName(), savedUser.getRole());

        return new AuthResponse(token, savedUser, true);
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
    }
}
