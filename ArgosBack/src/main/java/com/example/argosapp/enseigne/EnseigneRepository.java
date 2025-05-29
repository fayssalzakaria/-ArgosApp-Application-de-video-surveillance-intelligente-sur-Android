package com.example.argosapp.enseigne;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;


@Repository
public interface EnseigneRepository extends JpaRepository<Enseigne, EnseigneId> {

    /**
     * Récupère toutes les enseignes associées à un utilisateur donné.
     *
     * @param idUtilisateur l'identifiant de l'utilisateur
     * @return une liste d'enseignes correspondant à l'utilisateur
     */
    List<Enseigne> findByIdUtilisateur(String idUtilisateur);

    /**
     * Recherche une enseigne à partir de son nom et de son adresse via une requête JPQL personnalisée.
     *
     * @param nomEnseigne      le nom de l'enseigne
     * @param adresseEnseigne  l'adresse de l'enseigne
     * @return une {@link Optional} contenant l'enseigne si trouvée, sinon vide
     */
    @Query("SELECT e FROM Enseigne e WHERE e.id.nom_enseigne = :nomEnseigne AND e.id.adresse_enseigne = :adresseEnseigne")
    Optional<Enseigne> findByNomEnseigneAndAdresseEnseigne(String nomEnseigne, String adresseEnseigne);
}
