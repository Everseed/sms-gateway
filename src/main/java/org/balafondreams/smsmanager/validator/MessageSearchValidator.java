package org.balafondreams.smsmanager.validator;

import org.balafondreams.smsmanager.domain.exception.InvalidSearchCriteriaException;
import org.balafondreams.smsmanager.repository.MessageSearchCriteria;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MessageSearchValidator {

    private static final int MAX_SEARCH_DAYS = 365; // Maximum 1 an de recherche
    private static final int MAX_PAGE_SIZE = 100;   // Maximum 100 éléments par page

    public void validate(MessageSearchCriteria criteria) {
        List<String> errors = new ArrayList<>();

        // Validation de la période de recherche
        if (criteria.getStartDate() != null && criteria.getEndDate() != null) {
            if (criteria.getStartDate().isAfter(criteria.getEndDate())) {
                errors.add("Start date must be before end date");
            }

            long daysBetween = ChronoUnit.DAYS.between(
                    criteria.getStartDate(),
                    criteria.getEndDate()
            );
            if (daysBetween > MAX_SEARCH_DAYS) {
                errors.add("Search period cannot exceed " + MAX_SEARCH_DAYS + " days");
            }
        }

        // Validation du numéro de téléphone
        if (criteria.getPhoneNumber() != null &&
                !criteria.getPhoneNumber().matches("^\\+?[1-9]\\d{1,14}$")) {
            errors.add("Invalid phone number format");
        }

        // Validation de la pagination
        if (criteria.getSize() != null && criteria.getSize() > MAX_PAGE_SIZE) {
            errors.add("Page size cannot exceed " + MAX_PAGE_SIZE);
        }

        // Validation du tri
        if (criteria.getSortBy() != null &&
                !isValidSortField(criteria.getSortBy())) {
            errors.add("Invalid sort field: " + criteria.getSortBy());
        }

        if (!errors.isEmpty()) {
            throw new InvalidSearchCriteriaException(errors);
        }
    }

    private boolean isValidSortField(String field) {
        return Arrays.asList(
                "createdAt",
                "scheduledAt",
                "sentAt",
                "status",
                "phoneNumber"
        ).contains(field);
    }
}
