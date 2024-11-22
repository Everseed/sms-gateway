package org.balafondreams.smsmanager.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.balafondreams.smsmanager.repository.TokenRepository;
import org.balafondreams.smsmanager.service.security.TokenBlacklistService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenBlacklistService blacklistService;
    private final TokenRepository tokenRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = extractTokenFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // Vérifier si le token est blacklisté
                if (blacklistService.isTokenBlacklisted(jwt)) {
                    log.debug("Token is blacklisted: {}", jwt);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
                    return;
                }

                // Vérifier si le token est révoqué en base
                var storedToken = tokenRepository.findByToken(jwt);
                if (storedToken.isPresent() && storedToken.get().isRevoked()) {
                    log.debug("Token is revoked: {}", jwt);
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is revoked");
                    return;
                }

                // Vérifier et configurer l'authentification
                if (tokenProvider.validateToken(jwt)) {
                    String username = tokenProvider.getUsernameFromToken(jwt);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Set Authentication to security context for '{}', uri: {}",
                            username, request.getRequestURI());
                }
            }
        } catch (Exception e) {
            log.error("Cannot set user authentication", e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le token JWT de la requête HTTP
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Détermine si le filtre doit être appliqué
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/api/auth/") || // Endpoints d'authentification
                path.startsWith("/swagger-ui/") || // Documentation Swagger
                path.startsWith("/v3/api-docs/"); // OpenAPI
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}