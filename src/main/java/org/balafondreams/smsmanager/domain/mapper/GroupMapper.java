package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.sms.Group;
import org.balafondreams.smsmanager.domain.models.sms.GroupCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.GroupDTO;
import org.balafondreams.smsmanager.domain.models.sms.GroupSummaryDTO;
import org.balafondreams.smsmanager.domain.models.sms.GroupUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {ContactMapper.class}
)
public interface GroupMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "contactCount", expression = "java(group.getContacts().size())")
    GroupDTO toDto(Group group);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Group toEntity(GroupCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "contacts", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(GroupUpdateDTO updateDTO, @MappingTarget Group group);

    @Named("toGroupSummaryDTO")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "contactCount", expression = "java(group.getContacts().size())")
    GroupSummaryDTO toSummaryDto(Group group);
}
