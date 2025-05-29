package com.example.argosapp.camera;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cameras")
@CrossOrigin(origins = "*") // autoriser toutes les origines (pour Android)
public class CameraController {

    @Autowired
    private CameraService cameraService;

    @PostMapping("/ajouter")
    public void ajouterCamera(@RequestBody CameraRequest camera) {
        cameraService.ajouterCamera(camera);
    }
        @GetMapping("/rtsp")
    public List<String> getRtspUrls(@RequestParam String clientId) {
        return cameraService.getRtspUrlsByClientId(clientId);
    }


}
