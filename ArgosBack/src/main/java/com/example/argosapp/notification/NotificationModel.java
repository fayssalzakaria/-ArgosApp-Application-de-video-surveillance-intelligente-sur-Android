package com.example.argosapp.notification;

/**
 * Modèle de données pour représenter une notification dans les réponses API.
 * <p>
 * Sert à exposer uniquement les informations utiles au client, sans les entités complètes.
 */
public class NotificationModel {

    /**
     * Identifiant unique de la notification.
     */
    private Long id;

    /**
     * Date et heure de la notification sous forme de chaîne.
     */
    private String dateHeure;

    /**
     * Message contenu dans la notification.
     */
    private String message;

    /**
     * Indique si la notification a été lue.
     */
    private Boolean isRead;

    /**
     * Constructeur avec paramètres pour initialiser tous les champs.
     *
     * @param id         identifiant de la notification
     * @param isRead     statut de lecture (true si lue)
     * @param dateHeure  date et heure de la notification (format texte)
     * @param message    message associé à la notification
     */
    public NotificationModel(Long id, Boolean isRead, String dateHeure, String message) {
        this.id = id;
        this.dateHeure = dateHeure;
        this.message = message;
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
     * Définit si la notification a été lue.
     *
     * @param isRead true si la notification est lue, false sinon
     */
    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Définit la date et l'heure de la notification.
     *
     * @param dateHeure date/heure sous forme de chaîne
     */
    public void setDateHeure(String dateHeure) {
        this.dateHeure = dateHeure;
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
     * Retourne la date et l'heure de la notification.
     *
     * @return date et heure au format texte
     */
    public String getDateHeure() {
        return dateHeure;
    }

    /**
     * Retourne le message de la notification.
     *
     * @return message associé
     */
    public String getMessage() {
        return message;
    }

    /**
     * Retourne le statut de lecture.
     *
     * @return true si lue, false sinon
     */
    public Boolean getIsRead() {
        return isRead;
    }

    /**
     * Retourne l'identifiant de la notification.
     *
     * @return identifiant unique
     */
    public Long getId() {
        return id;
    }
}
