package com.ehei.chat.repository;

import com.ehei.chat.model.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour les groupes
 */
@Repository
public interface GroupRepository extends MongoRepository<Group, String> {
    
    /**
     * Trouver tous les groupes où un utilisateur est membre
     */
    List<Group> findByMemberIdsContaining(String userId);
}

