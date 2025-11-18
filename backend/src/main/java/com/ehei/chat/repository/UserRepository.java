package com.ehei.chat.repository;

import com.ehei.chat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository pour les opérations sur les utilisateurs
 * 
 * MongoRepository fournit automatiquement :
 * - save() : Sauvegarder un utilisateur
 * - findById() : Trouver par ID
 * - findAll() : Trouver tous les utilisateurs
 * - delete() : Supprimer un utilisateur
 * 
 * On peut aussi définir des méthodes personnalisées
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    
    /**
     * Trouver un utilisateur par son numéro de téléphone
     * Spring Data génère automatiquement la requête MongoDB
     */
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    /**
     * Vérifier si un numéro existe déjà
     */
    boolean existsByPhoneNumber(String phoneNumber);
}

