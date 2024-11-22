package org.balafondreams.smsmanager.security;

import org.balafondreams.smsmanager.domain.exception.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return authentication.getName();
    }

    public static UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() ||
                !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new UnauthorizedException("No authenticated user found");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role));
    }
}
