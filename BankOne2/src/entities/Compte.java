package entities;

import java.math.BigDecimal;

public abstract sealed class Compte permits CompteCourant , CompteEpargne {

    private Long id;
    private final String numero;
    private BigDecimal solde;
    private final Long idClient;

    protected Compte(Long id , String numero , BigDecimal solde , Long idClient){
        if (numero == null || numero.isBlank()){
            throw new IllegalArgumentException("Le numéro de compte est obligatoire");
        }
        if (solde == null || solde.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Le solde doit être positif");
        }
        if (idClient == null || idClient <= 0){
            throw new IllegalArgumentException("L'id du client est obligatoire");
        }
        this.id = id;
        this.numero = numero;
        this.solde = solde;
        this.idClient = idClient;
    }

    public Long getId() {
        return id;
    }

    public String getNumero() {
        return numero;
    }

    public BigDecimal getSolde() {
        return solde;
    }

    public BigDecimal setSolde(BigDecimal newSolde) {
        if (newSolde == null || newSolde.compareTo(BigDecimal.ZERO) < 0){
            throw new IllegalArgumentException("Le solde doit être positif");
        }
        this.solde = newSolde;
        return this.solde;
    }

    public Long getIdClient() {
        return idClient;
    }

    public void crediter(BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Montant crédit invalide");
        }
        solde = solde.add(montant);
    }

    public void debiter(BigDecimal montant) {
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Montant débit invalide");
        }
        // La règle de dépassement (découvert) est gérée dans les sous-classes si besoin.
        solde = solde.subtract(montant);
    }

    public abstract String typeCompte();

    @Override
    public String toString() {
        return typeCompte() + "{id=" + id + ", numero='" + numero + "', solde=" + solde + ", idClient=" + idClient + "}";
    }


    public void setNumero(String s) {
    }
}
