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

    public static void generateInvoice(Facture facture) throws FileNotFoundException {

        String folderPath = "C:\\Users\\enaa\\Documents\\Factures";
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }


        String filePath = folderPath + "\\Facture_" + facture.getId() + ".pdf";

        PdfWriter writer = new PdfWriter(filePath);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);


        Paragraph title = new Paragraph("FinPay")
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setBold();
        document.add(title);


        try {
            Image logo = new Image(ImageDataFactory.create("C:\\Users\\enaa\\Documents\\Logos\\logo.png"));
            logo.setWidth(UnitValue.createPercentValue(20));
            document.add(logo);
        } catch (Exception e) {

        }


        Client client = facture.getClient();
        document.add(new Paragraph("\nInformations Client").setBold().setFontSize(16));
        if (client != null) {
            document.add(new Paragraph("Nom : " + client.getNom()));
            document.add(new Paragraph("Téléphone : " + client.getTelephone()));
            document.add(new Paragraph("Email : " + client.getEmail()));
        }


        Prestatairedb prest = facture.getPrestataire();
        document.add(new Paragraph("\nInformations Prestataire").setBold().setFontSize(16));
        if (prest != null) {
            document.add(new Paragraph("Nom : " + prest.getNom()));
            document.add(new Paragraph("Type : " + prest.getType()));
            document.add(new Paragraph("ID : " + prest.getIdPrestat()));
        }


        document.add(new Paragraph("\nDétails de la Facture").setBold().setFontSize(16));
        document.add(new Paragraph("Date : " + facture.getDate()));
        document.add(new Paragraph("Montant Total : " + facture.getMontant() + " dh"));


        Paragraph status = new Paragraph("Statut : " + facture.getStatus());
        if ("PAID".equalsIgnoreCase(facture.getStatus())) {
            status.setFontColor(ColorConstants.GREEN);
        } else {
            status.setFontColor(ColorConstants.RED);
        }
        document.add(status);

        document.close();
        System.out.println("Facture PDF générée avec succès : " + filePath);
    }


    public static void genererRapportMensuel() {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            conn = DBConnection.createConnection();

            String sql = """
                SELECT p.nom,
                       COUNT(f.id) AS nombreFactures,
                       SUM(f.montant) AS totalGenere,
                       SUM(c.montant) AS totalCommission
                FROM facture f
                JOIN prestataire p ON f.idPrestataire = p.id
                LEFT JOIN paiement pa ON f.id = pa.idFacture
                LEFT JOIN commission c ON pa.id = c.idPaiement
                GROUP BY p.nom
                """;

            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Rapport Global");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Prestataire");
            header.createCell(1).setCellValue("Nombre Factures");
            header.createCell(2).setCellValue("Total Généré");
            header.createCell(3).setCellValue("Total Commission");

            int rowIndex = 1;

            while (rs.next()) {

                Row row = sheet.createRow(rowIndex);

                row.createCell(0).setCellValue(rs.getString("nom"));
                row.createCell(1).setCellValue(rs.getInt("nombreFactures"));
                row.createCell(2).setCellValue(rs.getDouble("totalGenere"));
                row.createCell(3).setCellValue(rs.getDouble("totalCommission"));

                rowIndex++;
            }

            FileOutputStream fileOut = new FileOutputStream("rapportglobal_mois.xls");
            workbook.write(fileOut);
            fileOut.close();
            workbook.close();

            System.out.println("Rapport généré avec succès !");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

