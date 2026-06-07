package com.alphateckplus.potify.notification.infrastructure.primary.notification;

import com.alphateckplus.potify.notification.application_service.primary.notification.GetNotificationsService;
import com.alphateckplus.potify.notification.application_service.primary.notification.MarkNotificationReadService;
import com.alphateckplus.potify.notification.domain.model.Notification;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NotificationController {

    private final GetNotificationsService getNotificationsService;
    private final MarkNotificationReadService markNotificationReadService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Notification>> getUserNotifications(@PathVariable String userId) {
        return ResponseEntity.ok(getNotificationsService.execute(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(@PathVariable String id) {
        return ResponseEntity.ok(markNotificationReadService.execute(id));
    }
}
