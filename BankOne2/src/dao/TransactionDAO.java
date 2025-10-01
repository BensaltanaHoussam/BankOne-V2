package dao;

import entities.Transaction;
import enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction save(Transaction t);
    Optional<Transaction> findById(long id);
    List<Transaction> findByCompte(long idCompte);
    List<Transaction> search(Long idCompte,
                             LocalDate dateDebut,
                             LocalDate dateFin,
                             TypeTransaction type,
                             BigDecimal minMontant,
                             BigDecimal maxMontant,
                             String lieu);
    List<Transaction> findAll();
    boolean delete(long id);
}
