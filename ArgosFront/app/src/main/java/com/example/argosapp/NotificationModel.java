package com.example.argosapp;

/**
 * Modèle représentant une notification reçue par l'utilisateur.
 * Contient un identifiant, un message, une date/heure et un indicateur de lecture.
 */
public class NotificationModel {

    /** Identifiant unique de la notification */
    private Long id;

    /** Message ou contenu de la notification */
    private String message;

    /** Date et heure de création ou de réception de la notification */
    private String dateHeure;

    /** Indique si la notification a été lue */
    private boolean isRead;

    /**
     * Constructeur de la classe NotificationModel.
     *
     * @param id        Identifiant unique
     * @param isRead    Statut de lecture
     * @param dateHeure Date et heure de l'événement
     * @param message   Contenu de la notification
     */
    public NotificationModel(Long id, Boolean isRead, String dateHeure, String message) {
        this.id = id;
        this.message = message;
        this.dateHeure = dateHeure;
        this.isRead = isRead;
    }

    /**
     * Définit l'identifiant de la notification.
     *
     * @param id identifiant unique
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Définit le statut de lecture de la notification.
     *
     * @param read true si lue, false sinon
     */
    public void setRead(boolean read) {
        this.isRead = read;
    }

    /**
     * Définit le message de la notification.
     *
     * @param message texte du message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Définit la date et l'heure de la notification.
     *
     * @param dateHeure date et heure au format texte
     */
    public void setDateHeure(String dateHeure) {
        this.dateHeure = dateHeure;
    }

    /**
     * Retourne l'identifiant de la notification.
     *
     * @return identifiant
     */
    public Long getId() {
        return id;
    }

    /**
     * Retourne le contenu de la notification.
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retourne la date et l'heure de la notification.
     *
     * @return date et heure
     */
    public String getDateHeure() {
        return dateHeure;
    }

    /**
     * Indique si la notification a été lue.
     *
     * @return true si lue, false sinon
     */
    public boolean getIsRead() {
        return isRead;
    }
}
