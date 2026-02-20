import java.time.LocalDate;

public class Facture {
    private int id;
    private LocalDate date;
    private double montant;
    private String status;
    private Client client;
    private Prestatairedb prestataire;

    public Facture(int id, LocalDate date, double montant, String status, Client client, Prestatairedb prestataire) {
        this.id = id;
        this.date = date;
        this.montant = montant;
        this.status = status;
        this.client = client;
        this.prestataire = prestataire;
    }

    public int getId() { return id; }
    public LocalDate getDate() { return date; }
    public double getMontant() { return montant; }
    public String getStatus() { return status; }
    public Client getClient() { return client; }
    public Prestatairedb getPrestataire() { return prestataire; }

    public void setId(int id) { this.id = id; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setMontant(double montant) { this.montant = montant; }
    public void setStatus(String status) { this.status = status; }
    public void setClient(Client client) { this.client = client; }
    public void setPrestataire(Prestatairedb prestataire) { this.prestataire = prestataire; }
}
