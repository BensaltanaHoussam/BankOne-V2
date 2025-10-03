package Observer;

public class Voiture implements Observateur {
    private String nom;
    public Voiture(String nom) { this.nom = nom; }

    public void mettreAJour(String couleur) {
        if ("VERT".equals(couleur)) {
            System.out.println(nom + " démarre !");
        } else {
            System.out.println(nom + " attend...");
        }
    }
}