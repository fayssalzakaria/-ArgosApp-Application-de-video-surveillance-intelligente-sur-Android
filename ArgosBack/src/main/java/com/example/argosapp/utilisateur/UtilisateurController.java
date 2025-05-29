package com.example.argosapp.utilisateur;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.example.argosapp.jwt.JwtUtil;
import com.example.argosapp.jwt.TokenBlacklistService;

import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Contrôleur REST pour gérer les utilisateurs dans l'application Argos.
 *
 * <p>
 * Ce contrôleur fournit des endpoints pour :
 * </p>
 * <ul>
 * <li>Ajouter un utilisateur</li>
 * <li>Authentifier un utilisateur (login)</li>
 * <li>Déconnecter un utilisateur (logout)</li>
 * <li>Valider un token JWT</li>
 * <li>Récupérer la liste de tous les utilisateurs</li>
 * </ul>
 *
 * <p>
 * Les tokens JWT sont utilisés pour gérer l’authentification et la sécurité.
 * </p>
 *
 */
@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateurController {

    /**
     * Service de gestion de la logique métier des utilisateurs.
     */
    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Utilitaire pour la création et validation des tokens JWT.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Service pour gérer les tokens JWT mis sur liste noire (invalidation des
     * tokens).
     */
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Endpoint pour ajouter un nouvel utilisateur.
     *
     * @param utilisateur L'utilisateur à enregistrer.
     * @return Une réponse HTTP confirmant l'ajout de l'utilisateur.
     */
    @PostMapping("/ajouter")
    public ResponseEntity<String> ajouterUtilisateur(@RequestBody Utilisateur utilisateur) {
        utilisateurService.enregistrerUtilisateur(utilisateur);
        return ResponseEntity.ok("Utilisateur ajouté avec succès");
    }

    /**
     * Endpoint pour authentifier un utilisateur et générer un token JWT.
     *
     * @param loginRequest Une map contenant l'ID et le mot de passe.
     * @return Un token JWT si l'authentification est réussie ; sinon une erreur
     *         401.
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Object> loginRequest) {
        if (!loginRequest.containsKey("id") || !loginRequest.containsKey("password")) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur : ID et mot de passe doivent être fournis.");
        }
    
        String id = loginRequest.get("id").toString();
        String password = loginRequest.get("password").toString();
    
        if (id.isBlank() || password.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Erreur : ID et mot de passe ne doivent pas être vides.");
        }
    
        Optional<String> token = utilisateurService.authentifier(id, password);
        if (token.isPresent()) {
            return ResponseEntity
                    .ok(token.get()); // On retourne directement le token en String
        } else {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Erreur : Identifiants invalides. Veuillez vérifier votre ID et votre mot de passe.");
        }
    }
    

    /**
     * Endpoint pour déconnecter un utilisateur en invalidant son token JWT.
     
     * @return Une réponse HTTP confirmant la déconnexion.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Aucun utilisateur authentifié");
        }

        String token = (String) authentication.getCredentials(); 
        tokenBlacklistService.blacklistToken(token);
        return ResponseEntity.ok("Déconnexion réussie");
    }


    /**
     * Endpoint pour valider un token JWT.
     * @return true si le token est valide, false sinon.
     */
    @GetMapping("/validate-token")
    public ResponseEntity<Boolean> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.ok(false);
        }
    
        Object credentials = authentication.getCredentials();
        if (credentials == null || !(credentials instanceof String token)) {
            return ResponseEntity.ok(false);
        }
    
        try {
            boolean isValid = jwtUtil.validateToken(token) && !tokenBlacklistService.isTokenBlacklisted(token);
            return ResponseEntity.ok(isValid);
        } catch (Exception e) {

            return ResponseEntity.ok(false);
        }
    }
    
    
    
    /**
     * Endpoint pour récupérer tous les utilisateurs enregistrés.
     *
     * @return La liste de tous les utilisateurs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    /**
     * Création du Endpoint pour supprimer un utilisateur par son identifiant.
     * 
     * @param id représentant l'identifiant de l'utilisateur à supprimer
     * @return le résultat de la suppression
     */
    @DeleteMapping("/supprimer/{id}")
    public ResponseEntity<String> supprimerUtilisateur(@PathVariable String id) {
        boolean suppression = utilisateurService.supprimerUtilisateur(id);
        if (suppression) {
            return ResponseEntity.ok("Utilisateur supprimée !");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non supprimée");
        }
    }

    /**
     * Récupère les informations d'un utilisateur sous forme de chaîne de caractères
     * via un endpoint REST.
     * 
     * <p>
     * Cette méthode expose un endpoint GET permettant de récupérer les informations
     * de l'utilisateur spécifié par son identifiant. Les informations retournées
     * incluent l'identifiant, le mot de passe (crypté), le nom et le prénom.
     * </p>
     *
     * @param id L'identifiant de l'utilisateur à récupérer.
     * @return Une réponse HTTP contenant une chaîne de caractères avec les
     *         informations de l'utilisateur, ou un message d'erreur si
     *         l'utilisateur
     *         n'est pas trouvé.
     */
    @GetMapping("/infos/{id}")
    public ResponseEntity<String> getInfosUtilisateur(@PathVariable String id) {
        String infos = utilisateurService.getInfosUtilisateur(id);
        return ResponseEntity.ok(infos);
    }
    /**
     * Permet à un administrateur de modifier le mot de passe d’un autre utilisateur.
     *
     * @param payload map contenant les clés : adminID, idTarget, nouveauPassword
     * @return message indiquant le succès ou l’échec de la modification
     */
    @PutMapping("/changer-mdp")
    public ResponseEntity<String> changerMotDePasseAdmin(@RequestBody Map<String, String> payload) {
        String adminId = payload.get("adminID");
        String idTarget = payload.get("idTarget");
        String nouveauPassword = payload.get("nouveauPassword");

        boolean modifier = utilisateurService.modifierMotDePasseAdmin(adminId, idTarget, nouveauPassword);
        if (modifier) {
            return ResponseEntity.ok("Mot de passe modifié avec succès");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Modification du mot de passe échoué !");
        }
    }

}
