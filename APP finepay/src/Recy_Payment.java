import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;
import java.time.LocalDate;


public class Recy_Payment {
    public static void generateRecy(int paymentId, int factureID, LocalDate datePayment,String paymentMethod,double amountPaid,double remainingAmount) {

        String name=  "recupaiement"+paymentId+".pdf";


        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(name));

            document.open();
            document.add(new Paragraph("*************** Reçu de paiment  ****************"));
            document.add(new Paragraph("Numero du paiement :"+paymentId));
            document.add(new Paragraph("Numero de la facture :"+factureID));
            document.add(new Paragraph("Date du paiement"+datePayment));
            document.add(new Paragraph("Méthode de paiement : " + paymentMethod));
            document.add(new Paragraph("Montant payé : " + amountPaid + " DH"));
            document.add(new Paragraph("Reste à payer : " + remainingAmount + " DH"));

            System.out.println("PDF genere avec succes :"+name);
            document.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

