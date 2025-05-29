package com.example.argosapp;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité permettant à un administrateur de consulter les informations d'un utilisateur
 * et de modifier son mot de passe via une API distante.
 */
public class AccountsActivity extends AppCompatActivity {

    // UI Components
    private TextView textView;
    private EditText editText;
    private Button buttonModifier, buttonEnregistrer, buttonAnnuler;
    private ImageView ivTogglePassword, ivTogglePasswordUp;
    // États d'affichage
    private boolean isPasswordVisible = false;
    private boolean isEditing = false;

    // États des boutons
    private boolean isModifierButtonEnabled = true;
    private boolean isEnregistrerButtonEnabled = false;
    private boolean isAnnulerButtonEnabled = false;
    // Stocke le mot de passe précédent (utilisé pour l'annulation)
    private String previousPassword = "";

    /**
     * Méthode appelée à la création de l'activité.
     *
     * @param savedInstanceState données sauvegardées lors d'un changement de configuration
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts);

        textView = findViewById(R.id.passwordTextView);
        editText = findViewById(R.id.passwordEditText);
        buttonModifier = findViewById(R.id.modifierButton);
        buttonEnregistrer = findViewById(R.id.saveButton);
        buttonAnnuler = findViewById(R.id.cancelButton);
        ivTogglePassword = findViewById(R.id.togglePasswordClose);
        ivTogglePasswordUp = findViewById(R.id.togglePasswordOpen);

        if (savedInstanceState != null) {
            isEditing = savedInstanceState.getBoolean("isEditing", false);
            previousPassword = savedInstanceState.getString("previousPassword", "idadmin001");
            isPasswordVisible = savedInstanceState.getBoolean("passwordVisible", false);
            isAnnulerButtonEnabled = savedInstanceState.getBoolean("isAnnulerButtonEnabled", false);
            isEnregistrerButtonEnabled = savedInstanceState.getBoolean("isEnregistrerButtonEnabled", false);
            isModifierButtonEnabled = savedInstanceState.getBoolean("isModifierButtonEnabled", true);
        } else {
            previousPassword = "idadmin001";
        }

        ivTogglePassword.setVisibility(View.GONE);
        ivTogglePasswordUp.setVisibility(View.VISIBLE);
        editText.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        textView.setVisibility(isEditing ? View.GONE : View.VISIBLE);
        updateButtonStates();

        ivTogglePassword.setOnClickListener(v -> togglePasswordVisibility(true));
        ivTogglePasswordUp.setOnClickListener(v -> togglePasswordVisibility(false));

        buttonModifier.setOnClickListener(v -> toggleEditMode(true));

        buttonEnregistrer.setOnClickListener(v -> {
            String newPassword = editText.getText().toString();
            SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
            String adminId = prefs.getString("user_id", null);
            String targetUserId = ((TextView) findViewById(R.id.identifiant)).getText().toString();

            if(adminId != null && targetUserId != null && !newPassword.isEmpty()){
                ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
                Map<String, String> requestBody = new HashMap<>();
                requestBody.put("adminID", adminId);
                requestBody.put("idTarget", targetUserId);
                requestBody.put("nouveauPassword", newPassword);

                Call<ResponseBody> call = apiService.changerMotDePasseAdmin(requestBody);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){
                            previousPassword = newPassword;
                            toggleEditMode(false);
                            Toast.makeText(AccountsActivity.this, "Mot de passe modifié avec succès !", Toast.LENGTH_LONG).show();
                            Log.d("PasswordChange","Mot de passe changé avec succès.");
                        }else{
                            Toast.makeText(AccountsActivity.this, "Echec de la modification du mot de passe", Toast.LENGTH_LONG).show();
                            Log.e("PasswordChange", "Erreur : " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(AccountsActivity.this, "Erreur côté réseau : " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("PasswordChange", "Exception : " + t.getMessage());
                    }
                });
            }else{
                Toast.makeText(AccountsActivity.this, "Veuillez remplir tout les champs !", Toast.LENGTH_LONG).show();
            }
        });

        buttonAnnuler.setOnClickListener(v -> {
            restorePassword();
            toggleEditMode(false);
        });

        findViewById(R.id.backButton).setOnClickListener(v -> {
            Intent intent = new Intent(AccountsActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        TextView decoButton = findViewById(R.id.SeDeconnecter);
        decoButton.setOnClickListener(this::onDecoClick);

        // Appel à l'API pour récupérer les infos utilisateur
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        String userId = prefs.getString("user_id", null);

        if (userId != null) {
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            Call<ResponseBody> call = apiService.getInfosUtilisateur(userId);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            String rawInfo = response.body().string();
                            Log.d("UserInfo", "Données reçues : " + rawInfo);

                            String userId = extractValue(rawInfo, "ID:");
                            String motDePasse = extractValue(rawInfo, "Mot de passe:");
                            String nom = extractValue(rawInfo, "Nom:");
                            String prenom = extractValue(rawInfo, "Prénom:");

                            TextView idTextView = findViewById(R.id.identifiant);
                            TextView passwordTextView = findViewById(R.id.passwordTextView);
                            TextView nameTextView = findViewById(R.id.name);
                            TextView surnameTextView = findViewById(R.id.prenom);

                            idTextView.setText(userId);
                            nameTextView.setText(nom);
                            surnameTextView.setText(prenom);
                            passwordTextView.setText(motDePasse);

                            previousPassword = motDePasse;
                        } catch (Exception e) {
                            Log.e("UserInfo", "Erreur de traitement", e);
                        }
                    } else {
                        Log.e("UserInfo", "Réponse invalide: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("UserInfo", "Erreur réseau : " + t.getMessage());
                }
            });
        } else {
            Log.e("UserInfo", "Aucun ID utilisateur trouvé !");
        }
    }

    /**
     * Met à jour l'état des boutons Modifier, Enregistrer et Annuler.
     */
    private void updateButtonStates() {
        buttonModifier.setEnabled(isModifierButtonEnabled);
        buttonModifier.setBackgroundColor(ContextCompat.getColor(this, isModifierButtonEnabled ? R.color.blue_custom : R.color.gray_custom));
        buttonEnregistrer.setEnabled(isEnregistrerButtonEnabled);
        buttonEnregistrer.setBackgroundColor(ContextCompat.getColor(this, isEnregistrerButtonEnabled ? R.color.blue_custom : R.color.gray_custom));
        buttonAnnuler.setEnabled(isAnnulerButtonEnabled);
        buttonAnnuler.setBackgroundColor(ContextCompat.getColor(this, isAnnulerButtonEnabled ? R.color.blue_custom : R.color.gray_custom));
    }

    /**
     * Affiche ou masque le mot de passe selon l'état choisi.
     *
     * @param visible true pour afficher, false pour masquer
     */
    private void togglePasswordVisibility(boolean visible) {
        isPasswordVisible = visible;

        if (visible) {
            String passwordToShow = editText.getVisibility() == View.VISIBLE
                    ? editText.getText().toString()
                    : previousPassword;

            textView.setText(passwordToShow);
            ivTogglePassword.setVisibility(View.GONE);
            ivTogglePasswordUp.setVisibility(View.VISIBLE);
        } else {
            textView.setText(".............");
            ivTogglePassword.setVisibility(View.VISIBLE);
            ivTogglePasswordUp.setVisibility(View.GONE);
        }
    }

    /**
     * Active ou désactive le mode édition du mot de passe.
     *
     * @param editing true pour activer, false pour désactiver
     */
    private void toggleEditMode(boolean editing) {
        if (editing) {
            if (isPasswordVisible) {
                previousPassword = textView.getText().toString();
            }
            Log.d("DEBUG", "Mot de passe injecté dans le editText: " + previousPassword);

            textView.setVisibility(View.GONE);
            editText.setVisibility(View.VISIBLE);
            editText.setText(previousPassword);
            ivTogglePassword.setVisibility(View.GONE);
            ivTogglePasswordUp.setVisibility(View.GONE);

            isModifierButtonEnabled = false;
            isEnregistrerButtonEnabled = true;
            isAnnulerButtonEnabled = true;
            isEditing = true;

        } else {
            textView.setText(editText.getText().toString());
            textView.setVisibility(View.VISIBLE);
            editText.setVisibility(View.GONE);
            togglePasswordVisibility(isPasswordVisible);

            if (isPasswordVisible) {
                ivTogglePasswordUp.setVisibility(View.VISIBLE);
            } else {
                ivTogglePassword.setVisibility(View.VISIBLE);
            }

            isModifierButtonEnabled = true;
            isEnregistrerButtonEnabled = false;
            isAnnulerButtonEnabled = false;
            isEditing = false;
        }
        updateButtonStates();
    }

    /**
     * Restaure le mot de passe précédent dans les champs de texte.
     */
    private void restorePassword() {
        textView.setText(previousPassword);
        editText.setText(previousPassword);
    }

    /**
     * Sauvegarde l'état de l'activité avant destruction.
     * @param outState Le Bundle dans lequel l'état est sauvegardé
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("passwordText", textView.getText().toString());
        outState.putBoolean("passwordVisible", isPasswordVisible);
        outState.putBoolean("isEditing", isEditing);
        outState.putString("previousPassword", previousPassword);
        outState.putBoolean("isAnnulerButtonEnabled", isAnnulerButtonEnabled);
        outState.putBoolean("isEnregistrerButtonEnabled", isEnregistrerButtonEnabled);
        outState.putBoolean("isModifierButtonEnabled", isModifierButtonEnabled);
        outState.putBoolean("ivTogglePasswordVisible", ivTogglePassword.getVisibility() == View.VISIBLE);
        outState.putBoolean("ivTogglePasswordUpVisible", ivTogglePasswordUp.getVisibility() == View.VISIBLE);
    }

    /**
     * Restaure l'état de l'activité après recréation.
     * @param savedInstanceState Le Bundle contenant les données sauvegardées précédemment
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        textView.setText(savedInstanceState.getString("passwordText", "idadmin001"));
        isPasswordVisible = savedInstanceState.getBoolean("passwordVisible", false);
        isEditing = savedInstanceState.getBoolean("isEditing", false);
        previousPassword = savedInstanceState.getString("previousPassword", "idadmin001");
        isAnnulerButtonEnabled = savedInstanceState.getBoolean("isAnnulerButtonEnabled", false);
        isEnregistrerButtonEnabled = savedInstanceState.getBoolean("isEnregistrerButtonEnabled", false);
        isModifierButtonEnabled = savedInstanceState.getBoolean("isModifierButtonEnabled", true);

        if (isEditing) {
            ivTogglePassword.setVisibility(View.GONE);
            ivTogglePasswordUp.setVisibility(View.GONE);
        } else {
            boolean isPasswordVisibleSaved = savedInstanceState.getBoolean("ivTogglePasswordVisible", false);
            boolean isPasswordUpVisibleSaved = savedInstanceState.getBoolean("ivTogglePasswordUpVisible", true);

            ivTogglePassword.setVisibility(isPasswordVisibleSaved ? View.VISIBLE : View.GONE);
            ivTogglePasswordUp.setVisibility(isPasswordUpVisibleSaved ? View.VISIBLE : View.GONE);
        }

        updateButtonStates();
        togglePasswordVisibility(isPasswordVisible);
    }

    /**
     * Gère le clic sur le bouton "Se déconnecter".
     *
     * @param view la vue déclenchant l'action
     */
    public void onDecoClick(View view) {
        new AlertDialog.Builder(this)
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> performLogout())
                .setNegativeButton("Annuler", (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Exécute la procédure de déconnexion.
     */
    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String token = prefs.getString("jwt_token", null);
        Log.d("Logout", "Token avant déconnexion: " + token);

        if (token != null) {
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            Call<ResponseBody> call = apiService.logout();

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        editor.remove("jwt_token");
                        editor.apply();

                        Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        Log.d("Logout", "Redirection vers la page de connexion.");
                    } else {
                        Log.e("Logout", "Échec de la déconnexion, code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e("Logout", "Erreur lors de la requête : " + t.getMessage());
                }
            });
        } else {
            Log.d("Logout", "Aucun token trouvé, redirection vers la page de connexion.");
            Intent intent = new Intent(AccountsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    /**
     * Extrait une valeur d'une chaîne brute basée sur une clé spécifique.
     *
     * @param data chaîne brute de données
     * @param key clé à rechercher
     * @return valeur extraite ou chaîne vide
     */
    private String extractValue(String data, String key) {
        try {
            int startIndex = data.indexOf(key);
            if (startIndex == -1) return "";
            int endIndex = data.indexOf(",", startIndex);
            if (endIndex == -1) endIndex = data.length();
            return data.substring(startIndex + key.length(), endIndex).trim();
        } catch (Exception e) {
            Log.e("Parser", "Erreur d'extraction pour la clé: " + key, e);
            return "";
        }
    }
}