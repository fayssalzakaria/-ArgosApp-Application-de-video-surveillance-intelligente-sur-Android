package com.example.argosapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.argosapp.configuration.NotificationWebSocketHandler;
import com.example.argosapp.enseigne.Enseigne;
import com.example.argosapp.enseigne.EnseigneRepository;
import com.example.argosapp.notification.Notification;
import com.example.argosapp.notification.NotificationModel;
import com.example.argosapp.notification.NotificationRepository;
import com.example.argosapp.notification.NotificationService;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private EnseigneRepository enseigneRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Enseigne enseigne;
    private Notification notification;

    @BeforeEach
    public void setUp() {
        enseigne = new Enseigne("ID_USER", "Mon Enseigne", "Adresse XYZ");
        notification = new Notification("Vol", "ID_USER");
    }

    @Test
    public void testRecevoirNotificationAsync_success() {
        when(enseigneRepository.findByNomEnseigneAndAdresseEnseigne("Mon Enseigne", "Adresse XYZ"))
            .thenReturn(Optional.of(enseigne));

        try (MockedStatic<NotificationWebSocketHandler> mockedStatic = mockStatic(NotificationWebSocketHandler.class)) {
            when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

            CompletableFuture<Notification> future = notificationService.recevoirNotificationAsync("Vol", "Mon Enseigne", "Adresse XYZ");
            Notification result = future.join();

            assertThat(result).isNotNull();
            assertThat(result.getType()).isEqualTo("Vol");
            assertThat(result.getUtilisateurId()).isEqualTo("ID_USER");
            verify(notificationRepository, times(1)).save(any(Notification.class));
            mockedStatic.verify(() -> NotificationWebSocketHandler.sendToUser(eq("ID_USER"), anyString()), times(1));
        }
    }

    @Test
    public void testRecevoirNotificationAsync_enseigneNonTrouvee() {
        when(enseigneRepository.findByNomEnseigneAndAdresseEnseigne(anyString(), anyString()))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationService.recevoirNotificationAsync("Vol", "Inconnue", "Adresse inconnue").join();
        });

        assertThat(exception.getMessage()).contains("Enseigne non trouvée");
    }

    @Test
    public void testCountNotificationsVolUnread() {
        when(notificationRepository.countByTypeAndLuFalseAndUtilisateurId("Vol", "ID_USER")).thenReturn(5L);

        long result = notificationService.countNotificationsVolUnread("ID_USER");

        assertThat(result).isEqualTo(5L);
        verify(notificationRepository).countByTypeAndLuFalseAndUtilisateurId("Vol", "ID_USER");
    }

    @Test
    public void testCountNotificationsIntrusionUnread() {
        when(notificationRepository.countByTypeAndLuFalseAndUtilisateurId("Intrusion", "ID_USER")).thenReturn(2L);

        long result = notificationService.countNotificationsIntrusionUnread("ID_USER");

        assertThat(result).isEqualTo(2L);
        verify(notificationRepository).countByTypeAndLuFalseAndUtilisateurId("Intrusion", "ID_USER");
    }

    @Test
    public void testGetAllNotifications() {
        Notification n1 = new Notification("Vol", "ID_USER");
        Notification n2 = new Notification("Intrusion", "ID_USER");
        n1.setId(1L); n2.setId(2L);

        when(notificationRepository.findByUtilisateurId("ID_USER")).thenReturn(Arrays.asList(n1, n2));

        List<NotificationModel> result = notificationService.getAllNotifications("ID_USER");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMessage()).isEqualTo("Suspicion de vol");
        assertThat(result.get(1).getMessage()).isEqualTo("Alerte intrusion");
    }

    @Test
    public void testMarkAsRead_success() {
        Notification unreadNotification = new Notification("Vol", "ID_USER");
        unreadNotification.setId(10L);
        unreadNotification.setLu(false);

        when(notificationRepository.findById(10L)).thenReturn(Optional.of(unreadNotification));

        notificationService.markAsRead(10L);

        assertThat(unreadNotification.getLu()).isTrue();
        verify(notificationRepository).save(unreadNotification);
    }

    @Test
    public void testMarkAsRead_notificationNotFound() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            notificationService.markAsRead(999L);
        });

        assertThat(exception.getMessage()).contains("Notification non trouvée");
    }
}
