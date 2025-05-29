package com.example.argosapp;

/**
 * Modèle de données représentant un utilisateur simplifié, typiquement utilisé pour l'authentification ou les échanges API.
 * <p>
 * Cette classe contient les informations de base : identifiant, nom, prénom, mot de passe et enseigne associée.
 */
public class User {

    /**
     * Identifiant unique de l'utilisateur.
     */
    private String id;

    /**
     * Nom de famille de l'utilisateur.
     */
    private String nom;

    /**
     * Prénom de l'utilisateur.
     */
    private String prenom;

    /**
     * Enseigne ou organisme auquel est rattaché l'utilisateur.
     */
    private String enseigne;

    /**
     * Mot de passe de l'utilisateur.
     */
    private String password;

    /**
     * Constructeur principal de la classe {@code User}.
     *
     * @param id         identifiant de l'utilisateur
     * @param nom        nom de famille
     * @param prenom     prénom
     * @param password   mot de passe
     * @param organisme  nom de l'organisme ou enseigne
     */
    public User(String id, String nom, String prenom, String password, String organisme) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.enseigne = organisme;
    }

    public String getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEnseigne() {
        return enseigne;
    }

    public String getPassword() {
        return password;
    }
}
