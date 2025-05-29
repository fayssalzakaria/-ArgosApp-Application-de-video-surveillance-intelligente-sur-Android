package com.example.argosapp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.argosapp.utilisateur.Utilisateur;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de test unitaire pour la classe {@link Utilisateur}.
 * Utilise JUnit 5 et AssertJ pour vérifier le bon fonctionnement
 * des accesseurs, constructeurs et méthodes de la classe Utilisateur.
 */
public class UtilisateurTest {

    private Utilisateur utilisateur;

    /**
     * Initialise un nouvel objet Utilisateur avant chaque test.
     */
    @BeforeEach
    public void setUp() {
        utilisateur = new Utilisateur();
    }

    /**
     * Teste les accesseurs (getters) et mutateurs (setters) de toutes les propriétés
     * de la classe Utilisateur.
     */
    @Test
    public void testGettersAndSetters() {
        utilisateur.setId("123");
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setPassword("password123");
        utilisateur.setEnseigne("Argos");
        utilisateur.setIsadmin(true);

        assertThat(utilisateur.getId()).isEqualTo("123");
        assertThat(utilisateur.getNom()).isEqualTo("Dupont");
        assertThat(utilisateur.getPrenom()).isEqualTo("Jean");
        assertThat(utilisateur.getPassword()).isEqualTo("password123");
        assertThat(utilisateur.getEnseigne()).isEqualTo("Argos");
        assertThat(utilisateur.getIsAdmin()).isTrue();
    }

    /**
     * Vérifie que la méthode {@code getIsAdmin()} retourne {@code false}
     * lorsque la propriété {@code isadmin} est définie à {@code false}.
     */
    @Test
    public void testGetIsAdminFalse() {
        utilisateur.setIsadmin(false);
        assertThat(utilisateur.getIsAdmin()).isFalse();
    }

    /**
     * Vérifie que la méthode {@code getIsAdmin()} retourne {@code true}
     * lorsque la propriété {@code isadmin} est définie à {@code true}.
     */
    @Test
    public void testGetIsAdminTrue() {
        utilisateur.setIsadmin(true);
        assertThat(utilisateur.getIsAdmin()).isTrue();
    }

    /**
     * Teste le constructeur avec paramètres de la classe {@code Utilisateur}.
     * Vérifie que tous les champs sont correctement initialisés.
     */
    @Test
    public void testConstructor() {
        Utilisateur utilisateur = new Utilisateur("123", "Dupont", "Jean", "password123", "Argos", true);

        assertThat(utilisateur.getId()).isEqualTo("123");
        assertThat(utilisateur.getNom()).isEqualTo("Dupont");
        assertThat(utilisateur.getPrenom()).isEqualTo("Jean");
        assertThat(utilisateur.getPassword()).isEqualTo("password123");
        assertThat(utilisateur.getEnseigne()).isEqualTo("Argos");
        assertThat(utilisateur.getIsAdmin()).isTrue();
    }

    /**
     * Teste la méthode {@code toString()} générée par Lombok.
     * Vérifie que le format retourné correspond à la représentation attendue.
     */
    @Test
    public void testToString() {
        utilisateur.setId("123");
        utilisateur.setNom("Dupont");
        utilisateur.setPrenom("Jean");
        utilisateur.setPassword("password123");
        utilisateur.setEnseigne("Argos");
        utilisateur.setIsadmin(true);

        String expectedToString = "Utilisateur(id=123, nom=Dupont, prenom=Jean, password=password123, enseigne=Argos, isadmin=true)";
        assertThat(utilisateur.toString()).isEqualTo(expectedToString);
    }
}
