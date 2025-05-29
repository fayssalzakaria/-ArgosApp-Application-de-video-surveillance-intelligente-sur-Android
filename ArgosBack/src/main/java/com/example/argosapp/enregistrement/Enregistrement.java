package com.example.argosapp.enregistrement;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

import com.example.argosapp.notification.Notification;

@Entity
@Getter
@Setter

@Table(name = "enregistrements")

public class Enregistrement {

public Enregistrement(Instant dateajout, Instant dateexpiration, String nomfichier, Long notification_id, String utilisateur_id) {
    
    this.dateajout = dateajout;
    this.dateexpiration = dateexpiration;
    this.nomfichier = nomfichier;
    this.notification_id = notification_id;
    this.utilisateurId = utilisateur_id;

}
    public Enregistrement() {
        // Constructeur vide requis par JPA
    }

    public String getNomProbleme() { return nomProbleme; }
    public void setNomProbleme(String nomProbleme) { this.nomProbleme = nomProbleme; }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant dateajout;

    private Instant dateexpiration;

    private String nomfichier;

    private Long notification_id;
    @Column(name = "utilisateur_id") // lie à la colonne utilisateur_id en BDD
    private String utilisateurId;
    
    @Transient
    private String nomProbleme;

}
