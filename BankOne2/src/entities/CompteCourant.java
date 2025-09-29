package entities;

import java.math.BigDecimal;

public final class CompteCourant extends Compte {

    private final BigDecimal decouvertAutorise;

    public CompteCourant(Long id ,String numero , BigDecimal solde , Long idClient ,BigDecimal decouvertAutorise){
        super(id, numero,solde,idClient);
        if (decouvertAutorise == null || decouvertAutorise.signum() < 0) {
            throw new IllegalArgumentException("Découvert autorisé doit être >= 0");
        }
        this.decouvertAutorise = decouvertAutorise;
    }

    public void debiter(BigDecimal montant){
        if(montant == null || montant.signum()<=0){
            throw new IllegalArgumentException("Montant débit invalide");
        }
        BigDecimal nouveau = getSolde().subtract(montant);

        if (nouveau.compareTo(decouvertAutorise.negate()) < 0) {
            throw new IllegalArgumentException("Débit dépasse le découvert autorisé");
        }
        setSolde(nouveau);
    }

    @Override
    public String typeCompte() {
        return "COURANT";
    }
}
