package com.example.argosapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Fragment affichant le menu latéral des paramètres dans plusieurs activités.
 * Permet de naviguer vers d'autres écrans et de gérer la déconnexion utilisateur.
 */
public class ParametersMenuFragmentActivity extends Fragment {

    /** Conteneur principal du menu de paramètres */
    private ConstraintLayout menuSettings;

    /** Indicateur d’état du menu (ouvert ou fermé) */
    private boolean isMenuVisible = false;

    /**
     * Méthode appelée pour initialiser l'UI du fragment.
     *
     * @param inflater           le LayoutInflater
     * @param container          le ViewGroup parent
     * @param savedInstanceState données sauvegardées précédemment
     * @return la vue générée par le fragment
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_parameters, container, false);

        menuSettings = rootView.findViewById(R.id.menuSettings);

        if (savedInstanceState != null) {
            isMenuVisible = savedInstanceState.getBoolean("menuVisible", false);
        }
        menuSettings.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);

        // Gestion des redirections vers d'autres activités
        rootView.findViewById(R.id.accountSpaceContainer).setOnClickListener(v -> openActivity(EspaceCompteActivity.class));
        rootView.findViewById(R.id.historicalContainer).setOnClickListener(v -> openActivity(HistoriqueActivity.class));
        rootView.findViewById(R.id.camerasManagementContainer).setOnClickListener(v -> openActivity(GestionCamUserActivity.class));
        rootView.findViewById(R.id.deconnectionTextView).setOnClickListener(v -> logout());
        rootView.findViewById(R.id.viewingContainer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VideoSurveillanceActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    /**
     * Affiche un dialogue de confirmation avant la déconnexion.
     */
    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Déconnexion")
                .setMessage("Voulez-vous vraiment vous déconnecter ?")
                .setPositiveButton("Oui", (dialog, which) -> performLogout())
                .setNegativeButton("Non", null)
                .show();
    }

    /**
     * Effectue la requête de déconnexion auprès de l'API.
     */
    private void performLogout() {
        ApiService apiService = ApiClient.getClient(requireContext()).create(ApiService.class);

        apiService.logout().enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("Logout", "Déconnexion réussie");
                } else {
                    Log.e("Logout", "Erreur lors de la déconnexion : " + response.code());
                }
                clearSessionAndRedirect();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Logout", "Échec de la déconnexion : " + t.getMessage());
                clearSessionAndRedirect();
            }
        });
    }

    /**
     * Supprime les préférences locales (notamment le JWT) et redirige vers la page de connexion.
     */
    private void clearSessionAndRedirect() {
        SharedPreferences prefs = requireContext().getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
        prefs.edit().clear().apply();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    /**
     * Méthode utilitaire pour ouvrir une activité.
     *
     * @param activityClass classe de l'activité à lancer
     */
    private void openActivity(Class<?> activityClass) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), activityClass);
            startActivity(intent);
        }
    }

    /**
     * Méthode appelée pour inverser la visibilité du menu.
     * Si ouvert, il est fermé. Sinon, il est affiché.
     */
    public void toggleMenuVisibility() {
        isMenuVisible = !isMenuVisible;
        if (menuSettings != null) {
            menuSettings.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Sauvegarde l’état de visibilité du menu (appelé lors des changements de configuration).
     *
     * @param outState le Bundle dans lequel stocker l’état
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("menuVisible", isMenuVisible);
    }

    /**
     * Restaure l’état de visibilité du menu après une recréation du fragment.
     *
     * @param view               vue du fragment
     * @param savedInstanceState bundle contenant l’état sauvegardé
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            isMenuVisible = savedInstanceState.getBoolean("menuVisible", false);
            menuSettings.setVisibility(isMenuVisible ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Retourne si le menu est actuellement visible à l'écran.
     *
     * @return true si visible, false sinon
     */
    public boolean isMenuCurrentlyVisible() {
        return menuSettings != null && menuSettings.getVisibility() == View.VISIBLE;
    }
}