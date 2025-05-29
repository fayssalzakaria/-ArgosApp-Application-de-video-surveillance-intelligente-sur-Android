package com.example.argosapp;

// Cette classe représente un message échangé entre les clients et le serveur de signalisation WebRTC.
// Elle est utilisée pour la communication via WebSocket.

public class SignalMessage {
    // Type général du message (ex : "register", "signal", etc.)
    private String type;

    // Identifiant du client qui envoie le message
    private String clientId;

    // Identifiant du client cible (celui qui doit recevoir le message)
    private String targetId;

    // Type de signal WebRTC (ex : "offer", "answer", "candidate")
    private String signalType;

    // Contenu du message (ex : description SDP, candidate ICE, etc.)
    private String data;

    // Identifiant du flux média (utile pour identifier un stream précis dans l'échange)
    private String streamId;

    // Getter pour streamId
    public String getStreamId() { return streamId; }

    // Setter pour streamId
    public void setStreamId(String streamId) { this.streamId = streamId; }

    // Getter pour type
    public String getType() {
        return type;
    }

    // Setter pour type
    public void setType(String type) {
        this.type = type;
    }

    // Getter pour clientId
    public String getClientId() {
        return clientId;
    }

    // Setter pour clientId
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    // Getter pour targetId
    public String getTargetId() {
        return targetId;
    }

    // Setter pour targetId
    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    // Getter pour signalType
    public String getSignalType() {
        return signalType;
    }

    // Setter pour signalType
    public void setSignalType(String signalType) {
        this.signalType = signalType;
    }

    // Getter pour data
    public String getData() {
        return data;
    }

    // Setter pour data
    public void setData(String data) {
        this.data = data;
    }
}
