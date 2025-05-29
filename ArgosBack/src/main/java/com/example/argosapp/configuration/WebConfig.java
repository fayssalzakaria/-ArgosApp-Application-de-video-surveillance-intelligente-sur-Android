package com.example.argosapp.configuration;

import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration globale du CORS (Cross-Origin Resource Sharing) pour l'application Web.
 * <p>
 * Permet aux clients front-end (même hébergés sur d'autres domaines) d'accéder à l'API
 * en définissant les règles de partage des ressources entre origines différentes.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure les règles CORS pour l'ensemble des routes de l'application.
     * <p>
     * Cette configuration :
     * <ul>
     *     <li>Autorise toutes les origines ({@code *})</li>
     *     <li>Permet les méthodes HTTP : GET, POST, PUT, DELETE</li>
     *     <li>Accepte tous les en-têtes</li>
     * </ul>
     *
     * @param registry le registre CORS à configurer
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("*")  // Autoriser toutes les origines
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");
    }
}
