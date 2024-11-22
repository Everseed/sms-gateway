package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.template.TemplateCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TemplateCategoryRepository extends JpaRepository<TemplateCategory, Long> {

    // Méthodes de base
    boolean existsByName(String name);

    List<TemplateCategoryRepository> findAllByOrderByNameAsc();

    // Recherche insensible à la casse
    List<TemplateCategory> findByNameContainingIgnoreCase(String name);

    // Trouver une catégorie par son nom exact (insensible à la casse)
    Optional<TemplateCategory> findByNameIgnoreCase(String name);

    // Recherche par nom ou description
    @Query("SELECT c FROM TemplateCategory c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<TemplateCategory> searchByNameOrDescription(@Param("search") String search);

    // Trouver les catégories avec un nombre minimum de templates
    @Query("SELECT c FROM TemplateCategory c WHERE SIZE(c.templates) >= :minCount")
    List<TemplateCategory> findCategoriesWithMinTemplates(@Param("minCount") int minCount);

    // Statistiques des catégories
    @Query("SELECT new org.balafondreams.smsmanager.domain.models.sms.CategoryStatsDTO(" +
            "c.id, c.name, COUNT(t), " +
            "MAX(t.createdAt), " +
            "MIN(t.createdAt)) " +
            "FROM TemplateCategory c " +
            "LEFT JOIN c.templates t " +
            "GROUP BY c.id, c.name")
    List<org.balafondreams.smsmanager.domain.models.sms.CategoryStatsDTO> getCategoryStatistics();

    // Vérifier si une catégorie est utilisée
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END " +
            "FROM TemplateCategory c " +
            "LEFT JOIN c.templates t " +
            "WHERE c.id = :categoryId")
    boolean isCategoryUsed(@Param("categoryId") Long categoryId);

    // Trouver les catégories sans templates
    @Query("SELECT c FROM TemplateCategory c WHERE c.templates IS EMPTY")
    List<TemplateCategory> findEmptyCategories();
}
