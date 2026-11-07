package com.workerhiring.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workerhiring.application.ApplicationRepository;
import com.workerhiring.auth.TokenService;
import com.workerhiring.common.ApiException;
import com.workerhiring.job.JobRepository;
import com.workerhiring.notification.NotificationRepository;
import com.workerhiring.user.UserDtos.ChangePasswordRequest;
import com.workerhiring.user.UserDtos.RegisterRequest;
import com.workerhiring.user.UserDtos.UpdateProfileRequest;

@Service
public class UserService {

    private final UserRepository users;
    private final JobRepository jobs;
    private final ApplicationRepository applications;
    private final NotificationRepository notifications;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository users, JobRepository jobs, ApplicationRepository applications,
                       NotificationRepository notifications, TokenService tokenService,
                       PasswordEncoder passwordEncoder) {
        this.users = users;
        this.jobs = jobs;
        this.applications = applications;
        this.notifications = notifications;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public User get(Long id) {
        return users.findById(id).orElseThrow(() -> ApiException.unauthorized("Please log in to continue"));
    }

    @Transactional
    public User register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (users.existsByEmailIgnoreCase(email)) {
            throw ApiException.conflict("An account with this email already exists. Log in instead.");
        }
        User u = new User();
        u.setFirstName(req.firstName().trim());
        u.setLastName(req.lastName().trim());
        u.setEmail(email);
        u.setPhone(req.phone().trim());
        u.setAddress(req.address().trim());
        u.setPasswordHash(passwordEncoder.encode(req.password()));
        return users.save(u);
    }

    @Transactional
    public User updateProfile(Long id, UpdateProfileRequest req) {
        User u = get(id);
        u.setFirstName(req.firstName().trim());
        u.setLastName(req.lastName().trim());
        u.setPhone(req.phone().trim());
        u.setAddress(req.address().trim());
        u.setSkills(req.skills() == null || req.skills().isBlank() ? null : req.skills().trim());
        return u;
    }

    @Transactional
    public void changePassword(Long id, ChangePasswordRequest req) {
        User u = get(id);
        if (!passwordEncoder.matches(req.currentPassword(), u.getPasswordHash())) {
            throw ApiException.badRequest("Current password is incorrect");
        }
        u.setPasswordHash(passwordEncoder.encode(req.newPassword()));
    }

    /** Removes the user and everything they own: ads (with their applications), applications, notifications, sessions. */
    @Transactional
    public void deleteAccount(Long id, String password) {
        User u = get(id);
        if (!passwordEncoder.matches(password, u.getPasswordHash())) {
            throw ApiException.badRequest("Password is incorrect");
        }
        applications.deleteByApplicantId(id);
        applications.deleteByJobPostedById(id);
        jobs.deleteByPostedById(id);
        notifications.deleteByUserId(id);
        tokenService.revokeAll(id);
        users.delete(u);
    }
}
