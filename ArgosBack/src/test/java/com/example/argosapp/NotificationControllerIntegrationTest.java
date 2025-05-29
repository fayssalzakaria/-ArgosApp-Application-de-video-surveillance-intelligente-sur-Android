package com.example.argosapp;

import com.example.argosapp.notification.Notification;
import com.example.argosapp.notification.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class NotificationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long notificationId;

    @BeforeEach
    public void setup() {
        Notification notif = new Notification("VOL", "user123");
        notif.setType("vol");
        notif.setLu(false);
        notif = notificationRepository.save(notif);
        this.notificationId = notif.getId();
    }

    @Test
    public void testRecevoirNotification() throws Exception {
        mockMvc.perform(post("/api/notifications")
                        .param("type", "vol")
                        .param("nomEnseigne", "Carrefour")
                        .param("adresseEnseigne", "Rue A"))
                .andExpect(status().isOk())
                .andExpect(content().string(matchesRegex("\\d+"))); // ID renvoyé
    }

    @Test
    public void testCountNotificationsVolUnread() throws Exception {
        mockMvc.perform(get("/api/notifications/vol/count")
                        .param("utilisateurId", "user123"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void testGetAllNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications")
                        .param("utilisateurId", "user123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].message").value("VOL"));
    }

    @Test
    public void testMarkNotificationAsRead() throws Exception {
        mockMvc.perform(post("/api/notifications/markAsRead")
                        .param("id", String.valueOf(notificationId)))
                .andExpect(status().isOk());

        // Vérification postérieure que la notification est bien marquée lue
        mockMvc.perform(get("/api/notifications/vol/count")
                        .param("utilisateurId", "user123"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
