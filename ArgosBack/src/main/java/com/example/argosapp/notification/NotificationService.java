package com.example.argosapp.notification;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.argosapp.enseigne.Enseigne;
import com.example.argosapp.enseigne.EnseigneRepository;
import com.example.argosapp.configuration.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;

/**
 * Service métier pour la gestion des notifications.
 * <p>
 * Gère la création asynchrone de notifications, le comptage de notifications non lues,
 * la récupération des notifications d’un utilisateur, et la mise à jour de leur statut de lecture.
 */
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EnseigneRepository enseigneRepository;

    /**
     * Crée une notification de façon asynchrone à partir des informations d'une enseigne.
     * Envoie également un message WebSocket à l'utilisateur concerné.
     *
     * @param type             le type ou message de la notification
     * @param nomEnseigne      le nom de l'enseigne liée
     * @param adresseEnseigne  l'adresse de l'enseigne liée
     * @return un {@link CompletableFuture} contenant la notification créée
     * @throws RuntimeException si l'enseigne n'est pas trouvée
     */
    @Async
    public CompletableFuture<Notification> recevoirNotificationAsync(String type, String nomEnseigne, String adresseEnseigne) {
        Optional<Enseigne> enseigneOpt = enseigneRepository.findByNomEnseigneAndAdresseEnseigne(nomEnseigne, adresseEnseigne);
        if (!enseigneOpt.isPresent()) {
            throw new RuntimeException("Enseigne non trouvée pour " + nomEnseigne + " et " + adresseEnseigne);
        }

        Enseigne enseigne = enseigneOpt.get();
        String utilisateurId = enseigne.getIdUtilisateur();

        Notification notification = new Notification(type, utilisateurId);

        // Envoi en temps réel via WebSocket
        NotificationWebSocketHandler.sendToUser(utilisateurId, notification.getMessage());

        notificationRepository.save(notification);
        System.out.println("Notification créée : " + notification.getMessage());

        return CompletableFuture.completedFuture(notification);
    }

    /**
     * Compte les notifications de type "Vol" non lues pour un utilisateur donné.
     *
     * @param utilisateurId identifiant de l'utilisateur
     * @return le nombre de notifications non lues de type "Vol"
     */
    public long countNotificationsVolUnread(String utilisateurId) {
        return notificationRepository.countByTypeAndLuFalseAndUtilisateurId("Vol", utilisateurId);
    }

    /**
     * Compte les notifications de type "Intrusion" non lues pour un utilisateur donné.
     *
     * @param utilisateurId identifiant de l'utilisateur
     * @return le nombre de notifications non lues de type "Intrusion"
     */
    public long countNotificationsIntrusionUnread(String utilisateurId) {
        return notificationRepository.countByTypeAndLuFalseAndUtilisateurId("Intrusion", utilisateurId);
    }

    /**
     * Récupère toutes les notifications associées à un utilisateur
     * et les convertit en une liste de modèles simplifiés pour l'interface.
     *
     * @param utilisateurId identifiant de l'utilisateur
     * @return liste de {@link NotificationModel}
     */
    public List<NotificationModel> getAllNotifications(String utilisateurId) {
        List<Notification> notifications = notificationRepository.findByUtilisateurId(utilisateurId);
        List<NotificationModel> models = new ArrayList<>();

        for (Notification n : notifications) {
            models.add(new NotificationModel(
                n.getId(),
                n.getLu(),
                n.getDateHeure().toString(),
                n.getMessage()
            ));
        }

        return models;
    }

    /**
     * Marque une notification comme lue en base de données.
     *
     * @param id identifiant de la notification à mettre à jour
     * @throws RuntimeException si la notification est introuvable
     */
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID " + id));
        notification.setLu(true);
        notificationRepository.save(notification);
    }
}
