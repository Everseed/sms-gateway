package org.balafondreams.smsmanager.provider.twilio;

import jakarta.annotation.PostConstruct;
import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.provider.SMSProvider;
import org.balafondreams.smsmanager.provider.SMSRequest;
import org.balafondreams.smsmanager.provider.SMSResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

//@Slf4j
@Component
public class TwilioSMSProvider implements SMSProvider {

//    @Value("${twilio.account-sid}")
//    private String accountSid;
//
//    @Value("${twilio.auth-token}")
//    private String authToken;
//
//    @Value("${twilio.from-number}")
//    private String fromNumber;
//
//    private TwilioRestClient twilioClient;
//
//    @PostConstruct
//    public void init() {
//        twilioClient = new TwilioRestClient.Builder(accountSid, authToken).build();
//    }

    @Override
    public SMSResponse sendMessage(SMSRequest request) {
//        try {
//            // Création de l'objet PhoneNumber pour les numéros source et destination
//            PhoneNumber to = new PhoneNumber(request.getPhoneNumber());
//            PhoneNumber from = new PhoneNumber(fromNumber);
//
//            // Création du message
//            com.twilio.rest.api.v2010.account.Message message =
//                    com.twilio.rest.api.v2010.account.Message.creator(
//                                    accountSid,    // Le SID du compte Twilio
//                                    to.toString(), // Numéro de destination
//                                    from.toString(), // Numéro d'envoi
//                                    request.getContent() // Contenu du message
//                            )
//                            .create(twilioClient);
//
//            // Log de succès
//            log.info("SMS sent successfully via Twilio. MessageSid: {}", message.getSid());
//
//            return SMSResponse.success(message.getSid());
//
//        } catch (Exception e) {
//            // Log de l'erreur
//            log.error("Failed to send SMS via Twilio", e);
//
//            return SMSResponse.error(
//                    "TWILIO_ERROR",
//                    "Failed to send SMS: " + e.getMessage()
//            );
//        }
        return null;
    }

//    /**
//     * Vérifie si les paramètres de configuration sont valides
//     */
//    public boolean isConfigured() {
//        return accountSid != null && !accountSid.isEmpty() &&
//                authToken != null && !authToken.isEmpty() &&
//                fromNumber != null && !fromNumber.isEmpty();
//    }
//
//    /**
//     * Formate le numéro de téléphone selon les exigences de Twilio
//     */
//    private String formatPhoneNumber(String phoneNumber) {
//        // Supprime tous les caractères non numériques
//        String cleanNumber = phoneNumber.replaceAll("[^\\d+]", "");
//
//        // Ajoute le + si non présent
//        if (!cleanNumber.startsWith("+")) {
//            cleanNumber = "+" + cleanNumber;
//        }
//
//        return cleanNumber;
//    }
}