package ui.menu;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RapportMenu {

    public static void afficher(MenuContext ctx) {
        System.out.println("--- Rapports ---");
        System.out.println("1. Top 5 clients");
        System.out.println("2. Rapport mensuel");
        System.out.println("3. Suspicious");
        System.out.println("4. Comptes inactifs");
        System.out.println("0. Retour");
        System.out.print("Choix: ");
        String c = ctx.sc.nextLine();
        switch (c) {
            case "1" -> top(ctx);
            case "2" -> mensuel(ctx);
            case "3" -> suspicious(ctx);
            case "4" -> inactifs(ctx);
            case "0" -> {}
            default -> System.out.println("Choix invalide");
        }
    }

    private static void top(MenuContext ctx) {
        var list = ctx.rapportService.topClientsParSolde(5);
        int i = 1;
        for (var c : list) {
            System.out.println(i++ + ". " + c.nom() + " id=" + c.id());
        }
    }

    private static void mensuel(MenuContext ctx) {
        System.out.print("Année: ");
        int y = Integer.parseInt(ctx.sc.nextLine());
        System.out.print("Mois: ");
        int m = Integer.parseInt(ctx.sc.nextLine());
        var map = ctx.rapportService.rapportMensuel(y, m);
        map.forEach((t, st) ->
                System.out.println(t + " nombre=" + st.get("nombre") + " volume=" + st.get("volume")));
    }

    private static void suspicious(MenuContext ctx) {
        var map = ctx.rapportService.detecterSuspicious(new BigDecimal("10000"), 5);
        map.forEach((k, v) -> System.out.println(k + " => " + v.size()));
    }

    private static void inactifs(MenuContext ctx) {
        System.out.print("Date seuil (YYYY-MM-DD): ");
        LocalDate d = LocalDate.parse(ctx.sc.nextLine());
        var list = ctx.rapportService.comptesInactifsDepuis(d);
        System.out.println("Inactifs: " + list);
    }
}
