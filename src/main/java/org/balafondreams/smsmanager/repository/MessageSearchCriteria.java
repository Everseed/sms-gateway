package org.balafondreams.smsmanager.repository;

import lombok.Builder;
import lombok.Data;
import org.balafondreams.smsmanager.domain.entities.sms.MessageStatus;
import org.balafondreams.smsmanager.domain.entities.sms.MessageType;

import java.time.LocalDateTime;

@Data
@Builder
public class MessageSearchCriteria {
    private MessageStatus status;
    private MessageType type;
    private String phoneNumber;
    private Long templateId;
    private Long conversationId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String contentSearch;

    // Pour la pagination et le tri
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;

    public static class MessageSearchCriteriaBuilder {
        // Méthodes utilitaires pour le builder

        public MessageSearchCriteriaBuilder dateRange(LocalDateTime start, LocalDateTime end) {
            this.startDate = start;
            this.endDate = end;
            return this;
        }

        public MessageSearchCriteriaBuilder lastDays(int days) {
            this.startDate = LocalDateTime.now().minusDays(days);
            this.endDate = LocalDateTime.now();
            return this;
        }

        public MessageSearchCriteriaBuilder thisMonth() {
            LocalDateTime now = LocalDateTime.now();
            this.startDate = now.withDayOfMonth(1).withHour(0).withMinute(0);
            this.endDate = now;
            return this;
        }
    }
}
