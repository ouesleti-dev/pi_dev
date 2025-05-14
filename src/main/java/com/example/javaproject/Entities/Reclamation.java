package com.example.javaproject.Entities;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private int idUtilisateur;
    private String message;
    private String statut = "En attente"; // Valeur par défaut
    private String typeReclamation;
    private LocalDateTime dateReclamation;
    private String titre;
    private String priorite;
    private String categorie;
    private Reponse reponse;

    public Reclamation() {}

    public Reclamation(int id, int idUtilisateur, String message, String statut,
                       String typeReclamation, LocalDateTime dateReclamation,
                       String titre, String priorite, String categorie) {
        this.id = id;
        this.idUtilisateur = idUtilisateur;
        this.message = message;
        this.statut = statut;
        this.typeReclamation = typeReclamation;
        this.dateReclamation = dateReclamation;
        this.titre = titre;
        this.priorite = priorite;
        this.categorie = categorie;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }
    public String getTypeReclamation() { return typeReclamation; }
    public void setTypeReclamation(String typeReclamation) { this.typeReclamation = typeReclamation; }
    public LocalDateTime getDateReclamation() { return dateReclamation; }
    public void setDateReclamation(LocalDateTime dateReclamation) { this.dateReclamation = dateReclamation; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public String getPriorite() { return priorite; }
    public void setPriorite(String priorite) { this.priorite = priorite; }
    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", statut='" + statut + '\'' +
                '}';
    }

    public Reponse getReponse() { return reponse; }
    public void setReponse(Reponse reponse) {
        this.reponse = reponse;
        if(reponse != null) {
            this.statut = "Approuvée";
        }
    }
}