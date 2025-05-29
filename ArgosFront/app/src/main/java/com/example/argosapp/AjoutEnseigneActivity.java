package com.example.argosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité permettant d'ajouter une nouvelle enseigne associée à un utilisateur.
 * L'utilisateur peut saisir un identifiant, un nom d'enseigne, et une adresse.
 */
public class AjoutEnseigneActivity extends AppCompatActivity {

    /**
     * Méthode appelée lors de la création de l'activité.
     *
     * @param savedInstanceState Données de l'état précédent si l'activité a été recréée.
     */
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout_enseigne);

        // Navigation vers la page de compte utilisateur
        ImageView compteButton = findViewById(R.id.ivUser);
        compteButton.setOnClickListener(v -> {
            Intent intent = new Intent(AjoutEnseigneActivity.this, AccountsActivity.class);
            startActivity(intent);
        });

        // Navigation vers la page d'accueil
        ImageView homeButton = findViewById(R.id.ivHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AjoutEnseigneActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        // Références aux champs de saisie
        EditText editTextIdentifiant = findViewById(R.id.editTextIdentifiant);
        EditText editTextEnseigne = findViewById(R.id.editTextEnseigne);
        EditText editTextAdresse = findViewById(R.id.editTextAdresse);
        View mainLayout = findViewById(R.id.main);

        // Configuration des EditText avec gestion du focus et du texte par défaut
        setupEditText(editTextIdentifiant, "Identifiant de l'utilisateur qui y travaille");
        setupEditText(editTextEnseigne, "Enseigne");
        setupEditText(editTextAdresse, "Adresse");

        // Fermer le clavier et réinitialiser le texte si clic hors des champs
        mainLayout.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                hideKeyboard();
                resetEditTextIfEmpty(editTextIdentifiant, "Identifiant de l'utilisateur qui y travaille");
                resetEditTextIfEmpty(editTextEnseigne, "Enseigne");
                resetEditTextIfEmpty(editTextAdresse, "Adresse");
            }
            return false;
        });

        // Gestion du clic sur le bouton d'ajout
        ImageButton ajoutButton = findViewById(R.id.ajoutButton);
        ajoutButton.setOnClickListener(v -> {
            String identifiant = editTextIdentifiant.getText().toString().trim();
            String enseigne = editTextEnseigne.getText().toString().trim();
            String adresse = editTextAdresse.getText().toString().trim();

            if (identifiant.isEmpty() || enseigne.isEmpty()) {
                showErrorDialog("Tous les champs doivent être remplis.");
            } else {
                // Création de l'objet Enseigne
                Enseigne enseigneDTO = new Enseigne(identifiant, enseigne, adresse);

                // Appel à l'API via Retrofit
                ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
                apiService.ajouterEnseigne(enseigneDTO).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            // Succès : on retourne à l'activité précédente
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            showErrorDialog("Erreur lors de l'ajout. Vérifiez que l'utilisateur de l'enseigne existe");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        showErrorDialog("Erreur de connexion à l'API.");
                    }
                });
            }
        });
    }

    /**
     * Configure un EditText pour effacer le texte par défaut lorsqu’il reçoit le focus,
     * et le rétablir s’il est vide lorsqu’il le perd.
     *
     * @param editText   Le champ à configurer.
     * @param defaultText Le texte par défaut à afficher.
     */
    private void setupEditText(EditText editText, String defaultText) {
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                if (editText.getText().toString().equals(defaultText)) {
                    editText.setText("");
                    editText.setTextColor(Color.BLACK);
                }
            } else {
                resetEditTextIfEmpty(editText, defaultText);
            }
        });
    }

    /**
     * Réinitialise le champ si aucun texte n’a été saisi.
     *
     * @param editText    Le champ à réinitialiser.
     * @param defaultText Le texte par défaut à afficher.
     */
    public void resetEditTextIfEmpty(EditText editText, String defaultText) {
        if (editText.getText().toString().trim().isEmpty()) {
            editText.setText(defaultText);
            editText.setTextColor(Color.GRAY);
        }
    }

    /**
     * Ferme le clavier virtuel si un champ est actuellement sélectionné.
     */
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
    }

    /**
     * Affiche une boîte de dialogue avec un message d’erreur.
     *
     * @param message Le message à afficher dans le pop-up.
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Erreur")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}