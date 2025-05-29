package com.example.argosapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.util.Log;

/**
 * Adaptateur pour afficher une liste de {@link NotificationModel} dans un {@link RecyclerView}.
 * Chaque notification affiche un message, une date, et réagit aux clics pour être marquée comme lue.
 */
public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationViewHolder> {

    /** Liste des notifications à afficher */
    private List<NotificationModel> notifications;

    /**
     * Constructeur de l'adaptateur.
     *
     * @param notifications Liste des notifications à injecter dans le RecyclerView
     */
    public NotificationsAdapter(List<NotificationModel> notifications) {
        this.notifications = notifications;
    }

    /**
     * Crée une nouvelle vue (ViewHolder) à partir du layout XML.
     */
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    /**
     * Lie les données d'une notification à la vue correspondante.
     */
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);

        holder.textViewMessage.setText(notification.getMessage());
        holder.textViewDate.setText(notification.getDateHeure());

        // Texte du message en gras
        holder.textViewMessage.setTypeface(null, android.graphics.Typeface.BOLD);

        // Style de la notification en fonction de son état de lecture et de son type
        if (notification.getIsRead()) {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.textViewMessage.setTextColor(Color.BLACK);
        } else {
            switch (notification.getMessage()) {
                case "Alerte intrusion":
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFCDD2")); // Rouge clair
                    holder.textViewMessage.setTextColor(Color.parseColor("#B71C1C")); // Rouge foncé
                    break;
                case "Suspicion de vol":
                    holder.itemView.setBackgroundColor(Color.parseColor("#FFE0B2")); // Orange clair
                    holder.textViewMessage.setTextColor(Color.parseColor("#FF8C00")); // Orange foncé
                    break;
                default:
                    holder.itemView.setBackgroundColor(Color.parseColor("#E0E0E0")); // Gris clair
                    holder.textViewMessage.setTextColor(Color.BLACK);
                    break;
            }
        }

        // La date reste toujours en noir
        holder.textViewDate.setTextColor(Color.BLACK);

        // Gestion du clic : marquer comme lu localement et via l'API
        holder.itemView.setOnClickListener(v -> {
            notification.setRead(true); // MAJ locale
            notifyItemChanged(position); // Rafraîchit la ligne

            // Appel serveur pour marquer la notification comme lue
            ApiService apiService = ApiClient.getClient(holder.itemView.getContext()).create(ApiService.class);
            apiService.markAsRead(notification.getId()).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Log.d("API", "Notification marquée comme lue !");
                    } else {
                        Log.e("API", "Erreur lors de la mise à jour (code " + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("API", "Erreur réseau : " + t.getMessage());
                }
            });
        });
    }

    /**
     * Retourne le nombre total de notifications dans la liste.
     */
    @Override
    public int getItemCount() {
        return notifications.size();
    }

    /**
     * ViewHolder interne pour représenter une seule notification à l'écran.
     */
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView textViewMessage, textViewDate;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}