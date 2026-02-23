package org.example;

import java.sql.*;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Scanner;

public class Prestatairedb {
private static int conpteur=0;
private int idPrestat;
private String nom;
private String type;
private static ArrayList<Prestatairedb> listePrestat=new ArrayList<>();


public Prestatairedb(String nom, String type) {
 conpteur++;
 this.idPrestat =conpteur;
 this.nom = nom;
 this.type = type;

                   }




   //geter et setter

   public int getIdPrestat() {
       return idPrestat;
   }

   public String getNom() {
       return nom;
   }

   public String getType() {
       return type;
   }

   public void setIdPrestat(int idPrestat) {
       this.idPrestat = idPrestat;
   }

   public void setNom(String nom) {
       this.nom = nom;
   }

   public void setType(String type) {
       this.type = type;
   }

   public static ArrayList<Prestatairedb> getListePrestat() {
       return listePrestat;
   }




   // FONCTION



public static void  menuPrestataire(Scanner scanner){


       String choix;

       do {
           System.out.println("entrer votre choix \n 1:ajouter prestataire  \n 2:modifier prestataire  \n 3:lister prestataire \n 4:rechercher prestataire  \n 5:supprimer prestataire \n 0:retour ");

           choix = scanner.nextLine();
           scanner.nextLine();
           if (choix.equals("1")) {
               Prestatairedb.ajouterPrestataire(scanner);
           }
           else if (choix.equals("2")) {
               Prestatairedb.ModifierPrestataire(scanner);
           }
           else if (choix.equals("3")) {
               Prestatairedb.ListerPrestataireDB();
           }
           else if (choix.equals("4")) {
               Prestatairedb.RechercherPrestataireDB(scanner);
           }
           else if (choix.equals("5")) {
               Prestatairedb.SupprimerPrestataireDB(scanner);
           }
           else if (choix.equals("6")) {
               System.out.println("choix invalide");
           }
       }while(!choix.equals("0"));

   }



public static void ajouterPrestataire(Scanner scanner) {

    String nom;
    while (true) {
        System.out.print(" entrer Nom: ");
        nom = scanner.nextLine().trim();
        if (!nom.isEmpty()) break;
        System.out.println("Nom ne peut pas être vide. Réessaie.");
    }

    String type;
    while (true) {
        System.out.print("enter Type: ");
        type = scanner.nextLine().trim();
        if (!type.isEmpty()) break;
        System.out.println(" Type ne peut pas être vide. Réessaie.");
    }

    String sql = "INSERT INTO prestataire(nom, type) VALUES (?, ?)";

    try ( Connection con = DBConnection.createConnection();
         PreparedStatement ps = con.prepareStatement(sql)  ) {

        ps.setString(1, nom);
        ps.setString(2, type);

        int rows = ps.executeUpdate();
        System.out.println(" Ajouté. Rows: " + rows);

    } catch (Exception e) {
        e.printStackTrace();
    }
}




public static void ModifierPrestataire(Scanner sc) {
    try {
        System.out.print("ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("1=nom, 2=type: ");
        String choix = sc.nextLine();

        System.out.print("Nouvelle valeur: ");
        String val = sc.nextLine();

        String sql;
        if (choix.equals("1")) {
            sql = "UPDATE prestataire SET nom = ? WHERE id = ?";
        } else if (choix.equals("2")) {
            sql = "UPDATE prestataire SET type = ? WHERE id = ?";
        } else {
            System.out.println("Choix invalide");
            return;
        }

        try (Connection con = DBConnection.createConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, val);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            if(rows>0){
                System.out.println("Modifié");
            }else{
                System.out.println(" ID introuvable");
            }

        }

    } catch (Exception e) {
        System.out.println(" Erreur (vérifie les entrées)");
        e.printStackTrace();
    }
}





    public static void SupprimerPrestataireDB(Scanner scanner) {
        try {
            System.out.print("Entrer ID du prestataire à supprimer: ");
            int id = Integer.parseInt(scanner.nextLine());

            String sql = "DELETE FROM prestataire WHERE id = ?";

            try (Connection con = DBConnection.createConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();
               if(rows>0){
                   System.out.println(" Prestataire supprimé");
               }else {
                   System.out.println(" ID introuvable");
               }

            }

        } catch (Exception e) {
            System.out.println(" Erreur (vérifie l'ID)");
            e.printStackTrace();
        }
    }



    public static void ListerPrestataireDB() {
        String sql = "SELECT id, nom, type FROM prestataire";

        try (Connection con = DBConnection.createConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("=== Liste des prestataires ===");

            boolean empty = true;
            while (rs.next()) {
                empty = false;
                System.out.println(
                                 "id: " + rs.getInt("id") +
                                " | nom: " + rs.getString("nom") +
                                " | type: " + rs.getString("type")
                );
            }

            if (empty) {
                System.out.println("(aucun prestataire)");
            }

        } catch (Exception e) {
            System.out.println(" Erreur SQL pendant l'affichage");
            e.printStackTrace();
        }
    }



    public static void RechercherPrestataireDB(Scanner scanner) {
        try {
            System.out.print("Entrer votre ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            String sql = "SELECT id, nom, type FROM prestataire WHERE id = ?";

            try (Connection con = DBConnection.createConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(" Prestataire trouvé:");
                        System.out.println("ID: " + rs.getInt("id"));
                        System.out.println("Nom: " + rs.getString("nom"));
                        System.out.println("Type: " + rs.getString("type"));
                    } else {
                        System.out.println(" Prestataire introuvable");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(" Erreur (vérifie l'ID)");
            e.printStackTrace();
        }
    }







}
