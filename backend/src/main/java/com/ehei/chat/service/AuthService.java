package com.ehei.chat.service;

import com.ehei.chat.model.User;
import com.ehei.chat.repository.UserRepository;
import com.ehei.chat.util.CodeGenerator;
import com.ehei.chat.util.PhoneNumberValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service d'authentification
 * 
 * Gère :
 * - L'inscription (envoi du code de vérification)
 * - La vérification du code
 * - La connexion
 */
@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WhatsAppService whatsAppService;
    
    /**
     * Inscription : Enregistre un numéro et envoie un code de vérification
     * 
     * @param phoneNumber Le numéro de téléphone marocain
     * @return L'utilisateur créé ou existant
     * @throws IllegalArgumentException Si le numéro est invalide
     */
    public User register(String phoneNumber) {
        // 1. Valider le numéro
        String normalizedPhone = PhoneNumberValidator.normalize(phoneNumber);
        if (!PhoneNumberValidator.isValid(normalizedPhone)) {
            throw new IllegalArgumentException("Numéro de téléphone invalide. Format attendu : +212XXXXXXXXX");
        }
        
        // 2. Vérifier si l'utilisateur existe déjà
        Optional<User> existingUser = userRepository.findByPhoneNumber(normalizedPhone);
        User user;
        
        if (existingUser.isPresent()) {
            // Utilisateur existe : générer un nouveau code
            user = existingUser.get();
        } else {
            // Nouvel utilisateur : créer un compte
            user = new User();
            user.setPhoneNumber(normalizedPhone);
            user.setCreatedAt(LocalDateTime.now());
        }
        
        // 3. Générer un nouveau code de vérification
        String code = CodeGenerator.generateVerificationCode();
        user.setVerificationCode(code);
        user.setCodeExpiration(LocalDateTime.now().plusMinutes(5));
        user.setVerified(false);
        
        // 4. Sauvegarder l'utilisateur
        user = userRepository.save(user);
        
        // 5. Envoyer le code via WhatsApp
        whatsAppService.sendVerificationCode(normalizedPhone, code);
        
        return user;
    }
    
    /**
     * Vérification : Vérifie le code reçu par l'utilisateur
     * 
     * @param phoneNumber Le numéro de téléphone
     * @param code Le code de vérification
     * @return L'utilisateur vérifié
     * @throws IllegalArgumentException Si le code est invalide ou expiré
     */
    public User verify(String phoneNumber, String code) {
        // 1. Trouver l'utilisateur
        String normalizedPhone = PhoneNumberValidator.normalize(phoneNumber);
        User user = userRepository.findByPhoneNumber(normalizedPhone)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        // 2. Vérifier le code
        if (user.getVerificationCode() == null || !user.getVerificationCode().equals(code)) {
            throw new IllegalArgumentException("Code de vérification invalide");
        }
        
        // 3. Vérifier l'expiration
        if (user.getCodeExpiration() == null || user.getCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Code de vérification expiré. Veuillez demander un nouveau code.");
        }
        
        // 4. Marquer comme vérifié
        user.setVerified(true);
        user.setVerificationCode(null); // Supprimer le code après vérification
        user.setCodeExpiration(null);
        user.setLastLogin(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    /**
     * Connexion : Vérifie si un utilisateur est vérifié et le connecte
     * 
     * @param phoneNumber Le numéro de téléphone
     * @return L'utilisateur connecté
     * @throws IllegalArgumentException Si l'utilisateur n'est pas vérifié
     */
    public User login(String phoneNumber) {
        String normalizedPhone = PhoneNumberValidator.normalize(phoneNumber);
        User user = userRepository.findByPhoneNumber(normalizedPhone)
            .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé"));
        
        if (!user.isVerified()) {
            throw new IllegalArgumentException("Veuillez d'abord vérifier votre numéro");
        }
        
        user.setLastLogin(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    /**
     * Trouver un utilisateur par son ID
     */
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Trouver un utilisateur par son numéro
     */
    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(PhoneNumberValidator.normalize(phoneNumber));
    }
}

