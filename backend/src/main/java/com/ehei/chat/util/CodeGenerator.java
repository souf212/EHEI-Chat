package com.ehei.chat.util;

import java.util.Random;

/**
 * Utilitaire pour générer des codes de vérification
 * 
 * Génère un code à 6 chiffres aléatoire
 * Exemple : 123456, 789012, etc.
 */
public class CodeGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Génère un code de vérification à 6 chiffres
     * 
     * @return Un code entre 100000 et 999999
     */
    public static String generateVerificationCode() {
        // Génère un nombre entre 100000 et 999999
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}

