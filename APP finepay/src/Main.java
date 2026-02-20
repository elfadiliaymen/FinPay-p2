import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choix;
        do{
            System.out.println("entrer voutre choix 1: gestion client 2: gestion prestatire 3:gestion facture/statistique 4:payment \n  5:fonction de test  \n E:exporter un document ");
            choix=sc.nextLine();
            if(choix.equals("1")){
                Client.gestionClient();
            }
            else if(choix.equals("2")){
                Prestatairedb.menuPrestataire(sc);
            }
            else if(choix.equals("3")){
                MenuFS.menuPrincipal();
            }
            else if(choix.equals("4")){
              Paiementdb.paimentDBservice(sc);
            }
            else if(choix.equals("E")){
                exportFile.exporterDOC(sc);
            } else if (choix.equals("5")) {
                Client.chercherClient(sc);
            } else {
                System.out.println("choix invalide");
            }

        }while(!choix.equals("0"));










    }
}
