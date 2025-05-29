package com.example.argosapp.notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Interface de dépôt pour accéder aux données des notifications.
 * <p>
 * Fournit des méthodes personnalisées pour interagir avec la base de données
 * concernant les entités {@link Notification}.
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Compte le nombre de notifications non lues pour un utilisateur donné, filtrées par type.
     *
     * @param type           le type de notification (ex. : "Vol", "Intrusion", etc.)
     * @param utilisateurId  l'identifiant de l'utilisateur concerné
     * @return le nombre de notifications non lues correspondant
     */
    long countByTypeAndLuFalseAndUtilisateurId(String type, String utilisateurId);

    /**
     * Récupère toutes les notifications associées à un utilisateur spécifique.
     *
     * @param utilisateurId l'identifiant de l'utilisateur concerné
     * @return une liste de notifications
     */
    List<Notification> findByUtilisateurId(String utilisateurId);

    /**
     * Recherche une notification par son identifiant unique.
     *
     * @param id l'identifiant de la notification
     * @return un {@link Optional} contenant la notification si elle est trouvée, sinon vide
     */
    Optional<Notification> findById(Long id);
}
