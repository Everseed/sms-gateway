package org.balafondreams.smsmanager.service.template;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.template.TemplateCategory;
import org.balafondreams.smsmanager.domain.exception.DuplicateResourceException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.mapper.TemplateCategoryMapper;
import org.balafondreams.smsmanager.domain.models.sms.CategoryStatsDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryCreateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryUpdateDTO;
import org.balafondreams.smsmanager.repository.TemplateCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TemplateCategoryService {
    private final TemplateCategoryRepository templateCategoryRepository;
    private final TemplateCategoryMapper templateCategoryMapper;

    public List<TemplateCategoryDTO> getAllCategories() {
        return templateCategoryRepository.findAllByOrderByNameAsc()
                .stream()
                .map(item -> templateCategoryMapper.toDto((TemplateCategory) item))
                .collect(Collectors.toList());
    }

    public TemplateCategoryDTO getCategory(Long id) {
        return templateCategoryRepository.findById(id)
                .map(templateCategoryMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public TemplateCategoryDTO updateCategory(Long id, TemplateCategoryUpdateDTO updateDto) {
        TemplateCategory category = templateCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        // Vérifier si le nouveau nom n'est pas déjà utilisé par une autre catégorie
        if (updateDto.getName() != null &&
                !updateDto.getName().equals(category.getName()) &&
                templateCategoryRepository.existsByName(updateDto.getName())) {
            throw new DuplicateResourceException("Category already exists with name: " + updateDto.getName());
        }

        templateCategoryMapper.updateEntityFromDto(updateDto, category);
        TemplateCategory updatedCategory = templateCategoryRepository.save(category);
        return templateCategoryMapper.toDto(updatedCategory);
    }

    public void deleteCategory(Long id) {
        TemplateCategory category = templateCategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getTemplates().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with existing templates");
        }

        templateCategoryRepository.delete(category);
    }

    public List<TemplateCategoryDTO> searchCategories(String query) {
        return templateCategoryRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(templateCategoryMapper::toDto)
                .collect(Collectors.toList());
    }
    public boolean canDeleteCategory(Long id) {
        return !templateCategoryRepository.isCategoryUsed(id);
    }

    public List<CategoryStatsDTO> getCategoryStatistics() {
        return templateCategoryRepository.getCategoryStatistics();
    }

    public void cleanupUnusedCategories() {
        List<TemplateCategory> emptyCategories = templateCategoryRepository.findEmptyCategories();
        templateCategoryRepository.deleteAll(emptyCategories);
    }

    public List<TemplateCategoryDTO> getPopularCategories(int minTemplates) {
        return templateCategoryRepository.findCategoriesWithMinTemplates(minTemplates)
                .stream()
                .map(templateCategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    // Méthode de création avec vérification insensible à la casse
    public TemplateCategoryDTO createCategory(TemplateCategoryCreateDTO createDto) {
        String normalizedName = createDto.getName().trim();

        // Vérification insensible à la casse
        if (templateCategoryRepository.findByNameIgnoreCase(normalizedName).isPresent()) {
            throw new DuplicateResourceException("Category already exists with name: " + normalizedName);
        }

        TemplateCategory category = templateCategoryMapper.toEntity(createDto);
        category.setName(normalizedName); // Assure un nom normalisé

        TemplateCategory savedCategory = templateCategoryRepository.save(category);
        return templateCategoryMapper.toDto(savedCategory);
    }
}