package com.example.argosapp.notification;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Entité représentant une notification dans le système.
 * <p>
 * Une notification peut correspondre à une alerte d'intrusion, une suspicion de vol
 * ou un dysfonctionnement technique (ex. déconnexion d'une caméra).
 * Elle est associée à un utilisateur et contient un message, un type, une date de création,
 * un statut de lecture, etc.
 */
@XmlRootElement // Permet la sérialisation en XML
@Entity
@Table(name = "notifications")
public class Notification {

    /**
     * Identifiant unique de la notification (clé primaire auto-générée).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Message décrivant le contenu ou le contexte de la notification.
     */
    private String message;

    /**
     * Type de notification (ex. "Intrusion", "Vol", ou type libre en cas de panne).
     */
    private String type;

    /**
     * Date et heure de génération de la notification.
     */
    @Column(name = "dateheure")
    private LocalDateTime dateHeure;

    /**
     * Statut de lecture de la notification.
     * {@code true} si elle a été consultée, {@code false} sinon.
     */
    private Boolean lu;

    /**
     * Identifiant de l'utilisateur auquel la notification est destinée.
     */
    @Column(name = "utilisateur_id")
    private String utilisateurId;

    /**
     * Constructeur par défaut requis par JPA.
     */
    public Notification() {}

    /**
     * Constructeur utilisé pour créer une notification personnalisée
     * selon un type standard ou un message libre (pour les erreurs système).
     *
     * @param type           le type de la notification ou message brut si non reconnu
     * @param utilisateurId  l'identifiant de l'utilisateur concerné
     */
    public Notification(String type, String utilisateurId) {
        switch (type) {
            case "Intrusion":
                this.type = type;
                this.message = "Alerte intrusion";
                break;
            case "Vol":
                this.type = type;
                this.message = "Suspicion de vol";
                break;
            default:
                this.type = "Disfonctionnement/Déconnexion des caméras";
                this.message = type;
                break;
        }
        this.dateHeure = LocalDateTime.now();
        this.lu = false;
        this.utilisateurId = utilisateurId;
    }

    /**
     * Définit l'identifiant utilisateur cible de la notification.
     *
     * @param utilisateurId identifiant de l'utilisateur concerné
     */
    public void setIdUtilisateur(String utilisateurId) {
        this.utilisateurId = utilisateurId;
    }

    /**
     * Définit la date et heure de la notification.
     *
     * @param dateHeure date de création
     */
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    /**
     * Définit l'identifiant unique de la notification.
     *
     * @param id identifiant de la notification
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Modifie le statut de lecture de la notification.
     *
     * @param lu {@code true} si lue, {@code false} sinon
     */
    public void setLu(Boolean lu) {
        this.lu = lu;
    }

    /**
     * Modifie le message associé à la notification.
     *
     * @param message texte explicatif
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Modifie le type de la notification.
     *
     * @param type libellé du type (ex. "Vol", "Intrusion")
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retourne l'identifiant de l'utilisateur associé.
     *
     * @return identifiant de l'utilisateur
     */
    @XmlElement
    public String getUtilisateurId() {
        return utilisateurId;
    }

    /**
     * Retourne la date de création de la notification.
     *
     * @return date et heure de génération
     */
    @XmlElement
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    /**
     * Retourne l'identifiant unique de la notification.
     *
     * @return identifiant de la notification
     */
    @XmlElement
    public Long getId() {
        return id;
    }

    /**
     * Indique si la notification a été lue.
     *
     * @return {@code true} si lue, {@code false} sinon
     */
    @XmlElement
    public Boolean getLu() {
        return lu;
    }

    /**
     * Retourne le message descriptif de la notification.
     *
     * @return message affiché à l'utilisateur
     */
    @XmlElement
    public String getMessage() {
        return message;
    }

    /**
     * Retourne le type de la notification.
     *
     * @return type ou catégorie
     */
    @XmlElement
    public String getType() {
        return type;
    }
}
