package org.balafondreams.smsmanager.domain.models.template;

import lombok.Data;

@Data
public class TemplateCategoryDTO {
    private Long id;
    private String name;
    private String description;
    private long templateCount;  // Nombre de templates dans cette catégorie
}
