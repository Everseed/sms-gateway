package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.sms.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findByUserId(Long userId, Pageable pageable);

    Optional<Contact> findByIdAndUserId(Long id, Long userId);

    boolean existsByPhoneNumberAndUserId(String phoneNumber, Long userId);

    @Query("SELECT c FROM Contact c " +
            "WHERE c.user.id = :userId " +
            "AND (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "     c.phoneNumber LIKE CONCAT('%', :query, '%'))")
    Page<Contact> searchContacts(
            @Param("query") String query,
            @Param("userId") Long userId,
            Pageable pageable
    );

    long countByUserId(Long userId);
}