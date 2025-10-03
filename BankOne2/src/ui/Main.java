package ui;

import dao.*;
import services.*;
import services.impl.*;
import ui.menu.*;
import utils.DBConnection;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        ClientDAO clientDAO = new ClientDAOImpl();
        CompteDAO compteDAO = new CompteDAOImpl();
        TransactionDAO transactionDAO = new TransactionDAOImpl();

        ClientService clientService = new ClientServiceImpl(clientDAO, compteDAO);
        CompteService compteService = new CompteServiceImpl(compteDAO, clientDAO);
        TransactionService transactionService = new TransactionServiceImpl(transactionDAO, compteDAO);
        RapportService rapportService = new RapportServiceImpl(clientDAO, compteDAO, transactionDAO);
        Scanner sc = new Scanner(System.in);
        MenuContext ctx = new MenuContext(sc, clientService, compteService, transactionService, rapportService);

        boolean run = true;
        while (run) {
            System.out.println("=== MENU PRINCIPAL ===");
            System.out.println("1. Clients");
            System.out.println("2. Comptes");
            System.out.println("3. Transactions");
            System.out.println("4. Rapports");
            System.out.println("0. Quitter");
            System.out.print("Choix: ");
            String choix = sc.nextLine();
            switch (choix) {
                case "1" -> ClientMenu.afficher(ctx);
                case "2" -> CompteMenu.afficher(ctx);
                case "3" -> TransactionMenu.afficher(ctx);
                case "4" -> RapportMenu.afficher(ctx);
                case "0" -> run = false;
                default -> System.out.println("Choix invalide");
            }
            System.out.println();
        }
        System.out.println("Fin.");
        sc.close();
    }



}
