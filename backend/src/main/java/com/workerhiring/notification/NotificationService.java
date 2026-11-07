package com.workerhiring.notification;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.workerhiring.common.ApiException;
import com.workerhiring.user.User;

@Service
public class NotificationService {

    public record NotificationResponse(Long id, String message, String link, boolean read, Instant createdAt) {
        static NotificationResponse from(Notification n) {
            return new NotificationResponse(n.getId(), n.getMessage(), n.getLink(), n.isRead(), n.getCreatedAt());
        }
    }

    public record NotificationList(long unread, List<NotificationResponse> items) {
    }

    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional
    public void notify(User user, String message, String link) {
        notifications.save(new Notification(user, message, link));
    }

    @Transactional(readOnly = true)
    public NotificationList list(Long userId) {
        var items = notifications.findTop50ByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponse::from).toList();
        return new NotificationList(notifications.countByUserIdAndReadFalse(userId), items);
    }

    @Transactional(readOnly = true)
    public long unreadCount(Long userId) {
        return notifications.countByUserIdAndReadFalse(userId);
    }

    @Transactional
    public void markRead(Long userId, Long id) {
        Notification n = notifications.findById(id)
                .filter(x -> x.getUser().getId().equals(userId))
                .orElseThrow(() -> ApiException.notFound("Notification"));
        n.setRead(true);
    }

    @Transactional
    public void markAllRead(Long userId) {
        notifications.markAllRead(userId);
    }
}
