package com.example.argosapp;

import com.google.gson.annotations.SerializedName;

/**
 * Classe représentant un utilisateur.
 * <p>
 * Cette classe est utilisée pour la sérialisation et la désérialisation
 * des objets JSON avec la bibliothèque Retrofit et Gson.
 */
public class Utilisateur {

    /**
     * Identifiant unique de l'utilisateur.
     */
    @SerializedName("id")
    private String id;

    /**
     * Nom de famille de l'utilisateur.
     */
    @SerializedName("nom")
    private String nom;

    /**
     * Prénom de l'utilisateur.
     */
    @SerializedName("prenom")
    private String prenom;

    /**
     * Mot de passe de l'utilisateur.
     */
    @SerializedName("password")
    private String password;

    /**
     * Enseigne (entreprise ou marque) associée à l'utilisateur.
     */
    @SerializedName("enseigne")
    private String enseigne;

    /**
     * Constructeur de la classe Utilisateur.
     *
     * @param id        Identifiant unique de l'utilisateur.
     * @param nom       Nom de l'utilisateur.
     * @param prenom    Prénom de l'utilisateur.
     * @param password  Mot de passe de l'utilisateur.
     * @param enseigne  Enseigne associée à l'utilisateur.
     */
    public Utilisateur(String id, String nom, String prenom, String password, String enseigne) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.password = password;
        this.enseigne = enseigne;
    }

    // Getters et setters

    /**
     * Retourne l'identifiant de l'utilisateur.
     *
     * @return L'identifiant de l'utilisateur.
     */
    public String getId() {
        return id;
    }

    /**
     * Modifie l'identifiant de l'utilisateur.
     *
     * @param id Le nouvel identifiant.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Retourne le nom de l'utilisateur.
     *
     * @return Le nom de l'utilisateur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Modifie le nom de l'utilisateur.
     *
     * @param nom Le nouveau nom.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Retourne le prénom de l'utilisateur.
     *
     * @return Le prénom de l'utilisateur.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Modifie le prénom de l'utilisateur.
     *
     * @param prenom Le nouveau prénom.
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * Retourne le mot de passe de l'utilisateur.
     *
     * @return Le mot de passe.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Modifie le mot de passe de l'utilisateur.
     *
     * @param password Le nouveau mot de passe.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Retourne l'enseigne associée à l'utilisateur.
     *
     * @return Le nom de l'enseigne.
     */
    public String getEnseigne() {
        return enseigne;
    }

    /**
     * Modifie l'enseigne associée à l'utilisateur.
     *
     * @param enseigne La nouvelle enseigne.
     */
    public void setEnseigne(String enseigne) {
        this.enseigne = enseigne;
    }
}
