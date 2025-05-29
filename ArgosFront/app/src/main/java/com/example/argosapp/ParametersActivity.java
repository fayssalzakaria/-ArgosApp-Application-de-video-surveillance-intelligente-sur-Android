package com.example.argosapp;

import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activité affichant le menu des paramètres de l'application via un fragment.
 * Elle gère l'ouverture/fermeture du panneau latéral et la gestion du bouton retour.
 */
public class ParametersActivity extends AppCompatActivity {

    /** Fragment du menu latéral des paramètres */
    private ParametersMenuFragmentActivity parametersMenuFragment;

    /**
     * Point d’entrée de l’activité. Initialise l’affichage du fragment et les comportements d’UI.
     *
     * @param savedInstanceState état sauvegardé de l’activité, s’il existe
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameters);

        // Affichage du menu paramètres via un fragment (si non encore présent)
        parametersMenuFragment = (ParametersMenuFragmentActivity)
                getSupportFragmentManager().findFragmentByTag("parametersFragment");

        if (parametersMenuFragment == null) {
            parametersMenuFragment = new ParametersMenuFragmentActivity();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, parametersMenuFragment, "parametersFragment")
                    .addToBackStack(null)
                    .commit();
        }

        // Clic sur l'icône des paramètres pour afficher ou masquer le menu
        findViewById(R.id.logoParameters).setOnClickListener(
                v -> parametersMenuFragment.toggleMenuVisibility()
        );

        // Affichage de la flèche retour dans la barre d’action
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Gestion personnalisée du bouton retour : ferme le menu si ouvert
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
}
