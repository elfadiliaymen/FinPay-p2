import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String choix;
        do{
          
            System.out.println("entrer voutre choix 1: gestion client 2: gestion prestatire 3:gestion facture/statistique 4:payment \n  5:fonction de test  \n 6:exporter un document \n 7: genererRapportMensuel ");

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

            else if(choix.equals("6")){
                exportFile.exporterDOC(sc);
            } else if (choix.equals("7")) {
                exportFile.genererRapportMensuel()

            } else if (choix.equals("5")) {
                Client.chercherClient(sc);
            } else {
                System.out.println("choix invalide");
            }

        }while(!choix.equals("0"));







    }
}
