package ui.menu;

import entities.Compte;

import java.math.BigDecimal;

public class CompteMenu {

    public static void afficher(MenuContext ctx) {
        System.out.println("--- Comptes ---");
        System.out.println("1. Créer courant");
        System.out.println("2. Créer épargne");
        System.out.println("3. MAJ solde");
        System.out.println("4. MAJ découvert");
        System.out.println("5. MAJ taux");
        System.out.println("6. Comptes d'un client");
        System.out.println("7. Compte solde max (client ou global)");
        System.out.println("8. Compte solde min (client ou global)");
        System.out.println("0. Retour");
        System.out.print("Choix: ");
        String c = ctx.sc.nextLine();
        switch (c) {
            case "1" -> creerCourant(ctx);
            case "2" -> creerEpargne(ctx);
            case "3" -> majSolde(ctx);
            case "4" -> majDecouvert(ctx);
            case "5" -> majTaux(ctx);
            case "6" -> comptesClient(ctx);
            case "7" -> max(ctx);
            case "8" -> min(ctx);
            case "0" -> {}
            default -> System.out.println("Choix invalide");
        }
    }

    private static void creerCourant(MenuContext ctx) {
        long idClient = lireLong(ctx, "ID client: ");
        BigDecimal solde = lireDecimal(ctx, "Solde initial: ");
        BigDecimal dec = lireDecimal(ctx, "Découvert autorisé: ");
        Compte c = ctx.compteService.creerCompteCourant(idClient, solde, dec);
        System.out.println("Créé id=" + c.getId());
    }

    private static void creerEpargne(MenuContext ctx) {
        long idClient = lireLong(ctx, "ID client: ");
        BigDecimal solde = lireDecimal(ctx, "Solde initial: ");
        BigDecimal taux = lireDecimal(ctx, "Taux: ");
        Compte c = ctx.compteService.creerCompteEpargne(idClient, solde, taux);
        System.out.println("Créé id=" + c.getId());
    }

    private static void majSolde(MenuContext ctx) {
        long id = lireLong(ctx, "ID compte: ");
        BigDecimal s = lireDecimal(ctx, "Nouveau solde: ");
        var c = ctx.compteService.mettreAJourSolde(id, s);
        System.out.println("OK solde=" + c.getSolde());
    }

    private static void majDecouvert(MenuContext ctx) {
        long id = lireLong(ctx, "ID compte: ");
        BigDecimal d = lireDecimal(ctx, "Nouveau découvert: ");
        ctx.compteService.mettreAJourDecouvert(id, d);
        System.out.println("OK");
    }

    private static void majTaux(MenuContext ctx) {
        long id = lireLong(ctx, "ID compte: ");
        BigDecimal t = lireDecimal(ctx, "Nouveau taux: ");
        ctx.compteService.mettreAJourTaux(id, t);
        System.out.println("OK");
    }

    private static void comptesClient(MenuContext ctx) {
        long id = lireLong(ctx, "ID client: ");
        var list = ctx.compteService.comptesDuClient(id);
        list.forEach(c -> System.out.println(c.getId() + " solde=" + c.getSolde()));
        if (list.isEmpty()) System.out.println("Aucun");
    }

    private static void max(MenuContext ctx) {
        Long idClient = lireOptionnelLong(ctx, "ID client (vide=global): ");
        var c = ctx.compteService.compteSoldeMax(idClient);
        System.out.println(c.map(x -> "Max id=" + x.getId() + " solde=" + x.getSolde()).orElse("Aucun"));
    }

    private static void min(MenuContext ctx) {
        Long idClient = lireOptionnelLong(ctx, "ID client (vide=global): ");
        var c = ctx.compteService.compteSoldeMin(idClient);
        System.out.println(c.map(x -> "Min id=" + x.getId() + " solde=" + x.getSolde()).orElse("Aucun"));
    }

    private static long lireLong(MenuContext ctx, String label) {
        System.out.print(label);
        return Long.parseLong(ctx.sc.nextLine());
    }

    private static Long lireOptionnelLong(MenuContext ctx, String label) {
        System.out.print(label);
        String s = ctx.sc.nextLine();
        return s.isBlank() ? null : Long.parseLong(s);
    }

    private static BigDecimal lireDecimal(MenuContext ctx, String label) {
        System.out.print(label);
        return new BigDecimal(ctx.sc.nextLine());
    }
}
