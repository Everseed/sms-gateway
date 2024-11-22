package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.sms.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ConversationRepository extends JpaRepository<Conversation, Long>, JpaSpecificationExecutor<Conversation> {

    Optional<Conversation> findByIdAndUserId(Long id, Long userId);

    Optional<Conversation> findByPhoneNumberAndUserId(String phoneNumber, Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);

    Page<Conversation> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT c FROM Conversation c " +
            "WHERE c.user.id = :userId " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "     LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY c.lastMessageAt DESC")
    Page<Conversation> searchConversations(
            @Param("userId") Long userId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT c FROM Conversation c " +
            "LEFT JOIN FETCH c.messages " +
            "WHERE c.id = :id AND c.user.id = :userId")
    Optional<Conversation> findByIdWithMessages(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    @Query("SELECT c FROM Conversation c " +
            "WHERE c.user.id = :userId " +
            "AND c.lastMessageAt > :since " +
            "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findRecentConversations(
            @Param("userId") Long userId,
            @Param("since") LocalDateTime since
    );

    @Query("SELECT COUNT(m) " +
            "FROM Message m " +
            "WHERE m.conversation.id = :conversationId " +
            "AND m.createdAt > :since")
    long countRecentMessages(
            @Param("conversationId") Long conversationId,
            @Param("since") LocalDateTime since
    );

    @Query("UPDATE Conversation c " +
            "SET c.lastMessageAt = :lastMessageAt " +
            "WHERE c.id = :conversationId")
    void updateLastMessageAt(
            @Param("conversationId") Long conversationId,
            @Param("lastMessageAt") LocalDateTime lastMessageAt
    );
}
