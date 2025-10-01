package dao;


import entities.Transaction;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionDAO {
    Transaction save(Transaction transaction);
    Optional<Transaction> findById(long id);
    List<Transaction> findByCompte(long compteId);
    List<Transaction> search(Long compteId,
                             LocalDate dateDebut,
                             LocalDate dateFin,
                             String type,
                             Double minMontant,
                             Double maxMontant,
                             String lieu);
    List<Transaction> findAll();
    boolean delete(long id);
}
