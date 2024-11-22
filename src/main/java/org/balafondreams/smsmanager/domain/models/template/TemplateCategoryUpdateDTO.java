package org.balafondreams.smsmanager.domain.models.template;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TemplateCategoryUpdateDTO {
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;
}