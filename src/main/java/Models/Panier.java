package Models;

import java.sql.Timestamp;

public class Panier {
    private int id_panier;
    private int id_events;
    private int prix;
    private int quantite;
    private int prix_total;
    private Timestamp date_creation;
    private Statut statut;
    public enum Statut {
        ABONDONNE("A"),
        VALIDE("V");

        private final String code;

        Statut(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Statut fromCode(String code) {
            if (code == null) return ABONDONNE;

            for (Statut s : values()) {
                if (s.code.equals(code)) {
                    return s;
                }
            }
            return ABONDONNE;
        }
    }

    // Constructeur par défaut
    public Panier() {
        this.statut = Statut.ABONDONNE;
        this.date_creation = new Timestamp(System.currentTimeMillis());
    }

    // Constructeur sans id (pour création)
    public Panier(int id_events, int prix, int quantite) {
        this();
        this.id_events = id_events;
        this.prix = prix;
        this.quantite = quantite;
        this.prix_total = prix * quantite;
    }

    // Constructeur complet
    public Panier(int id_panier, int id_events, int prix, int quantite,
                  int prix_total, Timestamp date_creation, Statut statut) {
        this.id_panier = id_panier;
        this.id_events = id_events;
        this.prix = prix;
        this.quantite = quantite;
        this.prix_total = prix_total;
        this.date_creation = date_creation;
        this.statut = statut;
    }

    // Getters et Setters
    public int getId_panier() {
        return id_panier;
    }

    public void setId_panier(int id_panier) {
        this.id_panier = id_panier;
    }

    public int getId_events() {
        return id_events;
    }

    public void setId_events(int id_events) {
        this.id_events = id_events;
    }

    public int getPrix() {
        return prix;
    }

    public void setPrix(int prix) {
        this.prix = prix;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getPrix_total() {
        return prix_total;
    }

    public void setPrix_total(int prix_total) {
        this.prix_total = prix_total;
    }

    public Timestamp getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(Timestamp date_creation) {
        this.date_creation = date_creation;
    }

    public Statut getStatut() {
        return statut;
    }

    public void setStatut(Statut statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Panier{" +
                "id_panier=" + id_panier +
                ", id_events=" + id_events +
                ", prix=" + prix +
                ", quantite=" + quantite +
                ", prix_total=" + prix_total +
                ", date_creation=" + date_creation +
                ", statut=" + statut +
                '}';
    }
}
