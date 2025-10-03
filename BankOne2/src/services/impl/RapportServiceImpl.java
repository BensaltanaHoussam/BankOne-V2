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
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
        Map<Long, BigDecimal> soldeParClient = new HashMap<>();
        for (Compte c : compteDAO.findAll()) {
            soldeParClient.merge(c.getIdClient(), c.getSolde(), BigDecimal::add);
        }
        Map<Long, Client> mapClients = clientDAO.findAll().stream()
                .collect(Collectors.toMap(Client::id, c -> c));
        return soldeParClient.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(e -> mapClients.get(e.getKey()))
                .filter(Objects::nonNull)
                .limit(topN)
                .toList();
    }

    @Override
    public Map<TypeTransaction, Map<String, Object>> rapportMensuel(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.atEndOfMonth().plusDays(1).atStartOfDay();
        Map<TypeTransaction, Map<String, Object>> res = new EnumMap<>(TypeTransaction.class);
        for (TypeTransaction t : TypeTransaction.values()) {
            Map<String, Object> st = new HashMap<>();
            st.put("nombre", 0L);
            st.put("volume", BigDecimal.ZERO);
            res.put(t, st);
        }
        for (Transaction t : transactionDAO.findAll()) {
            if (t.date().isBefore(start) || !t.date().isBefore(end)) continue;
            Map<String, Object> st = res.get(t.type());
            st.put("nombre", (Long) st.get("nombre") + 1);
            st.put("volume", ((BigDecimal) st.get("volume")).add(t.montant()));
        }
        return res;
    }

    @Override
    public Map<String, List<Transaction>> detecterSuspicious(BigDecimal seuilMontant, int maxOpsParMinute) {
        if (seuilMontant == null) seuilMontant = new BigDecimal("10000");
        if (maxOpsParMinute <= 0) maxOpsParMinute = 5;
        List<Transaction> all = transactionDAO.findAll();

        BigDecimal finalSeuilMontant = seuilMontant;
        List<Transaction> montantsEleves = all.stream()
                .filter(t -> t.montant().compareTo(finalSeuilMontant) > 0).toList();

        Map<Long, List<Transaction>> parCompte = all.stream()
                .collect(Collectors.groupingBy(Transaction::idCompteSource));

        List<Transaction> lieuxInhabituels = new ArrayList<>();
        for (List<Transaction> txs : parCompte.values()) {
            if (txs.size() < 5) continue;
            String major = txs.stream()
                    .collect(Collectors.groupingBy(Transaction::lieu, Collectors.counting()))
                    .entrySet().stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey).orElse(null);
            if (major == null) continue;
            txs.stream().filter(t -> !t.lieu().equalsIgnoreCase(major))
                    .distinct().forEach(lieuxInhabituels::add);
        }

        List<Transaction> rafales = new ArrayList<>();
        for (List<Transaction> txs : parCompte.values()) {
            List<Transaction> tri = txs.stream()
                    .sorted(Comparator.comparing(Transaction::date)).toList();
            int left = 0;
            for (int right = 0; right < tri.size(); right++) {
                while (left < right &&
                        ChronoUnit.SECONDS.between(tri.get(left).date(), tri.get(right).date()) >= 60) {
                    left++;
                }
                if (right - left + 1 > maxOpsParMinute) {
                    rafales.add(tri.get(right));
                }
            }
        }

        Map<String, List<Transaction>> res = new LinkedHashMap<>();
        res.put("montantsEleves", montantsEleves);
        res.put("lieuxInhabituels", lieuxInhabituels.stream().distinct().toList());
        res.put("rafales", rafales.stream().distinct().toList());
        return res;
    }

    @Override
    public List<Long> comptesInactifsDepuis(LocalDate dateSeuil) {
        if (dateSeuil == null) throw new IllegalArgumentException("date requise");
        LocalDateTime seuil = dateSeuil.atStartOfDay();
        Map<Long, LocalDateTime> last = new HashMap<>();
        for (Transaction t : transactionDAO.findAll()) {
            last.merge(t.idCompteSource(), t.date(),
                    (a, b) -> b.isAfter(a) ? b : a);
        }
        List<Long> inactifs = new ArrayList<>();
        for (Compte c : compteDAO.findAll()) {
            LocalDateTime l = last.get(c.getId());
            if (l == null || l.isBefore(seuil)) inactifs.add(c.getId());
        }
        return inactifs;
    }
}
