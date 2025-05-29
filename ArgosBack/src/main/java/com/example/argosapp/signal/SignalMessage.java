package com.example.argosapp.signal;

/**
 * Classe représentant un message de signalisation utilisé dans une application
 * de communication en temps réel (par exemple WebRTC).
 * Elle sert à l'échange d'informations entre les pairs, telles que
 * les identifiants, les offres/réponses SDP, et les candidats ICE.
 */
public class SignalMessage {
    private String type;        // Type du message : "register" pour l'enregistrement, "signal" pour l'échange de signaux
    private String clientId;    // Identifiant du client émetteur du message
    private String targetId;    // Identifiant du client destinataire du message
    private String data;        // Données de signalisation : SDP (Session Description Protocol) ou ICE candidate
    private String signalType;  // Type de signalisation : "offer", "answer" ou "candidate"
    private String streamId;    // Identifiant du flux multimédia (utile pour différencier plusieurs flux)

    // Accesseur pour le champ 'type'
    public String getType() { return type; }

    // Mutateur pour le champ 'type'
    public void setType(String type) { this.type = type; }

    // Accesseur pour le champ 'clientId'
    public String getClientId() { return clientId; }

    // Mutateur pour le champ 'clientId'
    public void setClientId(String clientId) { this.clientId = clientId; }

    // Accesseur pour le champ 'targetId'
    public String getTargetId() { return targetId; }

    // Mutateur pour le champ 'targetId'
    public void setTargetId(String targetId) { this.targetId = targetId; }

    // Accesseur pour le champ 'data'
    public String getData() { return data; }

    // Mutateur pour le champ 'data'
    public void setData(String data) { this.data = data; }

    // Accesseur pour le champ 'signalType'
    public String getSignalType() { return signalType; }

    // Mutateur pour le champ 'signalType'
    public void setSignalType(String signalType) { this.signalType = signalType; }

    // Accesseur pour le champ 'streamId'
    public String getStreamId() { return streamId; }

    // Mutateur pour le champ 'streamId'
    public void setStreamId(String streamId) { this.streamId = streamId; }
}
