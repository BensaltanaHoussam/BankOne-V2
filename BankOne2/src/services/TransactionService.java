package services;

import entities.Transaction;
import enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {
    Transaction verser(Long compteId, BigDecimal montant, String lieu);
    Transaction retirer(Long compteId, BigDecimal montant, String lieu);
    List<Transaction> virement(Long sourceId, Long destId, BigDecimal montant, String lieu);
    Transaction get(Long id);
    List<Transaction> listCompte(Long compteId);
    List<Transaction> search(Long compteId,
                             LocalDate debut,
                             LocalDate fin,
                             TypeTransaction type,
                             BigDecimal min,
                             BigDecimal max,
                             String lieu);
    List<Transaction> listAll();
    boolean delete(Long id);
}
