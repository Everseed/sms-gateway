package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.sms.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByIdAndUserId(Long id, Long userId);

    Page<Group> findByUserId(Long userId);
    Page<Group> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT g FROM Group g " +
            "WHERE g.user.id = :userId " +
            "AND LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Group> searchGroups(
            @Param("query") String query,
            @Param("userId") Long userId,
            Pageable pageable
    );

    boolean existsByNameAndUserId(String name, Long userId);
}