package com.example.argosapp;

/**
 * Modèle représentant une enseigne associée à un utilisateur.
 * Utilisée notamment lors de la création, la modification ou la suppression d'enseignes via l'API.
 */
public class Enseigne {

    /** Identifiant de l'utilisateur responsable de l'enseigne */
    private String idUtilisateur;

    /** Nom de l'enseigne */
    private String nomEnseigne;

    /** Adresse physique de l'enseigne */
    private String adresseEnseigne;

    /**
     * Constructeur par défaut requis pour la désérialisation (ex. : Gson).
     */
    public Enseigne() {}

    /**
     * Constructeur avec paramètres pour créer une instance d'enseigne.
     *
     * @param idUtilisateur     identifiant de l'utilisateur lié à l'enseigne
     * @param nomEnseigne       nom de l'enseigne
     * @param adresseEnseigne   adresse de l'enseigne
     */
    public Enseigne(String idUtilisateur, String nomEnseigne, String adresseEnseigne) {
        this.idUtilisateur = idUtilisateur;
        this.nomEnseigne = nomEnseigne;
        this.adresseEnseigne = adresseEnseigne;
    }

    /**
     * Retourne l'identifiant de l'utilisateur lié à cette enseigne.
     *
     * @return id de l'utilisateur
     */
    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    /**
     * Définit l'identifiant de l'utilisateur lié à cette enseigne.
     *
     * @param idUtilisateur nouvel identifiant utilisateur
     */
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    /**
     * Retourne le nom de l'enseigne.
     *
     * @return nom de l'enseigne
     */
    public String getNomEnseigne() {
        return nomEnseigne;
    }

    /**
     * Définit le nom de l'enseigne.
     *
     * @param nomEnseigne nouveau nom d'enseigne
     */
    public void setNomEnseigne(String nomEnseigne) {
        this.nomEnseigne = nomEnseigne;
    }

    /**
     * Retourne l'adresse de l'enseigne.
     *
     * @return adresse de l'enseigne
     */
    public String getAdresseEnseigne() {
        return adresseEnseigne;
    }

    /**
     * Définit l'adresse de l'enseigne.
     *
     * @param adresseEnseigne nouvelle adresse de l'enseigne
     */
    public void setAdresseEnseigne(String adresseEnseigne) {
        this.adresseEnseigne = adresseEnseigne;
    }

    /**
     * Représentation textuelle de l'objet Enseigne.
     *
     * @return chaîne formatée représentant l'objet
     */
    @Override
    public String toString() {
        return "Enseigne{" +
                "idUtilisateur='" + idUtilisateur + '\'' +
                ", nomEnseigne='" + nomEnseigne + '\'' +
                ", adresseEnseigne='" + adresseEnseigne + '\'' +
                '}';
    }
}