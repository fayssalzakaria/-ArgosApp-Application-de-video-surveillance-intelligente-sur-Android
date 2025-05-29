package com.example.argosapp.enseigne;

import jakarta.persistence.*;
import java.util.Objects;
import jakarta.persistence.Entity;

/**
 * Entité représentant une enseigne, identifiée par un identifiant composé (nom + adresse).
 * <p>
 * Une enseigne est associée à un utilisateur via son identifiant {@code idUtilisateur}.
 * L'identifiant composé est encapsulé dans la classe {@link EnseigneId} et utilisé via {@code @EmbeddedId}.
 */
@Entity
public class Enseigne {

    /**
     * Identifiant composé de l'enseigne (nom + adresse).
     */
    @EmbeddedId
    private EnseigneId id;

    /**
     * Identifiant de l'utilisateur auquel appartient l'enseigne.
     */
    @Column(name = "id_utilisateur", nullable = false)
    private String idUtilisateur;

    /**
     * Constructeur sans argument requis par JPA.
     */
    public Enseigne() {}

    /**
     * Construit une enseigne avec les informations de base.
     *
     * @param idUtilisateur     l'identifiant de l'utilisateur propriétaire
     * @param nom_enseigne      le nom de l'enseigne
     * @param adresse_enseigne  l'adresse de l'enseigne
     */
    public Enseigne(String idUtilisateur, String nom_enseigne, String adresse_enseigne) {
        this.id = new EnseigneId(nom_enseigne, adresse_enseigne);
        this.idUtilisateur = idUtilisateur;
    }

    /**
     * Retourne l'identifiant composé de l'enseigne.
     *
     * @return un objet {@link EnseigneId} représentant le nom et l'adresse
     */
    public EnseigneId getId() {
        return id;
    }

    /**
     * Définit l'identifiant composé de l'enseigne.
     *
     * @param id l'identifiant composé (nom + adresse)
     */
    public void setId(EnseigneId id) {
        this.id = id;
    }

    /**
     * Retourne l'identifiant de l'utilisateur propriétaire de l'enseigne.
     *
     * @return l'identifiant utilisateur
     */
    public String getIdUtilisateur() {
        return idUtilisateur;
    }

    /**
     * Définit l'identifiant de l'utilisateur propriétaire.
     *
     * @param idUtilisateur l'identifiant de l'utilisateur
     */
    public void setIdUtilisateur(String idUtilisateur) {
        this.idUtilisateur = idUtilisateur;
    }

    /**
     * Retourne le nom de l'enseigne.
     *
     * @return le nom de l'enseigne
     */
    public String getNomEnseigne() {
        return id.getNom_enseigne();
    }

    /**
     * Modifie le nom de l'enseigne dans l'identifiant composé.
     *
     * @param nomEnseigne le nouveau nom de l'enseigne
     */
    public void setNomEnseigne(String nomEnseigne) {
        id.setNom_enseigne(nomEnseigne);
    }

    /**
     * Retourne l'adresse de l'enseigne.
     *
     * @return l'adresse de l'enseigne
     */
    public String getAdresseEnseigne() {
        return id.getAdresse_enseigne();
    }

    /**
     * Modifie l'adresse de l'enseigne dans l'identifiant composé.
     *
     * @param adresseEnseigne la nouvelle adresse de l'enseigne
     */
    public void setAdresseEnseigne(String adresseEnseigne) {
        id.setAdresse_enseigne(adresseEnseigne);
    }

    /**
     * Retourne une représentation textuelle de l'enseigne.
     *
     * @return une chaîne représentant l’enseigne
     */
    @Override
    public String toString() {
        return "Enseigne{" +
                "nom_enseigne='" + id.getNom_enseigne() + '\'' +
                ", adresse_enseigne='" + id.getAdresse_enseigne() + '\'' +
                ", idUtilisateur='" + idUtilisateur + '\'' +
                '}';
    }

    /**
     * Vérifie l'égalité entre deux objets {@code Enseigne} sur la base de leur identifiant.
     *
     * @param o l’objet à comparer
     * @return {@code true} si les deux objets sont égaux, {@code false} sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Enseigne)) return false;
        Enseigne enseigne = (Enseigne) o;
        return Objects.equals(id, enseigne.id);
    }

    /**
     * Calcule le hashcode de l'objet {@code Enseigne}, basé sur son identifiant.
     *
     * @return un entier représentant le hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
