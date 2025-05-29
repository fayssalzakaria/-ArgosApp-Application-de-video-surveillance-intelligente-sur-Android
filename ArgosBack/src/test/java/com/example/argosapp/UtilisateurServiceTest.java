package com.example.argosapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.argosapp.jwt.JwtUtil;
import com.example.argosapp.utilisateur.Utilisateur;
import com.example.argosapp.utilisateur.UtilisateurRepository;
import com.example.argosapp.utilisateur.UtilisateurService;
/**
 * Classe de test pour {@link UtilisateurService}, utilisant JUnit 5 et Mockito.
 * 
 * Cette classe vérifie les fonctionnalités principales du service Utilisateur, telles que :
 * - l'enregistrement d'un utilisateur,
 * - l'authentification,
 * - la récupération des utilisateurs non administrateurs,
 * - la suppression d'un utilisateur,
 * - la récupération des informations utilisateur,
 * - la modification du mot de passe par un administrateur.
 * 
 * Les dépendances sont simulées (mocked) à l'aide de Mockito.
 */
public class UtilisateurServiceTest {

    /**
     * Mock du repository pour les utilisateurs.
     */
    @Mock
    private UtilisateurRepository utilisateurRepository;
    /**
     * Mock de l'utilitaire de génération de JWT.
     */
    @Mock
    private JwtUtil jwtUtil;
    /**
     * Service à tester, avec les mocks injectés.
     */
    @InjectMocks
    private UtilisateurService utilisateurService;
    /**
     * Encodeur de mot de passe utilisé pour simuler les hashs.
     */
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    /**
     * Initialise les mocks avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    /**
     * Teste l'enregistrement d'un utilisateur et la bonne prise en compte du hash du mot de passe.
     */
    @Test
    public void testEnregistrerUtilisateur() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("user1");
        utilisateur.setPassword("password123");

        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArguments()[0]);

        Utilisateur savedUser = utilisateurService.enregistrerUtilisateur(utilisateur);

        assertNotNull(savedUser);
        assertTrue(passwordEncoder.matches("password123", savedUser.getPassword()));
        verify(utilisateurRepository, times(1)).save(any(Utilisateur.class));
    }
    /**
     * Teste l'authentification réussie d'un utilisateur avec un mot de passe correct.
     */
    @Test
    public void testAuthentifier_Success() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("user1");
        utilisateur.setPassword(passwordEncoder.encode("password123"));
        utilisateur.setIsadmin(false);

        when(utilisateurRepository.findById("user1")).thenReturn(Optional.of(utilisateur));
        when(jwtUtil.generateToken("user1", false)).thenReturn("fake-jwt-token");

        Optional<String> token = utilisateurService.authentifier("user1", "password123");

        assertTrue(token.isPresent());
        assertEquals("fake-jwt-token", token.get());
    }
    /**
     * Teste l'échec de l'authentification lorsqu'un utilisateur est introuvable.
     */
    @Test
    public void testAuthentifier_Failure() {
        when(utilisateurRepository.findById("user1")).thenReturn(Optional.empty());

        Optional<String> token = utilisateurService.authentifier("user1", "wrongpassword");

        assertFalse(token.isPresent());
    }
    /**
     * Teste la récupération de tous les utilisateurs non administrateurs.
     */
    @Test
    public void testGetAllUtilisateurs() {
        Utilisateur utilisateur1 = new Utilisateur();
        utilisateur1.setId("user1");
        utilisateur1.setIsadmin(false);
        
        Utilisateur utilisateur2 = new Utilisateur();
        utilisateur2.setId("user2");
        utilisateur2.setIsadmin(true);
        
        when(utilisateurRepository.findAll()).thenReturn(List.of(utilisateur1, utilisateur2));

        List<Utilisateur> nonAdmins = utilisateurService.getAllUtilisateurs();

        assertNotNull(nonAdmins);
        assertEquals(1, nonAdmins.size());
        assertEquals("user1", nonAdmins.get(0).getId());
    }
    /**
     * Teste la suppression réussie d'un utilisateur existant.
     */
    @Test
    public void testSupprimerUtilisateur_Success() {
        when(utilisateurRepository.existsById("user1")).thenReturn(true);

        boolean result = utilisateurService.supprimerUtilisateur("user1");

        assertTrue(result);
        verify(utilisateurRepository, times(1)).deleteById("user1");
    }
    /**
     * Teste l'échec de suppression lorsqu'un utilisateur n'existe pas.
     */
    @Test
    public void testSupprimerUtilisateur_Failure() {
        when(utilisateurRepository.existsById("user1")).thenReturn(false);

        boolean result = utilisateurService.supprimerUtilisateur("user1");

        assertFalse(result);
        verify(utilisateurRepository, times(0)).deleteById("user1");
    }
    /**
     * Teste la récupération des informations complètes d'un utilisateur existant.
     */
    @Test
    public void testGetInfosUtilisateur_Success() {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId("user1");
        utilisateur.setPassword(passwordEncoder.encode("password123"));
        utilisateur.setNom("NomTest");
        utilisateur.setPrenom("PrenomTest");
        utilisateur.setEnseigne("adminToken");

        when(utilisateurRepository.findById("user1")).thenReturn(Optional.of(utilisateur));

        String infos = utilisateurService.getInfosUtilisateur("user1");

        assertNotNull(infos);
        assertTrue(infos.contains("ID: user1"));
        assertTrue(infos.contains("Nom: NomTest"));
        assertTrue(infos.contains("Prénom: PrenomTest"));
        assertTrue(infos.contains("Enseigne: adminToken"));
    }
    /**
     * Teste le cas où l'utilisateur dont on souhaite obtenir les infos n'existe pas.
     */
    @Test
    public void testGetInfosUtilisateur_UserNotFound() {
        when(utilisateurRepository.findById("user1")).thenReturn(Optional.empty());

        String infos = utilisateurService.getInfosUtilisateur("user1");

        assertEquals("Utilisateur non trouvé", infos);
    }
    /**
     * Teste la modification du mot de passe d’un utilisateur par un administrateur.
     */
    @Test
public void testModifierMotDePasseAdmin_Success() {
    Utilisateur admin = new Utilisateur();
    admin.setId("admin");
    admin.setIsadmin(true);
    
    Utilisateur utilisateur = new Utilisateur();
    utilisateur.setId("user1");

    when(utilisateurRepository.findById("admin")).thenReturn(Optional.of(admin));
    when(utilisateurRepository.findById("user1")).thenReturn(Optional.of(utilisateur));
    when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArguments()[0]);

    boolean result = utilisateurService.modifierMotDePasseAdmin("admin", "user1", "newPassword123");

    assertTrue(result);
    assertTrue(passwordEncoder.matches("newPassword123", utilisateur.getPassword()));
    verify(utilisateurRepository, times(1)).save(utilisateur);
}
    /**
     * Teste le cas où un utilisateur non administrateur tente de modifier un mot de passe.
     */
    @Test
    public void testModifierMotDePasseAdmin_Failure_NoAdmin() {
        Utilisateur nonAdmin = new Utilisateur();
        nonAdmin.setId("user1");
        nonAdmin.setIsadmin(false);
        
        when(utilisateurRepository.findById("user1")).thenReturn(Optional.of(nonAdmin));

        boolean result = utilisateurService.modifierMotDePasseAdmin("user1", "user1", "newPassword123");

        assertFalse(result);
    }
    /**
     * Teste le cas où l'utilisateur cible dont on veut modifier le mot de passe n'existe pas.
     */
    @Test
    public void testModifierMotDePasseAdmin_Failure_UserNotFound() {
        Utilisateur admin = new Utilisateur();
        admin.setId("admin");
        admin.setIsadmin(true);

        when(utilisateurRepository.findById("admin")).thenReturn(Optional.of(admin));
        when(utilisateurRepository.findById("user1")).thenReturn(Optional.empty());

        boolean result = utilisateurService.modifierMotDePasseAdmin("admin", "user1", "newPassword123");

        assertFalse(result);
    }

}   
