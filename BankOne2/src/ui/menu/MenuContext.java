package ui.menu;

import services.*;

import java.util.Scanner;

public class MenuContext {
    public final Scanner sc;
    public final ClientService clientService;
    public final CompteService compteService;
    public final TransactionService transactionService;
    public final RapportService rapportService;

    public MenuContext(Scanner sc,
                       ClientService clientService,
                       CompteService compteService,
                       TransactionService transactionService,
                       RapportService rapportService) {
        this.sc = sc;
        this.clientService = clientService;
        this.compteService = compteService;
        this.transactionService = transactionService;
        this.rapportService = rapportService;
    }
}
