package com.example.argosapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité principale de l'application après connexion.
 * Elle sert de tableau de bord pour accéder aux différentes fonctionnalités :
 * profil utilisateur, gestion des utilisateurs, gestion des clients, etc.
 */
public class HomeActivity extends AppCompatActivity {

    /**
     * Méthode appelée à la création de l'activité.
     *
     * @param savedInstanceState État précédent sauvegardé (null lors du premier lancement)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    /**
     * Redirige l'utilisateur vers son espace compte (profil).
     *
     * @param view Vue qui a déclenché l'action (icône ou bouton)
     */
    public void onProfileClick(View view) {
        Intent intent = new Intent(HomeActivity.this, AccountsActivity.class);
        startActivity(intent);
    }

    /**
     * Recharge l'activité Home (utile si elle est appelée depuis un autre écran avec la même icône).
     *
     * @param view Vue qui a déclenché l'action
     */
    public void onHomeClick(View view) {
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    /**
     * Ouvre l'écran de gestion des utilisateurs.
     *
     * @param view Vue ayant déclenché l'action (ex: bouton ou carte)
     */
    public void onUserManagementClick(View view) {
        Intent intent = new Intent(HomeActivity.this, UserManagementActivity.class);
        startActivity(intent);
    }

    /**
     * Ouvre l'écran de gestion des enseignes ou clients.
     *
     * @param view Vue ayant déclenché l'action
     */
    public void onClientManagementClick(View view) {
        Intent intent = new Intent(HomeActivity.this, GestionEnseignesActivity.class);
        startActivity(intent);
    }
}