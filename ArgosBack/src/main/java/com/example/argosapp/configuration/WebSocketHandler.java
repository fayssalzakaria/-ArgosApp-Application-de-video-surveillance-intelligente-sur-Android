package com.example.argosapp.configuration;

import org.springframework.stereotype.Service;
import com.example.argosapp.signal.SignalMessage;
import com.example.argosapp.signal.SignalService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;  
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Service // Indique que cette classe est un service Spring géré par le conteneur
public class WebSocketHandler extends TextWebSocketHandler {

    // Service de signalisation utilisé pour enregistrer les clients et envoyer les messages
    private final SignalService signalService;
    // Permet de convertir des objets Java en JSON et inversement
    private final ObjectMapper mapper = new ObjectMapper();
    // Utilisé pour envoyer des requêtes HTTP à un serveur distant (ex: MediaMTX)
    private final RestTemplate restTemplate;

    // URL de base pour les requêtes HTTP envoyées à MediaMTX (doit être adaptée selon l’environnement)
    private static final String BASE_URL  = "https://89e8-2a01-cb08-b43-d700-e3ed-9fb0-ef24-9695.ngrok-free.app";


    // Constructeur avec injection du service de signalissation
    public WebSocketHandler(SignalService signalService) {
        this.signalService = signalService;
        this.restTemplate  = new RestTemplate();
    }

    // Méthode appelée automatiquement à chaque nouvelle connexion WebSocket
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        System.out.println("Nouvelle connexion WebSocket : " + session.getId());
    }

    // Méthode appelée quand un message texte est reçu sur la WebSocket
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            // Convertit le message JSON reçu en un objet SignalMessage
            SignalMessage signal = mapper.readValue(message.getPayload(), SignalMessage.class);

            // Si le message est de type "register", on enregistre le client avec son ID et la session WebSocket
            if ("register".equals(signal.getType())) {
                signalService.registerClient(signal.getClientId(), session);
                System.out.println("Client enregistré : " + signal.getClientId());
                return;
            }

            // Si le message est de type "signal", on le traite
            if ("signal".equals(signal.getType())) {
                handleSignalMessage(signal);
            }
        } catch (Exception e) {
            System.err.println("Erreur durant le traitement du signal : " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Gère les différents types de messages de signalisation
    private void handleSignalMessage(SignalMessage signal) throws Exception {
        String target     = signal.getTargetId();     // Cible du message
        String signalType = signal.getSignalType();   // Type du signal (offer, answer, candidate)

        // 1) Si c’est une offre SDP destinée à MediaMTX
        if ("offer".equals(signalType) && "mediamtx".equals(target)) {
            String streamId = signal.getStreamId();
            // Vérifie que l’identifiant du flux est présent
            if (streamId == null || streamId.isEmpty()) {
                System.err.println("StreamId manquant dans l'offre SDP");
                return;
            }

            // Prépare l’URL pour envoyer l’offre SDP à MediaMTX
            String offerUrl = BASE_URL + "/" + streamId + "/whep";
            System.out.println("Envoi de l'offre SDP à MediaMTX → " + offerUrl);

            // Préparation de la requête HTTP avec type "application/sdp"
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/sdp"));
            HttpEntity<String> req = new HttpEntity<>(signal.getData(), headers);

            try {
                // Envoie l’offre SDP en POST à MediaMTX
                ResponseEntity<String> resp = restTemplate.postForEntity(offerUrl, req, String.class);
                System.out.println("→ Status offer POST : " + resp.getStatusCode());

                // Crée un message contenant l’"answer" de MediaMTX à retourner au client Android
                SignalMessage answer = new SignalMessage();
                answer.setType("signal");
                answer.setClientId("mediamtx");                 // MediaMTX comme expéditeur
                answer.setTargetId(signal.getClientId());       // Le client initial comme destinataire
                answer.setSignalType("answer");                 // Type "answer"
                answer.setData(resp.getBody());                 // Réponse SDP reçue
                answer.setStreamId(streamId);                   // Réutilise le même streamId

                // Envoie l’"answer" au client
                sendToClient(answer);
            } catch (HttpClientErrorException.NotFound nf) {
                // Gère l’erreur si l’URL de MediaMTX n’existe pas (404)
                System.err.println("404 lors de l'offer POST : " + nf.getResponseBodyAsString());
            }
            return;
        }

        // 2) Ignore les ICE candidates si la cible est MediaMTX (car mode non-trickle ICE)
        if ("candidate".equals(signalType) && "mediamtx".equals(target)) {
            return;
        }

        // 3) Relayage classique des autres messages (ICE entre clients, autres offres/réponses)
        WebSocketSession dst = signalService.getSession(target);
        if (dst != null && dst.isOpen()) {
            // Envoie le signal à la session WebSocket cible
            dst.sendMessage(new TextMessage(mapper.writeValueAsString(signal)));
        } else {
            System.err.println("Cible non connectée : " + target);
        }
    }

    // Méthode utilitaire pour envoyer un message à un client spécifique via WebSocket
    private void sendToClient(SignalMessage msg) throws Exception {
        WebSocketSession client = signalService.getSession(msg.getTargetId());
        if (client != null && client.isOpen()) {
            client.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
        } else {
            System.err.println("Impossible d'envoyer au client " + msg.getTargetId());
        }
    }
}
