package com.example.argosapp;

public class CameraRequest {

    private String clientId;
    private String rtspUrl;
    private String streamName;

    public CameraRequest(String clientId, String rtspUrl, String streamName) {
        this.clientId = clientId;
        this.rtspUrl = rtspUrl;
        this.streamName = streamName;
    }

    // Getters et setters
    public String getCameraId() {
        return clientId;
    }

    public void setCameraId(String cameraId) {
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
