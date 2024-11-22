package org.balafondreams.smsmanager.domain.models.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateDTO {
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Must be a valid email address")
    private String email;

    @Size(min = 6, max = 100, message = "New password must be between 6 and 100 characters")
    private String newPassword;

    @Size(min = 6, max = 100, message = "Current password must be between 6 and 100 characters")
    private String currentPassword;
}
