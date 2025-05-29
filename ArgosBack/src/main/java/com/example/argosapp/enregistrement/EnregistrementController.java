package com.example.argosapp.enregistrement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


import java.util.List;

/**
 * Contrôleur REST pour la gestion des enregistrements.
 * <p>
 * Fournit des points d'accès pour recevoir de nouveaux enregistrements
 * et pour récupérer la liste des enregistrements existants associés à un utilisateur.
 */
@RestController
@RequestMapping("/api/enregistrements")
public class EnregistrementController {

    @Autowired
    private EnregistrementService enregistrementService;

    /**
     * Reçoit un nouvel enregistrement de manière asynchrone.
     * <p>
     * Cette méthode déclenche une opération d'enregistrement sans attendre
     * la fin du traitement (mode asynchrone), afin de ne pas bloquer l'exécution côté client.
     *
     * @param notificationId identifiant de la notification associée à l'enregistrement
     * @param filePath       chemin du fichier vidéo à enregistrer
     * @return une réponse HTTP 200 OK sans contenu
     */
    @PostMapping
    public ResponseEntity<Void> recevoirEnregistrementAsync(
            @RequestParam Long notificationId,
            @RequestParam String filePath) {
                    // Décodage explicite de l'URL
        String decodedFilePath = URLDecoder.decode(filePath, StandardCharsets.UTF_8);

        enregistrementService.recevoirEnregistrementAsync(notificationId, decodedFilePath);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupère la liste de tous les enregistrements liés à un utilisateur.
     *
     * @param utilisateurId identifiant de l'utilisateur dont on souhaite récupérer les enregistrements
     * @return une réponse HTTP contenant la liste des enregistrements au format 
     */
    @GetMapping
    public ResponseEntity<List<Enregistrement>> getEnregistrementsParUtilisateur(@RequestParam String utilisateurId) {
        List<Enregistrement> enregistrements = enregistrementService.getEnregistrementsByUtilisateur(utilisateurId);
        return ResponseEntity.ok(enregistrements);
    }
}
