package ui.menu;

import entities.Client;

import java.util.NoSuchElementException;

public class ClientMenu {

    public static void afficher(MenuContext ctx) {
        System.out.println("--- Clients ---");
        System.out.println("1. Ajouter");
        System.out.println("2. Modifier");
        System.out.println("3. Supprimer");
        System.out.println("4. Rechercher par nom");
        System.out.println("5. Infos client (solde total / nb comptes)");
        System.out.println("6. Lister tous");
        System.out.println("0. Retour");
        System.out.print("Choix: ");
        String c = ctx.sc.nextLine();
        try {
            switch (c) {
                case "1" -> ajouter(ctx);
                case "2" -> modifier(ctx);
                case "3" -> supprimer(ctx);
                case "4" -> rechercher(ctx);
                case "5" -> infos(ctx);
                case "6" -> lister(ctx);
                case "0" -> {}
                default -> System.out.println("Choix invalide");
            }
        } catch (Exception ex) {
            System.out.println("Erreur: " + ex.getMessage());
        }
    }

    private static void ajouter(MenuContext ctx) {
        System.out.print("Nom: ");
        String nom = ctx.sc.nextLine();
        System.out.print("Email: ");
        String email = ctx.sc.nextLine();
        Client c = ctx.clientService.ajouterClient(nom, email);
        System.out.println("Créé id=" + c.id());
    }

    private static void modifier(MenuContext ctx) {
        System.out.print("ID: ");
        long id = Long.parseLong(ctx.sc.nextLine());
        System.out.print("Nouveau nom (vide=inchangé): ");
        String n = ctx.sc.nextLine();
        System.out.print("Nouvel email (vide=inchangé): ");
        String e = ctx.sc.nextLine();
        Client c = ctx.clientService.modifierClient(id,
                n.isBlank() ? null : n,
                e.isBlank() ? null : e);
        System.out.println("Modifié: " + c.nom());
    }

    private static void supprimer(MenuContext ctx) {
        System.out.print("ID: ");
        long id = Long.parseLong(ctx.sc.nextLine());
        boolean ok = ctx.clientService.supprimerClient(id);
        System.out.println(ok ? "Supprimé" : "Introuvable");
    }

    private static void rechercher(MenuContext ctx) {
        System.out.print("Fragment nom: ");
        String f = ctx.sc.nextLine();
        var list = ctx.clientService.rechercherParNom(f);
        list.forEach(c -> System.out.println(c.id() + " - " + c.nom()));
        if (list.isEmpty()) System.out.println("Aucun");
    }

    private static void infos(MenuContext ctx) {
        System.out.print("ID client: ");
        long id = Long.parseLong(ctx.sc.nextLine());
        var map = ctx.clientService.infosClient(id);
        System.out.println(map);
    }

    private static void lister(MenuContext ctx) {
        var list = ctx.clientService.lister();
        list.forEach(c -> System.out.println(c.id() + " - " + c.nom() + " - " + c.email()));
    }
}
