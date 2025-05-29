package com.example.argosapp;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.PlayerView;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.util.Log;

public class VideoPlayerActivity extends AppCompatActivity {

    private ExoPlayer player;
    private PlayerView playerView;

    private String videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        playerView = findViewById(R.id.player_view);

        videoUrl = getIntent().getStringExtra("video_url");

        if (videoUrl == null || videoUrl.isEmpty()) {
            Toast.makeText(this, "URL vidéo manquante", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        try {
            // Vérifie si l'URL contient au moins un caractère '%' avant de décoder
            if (videoUrl.contains("%")) {
                videoUrl = java.net.URLDecoder.decode(videoUrl, "UTF-8");
                Log.d("VideoPlayer", "URL décodée : " + videoUrl);
            } else {
                Log.d("VideoPlayer", "URL non encodée, pas de décodage nécessaire : " + videoUrl);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur lors du décodage de l'URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializePlayer();
    }


    private void initializePlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        Uri videoUri = Uri.parse(videoUrl);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);

        player.prepare();
        player.play();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
