package com.example.argosapp.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Filtre personnalisé pour la gestion de l'authentification basée sur un jeton JWT dans une application Spring Security.
 * 
 * <p>Ce filtre intercepte les requêtes HTTP entrantes, extrait le JWT depuis l'en-tête "Authorization", 
 * le valide et, si le jeton est valide, définit les détails de l'utilisateur authentifié dans le contexte de sécurité.</p>
 * 
 * @see JwtUtil
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Cette méthode est invoquée lors du traitement de la requête pour extraire, valider et authentifier le jeton JWT
     * s'il est présent dans l'en-tête "Authorization" de la requête.
     * 
     * <p>Si un JWT valide est trouvé, un {@link UsernamePasswordAuthenticationToken} est créé avec les détails de 
     * l'utilisateur (nom d'utilisateur et rôles) et est ajouté au {@link SecurityContextHolder} pour une utilisation 
     * ultérieure dans le cycle de vie de la requête.</p>
     * 
     * @param request la requête HTTP entrante
     * @param response la réponse HTTP sortante
     * @param filterChain la chaîne de filtres pour passer la requête et la réponse au filtre suivant
     * @throws ServletException si une erreur se produit lors du traitement du filtre
     * @throws IOException si une erreur se produit lors des opérations d'entrée/sortie
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        // Vérifier si l'en-tête Authorization existe et contient un jeton Bearer
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Valider le jeton JWT
            if (jwtUtil.validateToken(token)) {
                // Extraire le nom d'utilisateur et le rôle (administrateur ou utilisateur) du jeton
                String username = jwtUtil.extractUsername(token);
                boolean isAdmin = jwtUtil.extractIsAdmin(token);

                // Définir les autorisations en fonction du rôle de l'utilisateur (admin ou utilisateur)
                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                        new SimpleGrantedAuthority(isAdmin ? "ROLE_ADMIN" : "ROLE_USER"));

                // Créer un jeton d'authentification et le définir dans le contexte de sécurité et ajout du token dans les credentials
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, token, authorities);

                // Ajouter les détails de la requête au jeton d'authentification
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Définir l'authentification dans le contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Continuer avec la chaîne de filtres
        filterChain.doFilter(request, response);
    }
}
