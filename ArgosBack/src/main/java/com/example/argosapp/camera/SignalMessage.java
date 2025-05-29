package com.example.argosapp.camera;

/**
 * Représente un message de signalisation utilisé dans une application
 * de communication en temps réel, comme WebRTC.
 * <p>
 * Ce type de message est utilisé pour l'échange d'informations entre pairs :
 * enregistrement d'identifiants, offres/réponses SDP et candidats ICE.
 * </p>
 */
public class SignalMessage {

    /**
     * Type du message :
     * <ul>
     *     <li>{@code "register"} : message d'enregistrement d'un client</li>
     *     <li>{@code "signal"} : message de signalisation (ex : offre, réponse, ICE)</li>
     * </ul>
     */
    private String type;

    /**
     * Identifiant du client émetteur du message.
     */
    private String clientId;

    /**
     * Identifiant du client destinataire du message.
     */
    private String targetId;

    /**
     * Données transportées par le message.
     * Typiquement une offre ou une réponse SDP, ou un candidat ICE.
     */
    private String data;

    /**
     * Type de signalisation, comme {@code "offer"}, {@code "answer"} ou {@code "candidate"}.
     */
    private String signalType;

    /**
     * Identifiant du flux multimédia concerné.
     * Utile si plusieurs flux doivent être distingués dans une même session.
     */
    private String streamId;

    /**
     * Retourne le type de message.
     *
     * @return le type (ex : "register", "signal")
     */
    public String getType() {
        return type;
    }

    /**
     * Définit le type de message.
     *
     * @param type le type à définir
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retourne l'identifiant du client émetteur.
     *
     * @return l'identifiant du client
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Définit l'identifiant du client émetteur.
     *
     * @param clientId identifiant du client
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * Retourne l'identifiant du client destinataire.
     *
     * @return l'identifiant cible
     */
    public String getTargetId() {
        return targetId;
    }

    /**
     * Définit l'identifiant du client destinataire.
     *
     * @param targetId identifiant cible
     */
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    /**
     * Retourne les données du message.
     *
     * @return les données de signalisation (SDP, ICE, etc.)
     */
    public String getData() {
        return data;
    }

    /**
     * Définit les données de signalisation.
     *
     * @param data les données à définir
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Retourne le type de signal (offer, answer, candidate).
     *
     * @return le type de signalisation
     */
    public String getSignalType() {
        return signalType;
    }

    /**
     * Définit le type de signalisation.
     *
     * @param signalType type de signal (offer, answer, candidate)
     */
    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    /**
     * Retourne l'identifiant du flux multimédia.
     *
     * @return l'identifiant du flux
     */
    public String getStreamId() {
        return streamId;
    }

    /**
     * Définit l'identifiant du flux multimédia.
     *
     * @param streamId identifiant du flux
     */
    public void setStreamId(String streamId) {
        this.streamId = streamId;
    }
}
