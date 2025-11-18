package com.ehei.chat.controller;

import com.ehei.chat.model.User;
import com.ehei.chat.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour l'authentification
 * 
 * Gère les endpoints :
 * - POST /auth/register : Inscription
 * - POST /auth/verify : Vérification du code
 * - POST /auth/login : Connexion
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permettre les requêtes depuis React
public class AuthController {
    
    @Autowired
    private AuthService authService;
    
    /**
     * Endpoint d'inscription
     * 
     * Exemple de requête :
     * POST /api/auth/register
     * Body: { "phoneNumber": "+212612345678" }
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Le numéro de téléphone est requis"));
            }
            
            User user = authService.register(phoneNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Code de vérification envoyé via WhatsApp");
            response.put("userId", user.getId());
            // Ne pas renvoyer le code de vérification pour la sécurité
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur lors de l'inscription : " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint de vérification
     * 
     * Exemple de requête :
     * POST /api/auth/verify
     * Body: { "phoneNumber": "+212612345678", "code": "123456" }
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verify(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            String code = request.get("code");
            
            if (phoneNumber == null || code == null) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Le numéro et le code sont requis"));
            }
            
            User user = authService.verify(phoneNumber, code);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Numéro vérifié avec succès");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur lors de la vérification : " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint de connexion
     * 
     * Exemple de requête :
     * POST /api/auth/login
     * Body: { "phoneNumber": "+212612345678" }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        try {
            String phoneNumber = request.get("phoneNumber");
            
            if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Le numéro de téléphone est requis"));
            }
            
            User user = authService.login(phoneNumber);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Connexion réussie");
            response.put("user", createUserResponse(user));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Erreur lors de la connexion : " + e.getMessage()));
        }
    }
    
    /**
     * Créer une réponse d'erreur
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
    
    /**
     * Créer une réponse utilisateur (sans données sensibles)
     */
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("phoneNumber", user.getPhoneNumber());
        userResponse.put("verified", user.isVerified());
        userResponse.put("name", user.getName());
        return userResponse;
    }
}

