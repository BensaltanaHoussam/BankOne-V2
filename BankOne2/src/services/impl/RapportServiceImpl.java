package services.impl;

import dao.ClientDAO;
import dao.CompteDAO;
import dao.TransactionDAO;
import entities.Client;
import entities.Compte;
import entities.Transaction;
import enums.TypeTransaction;
import services.RapportService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RapportServiceImpl implements RapportService {

    private final ClientDAO clientDAO;
    private final CompteDAO compteDAO;
    private final TransactionDAO transactionDAO;

    public RapportServiceImpl(ClientDAO clientDAO, CompteDAO compteDAO, TransactionDAO transactionDAO) {
        this.clientDAO = clientDAO;
        this.compteDAO = compteDAO;
        this.transactionDAO = transactionDAO;
    }

    @Override
    public List<Client> topClientsParSolde(int topN) {
        if (topN <= 0) topN = 5;

        Map<Long, BigDecimal> totalParClient = compteDAO.findAll().stream()
                .collect(Collectors.groupingBy(Compte::getIdClient,
                        Collectors.reducing(BigDecimal.ZERO, Compte::getSolde, BigDecimal::add)));

        Map<Long, Client> clients = clientDAO.findAll().stream()
                .collect(Collectors.toMap(Client::id, c -> c));

        return totalParClient.entrySet().stream()
                .map(e -> clients.get(e.getKey()))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing((Client c) -> totalParClient.get(c.id())).reversed())
                .limit(topN)
                .toList();
    }

    @Override
    public Map<TypeTransaction, Map<String, Object>> rapportMensuel(int year, int month) {
        LocalDateTime start = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime end = start.plusMonths(1);

        return Arrays.stream(TypeTransaction.values())
                .collect(Collectors.toMap(t -> t, t -> {
                    List<Transaction> txs = transactionDAO.findAll().stream()
                            .filter(tx -> tx.type() == t && !tx.date().isBefore(start) && tx.date().isBefore(end))
                            .toList();
                    return Map.of(
                            "nombre", (long) txs.size(),
                            "volume", txs.stream().map(Transaction::montant).reduce(BigDecimal.ZERO, BigDecimal::add)
                    );
                }, (a, b) -> a, () -> new EnumMap<>(TypeTransaction.class)));
    }

    @Override
    public Map<String, List<Transaction>> detecterSuspicious(BigDecimal seuil, int maxOpsParMinute) {
        seuil = (seuil == null) ? BigDecimal.valueOf(10000) : seuil;
        if (maxOpsParMinute <= 0) maxOpsParMinute = 5;

        List<Transaction> all = transactionDAO.findAll();
        Map<Long, List<Transaction>> parCompte = all.stream().collect(Collectors.groupingBy(Transaction::idCompteSource));

        // 1. Montants élevés
        BigDecimal finalSeuil = seuil;
        List<Transaction> montantsEleves = all.stream().filter(t -> t.montant().compareTo(finalSeuil) > 0).toList();

        // 2. Lieux inhabituels
        List<Transaction> lieuxInhabituels = parCompte.values().stream()
                .flatMap(txs -> {
                    if (txs.size() < 5) return Stream.empty();
                    String lieuMajoritaire = txs.stream()
                            .collect(Collectors.groupingBy(Transaction::lieu, Collectors.counting()))
                            .entrySet().stream().max(Map.Entry.comparingByValue())
                            .map(Map.Entry::getKey).orElse(null);
                    return txs.stream().filter(t -> lieuMajoritaire != null && !t.lieu().equalsIgnoreCase(lieuMajoritaire));
                }).distinct().toList();

        // 3. Rafales
        int finalMaxOpsParMinute = maxOpsParMinute;
        List<Transaction> rafales = parCompte.values().stream()
                .flatMap(txs -> {
                    List<Transaction> sorted = txs.stream().sorted(Comparator.comparing(Transaction::date)).toList();
                    List<Transaction> res = new ArrayList<>();
                    for (int l = 0, r = 0; r < sorted.size(); r++) {
                        while (l < r && secondsBetween(sorted.get(l).date(), sorted.get(r).date()) >= 60) l++;
                        if (r - l + 1 > finalMaxOpsParMinute) res.add(sorted.get(r));
                    }
                    return res.stream();
                }).distinct().toList();

        return Map.of("montantsEleves", montantsEleves,
                "lieuxInhabituels", lieuxInhabituels,
                "rafales", rafales);
    }

    @Override
    public List<Long> comptesInactifsDepuis(LocalDate dateSeuil) {
        LocalDateTime seuil = Objects.requireNonNull(dateSeuil).atStartOfDay();

        Map<Long, LocalDateTime> lastByCompte = transactionDAO.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::idCompteSource,
                        Collectors.mapping(Transaction::date,
                                Collectors.reducing(LocalDateTime.MIN, (a, b) -> a.isAfter(b) ? a : b))));

        return compteDAO.findAll().stream()
                .filter(c -> lastByCompte.getOrDefault(c.getId(), LocalDateTime.MIN).isBefore(seuil))
                .map(Compte::getId)
                .toList();
    }

    @Override
    public Map<String, Object> genererRapportGlobal(int year, int month, BigDecimal seuil, int maxOpsParMinute, LocalDate dateInactivite) {
        return Map.of(
                "topClients", topClientsParSolde(5),
                "rapportMensuel", rapportMensuel(year, month),
                "suspicious", detecterSuspicious(seuil, maxOpsParMinute),
                "comptesInactifs", comptesInactifsDepuis(dateInactivite)
        );
    }

    private long secondsBetween(LocalDateTime a, LocalDateTime b) {
        return Math.abs(b.toEpochSecond(ZoneOffset.UTC) - a.toEpochSecond(ZoneOffset.UTC));
    }
}
