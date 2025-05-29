package com.example.argosapp.utilisateur;

import jakarta.persistence.*;
import lombok.*;

/**
 * Représente un utilisateur dans l'application Argos.
 * <p>
 * Cette classe est une entité JPA mappée à une table de base de données.
 * Elle utilise Lombok pour générer automatiquement les constructeurs, getters
 * et setters, afin de réduire le code répétitif.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    /**
     * Identifiant unique de l'utilisateur.
     * <p>
     * Utilise une chaîne de caractères (ex. UUID) pour garantir l'unicité
     * tout en conservant de la flexibilité.
     */
    @Id
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
     * Mot de passe de l'utilisateur.
     * <p>
     * Ce champ devrait être stocké de manière sécurisée (hachage + salage).
     */
    private String password;

    /**
     * Nom de l'enseigne ou de la société associée à l'utilisateur.
     */
    private String enseigne;

    /**
     * Indique si l'utilisateur a les droits d'administration.
     */
    @Column(name = "isadmin")
    private boolean isadmin;

    /**
     * Retourne le statut administrateur de l'utilisateur.
     *
     * @return {@code true} si l'utilisateur est un administrateur, sinon {@code false}
     */
    public Boolean getIsAdmin() {
        return isadmin;
    }

    /**
     * Représentation textuelle de l'utilisateur (utile pour le débogage).
     *
     * @return chaîne décrivant l'état de l'objet Utilisateur
     */
    @Override
    public String toString() {
        return "Utilisateur(id=" + id +
                ", nom=" + nom +
                ", prenom=" + prenom +
                ", password=" + password +
                ", enseigne=" + enseigne +
                ", isadmin=" + isadmin + ")";
    }
}
