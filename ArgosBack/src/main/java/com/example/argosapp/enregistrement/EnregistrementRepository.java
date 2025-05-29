    package com.example.argosapp.enregistrement;

    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;
    import java.util.List;

    @Repository
    public interface EnregistrementRepository extends JpaRepository<Enregistrement, Long> {

        // Méthode pour récupérer les enregistrements d'un utilisateur donné
        List<Enregistrement> findByUtilisateurId(String utilisateurId);
    }
