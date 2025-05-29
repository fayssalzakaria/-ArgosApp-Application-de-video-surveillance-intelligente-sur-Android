package com.example.argosapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité de connexion de l'application.
 * Gère l'authentification, le stockage du token JWT, et la redirection selon le rôle utilisateur.
 */
public class LoginActivity extends AppCompatActivity {

    private boolean motdepasseVisible = false;

    private static final String PREFS_NAME = "AuthPrefs";
    private static final String TOKEN_KEY = "jwt_token";

    /** Toast affiché globalement pour retour utilisateur */
    public static Toast currentToast;

    /** TextView utilisé dans le Toast (utile pour les tests ou personnalisation) */
    public static TextView toastTextView;

    /**
     * Point d’entrée principal. Vérifie la présence d’un token valide et redirige si besoin.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String token = getToken(this);
        if (token != null) {
            isTokenValidAsync(token, isValid -> {
                if (isValid) {
                    boolean isAdmin = extractIsAdmin(token);
                    Intent intent = new Intent(LoginActivity.this, isAdmin ? HomeActivity.class : VideoSurveillanceActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    showLoginScreen();
                }
            });
        } else {
            showLoginScreen();
        }
    }

    /**
     * Affiche l’interface utilisateur de connexion et configure les comportements.
     */
    public void showLoginScreen() {
        setContentView(R.layout.activity_login);

        EditText textUser = findViewById(R.id.editTextUsername);
        EditText textMdp = findViewById(R.id.editTextPassword);
        Button boutonLog = findViewById(R.id.buttonLogin);
        ImageView imageViewOeil = findViewById(R.id.imageViewTogglePassword);

        imageViewOeil.setOnClickListener(v -> {
            motdepasseVisible = !motdepasseVisible;
            textMdp.setInputType(motdepasseVisible ?
                    (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                    (android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD));
            imageViewOeil.setImageResource(motdepasseVisible ? R.drawable.oeilopen : R.drawable.oeil);
            textMdp.setSelection(textMdp.getText().length());
        });

        // Configuration du Toast personnalisé
        toastTextView = new TextView(this);
        toastTextView.setTextSize(16);
        currentToast = new Toast(this);
        currentToast.setDuration(Toast.LENGTH_SHORT);
        currentToast.setView(toastTextView);

        boutonLog.setOnClickListener(v -> {
            String identifiant = textUser.getText().toString().trim();
            String motdepasse = textMdp.getText().toString().trim();

            // Sauvegarde de l’identifiant saisi
            SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("user_id", identifiant);
            editor.apply();

            if (identifiant.isEmpty() || motdepasse.isEmpty()) {
                toastTextView.setText("Erreur : Veuillez remplir tous les champs.");
                currentToast.show();
                return;
            }

            // Préparation de l'appel à l'API de login
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("id", identifiant);
            loginRequest.put("password", motdepasse);

            apiService.login(loginRequest).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body();
                        saveToken(token);
                        boolean isAdmin = extractIsAdmin(token);
                        toastTextView.setText("Connexion réussie.");
                        currentToast.show();
                        Intent intent = new Intent(LoginActivity.this, isAdmin ? HomeActivity.class : VideoSurveillanceActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            if (response.errorBody() != null) {
                                String errorBody = response.errorBody().string();
                                toastTextView.setText(errorBody);
                            } else {
                                toastTextView.setText("Erreur inconnue");
                            }
                            currentToast.show();
                        } catch (Exception e) {
                            Log.e("LoginActivity", "Exception lors de la lecture de l'erreur : " + e.getMessage());
                            toastTextView.setText("Erreur de traitement de la réponse");
                            currentToast.show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e("LoginActivity", "Erreur de connexion : " + t.getMessage());
                    toastTextView.setText("Erreur de connexion");
                    currentToast.show();
                }
            });
        });
    }

    /**
     * Enregistre le token JWT dans les préférences locales.
     *
     * @param token le token JWT reçu après authentification
     */
    private void saveToken(String token) {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        preferences.edit().putString(TOKEN_KEY, token).apply();
    }

    /**
     * Récupère le token JWT stocké.
     *
     * @param context contexte d’exécution
     * @return token JWT ou null s’il n’existe pas
     */
    private String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(TOKEN_KEY, null);
    }

    /**
     * Décode le JWT pour extraire la propriété "isAdmin".
     *
     * @param token JWT en base64
     * @return true si l’utilisateur est admin, false sinon
     */
    private boolean extractIsAdmin(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length < 2) return false;

            String payload = new String(Base64.decode(parts[1], Base64.DEFAULT));
            Map<String, Object> claims = new Gson().fromJson(payload, Map.class);
            return claims.containsKey("isAdmin") && (boolean) claims.get("isAdmin");
        } catch (Exception e) {
            Log.e("LoginActivity", "Erreur lors de l'extraction de isAdmin", e);
            return false;
        }
    }

    /**
     * Vérifie la validité du token auprès du serveur.
     *
     * @param token token à valider
     * @param callback callback appelé avec le résultat (true si valide)
     */
    private void isTokenValidAsync(String token, ValidationCallback callback) {
        Log.d("LoginActivity", "Token envoyé pour validation: " + token);
        token = token.trim();

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.validateToken().enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                Log.d("LoginActivity", "Response code: " + response.code());
                if (response.isSuccessful()) {
                    boolean isValid = response.body() != null && response.body();
                    Log.d("LoginActivity", "Token valid: " + isValid);
                    callback.onResult(isValid);
                } else {
                    Log.e("LoginActivity", "Token validation failed: " + response.message());
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Log.e("LoginActivity", "Connection error: " + t.getMessage());
                callback.onResult(false);
            }
        });
    }

    /**
     * Interface pour gérer les retours de validation de token.
     */
    interface ValidationCallback {
        void onResult(boolean isValid);
    }
}