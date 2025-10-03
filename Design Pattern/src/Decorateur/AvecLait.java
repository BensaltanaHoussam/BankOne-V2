package Decorateur;

// Décorateur concret : lait
public class AvecLait extends DecorateurCafe {
    public AvecLait(Cafe cafe) { super(cafe); }
    @Override
    public String description() { return cafe.description() + " + lait"; }
    @Override
    public double prix() { return cafe.prix() + 0.5; }
}

