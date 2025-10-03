package Decorateur;

// Composant concret
public class CafeSimple implements Cafe {
    @Override
    public String description() { return "Café simple"; }
    @Override
    public double prix() { return 2.0; }
}

