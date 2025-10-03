package services;

import entities.Client;
import entities.Transaction;
import enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RapportService {
    List<Client> topClientsParSolde(int topN);
    Map<TypeTransaction, Map<String, Object>> rapportMensuel(int year, int month);
    Map<String, List<Transaction>> detecterSuspicious(BigDecimal seuilMontant, int maxOpsParMinute);
    List<Long> comptesInactifsDepuis(LocalDate dateSeuil);
}
