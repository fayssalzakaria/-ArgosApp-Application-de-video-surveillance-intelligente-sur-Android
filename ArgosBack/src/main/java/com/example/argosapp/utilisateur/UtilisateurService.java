package com.example.argosapp.utilisateur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.argosapp.jwt.JwtUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des utilisateurs dans l'application Argos.
 *
 * <p>
 * Ce service contient la logique métier liée à l'enregistrement, à
 * l'authentification
 * et à la récupération des utilisateurs. Il intègre le hachage de mot de passe
 * et la génération
 * de tokens JWT pour la sécurité.
 * </p>
 */
@Service
public class UtilisateurService {

    /**
     * Référentiel des utilisateurs pour l'accès aux données.
     */
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    /**
     * Utilitaire pour la génération de tokens JWT.
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Encodeur de mot de passe utilisant l'algorithme BCrypt.
     * <p>
     * Un "strength" de 12 est utilisé pour plus de sécurité.
     * </p>
     */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    /**
     * Enregistre un nouvel utilisateur avec son mot de passe encodé.
     *
     * @param utilisateur L'utilisateur à enregistrer.
     * @return L'utilisateur enregistré (persisté).
     */
    public Utilisateur enregistrerUtilisateur(Utilisateur utilisateur) {
        utilisateur.setPassword(passwordEncoder.encode(utilisateur.getPassword()));
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Authentifie un utilisateur via son identifiant et son mot de passe.
     *
     * @param id       L'identifiant de l'utilisateur.
     * @param password Le mot de passe brut saisi par l'utilisateur.
     * @return Un token JWT si les identifiants sont valides, sinon un
     *         {@code Optional} vide.
     */
    public Optional<String> authentifier(String id, String password) {
        return utilisateurRepository.findById(id)
                .filter(user -> passwordEncoder.matches(password, user.getPassword()))
                .map(user -> jwtUtil.generateToken(user.getId(), user.getIsAdmin()));
    }

    /**
     * Récupère tous les utilisateurs non administrateurs.
     *
     * @return Une liste d'utilisateurs qui ne sont pas administrateurs.
     */
    public List<Utilisateur> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return utilisateurs.stream()
                .filter(user -> !user.getIsAdmin()) // Filtrer ceux qui ne sont pas admin
                .collect(Collectors.toList());
    }

    /**
     * Cette méthode permet de supprimer un utilisateur par son identifiant
     * 
     * @param id représentant l'identifiant de l'utilisateur
     * @return true si la suppression a bien été faite, false sinon
     */
    public boolean supprimerUtilisateur(String id) {
        if (utilisateurRepository.existsById(id)) {
            utilisateurRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Récupère les informations d'un utilisateur sous forme de chaîne de
     * caractères.
     * 
     * <p>
     * Cette méthode retourne un format de chaîne contenant l'identifiant, le mot de
     * passe (crypté), le nom et le prénom de l'utilisateur spécifié par son
     * identifiant.
     * </p>
     *
     * @param id L'identifiant unique de l'utilisateur.
     * @return Une chaîne formatée contenant les informations de l'utilisateur, ou
     *         un message indiquant que l'utilisateur n'a pas été trouvé.
     */
    public String getInfosUtilisateur(String id) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(id);

        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            return String.format("ID: %s, Mot de passe: %s, Nom: %s, Prénom: %s, Enseigne: %s",
                    user.getId(), user.getPassword(), user.getNom(), user.getPrenom(), user.getEnseigne());
        } else {
            return "Utilisateur non trouvé";
        }
    }

    /**
     * Modifie le mot de passe d’un utilisateur cible (par un admin).
     *
     * @param idAdmin   identifiant de l’administrateur
     * @param idTarget  identifiant de l’utilisateur ciblé
     * @param nouveauMDP nouveau mot de passe à définir
     * @return true si la modification a réussi, false sinon
     */

    public boolean modifierMotDePasseAdmin(String idAdmin, String idTarget, String nouveauMDP) {
        Optional<Utilisateur> adminOpt = utilisateurRepository.findById(idAdmin);

        // Vérification de la présence de l'administrateur dans la BDD
        if (adminOpt.isPresent() && adminOpt.get().getIsAdmin()) {
            Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findById(idTarget);
            if (utilisateurOpt.isPresent()) {
                Utilisateur user = utilisateurOpt.get();
                // On crypte le nouveau mot de passe, avant de le stocker dans la BDD
                user.setPassword(passwordEncoder.encode(nouveauMDP));
                utilisateurRepository.save(user);
                return true;
            }
        }
        return false;
    }
}
