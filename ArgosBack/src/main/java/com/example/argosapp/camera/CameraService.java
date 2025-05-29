package com.example.argosapp.camera;
import java.util.List;


import org.springframework.stereotype.Service;

@Service
public class CameraService {

    private final CameraRepository cameraRepository;

    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    public Camera ajouterCamera(CameraRequest request) {
        Camera camera = new Camera(
            request.getClientId(),
            request.getRtspUrl(),
            request.getStreamName()
        );
        return cameraRepository.save(camera);
    }
    
    public List<String> getRtspUrlsByClientId(String clientId) {
        List<Camera> cameras = cameraRepository.findByClientId(clientId);
        return cameras.stream()
                      .map(Camera::getRtspUrl)
                      .toList();
    }
}
