package com.example.argosapp;

/**
 * Interface définissant les méthodes de rappel (callbacks) pour recevoir
 * des notifications via WebSocket concernant des événements de sécurité.
 *
 * <p>Implémentez cette interface pour être notifié lorsqu'une intrusion
 * ou une détection de volume est signalée par le serveur.</p>
 */
public interface WebSocketNotificationListener {

    /**
     * Méthode appelée lorsqu'une intrusion est détectée.
     *
     * @param message Le message détaillant l'intrusion détectée.
     */
    void onIntrusionDetected(String message);

    /**
     * Méthode appelée lorsqu'une détection de volume est signalée.
     *
     * @param message Le message contenant les informations sur la détection de volume.
     */
    void onVolDetected(String message);
}
