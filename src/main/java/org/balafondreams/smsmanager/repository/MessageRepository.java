package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.sms.Message;
import org.balafondreams.smsmanager.domain.entities.sms.MessageStatus;
import org.balafondreams.smsmanager.domain.models.sms.MessageStatsByDateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
        Optional<Message> findByIdAndUserId(Long id, Long userId);

        Page<Message> findByConversationIdAndUserIdOrderByCreatedAtDesc(
                Long conversationId,
                Long userId,
                Pageable pageable
        );

        List<Message> findByStatusAndScheduledAtBeforeAndSentAtIsNull(
                MessageStatus status,
                LocalDateTime scheduledTime
        );

        long countByUserId(Long userId);

        long countByUserIdAndStatus(Long userId, MessageStatus status);

        @Query("SELECT m FROM Message m " +
                "WHERE m.user.id = :userId " +
                "AND (:status IS NULL OR m.status = :status) " +
                "AND (:phoneNumber IS NULL OR m.phoneNumber = :phoneNumber) " +
                "AND (:startDate IS NULL OR m.createdAt >= :startDate) " +
                "AND (:endDate IS NULL OR m.createdAt <= :endDate) " +
                "ORDER BY m.createdAt DESC")
        Page<Message> findMessages(
                @Param("userId") Long userId,
                @Param("status") MessageStatus status,
                @Param("phoneNumber") String phoneNumber,
                @Param("startDate") LocalDateTime startDate,
                @Param("endDate") LocalDateTime endDate,
                Pageable pageable
        );

        @Query("SELECT m FROM Message m " +
                "WHERE m.user.id = :userId " +
                "AND m.template.id = :templateId " +
                "ORDER BY m.createdAt DESC")
        Page<Message> findByTemplateId(
                @Param("userId") Long userId,
                @Param("templateId") Long templateId,
                Pageable pageable
        );

        @Query("SELECT COUNT(m) > 0 FROM Message m " +
                "WHERE m.user.id = :userId " +
                "AND m.phoneNumber = :phoneNumber " +
                "AND m.status = 'SENT' " +
                "AND m.createdAt >= :since")
        boolean hasRecentMessageTo(
                @Param("userId") Long userId,
                @Param("phoneNumber") String phoneNumber,
                @Param("since") LocalDateTime since
        );

        @Query("SELECT new org.balafondreams.smsmanager.domain.models.sms.MessageStatsByDateDTO(" +
                "FUNCTION('DATE', m.createdAt), " +
                "COUNT(m), " +
                "COUNT(CASE WHEN m.status = 'SENT' THEN 1 END), " +
                "COUNT(CASE WHEN m.status = 'FAILED' THEN 1 END)) " +
                "FROM Message m " +
                "WHERE m.user.id = :userId " +
                "AND m.createdAt BETWEEN :startDate AND :endDate " +
                "GROUP BY FUNCTION('DATE', m.createdAt) " +
                "ORDER BY FUNCTION('DATE', m.createdAt)")
        List<MessageStatsByDateDTO> getMessageStatsByDate(
                @Param("userId") Long userId,
                @Param("startDate") LocalDateTime startDate,
                @Param("endDate") LocalDateTime endDate
        );

        @Query("SELECT m.phoneNumber, COUNT(m) as messageCount " +
                "FROM Message m " +
                "WHERE m.user.id = :userId " +
                "GROUP BY m.phoneNumber " +
                "ORDER BY messageCount DESC " +
                "LIMIT :limit")
        List<Object[]> findMostContactedNumbers(
                @Param("userId") Long userId,
                @Param("limit") int limit
        );

        @Query("SELECT m FROM Message m " +
                "WHERE m.status = 'FAILED' " +
                "AND m.createdAt < :date")
        List<Message> findFailedMessagesBefore(@Param("date") LocalDateTime date);

        @Query("DELETE FROM Message m " +
                "WHERE m.status = 'SENT' " +
                "AND m.createdAt < :date")
        void deleteOldMessages(@Param("date") LocalDateTime date);

        @Query("SELECT COUNT(m) FROM Message m " +
                "WHERE m.status = 'PENDING' " +
                "AND m.scheduledAt < :now")
        long countDelayedMessages(@Param("now") LocalDateTime now);
}


