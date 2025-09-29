package entities;

import enums.TypeTransaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction ( Long id,
                            LocalDateTime date,
                            BigDecimal montant,
                            TypeTransaction type,
                            String lieu,
                            Long idCompteSource) {
    public Transaction{
        if (date == null) {
            throw new IllegalArgumentException("La date est obligatoire");
        }
        if (montant == null || montant.signum() <= 0) {
            throw new IllegalArgumentException("Le montant doit être > 0");
        }
        if (type == null) {
            throw new IllegalArgumentException("Le type de transaction est obligatoire");
        }
        if (lieu == null || lieu.isBlank()) {
            throw new IllegalArgumentException("Le lieu est obligatoire");
        }
        if (idCompteSource == null || idCompteSource <= 0) {
            throw new IllegalArgumentException("L'id du compte source est obligatoire et doit être > 0");
        }
    }

    public Transaction withId(Long newId) {
        return new Transaction(newId, this.date, this.montant, this.type, this.lieu, this.idCompteSource);
    }

    public boolean estCredit() {
        return this.type == TypeTransaction.VERSEMENT;
    }
    public boolean estDebit() {
        return this.type == TypeTransaction.RETRAIT || this.type == TypeTransaction.VIREMENT;
    }

}
