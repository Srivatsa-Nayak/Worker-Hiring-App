package com.workerhiring.auth;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.workerhiring.common.ApiException;

/** Reads the id of the signed-in user from the security context. */
public final class CurrentUser {

    private CurrentUser() {
    }

    public static Optional<Long> idIfPresent() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Long id) {
            return Optional.of(id);
        }
        return Optional.empty();
    }

    public static Long id() {
        return idIfPresent().orElseThrow(() -> ApiException.unauthorized("Please log in to continue"));
    }
}
