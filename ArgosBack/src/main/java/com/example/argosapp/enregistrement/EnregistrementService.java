package com.example.argosapp.enregistrement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import java.time.ZoneId;
import com.example.argosapp.notification.Notification;
import com.example.argosapp.notification.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.time.LocalDateTime;
/**
 * Service métier responsable de la gestion des enregistrements vidéo.
 * <p>
 * Fournit des méthodes pour enregistrer les vidéos associées à des notifications
 * et récupérer les enregistrements liés à un utilisateur donné.
 */
@Service
public class EnregistrementService {

    @Autowired
    private EnregistrementRepository enregistrementRepository;

    @Autowired
    private NotificationRepository notificationRepository;
    private static final Logger logger = LoggerFactory.getLogger(EnregistrementService.class);
    /**
     * Enregistre un fichier vidéo de façon asynchrone, associé à une notification.
     * <p>
     * Cette méthode permet de libérer le thread principal HTTP immédiatement,
     * tout en poursuivant l’enregistrement du fichier en arrière-plan.
     *
     * @param notificationId identifiant de la notification liée à l'enregistrement
     * @param filePath       chemin du fichier vidéo à enregistrer
     * @return une {@link CompletableFuture} contenant l'objet {@link Enregistrement} créé
     * @throws RuntimeException si la notification est introuvable en base
     */
    @Async
    public CompletableFuture<Enregistrement> recevoirEnregistrementAsync(Long notificationId, String filePath) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        String utilisateurId = notification.getUtilisateurId();
       Enregistrement enregistrement = new Enregistrement(
    LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant(),
    LocalDateTime.now().plusDays(14).atZone(ZoneId.systemDefault()).toInstant(),
    filePath,
    notificationId,
    utilisateurId
);
        enregistrementRepository.save(enregistrement);

        System.out.println("Enregistrement avec l'id : " + enregistrement.getId() + " - thread: "
                + Thread.currentThread().getName());

        return CompletableFuture.completedFuture(enregistrement);
    }

    /**
     * Récupère tous les enregistrements liés à un utilisateur donné
     * et les transforme en objets {@link EnregistrementResponse} pour exposition via API.
     *
     * @param utilisateurId identifiant de l’utilisateur concerné
     * @return une liste de réponses contenant les dates d’ajout et messages associés
     */
    public List<Enregistrement> getEnregistrementsByUtilisateur(String utilisateurId) {
        List<Enregistrement> enregistrements = enregistrementRepository.findByUtilisateurId(utilisateurId);

        for (Enregistrement enr : enregistrements) {
            Notification notif = notificationRepository.findById(enr.getNotification_id()).orElse(null);
            if (notif != null) {
                enr.setNomProbleme(notif.getMessage());
            } else {
                enr.setNomProbleme("Problème inconnu");
            }
        }

        return enregistrements;
    }
    @Scheduled(fixedRate = 30000) 
    public void supprimerEnregistrementsExpirés() {
        Instant maintenant = Instant.now();
        List<Enregistrement> expirés = enregistrementRepository.findAll()
            .stream()
            .filter(e -> e.getDateexpiration().isBefore(maintenant))
            .toList();

        if (!expirés.isEmpty()) {
            enregistrementRepository.deleteAll(expirés);
            logger.info(expirés.size() + " enregistrements expirés supprimés.");
        }}



}
