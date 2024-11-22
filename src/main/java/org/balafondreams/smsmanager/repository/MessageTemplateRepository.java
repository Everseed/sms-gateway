package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.template.MessageTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplate, Long>,
        JpaSpecificationExecutor<MessageTemplate> {

    @Query("SELECT t FROM MessageTemplate t " +
            "LEFT JOIN FETCH t.variables " +
            "WHERE t.id = :id AND t.user.id = :userId AND t.isActive = true")
    Optional<MessageTemplate> findByIdAndUserIdAndIsActiveTrue(
            @Param("id") Long id,
            @Param("userId") Long userId
    );

    @Query("SELECT t FROM MessageTemplate t " +
            "WHERE (t.user.id = :userId OR t.user IS NULL) " +
            "AND t.isActive = true")
    Page<MessageTemplate> findAllAvailableTemplates(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT t FROM MessageTemplate t " +
            "WHERE t.category.id = :categoryId " +
            "AND t.isActive = true")
    Page<MessageTemplate> findByCategoryIdAndIsActiveTrue(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    boolean existsByNameAndUserIdAndIsActiveTrue(
            String name,
            Long userId
    );

    @Query("SELECT t FROM MessageTemplate t " +
            "WHERE (t.user.id = :userId OR t.user IS NULL) " +
            "AND t.isActive = true " +
            "AND (:categoryId IS NULL OR t.category.id = :categoryId) " +
            "AND (LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "     OR LOWER(t.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "     OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<MessageTemplate> searchTemplates(
            @Param("query") String query,
            @Param("categoryId") Long categoryId,
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT DISTINCT t FROM MessageTemplate t " +
            "LEFT JOIN FETCH t.variables " +
            "WHERE t.id = :id AND t.isActive = true")
    Optional<MessageTemplate> findByIdWithVariables(@Param("id") Long id);

    @Query("SELECT t FROM MessageTemplate t " +
            "WHERE t.user.id = :userId " +
            "AND t.isActive = true " +
            "ORDER BY size(t.messages) DESC")
    List<MessageTemplate> findMostUsedTemplates(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.template.id = :templateId")
    long countUsages(@Param("templateId") Long templateId);

    @Query("SELECT t FROM MessageTemplate t " +
            "WHERE t.user.id = :userId " +
            "AND t.isActive = true " +
            "AND NOT EXISTS (SELECT 1 FROM Message m WHERE m.template = t)")
    List<MessageTemplate> findUnusedTemplates(@Param("userId") Long userId);

    @Query("SELECT t FROM MessageTemplate t " +
            "WHERE t.category.id = :categoryId " +
            "AND t.isActive = true " +
            "ORDER BY size(t.messages) DESC")
    List<MessageTemplate> findPopularTemplatesByCategory(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    @Query("SELECT t FROM MessageTemplate t " +
            "LEFT JOIN FETCH t.variables " +
            "WHERE t.user.id = :userId " +
            "AND t.isActive = true " +
            "AND t.name LIKE :name")
    Optional<MessageTemplate> findByNameAndUserIdAndIsActiveTrue(
            @Param("name") String name,
            @Param("userId") Long userId
    );
}
