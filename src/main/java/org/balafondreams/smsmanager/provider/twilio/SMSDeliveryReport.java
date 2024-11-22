package org.balafondreams.smsmanager.provider.twilio;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class SMSDeliveryReport {
    private String messageId;
    private String status;
    private LocalDateTime deliveredAt;
    private String errorCode;
    private String errorMessage;

    public static SMSDeliveryReport delivered(String messageId) {
        return SMSDeliveryReport.builder()
                .messageId(messageId)
                .status("DELIVERED")
                .deliveredAt(LocalDateTime.now())
                .build();
    }

    public static SMSDeliveryReport failed(String messageId, String errorCode, String errorMessage) {
        return SMSDeliveryReport.builder()
                .messageId(messageId)
                .status("FAILED")
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}