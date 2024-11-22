package org.balafondreams.smsmanager.domain.mapper;

import org.balafondreams.smsmanager.domain.entities.template.MessageTemplate;
import org.balafondreams.smsmanager.domain.entities.template.TemplateVariable;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateVariableDTO;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        uses = {UserMapper.class, TemplateCategoryMapper.class}
)
public interface MessageTemplateMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    MessageTemplateDTO toDto(MessageTemplate template);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    MessageTemplate toEntity(MessageTemplateDTO dto);

    @Mapping(target = "template", ignore = true)
    TemplateVariable variableDtoToEntity(TemplateVariableDTO dto);

    @Mapping(target = "templateId", ignore = true)
    TemplateVariableDTO variableToDto(TemplateVariable variable);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "messages", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(MessageTemplateDTO dto, @MappingTarget MessageTemplate template);

    @AfterMapping
    default void setTemplateForVariables(@MappingTarget MessageTemplate template) {
        if (template.getVariables() != null) {
            template.getVariables().forEach(variable -> variable.setTemplate(template));
        }
    }
}