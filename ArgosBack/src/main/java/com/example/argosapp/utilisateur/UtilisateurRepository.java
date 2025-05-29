package com.example.argosapp.utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


/**
 * Interface de dépôt pour l'entité {@link Utilisateur}.
 *
 * <p>Cette interface étend {@link JpaRepository}, fournissant les opérations CRUD de base.
 * Elle permet également la définition de méthodes personnalisées pour l'accès aux données.</p>
 *
 * <p>L'identifiant de l'entité {@code Utilisateur} est de type {@code String}.</p>
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, String> {

    /**
     * Recherche un utilisateur par son identifiant.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return Un {@link Optional} contenant l'utilisateur s'il est trouvé, sinon vide.
     */
    Optional<Utilisateur> findById(String id);
}
