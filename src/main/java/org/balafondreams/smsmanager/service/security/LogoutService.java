package org.balafondreams.smsmanager.service.security;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.repository.TokenRepository;
import org.balafondreams.smsmanager.security.JwtTokenProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {
    private final TokenRepository tokenRepository;
    private final TokenBlacklistService blacklistService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        var storedToken = tokenRepository.findByToken(token);

        if (storedToken.isPresent()) {
            var tokenEntity = storedToken.get();
            tokenEntity.setRevoked(true);
            tokenRepository.save(tokenEntity);

            // Ajouter à la blacklist pour le temps restant
            long timeToLive = jwtTokenProvider.getRemainingTime(token);
            if (timeToLive > 0) {
                blacklistService.blacklistToken(token, timeToLive);
            }
        }
    }
}
