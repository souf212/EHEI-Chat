package ma.ehei.chat.service;

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
        // Generate 6-digit code
        String code = String.format("%06d", random.nextInt(999999));

        // Store in Redis (expires after 5 minutes)
        String redisKey = "verification:" + phoneNumber;
        redisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);

        // Send via WhatsApp
        try {
            String formattedPhone = formatPhoneNumber(phoneNumber);
            String messageBody = String.format(
                    "🔐 EHEI Chat - Code de vérification\n\n" +
                            "Votre code de vérification est : *%s*\n\n" +
                            "Valide pendant 5 minutes.\n" +
                            "Ne partagez ce code avec personne.",
                    code
            );

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + formattedPhone),
                    new PhoneNumber(whatsappFrom),
                    messageBody
            ).create();

            log.info("Code envoyé avec succès via WhatsApp. SID: {}", message.getSid());
            return message.getSid();

        } catch (Exception e) {
            log.error("Erreur lors de l'envoi WhatsApp: {}", e.getMessage(), e);
            throw new RuntimeException("Impossible d'envoyer le code via WhatsApp");
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
        // Remove spaces and add +212 prefix
        String cleaned = phone.replaceAll("\\s", "");
        if (!cleaned.startsWith("+212")) {
            return "+212" + cleaned;
        }
        return cleaned;
    }
}
