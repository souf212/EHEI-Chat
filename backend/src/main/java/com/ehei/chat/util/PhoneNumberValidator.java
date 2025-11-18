package com.ehei.chat.util;

import java.util.regex.Pattern;

/**
 * Utilitaire pour valider les numéros de téléphone marocains
 * 
 * Format attendu : +212XXXXXXXXX
 * Exemples valides :
 * - +212612345678
 * - +212712345678
 * - +21261234567
 */
public class PhoneNumberValidator {
    
    /**
     * Pattern pour un numéro marocain valide
     * +212 suivi de 9 chiffres (commençant par 6 ou 7 généralement)
     */
    private static final Pattern MOROCCAN_PHONE_PATTERN = 
        Pattern.compile("^\\+212[67]\\d{8}$");
    
    /**
     * Vérifie si un numéro de téléphone est valide
     * 
     * @param phoneNumber Le numéro à valider
     * @return true si valide, false sinon
     */
    public static boolean isValid(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return MOROCCAN_PHONE_PATTERN.matcher(phoneNumber.trim()).matches();
    }
    
    /**
     * Normalise un numéro (supprime les espaces)
     */
    public static String normalize(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        return phoneNumber.trim().replaceAll("\\s+", "");
    }
}

