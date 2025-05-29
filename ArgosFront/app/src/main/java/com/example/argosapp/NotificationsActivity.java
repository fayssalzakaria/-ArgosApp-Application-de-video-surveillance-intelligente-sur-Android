package com.example.argosapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

/**
 * Activité responsable de l'affichage des notifications de l'utilisateur.
 * <p>
 * Cette activité :
 * <ul>
 *     <li>Affiche les notifications dans un {@link RecyclerView}</li>
 *     <li>Permet de gérer un menu de paramètres via un fragment</li>
 *     <li>Fait appel à l'API pour récupérer les notifications à partir de l'ID utilisateur</li>
 * </ul>
 */
public class NotificationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NotificationsAdapter adapter;
    private List<NotificationModel> notificationsList;
    private ParametersMenuFragmentActivity parametersMenuFragment;

    /**
     * Méthode appelée lors de la création de l'activité.
     *
     * @param savedInstanceState L'état de l'instance précédemment sauvegardée (le cas échéant).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        recyclerView = findViewById(R.id.recyclerViewNotifications);
        notificationsList = new ArrayList<>();
        adapter = new NotificationsAdapter(notificationsList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        getNotifications(); // Appel à l'API pour charger les notifications

        // Gestion du fragment du menu des paramètres
        parametersMenuFragment = (ParametersMenuFragmentActivity) getSupportFragmentManager().findFragmentByTag("parametersFragment");
        if (parametersMenuFragment == null) {
            parametersMenuFragment = new ParametersMenuFragmentActivity();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, parametersMenuFragment, "parametersFragment")
                    .addToBackStack(null)
                    .commit();
        }

        // Gestion du clic sur l'icône des paramètres
        findViewById(R.id.iconParametre).setOnClickListener(v -> parametersMenuFragment.toggleMenuVisibility());

        // Affiche la flèche de retour dans la barre d'action
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Gestion personnalisée du bouton retour
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                View menuSettings = parametersMenuFragment.getView() != null
                        ? parametersMenuFragment.getView().findViewById(R.id.menuSettings)
                        : null;

                if (menuSettings != null && menuSettings.getVisibility() == View.VISIBLE) {
                    menuSettings.setVisibility(View.GONE);
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    /**
     * Récupère l'ID de l'utilisateur depuis les SharedPreferences.
     *
     * @return L'identifiant de l'utilisateur ou {@code null} s'il est introuvable.
     */
    private String getUserIdFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }

    /**
     * Récupère les notifications de l'utilisateur via l'API REST.
     * Les données reçues sont injectées dans l'adaptateur de la RecyclerView.
     */
    private void getNotifications() {
        String utilisateurId = getUserIdFromSharedPreferences();

        if (utilisateurId == null || utilisateurId.isEmpty()) {
            Log.e("getUnreadNotificationsCount", "L'ID utilisateur est introuvable.");
            return;
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        apiService.getAllNotifications(utilisateurId).enqueue(new Callback<List<NotificationModel>>() {
            @Override
            public void onResponse(Call<List<NotificationModel>> call, Response<List<NotificationModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "Notifications reçues : " + response.body().size());
                    notificationsList.clear();
                    notificationsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Réponse vide ou invalide");
                }
            }

            @Override
            public void onFailure(Call<List<NotificationModel>> call, Throwable t) {
                Log.e("API_FAILURE", "Erreur API : " + t.getMessage());
            }
        });
    }
}
