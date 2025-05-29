package	com.example.argosapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale de l'application Argosapp.
 * <p>
 * Cette classe contient le point d'entrée de l'application Spring Boot.
 * L'annotation {@link SpringBootApplication} active la configuration automatique, 
 * la recherche de composants et la configuration Spring.
 * L'annotation {@link EnableAsync} permet l'exécution de méthodes asynchrones 
 * via l'annotation {@code @Async}.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling

public class ArgosappApplication {

    /**
     * Méthode principale (point d'entrée) de l'application.
     *
     * @param args les arguments de ligne de commande passés lors du démarrage de l'application
     */
    public static void main(String[] args) {
        SpringApplication.run(ArgosappApplication.class, args);
    }

}
