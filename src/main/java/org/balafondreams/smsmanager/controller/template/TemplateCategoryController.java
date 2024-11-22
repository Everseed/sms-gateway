package org.balafondreams.smsmanager.controller.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.sms.CategoryStatsDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryCreateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateCategoryUpdateDTO;
import org.balafondreams.smsmanager.service.template.TemplateCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/template-categories")
@RequiredArgsConstructor
public class TemplateCategoryController {
    private final TemplateCategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<TemplateCategoryDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateCategoryDTO> getCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TemplateCategoryDTO> createCategory(
            @Valid @RequestBody TemplateCategoryCreateDTO createDto) {
        return ResponseEntity.ok(categoryService.createCategory(createDto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TemplateCategoryDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody TemplateCategoryUpdateDTO updateDto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, updateDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<TemplateCategoryDTO>> searchCategories(
            @RequestParam String query) {
        return ResponseEntity.ok(categoryService.searchCategories(query));
    }

    @GetMapping("/statistics")
    public ResponseEntity<List<CategoryStatsDTO>> getCategoryStatistics() {
        return ResponseEntity.ok(categoryService.getCategoryStatistics());
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TemplateCategoryDTO>> getPopularCategories(
            @RequestParam(defaultValue = "5") int minTemplates) {
        return ResponseEntity.ok(categoryService.getPopularCategories(minTemplates));
    }

    @DeleteMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cleanupUnusedCategories() {
        categoryService.cleanupUnusedCategories();
        return ResponseEntity.noContent().build();
    }

}