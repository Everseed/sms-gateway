package org.balafondreams.smsmanager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.domain.exception.SMSGatewayException;
import org.balafondreams.smsmanager.provider.bird.MessageBirdSMSProvider;
import org.balafondreams.smsmanager.provider.MockSMSProvider;
import org.balafondreams.smsmanager.provider.SMSProvider;
import org.balafondreams.smsmanager.provider.SMSRequest;
import org.balafondreams.smsmanager.provider.SMSResponse;
import org.balafondreams.smsmanager.provider.twilio.TwilioSMSProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SMSGatewayService {

    @Value("${sms.provider}")
    private String smsProvider;

    private final TwilioSMSProvider twilioProvider;
    private final MessageBirdSMSProvider messageBirdProvider;
    private final MockSMSProvider mockProvider;

    /**
     * Envoie un SMS via le fournisseur configuré
     */
    public void sendSMS(Message message) {
        SMSProvider provider = getProvider();
        try {
            SMSResponse response = provider.sendMessage(
                    new SMSRequest(
                            message.getPhoneNumber(),
                            message.getContent()
                    )
            );

            handleSMSResponse(message, response);

        } catch (Exception e) {
            log.error("Failed to send SMS", e);
            throw new SMSGatewayException("Failed to send SMS: " + e.getMessage(), e);
        }
    }

    private SMSProvider getProvider() {
        return switch (smsProvider.toLowerCase()) {
            case "twilio" -> twilioProvider;
            case "messagebird" -> messageBirdProvider;
            case "mock" -> mockProvider;
            default -> throw new SMSGatewayException("Unknown SMS provider: " + smsProvider);
        };
    }

    private void handleSMSResponse(Message message, SMSResponse response) {
        if (!response.isSuccess()) {
            message.setErrorCode(response.getErrorCode());
            message.setErrorMessage(response.getErrorMessage());
            throw new SMSGatewayException(
                    "SMS sending failed: " + response.getErrorMessage()
            );
        }
    }
}
