package services.impl;

import dao.CompteDAO;
import dao.ClientDAO;
import entities.Compte;
import entities.CompteCourant;
import entities.CompteEpargne;
import services.CompteService;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CompteServiceImpl implements CompteService {

    private final CompteDAO compteDAO;
    private final ClientDAO clientDAO;

    public CompteServiceImpl(CompteDAO compteDAO, ClientDAO clientDAO) {
        this.compteDAO = compteDAO;
        this.clientDAO = clientDAO;
    }

    @Override
    public Compte creerCompteCourant(long idClient, BigDecimal solde, BigDecimal decouvert) {
        verifierClient(idClient);
        Compte c = new CompteCourant(null, genererNumero(), solde, idClient, decouvert);
        return compteDAO.save(c);
    }

    @Override
    public Compte creerCompteEpargne(long idClient, BigDecimal solde, BigDecimal taux) {
        verifierClient(idClient);
        Compte c = new CompteEpargne(null, genererNumero(), solde, idClient, taux);
        return compteDAO.save(c);
    }

    @Override
    public Compte mettreAJourSolde(long idCompte, BigDecimal nouveauSolde) {
        Compte c = chargerCompte(idCompte);
        c.setSolde(nouveauSolde);
        if (!compteDAO.update(c)) throw new RuntimeException("Échec update solde");
        return chargerCompte(idCompte);
    }

    @Override
    public void mettreAJourDecouvert(long idCompte, BigDecimal nouveauDecouvert) {
        Compte c = chargerCompte(idCompte);
        if (c instanceof entities.CompteCourant cc) {
            cc.setDecouvertAutorise(nouveauDecouvert);
            if (!compteDAO.update(cc)) throw new RuntimeException("Échec update découvert");
        } else {
            throw new IllegalArgumentException("Compte non COURANT");
        }
    }

    @Override
    public void mettreAJourTaux(long idCompte, BigDecimal nouveauTaux) {
        Compte c = chargerCompte(idCompte);
        if (c instanceof entities.CompteEpargne ce) {
            ce.setTauxInteret(nouveauTaux);
            if (!compteDAO.update(ce)) throw new RuntimeException("Échec update taux");
        } else {
            throw new IllegalArgumentException("Compte non EPARGNE");
        }
    }

    @Override
    public List<Compte> comptesDuClient(long idClient) {
        return compteDAO.findByClient(idClient);
    }

    @Override
    public Optional<Compte> compteSoldeMax(Long idClient) {
        return (idClient == null ? compteDAO.findAll() : compteDAO.findByClient(idClient))
                .stream().max(Comparator.comparing(Compte::getSolde));
    }

    @Override
    public Optional<Compte> compteSoldeMin(Long idClient) {
        return (idClient == null ? compteDAO.findAll() : compteDAO.findByClient(idClient))
                .stream().min(Comparator.comparing(Compte::getSolde));
    }

    @Override
    public Compte findById(long id) {
        return chargerCompte(id);
    }

    private Compte chargerCompte(long id) {
        return compteDAO.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compte introuvable id=" + id));
    }

    private void verifierClient(long idClient) {
        clientDAO.findById(idClient)
                .orElseThrow(() -> new NoSuchElementException("Client introuvable id=" + idClient));
    }

    private String genererNumero() {
        return "C-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
