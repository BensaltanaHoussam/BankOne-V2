package services.impl;

import dao.CompteDAO;
import dao.TransactionDAO;
import entities.Compte;
import entities.Transaction;
import enums.TypeTransaction;
import services.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class TransactionServiceImpl implements TransactionService {

    private static final int MOYENNE_SCALE = 2;

    private final TransactionDAO transactionDAO;
    private final CompteDAO compteDAO;

    public TransactionServiceImpl(TransactionDAO transactionDAO, CompteDAO compteDAO) {
        this.transactionDAO = transactionDAO;
        this.compteDAO = compteDAO;
    }

    @Override
    public Transaction depot(long idCompte, BigDecimal montant, String lieu) {
        Compte c = chargerCompte(idCompte);
        validerMontant(montant);
        c.setSolde(c.getSolde().add(montant));
        if (!compteDAO.update(c)) throw new RuntimeException("Échec update solde");
        return transactionDAO.save(new Transaction(null, idCompte, null, montant, TypeTransaction.DEPOT, LocalDateTime.now(), lieu));
    }

    @Override
    public Transaction retrait(long idCompte, BigDecimal montant, String lieu) {
        Compte c = chargerCompte(idCompte);
        validerMontant(montant);
        if (c.getSolde().compareTo(montant) < 0) throw new IllegalArgumentException("Solde insuffisant");
        c.setSolde(c.getSolde().subtract(montant));
        if (!compteDAO.update(c)) throw new RuntimeException("Échec update solde");
        return transactionDAO.save(new Transaction(null, idCompte, null, montant, TypeTransaction.RETRAIT, LocalDateTime.now(), lieu));
    }

    @Override
    public Transaction virement(long idSource, long idDestination, BigDecimal montant, String lieu) {
        if (idSource == idDestination) throw new IllegalArgumentException("Comptes identiques");
        Compte src = chargerCompte(idSource);
        Compte dst = chargerCompte(idDestination);
        validerMontant(montant);
        if (src.getSolde().compareTo(montant) < 0) throw new IllegalArgumentException("Solde insuffisant");
        src.setSolde(src.getSolde().subtract(montant));
        dst.setSolde(dst.getSolde().add(montant));
        if (!compteDAO.update(src) || !compteDAO.update(dst)) throw new RuntimeException("Échec update comptes");
        return transactionDAO.save(new Transaction(null, idSource, idDestination, montant, TypeTransaction.VIREMENT, LocalDateTime.now(), lieu));
    }

    @Override
    public List<Transaction> transactionsCompte(long idCompte) {
        return transactionDAO.findByCompte(idCompte);
    }

    @Override
    public List<Transaction> transactionsClient(long idClient) {
        return compteDAO.findByClient(idClient).stream()
                .flatMap(c -> transactionDAO.findByCompte(c.getId()).stream())
                .sorted(Comparator.comparing(Transaction::date).reversed())
                .toList();
    }

    @Override
    public List<Transaction> filtrerTransactions(Long idClient,
                                                 Long idCompte,
                                                 BigDecimal min,
                                                 BigDecimal max,
                                                 TypeTransaction type,
                                                 LocalDateTime debut,
                                                 LocalDateTime fin,
                                                 String lieu) {
        return baseListe(idClient, idCompte).stream()
                .filter(t -> min == null || t.montant().compareTo(min) >= 0)
                .filter(t -> max == null || t.montant().compareTo(max) <= 0)
                .filter(t -> type == null || t.type() == type)
                .filter(t -> debut == null || !t.date().isBefore(debut))
                .filter(t -> fin == null || !t.date().isAfter(fin))
                .filter(t -> lieu == null || t.lieu().equalsIgnoreCase(lieu))
                .sorted(Comparator.comparing(Transaction::date).reversed())
                .toList();
    }

    @Override
    public Map<TypeTransaction, List<Transaction>> grouperParType(Long idClient, Long idCompte) {
        return baseListe(idClient, idCompte).stream()
                .collect(Collectors.groupingBy(Transaction::type, LinkedHashMap::new, Collectors.toList()));
    }

    @Override
    public Map<String, List<Transaction>> grouperParPeriode(Long idClient, Long idCompte, String periode) {
        boolean parJour = "JOUR".equalsIgnoreCase(periode);
        return baseListe(idClient, idCompte).stream()
                .collect(Collectors.groupingBy(t -> {
                    LocalDateTime d = t.date();
                    return parJour ? d.toLocalDate().toString()
                            : d.getYear() + "-" + String.format("%02d", d.getMonthValue());
                }, LinkedHashMap::new, Collectors.toList()));
    }

    @Override
    public BigDecimal totalTransactions(Long idClient, Long idCompte) {
        return baseListe(idClient, idCompte).stream()
                .map(Transaction::montant)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal moyenneTransactions(Long idClient, Long idCompte) {
        List<Transaction> list = baseListe(idClient, idCompte);
        if (list.isEmpty()) return BigDecimal.ZERO;
        BigDecimal total = totalTransactions(idClient, idCompte);
        return total.divide(BigDecimal.valueOf(list.size()), MOYENNE_SCALE, RoundingMode.HALF_UP);
    }

    @Override
    public Map<String, List<Transaction>> detecterSuspicious(BigDecimal seuilMontant, int maxParMinute) {
        final BigDecimal seuil = (seuilMontant == null ? BigDecimal.ZERO : seuilMontant);
        List<Transaction> all = transactionDAO.findAll();
        Map<String, List<Transaction>> res = new LinkedHashMap<>();

        List<Transaction> montant = all.stream()
                .filter(t -> t.montant().compareTo(seuil) > 0)
                .toList();
        if (!montant.isEmpty()) res.put("MONTANT", montant);

        Map<Long, List<Transaction>> parCompte = new HashMap<>();
        for (Transaction t : all) {
            Long key = t.idCompteSource() != null ? t.idCompteSource()
                    : (t.idCompteDestination() != null ? t.idCompteDestination() : -1L);
            parCompte.computeIfAbsent(key, k -> new ArrayList<>()).add(t);
        }

        List<Transaction> freq = new ArrayList<>();
        for (List<Transaction> l : parCompte.values()) {
            l.sort(Comparator.comparing(Transaction::date));
            for (int i = 0; i < l.size(); i++) {
                LocalDateTime base = l.get(i).date().truncatedTo(ChronoUnit.MINUTES);
                int count = 1;
                List<Transaction> window = new ArrayList<>();
                window.add(l.get(i));
                for (int j = i + 1; j < l.size(); j++) {
                    if (l.get(j).date().truncatedTo(ChronoUnit.MINUTES).equals(base)) {
                        count++;
                        window.add(l.get(j));
                    } else break;
                }
                if (count > maxParMinute) {
                    for (Transaction t : window) {
                        if (!freq.contains(t)) freq.add(t);
                    }
                }
            }
        }
        if (!freq.isEmpty()) res.put("FREQUENCE", freq);
        return res;
    }

    private List<Transaction> baseListe(Long idClient, Long idCompte) {
        if (idCompte != null) return transactionDAO.findByCompte(idCompte);
        if (idClient != null) return transactionsClient(idClient);
        return transactionDAO.findAll();
    }

    private Compte chargerCompte(long id) {
        return compteDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable"));
    }

    private void validerMontant(BigDecimal m) {
        if (m == null || m.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Montant invalide");
    }
}
