package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.template.MessageTemplate;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class MessageTemplateSpecification {

    public static Specification<MessageTemplate> withCriteria(MessageTemplateSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtre sur l'utilisateur
            if (criteria.getUserId() != null) {
                predicates.add(cb.or(
                        cb.equal(root.get("user").get("id"), criteria.getUserId()),
                        cb.isNull(root.get("user"))
                ));
            }

            // Filtre sur la catégorie
            if (criteria.getCategoryId() != null) {
                predicates.add(cb.equal(
                        root.get("category").get("id"),
                        criteria.getCategoryId()
                ));
            }

            // Filtre sur le statut actif
            predicates.add(cb.equal(root.get("isActive"), true));

            // Recherche textuelle
            if (criteria.getQuery() != null && !criteria.getQuery().isEmpty()) {
                String searchTerm = "%" + criteria.getQuery().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), searchTerm),
                        cb.like(cb.lower(root.get("content")), searchTerm),
                        cb.like(cb.lower(root.get("description")), searchTerm)
                ));
            }

            // Filtre sur les variables
            if (criteria.getVariableType() != null) {
                predicates.add(cb.equal(
                        root.join("variables").get("type"),
                        criteria.getVariableType()
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}