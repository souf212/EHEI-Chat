package com.ehei.chat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle Group : Représente un groupe de chat
 */
@Document(collection = "groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Group {
    
    @Id
    private String id;
    
    /**
     * Nom du groupe
     */
    private String name;
    
    /**
     * Description du groupe
     */
    private String description;
    
    /**
     * ID de l'administrateur (créateur du groupe)
     */
    private String adminId;
    
    /**
     * Liste des IDs des membres du groupe
     */
    private List<String> memberIds;
    
    /**
     * Date de création
     */
    private LocalDateTime createdAt;
    
    /**
     * Constructeur pour créer un nouveau groupe
     */
    public Group(String name, String description, String adminId) {
        this.name = name;
        this.description = description;
        this.adminId = adminId;
        this.memberIds = new ArrayList<>();
        this.memberIds.add(adminId); // L'admin est automatiquement membre
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Ajouter un membre au groupe
     */
    public void addMember(String userId) {
        if (!memberIds.contains(userId)) {
            memberIds.add(userId);
        }
    }
    
    /**
     * Retirer un membre du groupe
     */
    public void removeMember(String userId) {
        memberIds.remove(userId);
    }
}

