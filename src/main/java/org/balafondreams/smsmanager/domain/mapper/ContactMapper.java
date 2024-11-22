package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.sms.Contact;
import org.balafondreams.smsmanager.domain.models.sms.ContactCreateDTO;
import org.balafondreams.smsmanager.domain.models.sms.ContactDTO;
import org.balafondreams.smsmanager.domain.models.sms.ContactSummaryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ContactMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "groups", target = "groups")
    ContactDTO toDto(Contact contact);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Contact toEntity(ContactCreateDTO createDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "groups", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ContactDTO dto, @MappingTarget Contact contact);

    @Named("toContactSummaryDTO")
    ContactSummaryDTO toSummaryDto(Contact contact);

    @IterableMapping(qualifiedByName = "toContactSummaryDTO")
    Set<ContactSummaryDTO> toSummaryDtoSet(Set<Contact> contacts);
}
