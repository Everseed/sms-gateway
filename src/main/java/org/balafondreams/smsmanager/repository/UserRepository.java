package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.user.ERole;
import org.balafondreams.smsmanager.domain.entities.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.enabled = true AND " +
            "(LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<User> searchUsers(String search, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u JOIN u.roles r WHERE r.name = :roleName")
    List<User> findByRoleName(ERole roleName);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(Long id);

    @Query(value = "SELECT u.* FROM users u " +
            "JOIN user_roles ur ON u.id = ur.user_id " +
            "GROUP BY u.id " +
            "HAVING COUNT(ur.role_id) > :minRoles",
            nativeQuery = true)
    List<User> findUsersWithMultipleRoles(int minRoles);

    @Query("SELECT COUNT(u) > 0 FROM User u " +
            "JOIN u.roles r WHERE u.id = :userId AND r.name = :roleName")
    boolean hasRole(Long userId, ERole roleName);

    @Query("SELECT u FROM User u WHERE u.enabled = true " +
            "AND NOT EXISTS (SELECT 1 FROM Message m WHERE m.user = u)")
    List<User> findInactiveUsers();

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN FETCH u.roles " +
            "WHERE :role MEMBER OF u.roles")
    List<User> findByRolesName(@Param("role") ERole role);

    @Query(value = "SELECT r.name, COUNT(u.id) as count " +
            "FROM users u " +
            "JOIN user_roles ur ON u.id = ur.user_id " +
            "JOIN roles r ON ur.role_id = r.id " +
            "GROUP BY r.name",
            nativeQuery = true)
    List<Object[]> getRoleDistribution();
}
