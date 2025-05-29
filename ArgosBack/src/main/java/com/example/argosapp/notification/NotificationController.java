package com.example.argosapp.notification;

import com.example.argosapp.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Optional;
import com.example.argosapp.notification.NotificationModel;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public CompletableFuture<ResponseEntity<Long>> recevoirNotification(
            @RequestParam String type,
            @RequestParam String nomEnseigne,
            @RequestParam String adresseEnseigne) {

        return notificationService.recevoirNotificationAsync(type, nomEnseigne, adresseEnseigne)
                .thenApply(notification -> ResponseEntity.ok(notification.getId()))
                .exceptionally(ex -> {
                    ex.printStackTrace(); // Logguer l'erreur
                    return ResponseEntity.internalServerError().build(); // Retourner 500 en cas d'erreur
                });
    }

    @GetMapping("/vol/count")
    public ResponseEntity<Long> getCountNotificationsVolUnread(@RequestParam String utilisateurId) {
        long count = notificationService.countNotificationsVolUnread(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/intrusion/count")
    public ResponseEntity<Long> getCountNotificationsIntrusionUnread(@RequestParam String utilisateurId) {
        long count = notificationService.countNotificationsIntrusionUnread(utilisateurId);
        return ResponseEntity.ok(count);
    }

    @GetMapping
    public ResponseEntity<List<NotificationModel>> getAllNotifications(@RequestParam String utilisateurId) {
        List<NotificationModel> notificationModels = notificationService.getAllNotifications(utilisateurId);
        return ResponseEntity.ok(notificationModels);
    }

    @PostMapping("/markAsRead")
    public ResponseEntity<Void> markNotificationAsRead(@RequestParam Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
}