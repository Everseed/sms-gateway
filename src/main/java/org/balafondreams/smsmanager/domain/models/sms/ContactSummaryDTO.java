package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;

@Data
public class ContactSummaryDTO {
    private Long id;
    private String name;
    private String phoneNumber;
}
