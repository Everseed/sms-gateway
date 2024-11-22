package org.balafondreams.smsmanager.provider.twilio;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "twilio")
public class TwilioConfig {

    @NotEmpty(message = "Twilio Account SID is required")
    private String accountSid;

    @NotEmpty(message = "Twilio Auth Token is required")
    private String authToken;

    @NotEmpty(message = "Twilio From Number is required")
    private String fromNumber;

    private int maxRetries = 3;

    private long retryDelayMs = 1000;

    private boolean enableDeliveryReports = true;
}
