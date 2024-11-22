package org.balafondreams.smsmanager.service.template;

import org.balafondreams.smsmanager.domain.entities.template.TemplateCategory;
import org.balafondreams.smsmanager.domain.exception.AccessDeniedException;
import org.balafondreams.smsmanager.domain.exception.DuplicateResourceException;
import org.balafondreams.smsmanager.domain.exception.InvalidTemplateException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.mapper.MessageTemplateMapper;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateCreateDTO;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateDTO;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateUpdateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateUsageDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateVariableDTO;
import org.balafondreams.smsmanager.domain.models.template.ValidationResult;
import org.balafondreams.smsmanager.repository.MessageTemplateRepository;
import org.balafondreams.smsmanager.repository.TemplateCategoryRepository;
import org.balafondreams.smsmanager.service.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.template.MessageTemplate;
import org.balafondreams.smsmanager.domain.entities.template.TemplateVariable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MessageTemplateService {
    private final MessageTemplateRepository templateRepository;
    private final TemplateCategoryRepository categoryRepository;
    private final MessageTemplateMapper templateMapper;
    private final UserService userService;

    /**
     * Récupère un template avec ses variables
     */
    public MessageTemplate getTemplate(Long templateId, Long userId) {
        return templateRepository.findByIdWithVariables(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));
    }

    /**
     * Récupère un template en DTO
     */
    public MessageTemplateDTO getTemplateDto(Long templateId, Long userId) {
        MessageTemplate template = getTemplate(templateId, userId);
        return templateMapper.toDto(template);
    }

    /**
     * Vérifie si l'utilisateur a accès au template
     */
    public boolean canAccessTemplate(Long templateId, Long userId) {
        MessageTemplate template = getTemplate(templateId, userId);
        return template.getUser() == null || // Template global
                template.getUser().getId().equals(userId); // Template de l'utilisateur
    }

    /**
     * Vérifie si l'utilisateur peut modifier le template
     */
    public boolean canModifyTemplate(Long templateId, Long userId) {
        MessageTemplate template = getTemplate(templateId, userId);
        return template.getUser() != null &&
                template.getUser().getId().equals(userId);
    }

    // Ajouter cette méthode dans le repository si elle n'existe pas
    /* Dans MessageTemplateRepository :
    @Query("SELECT t FROM MessageTemplate t " +
           "LEFT JOIN FETCH t.variables " +
           "WHERE t.id = :id AND t.isActive = true")
    Optional<MessageTemplate> findByIdWithVariables(@Param("id") Long id);
    */

    /**
     * Validation d'accès avec exception
     */
    private void validateTemplateAccess(Long templateId, Long userId) {
        if (!canAccessTemplate(templateId, userId)) {
            throw new AccessDeniedException("User does not have access to this template");
        }
    }

    /**
     * Validation de modification avec exception
     */
    private void validateTemplateModification(Long templateId, Long userId) {
        if (!canModifyTemplate(templateId, userId)) {
            throw new AccessDeniedException("User cannot modify this template");
        }
    }



    /**
     * Récupère tous les modèles de messages disponibles pour un utilisateur
     */
    public Page<MessageTemplateDTO> getUserTemplates(Long userId, Pageable pageable) {
        return templateRepository.findAllAvailableTemplates(userId, pageable)
                .map(templateMapper::toDto);
    }

    /**
     * Récupère les modèles par catégorie
     */
    public Page<MessageTemplateDTO> getTemplatesByCategory(Long categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
        return templateRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(templateMapper::toDto);
    }

    /**
     * Crée un nouveau modèle de message
     */
    public MessageTemplateDTO createTemplate(MessageTemplateDTO dto, Long userId) {
        validateTemplateData(dto);

        MessageTemplate template = templateMapper.toEntity(dto);

        // Associer la catégorie si spécifiée
        if (dto.getCategoryId() != null) {
            TemplateCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            template.setCategory(category);
        }

        // Associer l'utilisateur
        template.setUser(userService.getCurrentUser());

        // Valider les variables du template
        validateTemplateVariables(template);

        MessageTemplate saved = templateRepository.save(template);
        return templateMapper.toDto(saved);
    }

    /**
     * Met à jour un modèle existant
     */
    public MessageTemplateDTO updateTemplate(Long id, MessageTemplateUpdateDTO dto, Long userId) {
       // TODO: update the content
        return null;
    }

    /**
     * Met à jour un modèle existant
     */
    public MessageTemplateDTO updateTemplate(Long id, MessageTemplateDTO dto, Long userId) {
        MessageTemplate existing = templateRepository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));

        validateTemplateData(dto);

        templateMapper.updateEntityFromDto(dto, existing);

        // Mettre à jour la catégorie si spécifiée
        if (dto.getCategoryId() != null &&
                !Objects.equals(existing.getCategory().getId(), dto.getCategoryId())) {
            TemplateCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + dto.getCategoryId()));
            existing.setCategory(category);
        }

        validateTemplateVariables(existing);

        MessageTemplate updated = templateRepository.save(existing);
        return templateMapper.toDto(updated);
    }

    /**
     * Supprime logiquement un modèle
     */
    public void deleteTemplate(Long id, Long userId) {
        MessageTemplate template = templateRepository.findByIdAndUserIdAndIsActiveTrue(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + id));

        template.setActive(false);
        templateRepository.save(template);
    }

    /**
     * Traite un template avec les variables fournies
     */
    // Mise à jour de la méthode processTemplate pour utiliser ces nouvelles méthodes
    public String processTemplate(Long templateId, Map<String, String> variables, Long userId) {
        validateTemplateAccess(templateId, userId);
        MessageTemplate template = getTemplate(templateId, userId);

        if (!validateTemplateVariables(template, variables)) {
            throw new InvalidTemplateException("Invalid template variables provided");
        }

        String content = template.getContent();
        for (TemplateVariable var : template.getVariables()) {
            String value = variables.getOrDefault(var.getKey(), var.getDefaultValue());
            if (value == null) {
                throw new InvalidTemplateException("Missing required variable: " + var.getKey());
            }
            content = content.replace("${" + var.getKey() + "}", value);
        }

        return content;
    }
    /**
     * Recherche de templates
     */
    public Page<MessageTemplateDTO> searchTemplates(String query, Long categoryId, Long userId, Pageable pageable) {
        return templateRepository.searchTemplates(query, categoryId, userId, pageable)
                .map(templateMapper::toDto);
    }

    /**
     * Copie un template existant
     */
    public MessageTemplateDTO duplicateTemplate(Long templateId, Long userId) {
        MessageTemplate source = templateRepository.findByIdAndUserIdAndIsActiveTrue(templateId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Template not found with id: " + templateId));

        MessageTemplate copy = new MessageTemplate();
        copy.setName(source.getName() + " (Copy)");
        copy.setContent(source.getContent());
        copy.setDescription(source.getDescription());
        copy.setCategory(source.getCategory());
        copy.setUser(userService.getCurrentUser());

        // Copier les variables
        Set<TemplateVariable> copiedVariables = source.getVariables().stream()
                .map(var -> {
                    TemplateVariable copyVar = new TemplateVariable();
                    copyVar.setKey(var.getKey());
                    copyVar.setDefaultValue(var.getDefaultValue());
                    copyVar.setType(var.getType());
                    copyVar.setDescription(var.getDescription());
                    copyVar.setTemplate(copy);
                    return copyVar;
                })
                .collect(Collectors.toSet());
        copy.setVariables(copiedVariables);

        MessageTemplate saved = templateRepository.save(copy);
        return templateMapper.toDto(saved);
    }

    // Méthodes privées d'aide

    private void validateTemplateData(MessageTemplateDTO dto) {
        // Vérifier le nom unique pour l'utilisateur
        if (templateRepository.existsByNameAndUserIdAndIsActiveTrue(dto.getName(), userService.getCurrentUser().getId())) {
            throw new DuplicateResourceException("Template with name '" + dto.getName() + "' already exists");
        }

        // Vérifier la présence de variables dans le contenu
        Set<String> contentVariables = extractVariablesFromContent(dto.getContent());
        Set<String> declaredVariables = dto.getVariables().stream()
                .map(TemplateVariableDTO::getKey)
                .collect(Collectors.toSet());

        // Toutes les variables utilisées doivent être déclarées
        if (!declaredVariables.containsAll(contentVariables)) {
            Set<String> undeclaredVariables = new HashSet<>(contentVariables);
            undeclaredVariables.removeAll(declaredVariables);
            throw new InvalidTemplateException("Undeclared variables found: " + undeclaredVariables);
        }
    }

    private boolean validateTemplateVariables(MessageTemplate template, Map<String, String> variables) {
        for (TemplateVariable var : template.getVariables()) {
            String value = variables.get(var.getKey());
            if (value == null && var.getDefaultValue() == null) {
                return false;
            }
            if (value != null && !isValidVariableValue(value, var.getType())) {
                return false;
            }
        }
        return true;
    }

    private void validateTemplateVariables(MessageTemplate template) {
        Set<String> uniqueKeys = new HashSet<>();
        for (TemplateVariable var : template.getVariables()) {
            if (!uniqueKeys.add(var.getKey())) {
                throw new InvalidTemplateException("Duplicate variable key found: " + var.getKey());
            }
            if (var.getDefaultValue() != null && !isValidVariableValue(var.getDefaultValue(), var.getType())) {
                throw new InvalidTemplateException(
                        "Invalid default value for variable: " + var.getKey() +
                                " of type: " + var.getType()
                );
            }
        }
    }

    private String processTemplateContent(MessageTemplate template, Map<String, String> variables) {
        String content = template.getContent();
        for (TemplateVariable var : template.getVariables()) {
            String value = variables.getOrDefault(var.getKey(), var.getDefaultValue());
            if (value == null) {
                throw new InvalidTemplateException("Missing required variable: " + var.getKey());
            }
            content = content.replace("${" + var.getKey() + "}", value);
        }
        return content;
    }

    private Set<String> extractVariablesFromContent(String content) {
        Set<String> variables = new HashSet<>();
        int start = 0;
        while ((start = content.indexOf("${", start)) != -1) {
            int end = content.indexOf("}", start);
            if (end == -1) break;
            variables.add(content.substring(start + 2, end));
            start = end + 1;
        }
        return variables;
    }

    public ValidationResult validateTemplateContent(MessageTemplateCreateDTO templateDTO) {
        List<String> errors = new ArrayList<>();
        Set<String> detectedVariables = new HashSet<>();

        try {
            // Validation du nom
            if (templateDTO.getName() == null || templateDTO.getName().trim().isEmpty()) {
                errors.add("Template name is required");
            } else if (templateDTO.getName().length() < 2 || templateDTO.getName().length() > 100) {
                errors.add("Template name must be between 2 and 100 characters");
            }

            // Validation du contenu
            if (templateDTO.getContent() == null || templateDTO.getContent().trim().isEmpty()) {
                errors.add("Template content is required");
            } else if (templateDTO.getContent().length() > 1000) {
                errors.add("Template content cannot exceed 1000 characters");
            } else {
                // Extraction et validation des variables
                detectedVariables = extractVariablesFromContent(templateDTO.getContent());

                // Vérification des variables déclarées vs utilisées
                Set<String> declaredVariables = templateDTO.getVariables().stream()
                        .map(var -> var.getKey())
                        .collect(Collectors.toSet());

                // Variables utilisées mais non déclarées
                Set<String> undeclaredVariables = new HashSet<>(detectedVariables);
                undeclaredVariables.removeAll(declaredVariables);
                if (!undeclaredVariables.isEmpty()) {
                    errors.add("Undeclared variables found: " + undeclaredVariables);
                }

                // Variables déclarées mais non utilisées
                Set<String> unusedVariables = new HashSet<>(declaredVariables);
                unusedVariables.removeAll(detectedVariables);
                if (!unusedVariables.isEmpty()) {
                    errors.add("Unused declared variables found: " + unusedVariables);
                }
            }

            // Validation des variables
            if (templateDTO.getVariables() != null) {
                for (TemplateVariableDTO var : templateDTO.getVariables()) {
                    validateVariable(var, errors);
                }
            }

            // Validation de la catégorie si spécifiée
            if (templateDTO.getCategoryId() != null &&
                    !categoryRepository.existsById(templateDTO.getCategoryId())) {
                errors.add("Category not found with id: " + templateDTO.getCategoryId());
            }

        } catch (Exception e) {
            errors.add("Validation error: " + e.getMessage());
        }

        return ValidationResult.builder()
                .valid(errors.isEmpty())
                .errors(errors)
                .detectedVariables(detectedVariables)
                .build();
    }

    private void validateVariable(TemplateVariableDTO var, List<String> errors) {
        if (var.getKey() == null || var.getKey().trim().isEmpty()) {
            errors.add("Variable key is required");
            return;
        }

        if (!isValidVariableKey(var.getKey())) {
            errors.add("Invalid variable key format: " + var.getKey() +
                    ". Must contain only letters, numbers, and underscores");
        }

        if (var.getType() == null) {
            errors.add("Variable type is required for: " + var.getKey());
            return;
        }

        // Validation du type de variable
        try {
            TemplateVariable.VariableType.valueOf(var.getType());
        } catch (IllegalArgumentException e) {
            errors.add("Invalid variable type: " + var.getType() +
                    " for variable: " + var.getKey());
        }

        // Validation de la valeur par défaut si présente
        if (var.getDefaultValue() != null) {
            if (!isValidVariableValue(var.getDefaultValue(),
                    TemplateVariable.VariableType.valueOf(var.getType()))) {
                errors.add("Invalid default value for variable: " + var.getKey() +
                        " of type: " + var.getType());
            }
        }
    }

    private boolean isValidVariableKey(String key) {
        return key.matches("^[a-zA-Z][a-zA-Z0-9_]*$");
    }

    private boolean isValidVariableValue(String value, TemplateVariable.VariableType type) {
        if (value == null) return true;

        switch (type) {
            case NUMBER:
                return value.matches("\\d+");
            case DATE:
                return value.matches("\\d{4}-\\d{2}-\\d{2}");
            case BOOLEAN:
                return value.matches("true|false");
            case TEXT:
            case CHOICE:
                return true;
            default:
                return false;
        }
    }

    public List<MessageTemplateDTO> getPopularTemplates(Long id, int limit) {
        return new ArrayList<>(0);
    }

    public Page<TemplateUsageDTO> getTemplateUsageHistory(Long id, Long id1, Pageable pageable) {
        return null;
    }
}