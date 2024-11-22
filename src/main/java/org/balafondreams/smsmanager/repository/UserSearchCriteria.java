package org.balafondreams.smsmanager.repository;

import lombok.Builder;
import lombok.Data;
import org.balafondreams.smsmanager.domain.entities.user.ERole;

@Data
@Builder
public class UserSearchCriteria {
    private String username;
    private String email;
    private Boolean enabled;
    private ERole roleName;

    // Pour la pagination et le tri
    private String sortBy;
    private String sortDirection;
}
