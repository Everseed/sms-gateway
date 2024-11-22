package org.balafondreams.smsmanager.repository;

import jakarta.persistence.criteria.Join;
import org.balafondreams.smsmanager.domain.entities.user.Role;
import org.balafondreams.smsmanager.domain.entities.user.User;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class UserSpecification {
    public static Specification<User> withFilters(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Recherche par username
            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("username")),
                        "%" + criteria.getUsername().toLowerCase() + "%"
                ));
            }

            // Recherche par email
            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("email")),
                        "%" + criteria.getEmail().toLowerCase() + "%"
                ));
            }

            // Filtre sur l'état enabled/disabled
            if (criteria.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), criteria.getEnabled()));
            }

            // Filtre par rôle
            if (criteria.getRoleName() != null) {
                Join<User, Role> roleJoin = root.join("roles");
                predicates.add(cb.equal(roleJoin.get("name"), criteria.getRoleName()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
