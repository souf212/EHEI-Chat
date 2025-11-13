package backend.eheichat.eheichat.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import backend.eheichat.eheichat.model.User;
import backend.eheichat.eheichat.repository.UserRepository;
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

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public User createUser(String phoneNumber, String name, String role) {
        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setName(name);
        user.setRole(role.toUpperCase());
        user.setCreatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("Nouvel utilisateur créé: {} ({})", savedUser.getName(), savedUser.getRole());
        return savedUser;
    }

    public void updateLastLogin(User user) {
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    public String generateToken(User user) {
        return jwtService.generateToken(user);
    }

    public boolean sendVerificationCode(String phoneNumber) {
        try {
            whatsAppService.sendVerificationCode(phoneNumber);
            return true;
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du code: {}", e.getMessage());
            return false;
        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        return whatsAppService.verifyCode(phoneNumber, code);
    }
}
