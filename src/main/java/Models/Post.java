package Models;

import java.sql.Date;

public class Post {
    private int idPost;
    private String contenu;
    private String auteur;
    private Date dateCreation ;

    public Post(String contenu, String auteur, Date dateCreation) {
        this.contenu = contenu;
        this.auteur = auteur;
        this.dateCreation  = dateCreation;
    }

    public Post( int idPost , String contenu, String auteur, Date dateCreation) {
        this.contenu = contenu;
        this.idPost  =  idPost ;
        this.auteur = auteur;
        this.dateCreation  = dateCreation;
    }
    public Post() {

    }

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
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

    public Date getDateCreation() {
        return dateCreation ;
    }

    public void setDateCreation(Date  dateCreation) {
        this.dateCreation = dateCreation;
    }

    @Override
    public String toString() {
        return "Post{" +
                "contenu='" + contenu + '\'' +
                ", auteur='" + auteur + '\'' +
                ", dateCreation=" + dateCreation +
                '}';
    }
}
