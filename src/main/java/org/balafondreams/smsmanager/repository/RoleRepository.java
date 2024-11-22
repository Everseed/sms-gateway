package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.user.ERole;
import org.balafondreams.smsmanager.domain.entities.user.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(ERole name);

    boolean existsByName(ERole name);

    @Query("SELECT r FROM Role r WHERE r.name IN :names")
    Set<Role> findByNames(Set<ERole> names);

    @Query("SELECT DISTINCT r FROM Role r " +
            "JOIN r.users u " +
            "GROUP BY r " +
            "ORDER BY COUNT(u) DESC")
    List<Role> findMostUsedRoles();

    @Query("SELECT r FROM Role r WHERE r NOT IN " +
            "(SELECT DISTINCT role FROM User u JOIN u.roles role)")
    List<Role> findUnusedRoles();

    @Query(value = "SELECT r.* FROM roles r " +
            "JOIN user_roles ur ON r.id = ur.role_id " +
            "JOIN users u ON ur.user_id = u.id " +
            "WHERE u.id = :userId",
            nativeQuery = true)
    Set<Role> findUserRoles(Long userId);
}