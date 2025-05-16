package Models;

import java.sql.Date;

public class Commentaire {
    private int idCommentaire;
    private String contenu;
    private String auteur;
    private Date dateCommentaire;
    private int idPost;

    public Commentaire(int idCommentaire, String contenu, String auteur, Date dateCommentaire, int idPost) {
        this.idCommentaire = idCommentaire;
        this.contenu = contenu;
        this.auteur = auteur;
        this.dateCommentaire = dateCommentaire;
        this.idPost = idPost;
    }

    public Commentaire(String contenu, String auteur, Date dateCommentaire, int idPost) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.dateCommentaire = dateCommentaire;
        this.idPost = idPost;
    }

    public Commentaire() {
    }

    // Getters & Setters
    public int getIdCommentaire() {
        return idCommentaire;
    }

    public void setIdCommentaire(int idCommentaire) {
        this.idCommentaire = idCommentaire;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public Date getDateCommentaire() {
        return dateCommentaire;
    }

    public void setDateCommentaire(Date dateCommentaire) {
        this.dateCommentaire = dateCommentaire;
    }

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public String toString() {
        return "Commentaire{" +
                "id=" + idCommentaire +
                ", contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                ", date=" + dateCommentaire +
                ", idPost=" + idPost +
                '}';
    }
}



