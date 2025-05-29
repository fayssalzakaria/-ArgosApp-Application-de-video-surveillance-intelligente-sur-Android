package com.example.argosapp;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.argosapp.enseigne.Enseigne;
import com.example.argosapp.enseigne.EnseigneId;
import com.example.argosapp.enseigne.EnseigneRepository;

@DataJpaTest
@ActiveProfiles("test")
public class EnseigneRepositoryTest {

    @Autowired
    private EnseigneRepository enseigneRepository;

    @Test
    void testFindByIdUtilisateur() {
        // Création d'un objet Enseigne avec ID composite
        EnseigneId enseigneId = new EnseigneId("Test enseigne", "10 rue du test");
        Enseigne enseigne = new Enseigne();
        enseigne.setId(enseigneId);
        enseigne.setIdUtilisateur("ID_123");
        
        enseigneRepository.save(enseigne);

        // Recherche de l'enseigne avec l'ID composite
        List<Enseigne> trouve = enseigneRepository.findByIdUtilisateur("ID_123");

        assertThat(trouve).isNotEmpty();
        assertThat(trouve.get(0).getId().getNom_enseigne()).isEqualTo("Test enseigne");
        assertThat(trouve.get(0).getId().getAdresse_enseigne()).isEqualTo("10 rue du test");
    }

    @Test
    void testFindByIdUtilisateurNonTrouve() {
        // Recherche d'une enseigne avec un utilisateur inexistant
        List<Enseigne> non_trouve = enseigneRepository.findByIdUtilisateur("?????");
        assertThat(non_trouve).isEmpty();
    }
}
