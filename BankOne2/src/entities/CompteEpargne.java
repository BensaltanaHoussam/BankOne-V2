package entities;

import java.math.BigDecimal;

public final class CompteEpargne extends Compte {
    private final BigDecimal tauxInteret;

    public CompteEpargne(Long id ,String numero , BigDecimal solde , Long idClient ,BigDecimal tauxInteret){
        super(id, numero,solde,idClient);
        if (tauxInteret == null || tauxInteret.signum() < 0) {
            throw new IllegalArgumentException("Taux d'intérêt doit être >= 0");
        }
        this.tauxInteret = tauxInteret;
    }

    public BigDecimal getTauxInteret() {
        return tauxInteret;
    }

    public void appliquerInterets() {
        BigDecimal gain = getSolde().multiply(tauxInteret);
        setSolde(getSolde().add(gain));
    }

    public String typeCompte() {
        return "EPARGNE";
    }
}
