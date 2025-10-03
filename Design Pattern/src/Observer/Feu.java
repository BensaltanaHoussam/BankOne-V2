package Observer;

import java.util.ArrayList;
import java.util.List;

public class Feu {
    private final List<Observateur> voitures = new ArrayList<>();

    public void abonner(Observateur v) { voitures.add(v); }

    public void changerCouleur(String couleur) {
        System.out.println("\nFeu = " + couleur);
        for (Observateur v : voitures) {
            v.mettreAJour(couleur);
        }
    }
}