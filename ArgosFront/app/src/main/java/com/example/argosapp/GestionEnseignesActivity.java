package com.example.argosapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity principale pour gérer  les enseignes.
 * Permet de visualiser, ajouter, modifier, supprimer et exporter les données des clients.
 */
public class GestionEnseignesActivity extends AppCompatActivity {

    private TableLayout tableClients;
    private List<TableRow> rowsList = new ArrayList<>();
    private List<Enseigne> enseigneList = new ArrayList<>();
 //Viewtext associe au toast (pour les test)
    /**
     * Méthode appelée lors de la création de l'activité.
     * Initialise les éléments de l'interface utilisateur et configure les événements.
     *
     * @param savedInstanceState état sauvegardé de l'activité.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gestion_enseignes);

        // Initialisation du TableLayout
        tableClients = findViewById(R.id.tableClients);
        loadEnseignes();

        // Bouton Espace Compte
        ImageView compteButton = findViewById(R.id.ivUser);
        compteButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestionEnseignesActivity.this, AccountsActivity.class);
            startActivity(intent);
        });

        // Bouton espace Home
        ImageView homeButton = findViewById(R.id.ivHome);
        homeButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestionEnseignesActivity.this, HomeActivity.class);
            startActivity(intent);
        });
        // Bouton espace d'ajout d'enseignes
        ImageButton gestionEnseignesButton = findViewById(R.id.ajout_client);

        gestionEnseignesButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestionEnseignesActivity.this, AjoutEnseigneActivity.class);
            startActivityForResult(intent, 1);
        });

        // Mise en place de la recherche
        EditText searchEditText = findViewById(R.id.search);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterTableRows(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Bouton d'export en format csv
        ImageButton exportButton = findViewById(R.id.share);
        exportButton.setOnClickListener(v -> {
            exportToCSV();
        });
    }

    /**
     * Exporte les données des clients dans un fichier CSV.
     */

    public void exportToCSV() {
        if (rowsList == null || rowsList.isEmpty()) {
            Toast.makeText(this, "Aucune donnée à exporter", Toast.LENGTH_LONG).show();
            return;
        }

        // Pour Android 10 et plus, utilisation MediaStore pour l'accès au stockage
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "clients.csv");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // Insertion du fichier dans MediaStore
            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                    writer.append("ID,Enseigne,Adresse\n"); // En-têtes de colonnes

                    // Boucle pour ajouter les données des clients
                    for (TableRow row : rowsList) {
                        TextView textViewID = (TextView) row.getChildAt(0);
                        TextView textViewName = (TextView) row.getChildAt(1);
                        TextView textViewAdresse = (TextView) row.getChildAt(2); // Récupère l'adresse (colonne 3)

                        // Vérifier que les TextViews ne sont pas vides
                        String id = textViewID.getText().toString().trim();
                        String name = textViewName.getText().toString().trim();
                        String adresse = textViewAdresse.getText().toString().trim();

                        // Séparer les colonnes par un virgule et ajouter à l'écriture du fichier CSV
                        writer.append(id).append(","); // ID
                        writer.append(name).append(","); // Enseigne
                        writer.append(adresse).append("\n"); // Adresse
                    }

                    writer.flush();
                    Toast.makeText(this, "Fichier exporté : " + uri.toString(), Toast.LENGTH_LONG).show();

                }
            } catch (IOException e) {
                Log.e("ExportCSV", "Erreur lors de l'exportation", e);
                Toast.makeText(this, "Erreur lors de l'exportation", Toast.LENGTH_LONG).show();
            }
        } else {
            // Pour Android 9 et inférieur, utiliser la méthode classique
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists() && !directory.mkdirs()) {
                Toast.makeText(this, "Échec de la création du dossier de destination", Toast.LENGTH_LONG).show();
                return;
            }

            File file = new File(directory, "clients.csv");

            try (FileWriter writer = new FileWriter(file)) {
                writer.append("ID,Enseigne,Adresse\n"); // En-têtes de colonnes

                // Boucle pour ajouter les données des clients
                for (TableRow row : rowsList) {
                    TextView textViewID = (TextView) row.getChildAt(0);
                    TextView textViewName = (TextView) row.getChildAt(1);
                    TextView textViewAdresse = (TextView) row.getChildAt(2); // Récupère l'adresse (colonne 3)

                    // Vérifier que les TextViews ne sont pas vides
                    String id = textViewID.getText().toString().trim();
                    String name = textViewName.getText().toString().trim();
                    String adresse = textViewAdresse.getText().toString().trim();

                    // Séparer les colonnes par un virgule et ajouter à l'écriture du fichier CSV
                    writer.append(id).append(","); // ID
                    writer.append(name).append(","); // Enseigne
                    writer.append(adresse).append("\n"); // Adresse
                }

                writer.flush();
                Toast.makeText(this, "Fichier exporté : " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Log.e("ExportCSV", "Erreur lors de l'exportation", e);
                Toast.makeText(this, "Erreur lors de l'exportation", Toast.LENGTH_LONG).show();
            }
        }
    }


    /**
     * Ajoute un client à la table affichée dans l'interface.
     *
     * @param clientID      L'ID du client.
     * @param clientName    Le nom de l'enseigne.
     * @param clientAdresse L'adresse de l'enseigne.
     */
    private void addClientRow(String clientID, String clientName, String clientAdresse) {
        TableRow newRow = new TableRow(this);

        // ID et Enseigne : poids 1
        TableRow.LayoutParams layoutWeight1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
        // Adresse : poids 2
        TableRow.LayoutParams layoutWeight2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);


        // ID
        TextView textViewID = new TextView(this);
        textViewID.setLayoutParams(layoutWeight1);
        textViewID.setText(clientID);
        textViewID.setGravity(Gravity.START);
        textViewID.setPadding(4, 4, 4, 4);
        textViewID.setTypeface(null, Typeface.BOLD);

        // Nom
        TextView textViewName = new TextView(this);
        textViewName.setLayoutParams(layoutWeight1);
        textViewName.setText(clientName);
        textViewName.setGravity(Gravity.CENTER);
        textViewName.setPadding(4, 4, 4, 4);
        textViewName.setTypeface(null, Typeface.BOLD);

        // Adresse
        TextView textViewAdresse = new TextView(this);
        textViewAdresse.setLayoutParams(layoutWeight2);
        textViewAdresse.setText(clientAdresse);
        textViewAdresse.setGravity(Gravity.CENTER);
        textViewName.setPadding(4, 4, 4, 4);
        textViewAdresse.setTypeface(null, Typeface.BOLD);


        // BOUTON MODIFIER
        LinearLayout layoutModifier = new LinearLayout(this);
        layoutModifier.setLayoutParams(layoutWeight1);
        layoutModifier.setGravity(Gravity.CENTER);

        ImageButton modifierButton = new ImageButton(this);
        modifierButton.setImageResource(R.drawable.modifier); // ton icône
        modifierButton.setBackgroundColor(Color.TRANSPARENT);
        modifierButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        modifierButton.setAdjustViewBounds(true);
        modifierButton.setLayoutParams(new LinearLayout.LayoutParams(80, 80)); // taille fixe

        layoutModifier.addView(modifierButton);

        // BOUTON SUPPRIMER
        LinearLayout layoutSupprimer = new LinearLayout(this);
        layoutSupprimer.setLayoutParams(layoutWeight1);
        layoutSupprimer.setGravity(Gravity.CENTER);

        ImageButton supprimerButton = new ImageButton(this);
        supprimerButton.setImageResource(R.drawable.delete); // ton icône
        supprimerButton.setBackgroundColor(Color.TRANSPARENT);
        supprimerButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        supprimerButton.setAdjustViewBounds(true);
        supprimerButton.setLayoutParams(new LinearLayout.LayoutParams(80, 80)); // taille fixe

        layoutSupprimer.addView(supprimerButton);

        //action du bouton modifier
        // action du bouton modifier
        modifierButton.setOnClickListener(v -> {
            Intent intent = new Intent(GestionEnseignesActivity.this, GestionCamActivity.class);
            intent.putExtra("utilisateur_id", clientID); // Transmettre l'ID utilisateur
            startActivity(intent);
        });
        //action du bouton supprimer
        supprimerButton.setOnClickListener(v -> {
            int position = rowsList.indexOf(newRow);
            new AlertDialog.Builder(GestionEnseignesActivity.this)
                    .setTitle("Confirmation")
                    .setMessage("Êtes-vous sûr de vouloir supprimer cette ligne ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        String nomEnseigne = textViewName.getText().toString(); // Récupérer nom de l'enseigne
                        String adresseEnseigne = textViewAdresse.getText().toString(); // Récupérer adresse de l'enseigne

                        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

                        // Effectuer l'appel API avec le nom et l'adresse de l'enseigne
                        Call<ResponseBody> call = apiService.supprimerEnseigne(nomEnseigne, adresseEnseigne);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    tableClients.removeView(newRow);
                                    rowsList.remove(newRow);
                                    enseigneList.remove(position);
                                    Toast.makeText(GestionEnseignesActivity.this, "Enseigne supprimée avec succès", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(GestionEnseignesActivity.this, "Erreur lors de la suppression", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Non", null)
                    .show();
        });

        // Ajouter les vues au TableRow
        newRow.addView(textViewID);
        newRow.addView(textViewName);
        newRow.addView(textViewAdresse);
        newRow.addView(layoutModifier);
        newRow.addView(layoutSupprimer);

        // Ajouter le nouveau row au TableLayout
        tableClients.addView(newRow);
        rowsList.add(newRow);
    }

    /**
     * Affiche un dialogue pour modifier une enseigne.
     *
     * @param textView Le TextView qui contient l'ID de l'enseigne à modifier.
     */
    private void showEditDialog(TextView textView) {
        Intent intent = new Intent(this, GestionCamActivity.class);
        startActivity(intent);
    }

    /**
     * Méthode appelée lorsqu'une activité enfant retourne un résultat.
     * Utilisée pour recharger la liste des enseignes après l'ajout d'une nouvelle enseigne.
     *
     * @param requestCode Le code de la requête pour identifier l'appel.
     * @param resultCode  Le code de résultat de l'activité.
     * @param data        Les données retournées par l'activité.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Si la requête correspond à l'ajout d'une enseigne et que l'ajout a réussi
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadEnseignes(); // Recharger la liste des enseignes
            Toast.makeText(GestionEnseignesActivity.this, "Enseigne ajoutée avec succès!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Filtre les lignes du tableau des clients en fonction du texte de recherche.
     * Seules les lignes dont l'ID client contient la requête de recherche sont visibles.
     *
     * @param query Le texte de recherche entré par l'utilisateur.
     */
    private void filterTableRows(String query) {
        for (TableRow row : rowsList) {
            TextView nameTextView = (TextView) row.getChildAt(0);
            String name = nameTextView.getText().toString().toLowerCase();

            if (name.contains(query.toLowerCase())) {
                row.setVisibility(View.VISIBLE);
            } else {
                row.setVisibility(View.GONE);
            }
        }
    }


    /**
     * Sauvegarde l'état actuel de l'activité avant qu'elle ne soit détruite (par exemple lors d'une rotation de l'écran).
     * Sauvegarde les données des clients dans le bundle pour pouvoir les restaurer plus tard.
     *
     * @param outState Le bundle dans lequel l'état de l'activité sera sauvegardé.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> clientIDs = new ArrayList<>();
        ArrayList<String> clientNames = new ArrayList<>();
        ArrayList<String> clientAdresses = new ArrayList<>();

        // Récupérer les informations de chaque ligne du tableau
        for (TableRow row : rowsList) {
            TextView textViewID = (TextView) row.getChildAt(0);
            TextView textViewName = (TextView) row.getChildAt(1);
            TextView textViewAdresses = (TextView) row.getChildAt(2);

            clientIDs.add(textViewID.getText().toString());
            clientNames.add(textViewName.getText().toString());
            clientAdresses.add(textViewAdresses.getText().toString());
        }

        // Sauvegarder les listes dans le bundle
        outState.putStringArrayList("clientIDs", clientIDs);
        outState.putStringArrayList("clientNames", clientNames);
        outState.putStringArrayList("clientAdresses", clientAdresses);
    }

    /**
     * Récupère la liste des enseignes via une API.
     * Cette méthode envoie une requête pour obtenir les enseignes et met à jour l'interface après la réponse.
     */
    private void loadEnseignes() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<Enseigne>> call = apiService.getEnseignesNonAdmin(); // Appel API pour récupérer les enseignes non administrateurs
        call.enqueue(new Callback<List<Enseigne>>() {
            @Override
            public void onResponse(Call<List<Enseigne>> call, Response<List<Enseigne>> response) {
                if (response.isSuccessful()) {
                    enseigneList = response.body(); // Récupérer la liste des enseignes
                    Log.d("DEBUG", "Nombre d'enseignes récupérées : " + enseigneList.size());
                    updateClientTable(); // Mettre à jour le tableau des clients avec les nouvelles données
                }
                Log.e("DEBUG", "Échec de l'appel réseau : ");
            }

            @Override
            public void onFailure(Call<List<Enseigne>> call, Throwable t) {
                Log.e("API_ERROR", "Échec de l'appel réseau : " + t.getMessage(), t);
                Toast.makeText(GestionEnseignesActivity.this, "Erreur de chargement des enseignes", Toast.LENGTH_SHORT).show(); // Gérer l'échec de l'appel réseau
            }
        });
    }

    /**
     * Met à jour le tableau des clients avec les nouvelles enseignes récupérées via l'API.
     * Cette méthode supprime toutes les lignes existantes sauf la première (qui est statique) et ajoute les nouvelles lignes.
     */
    private void updateClientTable() {
        int firstRowCount = 1; // La première ligne est celle qui doit être préservée

        // Vider la table (sauf la première ligne)
        tableClients.removeViews(firstRowCount, tableClients.getChildCount() - firstRowCount);

        // Ajouter ou mettre à jour les lignes avec les nouvelles données des enseignes
        for (Enseigne enseigne : enseigneList) {
            addClientRow(enseigne.getIdUtilisateur(), enseigne.getNomEnseigne(), enseigne.getAdresseEnseigne());
        }
    }

}
