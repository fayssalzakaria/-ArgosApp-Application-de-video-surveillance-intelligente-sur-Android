package com.example.argosapp.configuration;

import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.argosapp.jwt.JwtAuthFilter;

/**
 * Classe de configuration de la sécurité Web avec Spring Security.
 * Cette configuration gère les autorisations des requêtes HTTP, la politique
 * de session, le traitement CSRF, et la gestion de la déconnexion.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Déclare un filtre d'authentification JWT pour valider les tokens.
     */
    @Autowired
    private JwtAuthFilter jwtAuthFilter;
    
    /**
     * Définit la chaîne de filtres de sécurité pour les requêtes HTTP.
     * 
     * @param http l'objet HttpSecurity utilisé pour configurer les règles de sécurité.
     * @return la chaîne de filtres de sécurité configurée.
     * @throws Exception en cas d'erreur lors de la configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Désactive la protection CSRF car l'application est sans état (stateless)
            .csrf(AbstractHttpConfigurer::disable)
            
            // Configuration des autorisations d'accès pour les différentes routes
            .authorizeHttpRequests(auth -> auth
                // Routes accessibles sans authentification (public)
                .requestMatchers(
                    "/api/utilisateurs/login", 
                    "/api/utilisateurs/validate-token", 
                    "/api/utilisateurs/logout", 
                    "/api/enregistrements", 
                    "/api/notifications",
                    "/api/cameras/rtsp"
                ).permitAll() // Accès public aux routes de connexion, validation de token, déconnexion et notifications
                
                // Routes réservées aux utilisateurs avec le rôle "ADMIN"
                .requestMatchers(
                    "/api/utilisateurs/ajouter", 
                    "/api/utilisateurs/all", 
                    "/api/enseigne/ajouter", 
                    "/api/enseigne/all", 
                    "/api/cameras/ajouter", 
                
                    "/api/utilisateurs/supprimer/**", 
                    "/api/enseigne/supprimer/**"
                ).hasRole("ADMIN") // Accès restreint aux administrateurs pour manipuler les données de l'application
                
                // Routes WebSocket autorisée pour tous
                .requestMatchers("/ws-notifications/**").permitAll() // Autorise l'accès public aux notifications en temps réel via WebSocket
                .requestMatchers("/ws").permitAll() 
                // Toutes les autres routes doivent être authentifiées
                .anyRequest().authenticated()
            )
            
            // Désactive la gestion de session pour garder l'application sans état (stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Permet à tout utilisateur de se déconnecter
            .logout(LogoutConfigurer::permitAll)
            
            // Ajoute le filtre JWT avant le filtre d'authentification par nom d'utilisateur et mot de passe
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
    
        return http.build();
    }
}
