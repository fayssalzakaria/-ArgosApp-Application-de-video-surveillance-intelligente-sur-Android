package com.example.argosapp;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité représentant l'espace personnel d'un utilisateur connecté.
 * Affiche les informations personnelles et propose un menu de paramètres,
 * ainsi qu'un bouton d'accès aux notifications.
 */
public class EspaceCompteActivity extends AppCompatActivity {

    /** Indique si le mot de passe est visible ou non */
    private boolean motdepasseVisible = false;

    /** Champ de mot de passe */
    private EditText champMdp;

    /** Icône de l'œil pour afficher ou masquer le mot de passe */
    private ImageView oeil;

    /** Icône pour accéder aux notifications */
    private ImageView notif;

    /** Fragment du menu des paramètres */
    private ParametersMenuFragmentActivity parametersMenuFragment;

    /**
     * Méthode appelée lors de la création de l'activité.
     *
     * @param savedInstanceState État précédemment sauvegardé (null si création initiale)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.espace_compte_user);

        champMdp = findViewById(R.id.password);
        oeil = findViewById(R.id.toggle_password);
        notif = findViewById(R.id.notification_icon);

        // Mot de passe masqué par défaut
        champMdp.setTransformationMethod(new PasswordTransformationMethod());

        /**
         * Gestion de l'affichage du mot de passe avec l'icône œil.
         */
        oeil.setOnClickListener(view -> {
            if(motdepasseVisible){
                champMdp.setTransformationMethod(new PasswordTransformationMethod());
                oeil.setImageResource(R.drawable.oeil);
            } else {
                champMdp.setTransformationMethod(null);
                oeil.setImageResource(R.drawable.oeilopen);
            }
            champMdp.setSelection(champMdp.getText().length());
            motdepasseVisible = !motdepasseVisible;
        });

        /**
         * Redirection vers les notifications.
         */
        notif.setOnClickListener(view -> {
            Intent intent = new Intent(EspaceCompteActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        /**
         * Affichage du menu des paramètres via un fragment.
         */
        parametersMenuFragment = (ParametersMenuFragmentActivity) getSupportFragmentManager().findFragmentByTag("parametersFragment");
        if (parametersMenuFragment == null) {
            parametersMenuFragment = new ParametersMenuFragmentActivity();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, parametersMenuFragment, "parametersFragment")
                    .addToBackStack(null)
                    .commit();
        }

        findViewById(R.id.iconeParametre).setOnClickListener(v -> parametersMenuFragment.toggleMenuVisibility());

        // Active la flèche de retour dans la barre d'action
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        /**
         * Gestion personnalisée du bouton "Retour".
         * Ferme le menu s'il est ouvert, sinon effectue le retour par défaut.
         */
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                View menuSettings = parametersMenuFragment.getView() != null ?
                        parametersMenuFragment.getView().findViewById(R.id.menuSettings) : null;

                if (menuSettings != null && menuSettings.getVisibility() == View.VISIBLE) {
                    menuSettings.setVisibility(View.GONE);
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        /**
         * Récupération et affichage des informations de l'utilisateur via l'API.
         */
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

                            // Extraction des champs depuis la chaîne brute
                            String userId = extractValue(rawInfo, "ID:");
                            String motDePasse = extractValue(rawInfo, "Mot de passe:");
                            String nom = extractValue(rawInfo, "Nom:");
                            String prenom = extractValue(rawInfo, "Prénom:");
                            String enseigne = extractValue(rawInfo, " Enseigne:");

                            // Mise à jour de l'interface utilisateur
                            EditText idText = findViewById(R.id.identifiant);
                            EditText passwordText = findViewById(R.id.password);
                            TextView nameText = findViewById(R.id.name);
                            TextView surnameText = findViewById(R.id.prenom);
                            TextView enseigneText = findViewById(R.id.orgs);

                            idText.setText(userId);
                            nameText.setText(nom);
                            surnameText.setText(prenom);
                            passwordText.setText(motDePasse);
                            enseigneText.setText(enseigne);

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
     * Méthode utilitaire pour extraire une valeur textuelle à partir d'un mot-clé dans une chaîne.
     *
     * @param data Chaîne source contenant les données
     * @param key Clé (ex. : "Nom:", "Mot de passe:")
     * @return Valeur trouvée ou chaîne vide si non trouvée
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