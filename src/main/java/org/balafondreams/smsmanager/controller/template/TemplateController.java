package org.balafondreams.smsmanager.controller.template;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateCreateDTO;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateDTO;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateUpdateDTO;
import org.balafondreams.smsmanager.domain.models.template.ProcessTemplateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateUsageDTO;
import org.balafondreams.smsmanager.domain.models.template.ValidationResult;
import org.balafondreams.smsmanager.security.CurrentUser;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.balafondreams.smsmanager.service.template.MessageTemplateService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
public class TemplateController {
    private final MessageTemplateService templateService;

    /**
     * Récupère tous les templates paginés
     */
    @GetMapping
    public ResponseEntity<Page<MessageTemplateDTO>> getAllTemplates(
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(templateService.getUserTemplates(currentUser.getId(), pageable));
    }

    /**
     * Récupère un template spécifique
     */
    @GetMapping("/{id}")
    public ResponseEntity<MessageTemplateDTO> getTemplate(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(templateService.getTemplateDto(id, currentUser.getId()));
    }

    /**
     * Crée un nouveau template
     */
    @PostMapping
    public ResponseEntity<MessageTemplateDTO> createTemplate(
            @Valid @RequestBody MessageTemplateDTO createDTO, //MessageTemplateCreateDTO
            @CurrentUser UserPrincipal currentUser) {
        MessageTemplateDTO created = templateService.createTemplate(createDTO, currentUser.getId());
        return ResponseEntity.created(URI.create("/api/templates/" + created.getId()))
                .body(created);
    }

    /**
     * Met à jour un template existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<MessageTemplateDTO> updateTemplate(
            @PathVariable Long id,
            @Valid @RequestBody MessageTemplateUpdateDTO updateDTO,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(templateService.updateTemplate(id, updateDTO, currentUser.getId()));
    }

    /**
     * Supprime un template
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@templateService.canDeleteTemplate(#id, #currentUser.id)")
    public ResponseEntity<Void> deleteTemplate(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser) {
        templateService.deleteTemplate(id, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Traite un template avec les variables fournies
     */
    @PostMapping("/{id}/process")
    public ResponseEntity<String> processTemplate(
            @PathVariable Long id,
            @Valid @RequestBody ProcessTemplateDTO processDTO,
            @CurrentUser UserPrincipal currentUser) {
        String processedContent = templateService.processTemplate(
                id,
                processDTO.getVariables(),
                currentUser.getId()
        );
        return ResponseEntity.ok(processedContent);
    }

    /**
     * Recherche des templates
     */
    @GetMapping("/search")
    public ResponseEntity<Page<MessageTemplateDTO>> searchTemplates(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long categoryId,
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(templateService.searchTemplates(
                query,
                categoryId,
                currentUser.getId(),
                pageable
        ));
    }

    /**
     * Récupère les templates par catégorie
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<Page<MessageTemplateDTO>> getTemplatesByCategory(
            @PathVariable Long categoryId,
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(templateService.getTemplatesByCategory(categoryId, pageable));
    }

    /**
     * Duplique un template existant
     */
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<MessageTemplateDTO> duplicateTemplate(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser) {
        MessageTemplateDTO duplicated = templateService.duplicateTemplate(id, currentUser.getId());
        return ResponseEntity.created(URI.create("/api/templates/" + duplicated.getId()))
                .body(duplicated);
    }

    /**
     * Valide un template sans le sauvegarder
     */
    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validateTemplate(
            @Valid @RequestBody MessageTemplateCreateDTO templateDTO) {
        ValidationResult result = templateService.validateTemplateContent(templateDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Récupère les templates populaires
     */
    @GetMapping("/popular")
    public ResponseEntity<List<MessageTemplateDTO>> getPopularTemplates(
            @RequestParam(defaultValue = "5") int limit,
            @CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(templateService.getPopularTemplates(currentUser.getId(), limit));
    }

    /**
     * Récupère l'historique des utilisations d'un template
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<Page<TemplateUsageDTO>> getTemplateHistory(
            @PathVariable Long id,
            @CurrentUser UserPrincipal currentUser,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(templateService.getTemplateUsageHistory(id, currentUser.getId(), pageable));
    }
}
