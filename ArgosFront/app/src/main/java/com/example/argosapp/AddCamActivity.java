package com.example.argosapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Assure-toi que tu as bien importé ApiClient et ApiService
// import com.example.argosapp.network.ApiClient;
// import com.example.argosapp.network.ApiService;

public class AddCamActivity extends AppCompatActivity {

    private EditText editTextClientId;
    private EditText editTextStreamName;
    private EditText editTextRtspUrl;
    private Button addCamButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cam);

        // Références aux éléments de l’interface
        editTextClientId = findViewById(R.id.editTextClientId);
        editTextStreamName = findViewById(R.id.editTextStreamName);
        editTextRtspUrl = findViewById(R.id.editTextRtspUrl);
        addCamButton = findViewById(R.id.add_cam);

        // Boutons de navigation
        ImageView homeButton = findViewById(R.id.home_menu);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(AddCamActivity.this, HomeActivity.class);
            startActivity(intent);
        });

        ImageView userIcon = findViewById(R.id.icon_user);
        userIcon.setOnClickListener(v -> {
            Intent intent = new Intent(AddCamActivity.this, AccountsActivity.class);
            startActivity(intent);
        });

        // Gestion du clic sur le bouton d’ajout
        addCamButton.setOnClickListener(v -> {
            String clientId = editTextClientId.getText().toString().trim();
            String streamName = editTextStreamName.getText().toString().trim();
            String rtspUrl = editTextRtspUrl.getText().toString().trim();

            if (clientId.isEmpty() || streamName.isEmpty() || rtspUrl.isEmpty()) {
                showErrorDialog("Tous les champs doivent être remplis.");
            } else {
                // Création de l'objet CameraRequest
                CameraRequest camera = new CameraRequest(clientId, streamName, rtspUrl);

                // Appel à l’API via Retrofit
                ApiService apiService = ApiClient.getClient(AddCamActivity.this).create(ApiService.class);

                Log.d("AddCamDebug", "Envoi de la requête JSON : clientId=" + clientId + ", streamName=" + streamName + ", rtspUrl=" + rtspUrl);

                apiService.ajouterCamera(camera).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Intent resultIntent = new Intent();
                            setResult(RESULT_OK, resultIntent);
                            finish();
                        } else {
                            showErrorDialog("Erreur lors de l'ajout. Vérifiez les informations saisies.");
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
     * Affiche une boîte de dialogue avec un message d’erreur.
     */
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Erreur")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
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
}