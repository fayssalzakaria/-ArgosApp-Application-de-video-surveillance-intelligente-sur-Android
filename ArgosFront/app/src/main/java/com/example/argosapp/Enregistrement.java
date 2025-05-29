package com.example.argosapp;

public class Enregistrement {

    private Long id;
    private String dateajout;
    private String dateexpiration;
    private String nomfichier;
    private Long notification_id;
    private String utilisateurId;
    private String nomProbleme;

    public Enregistrement() {}

    public Enregistrement(String dateajout, String dateexpiration, String nomfichier, Long notification_id, String utilisateurId,String nomProbleme) {
        this.dateajout = dateajout;
        this.dateexpiration = dateexpiration;
        this.nomfichier = nomfichier;
        this.notification_id = notification_id;
        this.utilisateurId = utilisateurId;
    }
    public String getNomProbleme() { return nomProbleme; }
    public Long getId() { return id; }
    public String getDateajout() { return dateajout; }
    public String getDateexpiration() { return dateexpiration; }
    public String getNomfichier() { return nomfichier; }
    public Long getNotification_id() { return notification_id; }
    public String getUtilisateurId() { return utilisateurId; }
    public void setNomProbleme(String nomProbleme) { this.nomProbleme = nomProbleme; }
    public void setId(Long id) { this.id = id; }
    public void setDateajout(String dateajout) { this.dateajout = dateajout; }
    public void setDateexpiration(String dateexpiration) { this.dateexpiration = dateexpiration; }
    public void setNomfichier(String nomfichier) { this.nomfichier = nomfichier; }
    public void setNotification_id(Long notification_id) { this.notification_id = notification_id; }
    public void setUtilisateurId(String utilisateurId) { this.utilisateurId = utilisateurId; }
}
