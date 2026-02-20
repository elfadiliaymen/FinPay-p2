import java.util.*;
import java.sql.*;


public class Client {
    private int id;
    private String nom;
    private String telephone;
    private String email;

    final private static ArrayList<Client> listeClients = new ArrayList<>();

    public Client() {}

    public Client(int idClient, String nom, String telephone, String email) {
        this.id= idClient;
        this.nom = nom;
        this.telephone = telephone;
        this.email = email;
    }

    public int getIdClient() {
        return id;
    }

    public void setIdClient(int idClient) {
        this.id = idClient;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }






    // ================= FONCTIONS =================

    public static void  gestionClient(){
        Scanner sc = new Scanner(System.in);
        int choix;
        do {
            System.out.println("\n===== MENU CLIENT =====");
            System.out.println("1. Ajouter un client");
            System.out.println("2. Modifier un client");
            System.out.println("3. Supprimer un client");
            System.out.println("4. Lister les clients");
            System.out.println("5. Rechercher un client");
            System.out.println("0. Quitter");
            System.out.print("Entrer votre choix : ");
            choix = sc.nextInt();
            sc.nextLine();
            switch (choix) {
                case 0:
                    System.out.println("Au revoir !");
                    break;
                case 1:
                    Client.ajouterClient(sc);
                    break;
                case 2:
                    Client.modifierClient(sc);
                    break;
                case 3:
                    Client.supprimerClient(sc);
                    break;
                case 4:
                    Client.listerClient();
                    break;
                case 5:
                    Client.rechercherClient(sc);
                    break;
                default:
                    System.out.println(" Choix invalide !");
            }
        } while(choix != 0);


    }





    public static void ajouterClient(Scanner sc) {
        try {


            System.out.print("Id Client : ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Nom : ");
            String nom = sc.nextLine();

            System.out.print("Téléphone : ");
            String telephone = sc.nextLine();

            System.out.print("Email : ");
            String email = sc.nextLine();

            Connection con = DBConnection.createConnection();
            String sql = "INSERT INTO client(id, nom, telephone, email) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, nom);
            ps.setString(3, telephone);
            ps.setString(4, email);

            ps.executeUpdate();
            System.out.println(" Client ajouté avec succès");

        } catch (Exception e) {
            System.out.println(" Erreur lors de l'ajout");
            e.printStackTrace();
        }
    }


    public static void modifierClient(Scanner sc) {

        try {


            System.out.print("Id du client à modifier : ");
            int idClient = sc.nextInt();
            sc.nextLine();

            System.out.print("Nouveau nom : ");
            String nouveauNom = sc.nextLine();

            System.out.print("Nouveau téléphone : ");
            String nouveauTel = sc.nextLine();

            System.out.print("Nouveau email : ");
            String nouveauEmail = sc.nextLine();

            Connection con = DBConnection.createConnection();

            String sql = "UPDATE client SET nom = ?, telephone = ?, email = ? WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, nouveauNom);
            ps.setString(2, nouveauTel);
            ps.setString(3, nouveauEmail);
            ps.setInt(4, idClient);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println(" Client modifié avec succès");
            } else {
                System.out.println(" Client introuvable");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void supprimerClient(Scanner sc) {

        try {

            System.out.print("Id du client à supprimer : ");
            int idClient = sc.nextInt();

            Connection con = DBConnection.createConnection();

            String sql = "DELETE FROM client WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idClient);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println(" Client supprimé avec succès");
            } else {
                System.out.println("Client introuvable");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void listerClient() {

        try {
            Connection con = DBConnection.createConnection();

            String sql = "SELECT * FROM client";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            System.out.println(" Liste des clients :");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println("----------------------------");
                System.out.println("Id        : " + rs.getInt("id"));
                System.out.println("Nom       : " + rs.getString("nom"));
                System.out.println("Téléphone : " + rs.getString("telephone"));
                System.out.println("Email     : " + rs.getString("email"));
            }

            if (!found) {
                System.out.println("Aucun client trouvé");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Client rechercherClient(Scanner sc) {

        try {

            System.out.print("Id du client à rechercher : ");
            int idClient = sc.nextInt();

            Connection con = DBConnection.createConnection();

            String sql = "SELECT * FROM client WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idClient);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Client c = new Client(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("email")
                );

                System.out.println(" Client trouvé : " + c.getNom());
                return c;
            } else {
                System.out.println(" Client non trouvé");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }



    public static Client chercherClient(Scanner sc) {
        try { System.out.println("entrer le nom rechercher ");
            String nom = sc.nextLine();

            Connection con = DBConnection.createConnection();
            String sql= "select nom from client  where nom like concat('%',?,'%') ";

            PreparedStatement ps=con.prepareStatement(sql);
             ps.setString(1,nom);

             ResultSet rs= ps.executeQuery();

             if(rs.next()){
                 System.out.println("client trouver ");
                 while(rs.next()){
                     rs.getString("nom");
                 }



             }else {
                 System.out.println("aucaun client trouver");
             }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
return null;

    }








}

