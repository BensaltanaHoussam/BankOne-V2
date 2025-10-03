package Strategy;

public class CarteBancaire implements Paiement { // rendu public
    public void payer(int montant) {
        System.out.println("Payé " + montant + " avec la carte bancaire.");
    }
}
