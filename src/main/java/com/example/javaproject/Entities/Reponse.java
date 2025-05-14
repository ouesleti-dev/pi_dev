package com.example.javaproject.Entities;

import java.time.LocalDateTime;

public class Reponse {
    private int id;
    private int idReclamation;
    private int idUtilisateur; // Champ ajouté
    private String contenu;
    private LocalDateTime dateReponse;
    private String reclamationTitre;

    public Reponse() {
        this.dateReponse = LocalDateTime.now();
        this.idUtilisateur = 1; // Valeur statique par défaut
    }

    public Reponse(int idReclamation, String contenu) {
        this();
        this.idReclamation = idReclamation;
        this.contenu = contenu;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdReclamation() { return idReclamation; }
    public void setIdReclamation(int idReclamation) { this.idReclamation = idReclamation; }

    public int getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(int idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getDateReponse() { return dateReponse; }
    public void setDateReponse(LocalDateTime dateReponse) { this.dateReponse = dateReponse; }

    public String getReclamationTitre() {
        return reclamationTitre;
    }

    public void setReclamationTitre(String reclamationTitre) {
        this.reclamationTitre = reclamationTitre;
    }
}