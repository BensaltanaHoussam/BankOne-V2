package Decorateur;

// Décorateur abstrait
public abstract class DecorateurCafe implements Cafe {
    protected final Cafe cafe;
    protected DecorateurCafe(Cafe cafe) { this.cafe = cafe; }
}

