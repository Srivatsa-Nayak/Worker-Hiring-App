package com.workerhiring.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workerhiring.auth.CurrentUser;
import com.workerhiring.user.UserDtos.ChangePasswordRequest;
import com.workerhiring.user.UserDtos.DeleteAccountRequest;
import com.workerhiring.user.UserDtos.UpdateProfileRequest;
import com.workerhiring.user.UserDtos.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users/me")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public UserResponse profile() {
        return UserResponse.from(userService.get(CurrentUser.id()));
    }

    @PutMapping
    public UserResponse update(@Valid @RequestBody UpdateProfileRequest req) {
        return UserResponse.from(userService.updateProfile(CurrentUser.id(), req));
    }

    @PutMapping("/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest req) {
        userService.changePassword(CurrentUser.id(), req);
        return ResponseEntity.noContent().build();
    }

    /** POST rather than DELETE so the password can travel in a body reliably. */
    @PostMapping("/delete")
    public ResponseEntity<Void> delete(@Valid @RequestBody DeleteAccountRequest req) {
        userService.deleteAccount(CurrentUser.id(), req.password());
        return ResponseEntity.noContent().build();
    }
}
