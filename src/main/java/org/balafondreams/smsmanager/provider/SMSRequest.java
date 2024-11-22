package org.balafondreams.smsmanager.provider;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class SMSRequest {
    private String phoneNumber;
    private String content;
}
