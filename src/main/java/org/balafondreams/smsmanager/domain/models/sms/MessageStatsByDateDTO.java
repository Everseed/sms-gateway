package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Value;
import java.time.LocalDate;

@Value
public class MessageStatsByDateDTO {
    LocalDate date;
    long totalCount;
    long sentCount;
    long failedCount;
}
