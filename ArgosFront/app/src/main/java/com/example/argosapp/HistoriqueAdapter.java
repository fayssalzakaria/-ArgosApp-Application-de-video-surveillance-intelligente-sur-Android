package com.example.argosapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoriqueAdapter extends RecyclerView.Adapter<HistoriqueAdapter.HistoriqueViewHolder> {

    public interface OnPlayButtonClickListener {
        void onPlayButtonClicked(String videoUrl);
    }

    private OnPlayButtonClickListener listener;
    private List<HistoriqueItem> historiqueList;

    public HistoriqueAdapter(List<HistoriqueItem> historiqueList, OnPlayButtonClickListener listener) {
        this.historiqueList = historiqueList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoriqueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_historique, parent, false);
        return new HistoriqueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoriqueViewHolder holder, int position) {
        HistoriqueItem item = historiqueList.get(position);

        holder.nomEnregistrement.setText(item.getNomEnregistrement());
        holder.probleme.setText(item.getProbleme());
        holder.probleme.setTypeface(null, Typeface.BOLD);

        if ("Alerte intrusion".equals(item.getProbleme())) {
            holder.probleme.setTextColor(Color.RED);
        } else {
            holder.probleme.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.orange));
        }

        // Au clic sur play, lancer la vidéo cloud via le listener
        holder.playButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPlayButtonClicked(item.getVideoUrl());
            }
        });
    }

    @Override
    public int getItemCount() {
        return historiqueList.size();
    }

    public static class HistoriqueViewHolder extends RecyclerView.ViewHolder {
        TextView nomEnregistrement;
        TextView probleme;
        ImageButton playButton;

        public HistoriqueViewHolder(@NonNull View itemView) {
            super(itemView);
            nomEnregistrement = itemView.findViewById(R.id.textNomEnregistrement);
            probleme = itemView.findViewById(R.id.textProblem);
            playButton = itemView.findViewById(R.id.playButton);
        }
    }
}
