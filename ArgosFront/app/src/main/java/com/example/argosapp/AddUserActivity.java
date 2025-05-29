package com.example.argosapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité permettant d'ajouter un nouvel utilisateur via un formulaire.
 * L'utilisateur peut saisir les informations nécessaires et les envoyer à une API backend.
 */
public class AddUserActivity extends AppCompatActivity {

    /** Champ d'entrée pour l'identifiant de l'utilisateur */
    private EditText etIdentifiant;

    /** Champ d'entrée pour le nom de l'utilisateur */
    private EditText etNom;

    /** Champ d'entrée pour le prénom de l'utilisateur */
    private EditText etPrenom;

    /** Champ d'entrée pour le mot de passe */
    private EditText etPassword;

    /** Champ d'entrée pour l'organisme/enseigne */
    private EditText etEnseigne;

    /** Icône pour basculer la visibilité du mot de passe */
    private ImageView ivTogglePassword;

    /** État actuel de la visibilité du mot de passe */
    private boolean isPasswordVisible = false;

    /**
     * Méthode appelée à la création de l'activité.
     *
     * @param savedInstanceState données sauvegardées si l'activité est recréée
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        etIdentifiant = findViewById(R.id.etIdentifiant);
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);
        etPassword = findViewById(R.id.etPassword);
        etEnseigne = findViewById(R.id.etOrganisme);
        ivTogglePassword = findViewById(R.id.ivTogglePassword);

        /**
         * Gère le clic sur l'icône d'œil pour afficher ou masquer le mot de passe.
         */
        ivTogglePassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            etPassword.setInputType(isPasswordVisible ?
                    (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                    : (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD));
            ivTogglePassword.setImageResource(isPasswordVisible ? R.drawable.ic_psw_eye_open : R.drawable.ic_psw_eye_closed);
            etPassword.setSelection(etPassword.getText().length()); // Garde le curseur à la fin
        });
    }

    /**
     * Méthode appelée lors du clic sur le bouton "Ajouter".
     * Elle récupère les données des champs, les valide et effectue un appel API.
     *
     * @param view Vue qui déclenche l'appel (le bouton)
     */
    public void onAjouterClick(View view) {
        String identifiant = etIdentifiant.getText().toString().trim();
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String enseigne = etEnseigne.getText().toString().trim();

        // Vérifie que tous les champs sont remplis
        if (identifiant.isEmpty() || nom.isEmpty() || prenom.isEmpty() || password.isEmpty() || enseigne.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Création de l'objet utilisateur
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Utilisateur utilisateur = new Utilisateur(identifiant, nom, prenom, password, enseigne);

        // Log JSON de l'objet utilisateur
        String jsonUtilisateur = new Gson().toJson(utilisateur);
        Log.d("JSON Envoyé", jsonUtilisateur);

        // Envoi de la requête à l'API
        apiService.ajouterUtilisateur(utilisateur).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        Log.d("Retrofit", "Réponse complète de l'API : " + responseBody);
                        Toast.makeText(AddUserActivity.this, "Ajout effectué avec succès", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);  // Retourne un code de résultat OK à l'activité appelante
                        finish();
                    } catch (IOException e) {
                        Log.e("Retrofit", "Erreur lors de la lecture de la réponse", e);
                    }
                } else {
                    Log.e("Retrofit", "Erreur HTTP : " + response.code() + " - " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("AddUserActivity", "Erreur de connexion : " + t.getMessage());
                Toast.makeText(AddUserActivity.this, "Erreur de connexion.", Toast.LENGTH_LONG).show();
            }
        });
    }
}
