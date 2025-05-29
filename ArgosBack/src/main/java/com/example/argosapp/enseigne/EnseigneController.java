package com.example.argosapp.enseigne;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour la gestion des enseignes.
 * <p>
 * Fournit des points d'accès pour ajouter, récupérer et supprimer des enseignes
 * associées aux utilisateurs non administrateurs.
 */
@RestController
@RequestMapping("/api/enseigne")
public class EnseigneController {

    private final EnseigneService enseigneService;

    /**
     * Constructeur injectant le service {@link EnseigneService}.
     *
     * @param enseigneService service de gestion des enseignes
     */
    @Autowired
    public EnseigneController(EnseigneService enseigneService) {
        this.enseigneService = enseigneService;
    }

    /**
     * Ajoute une nouvelle enseigne à partir des données reçues dans la requête.
     *
     * @param enseigneDTO les données de l'enseigne à créer (nom, adresse, utilisateur)
     * @return un objet {@link ResponseEntity} contenant l’enseigne créée si succès, ou une erreur HTTP sinon
     */
    @PostMapping("/ajouter")
    public ResponseEntity<Enseigne> ajouterEnseigne(@RequestBody Enseigne enseigneDTO) {
        try {
            Enseigne enseigne = enseigneService.ajouterEnseigne(
                enseigneDTO.getIdUtilisateur(),
                enseigneDTO.getNomEnseigne(),
                enseigneDTO.getAdresseEnseigne()
            );
            return ResponseEntity.ok(enseigne);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Récupère toutes les enseignes enregistrées qui ne sont pas administratives.
     * <p>
     * Cette méthode est utilisée pour lister les enseignes classiques associées à des utilisateurs.
     *
     * @return une réponse HTTP contenant la liste des enseignes, ou une erreur serveur en cas de problème
     */
    @GetMapping("/all")
    public ResponseEntity<List<Enseigne>> getEnseignesNonAdmin() {
        try {
            List<Enseigne> enseignes = enseigneService.getAllEnseignesNonAdmin();
            return ResponseEntity.ok(enseignes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    /**
     * Supprime une enseigne à partir de son identifiant composé (nom + adresse).
     *
     * @param nom_enseigne     le nom de l’enseigne à supprimer
     * @param adresse_enseigne l’adresse de l’enseigne à supprimer
     * @return une réponse HTTP avec un message de succès ou d’erreur
     */
    @DeleteMapping("/supprimer/{nom_enseigne}/{adresse_enseigne}")
    public ResponseEntity<String> supprimerEnseigne(@PathVariable String nom_enseigne,
                                                    @PathVariable String adresse_enseigne) {
        try {
            EnseigneId enseigneId = new EnseigneId(nom_enseigne, adresse_enseigne);
            enseigneService.supprimerEnseigne(enseigneId);
            return ResponseEntity.ok("Enseigne supprimée avec succès");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur côté serveur : " + e.getMessage());
        }
    }
}
