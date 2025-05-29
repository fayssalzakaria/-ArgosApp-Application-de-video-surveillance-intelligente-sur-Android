// Package de l'application
package com.example.argosapp;

// Import des bibliothèques Android
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.widget.Button;
import android.graphics.Color;
import android.content.Intent;

// Import des bibliothèques AndroidX
import androidx.appcompat.app.AppCompatActivity;

// Import des bibliothèques JSON et WebRTC
import com.google.gson.Gson;
import org.webrtc.*;

// Import WebSocket
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.websocket.*;


import android.content.SharedPreferences;

import androidx.activity.OnBackPressedCallback;
import android.graphics.Typeface;

import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;




// Classe principale de l’activité de vidéo surveillance
public class VideoSurveillanceActivity4 extends AppCompatActivity implements WebSocketNotificationListener {

    // TAG pour les logs
    private static final String TAG = "VideoSurveillance4";

    // Constantes pour le serveur de signalisation WebSocket
    private static final String SIGNALING_SERVER_URL = "ws://192.168.1.121:8080/ws";
    private static final String CLIENT_ID = "androidClient4";
    private static final String TARGET_ID = "mediamtx";

    // Références aux vues pour les notifications
    private LinearLayout notificationContainer;
    private TextView tvEffraction, tvVol;

    // Vue pour le rendu vidéo
    private SurfaceViewRenderer surfaceViewRenderer;

    // Compteurs d'événements
    private int effractionCount = 0;
    private int volCount = 0;

    // WebRTC : éléments nécessaires pour la vidéo
    private EglBase rootEglBase;
    private PeerConnectionFactory peerConnectionFactory;
    private PeerConnection peerConnection;

    // Client de signalisation (WebSocket)
    private SignalingClient signalingClient;

    private WebSocket webSocket;
    private ParametersMenuFragmentActivity parametersMenuFragment;



    // Méthode appelée à la création de l'activité
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_surveillance);

        // Bouton caméra 1 désactivé (grisé)
        Button btnCam4 = findViewById(R.id.btn_cam4);
        btnCam4.setBackgroundColor(Color.GRAY);

        // Initialisation des vues pour les notifications
        notificationContainer = findViewById(R.id.notification_container);
        tvEffraction = findViewById(R.id.tv_effraction);
        tvVol = findViewById(R.id.tv_vol);

        // Configuration du rendu vidéo WebRTC
        surfaceViewRenderer = findViewById(R.id.surface_view);
        rootEglBase = EglBase.create();
        surfaceViewRenderer.init(rootEglBase.getEglBaseContext(), null);
        surfaceViewRenderer.setMirror(true);
        surfaceViewRenderer.setZOrderMediaOverlay(true);


        // Connexion a la websocket
        WebSocketManager.getInstance().setNotificationListener(this);
        WebSocketManager.getInstance().connect(this);

        // Récupérer les notifications non lues au démarrage
        getUnreadNotificationsCount();


        // Initialisation de WebRTC
        initializePeerConnectionFactory();

        // Connexion au serveur de signalisation WebSocket
        connectToSignalingServer();



        /**
         * Affichage du menu parametre
         **/
        // Récupérer le fragment existant ou en créer un nouveau si nécessaire
        parametersMenuFragment = (ParametersMenuFragmentActivity) getSupportFragmentManager().findFragmentByTag("parametersFragment");
        if (parametersMenuFragment == null) {
            parametersMenuFragment = new ParametersMenuFragmentActivity();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, parametersMenuFragment, "parametersFragment")
                    .addToBackStack(null)
                    .commit();
        }

        // Gérer le clic sur l'icône des paramètres avec une lambda expression
        findViewById(R.id.iconeParametre).setOnClickListener(v -> parametersMenuFragment.toggleMenuVisibility());
        // Configurer la flèche de retour dans la barre d'action
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Gestion du bouton retour personnalisé
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                View menuSettings = parametersMenuFragment.getView() != null ? parametersMenuFragment.getView().findViewById(R.id.menuSettings) : null;
                if (menuSettings != null && menuSettings.getVisibility() == View.VISIBLE) {
                    menuSettings.setVisibility(View.GONE);
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

    }




    @Override
    public void onIntrusionDetected(String message) {
        simulateNewNotifications(message,1, 0);

    }

    @Override
    public void onVolDetected(String message) {
        simulateNewNotifications(message,0, 1);
    }



    /**
     * Affiche une notification et met à jour l'interface utilisateur.
     */
    private void simulateNewNotifications(String text, int effractions, int vols) {
        effractionCount += effractions;
        volCount += vols;
        updateNotifications();  // Applique la mise à jour sans passer de texte dynamique
    }

    private void updateNotifications() {
        // Vérifie si au moins une notification doit être affichée
        if (effractionCount > 0 || volCount > 0) {
            notificationContainer.setVisibility(View.VISIBLE);
        } else {
            notificationContainer.setVisibility(View.GONE);
        }

        // Mise à jour pour "Suspicion de vol"
        if (volCount > 0) {
            tvVol.setText("Suspicion de vol (" + volCount + ")");
            tvVol.setTypeface(Typeface.DEFAULT_BOLD);
            tvVol.setTextColor(0xFFFFA500);
            tvVol.setVisibility(View.VISIBLE);
        } else {
            tvVol.setVisibility(View.GONE);
        }

        // Mise à jour pour "Alerte intrusion"
        if (effractionCount > 0) {
            tvEffraction.setText("Alerte intrusion (" + effractionCount + ")");
            tvEffraction.setTypeface(Typeface.DEFAULT_BOLD);
            tvEffraction.setTextColor(0xFFFF0000);
            tvEffraction.setVisibility(View.VISIBLE);
        } else {
            tvEffraction.setVisibility(View.GONE);
        }
    }


    private String getUserIdFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.getString("user_id", null);
    }

    private void getUnreadNotificationsCount() {
        String utilisateurId = getUserIdFromSharedPreferences();

        if (utilisateurId == null || utilisateurId.isEmpty()) {
            Log.e("getUnreadNotificationsCount", "L'ID utilisateur est introuvable.");
            return;  // Ne pas poursuivre si l'utilisateurId est invalide
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        // Récupérer le nombre de notifications non lues pour "Vol"
        apiService.getCountNotificationsVolUnread(utilisateurId).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, retrofit2.Response<Long> response) {
                if (response.isSuccessful()) {
                    long count = response.body();
                    volCount = (int) count;
                    Log.d("API", "Appel REST pour récupérer les notifications non lues");
                    updateNotifications();
                } else {
                    Log.e("ApiClient", "Erreur lors de la récupération des notifications de type Vol : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("ApiClient", "Erreur lors de la récupération des notifications de type Vol", t);
            }
        });

        // Récupérer le nombre de notifications non lues pour "Intrusion"
        apiService.getCountNotificationsIntrusionUnread(utilisateurId).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, retrofit2.Response<Long> response) {
                if (response.isSuccessful()) {
                    long count = response.body();
                    effractionCount = (int) count;
                    Log.d("API", "Appel REST pour récupérer les notifications non lues");
                    updateNotifications();
                } else {
                    Log.e("ApiClient", "Erreur lors de la récupération des notifications de type Intrusion : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                Log.e("ApiClient", "Erreur lors de la récupération des notifications de type Intrusion", t);
            }
        });
    }



    private void markAllNotificationsAsRead(String text) {
        effractionCount = 0;
        volCount = 0;
        updateNotifications();
    }



    // Initialisation de la PeerConnectionFactory WebRTC
    private void initializePeerConnectionFactory() {
        PeerConnectionFactory.InitializationOptions initOptions =
                PeerConnectionFactory.InitializationOptions
                        .builder(this)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initOptions);

        // Factories pour encoder/décoder la vidéo
        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(
                rootEglBase.getEglBaseContext());

        // Création de la factory de connexions WebRTC
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    // Connexion au serveur de signalisation WebSocket
    private void connectToSignalingServer() {
        signalingClient = new SignalingClient();
        new Thread(() -> {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            try {
                container.connectToServer(signalingClient, URI.create(SIGNALING_SERVER_URL));
            } catch (DeploymentException | java.io.IOException e) {
                Log.e(TAG, "WebSocket connection failed", e);
            }
        }).start();
    }

    // Classe client WebSocket pour la signalisation WebRTC
    @ClientEndpoint
    public class SignalingClient {

        private Session webSocketSession;
        private final Gson gson = new Gson();

        // Lorsqu'une connexion WebSocket est ouverte
        @OnOpen
        public void onOpen(Session session) {
            Log.d(TAG, "WebSocket opened");
            this.webSocketSession = session;
            sendRegisterMessage(); // Enregistrement du client

            runOnUiThread(() -> {
                // Création de la peer connection
                createPeerConnection();

                // Création de l'offre SDP
                MediaConstraints constraints = new MediaConstraints();
                constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "false"));
                constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));

                peerConnection.createOffer(new SimpleSdpObserver() {
                    @Override
                    public void onCreateSuccess(SessionDescription desc) {
                        peerConnection.setLocalDescription(new SimpleSdpObserver(), desc);

                        // Envoi de l'offre au serveur de signalisation
                        SignalMessage offerMsg = new SignalMessage();
                        offerMsg.setType("signal");
                        offerMsg.setClientId(CLIENT_ID);
                        offerMsg.setTargetId(TARGET_ID);
                        offerMsg.setSignalType("offer");
                        offerMsg.setStreamId("mystream4");
                        offerMsg.setData(desc.description);
                        sendMessage(offerMsg);
                    }

                    @Override
                    public void onCreateFailure(String s) {
                        Log.e(TAG, "Offer creation failed: " + s);
                    }
                }, constraints);
            });
        }

        // Réception d'un message WebSocket
        @OnMessage
        public void onMessage(String text) {
            SignalMessage msg = gson.fromJson(text, SignalMessage.class);

            // Traitement selon le type de signal reçu
            switch (msg.getSignalType()) {
                case "answer":
                    SessionDescription answer = new SessionDescription(
                            SessionDescription.Type.ANSWER, msg.getData());
                    peerConnection.setRemoteDescription(new SimpleSdpObserver(), answer);
                    break;

                case "candidate":
                    IceCandidate candidate = gson.fromJson(msg.getData(), IceCandidate.class);
                    peerConnection.addIceCandidate(candidate);
                    break;

                default:
                    Log.w(TAG, "Signal ignored: " + msg.getSignalType());
            }
        }

        // Fermeture du WebSocket
        @OnClose
        public void onClose(Session session) {
            Log.d(TAG, "WebSocket closed");
        }

        // Envoi du message d’enregistrement
        private void sendRegisterMessage() {
            SignalMessage reg = new SignalMessage();
            reg.setType("register");
            reg.setClientId(CLIENT_ID);
            sendMessage(reg);
        }

        // Envoi d’un message via WebSocket
        public void sendMessage(SignalMessage message) {
            if (webSocketSession != null && webSocketSession.isOpen()) {
                webSocketSession.getAsyncRemote().sendText(gson.toJson(message));
            } else {
                Log.e(TAG, "WebSocket is not open");
            }
        }

        // Création de la connexion pair-à-pair (WebRTC)
        private void createPeerConnection() {
            List<PeerConnection.IceServer> iceServers = new ArrayList<>();
            iceServers.add(PeerConnection.IceServer
                    .builder("stun:stun.l.google.com:19302").createIceServer());

            PeerConnection.RTCConfiguration config =
                    new PeerConnection.RTCConfiguration(iceServers);
            config.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

            peerConnection = peerConnectionFactory.createPeerConnection(
                    config, new PeerConnectionObserver());

            peerConnection.addTransceiver(MediaStreamTrack.MediaType.MEDIA_TYPE_VIDEO);
        }
    }

    // Observateur pour la connexion WebRTC
    private class PeerConnectionObserver implements PeerConnection.Observer {

        // Lorsqu’un ICE candidate est trouvé
        @Override
        public void onIceCandidate(IceCandidate c) {
            SignalMessage candMsg = new SignalMessage();
            candMsg.setType("signal");
            candMsg.setClientId(CLIENT_ID);
            candMsg.setTargetId(TARGET_ID);
            candMsg.setSignalType("candidate");
            candMsg.setStreamId("mystream4");
            candMsg.setData(new Gson().toJson(c));
            signalingClient.sendMessage(candMsg);
        }

        // Lorsqu’une piste (track) est ajoutée (vidéo reçue)
        @Override
        public void onAddTrack(RtpReceiver receiver, MediaStream[] streams) {
            if (receiver.track() instanceof VideoTrack) {
                VideoTrack videoTrack = (VideoTrack) receiver.track();
                videoTrack.addSink(surfaceViewRenderer); // Affichage sur l’écran
            }
        }

        // Méthodes inutilisées ici (obligatoires à implémenter)
        @Override public void onSignalingChange(PeerConnection.SignalingState s) {}
        @Override public void onIceConnectionChange(PeerConnection.IceConnectionState s) {}
        @Override public void onIceConnectionReceivingChange(boolean b) {}
        @Override public void onIceGatheringChange(PeerConnection.IceGatheringState s) {}
        @Override public void onIceCandidatesRemoved(IceCandidate[] c) {}
        @Override public void onAddStream(MediaStream s) {}
        @Override public void onRemoveStream(MediaStream s) {}
        @Override public void onDataChannel(DataChannel ch) {}
        @Override public void onRenegotiationNeeded() {}
        @Override public void onTrack(RtpTransceiver t) {}
    }

    // Observateur simple pour SDP
    private static class SimpleSdpObserver implements SdpObserver {
        @Override public void onCreateSuccess(SessionDescription s) {}
        @Override public void onSetSuccess() {}
        @Override public void onCreateFailure(String e) {
            Log.e(TAG, "SDP fail: " + e);
        }
        @Override public void onSetFailure(String e) {
            Log.e(TAG, "SDP set fail: " + e);
        }
    }

    // Méthode appelée lors d’un clic sur un bouton caméra
    public void onCamClick(View view) {
        Intent intent = null;
        int id = view.getId();

        // Redirection vers l’activité vidéo correspondante
        if (id == R.id.btn_cam1) {
            intent = new Intent(this, VideoSurveillanceActivity.class);
        } else if (id == R.id.btn_cam2) {
            intent = new Intent(this, VideoSurveillanceActivity2.class);
        } else if (id == R.id.btn_cam3) {
            intent = new Intent(this, VideoSurveillanceActivity3.class);
        } else if (id == R.id.btn_cam4) {
            intent = new Intent(this, VideoSurveillanceActivity4.class);
        }

        // Démarrage de la nouvelle activité
        if (intent != null) {
            startActivity(intent);
        }
    }


    // Redirection vers la page des notifications lors d'un clic sur l'icône notification
    public void openNotifications(View view) {
        Intent intent = new Intent(this, NotificationsActivity.class);
        startActivity(intent);
    }

}