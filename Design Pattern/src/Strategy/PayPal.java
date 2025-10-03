package Strategy;

public class PayPal implements Paiement { // rendu public
    public void payer(int montant) {
        System.out.println("Payé " + montant + " via PayPal.");
    }
}