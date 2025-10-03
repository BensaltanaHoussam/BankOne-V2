package ui.menu;

import enums.TypeTransaction;
import entities.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionMenu {

    public static void afficher(MenuContext ctx) {
        System.out.println("--- Transactions ---");
        System.out.println("1. Dépôt");
        System.out.println("2. Retrait");
        System.out.println("3. Virement");
        System.out.println("4. Liste compte");
        System.out.println("5. Liste client");
        System.out.println("6. Filtrer");
        System.out.println("7. Groupe par type");
        System.out.println("8. Groupe par période (jour/mois)");
        System.out.println("9. Total & moyenne");
        System.out.println("10. Suspicious");
        System.out.println("0. Retour");
        System.out.print("Choix: ");
        String c = ctx.sc.nextLine();
        switch (c) {
            case "1" -> depot(ctx);
            case "2" -> retrait(ctx);
            case "3" -> virement(ctx);
            case "4" -> listeCompte(ctx);
            case "5" -> listeClient(ctx);
            case "6" -> filtrer(ctx);
            case "7" -> groupeType(ctx);
            case "8" -> groupePeriode(ctx);
            case "9" -> totalMoyenne(ctx);
            case "10" -> suspicious(ctx);
            case "0" -> {}
            default -> System.out.println("Choix invalide");
        }
    }

    private static void depot(MenuContext ctx) {
        long id = lireLong(ctx, "ID compte: ");
        BigDecimal m = lireDecimal(ctx, "Montant: ");
        String lieu = lireTexte(ctx, "Lieu: ");
        Transaction t = ctx.transactionService.depot(id, m, lieu);
        System.out.println("OK idTx=" + t.id());
    }

    private static void retrait(MenuContext ctx) {
        long id = lireLong(ctx, "ID compte: ");
        BigDecimal m = lireDecimal(ctx, "Montant: ");
        String lieu = lireTexte(ctx, "Lieu: ");
        Transaction t = ctx.transactionService.retrait(id, m, lieu);
        System.out.println("OK idTx=" + t.id());
    }

    private static void virement(MenuContext ctx) {
        long src = lireLong(ctx, "ID source: ");
        long dst = lireLong(ctx, "ID destination: ");
        BigDecimal m = lireDecimal(ctx, "Montant: ");
        String lieu = lireTexte(ctx, "Lieu: ");
        Transaction t = ctx.transactionService.virement(src, dst, m, lieu);
        System.out.println("OK idTx=" + t.id());
    }

    private static void listeCompte(MenuContext ctx) {
        long id = lireLong(ctx, "ID compte: ");
        ctx.transactionService.transactionsCompte(id)
                .forEach(TransactionMenu::printTx);
    }

    private static void listeClient(MenuContext ctx) {
        long id = lireLong(ctx, "ID client: ");
        ctx.transactionService.transactionsClient(id)
                .forEach(TransactionMenu::printTx);
    }

    private static void filtrer(MenuContext ctx) {
        System.out.print("ID client (vide si non): ");
        String sc = ctx.sc.nextLine();
        Long idClient = sc.isBlank() ? null : Long.parseLong(sc);
        System.out.print("ID compte (vide si non): ");
        String sp = ctx.sc.nextLine();
        Long idCompte = sp.isBlank() ? null : Long.parseLong(sp);
        BigDecimal min = lireOptDecimal(ctx, "Min (vide): ");
        BigDecimal max = lireOptDecimal(ctx, "Max (vide): ");
        System.out.print("Type (DEPOT/RETRAIT/VIREMENT vide): ");
        String ts = ctx.sc.nextLine();
        TypeTransaction type = ts.isBlank() ? null : TypeTransaction.valueOf(ts.toUpperCase());
        System.out.print("Date début (YYYY-MM-DDTHH:MM ou vide): ");
        String ds = ctx.sc.nextLine();
        LocalDateTime d1 = ds.isBlank() ? null : LocalDateTime.parse(ds);
        System.out.print("Date fin (YYYY-MM-DDTHH:MM ou vide): ");
        String df = ctx.sc.nextLine();
        LocalDateTime d2 = df.isBlank() ? null : LocalDateTime.parse(df);
        System.out.print("Lieu (vide=ignore): ");
        String lieu = ctx.sc.nextLine();
        var list = ctx.transactionService.filtrerTransactions(idClient, idCompte, min, max, type, d1, d2, lieu.isBlank() ? null : lieu);
        list.forEach(TransactionMenu::printTx);
        if (list.isEmpty()) System.out.println("Aucun");
    }

    private static void groupeType(MenuContext ctx) {
        Long idClient = lireOptLong(ctx, "ID client (vide): ");
        Long idCompte = lireOptLong(ctx, "ID compte (vide): ");
        var map = ctx.transactionService.grouperParType(idClient, idCompte);
        map.forEach((k, v) -> System.out.println(k + " => " + v.size()));
    }

    private static void groupePeriode(MenuContext ctx) {
        Long idClient = lireOptLong(ctx, "ID client (vide): ");
        Long idCompte = lireOptLong(ctx, "ID compte (vide): ");
        System.out.print("Période (JOUR/MOIS): ");
        String p = ctx.sc.nextLine();
        var map = ctx.transactionService.grouperParPeriode(idClient, idCompte, p);
        map.forEach((k, v) -> System.out.println(k + " => " + v.size()));
    }

    private static void totalMoyenne(MenuContext ctx) {
        Long idClient = lireOptLong(ctx, "ID client (vide): ");
        Long idCompte = lireOptLong(ctx, "ID compte (vide): ");
        var total = ctx.transactionService.totalTransactions(idClient, idCompte);
        var moy = ctx.transactionService.moyenneTransactions(idClient, idCompte);
        System.out.println("Total=" + total + " Moyenne=" + moy);
    }

    private static void suspicious(MenuContext ctx) {
        var map = ctx.transactionService.detecterSuspicious(new BigDecimal("10000"), 5);
        map.forEach((k, v) -> System.out.println(k + " => " + v.size()));
    }

    private static void printTx(Transaction t) {
        System.out.println(t.id() + " | " + t.type() + " | " + t.montant() + " | " + t.date() + " | " + t.lieu());
    }

    private static long lireLong(MenuContext ctx, String label) {
        System.out.print(label);
        return Long.parseLong(ctx.sc.nextLine());
    }

    private static Long lireOptLong(MenuContext ctx, String label) {
        System.out.print(label);
        String s = ctx.sc.nextLine();
        return s.isBlank() ? null : Long.parseLong(s);
    }

    private static BigDecimal lireDecimal(MenuContext ctx, String label) {
        System.out.print(label);
        return new BigDecimal(ctx.sc.nextLine());
    }

    private static BigDecimal lireOptDecimal(MenuContext ctx, String label) {
        System.out.print(label);
        String s = ctx.sc.nextLine();
        return s.isBlank() ? null : new BigDecimal(s);
    }

    private static String lireTexte(MenuContext ctx, String label) {
        System.out.print(label);
        return ctx.sc.nextLine();
    }
}
