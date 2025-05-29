/*package com.example.argosapp;

import com.example.argosapp.enregistrement.Enregistrement;
import com.example.argosapp.enregistrement.EnregistrementRepository;
import com.example.argosapp.notification.Notification;
import com.example.argosapp.notification.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EnregistrementControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EnregistrementRepository enregistrementRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Notification notification;

    @BeforeEach
    public void setup() {
        // Créer une notification de test
        notification = new Notification("message test", "user1");
        notification = notificationRepository.save(notification);
    }

    @Test
    public void testRecevoirEnregistrementAsync() throws Exception {
        mockMvc.perform(post("/api/enregistrements")
                        .param("notificationId", String.valueOf(notification.getId()))
                        .param("filePath", "fichier_test.mp3"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAllEnregistrements() throws Exception {
        // Préparer les données
        Enregistrement enr = new Enregistrement("fichier1.mp3", notification, "user1");
        enregistrementRepository.save(enr);

        mockMvc.perform(get("/api/enregistrements")
                        .param("utilisateurId", "user1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nomFichier").value("fichier1.mp3"));
    }
}
*/