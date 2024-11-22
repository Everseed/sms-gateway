package org.balafondreams.smsmanager.provider;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MockSMSProvider implements SMSProvider {

    @Override
    public SMSResponse sendMessage(SMSRequest request) {
        log.info("Mock SMS sent to {}: {}",
                request.getPhoneNumber(),
                request.getContent()
        );

        // Simuler un délai aléatoire
        try {
            Thread.sleep((long) (Math.random() * 1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Simuler un échec aléatoire (10% de chance)
        if (Math.random() < 0.1) {
            return SMSResponse.error(
                    "MOCK_ERROR",
                    "Random failure for testing purposes"
            );
        }

        return SMSResponse.success(UUID.randomUUID().toString());
    }
}