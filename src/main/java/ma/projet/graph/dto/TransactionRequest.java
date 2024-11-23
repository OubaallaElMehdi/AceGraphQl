package ma.projet.graph.dto;

import lombok.Data;
import ma.projet.graph.entities.TypeTransaction;

import java.util.Date;

@Data
public class TransactionRequest {

    private Long compteId;
    private double montant;
    private TypeTransaction type;
    private Date dateTransaction;



}