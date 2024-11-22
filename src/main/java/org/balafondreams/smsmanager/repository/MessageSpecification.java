package org.balafondreams.smsmanager.repository;

import jakarta.persistence.criteria.Predicate;
import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class MessageSpecification {

    public static Specification<Message> withCriteria(MessageSearchCriteria criteria, Long userId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Utilisateur
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            // Statut
            if (criteria.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), criteria.getStatus()));
            }

            // Numéro de téléphone
            if (criteria.getPhoneNumber() != null) {
                predicates.add(cb.equal(root.get("phoneNumber"), criteria.getPhoneNumber()));
            }

            // Période
            if (criteria.getStartDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), criteria.getStartDate()));
            }
            if (criteria.getEndDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), criteria.getEndDate()));
            }

            // Template
            if (criteria.getTemplateId() != null) {
                predicates.add(cb.equal(root.get("template").get("id"), criteria.getTemplateId()));
            }

            // Recherche dans le contenu
            if (criteria.getContentSearch() != null) {
                predicates.add(cb.like(
                        cb.lower(root.get("content")),
                        "%" + criteria.getContentSearch().toLowerCase() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}