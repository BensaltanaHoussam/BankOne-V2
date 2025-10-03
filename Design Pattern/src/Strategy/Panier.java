package Strategy;

public class Panier { // rendu public
    private Paiement strategie;

    public void setStrategie(Paiement strategie) {
        this.strategie = strategie;
    }

    public void checkout(int montant) {
        if (strategie != null) {
            strategie.payer(montant);
        } else {
            System.out.println("Aucune méthode de paiement sélectionnée !");
        }
    }
}
