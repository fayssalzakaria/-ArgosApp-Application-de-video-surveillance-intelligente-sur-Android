package com.example.argosapp.camera;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CameraRepository extends JpaRepository<Camera, Long> {
    List<Camera> findByClientId(String clientId);
}
