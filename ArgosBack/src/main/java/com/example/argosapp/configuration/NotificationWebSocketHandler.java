package com.example.argosapp.configuration;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire WebSocket pour les notifications en temps réel.
 * <p>
 * Ce gestionnaire permet :
 * <ul>
 *   <li>La connexion et la déconnexion des sessions WebSocket</li>
 *   <li>L'authentification simple via un message contenant un ID utilisateur</li>
 *   <li>L'envoi ciblé de messages à un utilisateur connecté</li>
 * </ul>
 */
public class NotificationWebSocketHandler extends TextWebSocketHandler {

    /**
     * Map contenant les sessions WebSocket actives et leur utilisateur associé.
     * La clé est la session WebSocket, et la valeur est l'identifiant utilisateur.
     */
    private static final Map<WebSocketSession, String> sessionUtilisateurMap = new ConcurrentHashMap<>();

    /**
     * Méthode appelée après l’établissement d’une nouvelle connexion WebSocket.
     *
     * @param session la session WebSocket nouvellement connectée
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("WS connecté : " + session.getId());
    }

    /**
     * Gère les messages textuels reçus depuis le client.
     * Si le message commence par "AUTH:", l'utilisateur est identifié et associé à la session.
     *
     * @param session la session WebSocket qui a envoyé le message
     * @param message le message textuel reçu
     * @throws Exception en cas d'erreur de traitement
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if (payload.startsWith("AUTH:")) {
            String utilisateurId = payload.substring(5);
            sessionUtilisateurMap.put(session, utilisateurId);
            System.out.println("Utilisateur connecté via WS : " + utilisateurId);
        }
    }

    /**
     * Méthode appelée à la fermeture d’une session WebSocket.
     * Elle supprime la session de la map d’utilisateurs connectés.
     *
     * @param session la session WebSocket fermée
     * @param status  le statut de fermeture
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionUtilisateurMap.remove(session);
        System.out.println("WS déconnecté : " + session.getId());
    }

    /**
     * Envoie un message textuel à l'utilisateur connecté via WebSocket.
     *
     * @param utilisateurId l'identifiant de l'utilisateur cible
     * @param message       le message à envoyer
     */
    public static void sendToUser(String utilisateurId, String message) {
        sessionUtilisateurMap.forEach((session, id) -> {
            if (utilisateurId.equals(id) && session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(message));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
