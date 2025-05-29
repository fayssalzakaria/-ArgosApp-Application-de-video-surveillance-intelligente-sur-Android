package com.example.argosapp;

import org.junit.jupiter.api.Test;

import com.example.argosapp.enseigne.Enseigne;
import com.example.argosapp.enseigne.EnseigneId;

import static org.junit.jupiter.api.Assertions.*;

public class EnseigneTest {

    @Test
    public void testGettersEtSetters() {
        // Création de l'ID composite
        EnseigneId enseigneId = new EnseigneId("Monoprix", "10 rue de Paris");
        Enseigne enseigne = new Enseigne();
        enseigne.setId(enseigneId);  // Utilisation de l'ID composite
        enseigne.setIdUtilisateur("ID_1234");

        // Vérification des getters
        assertEquals("ID_1234", enseigne.getIdUtilisateur());
        assertEquals("Monoprix", enseigne.getNomEnseigne());  // Accès au nom via l'ID composite
        assertEquals("10 rue de Paris", enseigne.getAdresseEnseigne());  // Accès à l'adresse via l'ID composite
    }

    @Test
    public void testConstructeur() {
        // Création d'un ID composite
        EnseigneId enseigneId = new EnseigneId("Auchan", "15 rue de Bagnolet");
        Enseigne enseigne = new Enseigne("ID_456", "Auchan", "15 rue de Bagnolet");

        // Vérification des valeurs
        assertEquals("ID_456", enseigne.getIdUtilisateur());
        assertEquals("Auchan", enseigne.getNomEnseigne());  // Accès au nom via l'ID composite
        assertEquals("15 rue de Bagnolet", enseigne.getAdresseEnseigne());  // Accès à l'adresse via l'ID composite
    }

    @Test
    public void testEqualsEtHashCode() {
        // Création de l'ID composite pour les objets
        EnseigneId id1 = new EnseigneId("Carrefour", "10 rue Paris 20ème");
        EnseigneId id2 = new EnseigneId("Carrefour", "10 rue Paris 20ème");
        EnseigneId id3 = new EnseigneId("Lidl", "25 rue de la Paix");

        Enseigne e1 = new Enseigne("ID_123", id1.getNom_enseigne(), id1.getAdresse_enseigne());
        Enseigne e2 = new Enseigne("ID_123", id2.getNom_enseigne(), id2.getAdresse_enseigne());
        Enseigne e3 = new Enseigne("ID_456", id3.getNom_enseigne(), id3.getAdresse_enseigne());

        // Test des égalités
        assertEquals(e1, e2);
        assertNotEquals(e1, e3);
        assertNotEquals(e2, e3);

        // Test des hashcodes
        assertEquals(e1.hashCode(), e2.hashCode());
        assertNotEquals(e1.hashCode(), e3.hashCode());
    }

    @Test
    public void testToString() {
        // Création d'un ID composite
        EnseigneId enseigneId = new EnseigneId("La Grande Récré", "10 place Edith Piaf");
        Enseigne enseigne = new Enseigne("ID_XYZ", enseigneId.getNom_enseigne(), enseigneId.getAdresse_enseigne());

        // Vérification de la méthode toString
        String res = "Enseigne{nom_enseigne='La Grande Récré', adresse_enseigne='10 place Edith Piaf', idUtilisateur='ID_XYZ'}";
        assertEquals(res, enseigne.toString());
    }
}
