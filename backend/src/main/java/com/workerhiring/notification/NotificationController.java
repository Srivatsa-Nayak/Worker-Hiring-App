package com.workerhiring.notification;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.workerhiring.auth.CurrentUser;
import com.workerhiring.notification.NotificationService.NotificationList;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    @GetMapping
    public NotificationList list() {
        return service.list(CurrentUser.id());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unread() {
        return Map.of("unread", service.unreadCount(CurrentUser.id()));
    }

    @PostMapping("/{id}/read")
    public ResponseEntity<Void> read(@PathVariable Long id) {
        service.markRead(CurrentUser.id(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/read-all")
    public ResponseEntity<Void> readAll() {
        service.markAllRead(CurrentUser.id());
        return ResponseEntity.noContent().build();
    }
}
