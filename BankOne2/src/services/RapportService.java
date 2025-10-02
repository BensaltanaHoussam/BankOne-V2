package services;

import entities.Client;
import entities.Transaction;
import enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface RapportService {

    // Top N clients par somme des soldes de leurs comptes
    List<Client> topClientsParSolde(int topN);

    // Statistiques mensuelles: pour chaque type -> { "nombre": Long, "volume": BigDecimal }
    Map<TypeTransaction, Map<String, Object>> rapportMensuel(int year, int month);

    // Détection d'activités suspectes:
    // "montantsEleves", "lieuxInhabituels", "rafales"
    Map<String, List<Transaction>> detecterSuspicious(BigDecimal seuilMontant, int maxOpsParMinute);

    // Liste des IDs de comptes inactifs depuis la date
    List<Long> comptesInactifsDepuis(LocalDate dateSeuil);

    // Rapport global agrégé
    Map<String, Object> genererRapportGlobal(int year, int month,
                                             BigDecimal seuilMontant,
                                             int maxOpsParMinute,
                                             LocalDate dateInactivite);
}
