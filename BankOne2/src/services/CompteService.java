package services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import entities.Compte;

public interface CompteService {
    Compte creerCompteCourant(long idClient, BigDecimal solde, BigDecimal decouvert);
    Compte creerCompteEpargne(long idClient, BigDecimal solde, BigDecimal taux);
    Compte mettreAJourSolde(long idCompte, BigDecimal nouveauSolde);
    void mettreAJourDecouvert(long idCompte, BigDecimal nouveauDecouvert);
    void mettreAJourTaux(long idCompte, BigDecimal nouveauTaux);
    List<Compte> comptesDuClient(long idClient);
    Optional<Compte> compteSoldeMax(Long idClient);
    Optional<Compte> compteSoldeMin(Long idClient);
    Compte findById(long id);
}
