package org.balafondreams.smsmanager.cron;

import lombok.AllArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.security.Token;
import org.balafondreams.smsmanager.repository.TokenRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TokenCleanupService {
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 0 * * * *") // Toutes les heures
    public void cleanupExpiredTokens() {
        List<Token> expiredTokens = tokenRepository
                .findAllExpiredTokens(LocalDateTime.now());
        tokenRepository.deleteAll(expiredTokens);
    }
}
