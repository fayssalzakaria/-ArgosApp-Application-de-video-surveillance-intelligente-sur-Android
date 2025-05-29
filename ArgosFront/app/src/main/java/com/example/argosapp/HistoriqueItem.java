package com.example.argosapp;

/**
 * Classe représentant un élément de l'historique affiché dans la page {@link HistoriqueActivity}.
 * Chaque enregistrement contient un nom (ou date) et un type de problème détecté.
 */
public class HistoriqueItem {

    /** Nom ou identifiant de l'enregistrement (souvent lié à une date ou une caméra) */
    private String nomEnregistrement;

    /** Problème détecté, tel que "Alerte intrusion" ou "Suspicion de vol" */
    private String probleme;

    private String videoUrl;

    /**
     * Constructeur principal.
     *
     * @param nomEnregistrement Nom ou description de l'enregistrement
     * @param probleme          Type de problème associé à cet enregistrement
     */
    public HistoriqueItem(String nomEnregistrement, String probleme, String videoUrl) {
        this.nomEnregistrement = nomEnregistrement;
        this.probleme = probleme;
        this.videoUrl=videoUrl;
    }

    /**
     * Retourne le nom ou identifiant de l'enregistrement.
     *
     * @return le nom de l'enregistrement
     */
    public String getNomEnregistrement() {
        return nomEnregistrement;
    }

    /**
     * Retourne le type de problème lié à l'enregistrement.
     *
     * @return le problème détecté
     */
    public String getProbleme() {
        return probleme;
    }

    public String getVideoUrl(){ return videoUrl; }
}