package com.workerhiring.user;

import java.time.Instant;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class UserDtos {

    private UserDtos() {
    }

    static final String PHONE_REGEX = "^\\+?[0-9 ]{10,15}$";
    static final String PHONE_MESSAGE = "Enter a valid phone number (10 to 15 digits)";

    public record RegisterRequest(
            @NotBlank(message = "First name is required") @Size(max = 60) String firstName,
            @NotBlank(message = "Last name is required") @Size(max = 60) String lastName,
            @NotBlank(message = "Email is required") @Email(message = "Enter a valid email") @Size(max = 120) String email,
            @NotBlank(message = "Phone number is required") @Pattern(regexp = PHONE_REGEX, message = PHONE_MESSAGE) String phone,
            @NotBlank(message = "Address is required") @Size(max = 255) String address,
            @NotBlank(message = "Password is required") @Size(min = 6, max = 100, message = "Password must be at least 6 characters") String password) {
    }

    public record LoginRequest(
            @NotBlank(message = "Email is required") String email,
            @NotBlank(message = "Password is required") String password) {
    }

    public record UpdateProfileRequest(
            @NotBlank(message = "First name is required") @Size(max = 60) String firstName,
            @NotBlank(message = "Last name is required") @Size(max = 60) String lastName,
            @NotBlank(message = "Phone number is required") @Pattern(regexp = PHONE_REGEX, message = PHONE_MESSAGE) String phone,
            @NotBlank(message = "Address is required") @Size(max = 255) String address,
            @Size(max = 1000, message = "Keep skills under 1000 characters") String skills) {
    }

    public record ChangePasswordRequest(
            @NotBlank(message = "Current password is required") String currentPassword,
            @NotBlank(message = "New password is required") @Size(min = 6, max = 100, message = "Password must be at least 6 characters") String newPassword) {
    }

    public record DeleteAccountRequest(@NotBlank(message = "Password is required") String password) {
    }

    public record UserResponse(Long id, String firstName, String lastName, String email, String phone,
                               String address, String skills, Instant createdAt) {
        public static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail(), u.getPhone(),
                    u.getAddress(), u.getSkills(), u.getCreatedAt());
        }
    }

    public record AuthResponse(String token, UserResponse user) {
    }
}
