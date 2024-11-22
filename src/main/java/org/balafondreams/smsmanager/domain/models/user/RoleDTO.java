package org.balafondreams.smsmanager.domain.models.user;

import lombok.Data;

@Data
public class RoleDTO {
    private Long id;
    private String name;  // USER, ADMIN, MODERATOR (sans le préfixe ROLE_)
}