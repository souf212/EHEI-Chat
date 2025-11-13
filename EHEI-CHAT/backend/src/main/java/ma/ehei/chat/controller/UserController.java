package ma.ehei.chat.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ehei.chat.model.User;
import ma.ehei.chat.service.AuthService;
import ma.ehei.chat.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final AuthService authService;
    private final JwtService jwtService;

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.substring(7); // Remove "Bearer " prefix
            if (!jwtService.isTokenValid(jwt)) {
                return ResponseEntity.status(401).build();
            }

            String userId = jwtService.extractUserId(jwt);
            User user = authService.getUserById(userId);
            return ResponseEntity.ok(user);

        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'utilisateur: {}", e.getMessage());
            return ResponseEntity.status(401).build();
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable String userId) {
        try {
            User user = authService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<User> updateUser(
            @PathVariable String userId,
            @RequestBody Map<String, String> updates) {
        try {
            User user = authService.getUserById(userId);
            
            if (updates.containsKey("name")) {
                user.setName(updates.get("name"));
            }
            if (updates.containsKey("role")) {
                user.setRole(updates.get("role"));
            }
            if (updates.containsKey("profileImageUrl")) {
                user.setProfileImageUrl(updates.get("profileImageUrl"));
            }

            // Save would need to be added to AuthService
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
