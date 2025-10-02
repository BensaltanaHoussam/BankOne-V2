package services.impl;

import dao.CompteDAO;
import dao.TransactionDAO;
import entities.Compte;
import entities.CompteCourant;
import entities.Transaction;
import enums.TypeTransaction;
import services.TransactionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionDAO transactionDAO;
    private final CompteDAO compteDAO;

    public TransactionServiceImpl(TransactionDAO transactionDAO, CompteDAO compteDAO) {
        this.transactionDAO = transactionDAO;
        this.compteDAO = compteDAO;
    }

    private Compte getCompte(Long id) {
        return compteDAO.findById(id).orElseThrow(() -> new RuntimeException("Compte introuvable id=" + id));
    }

    private void checkRetraitPossible(Compte c, BigDecimal montant) {
        BigDecimal soldeApres = c.getSolde().subtract(montant);
        if (c instanceof CompteCourant cc) {
            BigDecimal limite = cc.getDecouvertAutorise().negate();
            if (soldeApres.compareTo(limite) < 0) throw new RuntimeException("Solde insuffisant");
        } else {
            if (soldeApres.compareTo(BigDecimal.ZERO) < 0) throw new RuntimeException("Solde insuffisant");
        }
    }

    private Transaction buildTx(Long id, BigDecimal montant, TypeTransaction type, String lieu, Long compteId) {
        return new Transaction(id, LocalDateTime.now(), montant, type, lieu, compteId);
    }

    @Override
    public Transaction verser(Long compteId, BigDecimal montant, String lieu) {
        if (montant == null || montant.signum() <= 0) throw new IllegalArgumentException("Montant > 0");
        Compte c = getCompte(compteId);
        c.crediter(montant);
        compteDAO.update(c);
        return transactionDAO.save(buildTx(null, montant, TypeTransaction.VERSEMENT, lieu, c.getId()));
    }

    @Override
    public Transaction retirer(Long compteId, BigDecimal montant, String lieu) {
        if (montant == null || montant.signum() <= 0) throw new IllegalArgumentException("Montant > 0");
        Compte c = getCompte(compteId);
        checkRetraitPossible(c, montant);
        c.debiter(montant);
        compteDAO.update(c);
        return transactionDAO.save(buildTx(null, montant, TypeTransaction.RETRAIT, lieu, c.getId()));
    }

    @Override
    public List<Transaction> virement(Long sourceId, Long destId, BigDecimal montant, String lieu) {
        if (sourceId.equals(destId)) throw new IllegalArgumentException("Comptes identiques");
        if (montant == null || montant.signum() <= 0) throw new IllegalArgumentException("Montant > 0");
        Compte src = getCompte(sourceId);
        Compte dst = getCompte(destId);
        checkRetraitPossible(src, montant);

        // Débit
        src.debiter(montant);
        compteDAO.update(src);
        Transaction tSrc = transactionDAO.save(buildTx(null, montant, TypeTransaction.VIREMENT, lieu, src.getId()));

        // Crédit
        dst.crediter(montant);
        compteDAO.update(dst);
        Transaction tDst = transactionDAO.save(buildTx(null, montant, TypeTransaction.VERSEMENT, lieu, dst.getId()));

        List<Transaction> result = new ArrayList<>();
        result.add(tSrc);
        result.add(tDst);
        return result;
    }

    @Override
    public Transaction get(Long id) {
        return transactionDAO.findById(id).orElseThrow(() -> new RuntimeException("Transaction introuvable id=" + id));
    }

    @Override
    public List<Transaction> listCompte(Long compteId) {
        getCompte(compteId);
        return transactionDAO.findByCompte(compteId);
    }

    @Override
    public List<Transaction> search(Long compteId, LocalDate debut, LocalDate fin, TypeTransaction type,
                                    BigDecimal min, BigDecimal max, String lieu) {
        return transactionDAO.search(compteId, debut, fin, type, min, max, lieu);
    }

    @Override
    public List<Transaction> listAll() {
        return transactionDAO.findAll();
    }

    @Override
    public boolean delete(Long id) {
        return transactionDAO.delete(id);
    }
}
