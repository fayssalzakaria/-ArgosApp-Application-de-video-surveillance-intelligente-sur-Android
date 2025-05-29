package com.example.argosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité permettant à un utilisateur de consulter et interagir avec la liste de ses caméras.
 * Chaque caméra peut afficher des options telles que l'activation ou la désactivation
 * de la détection de vol ou d'infraction.
 */
public class GestionCamUserActivity extends AppCompatActivity {

    /** Conteneur principal des caméras dynamiquement ajoutées */
    private LinearLayout camerasLayout;

    /** Clé utilisée pour sauvegarder l'état du switch de détection de vol */
    private static final String VOL_DETECTION_KEY = "vol_detection";

    /** Clé utilisée pour sauvegarder l'état du switch de détection d'infraction */
    private static final String INFRACTION_DETECTION_KEY = "infraction_detection";

    /** Fragment du menu des paramètres */
    private ParametersMenuFragmentActivity parametersMenuFragment;

    /**
     * Méthode appelée à la création de l'activité.
     *
     * @param savedInstanceState Données de l'état sauvegardé si applicable
     */
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_cam);

        camerasLayout = findViewById(R.id.camerasLayout);
        chargerCameras();

        if (savedInstanceState != null) {
            // Récupération des états des switches si besoin
            boolean volDetection = savedInstanceState.getBoolean(VOL_DETECTION_KEY, true);
            boolean infractionDetection = savedInstanceState.getBoolean(INFRACTION_DETECTION_KEY, true);
        }

        /**
         * Initialisation du menu des paramètres via un fragment.
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

        findViewById(R.id.notification_icon).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NotificationsActivity.class);
            v.getContext().startActivity(intent);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        /**
         * Gestion personnalisée du bouton retour.
         * Ferme le menu paramètres s'il est visible, sinon revient en arrière normalement.
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
    }

    /**
     * Méthode permettant d’ajouter dynamiquement une caméra à l’écran avec des options interactives.
     *
     * @param cameraName Le nom de la caméra à afficher
     */
    void addCamera(String cameraName) {
        // Carte principale contenant le nom de la caméra
        CardView cameraCard = new CardView(this);
        LinearLayout.LayoutParams cameraCardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cameraCardParams.gravity = Gravity.CENTER_HORIZONTAL;
        cameraCardParams.setMargins(0, 16, 0, 16);
        cameraCard.setLayoutParams(cameraCardParams);
        cameraCard.setCardBackgroundColor(Color.parseColor("#888888"));
        cameraCard.setRadius(24f);

        TextView cameraText = new TextView(this);
        cameraText.setText(cameraName);
        cameraText.setTextSize(25f);
        cameraText.setTypeface(null, Typeface.BOLD);
        cameraText.setGravity(Gravity.CENTER);
        cameraText.setTextColor(Color.parseColor("#000000"));
        cameraCard.addView(cameraText);

        // Carte secondaire contenant les switches
        CardView optionsCard = new CardView(this);
        optionsCard.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        optionsCard.setCardBackgroundColor(Color.parseColor("#cccccc"));
        optionsCard.setRadius(24f);
        optionsCard.setPadding(16, 16, 16, 16);

        LinearLayout optionsLayout = new LinearLayout(this);
        optionsLayout.setOrientation(LinearLayout.VERTICAL);
        optionsLayout.setVisibility(View.GONE);
        optionsLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        // Switch - Détection de vol
        Switch volDetectionSwitch = new Switch(this);
        volDetectionSwitch.setText("Détection de vol");
        volDetectionSwitch.setChecked(true);
        volDetectionSwitch.setTextColor(Color.BLACK);
        volDetectionSwitch.setTypeface(null, Typeface.BOLD);
        volDetectionSwitch.setThumbTintList(ColorStateList.valueOf(Color.BLUE));
        volDetectionSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#A2C8FF")));

        volDetectionSwitch.setSwitchPadding(10);
        volDetectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int thumbColor = isChecked ? Color.BLUE : Color.GRAY;
            int trackColor = isChecked ? Color.parseColor("#A2C8FF") : Color.parseColor("#E0E0E0");
            volDetectionSwitch.setThumbTintList(ColorStateList.valueOf(thumbColor));
            volDetectionSwitch.setTrackTintList(ColorStateList.valueOf(trackColor));
        });

        // Switch - Infraction
        Switch infractionDetectionSwitch = new Switch(this);
        infractionDetectionSwitch.setText("Infraction de vol");
        infractionDetectionSwitch.setChecked(true);
        infractionDetectionSwitch.setThumbTintList(ColorStateList.valueOf(Color.BLUE));
        infractionDetectionSwitch.setTrackTintList(ColorStateList.valueOf(Color.parseColor("#A2C8FF")));

        infractionDetectionSwitch.setTextColor(Color.BLACK);
        infractionDetectionSwitch.setTypeface(null, Typeface.BOLD);
        infractionDetectionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int thumbColor = isChecked ? Color.BLUE : Color.GRAY;
            int trackColor = isChecked ? Color.parseColor("#A2C8FF") : Color.parseColor("#E0E0E0");
            infractionDetectionSwitch.setThumbTintList(ColorStateList.valueOf(thumbColor));
            infractionDetectionSwitch.setTrackTintList(ColorStateList.valueOf(trackColor));
        });

        optionsLayout.addView(createCenteredSwitchLayout(volDetectionSwitch));
        optionsLayout.addView(createCenteredSwitchLayout(infractionDetectionSwitch));

        optionsCard.addView(optionsLayout);

        // Toggle visibilité des options au clic sur la carte de la caméra
        cameraCard.setOnClickListener(v -> {
            optionsLayout.setVisibility(
                    optionsLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE
            );
        });

        // Ajout des éléments au layout principal
        camerasLayout.addView(cameraCard);
        camerasLayout.addView(optionsCard);
    }
    private String getUserIdFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }
    private LinearLayout createCenteredSwitchLayout(Switch sw) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setPadding(0, 16, 0, 16);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(sw);
        return layout;
    }

    private void chargerCameras() {
        // 1. Récupérer l'ID utilisateur depuis les prefs
        String userId = getUserIdFromPrefs();

        if (userId == null) {
            Log.e("AuthPrefs", "user_id introuvable dans AuthPrefs");
            return; // on sort si pas d'utilisateur identifié
        }

        // 2. Appel Retrofit pour obtenir les RTSP URLs
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<String>> call = apiService.getRtspUrlsByClientId(userId);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> rtspUrls = response.body();
                    Log.d("API", "Caméras récupérées : " + rtspUrls.size());
                    for (String url : rtspUrls) {
                        Log.d("API", "→ URL caméra : " + url);
                    }

                    camerasLayout.removeAllViews();

                    for (String url : rtspUrls) {
                        addCamera(url);
                    }
                } else {
                    Log.e("API", "Échec récupération caméras, code=" + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("API", "Erreur réseau lors du chargement des caméras : " + t.getMessage());
            }
        });
    }


}