package com.example.argosapp.enseigne;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Représente une clé primaire composée pour l'entité {@link Enseigne}.
 * <p>
 * Cette clé est formée à partir du nom et de l'adresse de l'enseigne,
 * ce qui permet de garantir l’unicité d’une enseigne dans la base.
 * <p>
 * Elle est marquée avec {@code @Embeddable} pour être utilisée comme
 * identifiant intégré dans une entité JPA.
 */
@Embeddable
public class EnseigneId implements Serializable {

    /**
     * Nom de l'enseigne.
     */
    private String nom_enseigne;

    /**
     * Adresse de l'enseigne.
     */
    private String adresse_enseigne;

    /**
     * Constructeur sans argument requis par JPA.
     */
    public EnseigneId() {}

    /**
     * Construit une instance de {@code EnseigneId} avec nom et adresse.
     *
     * @param nom_enseigne     le nom de l’enseigne
     * @param adresse_enseigne l’adresse de l’enseigne
     */
    public EnseigneId(String nom_enseigne, String adresse_enseigne) {
        this.nom_enseigne = nom_enseigne;
        this.adresse_enseigne = adresse_enseigne;
    }

    /**
     * Retourne le nom de l’enseigne.
     *
     * @return le nom
     */
    public String getNom_enseigne() {
        return nom_enseigne;
    }

    /**
     * Définit le nom de l’enseigne.
     *
     * @param nom_enseigne le nom à définir
     */
    public void setNom_enseigne(String nom_enseigne) {
        this.nom_enseigne = nom_enseigne;
    }

    /**
     * Retourne l’adresse de l’enseigne.
     *
     * @return l’adresse
     */
    public String getAdresse_enseigne() {
        return adresse_enseigne;
    }

    /**
     * Définit l’adresse de l’enseigne.
     *
     * @param adresse_enseigne l’adresse à définir
     */
    public void setAdresse_enseigne(String adresse_enseigne) {
        this.adresse_enseigne = adresse_enseigne;
    }

    /**
     * Compare cette clé avec un autre objet.
     * Deux {@code EnseigneId} sont égaux s’ils ont le même nom et la même adresse.
     *
     * @param o l’objet à comparer
     * @return {@code true} si les deux objets sont égaux, sinon {@code false}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnseigneId)) return false;
        EnseigneId that = (EnseigneId) o;
        return Objects.equals(nom_enseigne, that.nom_enseigne) &&
               Objects.equals(adresse_enseigne, that.adresse_enseigne);
    }

    /**
     * Calcule le hashcode en fonction du nom et de l'adresse.
     *
     * @return une valeur de hachage cohérente avec {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return Objects.hash(nom_enseigne, adresse_enseigne);
    }
}
