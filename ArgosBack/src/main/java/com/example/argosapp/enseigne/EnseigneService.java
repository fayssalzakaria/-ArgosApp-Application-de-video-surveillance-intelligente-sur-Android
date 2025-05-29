package com.example.argosapp.enseigne;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.argosapp.utilisateur.Utilisateur;
import com.example.argosapp.utilisateur.UtilisateurRepository;

/**
 * Service métier pour la gestion des enseignes.
 * <p>
 * Fournit les opérations de création, suppression et récupération des enseignes,
 * en tenant compte des droits d'administration des utilisateurs.
 */
@Service
public class EnseigneService {

    private EnseigneRepository enseigneRepository;
    private UtilisateurRepository utilisateurRepository;

    /**
     * Constructeur du service {@code EnseigneService}.
     * <p>
     * Initialise les dépendances nécessaires à la gestion des enseignes.
     *
     * @param enseigneRepository         le repository JPA des enseignes
     * @param utilisateurRepository      le repository JPA des utilisateurs
     */
    @Autowired
    public EnseigneService(EnseigneRepository enseigneRepository, UtilisateurRepository utilisateurRepository) {
        this.enseigneRepository = enseigneRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    /**
     * Ajoute une nouvelle enseigne à partir des informations fournies.
     *
     * @param identifiant      l'identifiant de l'utilisateur propriétaire
     * @param nomEnseigne      le nom de l'enseigne
     * @param adresseEnseigne  l'adresse de l'enseigne
     * @return l'enseigne créée et persistée
     * @throws IllegalArgumentException si un des champs requis est vide ou nul
     */
    public Enseigne ajouterEnseigne(String identifiant, String nomEnseigne, String adresseEnseigne) {
        if (identifiant == null || identifiant.trim().isEmpty()) {
            throw new IllegalArgumentException("L'identifiant est nécessaire pour poursuivre");
        }
        if (nomEnseigne == null || nomEnseigne.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom de l'enseigne est requis");
        }
        if (adresseEnseigne == null || adresseEnseigne.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse de l'enseigne est requise");
        }

        EnseigneId enseigneId = new EnseigneId(nomEnseigne, adresseEnseigne);
        Enseigne enseigne = new Enseigne();
        enseigne.setId(enseigneId);
        enseigne.setIdUtilisateur(identifiant);

        return enseigneRepository.save(enseigne);
    }

    /**
     * Supprime une enseigne identifiée par son identifiant composite.
     *
     * @param enseigneId identifiant composite de l'enseigne (nom + adresse)
     * @throws IllegalArgumentException si l'enseigne n'existe pas
     */
    public void supprimerEnseigne(EnseigneId enseigneId) throws IllegalArgumentException {
        if (!enseigneRepository.existsById(enseigneId)) {
            throw new IllegalArgumentException("Cette enseigne n'existe pas !");
        }
        enseigneRepository.deleteById(enseigneId);
    }

    /**
     * Récupère toutes les enseignes dont l'utilisateur associé n'est pas administrateur.
     *
     * @return une liste filtrée d'enseignes non administratives
     */
    public List<Enseigne> getAllEnseignesNonAdmin() {
        List<Enseigne> enseignes = enseigneRepository.findAll();
        return enseignes.stream()
                .filter(enseigne -> {
                    Utilisateur utilisateur = utilisateurRepository.findById(enseigne.getIdUtilisateur()).orElse(null);
                    return utilisateur != null && !utilisateur.getIsAdmin();
                })
                .collect(Collectors.toList());
    }
}
