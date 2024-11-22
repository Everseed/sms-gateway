package org.balafondreams.smsmanager.domain.models.sms;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class ContactCreateDTO {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$")
    private String phoneNumber;

    @Email
    private String email;

    private Set<Long> groupIds;
}