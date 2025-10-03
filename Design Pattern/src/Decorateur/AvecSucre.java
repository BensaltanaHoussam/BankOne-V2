package Decorateur;

// Décorateur concret : sucre
public class AvecSucre extends DecorateurCafe {
    public AvecSucre(Cafe cafe) { super(cafe); }
    @Override
    public String description() { return cafe.description() + " + sucre"; }
    @Override
    public double prix() { return cafe.prix() + 0.2; }
}

