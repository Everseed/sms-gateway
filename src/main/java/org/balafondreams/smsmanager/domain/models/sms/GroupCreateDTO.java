package org.balafondreams.smsmanager.domain.models.sms;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.util.Set;

@Data
public class GroupCreateDTO {
    @NotBlank(message = "Group name is required")
    @Size(min = 2, max = 100, message = "Group name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    private Set<Long> contactIds;
}
