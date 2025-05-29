package com.example.argosapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoriqueActivity extends AppCompatActivity {
    private RecyclerView recyclerViewHistorique;
    private HistoriqueAdapter adapter;
    private List<HistoriqueItem> historiqueList = new ArrayList<>();

    private ParametersMenuFragmentActivity parametersMenuFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        ImageView paramButton = findViewById(R.id.iconParametre);

        recyclerViewHistorique = findViewById(R.id.recyclerViewHistorique);
        recyclerViewHistorique.setLayoutManager(new LinearLayoutManager(this));

        // Initialiser l'adaptateur en passant un listener qui ouvre la video cloud
        adapter = new HistoriqueAdapter(historiqueList, videoUrl -> {
            Intent intent = new Intent(HistoriqueActivity.this, VideoPlayerActivity.class);
            intent.putExtra("video_url", videoUrl);
            startActivity(intent);
        });
        recyclerViewHistorique.setAdapter(adapter);

        getEnregistrements();

        // Gestion du menu paramètres (idem original)
        parametersMenuFragment = (ParametersMenuFragmentActivity) getSupportFragmentManager().findFragmentByTag("parametersFragment");
        if (parametersMenuFragment == null) {
            parametersMenuFragment = new ParametersMenuFragmentActivity();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, parametersMenuFragment, "parametersFragment")
                    .addToBackStack(null)
                    .commit();
        }
        findViewById(R.id.iconeParametre).setOnClickListener(v -> parametersMenuFragment.toggleMenuVisibility());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                View menuSettings = parametersMenuFragment.getView() != null ? parametersMenuFragment.getView().findViewById(R.id.menuSettings) : null;
                if (menuSettings != null && menuSettings.getVisibility() == View.VISIBLE) {
                    menuSettings.setVisibility(View.GONE);
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
        findViewById(R.id.notification_icon).setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NotificationsActivity.class);
            v.getContext().startActivity(intent);
        });
    }

    private String getUserIdFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }

    private void getEnregistrements() {
        String utilisateurId = getUserIdFromSharedPreferences();
        if (utilisateurId == null) {
            Log.e("HistoriqueActivity", "User ID is null");
            return;
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        Call<List<Enregistrement>> call = apiService.getEnregistrements(utilisateurId);

        call.enqueue(new Callback<List<Enregistrement>>() {
            @Override
            public void onResponse(Call<List<Enregistrement>> call, Response<List<Enregistrement>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("HistoriqueActivity", "Réponse reçue: " + new Gson().toJson(response.body()));
                    historiqueList.clear();
                    for (Enregistrement enregistrement : response.body()) {
                        // Construis ton nom d’enregistrement (ici par exemple dateajout ou nomfichier)
                        String nomEnregistrement = "Enregistrement_" + enregistrement.getDateajout();

                        // Exemple simple pour le problème, à adapter selon ta logique métier
                        String probleme = enregistrement.getNomProbleme();


                        // Récupère le chemin ou URL de la vidéo
                        String videoUrl = enregistrement.getNomfichier();
                        Log.e("HistoriqueActivity", "url: " + videoUrl);
                        // Ajoute à ta liste
                        historiqueList.add(new HistoriqueItem(nomEnregistrement, probleme, videoUrl));
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("HistoriqueActivity", "Erreur API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Enregistrement>> call, Throwable t) {
                Log.e("HistoriqueActivity", "Échec requête API: " + t.getMessage());
            }
        });
    }


}
