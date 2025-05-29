package com.example.argosapp;

import com.example.argosapp.utilisateur.Utilisateur;
import com.example.argosapp.utilisateur.UtilisateurService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Classe d'intégration pour tester les endpoints du contrôleur Utilisateur.
 * Utilise MockMvc pour simuler les requêtes HTTP.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UtilisateurControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Initialise un utilisateur administrateur avant chaque test
     * et récupère un token d’authentification valide.
     */
    @BeforeEach
    public void setUp() throws Exception {
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

        adminToken = mockMvc.perform(post("/api/utilisateurs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    /**
     * Test l'ajout d'un utilisateur par un administrateur.
     */
    @Test
    public void testAjouterUtilisateur() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("testUser");
        utilisateur.setPassword("testPassword");

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur ajouté avec succès"));
    }

    /**
     * Test la réussite d'une connexion avec des identifiants valides.
     */
    @Test
    public void testLogin_Success() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("loginUser");
        utilisateur.setPassword("loginPassword");

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk());

        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("id", "loginUser");
        loginRequest.put("password", "loginPassword");

        mockMvc.perform(post("/api/utilisateurs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN));
    }

    /**
     * Test l'échec de connexion avec des identifiants invalides.
     */
    @Test
    public void testLogin_Failure() throws Exception {
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("id", "unknownUser");
        loginRequest.put("password", "wrongPassword");

        mockMvc.perform(post("/api/utilisateurs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Erreur : Identifiants invalides. Veuillez vérifier votre ID et votre mot de passe."));
    }

    /**
     * Test la déconnexion réussie d'un utilisateur connecté.
     */
    @Test
    public void testLogout_Success() throws Exception {
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("id", "Juventin");
        loginRequest.put("password", "1234");

        String token = mockMvc.perform(post("/api/utilisateurs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(post("/api/utilisateurs/logout")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("Déconnexion réussie"));
    }

    /**
     * Test la validation d'un token correct.
     */
    @Test
    public void testValidateToken_Success() throws Exception {
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("id", "Juventin");
        loginRequest.put("password", "1234");

        String token = mockMvc.perform(post("/api/utilisateurs/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/api/utilisateurs/validate-token")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    /**
     * Test la validation d'un token invalide.
     */
    @Test
    public void testValidateToken_Failure() throws Exception {
        mockMvc.perform(get("/api/utilisateurs/validate-token")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    /**
     * Test la récupération de tous les utilisateurs.
     */
    @Test
    public void testGetAllUtilisateurs() throws Exception {
        mockMvc.perform(get("/api/utilisateurs/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    /**
     * Test la suppression réussie d’un utilisateur.
     */
    @Test
    public void testSupprimerUtilisateur_Success() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("userToDelete");
        utilisateur.setNom("NomTest");
        utilisateur.setPrenom("PrenomTest");
        utilisateur.setPassword("password");
        utilisateur.setEnseigne("adminToken");

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/utilisateurs/supprimer/userToDelete")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Utilisateur supprimée !"));

        mockMvc.perform(get("/api/utilisateurs/all")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("userToDelete"))));
    }

    /**
     * Test l’échec de suppression d’un utilisateur inexistant.
     */
    @Test
    public void testSupprimerUtilisateur_Failure() throws Exception {
        mockMvc.perform(delete("/api/utilisateurs/supprimer/unknownUser")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Utilisateur non supprimée"));
    }

    /**
     * Test la récupération des informations d’un utilisateur spécifique.
     */
    @Test
    public void testGetUtilisateurInfo_Success() throws Exception {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("userInfo");
        utilisateur.setPassword("password");
        utilisateur.setNom("NomTest");
        utilisateur.setPrenom("PrenomTest");
        utilisateur.setEnseigne("adminToken");

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/utilisateurs/infos/userInfo")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/plain;charset=UTF-8"))
                .andExpect(content().string(containsString("ID: userInfo")))
                .andExpect(content().string(containsString("Nom: NomTest")))
                .andExpect(content().string(containsString("Prénom: PrenomTest")))
                .andExpect(content().string(containsString("Enseigne: adminToken")))
                .andExpect(content().string(not(containsString("Mot de passe: password"))))
                .andExpect(content().string(containsString("Mot de passe: $2a$12$")));
    }

    /**
     * Test la modification du mot de passe d’un utilisateur par un administrateur.
     */
    @Test
    public void testChangerMotDePasseAdmin_Success() throws Exception {
        Map<String, String> payload = new HashMap<>();
        payload.put("adminID", "Juventin");
        payload.put("idTarget", "userToChange");
        payload.put("nouveauPassword", "newPassword123");

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("userToChange");
        utilisateur.setPassword("oldPassword");
        utilisateur.setNom("NomTest");
        utilisateur.setPrenom("PrenomTest");
        utilisateur.setEnseigne("enseignant");

        mockMvc.perform(post("/api/utilisateurs/ajouter")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(utilisateur)))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/utilisateurs/changer-mdp")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andExpect(content().string("Mot de passe modifié avec succès"));
    }
}
