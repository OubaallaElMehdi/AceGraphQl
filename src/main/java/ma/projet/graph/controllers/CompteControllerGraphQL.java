package ma.projet.graph.controllers;

import ma.projet.graph.dto.TransactionRequest;
import ma.projet.graph.entities.Compte;
import lombok.AllArgsConstructor;
import ma.projet.graph.entities.Transaction;
import ma.projet.graph.entities.TypeCompte;
import ma.projet.graph.entities.TypeTransaction;
import ma.projet.graph.repositories.CompteRepository;
import ma.projet.graph.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;

@Controller
@AllArgsConstructor
public class CompteControllerGraphQL {

    private final CompteRepository compteRepository;

    @QueryMapping
    public List<Compte> allComptes() {
        return compteRepository.findAll();
    }

    @QueryMapping
    public Compte compteById(@Argument Long id) {
        return (Compte) compteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(String.format("Compte with ID %d not found", id)));
    }
    @QueryMapping
    public List<Compte> comptesByType(@Argument TypeCompte type) {
        return compteRepository.findByType(type);
    }


    @MutationMapping
    public Compte saveCompte(@Argument Compte compte) {
        return compteRepository.save(compte);
    }

    @QueryMapping
    public Map<String, Object> totalSolde() {
        long count = compteRepository.count();
        double sum = compteRepository.sumSoldes(); // Méthode personnalisée à créer dans le repository
        double average = count > 0 ? sum / count : 0;

        return Map.of(
                "count", count,
                "sum", sum,
                "average", average
        );
    }
    @MutationMapping
    public boolean deleteById(@Argument Long id) {
        if (!compteRepository.existsById(id)) {
            return false;
        }
        compteRepository.deleteById(id);
        return !compteRepository.existsById(id);
    }
    private TransactionRepository transactionRepository;

    @MutationMapping
    public Transaction addTransaction(@Argument TransactionRequest transactionRequest) {
        Compte compte = compteRepository.findById(transactionRequest.getCompteId())
                .orElseThrow(() -> new RuntimeException("Compte not found "));
        Transaction transaction = new Transaction();
        transaction.setMontant(transactionRequest.getMontant());
        transaction.setDateTransaction(transactionRequest.getDateTransaction());
        transaction.setType(transactionRequest.getType());
        transaction.setCompte(compte);
        transactionRepository.save(transaction);
        return transaction;

    }

    @QueryMapping
    public List<Transaction> transactionsByCompte(@Argument Long id) {
        Compte compte = compteRepository.findById(id).orElseThrow(() -> new RuntimeException("Compte not found "));
        return transactionRepository.findByCompte(compte);
    }

    @QueryMapping
    public Map<String, Object> transactionStats() {
        long count = transactionRepository.count();
        double sumDepots = transactionRepository.sumByType(TypeTransaction.DEPOT) != null
                ? transactionRepository.sumByType(TypeTransaction.DEPOT)
                : 0.0;
        double sumRetraits = transactionRepository.sumByType(TypeTransaction.RETRAIT) != null
                ? transactionRepository.sumByType(TypeTransaction.RETRAIT)
                : 0.0;

        return Map.of("count", count, "sumDepots", sumDepots, "sumRetraits", sumRetraits);
    }

}
