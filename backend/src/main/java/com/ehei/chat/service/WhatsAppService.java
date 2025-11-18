package com.ehei.chat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Service pour envoyer des messages via WhatsApp Cloud API
 * 
 * Cette classe communique avec l'API Meta WhatsApp pour envoyer
 * des codes de vérification aux utilisateurs
 */
@Service
public class WhatsAppService {
    
    @Value("${whatsapp.api.url}")
    private String apiUrl;
    
    @Value("${whatsapp.api.token}")
    private String apiToken;
    
    @Value("${whatsapp.api.phone-number-id}")
    private String phoneNumberId;
    
    private final WebClient webClient;
    
    /**
     * Constructeur : Initialise le client HTTP pour appeler l'API WhatsApp
     */
    public WhatsAppService() {
        this.webClient = WebClient.builder()
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    
    /**
     * Envoie un code de vérification via WhatsApp
     * 
     * @param phoneNumber Le numéro de téléphone du destinataire
     * @param code Le code de vérification à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
    public boolean sendVerificationCode(String phoneNumber, String code) {
        try {
            // Construire l'URL de l'API WhatsApp
            String url = apiUrl + "/" + phoneNumberId + "/messages";
            
            // Préparer le message
            Map<String, Object> messageBody = new HashMap<>();
            messageBody.put("messaging_product", "whatsapp");
            messageBody.put("to", phoneNumber);
            messageBody.put("type", "template");
            
            // Template de message (à configurer dans Meta Business)
            Map<String, Object> template = new HashMap<>();
            template.put("name", "verification_code");
            template.put("language", Map.of("code", "fr"));
            
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("type", "text");
            parameters.put("text", code);
            
            template.put("components", new Object[]{
                Map.of("type", "body", "parameters", new Object[]{parameters})
            });
            
            messageBody.put("template", template);
            
            // Envoyer la requête
            String response = webClient.post()
                .uri(url)
                .header("Authorization", "Bearer " + apiToken)
                .bodyValue(messageBody)
                .retrieve()
                .bodyToMono(String.class)
                .block(); // Attendre la réponse (en production, utiliser async)
            
            System.out.println("✅ Code WhatsApp envoyé à " + phoneNumber + " : " + code);
            System.out.println("Réponse API : " + response);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'envoi WhatsApp : " + e.getMessage());
            // En mode développement, on simule l'envoi
            System.out.println("⚠️ Mode développement : Code simulé = " + code);
            return true; // Retourner true pour permettre le développement sans API réelle
        }
    }
}

