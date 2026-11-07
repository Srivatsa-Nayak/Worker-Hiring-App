package com.workerhiring.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workerhiring.user.User;

@Service
public class TokenService {

    private final SecureRandom random = new SecureRandom();
    private final AuthTokenRepository tokens;
    private final Duration validity;

    public TokenService(AuthTokenRepository tokens,
                        @Value("${app.auth.token-validity-days:7}") long validityDays) {
        this.tokens = tokens;
        this.validity = Duration.ofDays(validityDays);
    }

    /** Creates a new session for the user and returns the raw token (only ever seen by the client). */
    @Transactional
    public String issue(User user) {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String raw = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        tokens.save(new AuthToken(hash(raw), user, Instant.now().plus(validity)));
        return raw;
    }

    /** Returns the user id for a valid, unexpired token. */
    @Transactional(readOnly = true)
    public Optional<Long> resolve(String raw) {
        return tokens.findWithUser(hash(raw))
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .map(t -> t.getUser().getId());
    }

    @Transactional
    public void revoke(String raw) {
        tokens.deleteById(hash(raw));
    }

    @Transactional
    public void revokeAll(Long userId) {
        tokens.deleteAllByUserId(userId);
    }

    @Scheduled(fixedDelayString = "PT1H")
    @Transactional
    public void purgeExpired() {
        tokens.deleteExpired(Instant.now());
    }

    static String hash(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
