package org.balafondreams.smsmanager.repository;

import org.balafondreams.smsmanager.domain.entities.security.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.revoked = false")
    List<Token> findAllValidTokensByUser(Long userId);

    Optional<Token> findByToken(String token);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true WHERE t.user.id = :userId")
    void revokeAllUserTokens(Long userId);

    @Query("SELECT t FROM Token t WHERE t.expiryDate < :now")
    List<Token> findAllExpiredTokens(LocalDateTime now);
}
