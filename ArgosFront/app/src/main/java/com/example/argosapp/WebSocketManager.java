package com.example.argosapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * Singleton responsable de la gestion d'une connexion WebSocket pour recevoir
 * des notifications en temps réel du serveur.
 *
 * <p>Cette classe permet de se connecter à un serveur WebSocket, d'envoyer et
 * de recevoir des messages, et de notifier un {@link WebSocketNotificationListener}
 * lors d'événements spécifiques comme une intrusion ou un vol.</p>
 */
public class WebSocketManager {

    private static final String TAG = "WebSocketManager";
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private boolean isConnected = false;
    private WebSocketNotificationListener notificationListener;
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Constructeur privé pour le pattern Singleton.
     */
    private WebSocketManager() {}

    /**
     * Retourne l'instance unique de {@code WebSocketManager}.
     *
     * @return L'instance unique de WebSocketManager.
     */
    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    /**
     * Définit le listener à utiliser pour les notifications.
     *
     * @param listener Une implémentation de {@link WebSocketNotificationListener}.
     */
    public void setNotificationListener(WebSocketNotificationListener listener) {
        this.notificationListener = listener;
    }

    /**
     * Établit la connexion WebSocket avec le serveur.
     * Si un utilisateur est authentifié (via SharedPreferences),
     * un message d'authentification est envoyé automatiquement.
     *
     * @param context Le contexte Android pour accéder aux SharedPreferences.
     */
    public void connect(Context context) {
        if (isConnected) {
            Log.d(TAG, "Already connected");
            return;
        }

        Request request = new Request.Builder()
                .url("wss://l3a1backend-1.onrender.com/ws-notifications")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {

            @Override
            public void onOpen(@NonNull WebSocket socket, @NonNull okhttp3.Response response) {
                Log.d(TAG, "onOpen called");
                isConnected = true;

                SharedPreferences prefs = context.getSharedPreferences("AuthPrefs", Context.MODE_PRIVATE);
                String utilisateurId = prefs.getString("user_id", null);

                if (utilisateurId != null) {
                    socket.send("AUTH:" + utilisateurId);
                }
            }

            @Override
            public void onMessage(@NonNull WebSocket socket, @NonNull String text) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Log.d(TAG, "onMessage: " + text);
                    if (text.contains("intrusion") && notificationListener != null) {
                        notificationListener.onIntrusionDetected(text);
                    } else if (text.contains("vol") && notificationListener != null) {
                        notificationListener.onVolDetected(text);
                    }
                });
            }

            @Override
            public void onClosing(@NonNull WebSocket socket, int code, @NonNull String reason) {
                Log.d(TAG, "onClosing: " + code + " Reason: " + reason);
                isConnected = false;
            }

            @Override
            public void onFailure(@NonNull WebSocket socket, @NonNull Throwable t, okhttp3.Response response) {
                Log.e(TAG, "onFailure: ", t);
                isConnected = false;
            }
        });
    }

    /**
     * Envoie un message via la connexion WebSocket si elle est active.
     *
     * @param message Le message à envoyer.
     */
    public void sendMessage(String message) {
        if (webSocket != null && isConnected) {
            webSocket.send(message);
        }
    }

    /**
     * Ferme proprement la connexion WebSocket si elle est ouverte.
     */
    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Client closed");
            isConnected = false;
        }
    }

    /**
     * Vérifie si la connexion WebSocket est actuellement active.
     *
     * @return {@code true} si connecté, sinon {@code false}.
     */
    public boolean isConnected() {
        return isConnected;
    }
}
