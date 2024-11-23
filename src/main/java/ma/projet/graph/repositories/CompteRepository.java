package ma.projet.graph.repositories;

import ma.projet.graph.entities.Compte;
import ma.projet.graph.entities.TypeCompte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompteRepository extends JpaRepository<Compte, Long> {

    @Query("SELECT SUM(c.solde) FROM Compte c")
    double sumSoldes();

    // Custom query to find accounts by type
    List<Compte> findByType(TypeCompte type);
}
