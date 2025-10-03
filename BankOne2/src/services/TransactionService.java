package services;

import entities.Transaction;
import enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TransactionService {
    Transaction depot(long idCompte, BigDecimal montant, String lieu);
    Transaction retrait(long idCompte, BigDecimal montant, String lieu);
    Transaction virement(long idSource, long idDestination, BigDecimal montant, String lieu);

    // Déjà utilisé par le menu
    List<Transaction> transactionsCompte(long idCompte);
    List<Transaction> transactionsClient(long idClient);

    List<Transaction> filtrerTransactions(Long idClient,
                                          Long idCompte,
                                          BigDecimal min,
                                          BigDecimal max,
                                          TypeTransaction type,
                                          LocalDateTime debut,
                                          LocalDateTime fin,
                                          String lieu);

    Map<TypeTransaction, List<Transaction>> grouperParType(Long idClient, Long idCompte);
    Map<String, List<Transaction>> grouperParPeriode(Long idClient, Long idCompte, String periode); // periode: JOUR ou MOIS

    BigDecimal totalTransactions(Long idClient, Long idCompte);
    BigDecimal moyenneTransactions(Long idClient, Long idCompte);

    Map<String, List<Transaction>> detecterSuspicious(BigDecimal seuilMontant, int maxParMinute);
}
