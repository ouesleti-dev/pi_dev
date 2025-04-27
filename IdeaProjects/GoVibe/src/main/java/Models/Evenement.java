package Models;

import java.util.Date;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private Date date;
    private int capacite;
    private double prix;
    private String statut;
    private int organisateurId;  // ID de l'organisateur

    // Constructeur
    public Evenement(int id, String titre, String description, Date date, int capacite, double prix, String statut, int organisateurId) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date = date;
        this.capacite = capacite;
        this.prix = prix;
        this.statut = statut;
        this.organisateurId = organisateurId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCapacite() {
        return capacite;
    }

    public void setCapacite(int capacite) {
        this.capacite = capacite;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public int getOrganisateurId() {
        return organisateurId;
    }

    public void setOrganisateurId(int organisateurId) {
        this.organisateurId = organisateurId;
    }
}