package com.example.argosapp.camera;


import jakarta.persistence.*;

@Entity
@Table(name = "cameras")
public class Camera {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String rtspUrl;
    private String streamName;

    public Camera() {
    }

    public Camera(String clientId, String rtspUrl, String streamName) {
        this.clientId = clientId;
        this.rtspUrl = rtspUrl;
        this.streamName = streamName;
    }

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getStreamName() {
        return streamName;
    }

    public void setStreamName(String streamName) {
        this.streamName = streamName;
    }
}
