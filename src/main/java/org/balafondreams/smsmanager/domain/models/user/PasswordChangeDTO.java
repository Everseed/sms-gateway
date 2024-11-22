package org.balafondreams.smsmanager.domain.models.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeDTO {
    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 6, max = 100)
    private String newPassword;
}