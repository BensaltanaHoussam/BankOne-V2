package services;

import entities.Compte;
import entities.CompteCourant;
import entities.CompteEpargne;

import java.math.BigDecimal;
import java.util.List;

public interface CompteService {
    CompteCourant createCompteCourant(String numero, BigDecimal soldeInit, Long clientId, BigDecimal decouvert);
    CompteEpargne createCompteEpargne(String numero, BigDecimal soldeInit, Long clientId, BigDecimal taux);
    Compte get(Long id);
    Compte getByNumero(String numero);
    List<Compte> list();
    List<Compte> listByClient(Long clientId);
    Compte crediter(Long compteId, BigDecimal montant);
    Compte debiter(Long compteId, BigDecimal montant);
    void virement(Long sourceId, Long destId, BigDecimal montant, String lieu);
}
