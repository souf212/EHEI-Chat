package backend.eheichat.eheichat.service;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class WhatsAppService {

    @Value("${twilio.whatsapp.from}")
    private String whatsappFrom;

    private final RedisTemplate<String, String> redisTemplate;
    private final Random random = new Random();

    public WhatsAppService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String sendVerificationCode(String phoneNumber) {
        // Générer code à 6 chiffres
        String code = String.format("%06d", random.nextInt(999999));

        // Sauvegarder dans Redis (expire après 5 minutes)
        String redisKey = "verification:" + phoneNumber;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        // Envoyer via WhatsApp
        try {
            String formattedPhone = formatPhoneNumber(phoneNumber);
            String messageBody = String.format(
                    "🔐 EHEI Chat\n\n" +
                            "Votre code de vérification est : %s\n\n" +
                            "Valide pendant 5 minutes.\n" +
                            "Ne partagez ce code avec personne.",
                    code
            );

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formattedPhone),
                    new PhoneNumber(whatsappFrom),
                    messageBody
            ).create();

            log.info("Code envoyé avec succès. SID: {}", message.getSid());
            return message.getSid();

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi WhatsApp: {}", e.getMessage());
            throw new RuntimeException("Impossible d'envoyer le code");
        }
    }

    public boolean verifyCode(String phoneNumber, String code) {
        String redisKey = "verification:" + phoneNumber;
        String storedCode = redisTemplate.opsForValue().get(redisKey);

        if (storedCode != null && storedCode.equals(code)) {
            redisTemplate.delete(redisKey);
            log.info("Code vérifié avec succès pour: {}", phoneNumber);
            return true;
        }

        log.warn("Code incorrect ou expiré pour: {}", phoneNumber);
        return false;
    }

    private String formatPhoneNumber(String phone) {
        // Enlever espaces et ajouter +212
        String cleaned = phone.replaceAll("\\s", "");
        return "+212" + cleaned;
    }
}