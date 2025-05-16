package Controllers;

import Models.Post;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class reserchPost {

    @FXML
    private Label IdPost;

    @FXML
    private Label Reserch;

    @FXML
    private TextField idPost;

    @FXML
    private Button reserch;

    private Connection conn;

    // Méthode de recherche dans la base
    public List<Post> rechercher(String auteur) throws Exception {
        List<Post> resultats = new ArrayList<>();

        // Vérification que la connexion existe
        if (conn == null) {
            throw new Exception("La connexion à la base de données n'est pas initialisée");
        }

        String req = "SELECT * FROM post WHERE auteur = ?";
        PreparedStatement stmt = conn.prepareStatement(req);
        stmt.setString(1, auteur);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Post post = new Post();
            post.setIdPost(rs.getInt("IdPost"));
            post.setContenu(rs.getString("contenu"));
            post.setAuteur(rs.getString("auteur"));
            post.setDateCreation(rs.getDate("dateCreation"));

            resultats.add(post);
        }

        return resultats;
    }

    @FXML
    void reserch(ActionEvent event) {
        try {
            // Initialisation de la connexion si elle n'existe pas
            if (conn == null) {
                String url = "jdbc:mysql://localhost:3306/gestion blog";
                String user ="root";
                String pwd="";
                conn = DriverManager.getConnection(url, user, pwd);
            }

            String auteurRecherche = idPost.getText();
            if (auteurRecherche == null || auteurRecherche.isEmpty()) {
                Reserch.setText("Veuillez entrer un auteur");
                return;
            }
            List<Post> postsTrouves = rechercher(auteurRecherche);

            if (postsTrouves.isEmpty()) {
                Reserch.setText("Aucun post trouvé pour cet auteur.");
            } else {
                StringBuilder sb = new StringBuilder();
                for (Post p : postsTrouves) {
                    sb.append("IdPost: ").append(p.getIdPost())
                            .append(", Contenu: ").append(p.getContenu())
                            .append(", Date: ").append(p.getDateCreation())
                            .append("\n");
                }
                Reserch.setText(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Reserch.setText("Erreur lors de la recherche: " + e.getMessage());
        }
    }
}