package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Scanner;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class exportFile {

    public static void exporterDOC(Scanner sc) {
        String choix;
        do {
            System.out.println("\n===== MENU EXPORT =====");
            System.out.println("1: Exporter les factures d'un prestataire");
            System.out.println("2: Exporter les factures impayées");
            System.out.println("3: Générer une facture PDF");
            System.out.println("4: Générer rapport mensuel");
            System.out.println("0: Retour au menu principal");
            System.out.print("Votre choix: ");
            choix = sc.nextLine();

            switch (choix) {
                case "1" -> exporterFacturesPrestataire();
                case "2" -> exporterFacturImpier();
                case "3" -> {
                    System.out.print("Entrer l'ID facture: ");
                    int id = Integer.parseInt(sc.nextLine());
                    Facture f = new FactureService().findById(id);
                    if (f != null) {
                        try {
                            generateInvoice(f);
                            System.out.println("PDF généré pour la facture " + id);
                        } catch (Exception e) {
                            System.out.println("Erreur génération PDF");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Facture introuvable");
                    }
                }
                case "4" -> genererRapportMensuel();
                case "0" -> System.out.println("Retour au menu principal");
                default -> System.out.println("Choix invalide");
            }
        } while (!choix.equals("0"));
    }

    public static void menuExport() {
        Scanner sc = new Scanner(System.in);
        String choix;
        do {
            System.out.println("\n===== MENU EXPORT =====");
            System.out.println("1: Exporter les factures d'un prestataire");
            System.out.println("2: Exporter les factures impayées");
            System.out.println("3: Générer une facture PDF");
            System.out.println("4: Générer rapport mensuel");
            System.out.println("0: Retour au menu principal");
            System.out.print("Votre choix: ");
            choix = sc.nextLine();

            switch (choix) {
                case "1" -> exporterFacturesPrestataire();
                case "2" -> exporterFacturImpier();
                case "3" -> {
                    System.out.print("Entrer l'ID facture: ");
                    int id = Integer.parseInt(sc.nextLine());
                    Facture f = new FactureService().findById(id);
                    if (f != null) {
                        try {
                            generateInvoice(f);
                            System.out.println("PDF généré pour la facture " + id);
                        } catch (Exception e) {
                            System.out.println("Erreur génération PDF");
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("Facture introuvable");
                    }
                }
                case "4" -> genererRapportMensuel();
                case "0" -> System.out.println("Retour au menu principal");
                default -> System.out.println("Choix invalide");
            }
        } while (!choix.equals("0"));
    }

    public static void exporterFacturesPrestataire() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrer votre ID Prestataire : ");
        int idPrestataire = Integer.parseInt(scanner.nextLine());

        System.out.print("Entrer le mois (1-12) : ");
        int mois = Integer.parseInt(scanner.nextLine());

        System.out.print("Entrer l'année (ex: 2026) : ");
        int annee = Integer.parseInt(scanner.nextLine());

        String query =
                "SELECT f.id, f.date, c.nom AS clientNom, f.montant, f.status, " +
                        "COALESCE(SUM(p.montant), 0) AS totalPaye " +
                        "FROM facture f " +
                        "JOIN client c ON c.id = f.idClient " +
                        "LEFT JOIN paiement p ON p.idFacture = f.id " +
                        "WHERE f.idPrestataire = ? " +
                        "AND MONTH(f.date) = ? " +
                        "AND YEAR(f.date) = ? " +
                        "GROUP BY f.id, f.date, c.nom, f.montant, f.status " +
                        "ORDER BY f.date ASC";

        try (Connection conn = DBConnection.createConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idPrestataire);
            ps.setInt(2, mois);
            ps.setInt(3, annee);

            ResultSet rs = ps.executeQuery();

            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Factures");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Date", "Client", "Montant", "Statut"};

            for (int i = 0; i < columns.length; i++) {
                headerRow.createCell(i).setCellValue(columns[i]);
            }

            int rowNum = 1;

            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(rs.getInt("id"));
                row.createCell(1).setCellValue(rs.getDate("date").toString());
                row.createCell(2).setCellValue(rs.getString("clientNom"));
                row.createCell(3).setCellValue(rs.getDouble("montant"));
                row.createCell(4).setCellValue(rs.getString("status"));
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "facturesprestataire-" + mois + "-" + annee + ".xls";
            FileOutputStream out = new FileOutputStream(fileName);
            workbook.write(out);
            out.close();
            workbook.close();

            System.out.println("Export Excel réussi !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void exporterFacturImpier() {
        String sql = "SELECT f.id, f.date, f.montant, c.nom " +
                "FROM facture f " +
                "JOIN client c ON f.idClient = c.id " +
                "WHERE f.status = 'UNPAID' OR f.status = 'PARTIAL'";

        try (Connection conn = DBConnection.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Factures impayées");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID Facture");
            header.createCell(1).setCellValue("Nom Client");
            header.createCell(2).setCellValue("Date Facture");
            header.createCell(3).setCellValue("Montant");
            header.createCell(4).setCellValue("Jours de retard");

            int rowNum = 1;
            LocalDate dateExport = LocalDate.now();

            while (rs.next()) {
                Row row = sheet.createRow(rowNum++);

                Date sqlDate = rs.getDate("date");
                long joursRetard = 0;

                if (sqlDate != null) {
                    LocalDate dateFacture = sqlDate.toLocalDate();
                    joursRetard = ChronoUnit.DAYS.between(dateFacture, dateExport);
                }

                row.createCell(0).setCellValue(rs.getInt("id"));
                row.createCell(1).setCellValue(rs.getString("nom"));
                row.createCell(2).setCellValue(sqlDate != null ? sqlDate.toString() : "");
                row.createCell(3).setCellValue(rs.getDouble("montant"));
                row.createCell(4).setCellValue(joursRetard);
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "factureimpayees.xlsx";
            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("Rapport généré avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void generateInvoice(Facture facture) throws Exception {
        String filePath = "Facture_" + facture.getId() + ".pdf";

        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(filePath));

        document.open();
        document.add(new Paragraph("FinPay"));
        document.add(new Paragraph("Date : " + facture.getDate()));
        document.add(new Paragraph("Montant Total : " + facture.getMontant()));
        document.add(new Paragraph("Statut : " + facture.getStatus()));
        document.close();
    }

    public static void genererRapportMensuel() {
        try (Connection conn = DBConnection.createConnection()) {

            String sql =
                    "SELECT p.nom, COUNT(f.id) AS nombreFactures, SUM(f.montant) AS totalGenere " +
                            "FROM facture f " +
                            "JOIN prestataire p ON f.idPrestataire = p.id " +
                            "GROUP BY p.nom";

            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Rapport Global");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Prestataire");
            header.createCell(1).setCellValue("Nombre Factures");
            header.createCell(2).setCellValue("Total Généré");

            int rowIndex = 1;

            while (rs.next()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(rs.getString("nom"));
                row.createCell(1).setCellValue(rs.getInt("nombreFactures"));
                row.createCell(2).setCellValue(rs.getDouble("totalGenere"));
            }

            FileOutputStream fileOut = new FileOutputStream("rapportglobal_mois.xls");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("Export terminé");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}