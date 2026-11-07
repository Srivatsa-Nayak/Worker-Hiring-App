package com.workerhiring.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workerhiring.common.ApiException;
import com.workerhiring.user.User;
import com.workerhiring.user.UserDtos.AuthResponse;
import com.workerhiring.user.UserDtos.LoginRequest;
import com.workerhiring.user.UserDtos.RegisterRequest;
import com.workerhiring.user.UserDtos.UserResponse;
import com.workerhiring.user.UserRepository;
import com.workerhiring.user.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository users;
    private final UserService userService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository users, UserService userService, TokenService tokenService,
                          PasswordEncoder passwordEncoder) {
        this.users = users;
        this.userService = userService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        User user = userService.register(req);
        String token = tokenService.issue(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token, UserResponse.from(user)));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        User user = users.findByEmailIgnoreCase(req.email().trim())
                .filter(u -> passwordEncoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> ApiException.unauthorized("Email or password is incorrect"));
        return new AuthResponse(tokenService.issue(user), UserResponse.from(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String raw = TokenAuthFilter.extractToken(request);
        if (raw != null) {
            tokenService.revoke(raw);
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public UserResponse me() {
        return UserResponse.from(userService.get(CurrentUser.id()));
    }
}
