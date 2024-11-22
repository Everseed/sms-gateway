package org.balafondreams.smsmanager.provider;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SMSResponse {
    private boolean success;
    private String messageId;
    private String errorCode;
    private String errorMessage;

    public static SMSResponse success(String messageId) {
        return SMSResponse.builder()
                .success(true)
                .messageId(messageId)
                .build();
    }

    public static SMSResponse error(String errorCode, String errorMessage) {
        return SMSResponse.builder()
                .success(false)
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();
    }
}