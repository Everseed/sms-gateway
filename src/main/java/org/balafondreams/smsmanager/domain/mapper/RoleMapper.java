package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.user.ERole;
import org.balafondreams.smsmanager.domain.entities.user.Role;
import org.balafondreams.smsmanager.domain.models.user.RoleDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ValueMapping;

import java.util.Set;
@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDTO toDto(Role role);

    Set<RoleDTO> toDtoSet(Set<Role> roles);

    @ValueMapping(source = "ROLE_USER", target = "USER")
    @ValueMapping(source = "ROLE_ADMIN", target = "ADMIN")
    @ValueMapping(source = "ROLE_MODERATOR", target = "MODERATOR")
    String roleEnumToString(ERole roleEnum);

    @ValueMapping(source = "USER", target = "ROLE_USER")
    @ValueMapping(source = "ADMIN", target = "ROLE_ADMIN")
    @ValueMapping(source = "MODERATOR", target = "ROLE_MODERATOR")
    ERole stringToRoleEnum(String role);

    @Named("toRoleNames")
    default Set<String> rolesToNames(Set<Role> roles) {
        if (roles == null) return null;
        return roles.stream()
                .map(role -> roleEnumToString(role.getName()))
                .collect(java.util.stream.Collectors.toSet());
    }
}
