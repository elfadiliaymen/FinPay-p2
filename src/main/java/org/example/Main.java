package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String choix;

        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1: Gestion Client");
            System.out.println("2: Gestion Prestataire");
            System.out.println("3: Gestion Facture / Statistique");
            System.out.println("4: Paiement");
            System.out.println("5: Recherche client (test)");
            System.out.println("6: Exporter un document");
            System.out.println("0: Quitter");
            System.out.print("Votre choix: ");

            choix = sc.nextLine();

            switch (choix) {
                case "1" -> Client.gestionClient();
                case "2" -> Prestatairedb.menuPrestataire(sc);
                case "3" -> MenuFS.menuPrincipal();
                case "4" -> Paiementdb.paimentDBservice(sc);
                case "5" -> Client.chercherClient(sc);
                case "6" -> exportFile.exporterDOC(sc);
                case "0" -> System.out.println("Au revoir !");
                default -> System.out.println("Choix invalide");
            }

        } while (!choix.equals("0"));

        sc.close();
    }
}