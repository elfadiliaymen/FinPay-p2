package org.example;

import java.time.LocalDate;
import java.util.Scanner;

public class Statistique {

    private double totalPaiements;
    private double totalCommissions;
    private int nombrePaiements;
    private int facturePaye;
    private int factureNonPaye;
    private LocalDate dateGeneration;

    public Statistique() {
        this.totalPaiements = 0;
        this.totalCommissions = 0;
        this.nombrePaiements = 0;
        this.facturePaye = 0;
        this.factureNonPaye = 0;
        this.dateGeneration = LocalDate.now();
    }

    public double getTotalPaiements() { return totalPaiements; }
    public double getTotalCommissions() { return totalCommissions; }
    public int getNombrePaiements() { return nombrePaiements; }
    public int getFacturePaye() { return facturePaye; }
    public int getFactureNonPaye() { return factureNonPaye; }
    public LocalDate getDateGeneration() { return dateGeneration; }

    public void setFacturePaye(int facturePaye) {
        this.facturePaye = facturePaye;
    }

    public void setFactureNonPaye(int factureNonPaye) {
        this.factureNonPaye = factureNonPaye;
    }

    public void calculerTotalPaiements(double montant) {
        this.totalPaiements += montant;
        this.nombrePaiements++;
    }

    public void calculerTotalCommissions(double commission) {
        this.totalCommissions += commission;
    }

    public void genererRapport() {
        System.out.println("===== Rapport Statistique =====");
        System.out.println("Total Paiements : " + totalPaiements);
        System.out.println("Total Commissions : " + totalCommissions);
        System.out.println("Nombre de Paiements : " + nombrePaiements);
        System.out.println("Factures Payées : " + facturePaye);
        System.out.println("Factures Non Payées : " + factureNonPaye);
    }
}
