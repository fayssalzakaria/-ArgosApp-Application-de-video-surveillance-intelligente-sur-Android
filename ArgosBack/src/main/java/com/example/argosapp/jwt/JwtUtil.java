package com.example.argosapp.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.Map;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import org.springframework.beans.factory.annotation.Value;

/**
 * Composant utilitaire pour la gestion des operations JWT
 * (JSON Web Tokens), incluant la creation, la validation
 * et l'extraction des informations du token.
 */
@Component
public class JwtUtil {

    /**
     * Cle secrete utilisee pour signer les tokens JWT,
     * chargee depuis le fichier de configuration.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Service permettant de verifier si un token est dans la liste noire.
     */
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Genere la cle de signature basee sur la cle secrete.
     *
     * @return une instance de Key pour signer et verifier les tokens JWT
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Verifie si un token JWT est valide.
     * Il verifie si le token n'est pas dans la liste noire
     * et s'il est correctement signe.
     *
     * @param token le token JWT a verifier
     * @return true si le token est valide et non liste, false sinon
     */
    public boolean validateToken(String token) {
        try {
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                return false;
            }
            Jwts.parser().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    /**
     * Genere un token JWT pour un utilisateur.
     *
     * @param username le nom d'utilisateur a inclure dans le token
     * @param isAdmin  booleen indiquant si l'utilisateur est un administrateur
     * @return une chaine contenant le token JWT signe
     */
    public String generateToken(String username, boolean isAdmin) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .claim("isAdmin", isAdmin)
                .claim("roles", isAdmin ? "ROLE_ADMIN" : "ROLE_USER") // Ajouter le rôle dans le JWT
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrait le nom d'utilisateur contenu dans un token JWT.
     *
     * @param token le token JWT
     * @return le nom d'utilisateur contenu dans le token
     */
    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Extrait l'information d'administration d'un token JWT.
     *
     * @param token le token JWT
     * @return true si l'utilisateur est admin, false sinon
     */
    public boolean extractIsAdmin(String token) {
        return Jwts.parser().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("isAdmin", Boolean.class);
    }
}
