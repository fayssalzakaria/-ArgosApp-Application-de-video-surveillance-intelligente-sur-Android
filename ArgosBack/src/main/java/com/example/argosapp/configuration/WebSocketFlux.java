package com.example.argosapp.configuration;

import com.example.argosapp.signal.SignalService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration // Indique que cette classe contient une configuration Spring
@EnableWebSocket // Active le support des WebSockets dans l'application Spring
public class WebSocketFlux implements WebSocketConfigurer {

    // Service de signalisation qui gère l'enregistrement et la communication entre les clients
    private final SignalService signalService;


    public WebSocketFlux(SignalService signalService) {
        this.signalService = signalService;
    }


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

        registry.addHandler(new WebSocketHandler(signalService), "/ws").setAllowedOrigins("*");
    }
}
