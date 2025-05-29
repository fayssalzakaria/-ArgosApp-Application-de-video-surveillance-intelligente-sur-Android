/*package com.example.argosapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.argosapp.enregistrement.Enregistrement;
import com.example.argosapp.enregistrement.EnregistrementRepository;
import com.example.argosapp.enregistrement.EnregistrementResponse;
import com.example.argosapp.enregistrement.EnregistrementService;
import com.example.argosapp.notification.Notification;
import com.example.argosapp.notification.NotificationRepository;

@ExtendWith(MockitoExtension.class)
public class EnregistrementServiceTest {

    @Mock
    private EnregistrementRepository enregistrementRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private EnregistrementService enregistrementService;

    private Notification notification;
    private Enregistrement enregistrement;

    @BeforeEach
    public void setUp() {
        notification = new Notification("Vol", "USER123");
        notification.setId(1L);

        enregistrement = new Enregistrement("fichier.mp3", notification, "USER123");
        enregistrement.setId(1L);
    }

    @Test
    public void testRecevoirEnregistrementAsync_Success() {
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(enregistrementRepository.save(any(Enregistrement.class))).thenReturn(enregistrement);

        CompletableFuture<Enregistrement> future = enregistrementService.recevoirEnregistrementAsync(1L, "fichier.mp3");
        Enregistrement result = future.join();

        assertThat(result).isNotNull();
        assertThat(result.getNomFichier()).isEqualTo("fichier.mp3");
        assertThat(result.getUtilisateurId()).isEqualTo("USER123");
        verify(enregistrementRepository, times(1)).save(any(Enregistrement.class));
    }

    @Test
    public void testRecevoirEnregistrementAsync_NotificationNonTrouvee() {
        when(notificationRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            enregistrementService.recevoirEnregistrementAsync(99L, "fichier.mp3").join();
        });

        assertThat(exception.getMessage()).contains("Notification not found");
        verify(enregistrementRepository, never()).save(any(Enregistrement.class));
    }

    @Test
    public void testGetAllEnregistrements() {
        Enregistrement enr1 = new Enregistrement("audio1.mp3", notification, "USER123");
        enr1.setDateAjout(LocalDateTime.of(2024, 5, 1, 10, 0));
        Enregistrement enr2 = new Enregistrement("audio2.mp3", notification, "USER123");
        enr2.setDateAjout(LocalDateTime.of(2024, 5, 2, 14, 30));

        when(enregistrementRepository.findByUtilisateurId("USER123")).thenReturn(Arrays.asList(enr1, enr2));

        List<EnregistrementResponse> result = enregistrementService.getAllEnregistrements("USER123");

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMessage()).isEqualTo(enr1.getMessage());
        assertThat(result.get(1).getDateAjout()).isEqualTo("2024-05-02T14:30");
    }
}
*/