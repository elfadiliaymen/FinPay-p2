import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class MenuFS {

    private static FactureService factureService = new FactureService();
    private static StatistiqueService statistiqueService = new StatistiqueService();

    public static void menuPrincipal() {

        Scanner scanner = new Scanner(System.in);
        String choix;

        do {
            System.out.println("\n===== MENU PRINCIPAL =====");
            System.out.println("1: Gestion Factures");
            System.out.println("2: Statistiques");
            System.out.println("0: Quitter");

            choix = scanner.nextLine();

            switch (choix) {
                case "1":
                    menuFacture(scanner);
                    break;
                case "2":
                    menuStatistique(scanner);
                    break;
            }

        } while (!choix.equals("0"));
    }

    private static void menuFacture(Scanner scanner) {

        String choix;

        do {
            System.out.println("\n===== MENU FACTURE =====");
            System.out.println("1: Ajouter facture");
            System.out.println("2: Modifier facture");
            System.out.println("3: Lister factures");
            System.out.println("4: Rechercher facture");
            System.out.println("0: Retour");

            choix = scanner.nextLine();

            switch (choix) {

                case "1":
                    System.out.println("Montant:");
                    double montant = Double.parseDouble(scanner.nextLine());

                    System.out.println("ID Client:");
                    int idClient = Integer.parseInt(scanner.nextLine());

                    System.out.println("ID Prestataire:");
                    int idPrestataire = Integer.parseInt(scanner.nextLine());

                    Client client = new Client();
                    client.setIdClient(idClient);

                    Prestatairedb prestataire = new Prestatairedb("", "");
                    prestataire.setIdPrestat(idPrestataire);

                    Facture facture = new Facture(
                            0,
                            LocalDate.now(),
                            montant,
                            "UNPAID",
                            client,
                            prestataire
                    );

                    factureService.creerFacture(facture);
                    break;

                case "2":
                    System.out.println("ID facture:");
                    int id = Integer.parseInt(scanner.nextLine());

                    System.out.println("Nouveau montant:");
                    double newMontant = Double.parseDouble(scanner.nextLine());

                    System.out.println("Nouveau status (UNPAID / PARTIAL / PAID):");
                    String status = scanner.nextLine();

                    factureService.modifierFacture(id, newMontant, status);
                    break;

                case "3":
                    List<Facture> factures = factureService.lister();
                    for (Facture f : factures) {
                        System.out.println("----------------------");
                        System.out.println("ID: " + f.getId());
                        System.out.println("Date: " + f.getDate());
                        System.out.println("Montant: " + f.getMontant());
                        System.out.println("Status: " + f.getStatus());
                    }
                    break;

                case "4":
                    System.out.println("ID facture:");
                    int searchId = Integer.parseInt(scanner.nextLine());

                    Facture f = factureService.findById(searchId);

                    if (f != null) {
                        System.out.println("Facture trouvée:");
                        System.out.println("ID: " + f.getId());
                        System.out.println("Date: " + f.getDate());
                        System.out.println("Montant: " + f.getMontant());
                        System.out.println("Status: " + f.getStatus());
                    } else {
                        System.out.println("Facture non trouvée");
                    }
                    break;
            }

        } while (!choix.equals("0"));
    }

    private static void menuStatistique(Scanner scanner) {
        String choix;

        do {
            System.out.println("\n===== MENU STATISTIQUE =====");
            System.out.println("1: Ajouter paiement avec commission");
            System.out.println("2: Afficher rapport");
            System.out.println("3: Enregistrer statistique");
            System.out.println("0: Retour");

            choix = scanner.nextLine();

            switch (choix) {

                case "1":
                    try {
                        System.out.println("ID Facture:");
                        int idFacture = Integer.parseInt(scanner.nextLine());

                        System.out.println("Montant du paiement:");
                        double montant = Double.parseDouble(scanner.nextLine());

                        System.out.println("Taux commission (ex: 2):");
                        double taux = Double.parseDouble(scanner.nextLine());

                        statistiqueService.ajouterPaiement(montant,idFacture);

                    } catch (Exception e) {
                        System.out.println("Valeur invalide");
                    }
                    break;

                case "2":
                    statistiqueService.afficherRapport();
                    break;

                case "3":
                    statistiqueService.saveStatistique();
                    break;

                case "0":
                    break;

                default:
                    System.out.println("Choix invalide");
            }

        } while (!choix.equals("0"));
    }

}
