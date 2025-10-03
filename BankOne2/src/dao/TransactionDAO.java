package dao;

import entities.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction save(Transaction t);
    List<Transaction> findByCompte(long idCompte);              // par compte (source ou destination)
    List<Transaction> findAll();                                // liste complète
    Optional<Transaction> findById(Long id);
    boolean delete(Long id);
}