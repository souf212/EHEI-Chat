package com.ehei.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Modèle User : Représente un utilisateur de la plateforme
 * 
 * @Document : Indique que cette classe est un document MongoDB
 * @Data : Lombok génère automatiquement getters, setters, toString, equals, hashCode
 */
@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * ID unique généré automatiquement par MongoDB
     */
    @Id
    private String id;
    
    /**
     * Numéro de téléphone marocain au format +212XXXXXXXXX
     * Exemple : +212612345678
     */
    private String phoneNumber;
    
    /**
     * Indique si le numéro a été vérifié via WhatsApp
     */
    private boolean verified;
    
    /**
     * Code de vérification à 6 chiffres (temporaire)
     * Généré lors de l'inscription et supprimé après vérification
     */
    private String verificationCode;
    
    /**
     * Date d'expiration du code de vérification (5 minutes)
     */
    private LocalDateTime codeExpiration;
    
    /**
     * Nom de l'utilisateur (optionnel, peut être ajouté plus tard)
     */
    private String name;
    
    /**
     * Date de création du compte
     */
    private LocalDateTime createdAt;
    
    /**
     * Dernière connexion
     */
    private LocalDateTime lastLogin;
    
    /**
     * Constructeur pour l'inscription initiale
     */
    public User(String phoneNumber, String verificationCode) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
        this.verified = false;
        this.createdAt = LocalDateTime.now();
        this.codeExpiration = LocalDateTime.now().plusMinutes(5);
    }
}

