package org.balafondreams.smsmanager.domain.models.user;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private boolean enabled;
    private Set<String> roles;
}