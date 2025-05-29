package com.example.argosapp.signal;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class SignalService {

    // Associe chaque clientId à une session WebSocket active.
    // Elle permet de retrouver la session d'un client pour lui envoyer un message.
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    // Utilisé pour convertir des objets Java en JSON (et inversement).
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Enregistre un nouveau client en associant son identifiant à sa session WebSocket.
     * Cela permet de le retrouver ultérieurement pour lui envoyer des messages.
     *
     * @param clientId identifiant unique du client
     * @param session  session WebSocket associée au client
     */
    public void registerClient(String clientId, WebSocketSession session) {
        sessions.put(clientId, session);
    }

    /**
     * Récupère la session WebSocket associée à un client donné.
     *
     * @param clientId identifiant du client
     * @return session WebSocket du client, ou null s’il n’est pas connecté
     */
    public WebSocketSession getSession(String clientId) {
        return sessions.get(clientId);
    }

    /**
     * Gère la transmission d'un message de signalisation vers un autre client.
     * Si la cible est connectée, le message lui est envoyé.
     *
     * @param message message de type SignalMessage contenant les informations à transmettre
     * @throws Exception en cas d'erreur lors de l'envoi du message
     */
    public void handleSignal(SignalMessage message) throws Exception {
        WebSocketSession target = sessions.get(message.getTargetId()); // Récupère la session du client cible
        if (target != null && target.isOpen()) {
            // Envoie le message converti en JSON à la cible via sa session WebSocket
            target.sendMessage(new TextMessage(mapper.writeValueAsString(message)));
        }
    }

    /**
     * Vérifie si un client est actuellement connecté (présent dans la map des sessions).
     *
     * @param clientId identifiant du client
     * @return true si le client est connecté, false sinon
     */
    public boolean isClientConnected(String clientId) {
        return sessions.containsKey(clientId);
    }
}