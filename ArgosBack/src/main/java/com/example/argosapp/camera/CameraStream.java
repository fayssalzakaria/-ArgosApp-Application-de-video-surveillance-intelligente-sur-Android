package com.example.argosapp.camera;

import jakarta.persistence.*;

/**
 * Entité JPA représentant une caméra et ses informations de streaming.
 * 
 * Chaque instance correspond à une ligne dans la table "camera" de la base de données.
 */
@Entity
@Table(name = "camera")
public class CameraStream {

    /**
     * Identifiant unique de la caméra (clé primaire auto-générée).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Identifiant logique de la caméra, défini par l'utilisateur.
     * Doit être unique et non nul.
     */
    @Column(name = "camera_id", unique = true, nullable = false)
    private String cameraId;

    /**
     * URL du flux RTSP de la caméra.
     * Utilisé pour accéder au flux vidéo brut.
     */
    @Column(name = "rtsp_url", nullable = false)
    private String rtspUrl;

    /**
     * Nom du flux utilisé pour identifier la caméra dans le serveur de streaming (ex : WebRTC).
     */
    @Column(name = "stream_name", nullable = false)
    private String streamName;

    /**
     * Constructeur par défaut requis par JPA.
     */
    public CameraStream() {}

    /**
     * Constructeur avec paramètres pour créer une instance complète de {@link CameraStream}.
     *
     * @param cameraId identifiant logique de la caméra
     * @param rtspUrl URL du flux RTSP de la caméra
     * @param streamName nom du flux utilisé pour le streaming
     */
    public CameraStream(String cameraId, String rtspUrl, String streamName) {
        this.cameraId = cameraId;
        this.rtspUrl = rtspUrl;
        this.streamName = streamName;
    }

    // Getters et Setters

    /**
     * @return l'identifiant unique (base de données) de la caméra
     */
    public Long getId() {
        return id;
    }

    /**
     * @return l'identifiant logique de la caméra
     */
    public String getCameraId() {
        return cameraId;
    }

    /**
     * Définit l'identifiant logique de la caméra.
     *
     * @param cameraId identifiant unique défini par l'utilisateur
     */
    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    /**
     * @return l'URL RTSP du flux vidéo
     */
    public String getRtspUrl() {
        return rtspUrl;
    }

    /**
     * Définit l'URL RTSP du flux de la caméra.
     *
     * @param rtspUrl URL complète du flux RTSP
     */
    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    /**
     * @return le nom du flux utilisé pour la diffusion
     */
    public String getStreamName() {
        return streamName;
    }

    /**
     * Définit le nom du flux de streaming.
     *
     * @param streamName identifiant du flux utilisé par le serveur de streaming
     */
    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
}
