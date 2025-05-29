package com.example.argosapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.example.argosapp.enseigne.Enseigne;
import com.example.argosapp.enseigne.EnseigneService;
import com.example.argosapp.utilisateur.Utilisateur;
import com.example.argosapp.utilisateur.UtilisateurService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe de test d'intégration pour le contrôleur {@link EnseigneController}.
 * Elle couvre les opérations principales : ajout, récupération et suppression d'une enseigne.
 * Utilise un utilisateur admin pour l'authentification via JWT.
 * 
 * <p>Les tests incluent :</p>
 * <ul>
 *   <li>Ajout d'une enseigne avec un utilisateur valide</li>
 *   <li>Validation d'une enseigne invalide</li>
 *   <li>Récupération de toutes les enseignes</li>
 *   <li>Suppression d'une enseigne existante et non existante</li>
 * </ul>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class EnseigneControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @Autowired
    private UtilisateurService utilisateurService;

    @Autowired
    private EnseigneService enseigneService;

    /**
     * Initialise le contexte Web et crée un utilisateur administrateur avec un token valide.
     * 
     * @throws Exception en cas d'échec de création de l'utilisateur ou d'authentification
     */
    @BeforeEach
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        adminToken = createAdminAndGetToken();
    }

    /**
     * Crée un utilisateur administrateur et retourne son token JWT après connexion.
     *
     * @return Le token JWT de l'utilisateur admin
     * @throws Exception si une erreur survient pendant l'enregistrement ou la connexion
     */
    private String createAdminAndGetToken() throws Exception {
        Utilisateur admin = new Utilisateur();
        admin.setId("admin");
        admin.setPassword("admin123");
        admin.setIsadmin(true);
        admin.setNom("name");
        admin.setPrenom("name");
        admin.setEnseigne("name");

        utilisateurService.enregistrerUtilisateur(admin);

        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("id", "admin");
        loginRequest.put("password", "admin123");

        return mockMvc.perform(post("/api/utilisateurs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**
     * Vérifie l'ajout correct d'une enseigne après la création d'un utilisateur.
     *
     * @throws Exception en cas d'erreur dans les appels HTTP ou assertions
     */
    @Test
    public void testAjouterEnseigne() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("u1");
        utilisateur.setPassword("testPassword");

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur ajouté avec succès"));

        Enseigne enseigne = new Enseigne("u1", "TestEnseigne", "123 Rue Test");

        mockMvc.perform(post("/api/enseigne/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enseigne)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id.nom_enseigne").value("TestEnseigne"))
                .andExpect(jsonPath("$.id.adresse_enseigne").value("123 Rue Test"))
                .andExpect(jsonPath("$.idUtilisateur").value("u1"));
    }

    /**
     * Vérifie qu'une enseigne invalide est rejetée par le contrôleur.
     *
     * @throws Exception si l'appel HTTP échoue
     */
    @Test
    public void testAjouterEnseigneFausse() throws Exception {
        mockMvc.perform(post("/api/enseigne/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"idUtilisateur\": \"u1\", \"nomEnseigne\": \"\", \"adresseEnseigne\": \"????\" }"))
                .andExpect(status().isBadRequest());
    }

    /**
     * Vérifie la récupération de toutes les enseignes disponibles pour un admin.
     *
     * @throws Exception si l'appel HTTP échoue
     */
    @Test
    public void testGetEnseigneNonAdmin() throws Exception {
        mockMvc.perform(get("/api/enseigne/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    /**
     * Vérifie que la suppression d'une enseigne inexistante retourne une erreur 404.
     *
     * @throws Exception si l'appel HTTP échoue
     */
    @Test
    public void testSuppressionEnseigneNonRealiser() throws Exception {
        mockMvc.perform(delete("/api/enseigne/supprimer/{nom_enseigne}/{adresse_enseigne}", "nonExistantNom", "nonExistantAdresse")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Cette enseigne n'existe pas !")));
    }

    /**
     * Vérifie la suppression d'une enseigne existante et que celle-ci n'est plus accessible ensuite.
     *
     * @throws Exception si une des opérations échoue
     */
    @Test
    public void testSupprimerEnseigneExistante() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("u1");
        utilisateur.setPassword("testPassword");
        utilisateur.setEnseigne("das");
        utilisateur.setNom("dasda");
        utilisateur.setPrenom("dasad");
        utilisateur.setIsadmin(false);

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur ajouté avec succès"));

        Enseigne enseigne = new Enseigne("u1", "das", "123 Rue Test");

        mockMvc.perform(post("/api/enseigne/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enseigne)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/enseigne/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))))
                .andDo(MockMvcResultHandlers.print());

        mockMvc.perform(delete("/api/enseigne/supprimer/das/123 Rue Test")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Enseigne supprimée avec succès"));

        mockMvc.perform(get("/api/enseigne/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("das"))));
    }
}
