package org.example;

import java.sql.*;
import java.time.LocalDate;

public class StatistiqueService {

    private Statistique statistique;

    public StatistiqueService() {
        this.statistique = new Statistique();
    }

    public void ajouterPaiement(double montant, int idFacture) {

        String sqlCheckFacture = "SELECT montant, status FROM facture WHERE id = ?";
        String sqlTotalPaye = "SELECT COALESCE(SUM(montant),0) AS total FROM paiement WHERE idFacture = ?";
        String sqlInsertPaiement = "INSERT INTO paiement (montant, date, idFacture) VALUES (?, ?, ?)";
        String sqlInsertCommission = "INSERT INTO commission (idPaiement, tauxCommission, montant) VALUES (?, ?, ?)";
        String sqlUpdateFacture = "UPDATE facture SET status = ? WHERE id = ?";

        try (Connection conn = DBConnection.createConnection()) {
            conn.setAutoCommit(false);

            double montantFacture = 0;
            String statusActuel = "UNPAID";

            PreparedStatement psCheck = conn.prepareStatement(sqlCheckFacture);
            psCheck.setInt(1, idFacture);
            ResultSet rsCheck = psCheck.executeQuery();

            if (!rsCheck.next()) {
                System.out.println("Facture inexistante !");
                conn.rollback();
                return;
            }

            montantFacture = rsCheck.getDouble("montant");
            statusActuel = rsCheck.getString("status");

            if ("PAID".equalsIgnoreCase(statusActuel)) {
                System.out.println("Cette facture est deja payee.");
                conn.rollback();
                return;
            }

            PreparedStatement psTotal = conn.prepareStatement(sqlTotalPaye);
            psTotal.setInt(1, idFacture);
            ResultSet rsTotal = psTotal.executeQuery();
            rsTotal.next();
            double totalPaye = rsTotal.getDouble("total");

            double restant = montantFacture - totalPaye;

            System.out.println("Montant facture : " + montantFacture);
            System.out.println("Total deja paye : " + totalPaye);
            System.out.println("Montant restant : " + restant);

            if (restant <= 0) {
                PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateFacture);
                psUpdate.setString(1, "PAID");
                psUpdate.setInt(2, idFacture);
                psUpdate.executeUpdate();

                conn.commit();
                System.out.println("Facture mise a jour: PAID");
                return;
            }

            if (montant <= 0 || montant > restant) {
                System.out.println("Paiement refuse.");
                conn.rollback();
                return;
            }

            PreparedStatement stmtPaiement = conn.prepareStatement(sqlInsertPaiement, Statement.RETURN_GENERATED_KEYS);
            stmtPaiement.setDouble(1, montant);
            stmtPaiement.setDate(2, Date.valueOf(LocalDate.now()));
            stmtPaiement.setInt(3, idFacture);
            stmtPaiement.executeUpdate();

            ResultSet rsKeys = stmtPaiement.getGeneratedKeys();
            int idPaiement = 0;
            if (rsKeys.next()) {
                idPaiement = rsKeys.getInt(1);
            }

            double tauxCommission = 2.0;
            double montantCommission = montant * tauxCommission / 100;

            PreparedStatement stmtCommission = conn.prepareStatement(sqlInsertCommission);
            stmtCommission.setInt(1, idPaiement);
            stmtCommission.setDouble(2, tauxCommission);
            stmtCommission.setDouble(3, montantCommission);
            stmtCommission.executeUpdate();

            double nouveauTotal = totalPaye + montant;
            String nouveauStatus;
            if (nouveauTotal == montantFacture) nouveauStatus = "PAID";
            else nouveauStatus = "PARTIAL";

            PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdateFacture);
            stmtUpdate.setString(1, nouveauStatus);
            stmtUpdate.setInt(2, idFacture);
            stmtUpdate.executeUpdate();

            conn.commit();

            System.out.println("Paiement ajoute !");
            System.out.println("Commission automatique (2%) : " + montantCommission);
            System.out.println("Nouveau status facture : " + nouveauStatus);

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    public void afficherRapport() {

        String sqlTotalPaiements = "SELECT COALESCE(SUM(montant),0) FROM paiement";
        String sqlTotalCommissions = "SELECT COALESCE(SUM(montant),0) FROM commission";
        String sqlNombrePaiements = "SELECT COUNT(*) FROM paiement";
        String sqlFacturePayee = "SELECT COUNT(*) FROM facture WHERE status = 'PAID'";
        String sqlFactureNonPayee = "SELECT COUNT(*) FROM facture WHERE status <> 'PAID'";

        try (Connection conn = DBConnection.createConnection()) {

            double totalPaiements = getDoubleResult(conn, sqlTotalPaiements);
            double totalCommissions = getDoubleResult(conn, sqlTotalCommissions);
            int nombrePaiements = getIntResult(conn, sqlNombrePaiements);
            int facturePayee = getIntResult(conn, sqlFacturePayee);
            int factureNonPayee = getIntResult(conn, sqlFactureNonPayee);

            System.out.println("===== Rapport Statistique =====");
            System.out.println("Total Paiements : " + totalPaiements);
            System.out.println("Total Commissions : " + totalCommissions);
            System.out.println("Nombre de Paiements : " + nombrePaiements);
            System.out.println("Factures Payees : " + facturePayee);
            System.out.println("Factures Non Payees : " + factureNonPayee);

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    public void saveStatistique() {

        String sqlInsert = "INSERT INTO statistique (totalPaiements, totalCommissions, nombrePaiements, nombreFacturePayee, nombreFactureNonPayee, dateGeneration) VALUES (?, ?, ?, ?, ?, ?)";

        String sqlTotalPaiements = "SELECT COALESCE(SUM(montant),0) FROM paiement";
        String sqlTotalCommissions = "SELECT COALESCE(SUM(montant),0) FROM commission";
        String sqlNombrePaiements = "SELECT COUNT(*) FROM paiement";
        String sqlFacturePayee = "SELECT COUNT(*) FROM facture WHERE status = 'PAID'";
        String sqlFactureNonPayee = "SELECT COUNT(*) FROM facture WHERE status <> 'PAID'";

        try (Connection conn = DBConnection.createConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {

            double totalPaiements = getDoubleResult(conn, sqlTotalPaiements);
            double totalCommissions = getDoubleResult(conn, sqlTotalCommissions);
            int nombrePaiements = getIntResult(conn, sqlNombrePaiements);
            int facturePayee = getIntResult(conn, sqlFacturePayee);
            int factureNonPayee = getIntResult(conn, sqlFactureNonPayee);

            stmt.setDouble(1, totalPaiements);
            stmt.setDouble(2, totalCommissions);
            stmt.setInt(3, nombrePaiements);
            stmt.setInt(4, facturePayee);
            stmt.setInt(5, factureNonPayee);
            stmt.setDate(6, Date.valueOf(LocalDate.now()));

            stmt.executeUpdate();
            System.out.println("Statistique enregistree en base avec succes !");

        } catch (SQLException e) {
            System.out.println("Erreur SQL : " + e.getMessage());
        }
    }

    public Statistique getStatistique() {
        return statistique;
    }

    private double getDoubleResult(Connection conn, String sql) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getDouble(1);
        return 0;
    }

    private int getIntResult(Connection conn, String sql) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) return rs.getInt(1);
        return 0;
    }
}
