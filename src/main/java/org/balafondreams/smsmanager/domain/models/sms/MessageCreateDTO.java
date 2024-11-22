package org.balafondreams.smsmanager.domain.models.sms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MessageCreateDTO {
    @NotBlank
    private String content;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;

    private LocalDateTime scheduledAt;

    private String type = "SMS";

    @Min(1)
    private Long templateId;

    private Map<String, String> templateVariables;
}
