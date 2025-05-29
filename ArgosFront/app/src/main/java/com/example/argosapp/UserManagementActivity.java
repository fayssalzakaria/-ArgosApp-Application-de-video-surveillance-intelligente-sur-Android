package com.example.argosapp;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activité Android pour la gestion des utilisateurs.
 * <p>
 * Elle permet l'affichage, la recherche dynamique, l'exportation CSV et la navigation
 * entre les vues d'administration.
 */
public class UserManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private EditText searchUser;
    private static final int REQUEST_STORAGE_PERMISSION = 100;

    /**
     * Méthode appelée à la création de l'activité. Initialise les composants, permissions et données.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchUser = findViewById(R.id.searchUser);
        checkStoragePermissions();
        fetchUsers();

        searchUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    /**
     * Filtre dynamiquement les utilisateurs affichés selon une chaîne de recherche.
     *
     * @param query texte saisi dans la barre de recherche
     */
    private void filterUsers(String query) {
        if (userList == null || userList.isEmpty()) return;

        List<User> filteredList = new ArrayList<>();
        for (User user : userList) {
            if (user.getNom().toLowerCase().contains(query.toLowerCase()) ||
                    user.getPrenom().toLowerCase().contains(query.toLowerCase()) ||
                    user.getId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(user);
            }
        }
        userAdapter = new UserAdapter(this, filteredList);
        recyclerView.setAdapter(userAdapter);
    }

    /**
     * Lance l'activité pour ajouter un utilisateur.
     */
    public void onPlusClick(View view) {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivityForResult(intent, 1);
    }

    /**
     * Gère le clic sur le bouton d'export. Vérifie les permissions puis exporte.
     */
    public void onExportClick(View view) {
        if (checkStoragePermissions()) {
            exportToCSV();
        }
    }

    /**
     * Vérifie si les permissions d'accès au stockage sont accordées.
     *
     * @return vrai si l'accès est autorisé, faux sinon
     */
    private boolean checkStoragePermissions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION);
                return false;
            }
        } else {
            return true;
        }
        return true;
    }

    /**
     * Gère la réponse à la demande de permission d'écriture.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission accordée, vous pouvez exporter.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission refusée, l'exportation ne fonctionnera pas.", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Exporte les utilisateurs au format CSV dans le dossier de téléchargements.
     */
    public void exportToCSV() {
        if (userList == null || userList.isEmpty()) {
            Toast.makeText(this, "Aucun utilisateur à exporter", Toast.LENGTH_LONG).show();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, "users_list.csv");
            values.put(MediaStore.MediaColumns.MIME_TYPE, "text/csv");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);

            try (OutputStream outputStream = getContentResolver().openOutputStream(uri)) {
                if (outputStream != null) {
                    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                    writer.append("ID,Nom,Prenom,Mot de passe,Organisme\n");
                    for (User user : userList) {
                        writer.append(user.getId()).append(",")
                                .append(user.getNom()).append(",")
                                .append(user.getPrenom()).append(",")
                                .append(user.getPassword()).append(",")
                                .append(user.getEnseigne()).append("\n");
                    }
                    writer.flush();
                    Toast.makeText(this, "Fichier exporté : " + uri.toString(), Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                Log.e("ExportCSV", "Erreur lors de l'exportation", e);
                Toast.makeText(this, "Erreur lors de l'exportation", Toast.LENGTH_LONG).show();
            }
        } else {
            File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!directory.exists() && !directory.mkdirs()) {
                Toast.makeText(this, "Échec de la création du dossier de destination", Toast.LENGTH_LONG).show();
                return;
            }

            File file = new File(directory, "users_list.csv");

            try (FileWriter writer = new FileWriter(file)) {
                writer.append("ID,Nom,Prenom,Mot de passe,Organisme\n");
                for (User user : userList) {
                    writer.append(user.getId()).append(",")
                            .append(user.getNom()).append(",")
                            .append(user.getPrenom()).append(",")
                            .append(user.getPassword()).append(",")
                            .append(user.getEnseigne()).append("\n");
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
     * Navigue vers l'écran d'accueil.
     */
    public void onHomeClick(View view) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    /**
     * Navigue vers l’écran des comptes utilisateurs.
     */
    public void onUserClick(View view) {
        Intent intent = new Intent(this, AccountsActivity.class);
        startActivity(intent);
    }

    /**
     * Récupère la liste des utilisateurs depuis l’API distante.
     */
    private void fetchUsers() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<User>> call = apiService.getAllUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                Log.d("API_RESPONSE", new Gson().toJson(response.body()));
                if (response.isSuccessful() && response.body() != null) {
                    userList = response.body();
                    Log.d("USER_LIST", "Utilisateur récupéré : " + userList.size());
                    userAdapter = new UserAdapter(UserManagementActivity.this, userList);
                    recyclerView.setAdapter(userAdapter);
                    userAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(UserManagementActivity.this, "Erreur de chargement", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(UserManagementActivity.this, "Erreur: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Rafraîchit la liste des utilisateurs après le retour d'une activité (ex. ajout).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            fetchUsers();
        }
    }
}
