import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;


public class exportFile {




    public static void exporterDOC(Scanner sc){
        System.out.println("entrer votre choix 1:exporter les facture d'un prestataire");
        String choix;
        System.out.println("Veuillez entrer votre choix :");
        choix=sc.nextLine();
        if(choix.equals("1")){
            exporterFacturesPrestataire();
        }
        else {
            System.out.println("choix invalid");
        }
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
                        "       COALESCE(SUM(p.montant), 0) AS totalPaye " +
                        "FROM facture f " +
                        "JOIN client c ON c.id = f.idClient " +
                        "LEFT JOIN paiement p ON p.idFacture = f.id " +
                        "WHERE f.idPrestataire = ? " +
                        "  AND MONTH(f.date) = ? " +
                        "  AND YEAR(f.date) = ? " +
                        "GROUP BY f.id, f.date, c.nom, f.montant, f.status " +
                        "ORDER BY f.date ASC";

        try (Connection conn = DBConnection.createConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idPrestataire);
            ps.setInt(2, mois);
            ps.setInt(3, annee);

            ResultSet rs = ps.executeQuery();

            // .xls
            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Factures");

            // Styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            CellStyle moneyStyle = workbook.createCellStyle();
            DataFormat df = workbook.createDataFormat();
            moneyStyle.setDataFormat(df.getFormat("#,##0.00"));

            // Header (5 colonnes demandées)
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Date", "Client", "Montant", "Statut"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 1;

            double totalFacture = 0;
            double totalPaye = 0;

            while (rs.next()) {

                Row row = sheet.createRow(rowNum++);

                int id = rs.getInt("id");
                String date = rs.getDate("date").toString();
                String clientNom = rs.getString("clientNom");
                double montant = rs.getDouble("montant");
                String status = rs.getString("status");
                double paye = rs.getDouble("totalPaye");

                row.createCell(0).setCellValue(id);
                row.createCell(1).setCellValue(date);
                row.createCell(2).setCellValue(clientNom);

                Cell cMontant = row.createCell(3);
                cMontant.setCellValue(montant);
                cMontant.setCellStyle(moneyStyle);

                row.createCell(4).setCellValue(status);

                totalFacture += montant;
                totalPaye += paye;
            }

            // Si aucune facture
            if (rowNum == 1) {
                Row r = sheet.createRow(rowNum);
                r.createCell(0).setCellValue("Aucune facture pour ce prestataire ce mois.");
            } else {
                double totalAttente = totalFacture - totalPaye;

                rowNum++; // ligne vide

                // Lignes totaux visibles
                Row totalRow1 = sheet.createRow(rowNum++);
                totalRow1.createCell(2).setCellValue("Total facturé");
                Cell tf = totalRow1.createCell(3);
                tf.setCellValue(totalFacture);
                tf.setCellStyle(moneyStyle);

                Row totalRow2 = sheet.createRow(rowNum++);
                totalRow2.createCell(2).setCellValue("Total payé");
                Cell tp = totalRow2.createCell(3);
                tp.setCellValue(totalPaye);
                tp.setCellStyle(moneyStyle);

                Row totalRow3 = sheet.createRow(rowNum++);
                totalRow3.createCell(2).setCellValue("Total en attente");
                Cell ta = totalRow3.createCell(3);
                ta.setCellValue(totalAttente);
                ta.setCellStyle(moneyStyle);
            }

            // Auto-size colonnes visibles
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            String fileName = "C:\\Users\\arkka\\Downloads\\facturesprestataire"+mois + "-" + annee +".xls";
            try (FileOutputStream out = new FileOutputStream(fileName)) {
                workbook.write(out);
            }
            workbook.close();

            System.out.println("Export Excel réussi !");
            System.out.println("Fichier créé : " + fileName);

        } catch (Exception e) {
            System.out.println("Erreur export");
            e.printStackTrace();
        }
    }







}

