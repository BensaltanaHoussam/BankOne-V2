package entities;

import enums.TypeTransaction;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record Transaction(Long id,
                          Long idCompteSource,
                          Long idCompteDestination,
                          BigDecimal montant,
                          TypeTransaction type,
                          LocalDateTime date,
                          String lieu) {
    public Transaction withId(Long newId) {
        return new Transaction(newId, idCompteSource, idCompteDestination, montant, type, date, lieu);
    }
}
