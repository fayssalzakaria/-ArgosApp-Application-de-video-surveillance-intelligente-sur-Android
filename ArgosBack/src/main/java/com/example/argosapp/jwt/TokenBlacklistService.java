package com.example.argosapp.jwt;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service permettant de gerer une liste noire de tokens JWT.
 * Utilise pour invalider les tokens apres deconnexion ou revocation.
 */
@Service
public class TokenBlacklistService {

    /**
     * Ensemble contenant les tokens JWT qui ont ete rendus invalides.
     */
    private final Set<String> blacklistedTokens = new HashSet<>();
    private final Path filePath = Paths.get("blacklist.txt");
    /**
     * Méthode appelée après l'initialisation du service pour charger les tokens invalidés
     * depuis le fichier à chaque démarrage de l'application.
     */
    @PostConstruct
    public void init() {

        loadTokensFromFile(); 
       

    }
    /**
     * Ajoute un token à la liste noire afin qu'il ne soit plus accepté pour l'authentification.
     * Le token est ajouté uniquement s'il n'est pas déjà présent dans la liste noire.
     * Après l'ajout, le token est sauvegardé dans le fichier de la liste noire.
     *
     * @param token le token JWT à invalider
     */
    public void blacklistToken(String token) {
        if (blacklistedTokens.add(token)) {
            saveTokenToFile(token); // Sauvegarder uniquement si nouveau
        }
    }
    

    /**
     * Verifie si un token est dans la liste noire.
     *
     * @param token le token JWT a verifier
     * @return true si le token est blackliste, false sinon
     */
    public boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
        /**
     * Charge les tokens invalidés depuis le fichier de la liste noire au démarrage de l'application.
     * Si le fichier n'existe pas ou qu'il y a une erreur lors de la lecture, la liste noire reste vide.
     */
    private void loadTokensFromFile() {
        try {
            if (Files.exists(filePath)) {
                List<String> lines = Files.readAllLines(filePath);
                blacklistedTokens.addAll(lines);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la blacklist : " + e.getMessage());
        }
    }
    /**
     * Sauvegarde un token dans le fichier de la liste noire.
     * Chaque nouveau token invalidé est ajouté à la fin du fichier.
     * Si le fichier n'existe pas, il est créé.
     *
     * @param token le token JWT à sauvegarder
     */
    private void saveTokenToFile(String token) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(token);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Erreur lors de la sauvegarde du token : " + e.getMessage());
        }
    }
}
