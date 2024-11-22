package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.template.TemplateCategory;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryCreateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryUpdateDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TemplateCategoryMapper {

    @Mapping(target = "templateCount", expression = "java(category.getTemplates().size())")
    TemplateCategoryDTO toDto(TemplateCategory category);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "templates", ignore = true)
    TemplateCategory toEntity(TemplateCategoryCreateDTO createDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "templates", ignore = true)
    void updateEntityFromDto(TemplateCategoryUpdateDTO updateDto, @MappingTarget TemplateCategory category);
}
