package com.example.argosapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité permettant à un utilisateur de gérer les caméras.
 * Elle offre la possibilité d'accéder à l'ajout d'une caméra via un bouton et des icônes de navigation.
 */
public class GestionCamActivity extends AppCompatActivity {

    /** Bouton permettant de lancer le processus d'ajout de caméra */
    private Button btnAddCamera;
    private String userId; /** Id de l'utilisateur courant*/
    private LinearLayout cameraListLayout;/** liste des caméras*/


    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise les vues et configure les comportements des boutons.
     *
     * @param savedInstanceState Données de l'instance sauvegardée (si disponibles)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_cam);
        cameraListLayout = findViewById(R.id.camera_list);

        userId=getIntent().getStringExtra("utilisateur_id");

        btnAddCamera = findViewById(R.id.btn_add_camera);
        ImageView homeButton = findViewById(R.id.home_menu);
        ImageView userIcon = findViewById(R.id.icon_user);
        chargerCameras();
        // Clic sur "Ajouter une caméra"
        btnAddCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpCamera();
            }
        });

        // Navigation vers l'écran d'accueil
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GestionCamActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        // Navigation vers le compte utilisateur
        userIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GestionCamActivity.this, AccountsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Affiche une boîte de dialogue de confirmation avant de rediriger l'utilisateur
     * vers l'écran d'ajout de caméra.
     */
    private void popUpCamera() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Vous allez entrer dans l'espace d'ajout de caméra, voulez-vous continuer ?")
                .setPositiveButton("Continuer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(GestionCamActivity.this, AddCamActivity.class);
                        startActivityForResult(intent, 1001);


                    }
                })
                .setNegativeButton("Abandonner", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private void chargerCameras() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        Call<List<String>> call = apiService.getRtspUrlsByClientId(userId);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<String> rtspUrls = response.body();

                    for (int i = 0; i < rtspUrls.size(); i++) {
                        String url = rtspUrls.get(i);

                        TextView cameraView = new TextView(GestionCamActivity.this);
                        cameraView.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        ));
                        cameraView.setMinHeight(dpToPx(42));
                        cameraView.setBackgroundResource(R.drawable.bordure);
                        cameraView.setText(url); // ou afficher le nom si disponible
                        cameraView.setPadding(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
                        cameraView.setTextColor(getResources().getColor(android.R.color.black));
                        cameraView.setTextSize(18);
                        cameraView.setTypeface(null, android.graphics.Typeface.BOLD);

                        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cameraView.getLayoutParams();
                        params.setMargins(0, 0, 0, dpToPx(15));
                        cameraView.setLayoutParams(params);

                        cameraListLayout.addView(cameraView);
                    }

                } else {
                    Log.e("API", "Erreur de récupération des caméras");
                }
            }
            private int dpToPx(int dp) {
                float density = getResources().getDisplayMetrics().density;
                return Math.round((float) dp * density);
            }


            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.e("API", "Échec : " + t.getMessage());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Réactualise la liste des caméras après ajout
            cameraListLayout.removeAllViews(); // Pour éviter les doublons
            chargerCameras();
        }
    }


}