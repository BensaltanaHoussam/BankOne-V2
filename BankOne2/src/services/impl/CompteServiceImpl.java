package services.impl;

import dao.ClientDAO;
import dao.CompteDAO;
import entities.Compte;
import entities.CompteCourant;
import entities.CompteEpargne;
import services.CompteService;

import java.math.BigDecimal;
import java.util.List;

public class CompteServiceImpl implements CompteService {

    private final CompteDAO compteDAO;
    private final ClientDAO clientDAO;

    public CompteServiceImpl(CompteDAO compteDAO, ClientDAO clientDAO) {
        this.compteDAO = compteDAO;
        this.clientDAO = clientDAO;
    }

    private void ensureClient(Long idClient) {
        clientDAO.findById(idClient).orElseThrow(() -> new RuntimeException("Client introuvable id=" + idClient));
    }

    @Override
    public CompteCourant createCompteCourant(String numero, BigDecimal soldeInit, Long clientId, BigDecimal decouvert) {
        ensureClient(clientId);
        if (decouvert == null || decouvert.signum() < 0) throw new IllegalArgumentException("Découvert >= 0");
        CompteCourant cc = new CompteCourant(null, numero, soldeInit, clientId, decouvert);
        return (CompteCourant) compteDAO.save(cc);
    }

    @Override
    public CompteEpargne createCompteEpargne(String numero, BigDecimal soldeInit, Long clientId, BigDecimal taux) {
        ensureClient(clientId);
        if (taux == null || taux.signum() < 0) throw new IllegalArgumentException("Taux >= 0");
        CompteEpargne ce = new CompteEpargne(null, numero, soldeInit, clientId, taux);
        return (CompteEpargne) compteDAO.save(ce);
    }

    @Override
    public Compte get(Long id) {
        return compteDAO.findById(id).orElseThrow(() -> new RuntimeException("Compte introuvable id=" + id));
    }

    @Override
    public Compte getByNumero(String numero) {
        return compteDAO.findByNumero(numero).orElseThrow(() -> new RuntimeException("Compte introuvable num=" + numero));
    }

    @Override
    public List<Compte> list() {
        return compteDAO.findAll();
    }

    @Override
    public List<Compte> listByClient(Long clientId) {
        ensureClient(clientId);
        return compteDAO.findByClient(clientId);
    }

    @Override
    public Compte crediter(Long compteId, BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) throw new IllegalArgumentException("Montant > 0");
        Compte c = get(compteId);
        c.crediter(montant);
        compteDAO.update(c);
        return c;
    }

    @Override
    public Compte debiter(Long compteId, BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) throw new IllegalArgumentException("Montant > 0");
        Compte c = get(compteId);
        BigDecimal soldeApres = c.getSolde().subtract(montant);
        if (c instanceof CompteCourant cc) {
            BigDecimal limite = cc.getDecouvertAutorise().negate();
            if (soldeApres.compareTo(limite) < 0) {
                throw new RuntimeException("Solde insuffisant (dépasse découvert)");
            }
        } else {
            if (soldeApres.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Solde insuffisant");
            }
        }
        c.debiter(montant);
        compteDAO.update(c);
        return c;
    }

    @Override
    public void virement(Long sourceId, Long destId, BigDecimal montant, String lieu) {
        if (sourceId.equals(destId)) throw new IllegalArgumentException("Comptes identiques");
        if (montant == null || montant.signum() <= 0) throw new IllegalArgumentException("Montant > 0");
        debiter(sourceId, montant);
        crediter(destId, montant);
        // Les transactions elles-mêmes seront enregistrées dans TransactionService (séparation de responsabilités)
    }
}
