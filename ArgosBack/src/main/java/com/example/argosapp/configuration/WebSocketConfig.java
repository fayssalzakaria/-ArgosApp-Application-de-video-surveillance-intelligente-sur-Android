package com.example.argosapp.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.config.annotation.*;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Configuration du serveur WebSocket de l'application.
 * <p>
 * Active le support de la messagerie WebSocket avec STOMP, définit les points de terminaison
 * d'abonnement et les préfixes de routage pour la communication entre le client et le serveur.
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Notre serveur WebSocket écoute ici
        registry.addHandler(new NotificationWebSocketHandler(), "/ws-notifications")
                .setAllowedOrigins("*");  // Permet toutes les origines (Android, etc)
    }
}

