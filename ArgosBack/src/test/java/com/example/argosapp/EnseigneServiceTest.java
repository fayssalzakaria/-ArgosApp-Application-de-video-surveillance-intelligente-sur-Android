package com.example.argosapp;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.example.argosapp.enseigne.Enseigne;
import com.example.argosapp.enseigne.EnseigneId;
import com.example.argosapp.enseigne.EnseigneRepository;
import com.example.argosapp.enseigne.EnseigneService;
import com.example.argosapp.utilisateur.Utilisateur;
import com.example.argosapp.utilisateur.UtilisateurRepository;

import jakarta.transaction.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class EnseigneServiceTest {

    @Mock
    private EnseigneRepository enseigneRepository;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @InjectMocks 
    private EnseigneService enseigneService;

    private Enseigne enseigneValide;
    private Utilisateur utilisateurNonAdmin;
    private Utilisateur utilisateurAdmin;

    @BeforeEach
    public void setUp() {
        enseigneValide = new Enseigne("ID_123", "Test", "Adresse de test");

        utilisateurNonAdmin = new Utilisateur();
        utilisateurNonAdmin.setIsadmin(false);

        utilisateurAdmin = new Utilisateur();
        utilisateurAdmin.setIsadmin(true);
    }

    @Test
    public void testAjouterEnseigne() {
        when(enseigneRepository.save(any(Enseigne.class))).thenReturn(enseigneValide);

        Enseigne resultat = enseigneService.ajouterEnseigne("ID_123", "Test", "Adresse de test");

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNomEnseigne()).isEqualTo("Test");
        verify(enseigneRepository, times(1)).save(any(Enseigne.class));
    }

    @Test
    public void testAjouterEnseigneIdentifiantVide() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            enseigneService.ajouterEnseigne("", "Test id vide", "Adresse vide");
        });

        assertThat(exception.getMessage()).isEqualTo("L'identifiant est nécessaire pour poursuivre");

    }

    @Test
    public void testAjouterEnseigneNomVide() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            enseigneService.ajouterEnseigne("ID_123", " ", "Adresse vide");
        });

        assertThat(exception.getMessage()).isEqualTo("Le nom de l'enseigne est requis");
    }

    @Test
    public void testSuppressionEnseigne() {
        when(enseigneRepository.existsById(any(EnseigneId.class))).thenReturn(true);
        enseigneService.supprimerEnseigne(new EnseigneId("Test", "Adresse de test"));
        verify(enseigneRepository, times(1)).deleteById(any(EnseigneId.class));
    }

    @Test
    public void testSuppressionEnseigneNomIncorrect() {
        when(enseigneRepository.existsById(any(EnseigneId.class))).thenReturn(false);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            enseigneService.supprimerEnseigne(new EnseigneId("", ""));
        });

        assertThat(exception.getMessage()).isEqualTo("Cette enseigne n'existe pas !");
    }

    @Test
    public void testGetAllEnseigneNonAdmin() {
        Enseigne enseigneNonAdmin = new Enseigne("ID1", "NonAdmin", "Adresse1");
        Enseigne enseigneAdmin = new Enseigne("ID2", "Admin", "Adresse2");

        when(enseigneRepository.findAll()).thenReturn(Arrays.asList(enseigneNonAdmin, enseigneAdmin));
        when(utilisateurRepository.findById("ID1")).thenReturn(Optional.of(utilisateurNonAdmin));
        when(utilisateurRepository.findById("ID2")).thenReturn(Optional.of(utilisateurAdmin));

        List<Enseigne> resultat = enseigneService.getAllEnseignesNonAdmin();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.get(0).getIdUtilisateur()).isEqualTo("ID1");
    }
}
