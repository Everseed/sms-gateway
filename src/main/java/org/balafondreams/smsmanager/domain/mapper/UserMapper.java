package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.user.User;
import org.balafondreams.smsmanager.domain.models.user.UserDTO;
import org.balafondreams.smsmanager.domain.models.user.UserRegistrationDTO;
import org.balafondreams.smsmanager.domain.models.user.UserUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface  UserMapper {
    //@Mapping(target = "password", ignore = true)
    @Mapping(source = "roles", target = "roles", qualifiedByName = "toRoleNames")
    UserDTO toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    User toEntity(UserRegistrationDTO registrationDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    void updateEntity(UserDTO dto, @MappingTarget User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "enabled", ignore = true)
    void updateEntity(UserUpdateDTO updateDTO, @MappingTarget User user);
}
