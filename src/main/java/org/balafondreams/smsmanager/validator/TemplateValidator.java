package org.balafondreams.smsmanager.validator;


import org.balafondreams.smsmanager.domain.exception.InvalidTemplateException;
import org.balafondreams.smsmanager.domain.models.template.MessageTemplateDTO;
import org.balafondreams.smsmanager.domain.models.template.TemplateVariableDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class TemplateValidator {

    public void validateTemplate(MessageTemplateDTO dto) {
        List<String> errors = new ArrayList<>();

        // Valider le contenu
        if (dto.getContent() != null) {
            Set<String> contentVariables = extractVariablesFromContent(dto.getContent());
            Set<String> declaredVariables = dto.getVariables().stream()
                    .map(TemplateVariableDTO::getKey)
                    .collect(Collectors.toSet());

            // Vérifier les variables non déclarées
            Set<String> undeclaredVariables = new HashSet<>(contentVariables);
            undeclaredVariables.removeAll(declaredVariables);
            if (!undeclaredVariables.isEmpty()) {
                errors.add("Undeclared variables found: " + undeclaredVariables);
            }

            // Vérifier les variables inutilisées
            Set<String> unusedVariables = new HashSet<>(declaredVariables);
            unusedVariables.removeAll(contentVariables);
            if (!unusedVariables.isEmpty()) {
                errors.add("Unused declared variables found: " + unusedVariables);
            }
        }

        if (!errors.isEmpty()) {
            throw new InvalidTemplateException(String.join(", ", errors));
        }
    }

    private Set<String> extractVariablesFromContent(String content) {
        Set<String> variables = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            variables.add(matcher.group(1));
        }

        return variables;
    }
}
