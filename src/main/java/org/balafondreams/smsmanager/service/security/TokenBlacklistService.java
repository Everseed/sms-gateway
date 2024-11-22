package org.balafondreams.smsmanager.service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final String BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * Ajoute un token à la blacklist
     */
    public void blacklistToken(String token, long timeToLiveMillis) {
        String key = BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "true", timeToLiveMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * Vérifie si un token est blacklisté
     */
    public boolean isTokenBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}
