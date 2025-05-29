package com.example.argosapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Adapter personnalisé pour afficher une liste d'utilisateurs dans un RecyclerView.
 * <p>
 * Fournit une interface avec options de suppression et d'affichage masqué du mot de passe.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private Context context;

    /**
     * Constructeur du UserAdapter.
     *
     * @param context   contexte de l'application
     * @param userList  liste des utilisateurs à afficher
     */
    public UserAdapter(Context context, List<User> userList) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    /**
     * Lie les données de l'utilisateur à la vue correspondante.
     *
     * @param holder   le ViewHolder contenant les éléments de l'interface
     * @param position position de l'élément dans la liste
     */
    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, @SuppressLint("RecyclerView") int position) {
        User user = userList.get(position);
        holder.idTextView.setText(user.getId());
        holder.nomTextView.setText(user.getNom());
        holder.prenomTextView.setText(user.getPrenom());
        holder.passwordTextView.setText(user.getPassword());
        holder.organismeTextView.setText(user.getEnseigne());

        // Afficher/masquer le mot de passe à l'aide du bouton œil
        holder.eyePasswordButton.setOnClickListener(v -> {
            if (holder.passwordTextView.getTransformationMethod() == null) {
                holder.passwordTextView.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
                holder.eyePasswordButton.setImageResource(R.drawable.ic_psw_eye_closed);
            } else {
                holder.passwordTextView.setTransformationMethod(null);
                holder.eyePasswordButton.setImageResource(R.drawable.ic_psw_eye_open);
            }
        });

        // Suppression de l'utilisateur avec confirmation
        holder.deleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Confirmation de suppression")
                    .setMessage("Voulez-vous vraiment supprimer cet utilisateur ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        String idUser = holder.idTextView.getText().toString();
                        ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
                        Map<String, String> requestBody = new HashMap<>();
                        requestBody.put("id", idUser);

                        Call<ResponseBody> call = apiService.supprimerUtilisateur(idUser);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()) {
                                    userList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position, userList.size());
                                    Toast.makeText(v.getContext(), "Utilisateur supprimé avec succès !", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(v.getContext(), "Échec de la suppression de l'utilisateur...", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(v.getContext(), "Erreur de communication avec le serveur", Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Non", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    /**
     * ViewHolder interne représentant la structure d’un item utilisateur.
     */
    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView idTextView, nomTextView, prenomTextView, passwordTextView, organismeTextView;
        ImageButton deleteButton, eyePasswordButton;

        /**
         * Initialise les vues à partir du layout XML {@code item_user}.
         *
         * @param itemView la vue représentant un utilisateur dans la liste
         */
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.userId);
            nomTextView = itemView.findViewById(R.id.userNom);
            prenomTextView = itemView.findViewById(R.id.userPrenom);
            passwordTextView = itemView.findViewById(R.id.userPassword);
            organismeTextView = itemView.findViewById(R.id.userOrganisme);
            deleteButton = itemView.findViewById(R.id.deleteUser);
            eyePasswordButton = itemView.findViewById(R.id.eyePassword);
        }
    }
}
