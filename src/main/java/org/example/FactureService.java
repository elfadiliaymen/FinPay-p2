package org.example;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class FactureService {

    public Facture creerFacture(Facture facture) {
        if (facture.getClient() == null || facture.getPrestataire() == null) {
            System.out.println("Client ou Prestataire manquant");
            return null;
        }
        if (facture.getStatus() == null || facture.getStatus().isEmpty()) {
            facture.setStatus("UNPAID");
        }

        String sql = "INSERT INTO facture (date, montant, status, idClient, idPrestataire) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setDate(1, Date.valueOf(facture.getDate()));
            stmt.setDouble(2, facture.getMontant());
            stmt.setString(3, facture.getStatus());
            stmt.setInt(4, facture.getClient().getIdClient());
            stmt.setInt(5, facture.getPrestataire().getIdPrestat());

            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                facture.setId(keys.getInt(1));
            }
            System.out.println("Facture enregistrée avec succès !");
            return facture;

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            return null;
        }
    }

    public List<Facture> lister() {
        List<Facture> factures = new ArrayList<>();
        String sql = "SELECT id, date, montant, status FROM facture";
        try (Connection conn = DBConnection.createConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Facture f = new Facture(
                        rs.getInt("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getDouble("montant"),
                        rs.getString("status"),
                        null,
                        null
                );
                factures.add(f);
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return factures;
    }

    public boolean modifierFacture(int id, double nouveauMontant, String nouveauStatus) {
        if (nouveauStatus == null) nouveauStatus = "";
        nouveauStatus = nouveauStatus.toUpperCase();
        if (!nouveauStatus.equals("UNPAID") && !nouveauStatus.equals("PARTIAL") && !nouveauStatus.equals("PAID")) {
            System.out.println("Status invalide");
            return false;
        }

        String sql = "UPDATE facture SET montant = ?, status = ? WHERE id = ? AND status <> 'PAID'";
        try (Connection conn = DBConnection.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, nouveauMontant);
            stmt.setString(2, nouveauStatus);
            stmt.setInt(3, id);

            int rows = stmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Facture modifiée avec succès !");
                return true;
            } else {
                System.out.println("Modification impossible !");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
            return false;
        }
    }

    public Facture findById(int id) {
        String sql = "SELECT id, date, montant, status FROM facture WHERE id = ?";
        try (Connection conn = DBConnection.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Facture(
                        rs.getInt("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getDouble("montant"),
                        rs.getString("status"),
                        null,
                        null
                );
            }

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
        return null;
    }

    // Simple PDF generation, no decorations, compatible with iText 5
    public static void generateInvoice(Facture facture) {
        String filePath = "Facture_" + facture.getId() + ".pdf";
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            document.add(new Paragraph("FinPay"));
            document.add(new Paragraph("Date : " + facture.getDate()));
            document.add(new Paragraph("Montant Total : " + facture.getMontant()));
            document.add(new Paragraph("Statut : " + facture.getStatus()));

            if (facture.getClient() != null) {
                document.add(new Paragraph("\nInformations Client"));
                document.add(new Paragraph("Nom : " + facture.getClient().getNom()));
                document.add(new Paragraph("Téléphone : " + facture.getClient().getTelephone()));
                document.add(new Paragraph("Email : " + facture.getClient().getEmail()));
            }

            if (facture.getPrestataire() != null) {
                document.add(new Paragraph("\nInformations Prestataire"));
                document.add(new Paragraph("Nom : " + facture.getPrestataire().getNom()));
                document.add(new Paragraph("Type : " + facture.getPrestataire().getType()));
                document.add(new Paragraph("ID : " + facture.getPrestataire().getIdPrestat()));
            }

            document.close();
            System.out.println("Facture PDF générée avec succès : " + filePath);

        } catch (FileNotFoundException e) {
            System.out.println("Erreur : fichier introuvable");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur lors de la génération du PDF");
            e.printStackTrace();
        }
    }
}